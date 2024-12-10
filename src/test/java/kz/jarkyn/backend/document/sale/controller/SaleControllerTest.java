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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ComponentScan(basePackages = "kz.jarkyn.backend")
@Sql({"../../../inti.sql"})
class SaleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DirtiesContext
    public void testDetail_success() throws Exception {
        mockMvc.perform(get(Api.Sale.PATH + "/9d56c02e-81e5-47a6-ab0a-fbeca21293af")
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("9d56c02e-81e5-47a6-ab0a-fbeca21293af"))
                .andExpect(jsonPath("$.name").value("SL-00001"))
                .andExpect(jsonPath("$.moment").value("2024-12-03T17:28:00"))
                .andExpect(jsonPath("$.amount").value(63800))
                .andExpect(jsonPath("$.warehouse.id").value("523961a7-696d-4779-8bb0-fd327feaecf3"))
                .andExpect(jsonPath("$.warehouse.name").value("Кенжина"))
                .andExpect(jsonPath("$.counterparty.id").value("1d468c04-6360-43e5-9d51-7771e9d9dcff"))
                .andExpect(jsonPath("$.counterparty.name").value("Заманбек Жетысай"))
                .andExpect(jsonPath("$.comment").value(""))
                .andExpect(jsonPath("$.shipmentMoment").isEmpty())
                .andExpect(jsonPath("$.items[0].id").value("2c7f1951-4ddb-4125-a52b-2183e013e65f"))
                .andExpect(jsonPath("$.items[0].good.id").value("7f316872-1da3-44c8-9293-0fddda859435"))
                .andExpect(jsonPath("$.items[0].good.name").value("Кикстартер L"))
                .andExpect(jsonPath("$.items[0].good.archived").value(false))
                .andExpect(jsonPath("$.items[0].price").value(880))
                .andExpect(jsonPath("$.items[0].quantity").value(10))
                .andExpect(jsonPath("$.items[0].remain").value(0))
                .andExpect(jsonPath("$.items[0].costPrice").value(0))
                .andExpect(jsonPath("$.items[1].id").value("49dfee7e-1683-4a53-92f2-bd65be5b045f"))
                .andExpect(jsonPath("$.items[1].good.id").value("bf6f2ba4-f994-44c1-839f-36a75f07242e"))
                .andExpect(jsonPath("$.items[1].good.name").value("Педаль переключения передач WY (короткий)"))
                .andExpect(jsonPath("$.items[1].good.archived").value(false))
                .andExpect(jsonPath("$.items[1].price").value(550))
                .andExpect(jsonPath("$.items[1].quantity").value(100))
                .andExpect(jsonPath("$.items[1].remain").value(0))
                .andExpect(jsonPath("$.items[1].costPrice").value(0))
                .andExpect(jsonPath("$.payments").isEmpty());
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
    public void testCreate_success() throws Exception {
        String requestData = """
                {
                  "moment": "2024-12-05T00:10:00",
                  "amount": 4800,
                  "warehouse": {
                    "id": "523961a7-696d-4779-8bb0-fd327feaecf3"
                  },
                  "counterparty": {
                    "id": "1d468c04-6360-43e5-9d51-7771e9d9dcff"
                  },
                  "comment": "",
                  "shipmentMoment": null,
                  "items": [
                    {
                      "good": {
                        "id": "7f316872-1da3-44c8-9293-0fddda859435"
                      },
                      "price": 900,
                      "quantity": 2
                    },
                    {
                      "good": {
                        "id": "bf6f2ba4-f994-44c1-839f-36a75f07242e"
                      },
                      "price": 600,
                      "quantity": 5
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
                .andExpect(jsonPath("$.moment").value("2024-12-05T00:10:00"))
                .andExpect(jsonPath("$.amount").value(4800))
                .andExpect(jsonPath("$.warehouse.id").value("523961a7-696d-4779-8bb0-fd327feaecf3"))
                .andExpect(jsonPath("$.warehouse.name").value("Кенжина"))
                .andExpect(jsonPath("$.counterparty.id").value("1d468c04-6360-43e5-9d51-7771e9d9dcff"))
                .andExpect(jsonPath("$.counterparty.name").value("Заманбек Жетысай"))
                .andExpect(jsonPath("$.comment").value(""))
                .andExpect(jsonPath("$.shipmentMoment").isEmpty())
                .andExpect(jsonPath("$.items[0].good.id").value("7f316872-1da3-44c8-9293-0fddda859435"))
                .andExpect(jsonPath("$.items[0].good.name").value("Кикстартер L"))
                .andExpect(jsonPath("$.items[0].good.archived").value(false))
                .andExpect(jsonPath("$.items[0].price").value(900))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].remain").value(0))
                .andExpect(jsonPath("$.items[0].costPrice").value(0))
                .andExpect(jsonPath("$.items[1].good.id").value("bf6f2ba4-f994-44c1-839f-36a75f07242e"))
                .andExpect(jsonPath("$.items[1].good.name").value("Педаль переключения передач WY (короткий)"))
                .andExpect(jsonPath("$.items[1].good.archived").value(false))
                .andExpect(jsonPath("$.items[1].price").value(600))
                .andExpect(jsonPath("$.items[1].quantity").value(5))
                .andExpect(jsonPath("$.items[1].remain").value(0))
                .andExpect(jsonPath("$.items[1].costPrice").value(0))
                .andExpect(jsonPath("$.payments").isEmpty());
    }

    @Test
    @DirtiesContext
    public void testEdit_success() throws Exception {
        String requestData = """
                {
                  "name": "SL-0101",
                  "moment": "2024-12-05T22:50:00",
                  "amount": 53000,
                  "warehouse": {
                    "id": "d1da1441-6598-4511-bc82-8fc06602e373"
                  },
                  "counterparty": {
                    "id": "43375a1e-1c91-46e5-9a10-a14200427fe9"
                  },
                  "comment": "comment",
                  "shipmentMoment": "2024-12-05T22:57:00",
                  "items": [
                    {
                      "good": {
                        "id": "7f316872-1da3-44c8-9293-0fddda859435"
                      },
                      "price": 800,
                      "quantity": 20
                    },
                    {
                      "id": "49dfee7e-1683-4a53-92f2-bd65be5b045f",
                      "good": {
                        "id": "bf6f2ba4-f994-44c1-839f-36a75f07242e"
                      },
                      "price": 600,
                      "quantity": 50
                    },
                    {
                      "good": {
                        "id": "bf6f2ba4-f994-44c1-839f-36a75f07242e"
                      },
                      "price": 700,
                      "quantity": 10
                    }
                  ]
                }""";
        mockMvc.perform(put(Api.Sale.PATH + "/9d56c02e-81e5-47a6-ab0a-fbeca21293af")
                        .with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("9d56c02e-81e5-47a6-ab0a-fbeca21293af"));
        mockMvc.perform(get(Api.Sale.PATH + "/9d56c02e-81e5-47a6-ab0a-fbeca21293af").with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("9d56c02e-81e5-47a6-ab0a-fbeca21293af"))
                .andExpect(jsonPath("$.name").value("SL-0101"))
                .andExpect(jsonPath("$.moment").value("2024-12-05T22:50:00"))
                .andExpect(jsonPath("$.amount").value(53000))
                .andExpect(jsonPath("$.warehouse.id").value("d1da1441-6598-4511-bc82-8fc06602e373"))
                .andExpect(jsonPath("$.warehouse.name").value("Барыс"))
                .andExpect(jsonPath("$.counterparty.id").value("43375a1e-1c91-46e5-9a10-a14200427fe9"))
                .andExpect(jsonPath("$.counterparty.name").value("Оркен Алматы"))
                .andExpect(jsonPath("$.comment").value("comment"))
                .andExpect(jsonPath("$.shipmentMoment").value("2024-12-05T22:57:00"))
                .andExpect(jsonPath("$.items.length()").value(3))
                .andExpect(jsonPath("$.items[0].good.id").value("7f316872-1da3-44c8-9293-0fddda859435"))
                .andExpect(jsonPath("$.items[0].good.name").value("Кикстартер L"))
                .andExpect(jsonPath("$.items[0].good.archived").value(false))
                .andExpect(jsonPath("$.items[0].price").value(800))
                .andExpect(jsonPath("$.items[0].quantity").value(20))
                .andExpect(jsonPath("$.items[0].remain").value(0))
                .andExpect(jsonPath("$.items[0].costPrice").value(0))
                .andExpect(jsonPath("$.items[1].id").value("49dfee7e-1683-4a53-92f2-bd65be5b045f"))
                .andExpect(jsonPath("$.items[1].good.id").value("bf6f2ba4-f994-44c1-839f-36a75f07242e"))
                .andExpect(jsonPath("$.items[1].good.name").value("Педаль переключения передач WY (короткий)"))
                .andExpect(jsonPath("$.items[1].good.archived").value(false))
                .andExpect(jsonPath("$.items[1].price").value(600))
                .andExpect(jsonPath("$.items[1].quantity").value(50))
                .andExpect(jsonPath("$.items[1].remain").value(0))
                .andExpect(jsonPath("$.items[1].costPrice").value(0))
                .andExpect(jsonPath("$.items[2].good.id").value("bf6f2ba4-f994-44c1-839f-36a75f07242e"))
                .andExpect(jsonPath("$.items[2].good.name").value("Педаль переключения передач WY (короткий)"))
                .andExpect(jsonPath("$.items[2].good.archived").value(false))
                .andExpect(jsonPath("$.items[2].price").value(700))
                .andExpect(jsonPath("$.items[2].quantity").value(10))
                .andExpect(jsonPath("$.items[2].remain").value(0))
                .andExpect(jsonPath("$.items[2].costPrice").value(0));
    }
}