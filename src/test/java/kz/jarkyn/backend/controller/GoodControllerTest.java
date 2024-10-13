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
class GoodControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql({"good.sql"})
    public void testDetail_success() throws Exception {
        mockMvc.perform(get(Api.Good.PATH + "/7f316872-1da3-44c8-9293-0fddda859435"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("7f316872-1da3-44c8-9293-0fddda859435"))
                .andExpect(jsonPath("$.name").value("Кикстартер L"))
                .andExpect(jsonPath("$.group.id").value("cdfcf458-7cca-11ef-0a80-152f001b4886"))
                .andExpect(jsonPath("$.group.name").value("Кикстартер"))
                .andExpect(jsonPath("$.image").doesNotExist())
                .andExpect(jsonPath("$.minimumPrice").value(800))
                .andExpect(jsonPath("$.attributes[0].id").value("e95420b5-3344-44ce-8d39-699f516ed715"))
                .andExpect(jsonPath("$.attributes[0].name").value("Мотоцикл GN"));
    }

    @Test
    @Sql({"good.sql"})
    public void testDetail_notFound() throws Exception {
        mockMvc.perform(get(Api.Good.PATH + "/a5747a2c-c97c-11ee-0a80-0777003791a7"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    @Test
    @Sql({"good.sql"})
    public void testCreate_success() throws Exception {
        String requestData = """
                {
                  "name": "Кикстартер S качественный",
                  "group": {"id": "cdfcf458-7cca-11ef-0a80-152f001b4886"},
                  "minimumPrice": 1000,
                  "attributes": [{"id": "355785a2-0dd8-49f8-987f-06e3c48bf9a8"}]
                }""";
        MvcResult result = mockMvc.perform(post(Api.Good.PATH)
                        .contentType(MediaType.APPLICATION_JSON).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Кикстартер S качественный"))
                .andExpect(jsonPath("$.group.id").value("cdfcf458-7cca-11ef-0a80-152f001b4886"))
                .andExpect(jsonPath("$.group.name").value("Кикстартер"))
                .andExpect(jsonPath("$.image").doesNotExist())
                .andExpect(jsonPath("$.minimumPrice").value(1000))
                .andExpect(jsonPath("$.attributes[0].id").value("355785a2-0dd8-49f8-987f-06e3c48bf9a8"))
                .andExpect(jsonPath("$.attributes[0].name").value("Мотоцикл WY"))
                .andReturn();
        mockMvc.perform(get(Api.Good.PATH + "/" + TestUtils.extractId(result)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Кикстартер S качественный"))
                .andExpect(jsonPath("$.group.id").value("cdfcf458-7cca-11ef-0a80-152f001b4886"))
                .andExpect(jsonPath("$.group.name").value("Кикстартер"))
                .andExpect(jsonPath("$.image").doesNotExist())
                .andExpect(jsonPath("$.minimumPrice").value(1000))
                .andExpect(jsonPath("$.attributes[0].id").value("355785a2-0dd8-49f8-987f-06e3c48bf9a8"))
                .andExpect(jsonPath("$.attributes[0].name").value("Мотоцикл WY"));
    }

    // TODO
    @Test
    @Sql({"groups.sql"})
    public void testEdit_success() throws Exception {
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
                        .contentType(MediaType.APPLICATION_JSON).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("da48c6fa-6739-11ee-0a80-039b000669e2"))
                .andExpect(jsonPath("$.name").value("Педаль new"))
                .andExpect(jsonPath("$.parent").doesNotExist())
                .andExpect(jsonPath("$.children.length()").value(2))
                .andExpect(jsonPath("$.children[0].id").value("cdfcf458-7cca-11ef-0a80-152f001b4886"))
                .andExpect(jsonPath("$.children[1].id").value("6120deea-5b87-11ee-0a80-000c0039b0fd"));
        mockMvc.perform(get(Api.Group.PATH + "/da48c6fa-6739-11ee-0a80-039b000669e2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("da48c6fa-6739-11ee-0a80-039b000669e2"))
                .andExpect(jsonPath("$.name").value("Педаль new"))
                .andExpect(jsonPath("$.parent").doesNotExist())
                .andExpect(jsonPath("$.children.length()").value(2))
                .andExpect(jsonPath("$.children[0].id").value("cdfcf458-7cca-11ef-0a80-152f001b4886"))
                .andExpect(jsonPath("$.children[1].id").value("6120deea-5b87-11ee-0a80-000c0039b0fd"));
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
                        .contentType(MediaType.APPLICATION_JSON).content(requestData))
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
                        .contentType(MediaType.APPLICATION_JSON).content(requestData))
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
                        .contentType(MediaType.APPLICATION_JSON).content(requestData))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.code").value("API Validation Error"));
    }

    @Test
    @Sql({"groups.sql"})
    public void testEdit_notFound() throws Exception {
        String requestData = """
                {
                  "name": "Педаль",
                  "children": []
                }""";
        mockMvc.perform(put(Api.Group.PATH + "/a5747a2c-c97c-11ee-0a80-0777003791a7")
                        .contentType(MediaType.APPLICATION_JSON).content(requestData))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    @Test
    @Sql({"attribute.sql"})
    public void testDelete_success() throws Exception {
        mockMvc.perform(delete(Api.Group.PATH + "/cdfcf458-7cca-11ef-0a80-152f001b4886"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("DELETED"));
        mockMvc.perform(get(Api.Group.PATH + "/cdfcf458-7cca-11ef-0a80-152f001b4886"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    @Test
    @Sql({"attribute.sql"})
    public void testDelete_hasRelation() throws Exception {
        mockMvc.perform(delete(Api.Group.PATH + "/da48c6fa-6739-11ee-0a80-039b000669e2"))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.code").value("RELATION_EXCEPTION"));
    }

    @Test
    @Sql({"attribute.sql"})
    public void testDelete_notFound() throws Exception {
        mockMvc.perform(delete(Api.Group.PATH + "/a5747a2c-c97c-11ee-0a80-0777003791a7"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }
}