package sg.ncl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;
import sg.ncl.domain.ExceptionState;
import sg.ncl.exceptions.WebServiceRuntimeException;
import sg.ncl.testbed_interface.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static sg.ncl.domain.ExceptionState.*;

/**
 * Created by dcsjnh on 11/17/2016.
 */
@Controller
@RequestMapping("/data")
@Slf4j
public class DataController extends MainController {

    private static final String REDIRECT_DATA = "redirect:/data";
    private static final String REDIRECT_CONTRIBUTE = "redirect:/data/contribute/";
    private static final String CATEGORIES = "categories";
    private static final String LICENSES = "licenses";
    private static final String RESOURCES = "resources";
    private static final String DATASET = "dataset";
    private static final String CONTRIBUTE_DATA_PAGE = "data_contribute";
    private static final String MESSAGE_ATTRIBUTE = "message";
    private static final String PUBLIC_USER_ID = "publicUserId";
    private static final String EDITABLE_FLAG = "editable";
    private static final String START_DATE = "startDate=";
    private static final String END_DATE = "endDate=";
    private static final String DATA_ID = "dataId";
    private static final String ERRORS_STR = "Error(s):";
    private static final String UL_TAG_START = "<ul class=\"fa-ul\">";
    private static final String LI_START_TAG = "<li><i class=\"fa fa-exclamation-circle\"></i> ";
    private static final String LI_END_TAG = "</li>";
    private static final String UL_END_TAG = "</ul>";

    @ModelAttribute
    @Override
    public void setXFrameResponseHeader(HttpServletResponse response) {
        response.setHeader("X-Frame-Options", "DENY");
    }

    @RequestMapping
    public String data(Model model) {
        DatasetManager datasetManager = new DatasetManager();

        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity <String> response = restTemplate.exchange(properties.getData(), HttpMethod.GET, request, String.class);
        String dataResponseBody = response.getBody();

        log.info("data: {}", dataResponseBody);

        JSONArray dataJsonArray = new JSONArray(dataResponseBody);
        for (int i = 0; i < dataJsonArray.length(); i++) {
            JSONObject dataInfoObject = dataJsonArray.getJSONObject(i);
            Dataset dataset = extractDataInfo(dataInfoObject.toString());
            datasetManager.addDataset(dataset);
        }

        model.addAttribute(CATEGORIES, getDataCategories());
        model.addAttribute(LICENSES, getDataLicenses());
        model.addAttribute("allDataMap", datasetManager.getDatasetMap());
        return "data";
    }

    @GetMapping(value = "/search")
    public String searchData(Model model, @RequestParam("keywords") String keywords) {
        if (keywords.trim().length() == 0) {
            return REDIRECT_DATA;
        }

        DatasetManager datasetManager = new DatasetManager();

        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity <String> response = restTemplate.exchange(properties.searchDatasets(keywords), HttpMethod.GET, request, String.class);
        String dataResponseBody = response.getBody();

        JSONArray dataJsonArray = new JSONArray(dataResponseBody);
        for (int i = 0; i < dataJsonArray.length(); i++) {
            JSONObject dataInfoObject = dataJsonArray.getJSONObject(i);
            Dataset dataset = extractDataInfo(dataInfoObject.toString());
            datasetManager.addDataset(dataset);
        }

        model.addAttribute(CATEGORIES, getDataCategories());
        model.addAttribute(LICENSES, getDataLicenses());
        model.addAttribute("allDataMap", datasetManager.getDatasetMap());
        model.addAttribute("requestForm", new DataRequestForm());
        model.addAttribute("keywords", keywords);
        return "data";
    }

    @GetMapping(value={"/contribute", "/contribute/{id}"})
    public String contributeData(Model model, @PathVariable Optional<String> id, HttpSession session) {
        if (id.isPresent()) {
            Dataset dataset = getDataset(id.get());
            if (dataset.getContributorId().equals(session.getAttribute("id").toString())) {
                model.addAttribute(EDITABLE_FLAG, true);
            }
            model.addAttribute(DATASET, dataset);
            model.addAttribute("data", dataset);
        } else {
            model.addAttribute(EDITABLE_FLAG, true);
            model.addAttribute(DATASET, new Dataset());
        }

        model.addAttribute(CATEGORIES, getDataCategories());
        model.addAttribute(LICENSES, getDataLicenses());
        model.addAttribute("requestForm", new DataRequestForm());
        model.addAttribute("agreementForm", new LicenseAgreementForm());
        return CONTRIBUTE_DATA_PAGE;
    }

    private Dataset getDataset(String id) {
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity <String> response = restTemplate.exchange(properties.getDataset(id), HttpMethod.GET, request, String.class);
        String dataResponseBody = response.getBody();
        JSONObject dataInfoObject = new JSONObject(dataResponseBody);
        return extractDataInfo(dataInfoObject.toString());
    }

    @RequestMapping(value = "/licensesInfo")
    public String getLicensesInfo(Model model) {
        model.addAttribute(LICENSES, getDataLicenses());
        return "data_licenses_info";
    }

    private List<DataCategory> getDataCategories() {
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity <String> response = restTemplate.exchange(properties.getCategories(), HttpMethod.GET, request, String.class);
        String responseBody = response.getBody();
        JSONArray jsonArray = new JSONArray(responseBody);
        List<DataCategory> dataCategories = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            DataCategory dataCategory = extractCategoryInfo(jsonObject.toString());
            dataCategories.add(dataCategory);
        }
        return dataCategories;
    }

    private List<DataLicense> getDataLicenses() {
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity <String> response = restTemplate.exchange(properties.getLicenses(), HttpMethod.GET, request, String.class);
        String responseBody = response.getBody();
        JSONArray jsonArray = new JSONArray(responseBody);
        List<DataLicense> dataLicenses = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            DataLicense dataLicense = extractLicenseInfo(jsonObject.toString());
            dataLicenses.add(dataLicense);
        }
        return dataLicenses;
    }

    @PostMapping(value={"/contribute", "/contribute/{id}"})
    public String validateContributeData(@Valid @ModelAttribute("dataset") Dataset dataset,
                                         BindingResult bindingResult,
                                         Model model, @PathVariable Optional<String> id,
                                         HttpSession session) throws WebServiceRuntimeException {
        setContributor(dataset, session);

        if (bindingResult.hasErrors()) {
            StringBuilder message = new StringBuilder();
            message.append(ERRORS_STR);
            message.append(UL_TAG_START);
            for (ObjectError objectError : bindingResult.getAllErrors()) {
                FieldError fieldError = (FieldError) objectError;
                message.append(LI_START_TAG);
                switch (fieldError.getField()) {
                    case "categoryId":
                        message.append("category must be selected");
                        break;
                    case "licenseId":
                        message.append("license must be selected");
                        break;
                    default:
                        message.append(fieldError.getField());
                        message.append(" ");
                        message.append(fieldError.getDefaultMessage());
                }
                message.append(LI_END_TAG);
            }
            message.append(UL_END_TAG);
            model.addAttribute(MESSAGE_ATTRIBUTE, message.toString());
            model.addAttribute(CATEGORIES, getDataCategories());
            model.addAttribute(LICENSES, getDataLicenses());
            if (id.isPresent()) {
                Dataset data = getDataset(id.get());
                model.addAttribute("data", data);
            }
            model.addAttribute(EDITABLE_FLAG, true);
            return CONTRIBUTE_DATA_PAGE;
        }

        JSONObject dataObject = new JSONObject();
        dataObject.put("name", dataset.getName());
        dataObject.put("description", dataset.getDescription());
        dataObject.put("contributorId", dataset.getContributorId());
        dataObject.put("visibility", dataset.getVisibility());
        dataObject.put("accessibility", dataset.getAccessibility());
        dataObject.put(RESOURCES, new ArrayList<String>());
        dataObject.put("approvedUsers", new ArrayList<String>());
        dataObject.put("releasedDate", dataset.getReleasedDate());
        dataObject.put("categoryId", dataset.getCategoryId());
        dataObject.put("licenseId", dataset.getLicenseId());
        dataObject.put("keywords", dataset.getKeywordList());
        log.debug("DataObject: {}", dataObject.toString());

        HttpEntity<String> request = createHttpEntityWithBody(dataObject.toString());
        restTemplate.setErrorHandler(new MyResponseErrorHandler());

        ResponseEntity <String> response = getResponseEntity(id, request);
        String dataResponseBody = response.getBody();

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(dataResponseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                checkExceptionState(dataset, model, exceptionState);
                return CONTRIBUTE_DATA_PAGE;
            }
        } catch (IOException e) {
            log.error("validateContributeData: {}", e.toString());
            throw new WebServiceRuntimeException(e.getMessage());
        }

        log.info("Dataset saved: {}", dataResponseBody);
        return REDIRECT_DATA;
    }

    private void checkExceptionState(@Valid @ModelAttribute("dataset") Dataset dataset, Model model, ExceptionState exceptionState) {
        switch (exceptionState) {
            case DATA_NAME_ALREADY_EXISTS_EXCEPTION:
                log.warn("Dataset name already exists: {}", dataset.getName());
                model.addAttribute(MESSAGE_ATTRIBUTE, "Error(s):<ul><li>dataset name already exists</li></ul>");
                break;
            case DATA_NOT_FOUND_EXCEPTION:
                log.warn("Dataset not found for updating.");
                model.addAttribute(MESSAGE_ATTRIBUTE, "Error(s):<ul><li>dataset not found for editing</li></ul>");
                break;
            case FORBIDDEN_EXCEPTION:
                log.warn("Saving of dataset forbidden.");
                model.addAttribute(MESSAGE_ATTRIBUTE, "Error(s):<ul><li>saving dataset forbidden</li></ul>");
                break;
            default:
                log.warn("Unknown error for validating data contribution.");
                model.addAttribute(MESSAGE_ATTRIBUTE, "Error(s):<ul><li>unknown error for validating data contribution</li></ul>");
        }
    }

    @RequestMapping(value={"/remove/{id}", "/remove/{id}/{admin}"})
    public String removeDataset(@PathVariable String id, @PathVariable Optional<String> admin, RedirectAttributes redirectAttributes) throws WebServiceRuntimeException {
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        UriComponentsBuilder uriComponents = UriComponentsBuilder.fromUriString(properties.getDataset(id));
        ResponseEntity <String> response = restTemplate.exchange(uriComponents.toUriString(), HttpMethod.DELETE, request, String.class);
        String dataResponseBody = response.getBody();

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(dataResponseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                if (exceptionState == FORBIDDEN_EXCEPTION) {
                    log.warn("Removing of dataset forbidden.");
                    redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, error.getMessage());
                } else if (exceptionState == DATA_NOT_FOUND_EXCEPTION) {
                    log.warn("Dataset not found for removing.");
                    redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, error.getMessage());
                } else {
                    log.warn("Unknown error for removing dataset.");
                }
            } else {
                log.info("Dataset removed: {}", dataResponseBody);
            }
        } catch (IOException e) {
            log.error("removeDataset: {}", e.toString());
            throw new WebServiceRuntimeException(e.getMessage());
        }

        if (admin.isPresent()) {
            return "redirect:/admin/data";
        }
        return REDIRECT_DATA;
    }

    @PostMapping(value = "/{id}/request")
    public String requestDataset(@PathVariable String id,
                                 @ModelAttribute DataRequestForm requestForm,
                                 RedirectAttributes redirectAttributes) throws WebServiceRuntimeException {
        JSONObject requestObject = new JSONObject();
        requestObject.put("reason", requestForm.getReason());

        HttpEntity<String> request = createHttpEntityWithBody(requestObject.toString());
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        UriComponentsBuilder uriComponents = UriComponentsBuilder.fromUriString(properties.requestDataset(id));
        ResponseEntity <String> response = restTemplate.exchange(uriComponents.toUriString(), HttpMethod.POST, request, String.class);
        String dataResponseBody = response.getBody();

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(dataResponseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                if (exceptionState == FORBIDDEN_EXCEPTION) {
                    log.warn("Requesting of dataset access forbidden.");
                    redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, error.getMessage());
                } else if (exceptionState == DATA_NOT_FOUND_EXCEPTION) {
                    log.warn("Dataset not found for requesting access.");
                    redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, error.getMessage());
                } else {
                    log.warn("Unknown error for requesting dataset access.");
                }
            } else {
                log.info("Dataset access requested: {}", dataResponseBody);
                redirectAttributes.addFlashAttribute("success", true);
            }
        } catch (IOException e) {
            log.error("requestDataset: {}", e.toString());
            throw new WebServiceRuntimeException(e.getMessage());
        }

        return REDIRECT_CONTRIBUTE + id;
    }

    @GetMapping(value = "{did}/requests/{rid}")
    public String getRequest(Model model, @PathVariable String did, @PathVariable String rid) throws WebServiceRuntimeException {
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        UriComponentsBuilder uriComponents = UriComponentsBuilder.fromUriString(properties.getRequest(did, rid));
        ResponseEntity <String> response = restTemplate.exchange(uriComponents.toUriString(), HttpMethod.GET, request, String.class);
        String dataResponseBody = response.getBody();

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(dataResponseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                if (exceptionState == FORBIDDEN_EXCEPTION) {
                    log.warn("Requesting of dataset access forbidden.");
                    model.addAttribute(MESSAGE_ATTRIBUTE, error.getMessage());
                } else if (exceptionState == DATA_ACCESS_REQUEST_NOT_FOUND_EXCEPTION) {
                    log.warn("Dataset access request not found.");
                    model.addAttribute(MESSAGE_ATTRIBUTE, error.getMessage());
                } else if (exceptionState == DATA_NOT_MATCH_EXCEPTION) {
                    log.warn("Dataset access request does not have matching parent.");
                    model.addAttribute(MESSAGE_ATTRIBUTE, error.getMessage());
                } else if (exceptionState == DATA_NOT_FOUND_EXCEPTION) {
                    log.warn("Dataset not found for requesting access.");
                    model.addAttribute(MESSAGE_ATTRIBUTE, error.getMessage());
                } else {
                    log.warn("Unknown error for getting dataset access request.");
                }
            } else {
                log.info("Dataset access request retrieved: {}", dataResponseBody);
                DataAccessRequest dataAccessRequest = extractDataAccessRequest(dataResponseBody);
                model.addAttribute("dataAccessRequest", dataAccessRequest);
            }
        } catch (IOException e) {
            log.error("getRequest: {}", e.toString());
            throw new WebServiceRuntimeException(e.getMessage());
        }

        return "data_request_access";
    }

    @RequestMapping("{did}/requests/{rid}/approve")
    public String approveRequest(@PathVariable String did,
                                 @PathVariable String rid,
                                 RedirectAttributes redirectAttributes) throws WebServiceRuntimeException {
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        UriComponentsBuilder uriComponents = UriComponentsBuilder.fromUriString(properties.getRequest(did, rid));
        ResponseEntity <String> response = restTemplate.exchange(uriComponents.toUriString(), HttpMethod.PUT, request, String.class);
        String dataResponseBody = response.getBody();

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(dataResponseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                if (exceptionState == FORBIDDEN_EXCEPTION) {
                    log.warn("Approving of dataset access request forbidden.");
                    redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, error.getMessage());
                } else if (exceptionState == DATA_ACCESS_REQUEST_NOT_FOUND_EXCEPTION) {
                    log.warn("Dataset access request not found.");
                    redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, error.getMessage());
                } else if (exceptionState == DATA_NOT_MATCH_EXCEPTION) {
                    log.warn("Dataset access request does not have matching parent.");
                    redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, error.getMessage());
                } else if (exceptionState == DATA_NOT_FOUND_EXCEPTION) {
                    log.warn("Dataset not found for approving request.");
                    redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, error.getMessage());
                } else {
                    log.warn("Unknown error for approving dataset access request.");
                }
            } else {
                log.info("Dataset access request approved: {}", dataResponseBody);
                redirectAttributes.addFlashAttribute("approved_message", "Request Approved");
            }
        } catch (IOException e) {
            log.error("approveRequest: {}", e.toString());
            throw new WebServiceRuntimeException(e.getMessage());
        }

        return "redirect:/data/" + did + "/requests/" + rid;
    }

    @GetMapping(value = "/public")
    public String getPublicDatasets(Model model) {
        DatasetManager datasetManager = new DatasetManager();

        HttpEntity<String> dataRequest = createHttpEntityHeaderOnlyNoAuthHeader();
        ResponseEntity <String> dataResponse = restTemplate.exchange(properties.getPublicData(), HttpMethod.GET, dataRequest, String.class);
        String dataResponseBody = dataResponse.getBody();

        JSONArray dataPublicJsonArray = new JSONArray(dataResponseBody);
        for (int i = 0; i < dataPublicJsonArray.length(); i++) {
            JSONObject dataInfoObject = dataPublicJsonArray.getJSONObject(i);
            Dataset dataset = extractDataInfo(dataInfoObject.toString());
            datasetManager.addDataset(dataset);
        }

        model.addAttribute("publicDataMap", datasetManager.getDatasetMap());
        return "data_public";
    }

    @GetMapping(value = "/public/{id}")
    public String getPublicDataset(HttpSession session, Model model, @PathVariable String id) {
        if (session.getAttribute("id") != null && !session.getAttribute("id").toString().isEmpty()) {
            return REDIRECT_CONTRIBUTE + id;
        }
        HttpEntity<String> dataRequest = createHttpEntityHeaderOnlyNoAuthHeader();
        UriComponentsBuilder uriComponents = UriComponentsBuilder.fromUriString(properties.getPublicDataset(id));
        ResponseEntity <String> dataResponse = restTemplate.exchange(uriComponents.toUriString(), HttpMethod.GET, dataRequest, String.class);
        String dataResponseBody = dataResponse.getBody();
        JSONObject dataInfoObject = new JSONObject(dataResponseBody);
        Dataset dataset = extractDataInfo(dataInfoObject.toString());
        model.addAttribute(DATASET, dataset);
        model.addAttribute("puser", new PublicUser());
        return "data_public_id";
    }

    @PostMapping(value = "/public/{id}")
    public String checkPublicDataset(HttpSession session, Model model, @PathVariable String id,
                                     @Valid @ModelAttribute("puser") PublicUser puser, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder message = new StringBuilder();
            message.append(ERRORS_STR);
            message.append(UL_TAG_START);
            for (ObjectError objectError : bindingResult.getAllErrors()) {
                FieldError fieldError = (FieldError) objectError;
                message.append(LI_START_TAG);
                switch (fieldError.getField()) {
                    case "fullName":
                        message.append("You have to fill in your full name");
                        break;
                    case "email":
                        message.append("You have to fill in your email address");
                        break;
                    case "jobTitle":
                        message.append("You have to fill in your job title");
                        break;
                    case "institution":
                        message.append("You have to fill in your institution");
                        break;
                    case "country":
                        message.append("You have to fill in your country");
                        break;
                    case "licenseAgreed":
                        message.append("You have to agree to the licensing terms");
                        break;
                    default:
                        message.append(fieldError.getField());
                        message.append(" ");
                        message.append(fieldError.getDefaultMessage());
                }
                message.append(LI_END_TAG);
            }
            message.append(UL_END_TAG);
            model.addAttribute(MESSAGE_ATTRIBUTE, message);
            HttpEntity<String> dataRequest = createHttpEntityHeaderOnlyNoAuthHeader();
            UriComponentsBuilder uriComponents = UriComponentsBuilder.fromUriString(properties.getPublicDataset(id));
            ResponseEntity <String> dataResponse = restTemplate.exchange(uriComponents.toUriString(), HttpMethod.GET, dataRequest, String.class);
            String dataResponseBody = dataResponse.getBody();
            JSONObject dataInfoObject = new JSONObject(dataResponseBody);
            Dataset dataset = extractDataInfo(dataInfoObject.toString());
            model.addAttribute(DATASET, dataset);
            return "data_public_id";
        }

        JSONObject puserObject = new JSONObject();
        puserObject.put("fullName", puser.getFullName());
        puserObject.put("email", puser.getEmail());
        puserObject.put("jobTitle", puser.getJobTitle());
        puserObject.put("institution", puser.getInstitution());
        puserObject.put("country", puser.getCountry());
        puserObject.put("licenseAgreed", puser.isLicenseAgreed());

        HttpEntity<String> request = createHttpEntityWithBodyNoAuthHeader(puserObject.toString());
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity <String> response = restTemplate.exchange(properties.getPublicDataUsers(), HttpMethod.POST, request, String.class);
        String responseBody = response.getBody();
        log.info("Public user saved: {}", responseBody);
        JSONObject object = new JSONObject(responseBody);
        session.setAttribute(PUBLIC_USER_ID, object.getLong("id"));

        return REDIRECT_DATA + "/public/" + id + "/" + RESOURCES;
    }

    @GetMapping(value = "/public/{id}/resources")
    public String getPublicResources(HttpSession session, Model model, @PathVariable String id, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("id") != null && !session.getAttribute("id").toString().isEmpty()) {
            return REDIRECT_DATA + "/" + id + "/" + RESOURCES;
        } else if (session.getAttribute(PUBLIC_USER_ID) == null || session.getAttribute(PUBLIC_USER_ID).toString().isEmpty()) {
            redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, "Please fill in your details before trying to access resources.");
            return REDIRECT_DATA + "/public/" + id;
        }

        HttpEntity<String> dataRequest = createHttpEntityHeaderOnlyNoAuthHeader();
        UriComponentsBuilder uriComponents = UriComponentsBuilder.fromUriString(properties.getPublicDataset(id));
        ResponseEntity <String> dataResponse = restTemplate.exchange(uriComponents.toUriString(), HttpMethod.GET, dataRequest, String.class);
        String dataResponseBody = dataResponse.getBody();
        JSONObject dataInfoObject = new JSONObject(dataResponseBody);
        Dataset dataset = extractDataInfo(dataInfoObject.toString());
        model.addAttribute(DATASET, dataset);

        return "data_public_resources";
    }

    @GetMapping(value = "/public/{did}/resources/{rid}")
    public void getPublicOpenResource(HttpSession session, @PathVariable String did, @PathVariable String rid,
                                      final HttpServletResponse httpResponse) throws UnsupportedEncodingException, WebServiceRuntimeException {
        if (session.getAttribute(PUBLIC_USER_ID) == null || session.getAttribute(PUBLIC_USER_ID).toString().isEmpty()) {
            throw new WebServiceRuntimeException("No public user id for downloading.");
        }
        try {
            String publicUserId = session.getAttribute(PUBLIC_USER_ID).toString();
            // Optional Accept header
            RequestCallback requestCallback = request -> {
                request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
                request.getHeaders().set("PublicUserId", publicUserId);
            };

            // Streams the response instead of loading it all in memory
            ResponseExtractor<Void> responseExtractor = getResponseExtractor(httpResponse);

            UriComponentsBuilder uriComponents = UriComponentsBuilder.fromUriString(properties.downloadPublicOpenResource(did, rid));
            restTemplate.execute(uriComponents.toUriString(), HttpMethod.GET, requestCallback, responseExtractor);
        } catch (Exception e) {
            log.error("Error transferring download: {}", e.getMessage());
        }
    }

    private ResponseExtractor<Void> getResponseExtractor(final HttpServletResponse httpResponse) {
        return response -> {
            String content = response.getHeaders().get("Content-Disposition").get(0);
            httpResponse.setContentType("application/octet-stream");
            httpResponse.setContentLengthLong(Long.parseLong(response.getHeaders().get("Content-Length").get(0)));
            httpResponse.setHeader("Content-Disposition", content);
            IOUtils.copy(response.getBody(), httpResponse.getOutputStream());
            httpResponse.flushBuffer();
            return null;
        };
    }

    @RequestMapping("{datasetId}/resources")
    public String getResources(@PathVariable String datasetId, @ModelAttribute LicenseAgreementForm agreementForm,
                               HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!agreementForm.isLicenseAgreed()) {
            StringBuilder message = new StringBuilder();
            message.append(ERRORS_STR);
            message.append(UL_TAG_START);
            message.append(LI_START_TAG);
            message.append("You have to agree to the licensing terms");
            message.append(LI_END_TAG);
            message.append(UL_END_TAG);
            redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, message);
            redirectAttributes.addFlashAttribute("hasErrors", true);
            return REDIRECT_CONTRIBUTE + datasetId;
        }
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        UriComponentsBuilder uriComponents = UriComponentsBuilder.fromUriString(properties.getDataset(datasetId));
        ResponseEntity <String> response = restTemplate.exchange(uriComponents.toUriString(), HttpMethod.GET, request, String.class);
        String dataResponseBody = response.getBody();
        JSONObject dataInfoObject = new JSONObject(dataResponseBody);
        Dataset dataset = extractDataInfo(dataInfoObject.toString());
        if (dataset.getContributorId().equals(session.getAttribute("id").toString())) {
            model.addAttribute(EDITABLE_FLAG, true);
        }
        model.addAttribute(DATASET, dataset);
        return "data_resources";
    }

    @RequestMapping("{datasetId}/stats")
    public String getStatistics(Model model,
                                @PathVariable String datasetId,
                                @RequestParam(value = "start", required = false) String start,
                                @RequestParam(value = "end", required = false) String end,
                                HttpSession session) {
        if (!validateIfAdmin(session)) {
            return "nopermission";
        }
        if (start == null) {
            start = "";
        }
        if (end == null) {
            end = "";
        }

        HttpEntity<String> request = createHttpEntityHeaderOnly();
        UriComponentsBuilder uriComponents = UriComponentsBuilder.fromUriString(properties.getDataset(datasetId));
        ResponseEntity <String> response = restTemplate.exchange(uriComponents.toUriString(), HttpMethod.GET, request, String.class);
        String dataResponseBody = response.getBody();
        JSONObject dataInfoObject = new JSONObject(dataResponseBody);
        Dataset dataset = extractDataInfo(dataInfoObject.toString());

        if (start.isEmpty() && end.isEmpty()) {
            response = restTemplate.exchange(properties.getDownloadStat("id=" + datasetId), HttpMethod.GET, request, String.class);
        } else if (end.isEmpty()) {
            response = restTemplate.exchange(properties.getDownloadStat("id=" + datasetId, START_DATE + start), HttpMethod.GET, request, String.class);
        } else if (start.isEmpty()) {
            response = restTemplate.exchange(properties.getDownloadStat("id=" + datasetId, END_DATE + end), HttpMethod.GET, request, String.class);
        } else {
            response = restTemplate.exchange(properties.getDownloadStat("id=" + datasetId, START_DATE + start, END_DATE + end), HttpMethod.GET, request, String.class);
        }
        String responseBody = response.getBody();

        Map<Integer, Long> downloadStats = new HashMap<>();
        JSONArray statJsonArray = new JSONArray(responseBody);
        for (int i = 0; i < statJsonArray.length(); i++) {
            JSONObject statInfoObject = statJsonArray.getJSONObject(i);
            downloadStats.put(statInfoObject.getInt(DATA_ID), statInfoObject.getLong("count"));
        }

        if (start.isEmpty() && end.isEmpty()) {
            response = restTemplate.exchange(properties.getPublicDownloadStat("id=" + datasetId), HttpMethod.GET, request, String.class);
        } else if (end.isEmpty()) {
            response = restTemplate.exchange(properties.getPublicDownloadStat("id=" + datasetId, START_DATE + start), HttpMethod.GET, request, String.class);
        } else if (start.isEmpty()) {
            response = restTemplate.exchange(properties.getPublicDownloadStat("id=" + datasetId, END_DATE + end), HttpMethod.GET, request, String.class);
        } else {
            response = restTemplate.exchange(properties.getPublicDownloadStat("id=" + datasetId, START_DATE + start, END_DATE + end), HttpMethod.GET, request, String.class);
        }
        responseBody = response.getBody();
        Map<Integer, Long> publicDownloadStats = new HashMap<>();
        statJsonArray = new JSONArray(responseBody);
        for (int i = 0; i < statJsonArray.length(); i++) {
            JSONObject statInfoObject = statJsonArray.getJSONObject(i);
            publicDownloadStats.put(statInfoObject.getInt(DATA_ID), statInfoObject.getLong("count"));
        }

        model.addAttribute(DATASET, dataset);
        model.addAttribute("downloadStats", downloadStats);
        model.addAttribute("publicDownloadStats", publicDownloadStats);
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        return "data_statistics";
    }

    /**
     * References:
     * [1] https://github.com/23/resumable.js/blob/master/samples/java/src/main/java/resumable/js/upload/UploadServlet.java
     */
    @GetMapping(value="{datasetId}/resources/upload")
    public ResponseEntity<String> checkChunk(@PathVariable String datasetId, HttpServletRequest request) {
        int resumableChunkNumber = getResumableChunkNumber(request);
        ResumableInfo info = getResumableInfo(request);
        UriComponentsBuilder uriComponents = UriComponentsBuilder.fromUriString(properties.checkUploadChunk(datasetId, resumableChunkNumber, info.resumableIdentifier));
        log.debug("URL: {}", uriComponents.toUriString());
        HttpEntity<String> httpEntity = createHttpEntityHeaderOnly();
        ResponseEntity <String> responseEntity = restTemplate.exchange(uriComponents.toUriString(), HttpMethod.GET, httpEntity, String.class);
        String body = responseEntity.getBody();
        log.debug(body);
        if (body.equals("Not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }
        return ResponseEntity.ok(body);
    }

    @PostMapping(value="{datasetId}/resources/upload")
    public ResponseEntity<String> uploadChunk(@PathVariable String datasetId, HttpServletRequest request) {
        int resumableChunkNumber = getResumableChunkNumber(request);
        ResumableInfo info = getResumableInfo(request);

        try {
            InputStream is = request.getInputStream();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            long readed = 0;
            long contentLength = request.getContentLength();
            byte[] bytes = new byte[1024 * 100];
            while (readed < contentLength) {
                int r = is.read(bytes);
                if (r < 0)  {
                    break;
                }
                os.write(bytes, 0, r);
                readed += r;
            }

            JSONObject resumableObject = new JSONObject();
            resumableObject.put("resumableChunkSize", info.resumableChunkSize);
            resumableObject.put("resumableTotalSize", info.resumableTotalSize);
            resumableObject.put("resumableIdentifier", info.resumableIdentifier);
            resumableObject.put("resumableFilename", info.resumableFilename);
            resumableObject.put("resumableRelativePath", info.resumableRelativePath);

            String resumableChunk = Base64.encodeBase64String(os.toByteArray());
            log.debug(resumableChunk);
            resumableObject.put("resumableChunk", resumableChunk);

            UriComponentsBuilder uriComponents = UriComponentsBuilder.fromUriString(properties.sendUploadChunk(datasetId, resumableChunkNumber));
            log.debug("URL: {}", uriComponents.toUriString());
            HttpEntity<String> httpEntity = createHttpEntityWithBody(resumableObject.toString());
            restTemplate.setErrorHandler(new MyResponseErrorHandler());
            ResponseEntity <String> responseEntity = restTemplate.exchange(uriComponents.toUriString(), HttpMethod.POST, httpEntity, String.class);
            String body = responseEntity.getBody();

            if (RestUtil.isError(responseEntity.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(body, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                return getStringResponseEntity(exceptionState);
            } else if (body.equals("All finished.")) {
                log.info("Data resource uploaded.");
            }
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            log.error("Error sending upload chunk: {}", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending upload chunk");
        }
    }

    private ResponseEntity<String> getStringResponseEntity(ExceptionState exceptionState) {
        switch (exceptionState) {
            case DATA_NOT_FOUND_EXCEPTION:
                log.warn("Dataset not found for uploading resource.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dataset not found for uploading resource.");
            case DATA_RESOURCE_ALREADY_EXISTS_EXCEPTION:
                log.warn("Data resource already exist.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Data resource already exist");
            case FORBIDDEN_EXCEPTION:
                log.warn("Uploading of dataset resource forbidden.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Uploading of dataset resource forbidden.");
            case UPLOAD_ALREADY_EXISTS_EXCEPTION:
                log.warn("Upload of data resource already exist.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Upload of data resource already exist.");
            default:
                log.warn("Unknown exception while uploading resource.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unknown exception while uploading resource.");
        }
    }

    /**
     * References:
     * [1] http://stackoverflow.com/questions/25854077/calling-a-servlet-from-another-servlet-after-the-request-dispatcher-forward-meth
     * [2] http://stackoverflow.com/questions/29712554/how-to-download-a-file-using-a-java-rest-service-and-a-data-stream
     * [3] http://stackoverflow.com/questions/32988370/download-large-file-from-server-using-rest-template-java-spring-mvc
     */
    @GetMapping(value="{datasetId}/resources/{resourceId}")
    public void getResource(@PathVariable String datasetId, @PathVariable String resourceId, HttpSession session,
                            final HttpServletResponse httpResponse) throws UnsupportedEncodingException, WebServiceRuntimeException {
        Dataset dataset = invokeAndExtractDataInfo(Long.valueOf(datasetId));
        if (!dataset.isDownloadable(session.getAttribute("id").toString())) {
            throw new WebServiceRuntimeException("Resource download denied!");
        }

        try {
            // Optional Accept header
            RequestCallback requestCallback = request -> {
                request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
                request.getHeaders().set("Authorization", httpScopedSession.getAttribute(webProperties.getSessionJwtToken()).toString());
            };

            // Streams the response instead of loading it all in memory
            ResponseExtractor<Void> responseExtractor = getResponseExtractor(httpResponse);

            UriComponentsBuilder uriComponents = UriComponentsBuilder.fromUriString(properties.downloadResource(datasetId, resourceId));
            restTemplate.execute(uriComponents.toUriString(), HttpMethod.GET, requestCallback, responseExtractor);
        } catch (Exception e) {
            log.error("Error transferring download: {}", e.getMessage());
        }
    }

    @GetMapping(value = "{datasetId}/resources/{resourceId}/delete")
    public String removeResource(@PathVariable String datasetId, @PathVariable String resourceId, RedirectAttributes redirectAttributes) throws WebServiceRuntimeException {
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        UriComponentsBuilder uriComponents = UriComponentsBuilder.fromUriString(properties.getResource(datasetId, resourceId));
        ResponseEntity <String> response = restTemplate.exchange(uriComponents.toUriString(), HttpMethod.DELETE, request, String.class);
        String dataResponseBody = response.getBody();

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(dataResponseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                if (exceptionState == FORBIDDEN_EXCEPTION) {
                    log.warn("Removing of data resource forbidden.");
                    redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, error.getMessage());
                } else if (exceptionState == DATA_NOT_FOUND_EXCEPTION) {
                    log.warn("Dataset not found for removing resource.");
                    redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, error.getMessage());
                } else if (exceptionState == DATA_RESOURCE_NOT_FOUND_EXCEPTION) {
                    log.warn("Data resource not found for removing.");
                    redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, error.getMessage());
                } else if (exceptionState == DATA_RESOURCE_DELETE_EXCEPTION) {
                    log.warn("Error when removing data resource file.");
                    redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, error.getMessage());
                } else {
                    log.warn("Unknown error for removing data resource.");
                }
            } else {
                log.info("Data resource removed: {}", dataResponseBody);
            }
        } catch (IOException e) {
            log.error("removeResource: {}", e);
            throw new WebServiceRuntimeException(e.getMessage());
        }

        return "redirect:/data/" + datasetId + "/" + RESOURCES;
    }

    private void setContributor(Dataset dataset, HttpSession session) {
        if (dataset.getContributorId() == null) {
            dataset.setContributorId(session.getAttribute("id").toString());
        }
        dataset.setContributor(invokeAndExtractUserInfo(dataset.getContributorId()));
    }

    private ResponseEntity<String> getResponseEntity(@PathVariable Optional<String> id, HttpEntity<String> request) {
        ResponseEntity <String> response;
        if (!id.isPresent()) {
            response = restTemplate.exchange(properties.getSioDataUrl(), HttpMethod.POST, request, String.class);
        } else {
            response = restTemplate.exchange(properties.getDataset(id.get()), HttpMethod.PUT, request, String.class);
        }
        return response;
    }

    private int getResumableChunkNumber(HttpServletRequest request) {
        return HttpUtils.toInt(request.getParameter("resumableChunkNumber"), -1);
    }

    private ResumableInfo getResumableInfo(HttpServletRequest request) {
        ResumableInfo info = new ResumableInfo();
        info.resumableChunkSize          = HttpUtils.toInt(request.getParameter("resumableChunkSize"), -1);
        info.resumableTotalSize         = HttpUtils.toLong(request.getParameter("resumableTotalSize"), -1);
        info.resumableIdentifier      = request.getParameter("resumableIdentifier");
        info.resumableFilename        = request.getParameter("resumableFilename");
        info.resumableRelativePath    = request.getParameter("resumableRelativePath");
        return info;
    }

    private DataAccessRequest extractDataAccessRequest(String json) {
        log.debug(json);

        DataAccessRequest dataAccessRequest = new DataAccessRequest();
        JSONObject object = new JSONObject(json);

        dataAccessRequest.setId(object.getLong("id"));
        dataAccessRequest.setDataId(object.getLong(DATA_ID));
        dataAccessRequest.setRequesterId(object.getString("requesterId"));
        dataAccessRequest.setReason(object.getString("reason"));
        try {
            dataAccessRequest.setRequestDate(getZonedDateTime(object.get("requestDate").toString()));
        } catch (IOException e) {
            log.warn("Error getting request date {}", e);
            dataAccessRequest.setRequestDate(null);
        }
        try {
            dataAccessRequest.setApprovedDate(getZonedDateTime(object.get("approvedDate").toString()));
        } catch (IOException e) {
            log.warn("Error getting approved date {}", e);
            dataAccessRequest.setApprovedDate(null);
        }

        dataAccessRequest.setRequester(invokeAndExtractUserInfo(dataAccessRequest.getRequesterId()));
        dataAccessRequest.setDataset(invokeAndExtractDataInfo(dataAccessRequest.getDataId()));

        return dataAccessRequest;
    }

}
