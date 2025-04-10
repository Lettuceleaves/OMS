package org.example.runfile.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface serviceInterface {
    String testHelloWorld(String cFile, String inFile) throws IOException;
    String singleTest (MultipartFile cFile, MultipartFile txtFile) throws Exception;
}
