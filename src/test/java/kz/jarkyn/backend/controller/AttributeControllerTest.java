package kz.jarkyn.backend.controller;

import kz.jarkyn.backend.config.Api;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AttributeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql({"attribute.sql"})
    public void testDetail_success() throws Exception {
        mockMvc.perform(get(Api.Attribute.PATH + "/e95420b5-3344-44ce-8d39-699f516ed715"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("e95420b5-3344-44ce-8d39-699f516ed715"))
                .andExpect(jsonPath("$.name").value("Мотоцикл GN"));
    }

    @Test
    @Sql({"attribute.sql"})
    public void testDetail_notFound() throws Exception {
        mockMvc.perform(get(Api.AttributeGroup.PATH + "/db689b56-4c87-40da-8969-e2bfbf89a84a"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    @Test
    @Sql({"attribute.sql"})
    public void testCreate() throws Exception {
        String requestData = """
                {
                  "group": {"id":  "c5a95fbd-121e-4f57-a84b-600a9919228a"},
                  "name": "Скутер"
                }""";
        MvcResult result = mockMvc.perform(post(Api.Attribute.PATH).contentType(MediaType.APPLICATION_JSON).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Скутер"))
                .andReturn();
        mockMvc.perform(get(Api.Attribute.PATH + "/" + TestUtils.extractId(result)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TestUtils.extractId(result)))
                .andExpect(jsonPath("$.name").value("Скутер"));
    }

    @Test
    @Sql({"attribute.sql"})
    public void testCreate_incorrectGroup() throws Exception {
        String requestData = """
                {
                  "group": {"id":  "db689b56-4c87-40da-8969-e2bfbf89a84a"},
                  "name": "Скутер"
                }""";
        mockMvc.perform(post(Api.Attribute.PATH).contentType(MediaType.APPLICATION_JSON).content(requestData))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("API Validation Error"));
    }

    @Test
    @Sql({"attribute.sql"})
    public void testEdit_success() throws Exception {
        String requestData = """
                {
                  "name": "Мотоцикл GN new"
                }""";
        mockMvc.perform(put(Api.Attribute.PATH + "/e95420b5-3344-44ce-8d39-699f516ed715")
                        .contentType(MediaType.APPLICATION_JSON).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("e95420b5-3344-44ce-8d39-699f516ed715"))
                .andExpect(jsonPath("$.name").value("Мотоцикл GN new"));
        mockMvc.perform(get(Api.Attribute.PATH + "/e95420b5-3344-44ce-8d39-699f516ed715"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("e95420b5-3344-44ce-8d39-699f516ed715"))
                .andExpect(jsonPath("$.name").value("Мотоцикл GN new"));
    }

    @Test
    @Sql({"attribute.sql"})
    public void testEdit_notFound() throws Exception {
        String requestData = """
                {
                  "name": "Мотоцикл GN new"
                }""";
        mockMvc.perform(put(Api.AttributeGroup.PATH + "/db689b56-4c87-40da-8969-e2bfbf89a84a")
                        .contentType(MediaType.APPLICATION_JSON).content(requestData))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    @Test
    @Sql({"attribute.sql"})
    public void testDelete_success() throws Exception {
        mockMvc.perform(delete(Api.Attribute.PATH + "/e95420b5-3344-44ce-8d39-699f516ed715"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("DELETED"));
        mockMvc.perform(get(Api.Attribute.PATH + "/e95420b5-3344-44ce-8d39-699f516ed715"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }
    @Test
    @Sql({"attribute.sql"})
    public void testDelete_notFound() throws Exception {
        mockMvc.perform(delete(Api.AttributeGroup.PATH + "/db689b56-4c87-40da-8969-e2bfbf89a84a"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }
}