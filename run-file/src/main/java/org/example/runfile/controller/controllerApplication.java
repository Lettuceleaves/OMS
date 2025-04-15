package org.example.runfile.controller;

import feign.Param;
import org.example.runfile.service.dockerServiceInterface;
import org.example.runfile.service.serviceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.desktop.SystemSleepEvent;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
public class controllerApplication {

    @Autowired
    private serviceInterface service;

    @Autowired
    private dockerServiceInterface dockerService;

    @GetMapping("hello")
    String hello() throws InterruptedException {
        Thread.sleep(1000);
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

    @PostMapping("/runFile/singleTestNoInput")
    String singleTestNoInput(@RequestParam("language") String language, @RequestParam("inFile") MultipartFile inFile, @RequestParam("answer") MultipartFile ans) throws Exception {
        return dockerService.runSingleFileNoInput(language, inFile, ans, Paths.get(System.getProperty("user.dir"), "run-file", UUID.randomUUID().toString()), 1);
    }

    @GetMapping("/runFile/addNewImage/{language}")
    String addNewImage(@PathVariable("language") String language) throws Exception {
        dockerService.addNewImage(language);
        return "add new image success";
    }
}
