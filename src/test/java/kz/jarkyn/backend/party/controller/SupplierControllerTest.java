package kz.jarkyn.backend.party.controller;

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
class SupplierControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DirtiesContext
    public void testDetail_success() throws Exception {
        mockMvc.perform(get(Api.Supplier.PATH + "/94fadc9a-83bb-4639-be07-f825ab9eb40e")
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("94fadc9a-83bb-4639-be07-f825ab9eb40e"))
                .andExpect(jsonPath("$.name").value("Урумчи Кытай"))
                .andExpect(jsonPath("$.archived").value(false));
    }

    @Test
    @DirtiesContext
    public void testDetail_notFound() throws Exception {
        mockMvc.perform(get(Api.Supplier.PATH + "/a5747a2c-c97c-11ee-0a80-0777003791a7").with(TestUtils.auth()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    @Test
    @DirtiesContext
    public void testList_success() throws Exception {
        mockMvc.perform(get(Api.Supplier.PATH).with(TestUtils.auth())
                        .queryParam("search", "ур")
                        .queryParam("sort", "-name")
                        .queryParam("page.first", "0")
                        .queryParam("page.size", "50")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.first").value(0))
                .andExpect(jsonPath("$.page.size").value(50))
                .andExpect(jsonPath("$.page.totalCount").value(1))
                .andExpect(jsonPath("$.row.length()").value(1))
                .andExpect(jsonPath("$.row[0].id").value("94fadc9a-83bb-4639-be07-f825ab9eb40e"))
                .andExpect(jsonPath("$.row[0].name").value("Урумчи Кытай"))
                .andExpect(jsonPath("$.row[0].accountBalance").value(0))
                .andExpect(jsonPath("$.row[0].accountCurrency.id").value("559109ea-f824-476d-8fa4-9990e53880ff"))
                .andExpect(jsonPath("$.row[0].accountCurrency.name").value("KZT"))
                .andExpect(jsonPath("$.row[0].firstSupplyMoment").isEmpty())
                .andExpect(jsonPath("$.row[0].lastSupplyMoment").isEmpty())
                .andExpect(jsonPath("$.row[0].totalSupplyCount").value(0))
                .andExpect(jsonPath("$.row[0].totalSupplyAmount").value(0))
                .andExpect(jsonPath("$.row[0].archived").value(false));

    }

    @Test
    @DirtiesContext
    public void testCreate_success() throws Exception {
        String requestData = """
                {
                  "name": "Шанхай",
                  "archived": false
                }""";
        MvcResult result = mockMvc.perform(post(Api.Supplier.PATH).with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();
        mockMvc.perform(get(Api.Supplier.PATH).with(TestUtils.auth())
                        .queryParam("id", TestUtils.extractId(result))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.row.length()").value(1))
                .andExpect(jsonPath("$.row[0].id").value(TestUtils.extractId(result)))
                .andExpect(jsonPath("$.row[0].name").value("Шанхай"))
                .andExpect(jsonPath("$.row[0].accountBalance").value(0))
                .andExpect(jsonPath("$.row[0].accountCurrency.id").value("559109ea-f824-476d-8fa4-9990e53880ff"))
                .andExpect(jsonPath("$.row[0].accountCurrency.name").value("KZT"))
                .andExpect(jsonPath("$.row[0].firstSupplyMoment").isEmpty())
                .andExpect(jsonPath("$.row[0].lastSupplyMoment").isEmpty())
                .andExpect(jsonPath("$.row[0].totalSupplyCount").value(0))
                .andExpect(jsonPath("$.row[0].totalSupplyAmount").value(0))
                .andExpect(jsonPath("$.row[0].archived").value(false));
    }

    @Test
    @DirtiesContext
    public void testEdit_success() throws Exception {
        String requestData = """
                {
                  "name": "Урумчи Кытай 2",
                  "archived": true
                }""";
        mockMvc.perform(put(Api.Supplier.PATH + "/94fadc9a-83bb-4639-be07-f825ab9eb40e")
                        .with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("94fadc9a-83bb-4639-be07-f825ab9eb40e"));
        mockMvc.perform(get(Api.Supplier.PATH + "/94fadc9a-83bb-4639-be07-f825ab9eb40e").with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("94fadc9a-83bb-4639-be07-f825ab9eb40e"))
                .andExpect(jsonPath("$.name").value("Урумчи Кытай 2"))
                .andExpect(jsonPath("$.archived").value(true));
    }
}