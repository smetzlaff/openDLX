
MIPS_CC=mipsel-linux-gnu-gcc -I. -I../basics
MIPS_ARCH=-mgp32 -mips3 -mno-mips16 -mno-mips3d #-static
MIPS_CC_FLAGS=-g -DMIPSSIM -Wall -O2 -fno-builtin $(MIPS_ARCH)
MIPS_LD_FLAGS=$(MIPS_ARCH) # -Xlinker -belf32-tradlittlemips #-lm -T  ../mips.ld
MIPS_DUMP=mipsel-linux-gnu-objdump 
MIPS_DUMP_FLAGS=-Ss
MIPS_OBJC=mipsel-linux-gnu-objcopy 

TARGET=bsort

SRC_FILES = $(wildcard *.c) $(wildcard ../basics/*.c)

all: $(TARGET).elf $(TARGET).bin $(TARGET).dump

$(SRC_FILES:%.c=%.o): $(SRC_FILES)
	$(MIPS_CC)  $(MIPS_CC_FLAGS) -c -o $@ $(@:%.o=%.c)

$(TARGET).elf: $(SRC_FILES:%.c=%.o)
	$(MIPS_CC) $(MIPS_LD_FLAGS) -o $@ $(SRC_FILES:%.c=%.o)

$(TARGET).bin: $(TARGET).elf
	$(MIPS_OBJC) -O binary $< $(TARGET).bin

$(TARGET).dump: $(TARGET).elf
	$(MIPS_DUMP) $(MIPS_DUMP_FLAGS) $< > $(@:%.elf=%.dump)
	-../basics/updatecfgFiles.sh $(TARGET).dump .

depend:
	makedepend -Y. -- $(MIPS_CC_FLAGS) --  $(SRC_FILES)

clean:
	-rm $(SRC_FILES:%.c=%.o)
	-rm $(TARGET).elf
	-rm $(TARGET).dump
	-rm $(TARGET).bin

# DO NOT DELETE

../basics/printf.o: ../basics/printf.h ../basics/datatypes.h
../basics/printf.o: ../basics/syscall.h
