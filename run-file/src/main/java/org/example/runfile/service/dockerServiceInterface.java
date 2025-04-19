package org.example.runfile.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface dockerServiceInterface {
    // 单题测验
    String runSingleFileNoInput(String language, Object file, Path tempDirPath, String title, boolean checkAnswer, boolean selfTest) throws Exception;
    // 添加新镜像
    void addNewImage(String serviceName) throws IOException, InterruptedException;

    String newTitle(String title, MultipartFile standardCode) throws IOException;

    String manuallyAddCheckFiles(String title, MultipartFile in, MultipartFile ans);

    String selfTest(String title, MultipartFile in) throws Exception;
}
