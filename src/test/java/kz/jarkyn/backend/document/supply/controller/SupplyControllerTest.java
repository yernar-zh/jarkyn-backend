package kz.jarkyn.backend.document.supply.controller;

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
class SupplyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DirtiesContext
    public void testDetail_success() throws Exception {
        mockMvc.perform(get(Api.Supply.PATH + "/17c1285b-6514-45d5-88a2-3b9f673dc5e3")
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("17c1285b-6514-45d5-88a2-3b9f673dc5e3"))
                .andExpect(jsonPath("$.name").value("SP-00001"))
                .andExpect(jsonPath("$.organization.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.organization.name").value("ИП Жырқын"))
                .andExpect(jsonPath("$.moment").value("2024-12-07T21:47:00"))
                .andExpect(jsonPath("$.currency.id").value("e6a3c207-a358-47bf-ac18-2d09973f3807"))
                .andExpect(jsonPath("$.currency.name").value("CNY"))
                .andExpect(jsonPath("$.exchangeRate").value(68))
                .andExpect(jsonPath("$.amount").value(710))
                .andExpect(jsonPath("$.deleted").value(false))
                .andExpect(jsonPath("$.warehouse.id").value("523961a7-696d-4779-8bb0-fd327feaecf3"))
                .andExpect(jsonPath("$.warehouse.name").value("Кенжина"))
                .andExpect(jsonPath("$.counterparty.id").value("94fadc9a-83bb-4639-be07-f825ab9eb40e"))
                .andExpect(jsonPath("$.counterparty.name").value("Урумчи Кытай"))
                .andExpect(jsonPath("$.comment").value(""))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].good.id").value("7f316872-1da3-44c8-9293-0fddda859435"))
                .andExpect(jsonPath("$.items[0].good.name").value("Кикстартер L"))
                .andExpect(jsonPath("$.items[0].good.archived").value(false))
                .andExpect(jsonPath("$.items[0].price").value(6.2))
                .andExpect(jsonPath("$.items[0].quantity").value(50))
                .andExpect(jsonPath("$.items[0].remain").value(0))
                .andExpect(jsonPath("$.items[0].costPrice").value(0))
                .andExpect(jsonPath("$.items[1].good.id").value("bf6f2ba4-f994-44c1-839f-36a75f07242e"))
                .andExpect(jsonPath("$.items[1].good.name").value("Педаль переключения передач WY (короткий)"))
                .andExpect(jsonPath("$.items[1].good.archived").value(false))
                .andExpect(jsonPath("$.items[1].price").value(4))
                .andExpect(jsonPath("$.items[1].quantity").value(100))
                .andExpect(jsonPath("$.items[1].remain").value(0))
                .andExpect(jsonPath("$.items[1].costPrice").value(0))
                .andExpect(jsonPath("$.paidDocuments.length()").value(2))
                .andExpect(jsonPath("$.paidDocuments[0].payment.id").value("5c799431-3bc3-400f-b9a3-209f27b935a0"))
                .andExpect(jsonPath("$.paidDocuments[0].payment.name").value("PO-00001"))
                .andExpect(jsonPath("$.paidDocuments[0].payment.moment").value("2024-12-07T22:47:00"))
                .andExpect(jsonPath("$.paidDocuments[0].payment.currency.id").value("e6a3c207-a358-47bf-ac18-2d09973f3807"))
                .andExpect(jsonPath("$.paidDocuments[0].payment.currency.name").value("CNY"))
                .andExpect(jsonPath("$.paidDocuments[0].payment.exchangeRate").value(68))
                .andExpect(jsonPath("$.paidDocuments[0].payment.amount").value(710))
                .andExpect(jsonPath("$.paidDocuments[0].payment.deleted").value(false))
                .andExpect(jsonPath("$.paidDocuments[0].amount").value(710))
                .andExpect(jsonPath("$.paidDocuments[1].payment.id").value("fa81596d-a236-4256-8686-7f7f3be85ae4"))
                .andExpect(jsonPath("$.paidDocuments[1].payment.name").value("PO-00002"))
                .andExpect(jsonPath("$.paidDocuments[1].payment.moment").value("2024-12-07T23:47:00"))
                .andExpect(jsonPath("$.paidDocuments[1].payment.currency.id").value("24f15639-67da-4df4-aab4-56b85c872c3b"))
                .andExpect(jsonPath("$.paidDocuments[1].payment.currency.name").value("USD"))
                .andExpect(jsonPath("$.paidDocuments[1].payment.exchangeRate").value(525))
                .andExpect(jsonPath("$.paidDocuments[1].payment.amount").value(20))
                .andExpect(jsonPath("$.paidDocuments[1].payment.deleted").value(false))
                .andExpect(jsonPath("$.paidDocuments[1].amount").value(20));
    }

    @Test
    @DirtiesContext
    public void testDetail_notFound() throws Exception {
        mockMvc.perform(get(Api.Supply.PATH + "/a5747a2c-c97c-11ee-0a80-0777003791a7").with(TestUtils.auth()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    @Test
    @DirtiesContext
    public void testList_success() throws Exception {
        mockMvc.perform(get(Api.Supply.PATH).with(TestUtils.auth())
                        .queryParam("search", "001 урум")
                        .queryParam("sort", "-name")
                        .queryParam("page.first", "0")
                        .queryParam("page.size", "50")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.row[0].name").value("SP-00001"))
                .andExpect(jsonPath("$.row[0].id").value("17c1285b-6514-45d5-88a2-3b9f673dc5e3"))
                .andExpect(jsonPath("$.row[0].currency.id").value("e6a3c207-a358-47bf-ac18-2d09973f3807"))
                .andExpect(jsonPath("$.row[0].currency.name").value("CNY"))
                .andExpect(jsonPath("$.row[0].comment").isEmpty())
                .andExpect(jsonPath("$.row[0].deleted").value(false))
                .andExpect(jsonPath("$.row[0].amount").value(710))
                .andExpect(jsonPath("$.row[0].moment").value("2024-12-07T21:47:00"))
                .andExpect(jsonPath("$.row[0].counterparty.name").value("Урумчи Кытай"))
                .andExpect(jsonPath("$.row[0].counterparty.id").value("94fadc9a-83bb-4639-be07-f825ab9eb40e"))
                .andExpect(jsonPath("$.row[0].organization.name").value("ИП Жырқын"))
                .andExpect(jsonPath("$.row[0].organization.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.row[0].commited").value(false))
                .andExpect(jsonPath("$.row[0].warehouse.name").value("Кенжина"))
                .andExpect(jsonPath("$.row[0].warehouse.id").value("523961a7-696d-4779-8bb0-fd327feaecf3"))
                .andExpect(jsonPath("$.row[0].exchangeRate").value(68))
                .andExpect(jsonPath("$.row[0].paidAmount").value(710))
                .andExpect(jsonPath("$.row[0].notPaidAmount").value(0))
                .andExpect(jsonPath("$.sum.amount").value(710))
                .andExpect(jsonPath("$.sum.exchangeRate").value(68))
                .andExpect(jsonPath("$.sum.paidAmount").value(710))
                .andExpect(jsonPath("$.sum.notPaidAmount").value(0))
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
                  "moment": "2024-12-21T12:18:07",
                  "currency": {
                    "id": "e6a3c207-a358-47bf-ac18-2d09973f3807"
                  },
                  "exchangeRate": 70,
                  "amount": 700,
                  "warehouse": {
                    "id": "523961a7-696d-4779-8bb0-fd327feaecf3"
                  },
                  "counterparty": {
                    "id": "94fadc9a-83bb-4639-be07-f825ab9eb40e"
                  },
                  "comment": "",
                  "items": [
                    {
                      "good": {
                        "id": "7f316872-1da3-44c8-9293-0fddda859435"
                      },
                      "price": 6.5,
                      "quantity": 50
                    },
                    {
                      "good": {
                        "id": "bf6f2ba4-f994-44c1-839f-36a75f07242e"
                      },
                      "price": 5,
                      "quantity": 50
                    }
                  ]
                }
                """;
        MvcResult result = mockMvc.perform(post(Api.Supply.PATH).with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();
        mockMvc.perform(get(Api.Supply.PATH + "/" + TestUtils.extractId(result)).with(TestUtils.auth())
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TestUtils.extractId(result)))
                .andExpect(jsonPath("$.name").value("SP-00002"))
                .andExpect(jsonPath("$.organization.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.organization.name").value("ИП Жырқын"))
                .andExpect(jsonPath("$.moment").value("2024-12-21T12:18:07"))
                .andExpect(jsonPath("$.currency.id").value("e6a3c207-a358-47bf-ac18-2d09973f3807"))
                .andExpect(jsonPath("$.currency.name").value("CNY"))
                .andExpect(jsonPath("$.exchangeRate").value(70))
                .andExpect(jsonPath("$.amount").value(700))
                .andExpect(jsonPath("$.deleted").value(false))
                .andExpect(jsonPath("$.commited").value(false))
                .andExpect(jsonPath("$.comment").value(""))
                .andExpect(jsonPath("$.warehouse.id").value("523961a7-696d-4779-8bb0-fd327feaecf3"))
                .andExpect(jsonPath("$.warehouse.name").value("Кенжина"))
                .andExpect(jsonPath("$.counterparty.id").value("94fadc9a-83bb-4639-be07-f825ab9eb40e"))
                .andExpect(jsonPath("$.counterparty.name").value("Урумчи Кытай"))
                .andExpect(jsonPath("$.items[0].good.id").value("7f316872-1da3-44c8-9293-0fddda859435"))
                .andExpect(jsonPath("$.items[0].good.name").value("Кикстартер L"))
                .andExpect(jsonPath("$.items[0].good.archived").value(false))
                .andExpect(jsonPath("$.items[0].price").value(6))
                .andExpect(jsonPath("$.items[0].quantity").value(50))
                .andExpect(jsonPath("$.items[0].remain").value(0))
                .andExpect(jsonPath("$.items[0].costPrice").value(0))
                .andExpect(jsonPath("$.items[1].good.id").value("bf6f2ba4-f994-44c1-839f-36a75f07242e"))
                .andExpect(jsonPath("$.items[1].good.name").value("Педаль переключения передач WY (короткий)"))
                .andExpect(jsonPath("$.items[1].good.archived").value(false))
                .andExpect(jsonPath("$.items[1].price").value(5))
                .andExpect(jsonPath("$.items[1].quantity").value(50))
                .andExpect(jsonPath("$.items[1].remain").value(0))
                .andExpect(jsonPath("$.items[1].costPrice").value(0))
                .andExpect(jsonPath("$.paidDocuments.length()").value(0));
    }

    @Test
    @DirtiesContext
    public void testEdit_success() throws Exception {
        String requestData = """
                {
                  "id": "17c1285b-6514-45d5-88a2-3b9f673dc5e3",
                  "name": "SP-00101",
                  "organization": {
                    "id": "c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"
                  },
                  "moment": "2024-12-21T22:29:00",
                  "currency": {
                    "id": "559109ea-f824-476d-8fa4-9990e53880ff"
                  },
                  "exchangeRate": 1,
                  "amount": 30000,
                  "warehouse": {
                    "id": "d1da1441-6598-4511-bc82-8fc06602e373"
                  },
                  "counterparty": {
                    "id": "72b5a4b5-3133-4076-ab5f-84262ebcae65"
                  },
                  "comment": "я",
                  "items": [
                    {
                      "good": {
                        "id": "7f316872-1da3-44c8-9293-0fddda859435"
                      },
                      "price": 400,
                      "quantity": 80
                    }
                  ]
                }
                """;
        mockMvc.perform(put(Api.Supply.PATH + "/17c1285b-6514-45d5-88a2-3b9f673dc5e3")
                        .with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("17c1285b-6514-45d5-88a2-3b9f673dc5e3"));
        mockMvc.perform(get(Api.Supply.PATH + "/17c1285b-6514-45d5-88a2-3b9f673dc5e3").with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("17c1285b-6514-45d5-88a2-3b9f673dc5e3"))
                .andExpect(jsonPath("$.name").value("SP-00101"))
                .andExpect(jsonPath("$.organization.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.organization.name").value("ИП Жырқын"))
                .andExpect(jsonPath("$.moment").value("2024-12-21T22:29:00"))
                .andExpect(jsonPath("$.currency.id").value("559109ea-f824-476d-8fa4-9990e53880ff"))
                .andExpect(jsonPath("$.currency.name").value("KZT"))
                .andExpect(jsonPath("$.exchangeRate").value(1))
                .andExpect(jsonPath("$.amount").value(30000))
                .andExpect(jsonPath("$.deleted").value(false))
                .andExpect(jsonPath("$.commited").value(false))
                .andExpect(jsonPath("$.comment").value("я"))
                .andExpect(jsonPath("$.warehouse.id").value("d1da1441-6598-4511-bc82-8fc06602e373"))
                .andExpect(jsonPath("$.warehouse.name").value("Барыс"))
                .andExpect(jsonPath("$.counterparty.id").value("72b5a4b5-3133-4076-ab5f-84262ebcae65"))
                .andExpect(jsonPath("$.counterparty.name").value("Шанхай Кытай"))
                .andExpect(jsonPath("$.items[0].good.id").value("7f316872-1da3-44c8-9293-0fddda859435"))
                .andExpect(jsonPath("$.items[0].good.name").value("Кикстартер L"))
                .andExpect(jsonPath("$.items[0].good.archived").value(false))
                .andExpect(jsonPath("$.items[0].price").value(400))
                .andExpect(jsonPath("$.items[0].quantity").value(80))
                .andExpect(jsonPath("$.items[0].remain").value(0))
                .andExpect(jsonPath("$.items[0].costPrice").value(0));
    }

    @Test
    @DirtiesContext
    public void testCommit_success() throws Exception {
        mockMvc.perform(put(Api.Supply.PATH + "/17c1285b-6514-45d5-88a2-3b9f673dc5e3/commit")
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("17c1285b-6514-45d5-88a2-3b9f673dc5e3"));
        mockMvc.perform(get(Api.Supply.PATH + "/17c1285b-6514-45d5-88a2-3b9f673dc5e3").with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("17c1285b-6514-45d5-88a2-3b9f673dc5e3"))
                .andExpect(jsonPath("$.name").value("SP-00001"))
                .andExpect(jsonPath("$.commited").value(true))
                .andExpect(jsonPath("$.items[0].good.id").value("7f316872-1da3-44c8-9293-0fddda859435"))
                .andExpect(jsonPath("$.items[0].remain").value(0))
                .andExpect(jsonPath("$.items[0].costPrice").value(513.29))
                .andExpect(jsonPath("$.items[1].good.id").value("bf6f2ba4-f994-44c1-839f-36a75f07242e"))
                .andExpect(jsonPath("$.items[1].remain").value(0))
                .andExpect(jsonPath("$.items[1].costPrice").value(331.15));
        mockMvc.perform(get(Api.Good.PATH + "/7f316872-1da3-44c8-9293-0fddda859435").with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock[0].warehouse.id").value("523961a7-696d-4779-8bb0-fd327feaecf3"))
                .andExpect(jsonPath("$.stock[0].remain").value(50))
                .andExpect(jsonPath("$.stock[0].costPrice").value(513.29));
        mockMvc.perform(get(Api.Good.PATH + "/bf6f2ba4-f994-44c1-839f-36a75f07242e").with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock[0].warehouse.id").value("523961a7-696d-4779-8bb0-fd327feaecf3"))
                .andExpect(jsonPath("$.stock[0].remain").value(100))
                .andExpect(jsonPath("$.stock[0].costPrice").value(331.15));
        mockMvc.perform(get(Api.Account.PATH).with(TestUtils.auth())
                        .queryParam("counterparty.id", "94fadc9a-83bb-4639-be07-f825ab9eb40e"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.row.length()").value(1))
                .andExpect(jsonPath("$.row[0].organization.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.row[0].currency.id").value("e6a3c207-a358-47bf-ac18-2d09973f3807"))
                .andExpect(jsonPath("$.row[0].currency.name").value("CNY"))
                .andExpect(jsonPath("$.row[0].balance").value(710));
    }
}