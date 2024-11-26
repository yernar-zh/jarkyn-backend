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
        mockMvc.perform(get(Api.Customer.PATH + "/523961a7-696d-4779-8bb0-fd327feaecf3")
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("523961a7-696d-4779-8bb0-fd327feaecf3"))
                .andExpect(jsonPath("$.name").value("Кенжина"));
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
//                        .queryParam("search", "Зам")
//                        .queryParam("firstSale[min]", "2024-11-24T14:54")
//                        .queryParam("discount[max]", "5")
//                        .queryParam("sort", "-lastSale,name")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

    }

    @Test
    @DirtiesContext
    public void testCreate_success() throws Exception {
        String requestData = """
                {
                  "name": "Толе би"
                }""";
        MvcResult result = mockMvc.perform(post(Api.Customer.PATH).with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Толе би"))
                .andReturn();
        mockMvc.perform(get(Api.Customer.PATH + "/" + TestUtils.extractId(result)).with(TestUtils.auth())
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TestUtils.extractId(result)))
                .andExpect(jsonPath("$.name").value("Толе би"));
    }

    @Test
    @DirtiesContext
    public void testEdit_success() throws Exception {
        String requestData = """
                {
                  "name": "Кенжина 5"
                }""";
        mockMvc.perform(put(Api.Customer.PATH + "/523961a7-696d-4779-8bb0-fd327feaecf3")
                        .with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("523961a7-696d-4779-8bb0-fd327feaecf3"))
                .andExpect(jsonPath("$.name").value("Кенжина 5"));
        mockMvc.perform(get(Api.Customer.PATH + "/523961a7-696d-4779-8bb0-fd327feaecf3").with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("523961a7-696d-4779-8bb0-fd327feaecf3"))
                .andExpect(jsonPath("$.name").value("Кенжина 5"));
    }
}