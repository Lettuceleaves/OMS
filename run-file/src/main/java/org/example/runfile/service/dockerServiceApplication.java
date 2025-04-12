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
import java.util.concurrent.locks.ReentrantLock;

@Service
public class dockerServiceApplication implements dockerServiceInterface {

    @Autowired
    private commandsInterface commands;

    // 静态变量，存储服务信息
        // 存储服务信息的 Map
    private static final Map<String, Map<String, String>> images = new HashMap<>(10);

    // 状态变量
    private static final Map<String, Integer> status = new HashMap<>(10);

    // image计时器 // 超时移除镜像 TODO
    private static final Map<String, Timer> timers = new HashMap<>(10);

    private final ReentrantLock lock = new ReentrantLock();

    // 添加新镜像
    public void addNewImage(String language) {
        try {
            ProcessBuilder processBuilder = commands.buildImage(language);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            System.out.println(exitCode);
            images.put(language, new HashMap<>());
        } catch (Exception e) {
            System.out.println("Error building image: " + e.getMessage());
        }
    }

    // 移除镜像 TODO

    // 运行注册

    public void registerRunDocker(String language) {
        lock.lock();
        if (!status.containsKey(language)) {
            addNewImage(language);
        }
        int state = status.getOrDefault(language, 0);
        status.put(language, state + 1);
        lock.unlock();
    }

    // 运行销毁

    public void deleteRunDocker(String language) {
        lock.lock();
        int state = status.getOrDefault(language, 0);
        if (state == 0) System.out.println("Error in delete");
        status.put(language, state - 1);
        if (state == 1) {
            status.remove(language);
            // 启动定时器 TODO
        }
        lock.unlock();
    }

    // 创建输出文件

    private String createOutputFile(Path tempDirPath) throws IOException {
        String outFilePath = tempDirPath.resolve("out.txt").toString();
        File outFile = new File(outFilePath);
        if (!outFile.exists()) {
            outFile.createNewFile(); // 创建文件
        }
        return outFilePath;
    }

    // 运行单个文件，无输入
    public String runSingleFileNoInput(String language, MultipartFile file, MultipartFile ans) throws Exception {
        if (language == null || language.isEmpty()) throw new Exception("Language is empty");
        try {
            Path tempDirPath = Paths.get(System.getProperty("user.dir"), "run-file", UUID.randomUUID().toString());
            String tempDir = tempDirPath.toString();
            String cFilePath = tempDirPath.resolve(file.getOriginalFilename()).toString();

            // 创建临时目录
            Files.createDirectories(tempDirPath);

            // 保存上传的文件
            file.transferTo(new File(cFilePath));

            // 检查文件是否正确上传
            if (!new File(cFilePath).exists()) {
                throw new IOException("File upload failed");
            }

            String outFilePath = createOutputFile(tempDirPath);

            ProcessBuilder processBuilder = commands.runSingleFileNoInput(language, file.getOriginalFilename(), tempDirPath.toString());
            processBuilder.redirectErrorStream(true);
            registerRunDocker(language);
            Process process = processBuilder.start();

            // 设置超时时间 TODO
            int limitTime = 100000;

            deleteRunDocker(language);
            process.waitFor();

            // 读取 out.txt 文件的内容
            if (!new File(outFilePath).exists()) {
                throw new IOException("Output file not found: " + outFilePath);
            }

            String result = new String(Files.readAllBytes(Paths.get(outFilePath))).replace("\r\n", "\n").trim();
            String ansString = new String(ans.getBytes()).replace("\r\n", "\n").trim();
            if (result.equals(ansString)) {
                result = "Accept";
            } else {
                result = "Wrong Answer\n" + result;
            }

            // 删除临时目录
            deleteDirectory(tempDir);

            return result;
        } catch (Exception e) {
            System.out.println("Error running single file: " + e.getMessage());
        }
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
