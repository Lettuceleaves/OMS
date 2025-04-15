#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/wait.h>
#include <unistd.h>
#include <time.h>
#include <errno.h>
#include <signal.h>
#include <dirent.h>

#define MAX_FILENAME_LENGTH 256
#define MAX_LINE_LENGTH 1024

void handle_alarm(int sig) {
    // 超时处理函数
}

int compare(const void* a, const void* b) {
    const char* str1 = *(const char**)a;
    const char* str2 = *(const char**)b;
    int num1, num2;
    sscanf(str1, "in%d", &num1);
    sscanf(str2, "in%d", &num2);
    return num1 - num2;
}

int execute_test_program(FILE* infile, FILE* outfile, int timeout, const char* filename) {
    pid_t pid = fork();
    if (pid == -1) {
        perror("fork");
        fprintf(outfile, "创建子进程失败\n");
        return -1;
    } else if (pid == 0) {
        if (infile) dup2(fileno(infile), STDIN_FILENO);
        dup2(fileno(outfile), STDOUT_FILENO);
        execlp("./test", "test", NULL);
        perror("execlp");
        exit(1);
    } else {
        signal(SIGALRM, handle_alarm);
        alarm(timeout);
        int status;
        pid_t ret = waitpid(pid, &status, 0);
        alarm(0);

        if (ret == -1) {
            perror("waitpid");
            return -1;
        }

        if (WIFEXITED(status)) {
            int exit_code = WEXITSTATUS(status);
            if (exit_code != 0) {
                fprintf(outfile, "文件%s处理失败，退出码：%d\n", filename, exit_code);
                return -1;
            }
        } else if (WIFSIGNALED(status)) {
            fprintf(outfile, "文件%s处理失败，被信号%d终止\n", filename, WTERMSIG(status));
            return -1;
        }
    }
    return 0;
}

int main() {
    int timeout;
    // printf("请输入超时时间（秒）：");
    // scanf("%d", &timeout);
    timeout = 10;

    FILE* outfile = fopen("out.txt", "w");
    if (!outfile) {
        perror("fopen");
        return 1;
    }

    // 编译test.c
    printf("正在编译test.c...\n");
    FILE* compile_fp = popen("gcc -o test test.c 2>&1", "r");
    if (!compile_fp) {
        perror("popen");
        fprintf(outfile, "编译失败\n");
        fclose(outfile);
        return 1;
    }
    
    char compile_output[MAX_LINE_LENGTH];
    while (fgets(compile_output, sizeof(compile_output), compile_fp)) {
        fprintf(outfile, "%s", compile_output);
    }
    pclose(compile_fp);

    if (access("./test", X_OK) != 0) {
        fprintf(outfile, "test程序不可执行\n");
        fclose(outfile);
        return 1;
    }

    // 收集输入文件
    DIR *dir = opendir(".");
    char **input_files = NULL;
    int num_files = 0;
    struct dirent *entry;

    while ((entry = readdir(dir)) != NULL) {
        if (strncmp(entry->d_name, "in", 2) == 0) {
            input_files = realloc(input_files, (num_files + 1) * sizeof(char*));
            input_files[num_files] = strdup(entry->d_name);
            num_files++;
        }
    }
    closedir(dir);
    qsort(input_files, num_files, sizeof(char*), compare);

    if (num_files > 0) {
        for (int i = 0; i < num_files; i++) {
            printf("正在处理文件：%s\n", input_files[i]);
            FILE* infile = fopen(input_files[i], "r");
            if (!infile) {
                fprintf(outfile, "无法打开%s\n", input_files[i]);
                free(input_files[i]);
                continue;
            }
            execute_test_program(infile, outfile, timeout, input_files[i]);
            fclose(infile);
            free(input_files[i]);
        }
        free(input_files);
    } else {
        printf("无输入文件，直接执行test\n");
        execute_test_program(NULL, outfile, timeout, NULL);
    }

    fclose(outfile);
    printf("处理完成\n");
    return 0;
}