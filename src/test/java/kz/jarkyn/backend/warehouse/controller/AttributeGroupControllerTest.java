package kz.jarkyn.backend.warehouse.controller;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ComponentScan(basePackages = "kz.jarkyn.backend")
@Sql({"../../init.sql"})
class AttributeGroupControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DirtiesContext
    public void testDetail_success() throws Exception {
        mockMvc.perform(get(Api.AttributeGroup.PATH + "/c5a95fbd-121e-4f57-a84b-600a9919228a")
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("c5a95fbd-121e-4f57-a84b-600a9919228a"))
                .andExpect(jsonPath("$.name").value("Транспорт"))
                .andExpect(jsonPath("$.attributes.length()").value(3))
                .andExpect(jsonPath("$.attributes[0].id").value("355785a2-0dd8-49f8-987f-06e3c48bf9a8"))
                .andExpect(jsonPath("$.attributes[0].name").value("Мотоцикл WY"))
                .andExpect(jsonPath("$.attributes[1].id").value("e95420b5-3344-44ce-8d39-699f516ed715"))
                .andExpect(jsonPath("$.attributes[1].name").value("Мотоцикл GN"))
                .andExpect(jsonPath("$.attributes[2].id").value("797e7bc8-fca0-4d2b-b1ce-6975f54b48eb"))
                .andExpect(jsonPath("$.attributes[2].name").value("Alpha"));
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
    public void testList_success() throws Exception {
        mockMvc.perform(get(Api.AttributeGroup.PATH)
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.row[0].id").value("c5a95fbd-121e-4f57-a84b-600a9919228a"))
                .andExpect(jsonPath("$.row[0].name").value("Транспорт"))
                .andExpect(jsonPath("$.row[0].attributes[0].id").value("355785a2-0dd8-49f8-987f-06e3c48bf9a8"))
                .andExpect(jsonPath("$.row[0].attributes[0].name").value("Мотоцикл WY"))
                .andExpect(jsonPath("$.row[0].attributes[1].id").value("e95420b5-3344-44ce-8d39-699f516ed715"))
                .andExpect(jsonPath("$.row[0].attributes[1].name").value("Мотоцикл GN"))
                .andExpect(jsonPath("$.row[0].attributes[2].id").value("797e7bc8-fca0-4d2b-b1ce-6975f54b48eb"))
                .andExpect(jsonPath("$.row[0].attributes[2].name").value("Alpha"))
                .andExpect(jsonPath("$.row[1].id").value("5d82a954-5b87-11ee-0a80-000c0039afd5"))
                .andExpect(jsonPath("$.row[1].name").value("Цвет"))
                .andExpect(jsonPath("$.row[1].attributes").isEmpty())
                .andExpect(jsonPath("$.page.first").value(0))
                .andExpect(jsonPath("$.page.size").value(20))
                .andExpect(jsonPath("$.page.totalCount").value(2));
    }

    @Test
    @DirtiesContext
    public void testMove_success() throws Exception {
        String requestData = """
                [
                  {"id": "5d82a954-5b87-11ee-0a80-000c0039afd5"},
                  {"id": "c5a95fbd-121e-4f57-a84b-600a9919228a"}
                ]""";
        mockMvc.perform(put(Api.AttributeGroup.PATH).with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("5d82a954-5b87-11ee-0a80-000c0039afd5"))
                .andExpect(jsonPath("$[0].name").value("Цвет"))
                .andExpect(jsonPath("$[1].id").value("c5a95fbd-121e-4f57-a84b-600a9919228a"))
                .andExpect(jsonPath("$[1].name").value("Транспорт"));
        mockMvc.perform(get(Api.AttributeGroup.PATH).with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("5d82a954-5b87-11ee-0a80-000c0039afd5"))
                .andExpect(jsonPath("$[0].name").value("Цвет"))
                .andExpect(jsonPath("$[1].id").value("c5a95fbd-121e-4f57-a84b-600a9919228a"))
                .andExpect(jsonPath("$[1].name").value("Транспорт"));
    }

    @Test
    @DirtiesContext
    public void testCreate_success() throws Exception {
        String requestData = """
                {
                  "name": "Двигатель",
                  "archived": false
                }""";
        MvcResult result = mockMvc.perform(post(Api.AttributeGroup.PATH).with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Двигатель"))
                .andReturn();
        mockMvc.perform(get(Api.AttributeGroup.PATH + "/" + TestUtils.extractId(result))
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TestUtils.extractId(result)))
                .andExpect(jsonPath("$.name").value("Двигатель"));
    }

    @Test
    @DirtiesContext
    public void testEdit_success() throws Exception {
        String requestData = """
                {
                  "name": "Транспорт new",
                  "attributes": [
                    {"id":  "e95420b5-3344-44ce-8d39-699f516ed715"},
                    {"id":  "355785a2-0dd8-49f8-987f-06e3c48bf9a8"},
                    {"id":  "797e7bc8-fca0-4d2b-b1ce-6975f54b48eb"}
                  ],
                  "archived": false
                }""";
        mockMvc.perform(put(Api.AttributeGroup.PATH + "/c5a95fbd-121e-4f57-a84b-600a9919228a")
                        .with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk());
        mockMvc.perform(get(Api.AttributeGroup.PATH + "/c5a95fbd-121e-4f57-a84b-600a9919228a")
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("c5a95fbd-121e-4f57-a84b-600a9919228a"))
                .andExpect(jsonPath("$.name").value("Транспорт new"))
                .andExpect(jsonPath("$.parent").doesNotExist())
                .andExpect(jsonPath("$.attributes[0].id").value("e95420b5-3344-44ce-8d39-699f516ed715"))
                .andExpect(jsonPath("$.attributes[1].id").value("355785a2-0dd8-49f8-987f-06e3c48bf9a8"))
                .andExpect(jsonPath("$.attributes[2].id").value("797e7bc8-fca0-4d2b-b1ce-6975f54b48eb"));

    }

    @Test
    @DirtiesContext
    public void testEdit_notFound() throws Exception {
        String requestData = """
                {
                  "name": "Транспорт new",
                  "attributes": [],
                  "archived": false
                }""";
        mockMvc.perform(put(Api.AttributeGroup.PATH + "/db689b56-4c87-40da-8969-e2bfbf89a84a")
                        .with(TestUtils.auth()).content(requestData))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    @Test
    @DirtiesContext
    public void testEdit_attributesNotSame() throws Exception {
        String requestData = """
                {
                  "name": "Транспорт new",
                  "attributes": [
                    {"id":  "e95420b5-3344-44ce-8d39-699f516ed715"}
                  ]
                }""";
        mockMvc.perform(put(Api.AttributeGroup.PATH + "/c5a95fbd-121e-4f57-a84b-600a9919228a")
                        .with(TestUtils.auth()).content(requestData))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("API Validation Error"));
    }

    @Test
    @DirtiesContext
    public void testDelete_success() throws Exception {
        mockMvc.perform(delete(Api.AttributeGroup.PATH + "/5d82a954-5b87-11ee-0a80-000c0039afd5")
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("DELETED"));
        mockMvc.perform(get(Api.AttributeGroup.PATH + "/5d82a954-5b87-11ee-0a80-000c0039afd5")
                        .with(TestUtils.auth()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    @Test
    @DirtiesContext
    public void testDelete_hasAttribute() throws Exception {
        mockMvc.perform(delete(Api.AttributeGroup.PATH + "/c5a95fbd-121e-4f57-a84b-600a9919228a")
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