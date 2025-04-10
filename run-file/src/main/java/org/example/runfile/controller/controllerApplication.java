package org.example.runfile.controller;

import feign.Param;
import org.example.runfile.service.dockerServiceInterface;
import org.example.runfile.service.serviceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class controllerApplication {

    @Autowired
    private serviceInterface service;

    @Autowired
    private dockerServiceInterface dockerService;

    @GetMapping("hello")
    String hello() {
        return "Hello World!";
    }

    @GetMapping("/runFile/{cFile}/{inFile}")
    String runFile(@PathVariable("cFile") String cFile, @PathVariable("inFile") String inFile) throws IOException {
        // Logic to get user info by id
        return service.testHelloWorld(cFile, inFile);
    }

//    @GetMapping("/runFile/singleTest")
//    String singleTest(@RequestParam("testFile") MultipartFile testFile,
//                      @RequestParam("inFile") MultipartFile inFile
//                      @RequestParam("outFile")) throws Exception {
//        // Logic to get user info by name
//        String result = service.singleTest(testFile, inFile);
//        if (result.equals("Command executed successfully")) {
//
//        } else {
//            return "Command execution failed";
//        }
//    }
    @GetMapping("/runFile/singleTest")
    String singleTest(@RequestParam("testFile") MultipartFile testFile,
                      @RequestParam("inFile") MultipartFile inFile) throws Exception {
        return service.singleTest(testFile, inFile);
    }

    @GetMapping("/runFile/singleTestNoInput")
    String singleTestNoInput(@RequestParam("language") String language, @RequestParam("inFile") MultipartFile inFile) throws Exception {
        return dockerService.runSingleFileNoInput(language, inFile);
    }
}
