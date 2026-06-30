package com.noizy.adapters.input.web

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `register login and me endpoints work`() {
        val registerBody = """
            {
              "name": "Integration Listener",
              "email": "integration-listener@noizy.local",
              "password": "password123"
            }
        """.trimIndent()

        val registerResult = mockMvc.post("/api/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = registerBody
        }.andExpect {
            status { isOk() }
            jsonPath("$.token") { exists() }
            jsonPath("$.user.email") { value("integration-listener@noizy.local") }
            jsonPath("$.user.role") { value("FREE_TIER") }
        }.andReturn()

        val token = objectMapper.readTree(registerResult.response.contentAsString).get("token").asText()

        mockMvc.get("/api/auth/me") {
            header("Authorization", "Bearer $token")
        }.andExpect {
            status { isOk() }
            jsonPath("$.email") { value("integration-listener@noizy.local") }
        }
    }

    companion object {
        @Container
        private val postgres = PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("noizy")
            .withUsername("noizy")
            .withPassword("noizy")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("noizy.aws.sqs.track-events-queue-url") { "" }
            registry.add("noizy.aws.sns.topic-arn") { "" }
        }
    }
}
