package com.example.demo.web;

import com.example.demo.domain.Person;
import com.example.demo.service.GraphStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    private final GraphStorageService graphStorageService;

    public DemoController(GraphStorageService graphStorageService) {
        this.graphStorageService = graphStorageService;
    }

    @PostMapping("/connect")
    public ResponseEntity connectPerson(@RequestBody Person person) {
        graphStorageService.storePerson(person);

        return new ResponseEntity(HttpStatus.CREATED);
    }
}
