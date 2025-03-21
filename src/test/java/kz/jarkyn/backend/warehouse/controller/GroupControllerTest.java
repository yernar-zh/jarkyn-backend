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
class GroupControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DirtiesContext
    public void testDetail_success() throws Exception {
        mockMvc.perform(get(Api.Group.PATH + "/cdfcf458-7cca-11ef-0a80-152f001b4886").with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("cdfcf458-7cca-11ef-0a80-152f001b4886"))
                .andExpect(jsonPath("$.name").value("Кикстартер"))
                .andExpect(jsonPath("$.parent.id").value("da48c6fa-6739-11ee-0a80-039b000669e2"))
                .andExpect(jsonPath("$.parent.name").value("Педаль"));
    }

    @Test
    @DirtiesContext
    public void testDetail_notFound() throws Exception {
        mockMvc.perform(get(Api.Group.PATH + "/a5747a2c-c97c-11ee-0a80-0777003791a7").with(TestUtils.auth()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    @Test
    @DirtiesContext
    public void testTree_success() throws Exception {
        mockMvc.perform(get(Api.Group.PATH+ "/tree").with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("656e38bc-bbc7-4e25-8b94-a2018783324c"))
                .andExpect(jsonPath("$.name").value("Все товары"))
                .andExpect(jsonPath("$.children[0].id").value("a590bc3a-9498-4f56-bce6-0e604b00590d"))
                .andExpect(jsonPath("$.children[0].name").value("Свеча"))
                .andExpect(jsonPath("$.children[1].id").value("da48c6fa-6739-11ee-0a80-039b000669e2"))
                .andExpect(jsonPath("$.children[1].name").value("Педаль"))
                .andExpect(jsonPath("$.children[1].children.length()").value(2))
                .andExpect(jsonPath("$.children[1].children[0].id").value("6120deea-5b87-11ee-0a80-000c0039b0fd"))
                .andExpect(jsonPath("$.children[1].children[0].name").value("Педаль переключения передач"))
                .andExpect(jsonPath("$.children[1].children[0].children").isEmpty())
                .andExpect(jsonPath("$.children[1].children[1].id").value("cdfcf458-7cca-11ef-0a80-152f001b4886"))
                .andExpect(jsonPath("$.children[1].children[1].name").value("Кикстартер"))
                .andExpect(jsonPath("$.children[1].children[1].children").isEmpty());

    }

    @Test
    @DirtiesContext
    public void testCreate_success() throws Exception {
        String requestData = """
                {
                  "name": "Тормоз",
                  "parent": {"id": "da48c6fa-6739-11ee-0a80-039b000669e2"},
                  "archived": false
                }""";
        MvcResult result = mockMvc.perform(post(Api.Group.PATH).with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Тормоз"))
                .andExpect(jsonPath("$.parent.id").value("da48c6fa-6739-11ee-0a80-039b000669e2"))
                .andExpect(jsonPath("$.parent.name").value("Педаль"))
                .andReturn();
        mockMvc.perform(get(Api.Group.PATH + "/" + TestUtils.extractId(result)).with(TestUtils.auth())
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Тормоз"))
                .andExpect(jsonPath("$.parent.id").value("da48c6fa-6739-11ee-0a80-039b000669e2"))
                .andExpect(jsonPath("$.parent.name").value("Педаль"));
    }

    @Test
    @DirtiesContext
    public void testEdit_success() throws Exception {
        String requestData = """
                {
                  "name": "Педаль new",
                  "parent": null,
                  "children": [
                    {"id":  "cdfcf458-7cca-11ef-0a80-152f001b4886"},
                    {"id":  "6120deea-5b87-11ee-0a80-000c0039b0fd"}
                  ],
                  "archived": false
                }""";
        mockMvc.perform(put(Api.Group.PATH + "/da48c6fa-6739-11ee-0a80-039b000669e2")
                        .with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("da48c6fa-6739-11ee-0a80-039b000669e2"))
                .andExpect(jsonPath("$.name").value("Педаль new"))
                .andExpect(jsonPath("$.parent").doesNotExist())
                .andExpect(jsonPath("$.children.length()").value(2))
                .andExpect(jsonPath("$.children[0].id").value("cdfcf458-7cca-11ef-0a80-152f001b4886"))
                .andExpect(jsonPath("$.children[1].id").value("6120deea-5b87-11ee-0a80-000c0039b0fd"));
        mockMvc.perform(get(Api.Group.PATH + "/da48c6fa-6739-11ee-0a80-039b000669e2").with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("da48c6fa-6739-11ee-0a80-039b000669e2"))
                .andExpect(jsonPath("$.name").value("Педаль new"))
                .andExpect(jsonPath("$.parent").doesNotExist())
                .andExpect(jsonPath("$.children.length()").value(2))
                .andExpect(jsonPath("$.children[0].id").value("cdfcf458-7cca-11ef-0a80-152f001b4886"))
                .andExpect(jsonPath("$.children[1].id").value("6120deea-5b87-11ee-0a80-000c0039b0fd"));
    }

    @Test
    @DirtiesContext
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
                  ],
                  "archived": false
                }""";
        mockMvc.perform(put(Api.Group.PATH + "/da48c6fa-6739-11ee-0a80-039b000669e2")
                        .with(TestUtils.auth()).content(requestData))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.code").value("EXIST_PARENT_LOOP"));
    }

    @Test
    @DirtiesContext
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
                  ],
                  "archived": false
                }""";
        mockMvc.perform(put(Api.Group.PATH + "/da48c6fa-6739-11ee-0a80-039b000669e2")
                        .with(TestUtils.auth()).content(requestData))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.code").value("EXIST_PARENT_LOOP"));
    }

    @Test
    @DirtiesContext
    public void testEdit_childrenNotSame() throws Exception {
        String requestData = """
                {
                  "name": "Педаль",
                  "children": [
                    {"id":  "cdfcf458-7cca-11ef-0a80-152f001b4886"}
                  ]
                }""";
        mockMvc.perform(put(Api.Group.PATH + "/da48c6fa-6739-11ee-0a80-039b000669e2")
                        .with(TestUtils.auth()).content(requestData))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.code").value("API Validation Error"));
    }

    @Test
    @DirtiesContext
    public void testEdit_notFound() throws Exception {
        String requestData = """
                {
                  "name": "Педаль",
                  "children": [],
                  "archived": false
                }""";
        mockMvc.perform(put(Api.Group.PATH + "/a5747a2c-c97c-11ee-0a80-0777003791a7")
                        .with(TestUtils.auth()).content(requestData))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    @Test
    @DirtiesContext
    public void testDelete_success() throws Exception {
        mockMvc.perform(delete(Api.Group.PATH + "/a590bc3a-9498-4f56-bce6-0e604b00590d")
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("DELETED"));
        mockMvc.perform(get(Api.Group.PATH + "/a590bc3a-9498-4f56-bce6-0e604b00590d")
                        .with(TestUtils.auth()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    @Test
    @DirtiesContext
    public void testDelete_hasChildren() throws Exception {
        mockMvc.perform(delete(Api.Group.PATH + "/da48c6fa-6739-11ee-0a80-039b000669e2")
                        .with(TestUtils.auth()))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.code").value("RELATION_EXCEPTION"));
    }

    @Test
    @DirtiesContext
    public void testDelete_hasGood() throws Exception {
        mockMvc.perform(delete(Api.Group.PATH + "/cdfcf458-7cca-11ef-0a80-152f001b4886")
                        .with(TestUtils.auth()))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.code").value("RELATION_EXCEPTION"));
    }

    @Test
    @DirtiesContext
    public void testDelete_notFound() throws Exception {
        mockMvc.perform(delete(Api.Group.PATH + "/a5747a2c-c97c-11ee-0a80-0777003791a7")
                        .with(TestUtils.auth()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }
}