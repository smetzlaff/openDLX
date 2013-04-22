
#include "malloc.h"

static uint32 mipssim_malloc_pointer = MALLOC_BASE_ADDR;

void *mipssim_malloc(size_t size)
{
	if(mipssim_malloc_pointer + size > MALLOC_BASE_ADDR + MALLOC_MEM_SIZE)
	{
		return NULL;
	}
	else
	{
		uint32 value = mipssim_malloc_pointer;
		mipssim_malloc_pointer += size;

		// align to word addresses
		if((mipssim_malloc_pointer & 0x3) != 0)
		{
			mipssim_malloc_pointer = (mipssim_malloc_pointer & 0xfffffffc) + 4;
		}

		return (void *)value;
	}
}

void mipssim_free(void *ptr)
{
	// do nothing
}




