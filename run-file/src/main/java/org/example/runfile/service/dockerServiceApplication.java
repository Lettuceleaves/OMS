package org.example.runfile.service;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
//import org.springframework.mock.web.MockMultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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
    public void addNewImage(String language) throws IOException, InterruptedException {
        checkDocker();
        try {
            ProcessBuilder processBuilder = commands.buildImage(language);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            System.out.println("Success_exitcode:" + exitCode);
            images.put(language, new HashMap<>());
        } catch (Exception e) {
            System.out.println("Error building image: " + e.getMessage());
        }
    }

    // 移除镜像 TODO

    // 运行注册

    public void registerRunDocker(String language) throws IOException, InterruptedException {
        lock.lock();
        if (!status.containsKey(language)) {
            System.out.println("New image registered: " + language);
            addNewImage(language);
        }
        System.out.println("Running image " + language + " in docker mode");
        int state = status.getOrDefault(language, 0);
        status.put(language, state + 1);
        lock.unlock();
    }

    // 运行注册信息销毁

    public void deleteRunDocker(String language) {
        lock.lock();
        int state = status.getOrDefault(language, 0);
        if (state == 0) System.out.println("Error in delete");
        if (state == 1) {
            // 启动定时器 TODO
        }
        status.put(language, state - 1);
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

    private String checkAmswer(String title, String outFilePath, Path tempDirPath) throws IOException {
        StringBuilder ansStringBuilder = new StringBuilder();
        File[] ansFiles = new File(System.getProperty("user.dir"), "run-file" + File.separator + "problems" + File.separator + title)
                .listFiles((dir, name) -> name.startsWith("ans"));
        if (ansFiles != null) {
            for (File ansFile : ansFiles) {
                String fileName = ansFile.getName();
                String filePath = tempDirPath.resolve(fileName).toString();
                Files.copy(ansFile.toPath(), Paths.get(filePath));
                ansStringBuilder.append(new String(Files.readAllBytes(ansFile.toPath()))).append("\n");
            }
        }
        String ans = ansStringBuilder.toString().trim();


        String result = new String(Files.readAllBytes(Paths.get(outFilePath))).replace("\r\n", "\n").trim();
        String ansString = new String(ans.getBytes()).replace("\r\n", "\n").trim();
        if (result.equals(ansString)) {
            result = "Accept";
        } else {
            System.out.println(result);
            System.out.println(ansString);
            result = "Wrong Answer\n" + result;
        }
        return result;
    }

    // 运行单个文件
    public String runSingleFileNoInput(String language, Object file, Path tempDirPath, String title, boolean checkAnswer, boolean selfTest) throws Exception {
        if (language == null || language.isEmpty()) throw new Exception("Language is empty");
        try {
            // Path tempDirPath = Paths.get(System.getProperty("user.dir"), "run-file", UUID.randomUUID().toString());
            String tempDir = tempDirPath.toString();
            String cFilePath = tempDirPath.resolve("test" + '.' + language).toString();
            String monitorFilePath = tempDirPath.resolve("monitor.c").toString();

            // 创建临时目录
            Files.createDirectories(tempDirPath);

            // 当前路径下的tun-file子文件夹的 title子文件夹的所有in开头的文件
            File[] files = new File(System.getProperty("user.dir"), "run-file" + File.separator + "problems" + File.separator + title)
                    .listFiles((dir, name) -> name.startsWith("in"));
            if (files != null) {
                if (selfTest) {
                    // 只拷贝in9999.txt,不拷贝其他的
                    for (File f : files) {
                        String fileName = f.getName();
                        if (fileName.equals("in9999.txt")) {
                            String filePath = tempDirPath.resolve(fileName).toString();
                            Files.copy(f.toPath(), Paths.get(filePath));
                            break;
                        }
                    }
                } else {
                    for (File f : files) {
                        String fileName = f.getName();
                        String filePath = tempDirPath.resolve(fileName).toString();
                        Files.copy(f.toPath(), Paths.get(filePath));
                    }
                }
            }
            // 保存上传的文件
            if (file instanceof MultipartFile) {
                MultipartFile multipartFile = (MultipartFile) file;
                multipartFile.transferTo(new File(cFilePath));
            } else if (file instanceof File) {
                File file1 = (File) file;
                if (file1 != null && file1.exists()) {
                    Files.copy(file1.toPath(), Paths.get(cFilePath));
                }
            }

            // 把当前路径下的monitor.c文件复制到临时目录
            File monitorFile = new File(monitorFilePath);
            if (!monitorFile.exists()) {
                Files.copy(Paths.get(System.getProperty("user.dir"), "run-file", "monitor.c"), monitorFile.toPath());
//                把这句改成当前路径下的runfile子文件夹的title子文件夹中的monitor.c文件
//                Files.copy(Paths.get(System.getProperty("user.dir"), "run-file", title, "monitor.c"), monitorFile.toPath());
            }

            // 检查文件是否正确上传
            if (!new File(cFilePath).exists()) {
                throw new IOException("File upload failed");
            }

            String outFilePath = createOutputFile(tempDirPath);

            ProcessBuilder processBuilder = commands.runSingleFileNoInput(language, "test", tempDirPath.toString());
            processBuilder.redirectErrorStream(true);
            registerRunDocker(language);
            Process process = processBuilder.start();

            // 设置超时时间 TODO
            int limitTime = 100000;

            deleteRunDocker(language);
            process.waitFor();

            if (!new File(outFilePath).exists()) {
                throw new IOException("Output file not found: " + outFilePath);
            }

            String result = "No Check Mode";
            if (checkAnswer) {
                result = checkAmswer(title, outFilePath, tempDirPath);
            }

            if (selfTest) {
                result = new String(Files.readAllBytes(Paths.get(outFilePath))).replace("\r\n", "\n").trim();
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

    public void checkDocker() throws IOException, InterruptedException {
        try {
            // 执行任务列表命令
            Process process = Runtime.getRuntime().exec("tasklist");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean isDockerRunning = false;

            // 逐行读取输出
            while ((line = reader.readLine()) != null) {
                // 检查是否包含 dockerd.exe
                if (line.contains("dockerd.exe")) {
                    isDockerRunning = true;
                    break;
                }
            }

            // 关闭输入流
            reader.close();

            // 输出结果
            if (isDockerRunning) {
                System.out.println("Docker is running.");
            } else {
                System.out.println("Docker is not running.");
                throw new Exception("Docker is not running");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String newTitle(String title, MultipartFile standardCode) throws IOException {
        // 检查当前路径下的problems文件夹中是否有title子文件夹，如果有，就返回false
        File dir = new File(System.getProperty("user.dir"), "run-file" + File.separator + "problems" + File.separator + title);
        if (dir.exists()) {
            System.out.println("Directory already exists: " + dir.getAbsolutePath());
            return "Directory already exists";
        } else {
            // 创建题目文件夹
            if (dir.mkdirs()) {
                System.out.println("Directory created successfully: " + dir.getAbsolutePath());
                String dirString = dir.getAbsolutePath();
                // 保存上传的文件
                String standardCodePath = dirString + File.separator + "standardCode.c";
                try {
                    standardCode.transferTo(new File(standardCodePath));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (IllegalStateException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("Unable to create directory: " + dir.getAbsolutePath());
                return "Unable to create directory";
            }
        }
        return "Directory created successfully";
    }

    public String manuallyAddCheckFiles(String title, MultipartFile in, MultipartFile ans) {
        // 检查当前路径下的problems文件夹中是否有title子文件夹，如果没有，就返回false
        File dir = new File(System.getProperty("user.dir"), "run-file" + File.separator + "problems" + File.separator + title);
        if (!dir.exists()) {
            System.out.println("Directory does not exist: " + dir.getAbsolutePath());
            return "Directory does not exist";
        } else {
            // 保存上传的文件，用in和已经存在的文件中以in开头的文件的文件名后最大数字+1命名
            File[] inFiles = dir.listFiles((dir1, name) -> name.startsWith("in"));
            int maxNum = 0;
            if (inFiles != null) {
                for (File f : inFiles) {
                    String fileName = f.getName();
                    String numStr = fileName.substring(2, fileName.indexOf('.'));
                    int num = Integer.parseInt(numStr);
                    if (num > maxNum) {
                        maxNum = num;
                    }
                }
            }
            String inFilePath = dir.getAbsolutePath() + File.separator + "in" + (maxNum + 1) + ".txt";
            String ansFilePath = dir.getAbsolutePath() + File.separator + "ans" + (maxNum + 1) + ".txt";
            try {
                in.transferTo(new File(inFilePath));
                ans.transferTo(new File(ansFilePath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (IllegalStateException e) {
                throw new RuntimeException(e);
            }
        }
        return "Files added successfully";
    }



    @Override
    public String selfTest(String title, MultipartFile in) throws Exception {
        // 检查当前路径下的problems文件夹中是否有title子文件夹，如果没有，就返回false
        File dir = new File(System.getProperty("user.dir"), "run-file" + File.separator + "problems" + File.separator + title);
        if (!dir.exists()) {
            System.out.println("Directory does not exist: " + dir.getAbsolutePath());
            return "Directory does not exist";
        } else {
            // 保存上传文件,用in9999.txt命名
            String inFilePath = dir.getAbsolutePath() + File.separator + "in9999.txt";
            try {
                in.transferTo(new File(inFilePath));
            } catch (IOException | IllegalStateException e) {
                throw new RuntimeException(e);
            }
            // 获取这道题的standardCode.c文件，作为MultipartFile
            File standardCodeFile = new File(System.getProperty("user.dir"), "run-file" + File.separator + "problems" + File.separator + title + File.separator + "standardCode.c");
            String ans = runSingleFileNoInput("c", standardCodeFile, Paths.get(System.getProperty("user.dir"), "run-file", "runTimeDir", UUID.randomUUID().toString()), title, false, true);
            // 删除上传的文件
            File inFile = new File(inFilePath);
            if (inFile.exists()) {
                inFile.delete();
            } else {
                System.out.println("File not found: " + inFilePath);
            }
            return ans;
        }
    }

    // 可复用资源管理
    // docker容器哦 && 运行文件夹
}
