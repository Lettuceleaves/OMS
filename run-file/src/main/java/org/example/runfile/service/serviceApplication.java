package org.example.runfile.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class serviceApplication implements serviceInterface {

    @Override
    public String testHelloWorld(String cFile, String inFile) throws IOException {
        // String command = "docker run -v %cd%\\run-file\\" + cFile + ":/app/" + cFile + " -v %cd%\\run-file\\in.txt:/app/" + inFile + " a sh -c \"cd /app && ls && gcc " + cFile +  " -o hello && ./hello < " + inFile + "\"";
        String command = "docker run -v %cd%\\run-file\\" + cFile + ":/app/" + cFile + " -v %cd%\\run-file\\in.txt:/app/" + inFile + " -v %cd%\\run-file\\out.txt:/app/out.txt a sh -c \"cd /app && ls && gcc " + cFile + " -o hello && ./hello < " + inFile + " > out.txt\"";
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

    @Override
    public String singleTest(MultipartFile cFile, MultipartFile txtFile) throws Exception {
        Path tempDirPath = Paths.get(System.getProperty("user.dir"), "docker-run");
        String tempDir = tempDirPath.toString();
        String cFilePath = tempDirPath.resolve(cFile.getOriginalFilename()).toString();
        String txtFilePath = tempDirPath.resolve(txtFile.getOriginalFilename()).toString();
        String outFilePath = tempDirPath.resolve("out.txt").toString();

        try {
            // 创建临时目录
            Files.createDirectories(tempDirPath);

            // 保存上传的文件
            cFile.transferTo(new File(cFilePath));
            txtFile.transferTo(new File(txtFilePath));

            // 检查文件是否正确上传
            if (!new File(cFilePath).exists() || !new File(txtFilePath).exists()) {
                throw new IOException("File upload failed");
            }

            // 显式创建 out.txt 文件
            File outFile = new File(outFilePath);
            if (!outFile.exists()) {
                outFile.createNewFile(); // 创建文件
            }

            // docker run --rm -v E:\projects\OMS\docker-run:/app a sh -c "cd /app && echo 'Starting compilation...' && gcc hello.c -o hello >> out.txt 2>&1 && echo 'Compilation done. Running program...' && { ./hello < in.txt >> out.txt 2>&1; } || echo 'Program execution failed' >> out.txt "

            // 构建 Docker 命令
            String command = "docker run --rm -v \"" + tempDir + "\":/app a " +
                    "sh -c \"" +
                    "cd /app && " +
                    "echo 'Starting compilation...' && " +
                    "gcc " + cFile.getOriginalFilename() + " -o hello >> out.txt 2>&1 && " +
                    "echo 'Compilation done. Running program...' && " +
                    "{ ./hello < " + txtFile.getOriginalFilename() + " >> out.txt 2>&1; } || echo 'Program execution failed' >> out.txt " +
                    "\"";
            System.out.println(command);

            // 执行 Docker 命令
            ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", command);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            int exitCode = process.waitFor();
            System.out.println(exitCode);

            // 读取 out.txt 文件的内容
            if (!new File(outFilePath).exists()) {
                throw new IOException("Output file not found: " + outFilePath);
            }

            String result = new String(Files.readAllBytes(Paths.get(outFilePath)));
            System.out.println(result);

            // 删除临时目录
//            deleteDirectory(tempDir);

            return result;
        } catch (Exception e) {
            deleteDirectory(tempDir);
            return "Error: " + e.getMessage();
        }
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
