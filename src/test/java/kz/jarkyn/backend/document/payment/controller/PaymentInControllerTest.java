package kz.jarkyn.backend.document.payment.controller;


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
@Sql({"../../../init.sql"})
class PaymentInControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DirtiesContext
    public void testDetail_success() throws Exception {
        mockMvc.perform(get(Api.PaymentIn.PATH + "/4aae4391-3bd4-4746-b06d-faedb32fefd8")
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("4aae4391-3bd4-4746-b06d-faedb32fefd8"))
                .andExpect(jsonPath("$.name").value("PI-00001"))
                .andExpect(jsonPath("$.organization.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.organization.name").value("ИП Жырқын"))
                .andExpect(jsonPath("$.moment").value("2024-12-30T18:20:00"))
                .andExpect(jsonPath("$.currency").value("KZT"))
                .andExpect(jsonPath("$.exchangeRate").value(1))
                .andExpect(jsonPath("$.amount").value(61500))
                .andExpect(jsonPath("$.deleted").value(false))
                .andExpect(jsonPath("$.commited").value(false))
                .andExpect(jsonPath("$.comment").value(""))
                .andExpect(jsonPath("$.account.id").value("6057082b-041b-47b7-ba31-9fa693eb2a21"))
                .andExpect(jsonPath("$.account.name").value("Ернар Ж."))
                .andExpect(jsonPath("$.counterparty.id").value("1d468c04-6360-43e5-9d51-7771e9d9dcff"))
                .andExpect(jsonPath("$.counterparty.name").value("Заманбек Жетысай"))
                .andExpect(jsonPath("$.receiptNumber").isEmpty())
                .andExpect(jsonPath("$.paidDocuments[0].payment.id").value("4aae4391-3bd4-4746-b06d-faedb32fefd8"))
                .andExpect(jsonPath("$.paidDocuments[0].amount").value(61500));
    }

    @Test
    @DirtiesContext
    public void testDetail_notFound() throws Exception {
        mockMvc.perform(get(Api.PaymentIn.PATH + "/a5747a2c-c97c-11ee-0a80-0777003791a7").with(TestUtils.auth()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    @Test
    @DirtiesContext
    public void testList_success() throws Exception {
        mockMvc.perform(get(Api.PaymentIn.PATH).with(TestUtils.auth())
                        .queryParam("search", "001 зам")
                        .queryParam("sort", "-name")
                        .queryParam("page.first", "0")
                        .queryParam("page.size", "50")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.row[0].name").value("PI-00001"))
                .andExpect(jsonPath("$.row[0].id").value("4aae4391-3bd4-4746-b06d-faedb32fefd8"))
                .andExpect(jsonPath("$.row[0].currency").value("KZT"))
                .andExpect(jsonPath("$.row[0].comment").value(""))
                .andExpect(jsonPath("$.row[0].attached").value(61500))
                .andExpect(jsonPath("$.row[0].receiptNumber").isEmpty())
                .andExpect(jsonPath("$.row[0].exchangeRate").value(1))
                .andExpect(jsonPath("$.row[0].commited").value(false))
                .andExpect(jsonPath("$.row[0].amount").value(61500))
                .andExpect(jsonPath("$.row[0].organization.name").value("ИП Жырқын"))
                .andExpect(jsonPath("$.row[0].organization.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.row[0].counterparty.name").value("Заманбек Жетысай"))
                .andExpect(jsonPath("$.row[0].counterparty.id").value("1d468c04-6360-43e5-9d51-7771e9d9dcff"))
                .andExpect(jsonPath("$.row[0].moment").value("2024-12-30T18:20:00"))
                .andExpect(jsonPath("$.row[0].deleted").value(false))
                .andExpect(jsonPath("$.row[0].account.name").value("Ернар Ж."))
                .andExpect(jsonPath("$.row[0].account.id").value("6057082b-041b-47b7-ba31-9fa693eb2a21"))
                .andExpect(jsonPath("$.row[0].notAttached").value(0))
                .andExpect(jsonPath("$.sum.attached").value(61500))
                .andExpect(jsonPath("$.sum.notAttached").value(0))
                .andExpect(jsonPath("$.sum.amount").value(61500))
                .andExpect(jsonPath("$.sum.exchangeRate").value(1))
                .andExpect(jsonPath("$.page.first").value(0))
                .andExpect(jsonPath("$.page.size").value(50))
                .andExpect(jsonPath("$.page.totalCount").value(1));
    }

    @Test
    @DirtiesContext
    public void testCreate_success() throws Exception {
        String requestData = """
                {
                  "organization": {
                    "id": "c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"
                  },
                  "moment": "2025-01-01T16:17:07",
                  "currency": "KZT",
                  "exchangeRate": 69,
                  "amount": 10000,
                  "account": {
                    "id": "6057082b-041b-47b7-ba31-9fa693eb2a21"
                  },
                  "counterparty": {
                    "id": "1d468c04-6360-43e5-9d51-7771e9d9dcff"
                  },
                  "receiptNumber": "1234",
                  "comment": "",
                  "state": "NEW"
                }
                """;
        MvcResult result = mockMvc.perform(post(Api.PaymentIn.PATH).with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();
        mockMvc.perform(get(Api.PaymentIn.PATH + "/" + TestUtils.extractId(result)).with(TestUtils.auth())
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TestUtils.extractId(result)))
                .andExpect(jsonPath("$.name").value("PI-00002"))
                .andExpect(jsonPath("$.organization.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.organization.name").value("ИП Жырқын"))
                .andExpect(jsonPath("$.moment").value("2025-01-01T16:17:07"))
                .andExpect(jsonPath("$.currency").value("KZT"))
                .andExpect(jsonPath("$.exchangeRate").value(69))
                .andExpect(jsonPath("$.amount").value(10000))
                .andExpect(jsonPath("$.deleted").value(false))
                .andExpect(jsonPath("$.commited").value(false))
                .andExpect(jsonPath("$.comment").value(""))
                .andExpect(jsonPath("$.account.id").value("6057082b-041b-47b7-ba31-9fa693eb2a21"))
                .andExpect(jsonPath("$.account.name").value("Ернар Ж."))
                .andExpect(jsonPath("$.counterparty.id").value("1d468c04-6360-43e5-9d51-7771e9d9dcff"))
                .andExpect(jsonPath("$.counterparty.name").value("Заманбек Жетысай"))
                .andExpect(jsonPath("$.receiptNumber").value("1234"))
                .andExpect(jsonPath("$.paidDocuments").isEmpty());

    }

    @Test
    @DirtiesContext
    public void testEdit_success() throws Exception {
        String requestData = """
                {
                  "name": "PI-00101",
                  "organization": {
                    "id": "c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"
                  },
                  "moment": "2025-01-01T16:17:07",
                  "currency": "KZT",
                  "exchangeRate": 69,
                  "amount": 10000,
                  "account": {
                    "id": "6057082b-041b-47b7-ba31-9fa693eb2a21"
                  },
                  "counterparty": {
                    "id": "1d468c04-6360-43e5-9d51-7771e9d9dcff"
                  },
                  "receiptNumber": "1234",
                  "comment": "",
                  "state": "NEW"
                }
                """;
        mockMvc.perform(put(Api.PaymentIn.PATH + "/4aae4391-3bd4-4746-b06d-faedb32fefd8")
                        .with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("4aae4391-3bd4-4746-b06d-faedb32fefd8"));
        mockMvc.perform(get(Api.PaymentIn.PATH + "/4aae4391-3bd4-4746-b06d-faedb32fefd8").with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("4aae4391-3bd4-4746-b06d-faedb32fefd8"))
                .andExpect(jsonPath("$.name").value("PI-00101"))
                .andExpect(jsonPath("$.organization.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.organization.name").value("ИП Жырқын"))
                .andExpect(jsonPath("$.moment").value("2025-01-01T16:17:07"))
                .andExpect(jsonPath("$.currency").value("KZT"))
                .andExpect(jsonPath("$.exchangeRate").value(69))
                .andExpect(jsonPath("$.amount").value(10000))
                .andExpect(jsonPath("$.deleted").value(false))
                .andExpect(jsonPath("$.commited").value(false))
                .andExpect(jsonPath("$.comment").value(""))
                .andExpect(jsonPath("$.account.id").value("6057082b-041b-47b7-ba31-9fa693eb2a21"))
                .andExpect(jsonPath("$.account.name").value("Ернар Ж."))
                .andExpect(jsonPath("$.counterparty.id").value("1d468c04-6360-43e5-9d51-7771e9d9dcff"))
                .andExpect(jsonPath("$.counterparty.name").value("Заманбек Жетысай"))
                .andExpect(jsonPath("$.receiptNumber").value("1234"))
                .andExpect(jsonPath("$.paidDocuments").isEmpty());
    }

    @Test
    @DirtiesContext
    public void testCommit_success() throws Exception {
        mockMvc.perform(put(Api.PaymentIn.PATH + "/4aae4391-3bd4-4746-b06d-faedb32fefd8/commit").with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("4aae4391-3bd4-4746-b06d-faedb32fefd8"));
        mockMvc.perform(get(Api.PaymentIn.PATH + "/4aae4391-3bd4-4746-b06d-faedb32fefd8").with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("4aae4391-3bd4-4746-b06d-faedb32fefd8"))
                .andExpect(jsonPath("$.name").value("PI-00001"))
                .andExpect(jsonPath("$.commited").value(true));
        mockMvc.perform(get(Api.Account.PATH + "/6057082b-041b-47b7-ba31-9fa693eb2a21").with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("6057082b-041b-47b7-ba31-9fa693eb2a21"))
                .andExpect(jsonPath("$.balance").value(61500));
        mockMvc.perform(get(Api.Account.PATH).with(TestUtils.auth())
                        .queryParam("counterparty.id", "1d468c04-6360-43e5-9d51-7771e9d9dcff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.row.length()").value(1))
                .andExpect(jsonPath("$.row[0].organization.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.row[0].currency").value("KZT"))
                .andExpect(jsonPath("$.row[0].balance").value(61500));
    }
}