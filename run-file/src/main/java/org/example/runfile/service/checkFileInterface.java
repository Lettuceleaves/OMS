package org.example.runfile.service;

import org.springframework.web.multipart.MultipartFile;

public interface checkFileInterface {
    // 新建题目文件夹
    void createNewDir(String directory) throws Exception;
    // 传入一组输入和输出文件
    void checkPair(String problemName, MultipartFile input, MultipartFile output) throws Exception;
    // 传入标准代码
    void standardCode(String problemName, MultipartFile code) throws Exception;
    // 传入标准输入文件
    void standardFile(String problemName, MultipartFile input) throws Exception;
}
