package kz.jarkyn.backend.controller;

import com.jayway.jsonpath.JsonPath;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public class TestUtils {
    public static String extractId(MvcResult result) throws Exception {
        String responseBody = result.getResponse().getContentAsString();
        return JsonPath.parse(responseBody).read("$.id");
    }

    public static RequestPostProcessor auth() {
        return request -> {
            request.addHeader("Authorization", "Bearer <AUTH_TOKEN>");
            request.setContentType(MediaType.APPLICATION_JSON.toString());
            return request;
        };
    }
}
