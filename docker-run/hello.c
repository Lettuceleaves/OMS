#include <stdio.h>

int main() {
    int a, b, c;
     scanf("%d %d %d", &a, &b, &c);
    int sum = a - b * c;
    printf("a: %d, b: %d, c: %d\n", a, b, c);
    printf("Sum: %d\n", sum)
    return 0;
}

// docker run -v /run-file/test.txt:/app/test.txt a sh -c "gcc hello.c -o hello && ./hello < in.txt && ls"

// PS E:\projects\OMS> docker run -v /run-file/test.txt:/app/test.txt -v /run-file/hello.c:/app/hello.c -v /run-file/in.txt:/app/in.txt a sh -c "cd hello.c && pwd"

// docker run -v $PWD\run-file\hello.c:/app/hello.c -v $PWD\run-file\in.txt:/app/in.txt gcc sh -c "cd /app && ls && gcc hello.c -o hello && ./hello < in.txt"