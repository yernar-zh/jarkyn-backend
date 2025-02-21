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
class PaymentOutControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DirtiesContext
    public void testDetail_success() throws Exception {
        mockMvc.perform(get(Api.PaymentOut.PATH + "/5c799431-3bc3-400f-b9a3-209f27b935a0")
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("5c799431-3bc3-400f-b9a3-209f27b935a0"))
                .andExpect(jsonPath("$.name").value("PO-00001"))
                .andExpect(jsonPath("$.organization.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.organization.name").value("ИП Жырқын"))
                .andExpect(jsonPath("$.moment").value("2024-12-07T22:47:00"))
                .andExpect(jsonPath("$.currency.id").value("e6a3c207-a358-47bf-ac18-2d09973f3807"))
                .andExpect(jsonPath("$.currency.name").value("CNY"))
                .andExpect(jsonPath("$.exchangeRate").value(68))
                .andExpect(jsonPath("$.amount").value(710))
                .andExpect(jsonPath("$.deleted").value(false))
                .andExpect(jsonPath("$.commited").value(false))
                .andExpect(jsonPath("$.comment").value(""))
                .andExpect(jsonPath("$.account.id").value("c8190dcc-1cbe-4df6-a582-0f85e9850335"))
                .andExpect(jsonPath("$.account.name").value("Наличный CNY"))
                .andExpect(jsonPath("$.counterparty.id").value("94fadc9a-83bb-4639-be07-f825ab9eb40e"))
                .andExpect(jsonPath("$.counterparty.name").value("Урумчи Кытай"))
                .andExpect(jsonPath("$.receiptNumber").isEmpty())
                .andExpect(jsonPath("$.itemOfExpenditure").value("SUPPLY"))
                .andExpect(jsonPath("$.purpose").isEmpty())
                .andExpect(jsonPath("$.paidDocuments.length()").value(1))
                .andExpect(jsonPath("$.paidDocuments[0].id").value("538c3271-7398-4fab-ad05-0a886188de11"))
                .andExpect(jsonPath("$.paidDocuments[0].document.id").value("17c1285b-6514-45d5-88a2-3b9f673dc5e3"))
                .andExpect(jsonPath("$.paidDocuments[0].document.name").value("SP-00001"))
                .andExpect(jsonPath("$.paidDocuments[0].document.organization.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.paidDocuments[0].document.organization.name").value("ИП Жырқын"))
                .andExpect(jsonPath("$.paidDocuments[0].document.moment").value("2024-12-07T21:47:00"))
                .andExpect(jsonPath("$.paidDocuments[0].document.currency.id").value("e6a3c207-a358-47bf-ac18-2d09973f3807"))
                .andExpect(jsonPath("$.paidDocuments[0].document.currency.name").value("CNY"))
                .andExpect(jsonPath("$.paidDocuments[0].document.exchangeRate").value(68))
                .andExpect(jsonPath("$.paidDocuments[0].document.amount").value(710))
                .andExpect(jsonPath("$.paidDocuments[0].document.deleted").value(false))
                .andExpect(jsonPath("$.paidDocuments[0].document.commited").value(false))
                .andExpect(jsonPath("$.paidDocuments[0].document.comment").value(""))
                .andExpect(jsonPath("$.paidDocuments[0].amount").value(710));
    }

    @Test
    @DirtiesContext
    public void testDetail_notFound() throws Exception {
        mockMvc.perform(get(Api.PaymentOut.PATH + "/a5747a2c-c97c-11ee-0a80-0777003791a7").with(TestUtils.auth()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    @Test
    @DirtiesContext
    public void testList_success() throws Exception {
        mockMvc.perform(get(Api.PaymentOut.PATH).with(TestUtils.auth())
                        .queryParam("search", "001 урум")
                        .queryParam("sort", "-name")
                        .queryParam("page.first", "0")
                        .queryParam("page.size", "50")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.row[0].name").value("PO-00001"))
                .andExpect(jsonPath("$.row[0].id").value("5c799431-3bc3-400f-b9a3-209f27b935a0"))
                .andExpect(jsonPath("$.row[0].currency.id").value("e6a3c207-a358-47bf-ac18-2d09973f3807"))
                .andExpect(jsonPath("$.row[0].currency.name").value("CNY"))
                .andExpect(jsonPath("$.row[0].currency.id").value("e6a3c207-a358-47bf-ac18-2d09973f3807"))
                .andExpect(jsonPath("$.row[0].currency.name").value("CNY"))
                .andExpect(jsonPath("$.row[0].comment").value(""))
                .andExpect(jsonPath("$.row[0].attached").value(710))
                .andExpect(jsonPath("$.row[0].purpose").isEmpty())
                .andExpect(jsonPath("$.row[0].deleted").value(false))
                .andExpect(jsonPath("$.row[0].amount").value(710))
                .andExpect(jsonPath("$.row[0].moment").value("2024-12-07T22:47:00"))
                .andExpect(jsonPath("$.row[0].account.name").value("Наличный CNY"))
                .andExpect(jsonPath("$.row[0].account.id").value("c8190dcc-1cbe-4df6-a582-0f85e9850335"))
                .andExpect(jsonPath("$.row[0].itemOfExpenditure").value("SUPPLY"))
                .andExpect(jsonPath("$.row[0].organization.name").value("ИП Жырқын"))
                .andExpect(jsonPath("$.row[0].organization.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.row[0].counterparty.name").value("Урумчи Кытай"))
                .andExpect(jsonPath("$.row[0].counterparty.id").value("94fadc9a-83bb-4639-be07-f825ab9eb40e"))
                .andExpect(jsonPath("$.row[0].exchangeRate").value(68))
                .andExpect(jsonPath("$.row[0].commited").value(false))
                .andExpect(jsonPath("$.row[0].receiptNumber").isEmpty())
                .andExpect(jsonPath("$.row[0].notAttached").value(0))
                .andExpect(jsonPath("$.sum.attached").value(710))
                .andExpect(jsonPath("$.sum.amount").value(710))
                .andExpect(jsonPath("$.sum.exchangeRate").value(68))
                .andExpect(jsonPath("$.sum.notAttached").value(0))
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
                  "currency": {
                    "id": "559109ea-f824-476d-8fa4-9990e53880ff"
                  },
                  "exchangeRate": 69,
                  "amount": 10000,
                  "account": {
                    "id": "c8190dcc-1cbe-4df6-a582-0f85e9850335"
                  },
                  "counterparty": {
                    "id": "94fadc9a-83bb-4639-be07-f825ab9eb40e"
                  },
                  "itemOfExpenditure": "SUPPLY",
                  "purpose": "aaa",
                  "receiptNumber": "1234",
                  "comment": "",
                  "state": "NEW",
                  "paidDocuments": [{
                    "document": {
                      "id": "17c1285b-6514-45d5-88a2-3b9f673dc5e3"
                    },
                    "amount": 5000
                  }]
                }
                """;
        MvcResult result = mockMvc.perform(post(Api.PaymentOut.PATH).with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();
        mockMvc.perform(get(Api.PaymentOut.PATH + "/" + TestUtils.extractId(result)).with(TestUtils.auth())
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TestUtils.extractId(result)))
                .andExpect(jsonPath("$.name").value("PO-00003"))
                .andExpect(jsonPath("$.organization.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.organization.name").value("ИП Жырқын"))
                .andExpect(jsonPath("$.moment").value("2025-01-01T16:17:07"))
                .andExpect(jsonPath("$.currency.id").value("559109ea-f824-476d-8fa4-9990e53880ff"))
                .andExpect(jsonPath("$.currency.name").value("KZT"))
                .andExpect(jsonPath("$.exchangeRate").value(69))
                .andExpect(jsonPath("$.amount").value(10000))
                .andExpect(jsonPath("$.deleted").value(false))
                .andExpect(jsonPath("$.commited").value(false))
                .andExpect(jsonPath("$.comment").value(""))
                .andExpect(jsonPath("$.account.id").value("c8190dcc-1cbe-4df6-a582-0f85e9850335"))
                .andExpect(jsonPath("$.account.name").value("Наличный CNY"))
                .andExpect(jsonPath("$.counterparty.id").value("94fadc9a-83bb-4639-be07-f825ab9eb40e"))
                .andExpect(jsonPath("$.counterparty.name").value("Урумчи Кытай"))
                .andExpect(jsonPath("$.receiptNumber").value("1234"))
                .andExpect(jsonPath("$.itemOfExpenditure").value("SUPPLY"))
                .andExpect(jsonPath("$.purpose").value("aaa"))
                .andExpect(jsonPath("$.paidDocuments[0].payment.id").value(TestUtils.extractId(result)))
                .andExpect(jsonPath("$.paidDocuments[0].document.id").value("17c1285b-6514-45d5-88a2-3b9f673dc5e3"))
                .andExpect(jsonPath("$.paidDocuments[0].amount").value(5000));
    }

    @Test
    @DirtiesContext
    public void testEdit_success() throws Exception {
        String requestData = """
                {
                  "name": "PO-00101",
                  "organization": {
                    "id": "c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"
                  },
                  "moment": "2025-01-01T17:01:00",
                  "currency": {
                    "id": "559109ea-f824-476d-8fa4-9990e53880ff"
                  },
                  "exchangeRate": 1,
                  "amount": 2000,
                  "deleted": false,
                  "commited": false,
                  "comment": "",
                  "account": {
                    "id": "6057082b-041b-47b7-ba31-9fa693eb2a21"
                  },
                  "counterparty": {
                    "id": "94fadc9a-83bb-4639-be07-f825ab9eb40e"
                  },
                  "receiptNumber": "1234",
                  "itemOfExpenditure": "SUPPLY",
                  "purpose": "zxcv",
                  "paidDocuments": [
                    {
                      "document": {
                        "id": "17c1285b-6514-45d5-88a2-3b9f673dc5e3"
                      },
                      "amount": 1000
                    }
                  ]
                }
                """;
        mockMvc.perform(put(Api.PaymentOut.PATH + "/5c799431-3bc3-400f-b9a3-209f27b935a0")
                        .with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("5c799431-3bc3-400f-b9a3-209f27b935a0"));
        mockMvc.perform(get(Api.PaymentOut.PATH + "/5c799431-3bc3-400f-b9a3-209f27b935a0").with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("5c799431-3bc3-400f-b9a3-209f27b935a0"))
                .andExpect(jsonPath("$.name").value("PO-00101"))
                .andExpect(jsonPath("$.organization.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.organization.name").value("ИП Жырқын"))
                .andExpect(jsonPath("$.moment").value("2025-01-01T17:01:00"))
                .andExpect(jsonPath("$.currency.id").value("559109ea-f824-476d-8fa4-9990e53880ff"))
                .andExpect(jsonPath("$.currency.name").value("KZT"))
                .andExpect(jsonPath("$.exchangeRate").value(1))
                .andExpect(jsonPath("$.amount").value(2000))
                .andExpect(jsonPath("$.deleted").value(false))
                .andExpect(jsonPath("$.commited").value(false))
                .andExpect(jsonPath("$.comment").value(""))
                .andExpect(jsonPath("$.account.id").value("6057082b-041b-47b7-ba31-9fa693eb2a21"))
                .andExpect(jsonPath("$.account.name").value("Ернар Ж."))
                .andExpect(jsonPath("$.counterparty.id").value("94fadc9a-83bb-4639-be07-f825ab9eb40e"))
                .andExpect(jsonPath("$.counterparty.name").value("Урумчи Кытай"))
                .andExpect(jsonPath("$.receiptNumber").value("1234"))
                .andExpect(jsonPath("$.itemOfExpenditure").value("SUPPLY"))
                .andExpect(jsonPath("$.purpose").value("zxcv"))
                .andExpect(jsonPath("$.paidDocuments[0].document.id").value("17c1285b-6514-45d5-88a2-3b9f673dc5e3"))
                .andExpect(jsonPath("$.paidDocuments[0].amount").value(1000));
    }

    @Test
    @DirtiesContext
    public void testCommit_success() throws Exception {
        mockMvc.perform(put(Api.PaymentOut.PATH + "/5c799431-3bc3-400f-b9a3-209f27b935a0/commit").with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("5c799431-3bc3-400f-b9a3-209f27b935a0"));
        mockMvc.perform(get(Api.PaymentOut.PATH + "/5c799431-3bc3-400f-b9a3-209f27b935a0").with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("5c799431-3bc3-400f-b9a3-209f27b935a0"))
                .andExpect(jsonPath("$.name").value("PO-00001"))
                .andExpect(jsonPath("$.commited").value(true));
        mockMvc.perform(get(Api.Account.PATH + "/c8190dcc-1cbe-4df6-a582-0f85e9850335").with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("c8190dcc-1cbe-4df6-a582-0f85e9850335"))
                .andExpect(jsonPath("$.balance").value(-710));
        mockMvc.perform(get(Api.Account.PATH).with(TestUtils.auth())
                        .queryParam("counterparty.id", "94fadc9a-83bb-4639-be07-f825ab9eb40e"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.row.length()").value(1))
                .andExpect(jsonPath("$.row[0].organization.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.row[0].currency.id").value("e6a3c207-a358-47bf-ac18-2d09973f3807"))
                .andExpect(jsonPath("$.row[0].currency.symbol").value("¥"))
                .andExpect(jsonPath("$.row[0].balance").value(-710));
    }
}