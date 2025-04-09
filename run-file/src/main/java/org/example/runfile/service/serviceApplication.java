package org.example.runfile.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class serviceApplication implements serviceInterface {

    @Override
    public String testHelloWorld(String cFile, String inFile) throws IOException {
        String command = "docker run -v %cd%\\run-file\\" + cFile + ":/app/" + cFile + " -v %cd%\\run-file\\in.txt:/app/" + inFile + " a sh -c \"cd /app && ls && gcc " + cFile +  " -o hello && ./hello < " + inFile + "\"";
        System.out.println(command);
        // ProcessBuilder processBuilder = new ProcessBuilder("sh", "-c", command); // 在 Linux 上
        ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", command); // 在 Windows 上
        processBuilder.redirectErrorStream(true); // 将错误输出和标准输出合并

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // 打印命令的输出
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Command executed successfully"; // 返回执行结果
    }

}
