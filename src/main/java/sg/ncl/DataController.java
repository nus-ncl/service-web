package sg.ncl;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sg.ncl.domain.ExceptionState;
import sg.ncl.exceptions.WebServiceRuntimeException;
import sg.ncl.testbed_interface.DataResource;
import sg.ncl.testbed_interface.Dataset;
import sg.ncl.testbed_interface.RestUtil;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static sg.ncl.domain.ExceptionState.FORBIDDEN_EXCEPTION;

/**
 * Created by dcsjnh on 11/17/2016.
 */
@Controller
@RequestMapping("/data")
@Slf4j
public class DataController extends MainController {

    private static final String CONTRIBUTE_DATA_PAGE = "data_contribute";
    private static final String MESSAGE_ATTRIBUTE = "message";

    @RequestMapping
    public String data(Model model, HttpSession session) {
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

        model.addAttribute("allDataMap", datasetManager.getDatasetMap());
        return "data";
    }

    @RequestMapping(value={"/contribute", "/contribute/{id}"}, method= RequestMethod.GET)
    public String contributeData(Model model, @PathVariable Optional<String> id) throws Exception {
        if (id.isPresent()) {
            HttpEntity<String> request = createHttpEntityHeaderOnly();
            ResponseEntity response = restTemplate.exchange(properties.getDataset(id.get()), HttpMethod.GET, request, String.class);
            String dataResponseBody = response.getBody().toString();
            JSONObject dataInfoObject = new JSONObject(dataResponseBody);
            Dataset dataset = extractDataInfo(dataInfoObject.toString());
            model.addAttribute("dataset", dataset);
        } else {
            model.addAttribute("dataset", new Dataset());
        }
        return CONTRIBUTE_DATA_PAGE;
    }

    @RequestMapping(value={"/contribute", "/contribute/{id}"}, method=RequestMethod.POST)
    public String validateContributeData(@Valid @ModelAttribute("dataset") Dataset dataset,
                                         BindingResult bindingResult,
                                         Model model, @PathVariable Optional<String> id,
                                         HttpSession session) throws WebServiceRuntimeException {
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
            return CONTRIBUTE_DATA_PAGE;
        }

        JSONObject dataObject = new JSONObject();
        dataObject.put("name", dataset.getName());
        dataObject.put("description", dataset.getDescription());
        dataObject.put("contributorId", session.getAttribute("id").toString());
        dataObject.put("visibility", dataset.getVisibility());
        dataObject.put("accessibility", dataset.getAccessibility());
        dataObject.put("resources", new ArrayList());
        dataObject.put("approvedUsers", new ArrayList());
        dataObject.put("releasedDate", dataset.getReleasedDate());
        log.debug("DataObject: {}", dataObject.toString());

        HttpEntity<String> request = createHttpEntityWithBody(dataObject.toString());
        restTemplate.setErrorHandler(new MyResponseErrorHandler());

        ResponseEntity response = getResponseEntity(id, request);
        String dataResponseBody = response.getBody().toString();

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(dataResponseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                switch (exceptionState) {
                    case DATASET_NAME_IN_USE_EXCEPTION:
                        log.warn("Dataset name already exists.");
                        model.addAttribute(MESSAGE_ATTRIBUTE, "Error(s):<ul><li>dataset name already exists</li></ul>");
                        break;
                    case FORBIDDEN_EXCEPTION:
                        log.warn("Saving of dataset forbidden.");
                        model.addAttribute(MESSAGE_ATTRIBUTE, "Error(s):<ul><li>saving dataset forbidden</li></ul>");
                        break;
                    default:
                        log.warn("Unknown error.");
                }

                return CONTRIBUTE_DATA_PAGE;
            }
        } catch (IOException e) {
            log.error("validateContributeData: {}", e.toString());
            throw new WebServiceRuntimeException(e.getMessage());
        }

        return "redirect:/data";
    }

    @RequestMapping("/remove/{id}")
    public String removeDataset(@PathVariable String id, RedirectAttributes redirectAttributes) throws WebServiceRuntimeException {
        HttpEntity<String> request = createHttpEntityHeaderOnly();
        restTemplate.setErrorHandler(new MyResponseErrorHandler());
        ResponseEntity response = restTemplate.exchange(properties.getDataset(id), HttpMethod.DELETE, request, String.class);
        String dataResponseBody = response.getBody().toString();

        try {
            if (RestUtil.isError(response.getStatusCode())) {
                MyErrorResource error = objectMapper.readValue(dataResponseBody, MyErrorResource.class);
                ExceptionState exceptionState = ExceptionState.parseExceptionState(error.getError());

                if (exceptionState == FORBIDDEN_EXCEPTION) {
                    log.error("Removing of dataset forbidden.");
                    redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, error.getMessage());
                }
            }
        } catch (IOException e) {
            log.error("removeDataset: {}", e.toString());
            throw new WebServiceRuntimeException(e.getMessage());
        }

        return "redirect:/data";
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

    private Dataset extractDataInfo(String json) {
        log.debug(json);

        JSONObject object = new JSONObject(json);
        Dataset dataset = new Dataset();

        dataset.setId(object.getInt("id"));
        dataset.setName(object.getString("name"));
        dataset.setDescription(object.getString("description"));
        dataset.setContributorId(object.getString("contributorId"));
        dataset.addVisibility(object.getString("visibility"));
        dataset.addAccessibility(object.getString("accessibility"));
        try {
            dataset.setReleasedDate(getZonedDateTime(object.get("releasedDate").toString()));
        } catch (IOException e) {
            log.warn("Error getting released date {}", e);
            dataset.setReleasedDate(null);
        }

        dataset.setContributor(invokeAndExtractUserInfo(dataset.getContributorId()));

        JSONArray resources = object.getJSONArray("resources");
        for (int i = 0; i < resources.length(); i++) {
            JSONObject resource = resources.getJSONObject(i);
            DataResource dataResource = new DataResource();
            dataResource.setId(resource.getLong("id"));
            dataResource.setUri(resource.getString("uri"));
            dataset.addResource(dataResource);
        }

        JSONArray approvedUsers = object.getJSONArray("approvedUsers");
        for (int i =0; i < approvedUsers.length(); i++) {
            dataset.addApprovedUser(approvedUsers.getString(0));
        }

        return dataset;
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

}
