package com.example.demo;

import com.example.demo.domain.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.StatementResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.Neo4jContainer;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = {DemoIntegrationTest.Initializer.class})
public class DemoIntegrationTest {

    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    private Driver driver;

    private static final ObjectMapper OM = new ObjectMapper();
    private MockMvc mockMvc;
    private static Neo4jContainer neo4jContainer = new Neo4jContainer().withoutAuthentication();

    @BeforeClass
    public static void setUpClass() {
        neo4jContainer.start();
    }

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .build();
    }

    @Test
    public void testAddingNewUserFromRequest() throws Exception {
        Person person = new Person("demo@example.com", "John", "Doe");
        mockMvc.perform(post("/connect")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(OM.writeValueAsString(person)))
                .andExpect(status().isCreated());

        try (Session session = driver.session()) {
            StatementResult result = session.run("MATCH (n:Person) RETURN count(n) AS count");
            assertEquals(1, result.single().get("count").asLong(), 0L);
        }
    }



    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {

            TestPropertyValues.of(
                    "org.neo4j.driver.uri=" + neo4jContainer.getBoltUrl(),
                    "org.neo4j.driver.authentication.password="
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

}
