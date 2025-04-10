package org.example.runfile.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class dockerServiceApplication implements dockerServiceInterface {

    @Autowired
    private commandsInterface commands;

    // 静态变量，存储服务信息
    private static final class services {
        // 存储服务信息的 Map
        private final static Map<String, Map<String, String>> images = new HashMap<>(10);

        // 状态变量
        private final static Map<String, Integer> status = new HashMap<>(10);

        private services() {}
    }

    private static final services service = new services();

    // 添加新镜像
    public void addNewImage(String language) {
        if (services.status.containsKey(language)) return;
        synchronized (service) { // 锁定 service
            try {
                ProcessBuilder processBuilder = commands.buildImage(language);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
                int exitCode = process.waitFor();
                System.out.println(exitCode);
                services.images.put(language, new HashMap<>());
                services.status.put(language, 0);
            } catch (Exception e) {
                System.out.println("Error building image: " + e.getMessage());
            }
        }
    }

    // 移除镜像 TODO

    // 运行单个文件，无输入
    @Async
    public String runSingleFileNoInput(String language, MultipartFile file) throws Exception {
        if (language == null || language.isEmpty()) throw new Exception("Language is empty");
//        synchronized (service) { // 锁定 service
            try {
                Path tempDirPath = Paths.get(System.getProperty("user.dir"), "docker-run", UUID.randomUUID().toString());
                String tempDir = tempDirPath.toString();
                String cFilePath = tempDirPath.resolve(file.getOriginalFilename()).toString();
                String outFilePath = tempDirPath.resolve("out.txt").toString();

                // 创建临时目录
                Files.createDirectories(tempDirPath);

                // 保存上传的文件
                file.transferTo(new File(cFilePath));

                // 检查文件是否正确上传
                if (!new File(cFilePath).exists()) {
                    throw new IOException("File upload failed");
                }

                // 显式创建 out.txt 文件
                File outFile = new File(outFilePath);
                if (!outFile.exists()) {
                    outFile.createNewFile(); // 创建文件
                }

                ProcessBuilder processBuilder = commands.runSingleFileNoInput(language, file.getOriginalFilename(), tempDirPath.toString());
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                System.out.println(outFilePath);

//                if (!process.waitFor(30, TimeUnit.SECONDS)) {
//                    throw new Exception("Docker run timed out");
//                }
                int exitCode = process.waitFor();
                System.out.println(exitCode);

                // 读取 out.txt 文件的内容
                if (!new File(outFilePath).exists()) {
                    throw new IOException("Output file not found: " + outFilePath);
                }

                String result = new String(Files.readAllBytes(Paths.get(outFilePath)));
                System.out.println("->" + result + "<-");

                // 删除临时目录
                deleteDirectory(tempDir);

                return result;
            } catch (Exception e) {
                System.out.println("Error running single file: " + e.getMessage());
            }
        // }
        return "Success";
    }

    private void deleteDirectory(String directoryPath) throws IOException {
        File directory = new File(directoryPath);
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            directory.delete();
        }
    }
}
