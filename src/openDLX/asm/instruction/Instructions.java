/*******************************************************************************
 * openDLX - A DLX/MIPS processor simulator.
 * Copyright (C) 2013 The openDLX project, University of Augsburg, Germany
 * Project URL: <https://sourceforge.net/projects/opendlx>
 * Development branch: <https://github.com/smetzlaff/openDLX>
 *
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program, see <LICENSE>. If not, see
 * <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package openDLX.asm.instruction;

import java.util.Hashtable;

public class Instructions {
	private static Instructions instance_;
	private Hashtable<String, Instruction> str2instr_;
	private Hashtable<InstructionWrapper, String> instr2str_;
	private Hashtable<InstructionWrapper, InstructionType> instr2type_;
	private Hashtable<String, String> str2desc_;

	public static Instructions instance() {
		if (instance_ == null)
			instance_ = new Instructions();
		return instance_;
	}

	/**
	 * e.g. getMnemonic(new Instruction(0, 0, 0, 0, 0, 0x20)) == add
	 * 
	 * @param instr
	 * @return
	 */
	public String getMnemonic(Instruction instr) {
		if (instr == null)
			return null;
		else if (instr.instrWord() == 0x0)
			return "nop";
		else
			return instr2str_.get(new InstructionWrapper(instr));
	}

	/**
	 * e.g. getType(new Instruction(0, 0, 0, 0, 0, 0x20)) ==
	 * InstructionType.ALU_REGISTER
	 * 
	 * @param instr
	 * @return
	 */
	public InstructionType getType(Instruction instr) {
		if (instr == null)
			return null;
		else if (instr.instrWord() == 0x0)
			return InstructionType.NOP;
		else
			return instr2type_.get(new InstructionWrapper(instr));
	}

	/**
	 * e.g. getInstruction("add") == new Instruction(0, 0, 0, 0, 0, 0x20)
	 * 
	 * @param mnem
	 * @return
	 */
	public Instruction getInstruction(String mnem) {
		if (mnem == null)
			return null;
		if (mnem.equalsIgnoreCase("nop"))
			return new Instruction(0x0);
		return str2instr_.get(mnem.toLowerCase());
	}

	/**
	 * e.g. getDescription("addi") =
	 * "addi rt,rs,immediate: add immediate and rs and save result in rt"
	 * 
	 * @param mnem
	 * @return
	 */
	public String getDescription(String mnem) {
		if (mnem == null)
			return null;
		if (mnem.equalsIgnoreCase("nop"))
			return "";
		return str2desc_.get(mnem);
	}

	private Instructions() {
		str2instr_ = new Hashtable<String, Instruction>();
		instr2str_ = new Hashtable<InstructionWrapper, String>();
		instr2type_ = new Hashtable<InstructionWrapper, InstructionType>();
		str2desc_ = new Hashtable<String, String>();
		try {
			//add("nop", new Instruction(), InstructionType.NOP);
			//normal
			add("j", new Instruction(0x02, 0), InstructionType.JUMP,
					"j[Jump] target: jump to target");
			add("jal", new Instruction(0x03, 0), InstructionType.JUMP,
					"jal[Jump And Link] target: save next adress in r31 and jump to target");
			add("beqz", new Instruction(0x04, 0, 0, 0), InstructionType.BRANCH,
					"beqz[Branch on Equal Zero] rs,offset: compare rs to zero then do a conditional branch");//beq $rt = 0
			add("bnez", new Instruction(0x05, 0, 0, 0), InstructionType.BRANCH,
					"bnez[Branch on Not Equal Zero] rs, offset: compare rs to non-zero then do a conditional branch");//bne $rt = 0

			add("addi", new Instruction(0x08, 0, 0, 0), InstructionType.ALU_IMMEDIATE,
					"addi[ADD Immediate word] rt,rs,immediate: add immediate and rs and save result in rt");
			add("addui",
					new Instruction(0x09, 0, 0, 0),
					InstructionType.ALU_IMMEDIATE,
					"addui[ADD Unsigned Immediate word] rt,rs,immediate: add unsigned immediate to rs and save result in rt");
			add("slti",
					new Instruction(0x0A, 0, 0, 0),
					InstructionType.ALU_IMMEDIATE,
					"slti[Set on Less Than Immediate] rt,rs,immediate: record result of less-than comparison with rs and immediate in rt");
			add("sltui",
					new Instruction(0x0B, 0, 0, 0),
					InstructionType.ALU_IMMEDIATE,
					"sltui[Set on Less Than Unsigned Immediate] rt,rs,immediate: record result of less-than comparison with rs and unsigned immediate in rt");
			add("andi",
					new Instruction(0x0C, 0, 0, 0),
					InstructionType.ALU_IMMEDIATE,
					"andi[AND Immediate] rt,rs,immediate: do a bitwise logical AND with rs and immediate and save result in rt");
			add("ori",
					new Instruction(0x0D, 0, 0, 0),
					InstructionType.ALU_IMMEDIATE,
					"ori[OR Immediate] rt,rs,immediate: do a bitwise logical OR with rs and immediate and save result in rt");
			add("xori",
					new Instruction(0x0E, 0, 0, 0),
					InstructionType.ALU_IMMEDIATE,
					"xori[eXclusive OR Immediate] rt,rs,immediate: do a bitwise logical exclusive OR with rs and immediate and save result in rt");
			add("lhi", new Instruction(0x0F, 0, 0, 0), InstructionType.LOAD_IMMEDIATE,
					"lhi[Load High Immediate] rt, immediate: load immediate in upper 16 bit of rt");//lui
			add("subi", new Instruction(0x10, 0, 0, 0), InstructionType.ALU_IMMEDIATE,
					"subi[SUBtract Immediate word] rt,rs,immediate: subtract immediate and rs and save result in rt"); //COP0
			add("subui",
					new Instruction(0x11, 0, 0, 0),
					InstructionType.ALU_IMMEDIATE,
					"subui[SUBtract Unsigned Immediate word] rt,rs,immediate: subtract unsigned immediate and rs and save result in rt");//COP1
			add("sgti",
					new Instruction(0x12, 0, 0, 0),
					InstructionType.ALU_IMMEDIATE,
					"sgti[Set on Greater Than Immediate] rt,rs,immediate: record result of greater-than comparison with rs and immediate in rt");//COP2
			add("sgtui",
					new Instruction(0x13, 0, 0, 0),
					InstructionType.ALU_IMMEDIATE,
					"sgtui[Set on Greater Than Unsigned Immediate] rt,rs,immediate: record result of greater-than comparison with rs and unsigned immediate in rt");//COP1X

			add("lb", new Instruction(0x20, 0, 0, 0), InstructionType.LOAD,
					"lb[Load Byte] rt,offset(base): load byte from adress at register base + offset to rt");
			add("lh", new Instruction(0x21, 0, 0, 0), InstructionType.LOAD,
					"lh[Load Half] rt,offset(base): load half from adress at register base + offset to rt");
			add("lw", new Instruction(0x23, 0, 0, 0), InstructionType.LOAD,
					"lw[Load Word] rt,offset(base): load word from adress at register base + offset to rt");
			add("lbu",
					new Instruction(0x24, 0, 0, 0),
					InstructionType.LOAD,
					"lbu[Load Byte Unsigned] rt,offset(base): load unsigned byte from adress at register base + offset to rt");
			add("lhu",
					new Instruction(0x25, 0, 0, 0),
					InstructionType.LOAD,
					"lhu[Load Half Unsigned] rt,offset(base): load unsigned half from adress at register base + offset to rt");

			add("sb", new Instruction(0x28, 0, 0, 0), InstructionType.SAVE,
					"sb[Save Byte] rt,offset(base): save byte from rt to adress at register base + offset");
			add("sh", new Instruction(0x29, 0, 0, 0), InstructionType.SAVE,
					"sh[Save Half] rt,offset(base): save half from rt to adress at register base + offset");

			add("sw", new Instruction(0x2B, 0, 0, 0), InstructionType.SAVE,
					"sw[Save Word] rt,offset(base): save word from rt to adress at register base + offset");

			add("seqi",
					new Instruction(0x31, 0, 0, 0),
					InstructionType.ALU_IMMEDIATE,
					"seqi[Set on EQual Immediate] rt,rs,immediate: record result of equality comparison with rs and immediate in rt");//LWC1
			add("snei",
					new Instruction(0x32, 0, 0, 0),
					InstructionType.ALU_IMMEDIATE,
					"seqi[Set on Not Equal Immediate] rt,rs,immediate: record result of non-equality comparison with rs and immediate in rt");//LWC2

			add("slei",
					new Instruction(0x35, 0, 0, 0),
					InstructionType.ALU_IMMEDIATE,
					"seqi[Set on Less Equal Immediate] rt,rs,immediate: record result of less-equal comparison with rs and immediate in rt");//LDC1
			add("sgei",
					new Instruction(0x36, 0, 0, 0),
					InstructionType.ALU_IMMEDIATE,
					"sgei[Set on Greater Equal Immediate] rt,rs,immediate: record result of greater-equal comparison with rs and immediate in rt");//LDC2

			add("sequi",
					new Instruction(0x39, 0, 0, 0),
					InstructionType.ALU_IMMEDIATE,
					"sequi[Set on EQual Unsigned Immediate] rt,rs,immediate: record result of equality comparison with rs and unsigned immediate in rt");//SWC1
			add("sneui",
					new Instruction(0x3A, 0, 0, 0),
					InstructionType.ALU_IMMEDIATE,
					"sneui[Set on Not Equal Unsigned Immediate] rt,rs,immediate: record result of non-equality comparison with rs and unsigned immediate in rt");//SWC2

			add("sleui",
					new Instruction(0x3D, 0, 0, 0),
					InstructionType.ALU_IMMEDIATE,
					"sleui[Set on Less Equal Unsigned Immediate] rt,rs,immediate: record result of less-equal comparison with rs and unsigned immediate in rt");//SDC1
			add("sgeui",
					new Instruction(0x3E, 0, 0, 0),
					InstructionType.ALU_IMMEDIATE,
					"sgeui[Set on Greater Equal Unsigned Immediate] rt,rs,immediate: record result of greater-equal comparison with rs and unsigned immediate in rt");//SDC2

			//special
			add("slli",
					new Instruction(0, 0, 0, 0, 0, 0x00),
					InstructionType.SHIFT_IMMEDIATE,
					"slli[Shift word Left Logical Immediate] rd,rt,sa: do an left logical shift on rt by sa bits and save result in rd");//sll

			add("srli",
					new Instruction(0, 0, 0, 0, 0, 0x02),
					InstructionType.SHIFT_IMMEDIATE,
					"srli[Shift word Right Logical Immediate] rd,rt,sa: do an right logical shift on rt by sa bits and save result in rd");//srl
			add("srai",
					new Instruction(0, 0, 0, 0, 0, 0x03),
					InstructionType.SHIFT_IMMEDIATE,
					"srai[Shift word Right Arithmetic Immediate] rd,rt,sa: do an right arithmetic shift on rt by sa bits and save result in rd");//srai
			add("sll",
					new Instruction(0, 0, 0, 0, 0, 0x04),
					InstructionType.SHIFT_REGISTER,
					"sll[Shift word Left Logical] rd,rt,rs: do an left logical shift on rt by rs bits and save result in rd");//sllv
			add("srl",
					new Instruction(0, 0, 0, 0, 0, 0x06),
					InstructionType.SHIFT_REGISTER,
					"srl[Shift word Right Logical] rd,rt,rs: do an right logical shift on rt by rs bits and save result in rd");//srlv
			add("sra",
					new Instruction(0, 0, 0, 0, 0, 0x07),
					InstructionType.SHIFT_REGISTER,
					"sra[Shift word Right Arithmetic] rd,rt,sa: do an right artihmetic shift on rt by rs bits and save result in rd");//srav
			add("jr", new Instruction(0, 0, 0, 0, 0, 0x08), InstructionType.JUMP_REGISTER,
					"jr[Jump Register] rs: jump to rs");
			add("jalr", new Instruction(0, 0, 0, 31, 0, 0x09), InstructionType.JUMP_REGISTER,
					"jalr[Jump And Link Register] rs: save next adress in r31 and jump to target");//$rd = 31

			add("trap",
					new Instruction(0, 0, 0, 0, 0, 0x0C),
					InstructionType.TRAP,
					"trap id: execute trap with given id. Available trap ids are:\n0: terminate programm\n5: print formatted String\nAll parameters are fetched from r14");//syscall

			add("mult", new Instruction(0, 0, 0, 0, 0, 0x18), InstructionType.ALU_REGISTER,
					"mult[MULTiply word] rd,rs,rt: multiply rs and rt and save result in rd");
			add("multu", new Instruction(0, 0, 0, 0, 0, 0x19), InstructionType.ALU_REGISTER,
					"multu[MULTiply Unsigned word] rd,rs,rt: multiply rs and rt as unsigned and save result in rd");
			add("div", new Instruction(0, 0, 0, 0, 0, 0x1A), InstructionType.ALU_REGISTER,
					"div[Divide word] rd,rs,rt: divide rs and rt and save result in rd");
			add("divu", new Instruction(0, 0, 0, 0, 0, 0x1B), InstructionType.ALU_REGISTER,
					"divu[Divide Unsigned word] rd,rs,rt: divide rs and rt as unsigned and save result in rd");

			add("add", new Instruction(0, 0, 0, 0, 0, 0x20), InstructionType.ALU_REGISTER,
					"add[ADD word] rd,rs,rt: add rs and rt and save result in rd");
			add("addu", new Instruction(0, 0, 0, 0, 0, 0x21), InstructionType.ALU_REGISTER,
					"addu[ADD Unsigned word] rd,rs,rt: add rs and rt as unsigned and save result in rd");
			add("sub", new Instruction(0, 0, 0, 0, 0, 0x22), InstructionType.ALU_REGISTER,
					"sub[SUBtract word] rd,rs,rt: subtract rs and rt and save result in rd");
			add("subu", new Instruction(0, 0, 0, 0, 0, 0x23), InstructionType.ALU_REGISTER,
					"subu[SUBtract Unsigned word] rd,rs,rt: subtract rs and rt as unsigned and save result in rd");
			add("and", new Instruction(0, 0, 0, 0, 0, 0x24), InstructionType.ALU_REGISTER,
					"and[AND] rd,rs,rt: do a bitwise logical AND with rt and rs and save result in rt");
			add("or", new Instruction(0, 0, 0, 0, 0, 0x25), InstructionType.ALU_REGISTER,
					"or[OR] rd,rs,rt: do a bitwise logical OR with rt and rs and save result in rt");
			add("xor", new Instruction(0, 0, 0, 0, 0, 0x26), InstructionType.ALU_REGISTER,
					"xor[eXclusive OR] rd,rs,rt: do a bitwise logical XOR with rt and rs and save result in rt");

			add("sgt", new Instruction(0, 0, 0, 0, 0, 0x28), InstructionType.ALU_REGISTER,
					"sgt[Set on Greater Than] rd,rs,rt: record result of greater-than comparison with rs and rt in rd");
			add("sgtu",
					new Instruction(0, 0, 0, 0, 0, 0x29),
					InstructionType.ALU_REGISTER,
					"sgtu[Set on Greater Than Unsigned] rd,rs,rt: record result of unsigned greater-than comparison with rs and rt in rd");
			add("slt", new Instruction(0, 0, 0, 0, 0, 0x2A), InstructionType.ALU_REGISTER,
					"slt[Set on Less Than] rd,rs,rt: record result of less-than comparison with rs and rt in rd");
			add("sltu",
					new Instruction(0, 0, 0, 0, 0, 0x2B),
					InstructionType.ALU_REGISTER,
					"sltu[Set on Less Than Unsigned] rd,rs,rt: record result of unsigned less-than comparison with rs and rt in rd");

			add("seq", new Instruction(0, 0, 0, 0, 0, 0x35), InstructionType.ALU_REGISTER,
					"seq[Set on EQual] rd,rs,rt: record result of equal comparison with rs and rt in rd");

			add("sne", new Instruction(0, 0, 0, 0, 0, 0x37), InstructionType.ALU_REGISTER,
					"sne[Set on Not Equal] rd,rs,rt: record result of not-equal comparison with rs and rt in rd");
			add("sle", new Instruction(0, 0, 0, 0, 0, 0x38), InstructionType.ALU_REGISTER,
					"sle[Set on Less Equal] rd,rs,rt: record result of less-equal comparison with rs and rt in rd");//DSLL
			add("sge",
					new Instruction(0, 0, 0, 0, 0, 0x39),
					InstructionType.ALU_REGISTER,
					"sge[Set on Greater Equal] rd,rs,rt: record result of greater-equal comparison with rs and rt in rd");

			add("sequ",
					new Instruction(0, 0, 0, 0, 0, 0x3A),
					InstructionType.ALU_REGISTER,
					"sequ[Set on EQual Unsigned] rd,rs,rt: record result of unsigned equal comparison with rs and rt in rd");//DSRL
			add("sneu",
					new Instruction(0, 0, 0, 0, 0, 0x3B),
					InstructionType.ALU_REGISTER,
					"sneu[Set on Not Equal Unsigned] rd,rs,rt: record result of not-equal comparison with rs and rt in rd");//DSRA
			add("sleu",
					new Instruction(0, 0, 0, 0, 0, 0x3C),
					InstructionType.ALU_REGISTER,
					"sleu[Set on Less Equal Unsigned] rd,rs,rt: record result of unsigned less-equal comparison with rs and rt in rd");//DSLL32
			add("sgeu",
					new Instruction(0, 0, 0, 0, 0, 0x3D),
					InstructionType.ALU_REGISTER,
					"sgeu[Set on Grater Equal Unsigned] rd,rs,rt: record result of unsigned greater-equal comparison with rs and rt in rd");

		} catch (InstructionException e) {
			e.printStackTrace();
		}
	}

	private void add(String mnemonic, Instruction instruction, InstructionType encoding, String desc) {
		str2instr_.put(mnemonic, instruction);
		InstructionWrapper wrapper = new InstructionWrapper(instruction);
		instr2str_.put(wrapper, mnemonic);
		instr2type_.put(wrapper, encoding);
		str2desc_.put(mnemonic, desc);
	}

	/**
	 * This wrapper is used so that the test for equality is only applied on the
	 * skeleton.
	 * 
	 */
	private class InstructionWrapper {
		public Instruction instr_;

		public InstructionWrapper(Instruction instr) {
			this.instr_ = instr;
		}

		public boolean equals(Object o) {
			if (o instanceof InstructionWrapper) {
				return ((InstructionWrapper) o).instr_.equalsFamily((instr_));
			}
			return false;
		}

		public int hashCode() {
			return instr_.skeleton();
		}
	}
}
