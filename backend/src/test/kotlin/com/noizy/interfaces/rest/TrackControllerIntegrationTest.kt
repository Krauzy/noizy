package com.noizy.interfaces.rest

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TrackControllerIntegrationTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `tracks can be listed and searched from seed data`() {
        mockMvc.get("/api/tracks?size=10").andExpect {
            status { isOk() }
            jsonPath("$.content[0].title") { exists() }
        }

        mockMvc.get("/api/tracks/search?query=Dawn&size=10").andExpect {
            status { isOk() }
            jsonPath("$.content[0].title") { value("Dawn Circuit") }
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
