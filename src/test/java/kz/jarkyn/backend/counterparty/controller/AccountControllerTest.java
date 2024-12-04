package kz.jarkyn.backend.counterparty.controller;

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
@Sql({"../../inti.sql"})
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DirtiesContext
    public void testDetail_success() throws Exception {
        mockMvc.perform(get(Api.Account.PATH + "/6057082b-041b-47b7-ba31-9fa693eb2a21")
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("6057082b-041b-47b7-ba31-9fa693eb2a21"))
                .andExpect(jsonPath("$.name").value("Ернар Ж."))
                .andExpect(jsonPath("$.bank").value("Kaspi Bank"))
                .andExpect(jsonPath("$.giro").value("+7(775)216-6661"));
    }

    @Test
    @DirtiesContext
    public void testDetail_notFound() throws Exception {
        mockMvc.perform(get(Api.Account.PATH + "/a5747a2c-c97c-11ee-0a80-0777003791a7").with(TestUtils.auth()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    @Test
    @DirtiesContext
    public void testDetail_notFoundCustomerAccount() throws Exception {
        mockMvc.perform(get(Api.Account.PATH + "/054935c1-1369-4aa1-9c55-6f76e68a0dfb").with(TestUtils.auth()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }


    @Test
    @DirtiesContext
    public void testList_success() throws Exception {
        mockMvc.perform(get(Api.Account.PATH).with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("6057082b-041b-47b7-ba31-9fa693eb2a21"))
                .andExpect(jsonPath("$[0].name").value("Ернар Ж."))
                .andExpect(jsonPath("$[0].bank").value("Kaspi Bank"))
                .andExpect(jsonPath("$[0].giro").value("+7(775)216-6661"))
                .andExpect(jsonPath("$[1].id").value("c8190dcc-1cbe-4df6-a582-0f85e9850335"))
                .andExpect(jsonPath("$[1].name").value("Жайнагүл Қ."))
                .andExpect(jsonPath("$[1].bank").value("Kaspi Bank"))
                .andExpect(jsonPath("$[1].giro").value("+7(702)445-9711"));
    }

    @Test
    @DirtiesContext
    public void testCreate_success() throws Exception {
        String requestData = """
                {
                  "name": "Ақжол Б.",
                  "bank": "Kaspi Bank",
                  "giro": "+7(747)421-5569"
                }""";
        MvcResult result = mockMvc.perform(post(Api.Account.PATH).with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();
        mockMvc.perform(get(Api.Account.PATH + "/" + TestUtils.extractId(result)).with(TestUtils.auth())
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TestUtils.extractId(result)))
                .andExpect(jsonPath("$.name").value("Ақжол Б."))
                .andExpect(jsonPath("$.balance").value(0))
                .andExpect(jsonPath("$.bank").value("Kaspi Bank"))
                .andExpect(jsonPath("$.giro").value("+7(747)421-5569"));
    }

    @Test
    @DirtiesContext
    public void testEdit_success() throws Exception {
        String requestData = """
                {
                  "name": "Ернар Ж. 1",
                  "bank": "Kaspi Bank 2",
                  "giro": "+7(775)216-6662"
                }""";
        mockMvc.perform(put(Api.Account.PATH + "/6057082b-041b-47b7-ba31-9fa693eb2a21")
                        .with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("6057082b-041b-47b7-ba31-9fa693eb2a21"));
        mockMvc.perform(get(Api.Account.PATH + "/6057082b-041b-47b7-ba31-9fa693eb2a21").with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("6057082b-041b-47b7-ba31-9fa693eb2a21"))
                .andExpect(jsonPath("$.name").value("Ернар Ж. 1"))
                .andExpect(jsonPath("$.bank").value("Kaspi Bank 2"))
                .andExpect(jsonPath("$.giro").value("+7(775)216-6662"));
    }
}