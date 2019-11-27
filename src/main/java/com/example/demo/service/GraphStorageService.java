package com.example.demo.service;

import com.example.demo.domain.Person;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GraphStorageService {

    private final Driver driver;

    public GraphStorageService(Driver driver) {
        this.driver = driver;
    }

    public void storePerson(Person person) {
        String query = "MERGE (p:Person {email: $email}) SET p.firstName = $firstName, p.lastName = $lastName";
        try (Session session = driver.session()) {
            session.writeTransaction((tx) -> {
                Map<String, Object> queryParams = new HashMap<>();
                queryParams.put("email", person.getEmail());
                queryParams.put("firstName", person.getFirstName());
                queryParams.put("lastName", person.getLastName());
                tx.run(query, queryParams);

                return null;
            });
        }
    }
}
