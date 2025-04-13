package org.example.runfile.service;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface dockerServiceInterface {
    // 单题测验
    String runSingleFileNoInput(String language, MultipartFile file, MultipartFile ans, Path tempDirPath, int mode) throws Exception;
    // 添加新镜像
    void addNewImage(String serviceName);
}
