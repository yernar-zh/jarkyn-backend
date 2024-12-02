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
@Sql({"../../auth.sql", "counterparty.sql"})
class CustomerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DirtiesContext
    public void testDetail_success() throws Exception {
        mockMvc.perform(get(Api.Customer.PATH + "/1d468c04-6360-43e5-9d51-7771e9d9dcff")
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1d468c04-6360-43e5-9d51-7771e9d9dcff"))
                .andExpect(jsonPath("$.name").value("Заманбек Жетысай"))
                .andExpect(jsonPath("$.phoneNumber").value("+7(707)145-1475"))
                .andExpect(jsonPath("$.shippingAddress").value(""))
                .andExpect(jsonPath("$.discount").value(3));
    }

    @Test
    @DirtiesContext
    public void testDetail_notFound() throws Exception {
        mockMvc.perform(get(Api.Customer.PATH + "/a5747a2c-c97c-11ee-0a80-0777003791a7").with(TestUtils.auth()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    @Test
    @DirtiesContext
    public void testList_success() throws Exception {
        mockMvc.perform(get(Api.Customer.PATH).with(TestUtils.auth())
                                .queryParam("search", "Зам 145")
//                              .queryParam("firstSale[min]", "2024-11-24T14:54")
                                .queryParam("discount[min]", "2")
                                .queryParam("discount[max]", "4")
                                .queryParam("sort", "-discount")
                                .queryParam("page.first", "0")
                                .queryParam("page.size", "50")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.first").value(0))
                .andExpect(jsonPath("$.page.size").value(50))
                .andExpect(jsonPath("$.page.totalCount").value(1))
                .andExpect(jsonPath("$.row.length()").value(1))
                .andExpect(jsonPath("$.row[0].id").value("1d468c04-6360-43e5-9d51-7771e9d9dcff"))
                .andExpect(jsonPath("$.row[0].name").value("Заманбек Жетысай"))
                .andExpect(jsonPath("$.row[0].phoneNumber").value("+7(707)145-1475"))
                .andExpect(jsonPath("$.row[0].shippingAddress").value(""))
                .andExpect(jsonPath("$.row[0].discount").value(3))
                .andExpect(jsonPath("$.row[0].balance").value(0))
                .andExpect(jsonPath("$.row[0].firstSaleMoment").isEmpty())
                .andExpect(jsonPath("$.row[0].lastSaleMoment").isEmpty())
                .andExpect(jsonPath("$.row[0].totalSaleCount").value(0))
                .andExpect(jsonPath("$.row[0].totalSaleAmount").value(0));

    }

    @Test
    @DirtiesContext
    public void testCreate_success() throws Exception {
        String requestData = """
                {
                  "name": "Зейнел Зайсан",
                  "phoneNumber": "+7(702)564-3638",
                  "shippingAddress": "Рынок Салем",
                  "discount": 5
                }""";
        MvcResult result = mockMvc.perform(post(Api.Customer.PATH).with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();
        mockMvc.perform(get(Api.Customer.PATH).with(TestUtils.auth())
                                .queryParam("id", TestUtils.extractId(result))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.row.length()").value(1))
                .andExpect(jsonPath("$.row[0].id").value(TestUtils.extractId(result)))
                .andExpect(jsonPath("$.row[0].name").value("Зейнел Зайсан"))
                .andExpect(jsonPath("$.row[0].phoneNumber").value("+7(702)564-3638"))
                .andExpect(jsonPath("$.row[0].shippingAddress").value("Рынок Салем"))
                .andExpect(jsonPath("$.row[0].discount").value(5))
                .andExpect(jsonPath("$.row[0].balance").value(0))
                .andExpect(jsonPath("$.row[0].firstSaleMoment").isEmpty())
                .andExpect(jsonPath("$.row[0].lastSaleMoment").isEmpty())
                .andExpect(jsonPath("$.row[0].totalSaleCount").value(0))
                .andExpect(jsonPath("$.row[0].totalSaleAmount").value(0));
    }

    @Test
    @DirtiesContext
    public void testEdit_success() throws Exception {
        String requestData = """
                {
                  "name": "Заманбек Жетысай 2",
                  "phoneNumber": "+7(707)145-1476",
                  "shippingAddress": "Рынок Салем",
                  "discount": 5
                }""";
        mockMvc.perform(put(Api.Customer.PATH + "/1d468c04-6360-43e5-9d51-7771e9d9dcff")
                        .with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1d468c04-6360-43e5-9d51-7771e9d9dcff"));
        mockMvc.perform(get(Api.Customer.PATH + "/1d468c04-6360-43e5-9d51-7771e9d9dcff").with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1d468c04-6360-43e5-9d51-7771e9d9dcff"))
                .andExpect(jsonPath("$.name").value("Заманбек Жетысай 2"))
                .andExpect(jsonPath("$.phoneNumber").value("+7(707)145-1476"))
                .andExpect(jsonPath("$.shippingAddress").value("Рынок Салем"))
                .andExpect(jsonPath("$.discount").value(5));
    }
}