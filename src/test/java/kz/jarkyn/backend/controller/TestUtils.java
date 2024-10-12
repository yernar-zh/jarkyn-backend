package kz.jarkyn.backend.controller;

import com.jayway.jsonpath.JsonPath;
import org.springframework.test.web.servlet.MvcResult;

public class TestUtils {
    public static String extractId(MvcResult result) throws Exception {
        String responseBody = result.getResponse().getContentAsString();
        return JsonPath.parse(responseBody).read("$.id");
    }
}
