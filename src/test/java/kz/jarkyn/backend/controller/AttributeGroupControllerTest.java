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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AttributeGroupControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql({"attribute.sql"})
    public void testDetail() throws Exception {
        mockMvc.perform(get(Api.AttributeGroup.PATH + "/c5a95fbd-121e-4f57-a84b-600a9919228a"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("c5a95fbd-121e-4f57-a84b-600a9919228a"))
                .andExpect(jsonPath("$.name").value("Транспорт"))
                .andExpect(jsonPath("$.attributes[0].id").value("355785a2-0dd8-49f8-987f-06e3c48bf9a8"))
                .andExpect(jsonPath("$.attributes[0].name").value("Мотоцикл WY"))
                .andExpect(jsonPath("$.attributes[1].id").value("e95420b5-3344-44ce-8d39-699f516ed715"))
                .andExpect(jsonPath("$.attributes[1].name").value("Мотоцикл GN"));
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
    public void testList() throws Exception {
        mockMvc.perform(get(Api.AttributeGroup.PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("c5a95fbd-121e-4f57-a84b-600a9919228a"))
                .andExpect(jsonPath("$[0].name").value("Транспорт"));
    }

    @Test
    @Sql({"attribute.sql"})
    public void testCreate() throws Exception {
        String requestData = """
                {
                  "name": "Объем двигателя"
                }""";
        mockMvc.perform(post(Api.AttributeGroup.PATH).contentType(MediaType.APPLICATION_JSON).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Объем двигателя"));
    }

    @Test
    @Sql({"attribute.sql"})
    public void testEdit() throws Exception {
        String requestData = """
                {
                  "name": "Транспорт new",
                  "attributes": [
                    {"id":  "e95420b5-3344-44ce-8d39-699f516ed715"},
                    {"id":  "355785a2-0dd8-49f8-987f-06e3c48bf9a8"}
                  ]
                }""";
        mockMvc.perform(put(Api.AttributeGroup.PATH + "/c5a95fbd-121e-4f57-a84b-600a9919228a")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("c5a95fbd-121e-4f57-a84b-600a9919228a"))
                .andExpect(jsonPath("$.name").value("Транспорт new"))
                .andExpect(jsonPath("$.parent").doesNotExist())
                .andExpect(jsonPath("$.attributes[0].id").value("e95420b5-3344-44ce-8d39-699f516ed715"))
                .andExpect(jsonPath("$.attributes[1].id").value("355785a2-0dd8-49f8-987f-06e3c48bf9a8"));
    }

    @Test
    @Sql({"attribute.sql"})
    public void testEdit_attributesNotSame() throws Exception {
        String requestData = """
                {
                  "name": "Транспорт new",
                  "attributes": [
                    {"id":  "e95420b5-3344-44ce-8d39-699f516ed715"}
                  ]
                }""";
        mockMvc.perform(put(Api.AttributeGroup.PATH + "/c5a95fbd-121e-4f57-a84b-600a9919228a")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestData))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.code").value("API Validation Error"));
    }
}