package sg.ncl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import sg.ncl.testbed_interface.RestUtil;

import java.io.IOException;

/**
 * @author Te Ye
 */
@Slf4j
public class MyResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        String statusText;
        if (response.getStatusText() == null) {
            statusText = "";
        } else {
            statusText = response.getStatusText();
        }
        log.error("Response error: {} {}", response.getStatusCode(), statusText);
    }


    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return RestUtil.isError(response.getStatusCode());
    }
}
