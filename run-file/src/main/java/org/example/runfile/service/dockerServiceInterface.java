package org.example.runfile.service;

import org.springframework.web.multipart.MultipartFile;

public interface dockerServiceInterface {
    // 单题测验
    String runSingleFileNoInput(String language, MultipartFile file) throws Exception;
    // 添加新镜像
    void addNewImage(String serviceName);
}
