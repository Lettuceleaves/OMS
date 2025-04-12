package org.example.runfile.service;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class commandsApplication implements commandsInterface {

    public ProcessBuilder runSingleFileNoInputWin(String language, String file, String directory) {
        // Windows系统
        String command = "docker run -v " + directory + ":/app " + language + " sh -c \"ls && cd /app && echo 'Starting compilation...' && ls && gcc " + file + " -o test && ./test" + " > out.txt\"";
        ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", command);
        System.out.println(command);
        return processBuilder;
    }

    public ProcessBuilder runSingleFileNoInput(String language, String file, String directory) {
        // 获取当前的操作系统
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return runSingleFileNoInputWin(language, file, directory);
        } else if (os.contains("mac")) {
            // Mac系统
            System.out.println("Mac系统， 暂不支持");
        } else if (os.contains("nix") || os.contains("nux")) {
            // Linux系统
            System.out.println("Linux系统");
        } else {
            // 其他操作系统
            System.out.println("其他操作系统，暂不支持");
        }
        return null;
    }

    public ProcessBuilder buildImageWin(String language) {
        // Windows系统
        String command = "docker build -t " + language + " run-file";
        ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", command);
        System.out.println(command);
        return processBuilder;
    }

    public ProcessBuilder buildImage(String language) {
        // 获取当前的操作系统
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return buildImageWin(language);
        } else if (os.contains("mac")) {
            // Mac系统
            System.out.println("Mac系统， 暂不支持");
        } else if (os.contains("nix") || os.contains("nux")) {
            // Linux系统
            System.out.println("Linux系统");
        } else {
            // 其他操作系统
            System.out.println("其他操作系统，暂不支持");
        }
        return null;
    }
}