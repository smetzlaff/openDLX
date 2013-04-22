#ifndef _PRINTF_H_
#define _PRINTF_H_
#define	_PRINTF_H	1


#include "datatypes.h"
#include <stdarg.h>
#include <stdio.h>
#include "syscall.h"

//int putchar(int c);
int printf(const char *format, ...);

#endif
