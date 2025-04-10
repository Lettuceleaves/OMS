package org.example.runfile.service;

import org.springframework.stereotype.Service;

@Service
public class commandsApplication implements commandsInterface {

    // 目前只适配windows，out没做处理，文件没分文件夹 TODO
    public ProcessBuilder runSingleFileNoInput(String language, String file, String directory) {
        String command = "docker run -v %cd%\\" + directory + ":/app " + language + " sh -c \"ls && cd /app && echo 'Starting compilation...' && ls && gcc " + file + " -o test && ./test" + " > out.txt\"";
        ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", command);
        System.out.println(command);
        return processBuilder;
    }

    public ProcessBuilder buildImage(String language) {
        String command = "docker build -t c run-file";
        ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", command);
        System.out.println(command);
        return processBuilder;
    }
}