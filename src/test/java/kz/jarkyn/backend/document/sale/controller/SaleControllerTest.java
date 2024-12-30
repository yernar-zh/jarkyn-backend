package kz.jarkyn.backend.document.sale.controller;

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
import org.testcontainers.shaded.org.hamcrest.core.IsNull;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ComponentScan(basePackages = "kz.jarkyn.backend")
@Sql({"../../../init.sql"})
class SaleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DirtiesContext
    public void testDetail_success() throws Exception {
        mockMvc.perform(get(Api.Sale.PATH + "/9f26476e-e143-4468-8a37-abdb479e89b8")
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("9f26476e-e143-4468-8a37-abdb479e89b8"))
                .andExpect(jsonPath("$.name").value("SL-00001"))
                .andExpect(jsonPath("$.organization.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.organization.name").value("ИП Жырқын"))
                .andExpect(jsonPath("$.moment").value("2024-12-30T16:45:00"))
                .andExpect(jsonPath("$.currency").value("KZT"))
                .andExpect(jsonPath("$.exchangeRate").value(1))
                .andExpect(jsonPath("$.amount").value(61500))
                .andExpect(jsonPath("$.deleted").value(false))
                .andExpect(jsonPath("$.commited").value(false))
                .andExpect(jsonPath("$.comment").value(""))
                .andExpect(jsonPath("$.warehouse.id").value("523961a7-696d-4779-8bb0-fd327feaecf3"))
                .andExpect(jsonPath("$.warehouse.name").value("Кенжина"))
                .andExpect(jsonPath("$.counterparty.id").value("1d468c04-6360-43e5-9d51-7771e9d9dcff"))
                .andExpect(jsonPath("$.counterparty.name").value("Заманбек Жетысай"))
                .andExpect(jsonPath("$.state").value("NEW"))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].id").value("dcd46fd5-b931-4759-8b1b-c1ad39c3cffa"))
                .andExpect(jsonPath("$.items[0].good.id").value("7f316872-1da3-44c8-9293-0fddda859435"))
                .andExpect(jsonPath("$.items[0].good.name").value("Кикстартер L"))
                .andExpect(jsonPath("$.items[0].good.archived").value(false))
                .andExpect(jsonPath("$.items[0].price").value(850))
                .andExpect(jsonPath("$.items[0].quantity").value(40))
                .andExpect(jsonPath("$.items[0].remain").value(0))
                .andExpect(jsonPath("$.items[0].costPrice").value(0))
                .andExpect(jsonPath("$.items[1].id").value("d1c004c3-f3e8-4cd9-80f3-74233f91d9b9"))
                .andExpect(jsonPath("$.items[1].good.id").value("bf6f2ba4-f994-44c1-839f-36a75f07242e"))
                .andExpect(jsonPath("$.items[1].good.name").value("Педаль переключения передач WY (короткий)"))
                .andExpect(jsonPath("$.items[1].good.archived").value(false))
                .andExpect(jsonPath("$.items[1].price").value(550))
                .andExpect(jsonPath("$.items[1].quantity").value(50))
                .andExpect(jsonPath("$.items[1].remain").value(0))
                .andExpect(jsonPath("$.items[1].costPrice").value(0))
                .andExpect(jsonPath("$.paidDocuments.length()").value(1))
                .andExpect(jsonPath("$.paidDocuments[0].id").value("e207245e-dc26-44ab-b679-ef52b89e76f7"))
                .andExpect(jsonPath("$.paidDocuments[0].payment.id").value("4aae4391-3bd4-4746-b06d-faedb32fefd8"))
                .andExpect(jsonPath("$.paidDocuments[0].payment.name").value("PI-00001"))
                .andExpect(jsonPath("$.paidDocuments[0].payment.organization.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.paidDocuments[0].payment.organization.name").value("ИП Жырқын"))
                .andExpect(jsonPath("$.paidDocuments[0].payment.moment").value("2024-12-30T18:20:00"))
                .andExpect(jsonPath("$.paidDocuments[0].payment.currency").value("KZT"))
                .andExpect(jsonPath("$.paidDocuments[0].payment.exchangeRate").value(1))
                .andExpect(jsonPath("$.paidDocuments[0].payment.amount").value(61500))
                .andExpect(jsonPath("$.paidDocuments[0].payment.deleted").value(false))
                .andExpect(jsonPath("$.paidDocuments[0].payment.commited").value(false))
                .andExpect(jsonPath("$.paidDocuments[0].payment.comment").value(""))
                .andExpect(jsonPath("$.paidDocuments[0].amount").value(61500));
    }

    @Test
    @DirtiesContext
    public void testDetail_notFound() throws Exception {
        mockMvc.perform(get(Api.Sale.PATH + "/a5747a2c-c97c-11ee-0a80-0777003791a7").with(TestUtils.auth()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    @Test
    @DirtiesContext
    public void testList_success() throws Exception {
        mockMvc.perform(get(Api.Sale.PATH).with(TestUtils.auth())
                        .queryParam("search", "001 зам")
                        .queryParam("sort", "-name")
                        .queryParam("page.first", "0")
                        .queryParam("page.size", "50")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.row.length()").value(1))
                .andExpect(jsonPath("$.row[0].name").value("SL-00001"))
                .andExpect(jsonPath("$.row[0].id").value("9f26476e-e143-4468-8a37-abdb479e89b8"))
                .andExpect(jsonPath("$.row[0].state").value("NEW"))
                .andExpect(jsonPath("$.row[0].currency").value("KZT"))
                .andExpect(jsonPath("$.row[0].comment").value(""))
                .andExpect(jsonPath("$.row[0].deleted").value(false))
                .andExpect(jsonPath("$.row[0].moment").value("2024-12-30T16:45:00"))
                .andExpect(jsonPath("$.row[0].amount").value(61500))
                .andExpect(jsonPath("$.row[0].counterparty.name").value("Заманбек Жетысай"))
                .andExpect(jsonPath("$.row[0].counterparty.id").value("1d468c04-6360-43e5-9d51-7771e9d9dcff"))
                .andExpect(jsonPath("$.row[0].organization.name").value("ИП Жырқын"))
                .andExpect(jsonPath("$.row[0].organization.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.row[0].warehouse.name").value("Кенжина"))
                .andExpect(jsonPath("$.row[0].warehouse.id").value("523961a7-696d-4779-8bb0-fd327feaecf3"))
                .andExpect(jsonPath("$.row[0].shipmentMoment").isEmpty())
                .andExpect(jsonPath("$.row[0].costPrice").value(0))
                .andExpect(jsonPath("$.row[0].exchangeRate").value(1))
                .andExpect(jsonPath("$.row[0].commited").value(false))
                .andExpect(jsonPath("$.row[0].profit").value(0))
                .andExpect(jsonPath("$.row[0].paidAmount").value(61500))
                .andExpect(jsonPath("$.row[0].notPaidAmount").value(0))
                .andExpect(jsonPath("$.sum.amount").value(61500))
                .andExpect(jsonPath("$.sum.costPrice").value(0))
                .andExpect(jsonPath("$.sum.exchangeRate").value(1))
                .andExpect(jsonPath("$.sum.paidAmount").value(61500))
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
                  "moment": "2024-12-30T18:59:07",
                  "currency": "KZT",
                  "exchangeRate": 1,
                  "amount": 210000,
                  "warehouse": {
                    "id": "523961a7-696d-4779-8bb0-fd327feaecf3"
                  },
                  "counterparty": {
                    "id": "1d468c04-6360-43e5-9d51-7771e9d9dcff"
                  },
                  "comment": "",
                  "state": "NEW",
                  "items": [
                    {
                      "good": {
                        "id": "7f316872-1da3-44c8-9293-0fddda859435"
                      },
                      "price": 800,
                      "quantity": 200
                    },
                    {
                      "good": {
                        "id": "bf6f2ba4-f994-44c1-839f-36a75f07242e"
                      },
                      "price": 100,
                      "quantity": 500
                    }
                  ]
                }
                """;
        MvcResult result = mockMvc.perform(post(Api.Sale.PATH).with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();
        mockMvc.perform(get(Api.Sale.PATH + "/" + TestUtils.extractId(result)).with(TestUtils.auth())
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TestUtils.extractId(result)))
                .andExpect(jsonPath("$.name").value("SL-00002"))
                .andExpect(jsonPath("$.organization.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.organization.name").value("ИП Жырқын"))
                .andExpect(jsonPath("$.moment").value("2024-12-30T18:59:07"))
                .andExpect(jsonPath("$.currency").value("KZT"))
                .andExpect(jsonPath("$.exchangeRate").value(1))
                .andExpect(jsonPath("$.amount").value(210000))
                .andExpect(jsonPath("$.deleted").value(false))
                .andExpect(jsonPath("$.commited").value(false))
                .andExpect(jsonPath("$.comment").value(""))
                .andExpect(jsonPath("$.warehouse.id").value("523961a7-696d-4779-8bb0-fd327feaecf3"))
                .andExpect(jsonPath("$.warehouse.name").value("Кенжина"))
                .andExpect(jsonPath("$.counterparty.id").value("1d468c04-6360-43e5-9d51-7771e9d9dcff"))
                .andExpect(jsonPath("$.counterparty.name").value("Заманбек Жетысай"))
                .andExpect(jsonPath("$.shipmentMoment").isEmpty())
                .andExpect(jsonPath("$.state").value("NEW"))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].good.id").value("7f316872-1da3-44c8-9293-0fddda859435"))
                .andExpect(jsonPath("$.items[0].good.name").value("Кикстартер L"))
                .andExpect(jsonPath("$.items[0].good.archived").value(false))
                .andExpect(jsonPath("$.items[0].price").value(800))
                .andExpect(jsonPath("$.items[0].quantity").value(200))
                .andExpect(jsonPath("$.items[0].remain").value(0))
                .andExpect(jsonPath("$.items[0].costPrice").value(0))
                .andExpect(jsonPath("$.items[1].good.id").value("bf6f2ba4-f994-44c1-839f-36a75f07242e"))
                .andExpect(jsonPath("$.items[1].good.name").value("Педаль переключения передач WY (короткий)"))
                .andExpect(jsonPath("$.items[1].good.archived").value(false))
                .andExpect(jsonPath("$.items[1].price").value(100))
                .andExpect(jsonPath("$.items[1].quantity").value(500))
                .andExpect(jsonPath("$.items[1].remain").value(0))
                .andExpect(jsonPath("$.items[1].costPrice").value(0))
                .andExpect(jsonPath("$.paidDocuments.length()").value(0));
    }

    @Test
    @DirtiesContext
    public void testEdit_success() throws Exception {
        String requestData = """
                {
                  "id": "9f26476e-e143-4468-8a37-abdb479e89b8",
                  "name": "SL-00101",
                  "organization": {
                    "id": "c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"
                  },
                  "moment": "2024-12-31T19:12:00",
                  "currency": "KZT",
                  "exchangeRate": 1,
                  "amount": 30000,
                  "warehouse": {
                    "id": "d1da1441-6598-4511-bc82-8fc06602e373"
                  },
                  "counterparty": {
                    "id": "43375a1e-1c91-46e5-9a10-a14200427fe9"
                  },
                  "comment": "я",
                  "state": "NEW",
                  "items": [
                    {
                      "good": {
                        "id": "7f316872-1da3-44c8-9293-0fddda859435"
                      },
                      "price": 700,
                      "quantity": 80
                    }
                  ]
                }
                """;
        mockMvc.perform(put(Api.Sale.PATH + "/9f26476e-e143-4468-8a37-abdb479e89b8")
                        .with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("9f26476e-e143-4468-8a37-abdb479e89b8"));
        mockMvc.perform(get(Api.Sale.PATH + "/9f26476e-e143-4468-8a37-abdb479e89b8").with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("9f26476e-e143-4468-8a37-abdb479e89b8"))
                .andExpect(jsonPath("$.name").value("SL-00101"))
                .andExpect(jsonPath("$.organization.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.organization.name").value("ИП Жырқын"))
                .andExpect(jsonPath("$.moment").value("2024-12-31T19:12:00"))
                .andExpect(jsonPath("$.currency").value("KZT"))
                .andExpect(jsonPath("$.exchangeRate").value(1))
                .andExpect(jsonPath("$.amount").value(30000))
                .andExpect(jsonPath("$.deleted").value(false))
                .andExpect(jsonPath("$.commited").value(false))
                .andExpect(jsonPath("$.comment").value("я"))
                .andExpect(jsonPath("$.warehouse.id").value("d1da1441-6598-4511-bc82-8fc06602e373"))
                .andExpect(jsonPath("$.warehouse.name").value("Барыс"))
                .andExpect(jsonPath("$.counterparty.id").value("43375a1e-1c91-46e5-9a10-a14200427fe9"))
                .andExpect(jsonPath("$.counterparty.name").value("Оркен Алматы"))
                .andExpect(jsonPath("$.shipmentMoment").isEmpty())
                .andExpect(jsonPath("$.state").value("NEW"))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].good.id").value("7f316872-1da3-44c8-9293-0fddda859435"))
                .andExpect(jsonPath("$.items[0].good.name").value("Кикстартер L"))
                .andExpect(jsonPath("$.items[0].good.archived").value(false))
                .andExpect(jsonPath("$.items[0].price").value(700))
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
        mockMvc.perform(put(Api.Sale.PATH + "/9f26476e-e143-4468-8a37-abdb479e89b8/commit")
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("9f26476e-e143-4468-8a37-abdb479e89b8"));
        mockMvc.perform(get(Api.Sale.PATH + "/9f26476e-e143-4468-8a37-abdb479e89b8").with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("9f26476e-e143-4468-8a37-abdb479e89b8"))
                .andExpect(jsonPath("$.name").value("SL-00001"))
                .andExpect(jsonPath("$.commited").value(true))
                .andExpect(jsonPath("$.items[0].good.id").value("7f316872-1da3-44c8-9293-0fddda859435"))
                .andExpect(jsonPath("$.items[0].remain").value(50))
                .andExpect(jsonPath("$.items[0].costPrice").value(513.29))
                .andExpect(jsonPath("$.items[1].good.id").value("bf6f2ba4-f994-44c1-839f-36a75f07242e"))
                .andExpect(jsonPath("$.items[1].remain").value(100))
                .andExpect(jsonPath("$.items[1].costPrice").value(331.15));
        mockMvc.perform(get(Api.Good.PATH + "/7f316872-1da3-44c8-9293-0fddda859435").with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock[0].warehouse.id").value("523961a7-696d-4779-8bb0-fd327feaecf3"))
                .andExpect(jsonPath("$.stock[0].remain").value(10))
                .andExpect(jsonPath("$.stock[0].costPrice").value(513.29));
        mockMvc.perform(get(Api.Good.PATH + "/bf6f2ba4-f994-44c1-839f-36a75f07242e").with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock[0].warehouse.id").value("523961a7-696d-4779-8bb0-fd327feaecf3"))
                .andExpect(jsonPath("$.stock[0].remain").value(50))
                .andExpect(jsonPath("$.stock[0].costPrice").value(331.15));
        mockMvc.perform(get(Api.Account.PATH).with(TestUtils.auth())
                        .queryParam("counterparty.id", "1d468c04-6360-43e5-9d51-7771e9d9dcff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.row.length()").value(1))
                .andExpect(jsonPath("$.row[0].organization.id").value("c6e5e4f9-93c0-40ea-91fa-e8a9bfffc515"))
                .andExpect(jsonPath("$.row[0].currency").value("KZT"))
                .andExpect(jsonPath("$.row[0].balance").value(-61500));
    }
}