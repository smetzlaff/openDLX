#ifndef _MALLOC_H_
#define _MALLOC_H_
#define _MALLOC_H 1

#include "datatypes.h"
#include <stdarg.h>

// heap base address
#define MALLOC_BASE_ADDR 0xee000000
// 512k heap size
#define MALLOC_MEM_SIZE 0x00080000 

void *mipssim_malloc(size_t size);
void mipssim_free(void *ptr);

#endif
