#!/bin/sh

# 编译 C 文件
gcc hello.c -o hello

# 运行程序，并将输入文件的内容传递给程序
./hello < in.txt
