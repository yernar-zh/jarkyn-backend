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
@Sql({"../../init.sql"})
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
                .andExpect(jsonPath("$.organization.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.organization.name").value("ИП Жырқын"))
                .andExpect(jsonPath("$.counterparty").isEmpty())
                .andExpect(jsonPath("$.bank").value("Kaspi Bank"))
                .andExpect(jsonPath("$.giro").value("+7(775)216-6661"))
                .andExpect(jsonPath("$.currency").value("KZT"))
                .andExpect(jsonPath("$.balance").value(0));
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
    public void testList_success() throws Exception {
        mockMvc.perform(get(Api.Account.PATH).with(TestUtils.auth())
                        .queryParam("counterparty.id[exists]", "false")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.first").value(0))
                .andExpect(jsonPath("$.page.size").value(20))
                .andExpect(jsonPath("$.page.totalCount").value(3))
                .andExpect(jsonPath("$.sum.balance").value(0))
                .andExpect(jsonPath("$.row.length()").value(3))
                .andExpect(jsonPath("$.row[0].id").value("6057082b-041b-47b7-ba31-9fa693eb2a21"))
                .andExpect(jsonPath("$.row[0].name").value("Ернар Ж."))
                .andExpect(jsonPath("$.row[0].currency").value("KZT"))
                .andExpect(jsonPath("$.row[0].giro").value("+7(775)216-6661"))
                .andExpect(jsonPath("$.row[0].balance").value(0))
                .andExpect(jsonPath("$.row[0].bank").value("Kaspi Bank"))
                .andExpect(jsonPath("$.row[0].organization.name").value("ИП Жырқын"))
                .andExpect(jsonPath("$.row[0].organization.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.row[1].id").value("c8190dcc-1cbe-4df6-a582-0f85e9850335"))
                .andExpect(jsonPath("$.row[1].name").value("Наличный Юань"))
                .andExpect(jsonPath("$.row[1].currency").value("CNY"))
                .andExpect(jsonPath("$.row[1].giro").isEmpty())
                .andExpect(jsonPath("$.row[1].balance").value(0))
                .andExpect(jsonPath("$.row[1].bank").isEmpty())
                .andExpect(jsonPath("$.row[1].organization.name").value("ИП Жырқын"))
                .andExpect(jsonPath("$.row[1].organization.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.row[2].id").value("8d1ed49a-6964-4a3e-bc83-8c22601e70f8"))
                .andExpect(jsonPath("$.row[2].name").value("Наличный Доллар"))
                .andExpect(jsonPath("$.row[2].currency").value("USD"))
                .andExpect(jsonPath("$.row[2].giro").isEmpty())
                .andExpect(jsonPath("$.row[2].balance").value(0))
                .andExpect(jsonPath("$.row[2].bank").isEmpty())
                .andExpect(jsonPath("$.row[2].organization.name").value("ИП Жырқын"))
                .andExpect(jsonPath("$.row[2].organization.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"));
    }

    @Test
    @DirtiesContext
    public void testCreate_success() throws Exception {
        String requestData = """
                {
                  "organization": {
                     "id": "c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"
                  },
                  "name": "Ақжол Б.",
                  "bank": "Kaspi Bank",
                  "giro": "+7(747)421-5569",
                  "currency": "KZT"
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
                .andExpect(jsonPath("$.organization.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.organization.name").value("ИП Жырқын"))
                .andExpect(jsonPath("$.bank").value("Kaspi Bank"))
                .andExpect(jsonPath("$.giro").value("+7(747)421-5569"))
                .andExpect(jsonPath("$.currency").value("KZT"))
                .andExpect(jsonPath("$.balance").value(0));
    }

    @Test
    @DirtiesContext
    public void testEdit_success() throws Exception {
        String requestData = """
                {
                  "organization": {
                     "id": "c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"
                  },
                  "name": "Ернар Ж. 1",
                  "bank": "Kaspi Bank 2",
                  "giro": "+7(775)216-6662",
                  "currency": "KZT"
                }""";
        mockMvc.perform(put(Api.Account.PATH + "/6057082b-041b-47b7-ba31-9fa693eb2a21")
                        .with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("6057082b-041b-47b7-ba31-9fa693eb2a21"));
        mockMvc.perform(get(Api.Account.PATH + "/6057082b-041b-47b7-ba31-9fa693eb2a21").with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("6057082b-041b-47b7-ba31-9fa693eb2a21"))
                .andExpect(jsonPath("$.name").value("Ернар Ж. 1"))
                .andExpect(jsonPath("$.organization.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.organization.name").value("ИП Жырқын"))
                .andExpect(jsonPath("$.bank").value("Kaspi Bank 2"))
                .andExpect(jsonPath("$.giro").value("+7(775)216-6662"))
                .andExpect(jsonPath("$.currency").value("KZT"))
                .andExpect(jsonPath("$.balance").value(0));
    }
}