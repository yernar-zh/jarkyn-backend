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
                .andExpect(jsonPath("$.currency").value("CNY"))
                .andExpect(jsonPath("$.exchangeRate").value(68))
                .andExpect(jsonPath("$.amount").value(710))
                .andExpect(jsonPath("$.deleted").value(false))
                .andExpect(jsonPath("$.warehouse.id").value("523961a7-696d-4779-8bb0-fd327feaecf3"))
                .andExpect(jsonPath("$.warehouse.name").value("Кенжина"))
                .andExpect(jsonPath("$.counterparty.id").value("94fadc9a-83bb-4639-be07-f825ab9eb40e"))
                .andExpect(jsonPath("$.counterparty.name").value("Урумчи Кытай"))
                .andExpect(jsonPath("$.comment").value(""))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].id").value("0098b2bc-da73-4451-bdb5-35f42f756f10"))
                .andExpect(jsonPath("$.items[0].good.id").value("7f316872-1da3-44c8-9293-0fddda859435"))
                .andExpect(jsonPath("$.items[0].good.name").value("Кикстартер L"))
                .andExpect(jsonPath("$.items[0].good.archived").value(false))
                .andExpect(jsonPath("$.items[0].price").value(6.2))
                .andExpect(jsonPath("$.items[0].quantity").value(50))
                .andExpect(jsonPath("$.items[0].remain").value(0))
                .andExpect(jsonPath("$.items[0].costPrice").value(0))
                .andExpect(jsonPath("$.items[1].id").value("986378a0-271f-4293-96db-218f608599cd"))
                .andExpect(jsonPath("$.items[1].good.id").value("bf6f2ba4-f994-44c1-839f-36a75f07242e"))
                .andExpect(jsonPath("$.items[1].good.name").value("Педаль переключения передач WY (короткий)"))
                .andExpect(jsonPath("$.items[1].good.archived").value(false))
                .andExpect(jsonPath("$.items[1].price").value(4))
                .andExpect(jsonPath("$.items[1].quantity").value(100))
                .andExpect(jsonPath("$.items[1].remain").value(0))
                .andExpect(jsonPath("$.items[1].costPrice").value(0))
                .andExpect(jsonPath("$.outPaidDocuments.length()").value(2))
                .andExpect(jsonPath("$.outPaidDocuments[0].id").value("538c3271-7398-4fab-ad05-0a886188de11"))
                .andExpect(jsonPath("$.outPaidDocuments[0].payment.id").value("5c799431-3bc3-400f-b9a3-209f27b935a0"))
                .andExpect(jsonPath("$.outPaidDocuments[0].payment.name").value("PO-00001"))
                .andExpect(jsonPath("$.outPaidDocuments[0].payment.moment").value("2024-12-07T22:47:00"))
                .andExpect(jsonPath("$.outPaidDocuments[0].payment.currency").value("CNY"))
                .andExpect(jsonPath("$.outPaidDocuments[0].payment.exchangeRate").value(68))
                .andExpect(jsonPath("$.outPaidDocuments[0].payment.amount").value(710))
                .andExpect(jsonPath("$.outPaidDocuments[0].payment.deleted").value(false))
                .andExpect(jsonPath("$.outPaidDocuments[0].amount").value(710))
                .andExpect(jsonPath("$.outPaidDocuments[1].id").value("e70efb3f-9124-4ef9-9b7e-7bc24385710f"))
                .andExpect(jsonPath("$.outPaidDocuments[1].payment.id").value("fa81596d-a236-4256-8686-7f7f3be85ae4"))
                .andExpect(jsonPath("$.outPaidDocuments[1].payment.name").value("PO-00002"))
                .andExpect(jsonPath("$.outPaidDocuments[1].payment.moment").value("2024-12-07T23:47:00"))
                .andExpect(jsonPath("$.outPaidDocuments[1].payment.currency").value("USD"))
                .andExpect(jsonPath("$.outPaidDocuments[1].payment.exchangeRate").value(525))
                .andExpect(jsonPath("$.outPaidDocuments[1].payment.amount").value(20))
                .andExpect(jsonPath("$.outPaidDocuments[1].payment.deleted").value(false))
                .andExpect(jsonPath("$.outPaidDocuments[1].amount").value(20));
    }
}