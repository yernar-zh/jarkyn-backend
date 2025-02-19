package kz.jarkyn.backend.party.controller;

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
@Sql({"../../init.sql"})
class OrganizationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DirtiesContext
    public void testDetail_success() throws Exception {
        mockMvc.perform(get(Api.Organization.PATH + "/c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515")
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.name").value("ИП Жырқын"))
                .andExpect(jsonPath("$.archived").value(false));
    }

    @Test
    @DirtiesContext
    public void testDetail_notFound() throws Exception {
        mockMvc.perform(get(Api.Organization.PATH + "/a5747a2c-c97c-11ee-0a80-0777003791a7").with(TestUtils.auth()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    @Test
    @DirtiesContext
    public void testList_success() throws Exception {
        mockMvc.perform(get(Api.Organization.PATH).with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.row.length()").value(1))
                .andExpect(jsonPath("$.row[0].id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.row[0].name").value("ИП Жырқын"))
                .andExpect(jsonPath("$.row[0].archived").value(false));

    }

    @Test
    @DirtiesContext
    public void testCreate_success() throws Exception {
        String requestData = """
                {
                  "name": "TAUZ",
                  "archived": false
                }""";
        MvcResult result = mockMvc.perform(post(Api.Organization.PATH).with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();
        mockMvc.perform(get(Api.Organization.PATH + "/" + TestUtils.extractId(result)).with(TestUtils.auth())
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TestUtils.extractId(result)))
                .andExpect(jsonPath("$.name").value("TAUZ"))
                .andExpect(jsonPath("$.archived").value(false));
    }

    @Test
    @DirtiesContext
    public void testEdit_success() throws Exception {
        String requestData = """
                {
                  "name": "ТОО Жаркын",
                  "archived": true
                }""";
        mockMvc.perform(put(Api.Organization.PATH + "/c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515")
                        .with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"));
        mockMvc.perform(get(Api.Organization.PATH + "/c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515").with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.name").value("ТОО Жаркын"))
                .andExpect(jsonPath("$.archived").value(true));

    }
}