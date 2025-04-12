#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/wait.h>
#include <unistd.h>
#include <time.h>
#include <errno.h>
#include <signal.h>

#define MAX_FILENAME_LENGTH 256
#define MAX_LINE_LENGTH 1024

// 用于处理子进程超时的信号处理函数
void handle_alarm(int sig) {
    // 如果收到SIGALRM信号，说明子进程超时了
    // 这里不需要做任何处理，因为父进程会检测到子进程状态异常
}

// 比较函数，用于对文件名按数字排序
int compare(const void* a, const void* b) {
    const char* str1 = *(const char**)a;
    const char* str2 = *(const char**)b;
    int num1, num2;
    sscanf(str1, "in%d", &num1);
    sscanf(str2, "in%d", &num2);
    return num1 - num2;
}

int main() {
    char cwd[1024];
    if (getcwd(cwd, sizeof(cwd)) != NULL) {
        printf("当前工作目录：%s\n", cwd);
    } else {
        perror("getcwd");
    }
    int timeout; // 超时时间
    printf("请输入超时时间（秒）：");
    scanf("%d", &timeout);

    // 打开输出文件
    FILE* outfile = fopen("out.txt", "w");
    if (outfile == NULL) {
        perror("fopen");
        return 1;
    }

    // 尝试编译test.c文件
    printf("正在尝试编译test.c文件...\n");
    FILE* compile_fp = popen("gcc -o test test.c 2>&1", "r");
    if (compile_fp == NULL) {
        perror("popen");
        fprintf(outfile, "无法执行gcc编译test.c文件\n");
        fclose(outfile);
        return 1;
    }
    char compile_output[MAX_LINE_LENGTH];
    while (fgets(compile_output, MAX_LINE_LENGTH, compile_fp)) {
        fprintf(outfile, "%s", compile_output);
    }
    pclose(compile_fp);

    // 检查test程序是否存在
    if (access("./test", X_OK) != 0) {
        fprintf(outfile, "test程序编译失败或不存在，无法继续处理文件\n");
        printf("test程序编译失败或不存在，无法继续处理文件\n");
        fclose(outfile);
        return 1;
    }

    // 获取当前目录下的所有以"in"开头的文件名
    char* filenames[1000]; // 假设最多有1000个输入文件
    int count = 0;
    FILE* fp = popen("ls in* 2>/dev/null", "r");
    if (fp == NULL) {
        perror("popen");
        return 1;
    }
    char filename[MAX_FILENAME_LENGTH];
    while (fgets(filename, MAX_FILENAME_LENGTH, fp)) {
        filename[strcspn(filename, "\n")] = '\0'; // 去掉换行符
        filenames[count++] = strdup(filename);
    }
    pclose(fp);

    // 按数字排序
    qsort(filenames, count, sizeof(char*), compare);

    // 遍历所有输入文件
    for (int i = 0; i < count; i++) {
        printf("正在处理文件：%s\n", filenames[i]);

        // 打开输入文件
        FILE* infile = fopen(filenames[i], "r");
        if (infile == NULL) {
            perror("fopen");
            fprintf(outfile, "无法打开输入文件：%s\n", filenames[i]);
            fclose(outfile);
            return 1;
        }

        // 创建子进程运行test程序
        pid_t pid = fork();
        if (pid == -1) {
            perror("fork");
            fprintf(outfile, "创建子进程失败\n");
            fclose(infile);
            fclose(outfile);
            return 1;
        } else if (pid == 0) {
            // 子进程
            // 重定向标准输入和标准输出
            dup2(fileno(infile), STDIN_FILENO);
            dup2(fileno(outfile), STDOUT_FILENO);

            // 执行test程序
            execlp("./test", "test", NULL);
            perror("execlp");
            exit(1); // 如果execlp失败，退出子进程
        } else {
            // 父进程
            // 设置超时信号处理
            signal(SIGALRM, handle_alarm);
            alarm(timeout);

            // 等待子进程结束
            int status;
            pid_t ret = waitpid(pid, &status, 0);
            alarm(0); // 取消超时信号

            if (ret == -1) {
                perror("waitpid");
                fprintf(outfile, "等待子进程失败\n");
                fclose(infile);
                fclose(outfile);
                return 1;
            }

            if (WIFEXITED(status)) {
                int exit_code = WEXITSTATUS(status);
                if (exit_code == 0) {
                    printf("文件%s处理成功\n", filenames[i]);
                } else {
                    fprintf(outfile, "文件%s处理失败，退出码：%d\n", filenames[i], exit_code);
                    printf("文件%s处理失败，退出码：%d\n", filenames[i], exit_code);
                    fclose(infile);
                    fclose(outfile);
                    return 1;
                }
            } else if (WIFSIGNALED(status)) {
                fprintf(outfile, "文件%s处理失败，被信号%d终止\n", filenames[i], WTERMSIG(status));
                printf("文件%s处理失败，被信号%d终止\n", filenames[i], WTERMSIG(status));
                fclose(infile);
                fclose(outfile);
                return 1;
            }

            fclose(infile); // 关闭输入文件
        }
    }

    printf("所有文件处理完成\n");
    fclose(outfile); // 关闭输出文件
    return 0;
}
