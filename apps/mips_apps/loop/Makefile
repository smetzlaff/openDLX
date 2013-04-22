
MIPS_CC=mipsel-linux-gnu-gcc
MIPS_ARCH=-mgp32 -mips3 -mno-mips16 -mno-mips3d
MIPS_CC_FLAGS=-g -Wall -O2 $(MIPS_ARCH)
MIPS_LD_FLAGS=$(MIPS_ARCH) #-lm -T  ../mips.ld
MIPS_DUMP=mipsel-linux-gnu-objdump 
MIPS_DUMP_FLAGS=-Ss
MIPS_OBJC=mipsel-linux-gnu-objcopy 

all: loop 

loop : loop.elf loop.bin loop.dump
	-echo "Ready"

loop.elf : loop.s
	$(MIPS_CC) $(MIPS_LD_FLAGS) -o $@ loop.s

loop.bin: loop.elf
	$(MIPS_OBJC) -O binary $< loop.bin

loop.dump: loop.elf
	$(MIPS_DUMP) $(MIPS_DUMP_FLAGS) $< > $(@:%.elf=%.dump)
	-../basics/updatecfgFiles.sh loop.dump .


clean:
	-rm *.elf
	-rm *.dump
	-rm *.bin

