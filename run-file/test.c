#include <stdio.h>
#include <unistd.h>

int main() {
    // 打印 0 到 1000项斐波那契数列， 注意溢出问题
    // long a = 0, b = 1, c;
    // printf("0\n");
    // for (int i = 1; i <= 1000; i++) {
    //     c = a + b;
    //     if (c > 1000) {
    //         break;
    //     }
    //     printf("%ld\n", c);
    //     a = b;
    //     b = c;
    // }

    int a, b, c;
    scanf("%d%d%d", &a, &b, &c);
    printf("%d\n", a + b + c);
}