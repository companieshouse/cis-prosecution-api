package uk.gov.companieshouse.cis_prosecution_api.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = HealthcheckController.class)
class HealthcheckControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /cis-cppi-api/healthcheck - Success")
    void healthcheckEndpointReturnsOk() throws Exception {
        mockMvc.perform(get("/healthcheck"))
                .andExpect(status().isOk());
    }
}
