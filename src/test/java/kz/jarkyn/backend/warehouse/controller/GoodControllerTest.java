package kz.jarkyn.backend.warehouse.controller;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ComponentScan(basePackages = "kz.jarkyn.backend")
@Sql({"../../init.sql"})
class GoodControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DirtiesContext
    public void testDetail_success() throws Exception {
        mockMvc.perform(get(Api.Good.PATH + "/7f316872-1da3-44c8-9293-0fddda859435").with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("7f316872-1da3-44c8-9293-0fddda859435"))
                .andExpect(jsonPath("$.name").value("Кикстартер L"))
                .andExpect(jsonPath("$.group.id").value("cdfcf458-7cca-11ef-0a80-152f001b4886"))
                .andExpect(jsonPath("$.group.name").value("Кикстартер"))
                .andExpect(jsonPath("$.image").doesNotExist())
                .andExpect(jsonPath("$.minimumPrice").value(800))
                .andExpect(jsonPath("$.archived").value(false))
                .andExpect(jsonPath("$.attributes[0].id").value("e95420b5-3344-44ce-8d39-699f516ed715"))
                .andExpect(jsonPath("$.attributes[0].name").value("Мотоцикл GN"))
                .andExpect(jsonPath("$.archived").value(false))
                .andExpect(jsonPath("$.sellingPrices[0].id").value("fbe70a5c-a0d1-414c-9507-7352739b1243"))
                .andExpect(jsonPath("$.sellingPrices[0].quantity").value(1))
                .andExpect(jsonPath("$.sellingPrices[0].value").value(880))
                .andExpect(jsonPath("$.sellingPrices[1].id").value("de14fc67-d461-4094-a79f-ed32e2e50518"))
                .andExpect(jsonPath("$.sellingPrices[1].quantity").value(20))
                .andExpect(jsonPath("$.sellingPrices[1].value").value(850))
                .andExpect(jsonPath("$.stock[0].warehouse.id").value("523961a7-696d-4779-8bb0-fd327feaecf3"))
                .andExpect(jsonPath("$.stock[0].warehouse.name").value("Кенжина"))
                .andExpect(jsonPath("$.stock[0].remain").value(0))
                .andExpect(jsonPath("$.stock[0].costPrice").value(0))
                .andExpect(jsonPath("$.stock[1].warehouse.id").value("d1da1441-6598-4511-bc82-8fc06602e373"))
                .andExpect(jsonPath("$.stock[1].warehouse.name").value("Барыс"))
                .andExpect(jsonPath("$.stock[1].remain").value(0))
                .andExpect(jsonPath("$.stock[1].costPrice").value(0));
    }

    @Test
    @DirtiesContext
    public void testDetail_afterSupplyCommit() throws Exception {
        mockMvc.perform(put(Api.Supply.PATH + "/17c1285b-6514-45d5-88a2-3b9f673dc5e3/commit")
                        .with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("17c1285b-6514-45d5-88a2-3b9f673dc5e3"));
        mockMvc.perform(get(Api.Good.PATH + "/7f316872-1da3-44c8-9293-0fddda859435").with(TestUtils.auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("7f316872-1da3-44c8-9293-0fddda859435"))
                .andExpect(jsonPath("$.stock[0].warehouse.id").value("523961a7-696d-4779-8bb0-fd327feaecf3"))
                .andExpect(jsonPath("$.stock[0].warehouse.name").value("Кенжина"))
                .andExpect(jsonPath("$.stock[0].warehouse.id").value("523961a7-696d-4779-8bb0-fd327feaecf3"))
                .andExpect(jsonPath("$.stock[0].warehouse.name").value("Кенжина"))
                .andExpect(jsonPath("$.stock[0].remain").value(50))
                .andExpect(jsonPath("$.stock[0].costPrice").value(513.29))
                .andExpect(jsonPath("$.stock[1].warehouse.id").value("d1da1441-6598-4511-bc82-8fc06602e373"))
                .andExpect(jsonPath("$.stock[1].warehouse.name").value("Барыс"))
                .andExpect(jsonPath("$.stock[1].remain").value(0))
                .andExpect(jsonPath("$.stock[1].costPrice").value(0));
    }

    @Test
    @DirtiesContext
    public void testList_success() throws Exception {
        mockMvc.perform(get(Api.Good.PATH).with(TestUtils.auth())
                        .queryParam("search", "кикL")
                        .queryParam("group.id", "cdfcf458-7cca-11ef-0a80-152f001b4886")
                        .queryParam("attributeIds[like]", "e95420b5-3344-44ce-8d39-699f516ed715")
                        .queryParam("archived", "false")
                        .queryParam("sort", "-name")
                        .queryParam("page.first", "0")
                        .queryParam("page.size", "50")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.row[0].id").value("7f316872-1da3-44c8-9293-0fddda859435"))
                .andExpect(jsonPath("$.row[0].name").value("Кикстартер L"))
                .andExpect(jsonPath("$.row[0].group.id").value("cdfcf458-7cca-11ef-0a80-152f001b4886"))
                .andExpect(jsonPath("$.row[0].group.name").value("Кикстартер"))
                .andExpect(jsonPath("$.row[0].image").isEmpty())
                .andExpect(jsonPath("$.row[0].minimumPrice").value(800))
                .andExpect(jsonPath("$.row[0].archived").value(false))
                .andExpect(jsonPath("$.row[0].attributeIds").value("e95420b5-3344-44ce-8d39-699f516ed715"))
                .andExpect(jsonPath("$.row[0].sellingPrice").value(880))
                .andExpect(jsonPath("$.row[0].remain").value(0))
                .andExpect(jsonPath("$.sum.name").isEmpty())
                .andExpect(jsonPath("$.sum.id").isEmpty())
                .andExpect(jsonPath("$.sum.attributeIds").isEmpty())
                .andExpect(jsonPath("$.sum.minimumPrice").value(800))
                .andExpect(jsonPath("$.sum.image.id").isEmpty())
                .andExpect(jsonPath("$.sum.remain").value(0))
                .andExpect(jsonPath("$.sum.group.name").isEmpty())
                .andExpect(jsonPath("$.sum.group.id").isEmpty())
                .andExpect(jsonPath("$.sum.sellingPrice").value(880))
                .andExpect(jsonPath("$.sum.archived").isEmpty())
                .andExpect(jsonPath("$.page.first").value(0))
                .andExpect(jsonPath("$.page.size").value(50))
                .andExpect(jsonPath("$.page.totalCount").value(1));
    }

    @Test
    @DirtiesContext
    public void testCreate_success() throws Exception {
        String requestData = """
                {
                  "name": "Кикстартер S качественный",
                  "group": {"id": "cdfcf458-7cca-11ef-0a80-152f001b4886"},
                  "minimumPrice": 1000,
                  "attributes": [{"id": "355785a2-0dd8-49f8-987f-06e3c48bf9a8"}],
                  "sellingPrices": [
                    {"quantity": 1, "value": 920},
                    {"quantity": 30, "value": 899}
                  ],
                  "archived": false
                }""";
        mockMvc.perform(post(Api.Good.PATH).with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Кикстартер S качественный"))
                .andExpect(jsonPath("$.group.id").value("cdfcf458-7cca-11ef-0a80-152f001b4886"))
                .andExpect(jsonPath("$.group.name").value("Кикстартер"))
                .andExpect(jsonPath("$.image").doesNotExist())
                .andExpect(jsonPath("$.minimumPrice").value(1000))
                .andExpect(jsonPath("$.archived").value(false))
                .andExpect(jsonPath("$.attributes[0].id").value("355785a2-0dd8-49f8-987f-06e3c48bf9a8"))
                .andExpect(jsonPath("$.attributes[0].name").value("Мотоцикл WY"))
                .andExpect(jsonPath("$.sellingPrices[0].id").exists())
                .andExpect(jsonPath("$.sellingPrices[0].quantity").value(1))
                .andExpect(jsonPath("$.sellingPrices[0].value").value(920))
                .andExpect(jsonPath("$.sellingPrices[1].id").exists())
                .andExpect(jsonPath("$.sellingPrices[1].quantity").value(30))
                .andExpect(jsonPath("$.sellingPrices[1].value").value(899))
                .andExpect(jsonPath("$.stock[0].warehouse.id").value("523961a7-696d-4779-8bb0-fd327feaecf3"))
                .andExpect(jsonPath("$.stock[0].warehouse.name").value("Кенжина"))
                .andExpect(jsonPath("$.stock[0].remain").value(0))
                .andExpect(jsonPath("$.stock[0].costPrice").value(0))
                .andExpect(jsonPath("$.stock[1].warehouse.id").value("d1da1441-6598-4511-bc82-8fc06602e373"))
                .andExpect(jsonPath("$.stock[1].warehouse.name").value("Барыс"))
                .andExpect(jsonPath("$.stock[1].remain").value(0))
                .andExpect(jsonPath("$.stock[1].costPrice").value(0));
    }

    @Test
    @DirtiesContext
    public void testEdit_success() throws Exception {
        String requestData = """
                {
                  "name": "Кикстартер L new",
                  "group": {"id": "6120deea-5b87-11ee-0a80-000c0039b0fd"},
                  "minimumPrice": 1200,
                  "attributes": [
                    {"id": "355785a2-0dd8-49f8-987f-06e3c48bf9a8"},
                    {"id": "e95420b5-3344-44ce-8d39-699f516ed715"}
                  ],
                  "sellingPrices": [
                    {"id":"fbe70a5c-a0d1-414c-9507-7352739b1243", "quantity": 1, "value": 919},
                    {"quantity": 25, "value": 909}
                  ],
                  "archived": false
                }""";
        mockMvc.perform(put(Api.Good.PATH + "/7f316872-1da3-44c8-9293-0fddda859435")
                        .with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("7f316872-1da3-44c8-9293-0fddda859435"))
                .andExpect(jsonPath("$.name").value("Кикстартер L new"))
                .andExpect(jsonPath("$.group.id").value("6120deea-5b87-11ee-0a80-000c0039b0fd"))
                .andExpect(jsonPath("$.image").doesNotExist())
                .andExpect(jsonPath("$.minimumPrice").value(1200))
                .andExpect(jsonPath("$.archived").value(false))
                .andExpect(jsonPath("$.attributes[0].id").value("e95420b5-3344-44ce-8d39-699f516ed715"))
                .andExpect(jsonPath("$.attributes[1].id").value("355785a2-0dd8-49f8-987f-06e3c48bf9a8"))
                .andExpect(jsonPath("$.sellingPrices[0].id").value("fbe70a5c-a0d1-414c-9507-7352739b1243"))
                .andExpect(jsonPath("$.sellingPrices[0].quantity").value(1))
                .andExpect(jsonPath("$.sellingPrices[0].value").value(919))
                .andExpect(jsonPath("$.sellingPrices[1].id").exists())
                .andExpect(jsonPath("$.sellingPrices[1].quantity").value(25))
                .andExpect(jsonPath("$.sellingPrices[1].value").value(909));
    }

    @Test
    @DirtiesContext
    public void testArchive_success() throws Exception {
        String requestData = """
                {
                  "value": true
                }
                """;
        mockMvc.perform(put(Api.Good.PATH + "/7f316872-1da3-44c8-9293-0fddda859435/archive")
                        .with(TestUtils.auth()).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.archived").value(true));
    }
}