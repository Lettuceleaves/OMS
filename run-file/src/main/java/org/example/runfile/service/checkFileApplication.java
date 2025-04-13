package org.example.runfile.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public class checkFileApplication implements checkFileInterface {

    @Override
    public void createNewDir(String directory) throws Exception {
        File dir = new File(directory);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.println("Directory created successfully: " + directory);
            } else throw new Exception("Unable to create directory: " + directory);
        } else throw new Exception("Directory already exists: " + directory);
        // 在这个文件夹下创建一个文件夹，用来装标准代码和用这个代码文件生成的输入输出文件
        String subDir = directory + File.separator + "standard";
        File subDirFile = new File(subDir);
        if (!subDirFile.exists()) {
            if (subDirFile.mkdirs()) {
                System.out.println("Subdirectory created successfully: " + subDir);
            } else throw new Exception("Unable to create subdirectory: " + subDir);
        } else throw new Exception("Subdirectory already exists: " + subDir);
    }

    @Override
    public void checkPair(String problemName, MultipartFile input, MultipartFile output) throws Exception {}

    @Override
    public void standardCode(String problemName, MultipartFile code) throws Exception {}

    @Override
    public void standardFile(String problemName, MultipartFile input) throws Exception {}
}
