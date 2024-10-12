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
class AttributeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql({"attribute.sql"})
    public void testDetail() throws Exception {
        mockMvc.perform(get(Api.Attribute.PATH + "/c5a95fbd-121e-4f57-a84b-600a9919228a"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("c5a95fbd-121e-4f57-a84b-600a9919228a"))
                .andExpect(jsonPath("$.name").value("Транспорт"));
    }

    @Test
    @Sql({"attribute.sql"})
    public void testDetail_notFound() throws Exception {
        mockMvc.perform(get(Api.Attribute.PATH + "/db689b56-4c87-40da-8969-e2bfbf89a84a"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    @Test
    @Sql({"attribute.sql"})
    public void testList() throws Exception {
        mockMvc.perform(get(Api.Attribute.PATH))
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
        mockMvc.perform(post(Api.Attribute.PATH).contentType(MediaType.APPLICATION_JSON).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Объем двигателя"));
    }

    @Test
    @Sql({"groups.sql"})
    public void testEdit() throws Exception {
        String requestData = """
                {
                  "name": "Педаль new",
                  "parent": null,
                  "children": [
                    {"id":  "cdfcf458-7cca-11ef-0a80-152f001b4886"},
                    {"id":  "6120deea-5b87-11ee-0a80-000c0039b0fd"}
                  ]
                }""";
        mockMvc.perform(put(Api.Group.PATH + "/da48c6fa-6739-11ee-0a80-039b000669e2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("da48c6fa-6739-11ee-0a80-039b000669e2"))
                .andExpect(jsonPath("$.name").value("Педаль new"))
                .andExpect(jsonPath("$.parent").doesNotExist())
                .andExpect(jsonPath("$.children[0].id").value("cdfcf458-7cca-11ef-0a80-152f001b4886"))
                .andExpect(jsonPath("$.children[1].id").value("6120deea-5b87-11ee-0a80-000c0039b0fd"));
        mockMvc.perform(get(Api.Group.PATH).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("da48c6fa-6739-11ee-0a80-039b000669e2"))
                .andExpect(jsonPath("$[0].children[0].id").value("cdfcf458-7cca-11ef-0a80-152f001b4886"))
                .andExpect(jsonPath("$[0].children[1].id").value("6120deea-5b87-11ee-0a80-000c0039b0fd"));
    }

    @Test
    @Sql({"groups.sql"})
    public void testEdit_existLoop() throws Exception {
        String requestData = """
                {
                  "name": "Педаль",
                  "parent": {
                    "id": "cdfcf458-7cca-11ef-0a80-152f001b4886"
                  },
                  "children": [
                    {"id":  "6120deea-5b87-11ee-0a80-000c0039b0fd"},
                    {"id":  "cdfcf458-7cca-11ef-0a80-152f001b4886"}
                  ]
                }""";
        mockMvc.perform(put(Api.Group.PATH + "/da48c6fa-6739-11ee-0a80-039b000669e2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestData))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.code").value("EXIST_PARENT_LOOP"));
    }

    @Test
    @Sql({"groups.sql"})
    public void testEdit_existLoopShort() throws Exception {
        String requestData = """
                {
                  "name": "Педаль",
                  "parent": {
                      "id": "da48c6fa-6739-11ee-0a80-039b000669e2"
                  },
                  "children": [
                    {"id":  "6120deea-5b87-11ee-0a80-000c0039b0fd"},
                    {"id":  "cdfcf458-7cca-11ef-0a80-152f001b4886"}
                  ]
                }""";
        mockMvc.perform(put(Api.Group.PATH + "/da48c6fa-6739-11ee-0a80-039b000669e2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestData))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.code").value("EXIST_PARENT_LOOP"));
    }

    @Test
    @Sql({"groups.sql"})
    public void testEdit_childrenNotSame() throws Exception {
        String requestData = """
                {
                  "name": "Педаль",
                  "children": [
                    {"id":  "cdfcf458-7cca-11ef-0a80-152f001b4886"}
                  ]
                }""";
        mockMvc.perform(put(Api.Group.PATH + "/da48c6fa-6739-11ee-0a80-039b000669e2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestData))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.code").value("API Validation Error"));
    }
}