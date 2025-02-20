package kz.jarkyn.backend.reference.controller;

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
class CurrencyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DirtiesContext
    public void testDetail_success() throws Exception {
        mockMvc.perform(get(Api.Currency.PATH + "/559109ea-f824-476d-8fa4-9990e53880ff")
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("559109ea-f824-476d-8fa4-9990e53880ff"))
                .andExpect(jsonPath("$.name").value("Тенге"))
                .andExpect(jsonPath("$.code").value("KZT"))
                .andExpect(jsonPath("$.symbol").value("₸"));
    }

    @Test
    @DirtiesContext
    public void testDetail_notFound() throws Exception {
        mockMvc.perform(get(Api.Currency.PATH + "/a5747a2c-c97c-11ee-0a80-0777003791a7").with(TestUtils.auth()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    @Test
    @DirtiesContext
    public void testList_success() throws Exception {
        mockMvc.perform(get(Api.Currency.PATH).with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.row[0].id").value("559109ea-f824-476d-8fa4-9990e53880ff"))
                .andExpect(jsonPath("$.row[0].name").value("Тенге"))
                .andExpect(jsonPath("$.row[0].code").value("KZT"))
                .andExpect(jsonPath("$.row[0].symbol").value("₸"))
                .andExpect(jsonPath("$.row[1].id").value("e6a3c207-a358-47bf-ac18-2d09973f3807"))
                .andExpect(jsonPath("$.row[1].name").value("Юань"))
                .andExpect(jsonPath("$.row[1].code").value("CNY"))
                .andExpect(jsonPath("$.row[1].symbol").value("¥"))
                .andExpect(jsonPath("$.row[2].id").value("24f15639-67da-4df4-aab4-56b85c872c3b"))
                .andExpect(jsonPath("$.row[2].name").value("Доллар"))
                .andExpect(jsonPath("$.row[2].code").value("USD"))
                .andExpect(jsonPath("$.row[2].symbol").value("$"));

    }
}