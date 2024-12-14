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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ComponentScan(basePackages = "kz.jarkyn.backend")
@Sql({"../../../inti.sql"})
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
                .andExpect(jsonPath("$.moment").value("2024-12-07T21:47:00"))
                .andExpect(jsonPath("$.amount").value(21060))
                .andExpect(jsonPath("$.warehouse.id").value("523961a7-696d-4779-8bb0-fd327feaecf3"))
                .andExpect(jsonPath("$.warehouse.name").value("Кенжина"))
                .andExpect(jsonPath("$.counterparty.id").value("94fadc9a-83bb-4639-be07-f825ab9eb40e"))
                .andExpect(jsonPath("$.counterparty.name").value("Урумчи Кытай"))
                .andExpect(jsonPath("$.comment").value(""))
                .andExpect(jsonPath("$.shipmentMoment").isEmpty())
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].good.id").value("7f316872-1da3-44c8-9293-0fddda859435"))
                .andExpect(jsonPath("$.items[0].good.name").value("Кикстартер L"))
                .andExpect(jsonPath("$.items[0].good.archived").value(false))
                .andExpect(jsonPath("$.items[0].price").value(201.2))
                .andExpect(jsonPath("$.items[0].quantity").value(50))
                .andExpect(jsonPath("$.items[0].remain").value(0))
                .andExpect(jsonPath("$.items[0].costPrice").value(0))
                .andExpect(jsonPath("$.items[1].good.id").value("bf6f2ba4-f994-44c1-839f-36a75f07242e"))
                .andExpect(jsonPath("$.items[1].good.name").value("Педаль переключения передач WY (короткий)"))
                .andExpect(jsonPath("$.items[1].good.archived").value(false))
                .andExpect(jsonPath("$.items[1].price").value(220))
                .andExpect(jsonPath("$.items[1].quantity").value(50))
                .andExpect(jsonPath("$.items[1].remain").value(0))
                .andExpect(jsonPath("$.items[1].costPrice").value(0))
                .andExpect(jsonPath("$.payments.length()").value(0));
    }
}