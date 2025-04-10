package org.example.runfile.service;

public interface commandsInterface {
    ProcessBuilder runSingleFileNoInput(String language, String file, String directory);
    ProcessBuilder buildImage(String language);
}
