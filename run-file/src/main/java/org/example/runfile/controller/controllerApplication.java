package org.example.runfile.controller;

import org.example.runfile.service.serviceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class controllerApplication {

    @Autowired
    private serviceInterface service;

    @GetMapping("hello")
    String hello() {
        return "Hello World!";
    }

    @GetMapping("/runFile/{cFile}/{inFile}")
    String runFile(@PathVariable("cFile") String cFile, @PathVariable("inFile") String inFile) throws IOException {
        // Logic to get user info by id
        return service.testHelloWorld(cFile, inFile);
    }
}
