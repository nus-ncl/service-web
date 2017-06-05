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
    private static final String CATEGORIES = "categories";
    private static final String LICENSES = "licenses";
    private static final String DATASET = "dataset";
    private static final String CONTRIBUTE_DATA_PAGE = "data_contribute";
    private static final String MESSAGE_ATTRIBUTE = "message";
    private static final String EDIT_DISALLOWED = "Edit/delete of dataset disallowed as user is not contributor";
    private static final String UPLOAD_DISALLOWED = "Upload of data resource disallowed as user is not contributor";

    @RequestMapping
    public String data(Model model) {
        DatasetManager datasetManager = new DatasetManager();

        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response = restTemplate.exchange(properties.getData(), HttpMethod.GET, request, String.class);
        String dataResponseBody = response.getBody().toString();

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
        return "data";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String searchData(Model model, @RequestParam("keywords") String keywords) {
        if (keywords.trim().length() == 0) {
            return REDIRECT_DATA;
        }

        DatasetManager datasetManager = new DatasetManager();

        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response = restTemplate.exchange(properties.searchDatasets(keywords), HttpMethod.GET, request, String.class);
        String dataResponseBody = response.getBody().toString();

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

    @RequestMapping(value={"/contribute", "/contribute/{id}"}, method=RequestMethod.GET)
    public String contributeData(Model model, @PathVariable Optional<String> id, HttpSession session, RedirectAttributes redirectAttributes) throws Exception {
        if (id.isPresent()) {
            HttpEntity<String> request = createHttpEntityHeaderOnly();
            ResponseEntity response = restTemplate.exchange(properties.getDataset(id.get()), HttpMethod.GET, request, String.class);
            String dataResponseBody = response.getBody().toString();
            JSONObject dataInfoObject = new JSONObject(dataResponseBody);
            Dataset dataset = extractDataInfo(dataInfoObject.toString());
            if (!dataset.getContributorId().equals(session.getAttribute("id").toString())) {
                log.warn(EDIT_DISALLOWED);
                redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, EDIT_DISALLOWED);
                return REDIRECT_DATA;
            }
            model.addAttribute(DATASET, dataset);
        } else {
            model.addAttribute(DATASET, new Dataset());
        }

        model.addAttribute(CATEGORIES, getDataCategories());
        model.addAttribute(LICENSES, getDataLicenses());
        return CONTRIBUTE_DATA_PAGE;
    }

    @RequestMapping(value = "/licensesInfo")
    public String getLicensesInfo(Model model) {
        model.addAttribute(LICENSES, getDataLicenses());
        return "data_licenses_info";
    }

    private List<DataCategory> getDataCategories() {
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response = restTemplate.exchange(properties.getCategories(), HttpMethod.GET, request, String.class);
        String responseBody = response.getBody().toString();
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
        ResponseEntity response = restTemplate.exchange(properties.getLicenses(), HttpMethod.GET, request, String.class);
        String responseBody = response.getBody().toString();
        JSONArray jsonArray = new JSONArray(responseBody);
        List<DataLicense> dataLicenses = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            DataLicense dataLicense = extractLicenseInfo(jsonObject.toString());
            dataLicenses.add(dataLicense);
        }
        return dataLicenses;
    }

    @RequestMapping(value={"/contribute", "/contribute/{id}"}, method=RequestMethod.POST)
    public String validateContributeData(@Valid @ModelAttribute("dataset") Dataset dataset,
                                         BindingResult bindingResult,
                                         Model model, @PathVariable Optional<String> id,
                                         HttpSession session) throws WebServiceRuntimeException {
        setContributor(dataset, session);

        if (bindingResult.hasErrors()) {
            StringBuilder message = new StringBuilder();
            message.append("Error(s):");
            message.append("<ul class=\"fa-ul\">");
            for (ObjectError objectError : bindingResult.getAllErrors()) {
                FieldError fieldError = (FieldError) objectError;
                message.append("<li><i class=\"fa fa-exclamation-circle\"></i> ");
                message.append(fieldError.getField());
                message.append(" ");
                message.append(fieldError.getDefaultMessage());
                message.append("</li>");
            }
            message.append("</ul>");
            model.addAttribute(MESSAGE_ATTRIBUTE, message.toString());
            model.addAttribute(CATEGORIES, getDataCategories());
            model.addAttribute(LICENSES, getDataLicenses());
            return CONTRIBUTE_DATA_PAGE;
        }

        JSONObject dataObject = new JSONObject();
        dataObject.put("name", dataset.getName());
        dataObject.put("description", dataset.getDescription());
        dataObject.put("contributorId", dataset.getContributorId());
        dataObject.put("visibility", dataset.getVisibility());
        dataObject.put("accessibility", dataset.getAccessibility());
        dataObject.put("resources", new ArrayList());
        dataObject.put("approvedUsers", new ArrayList());
        dataObject.put("releasedDate", dataset.getReleasedDate());
        dataObject.put("categoryId", dataset.getCategoryId());
        dataObject.put("licenseId", dataset.getLicenseId());
        dataObject.put("keywords", dataset.getKeywordList());
        log.debug("DataObject: {}", dataObject.toString());

        HttpEntity<String> request = createHttpEntityWithBody(dataObject.toString());
        restTemplate.setErrorHandler(new MyResponseErrorHandler());

        ResponseEntity response = getResponseEntity(id, request);
        String dataResponseBody = response.getBody().toString();

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
        ResponseEntity response = restTemplate.exchange(properties.getDataset(id), HttpMethod.DELETE, request, String.class);
        String dataResponseBody = response.getBody().toString();

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

    @RequestMapping(value = "/{id}/request", method = RequestMethod.POST)
    public String requestDataset(@PathVariable String id,
                                 @ModelAttribute DataRequestForm requestForm,
                                 RedirectAttributes redirectAttributes) throws WebServiceRuntimeException {
        JSONObject requestObject = new JSONObject();
        requestObject.put("reason", requestForm.getReason());

        HttpEntity<String> request = createHttpEntityWithBody(requestObject.toString());
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response = restTemplate.exchange(properties.requestDataset(id), HttpMethod.POST, request, String.class);
        String dataResponseBody = response.getBody().toString();

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
            }
        } catch (IOException e) {
            log.error("requestDataset: {}", e.toString());
            throw new WebServiceRuntimeException(e.getMessage());
        }

        return REDIRECT_DATA;
    }

    @RequestMapping(value = "{did}/requests/{rid}", method = RequestMethod.GET)
    public String getRequest(Model model, @PathVariable String did, @PathVariable String rid) throws WebServiceRuntimeException {
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response = restTemplate.exchange(properties.getRequest(did, rid), HttpMethod.GET, request, String.class);
        String dataResponseBody = response.getBody().toString();

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
        ResponseEntity response = restTemplate.exchange(properties.getRequest(did, rid), HttpMethod.PUT, request, String.class);
        String dataResponseBody = response.getBody().toString();

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

    @RequestMapping("/public")
    public String getPublicDatasets(Model model) {
        DatasetManager datasetManager = new DatasetManager();

        HttpEntity<String> dataRequest = createHttpEntityHeaderOnlyNoAuthHeader();
        ResponseEntity dataResponse = restTemplate.exchange(properties.getPublicData(), HttpMethod.GET, dataRequest, String.class);
        String dataResponseBody = dataResponse.getBody().toString();

        JSONArray dataPublicJsonArray = new JSONArray(dataResponseBody);
        for (int i = 0; i < dataPublicJsonArray.length(); i++) {
            JSONObject dataInfoObject = dataPublicJsonArray.getJSONObject(i);
            Dataset dataset = extractDataInfo(dataInfoObject.toString());
            datasetManager.addDataset(dataset);
        }

        model.addAttribute("publicDataMap", datasetManager.getDatasetMap());
        return "data_public";
    }

    @RequestMapping("{datasetId}/resources")
    public String getResources(Model model, @PathVariable String datasetId, HttpSession session, RedirectAttributes redirectAttributes) {
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response = restTemplate.exchange(properties.getDataset(datasetId), HttpMethod.GET, request, String.class);
        String dataResponseBody = response.getBody().toString();
        JSONObject dataInfoObject = new JSONObject(dataResponseBody);
        Dataset dataset = extractDataInfo(dataInfoObject.toString());
        if (!dataset.getContributorId().equals(session.getAttribute("id").toString())) {
            log.warn(UPLOAD_DISALLOWED);
            redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, UPLOAD_DISALLOWED);
            return REDIRECT_DATA;
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
        ResponseEntity response = restTemplate.exchange(properties.getDataset(datasetId), HttpMethod.GET, request, String.class);
        String dataResponseBody = response.getBody().toString();
        JSONObject dataInfoObject = new JSONObject(dataResponseBody);
        Dataset dataset = extractDataInfo(dataInfoObject.toString());

        if (start.isEmpty() && end.isEmpty()) {
            response = restTemplate.exchange(properties.getDownloadStat("id=" + datasetId), HttpMethod.GET, request, String.class);
        } else if (end.isEmpty()) {
            response = restTemplate.exchange(properties.getDownloadStat("id=" + datasetId, "startDate=" + start), HttpMethod.GET, request, String.class);
        } else if (start.isEmpty()) {
            response = restTemplate.exchange(properties.getDownloadStat("id=" + datasetId, "endDate=" + end), HttpMethod.GET, request, String.class);
        } else {
            response = restTemplate.exchange(properties.getDownloadStat("id=" + datasetId, "startDate=" + start, "endDate=" + end), HttpMethod.GET, request, String.class);
        }
        String responseBody = response.getBody().toString();

        Map<Integer, Long> downloadStats = new HashMap<>();
        JSONArray statJsonArray = new JSONArray(responseBody);
        for (int i = 0; i < statJsonArray.length(); i++) {
            JSONObject statInfoObject = statJsonArray.getJSONObject(i);
            downloadStats.put(statInfoObject.getInt("dataId"), statInfoObject.getLong("count"));
        }

        model.addAttribute(DATASET, dataset);
        model.addAttribute("downloadStats", downloadStats);
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        return "data_statistics";
    }

    /**
     * References:
     * [1] https://github.com/23/resumable.js/blob/master/samples/java/src/main/java/resumable/js/upload/UploadServlet.java
     */
    @RequestMapping(value="{datasetId}/resources/upload", method=RequestMethod.GET)
    public ResponseEntity<String> checkChunk(@PathVariable String datasetId, HttpServletRequest request) {
        int resumableChunkNumber = getResumableChunkNumber(request);
        ResumableInfo info = getResumableInfo(request);

        String url = properties.checkUploadChunk(datasetId, resumableChunkNumber, info.resumableIdentifier);
        log.debug("URL: {}", url);
        HttpEntity<String> httpEntity = createHttpEntityHeaderOnly();
        ResponseEntity responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        String body = responseEntity.getBody().toString();
        log.debug(body);
        if (body.equals("Not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }
        return ResponseEntity.ok(body);
    }

    @RequestMapping(value="{datasetId}/resources/upload", method=RequestMethod.POST)
    public ResponseEntity<String> uploadChunk(@PathVariable String datasetId, HttpServletRequest request) {
        int resumableChunkNumber = getResumableChunkNumber(request);
        ResumableInfo info = getResumableInfo(request);

        try {
            InputStream is = request.getInputStream();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            long readed = 0;
            long content_length = request.getContentLength();
            byte[] bytes = new byte[1024 * 100];
            while (readed < content_length) {
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

            String url = properties.sendUploadChunk(datasetId, resumableChunkNumber);
            log.debug("URL: {}", url);
            HttpEntity<String> httpEntity = createHttpEntityWithBody(resumableObject.toString());
            restTemplate.setErrorHandler(new MyResponseErrorHandler());
            ResponseEntity responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
            String body = responseEntity.getBody().toString();

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
    @RequestMapping(value="{datasetId}/resources/{resourceId}", method=RequestMethod.GET)
    public void getResource(@PathVariable String datasetId,
                            @PathVariable String resourceId,
                            final HttpServletResponse httpResponse) throws UnsupportedEncodingException {
        try {
            // Optional Accept header
            RequestCallback requestCallback = request -> {
                request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
                request.getHeaders().set("Authorization", httpScopedSession.getAttribute(webProperties.getSessionJwtToken()).toString());
            };

            // Streams the response instead of loading it all in memory
            ResponseExtractor<Void> responseExtractor = response -> {
                String content = response.getHeaders().get("Content-Disposition").get(0);
                httpResponse.setContentType("application/octet-stream");
                httpResponse.setContentLengthLong(Long.parseLong(response.getHeaders().get("Content-Length").get(0)));
                httpResponse.setHeader("Content-Disposition", content);
                IOUtils.copy(response.getBody(), httpResponse.getOutputStream());
                httpResponse.flushBuffer();
                return null;
            };

            restTemplate.execute(properties.downloadResource(datasetId, resourceId), HttpMethod.GET, requestCallback, responseExtractor);
        } catch (Exception e) {
            log.error("Error transferring download: {}", e.getMessage());
        }
    }

    @RequestMapping(value = "{datasetId}/resources/{resourceId}/delete", method = RequestMethod.GET)
    public String removeResource(@PathVariable String datasetId, @PathVariable String resourceId, RedirectAttributes redirectAttributes) throws WebServiceRuntimeException {
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response = restTemplate.exchange(properties.getResource(datasetId, resourceId), HttpMethod.DELETE, request, String.class);
        String dataResponseBody = response.getBody().toString();

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

        return "redirect:/data/" + datasetId + "/resources";
    }

    private void setContributor(Dataset dataset, HttpSession session) {
        if (dataset.getContributorId() == null) {
            dataset.setContributorId(session.getAttribute("id").toString());
        }
        dataset.setContributor(invokeAndExtractUserInfo(dataset.getContributorId()));
    }

    private ResponseEntity getResponseEntity(@PathVariable Optional<String> id, HttpEntity<String> request) {
        ResponseEntity response;
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
        dataAccessRequest.setDataId(object.getLong("dataId"));
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

    private Dataset invokeAndExtractDataInfo(Long dataId) {
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        ResponseEntity response = restTemplate.exchange(properties.getDataset(dataId.toString()), HttpMethod.GET, request, String.class);
        return extractDataInfo(response.getBody().toString());
    }

}
