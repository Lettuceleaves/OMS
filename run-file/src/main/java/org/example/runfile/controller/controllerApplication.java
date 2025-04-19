package org.example.runfile.controller;

import org.example.runfile.service.dockerServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
public class controllerApplication {

    @Autowired
    private dockerServiceInterface dockerService;

    @GetMapping("hello")
    String hello() throws InterruptedException {
        Thread.sleep(1000);
        return "Hello World!";
    }

    @PostMapping("/runFile/singleTestNoInput")
    String singleTestNoInput(@RequestParam("language") String language, @RequestParam("inFile") MultipartFile inFile, @RequestParam("title") String title) throws Exception {
        return dockerService.runSingleFileNoInput(language, inFile, Paths.get(System.getProperty("user.dir"), "run-file", "runTimeDir", UUID.randomUUID().toString()), title, true, false);
    }

    @GetMapping("/runFile/addNewImage/{language}")
    String addNewImage(@PathVariable("language") String language) throws Exception {
        dockerService.addNewImage(language);
        return "add new image success";
    }

    @GetMapping("/runFile/newTitle/standardCode")
    String newTitle(@RequestParam("title") String title,@RequestParam("standardCode") MultipartFile standardCode) throws IOException {
        return dockerService.newTitle(title, standardCode);
    }

    @GetMapping("/runFile/manuallyAddCheckFiles")
    String manuallyAddCheckFiles(@RequestParam("title") String title, @RequestParam("in") MultipartFile in, @RequestParam("ans") MultipartFile ans) throws IOException {
        return dockerService.manuallyAddCheckFiles(title, in, ans);
    }

    @GetMapping("/runFile/autoAddCheckFiles/input")
    String autoAddCheckFilesWithInput(@RequestParam("title") String title, @RequestParam("in") MultipartFile in) throws Exception {
        return dockerService.selfTest(title, in);
    }
}
