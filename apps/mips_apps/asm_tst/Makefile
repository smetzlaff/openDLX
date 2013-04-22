
MIPS_CC=mipsel-linux-gnu-gcc
MIPS_ARCH=-mgp32 -mips3 -mno-mips16 -mno-mips3d
MIPS_CC_FLAGS=-g -Wall -O2 $(MIPS_ARCH)
MIPS_LD_FLAGS=$(MIPS_ARCH) #-lm -T  ../mips.ld
MIPS_DUMP=mipsel-linux-gnu-objdump 
MIPS_DUMP_FLAGS=-S
MIPS_OBJC=mipsel-linux-gnu-objcopy 

TARGET=asm_tst

SRC_FILES = asm_tst.s

all: $(TARGET).elf $(TARGET).bin $(TARGET).dump

$(TARGET).elf: $(SRC_FILES)
	$(MIPS_CC) $(MIPS_LD_FLAGS) -o $@ $(SRC_FILES)

$(TARGET).bin: $(TARGET).elf
	$(MIPS_OBJC) -O binary $< $(TARGET).bin

$(TARGET).dump: $(TARGET).elf
	$(MIPS_DUMP) $(MIPS_DUMP_FLAGS) $< > $(@:%.elf=%.dump)

clean:
	-rm $(TARGET).elf
	-rm $(TARGET).dump
	-rm $(TARGET).bin

