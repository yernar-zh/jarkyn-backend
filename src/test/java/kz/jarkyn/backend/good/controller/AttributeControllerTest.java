package kz.jarkyn.backend.good.controller;

import kz.jarkyn.backend.TestUtils;
import kz.jarkyn.backend.core.controller.Api;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
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
@ComponentScan(basePackages = "kz.jarkyn.backend")
@Sql({"../../auth.sql", "good.sql"})
class AttributeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DirtiesContext
    public void testDetail_success() throws Exception {
        mockMvc.perform(get(Api.Attribute.PATH + "/e95420b5-3344-44ce-8d39-699f516ed715")
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("e95420b5-3344-44ce-8d39-699f516ed715"))
                .andExpect(jsonPath("$.name").value("Мотоцикл GN"));
    }

    @Test
    @DirtiesContext
    public void testDetail_notFound() throws Exception {
        mockMvc.perform(get(Api.AttributeGroup.PATH + "/db689b56-4c87-40da-8969-e2bfbf89a84a")
                        .with(TestUtils.auth()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    @Test
    @DirtiesContext
    public void testCreate() throws Exception {
        String requestData = """
                {
                  "group": {"id":  "c5a95fbd-121e-4f57-a84b-600a9919228a"},
                  "name": "Скутер"
                }""";
        MvcResult result = mockMvc.perform(post(Api.Attribute.PATH).with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Скутер"))
                .andReturn();
        mockMvc.perform(get(Api.Attribute.PATH + "/" + TestUtils.extractId(result))
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TestUtils.extractId(result)))
                .andExpect(jsonPath("$.name").value("Скутер"));
    }

    @Test
    @DirtiesContext
    public void testCreate_incorrectGroup() throws Exception {
        String requestData = """
                {
                  "group": {"id":  "db689b56-4c87-40da-8969-e2bfbf89a84a"},
                  "name": "Скутер"
                }""";
        mockMvc.perform(post(Api.Attribute.PATH).with(TestUtils.auth()).content(requestData))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("API Validation Error"));
    }

    @Test
    @DirtiesContext
    public void testEdit_success() throws Exception {
        String requestData = """
                {
                  "name": "Мотоцикл GN new"
                }""";
        mockMvc.perform(put(Api.Attribute.PATH + "/e95420b5-3344-44ce-8d39-699f516ed715")
                        .with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("e95420b5-3344-44ce-8d39-699f516ed715"))
                .andExpect(jsonPath("$.name").value("Мотоцикл GN new"));
        mockMvc.perform(get(Api.Attribute.PATH + "/e95420b5-3344-44ce-8d39-699f516ed715")
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("e95420b5-3344-44ce-8d39-699f516ed715"))
                .andExpect(jsonPath("$.name").value("Мотоцикл GN new"));
    }

    @Test
    @DirtiesContext
    public void testEdit_notFound() throws Exception {
        String requestData = """
                {
                  "name": "Мотоцикл GN new"
                }""";
        mockMvc.perform(put(Api.AttributeGroup.PATH + "/db689b56-4c87-40da-8969-e2bfbf89a84a")
                        .with(TestUtils.auth()).content(requestData))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    @Test
    @DirtiesContext
    public void testDelete_success() throws Exception {
        mockMvc.perform(delete(Api.Attribute.PATH + "/355785a2-0dd8-49f8-987f-06e3c48bf9a8")
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("DELETED"));
        mockMvc.perform(get(Api.Attribute.PATH + "/355785a2-0dd8-49f8-987f-06e3c48bf9a8")
                        .with(TestUtils.auth()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    @Test
    @DirtiesContext
    public void testDelete_hasGood() throws Exception {
        mockMvc.perform(delete(Api.Attribute.PATH + "/e95420b5-3344-44ce-8d39-699f516ed715")
                        .with(TestUtils.auth()))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.code").value("RELATION_EXCEPTION"));
    }

    @Test
    @DirtiesContext
    public void testDelete_notFound() throws Exception {
        mockMvc.perform(delete(Api.AttributeGroup.PATH + "/db689b56-4c87-40da-8969-e2bfbf89a84a")
                        .with(TestUtils.auth()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }
}