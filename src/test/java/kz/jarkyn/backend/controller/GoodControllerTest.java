package kz.jarkyn.backend.controller;

import kz.jarkyn.backend.config.Api;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
@Sql({"good.sql"})
class GoodControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DirtiesContext
    public void testDetail_success() throws Exception {
        mockMvc.perform(get(Api.Good.PATH + "/7f316872-1da3-44c8-9293-0fddda859435"))
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
                .andExpect(jsonPath("$.sellingPrices[1].value").value(850));
    }

    @Test
    @DirtiesContext
    public void testList_success() throws Exception {
        mockMvc.perform(get(Api.Good.PATH)
                        .param("search", "кик")
                        .param("groupId", "cdfcf458-7cca-11ef-0a80-152f001b4886")
                        .param("attributeId", "e95420b5-3344-44ce-8d39-699f516ed715")
                        .param("archived", Boolean.FALSE.toString())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
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
                  ]
                }""";
        mockMvc.perform(post(Api.Good.PATH)
                        .contentType(MediaType.APPLICATION_JSON).content(requestData))
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
                .andExpect(jsonPath("$.sellingPrices[1].value").value(899));
    }

    @Test
    @DirtiesContext
    public void testEdit_success() throws Exception {
        String requestData = """
                {
                  "name": "Кикстартер L new",
                  "group": {"id": "faeb45ef-cf5a-45e1-af2e-6e875a2d6331"},
                  "minimumPrice": 1200,
                  "attributes": [
                    {"id": "355785a2-0dd8-49f8-987f-06e3c48bf9a8"},
                    {"id": "e95420b5-3344-44ce-8d39-699f516ed715"}
                  ],
                  "sellingPrices": [
                    {"id":"fbe70a5c-a0d1-414c-9507-7352739b1243", "quantity": 1, "value": 919},
                    {"quantity": 25, "value": 909}
                  ]
                }""";
        mockMvc.perform(put(Api.Good.PATH + "/7f316872-1da3-44c8-9293-0fddda859435")
                        .contentType(MediaType.APPLICATION_JSON).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("7f316872-1da3-44c8-9293-0fddda859435"))
                .andExpect(jsonPath("$.name").value("Кикстартер L new"))
                .andExpect(jsonPath("$.group.id").value("faeb45ef-cf5a-45e1-af2e-6e875a2d6331"))
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
                        .contentType(MediaType.APPLICATION_JSON).content(requestData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.archived").value(true));
    }
}