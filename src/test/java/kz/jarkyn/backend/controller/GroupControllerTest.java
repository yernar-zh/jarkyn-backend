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
class GroupControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql({"groups.sql"})
    public void testDetail_parentNull() throws Exception {
        mockMvc.perform(get(Api.Group.PATH + "/da48c6fa-6739-11ee-0a80-039b000669e2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("da48c6fa-6739-11ee-0a80-039b000669e2"))
                .andExpect(jsonPath("$.name").value("Педаль"))
                .andExpect(jsonPath("$.position").value(100))
                .andExpect(jsonPath("$.parent").isEmpty());
    }

    @Test
    @Sql({"groups.sql"})
    public void testDetail_parentExist() throws Exception {
        mockMvc.perform(get(Api.Group.PATH + "/cdfcf458-7cca-11ef-0a80-152f001b4886")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("cdfcf458-7cca-11ef-0a80-152f001b4886"))
                .andExpect(jsonPath("$.name").value("Кикстартер"))
                .andExpect(jsonPath("$.position").value(100))
                .andExpect(jsonPath("$.parent.id").value("da48c6fa-6739-11ee-0a80-039b000669e2"));
    }

    @Test
    @Sql({"groups.sql"})
    public void testList() throws Exception {
        mockMvc.perform(get(Api.Group.PATH).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("da48c6fa-6739-11ee-0a80-039b000669e2"))
                .andExpect(jsonPath("$[0].name").value("Педаль"))
                .andExpect(jsonPath("$[0].children[0].id").value("6120deea-5b87-11ee-0a80-000c0039b0fd"))
                .andExpect(jsonPath("$[0].children[0].name").value("Педаль переключения передач"))
                .andExpect(jsonPath("$[0].children[0].children").isEmpty())
                .andExpect(jsonPath("$[0].children[1].id").value("cdfcf458-7cca-11ef-0a80-152f001b4886"))
                .andExpect(jsonPath("$[0].children[1].name").value("Кикстартер"))
                .andExpect(jsonPath("$[0].children[1].children").isEmpty());
    }

    @Test
    @Sql({"groups.sql"})
    public void testCreate() throws Exception {
        String requestData = """
                {
                  "name": "Аккумулятор",
                  "position": 100
                }""";
        mockMvc.perform(post(Api.Group.PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Аккумулятор"))
                .andExpect(jsonPath("$.parent").doesNotExist())
                .andExpect(jsonPath("$.position").value(100));
    }

    @Test
    @Sql({"groups.sql"})
    public void testEdit() throws Exception {
        String requestData = """
                {
                  "name": "Кикстартер new",
                  "parent": null,
                  "position": 101
                }""";
        mockMvc.perform(put(Api.Group.PATH + "/cdfcf458-7cca-11ef-0a80-152f001b4886")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Кикстартер new"))
                .andExpect(jsonPath("$.parent").doesNotExist())
                .andExpect(jsonPath("$.position").value(101));
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
                  "position": 100
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
                  "position": 100
                }""";
        mockMvc.perform(put(Api.Group.PATH + "/da48c6fa-6739-11ee-0a80-039b000669e2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestData))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.code").value("EXIST_PARENT_LOOP"));
    }
}