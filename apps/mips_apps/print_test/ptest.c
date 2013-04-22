
#include "printf.h"

int main(int argc, char *argv[])
{
	// output should be:
	// "0: Hello World -123 45$4000000000fffffff0-001  -89"
    printf("%d: Hello World%5d% d%c%u%x%04d%5d\n", 0, -123, 45, '$', -294967296, -16, -1, -89);
	asm volatile ("break");
    return 0;
}

