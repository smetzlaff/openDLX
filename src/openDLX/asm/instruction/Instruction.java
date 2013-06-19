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

/**
 * <p>
 * This class is a wrapper around an instruction word Its operations access the
 * bit fields of such an instruction word.
 * </p>
 */
public class Instruction {
	private int instrWord_;

	/*
	 * ============================* Constructors *============================
	 */

	public static Instruction fromMnemonic(String mnemonic) {
		Instruction i = Instructions.instance().getInstruction(mnemonic);
		if (i != null)
			return i.clone();
		else
			return null;
	}

	/**
	 * Standard constructor
	 */
	public Instruction() {
		setInstrWord(0);
	}

	/**
	 * Constructor for instruction word
	 * 
	 * @param instrWord
	 */
	public Instruction(int instrWord) {
		setInstrWord(instrWord);
	}

	/**
	 * Constructor for IType
	 * 
	 * @param opcode
	 * @param rs
	 *            source register
	 * @param rt
	 *            destination register
	 * @param offset
	 *            immediate value
	 * @throws InstructionException
	 */
	public Instruction(int opcode, int rs, int rt, int offset) throws InstructionException {
		setOpcode(opcode);
		setRs(rs);
		setRt(rt);
		setOffset(offset);
	}

	/**
	 * Constructor for JType
	 * 
	 * @param opcode
	 * @param index
	 *            jump destination
	 * @throws InstructionException
	 */
	public Instruction(int opcode, int index) throws InstructionException {
		setOpcode(opcode);
		setInstrIndex(index);
	}

	/**
	 * Constructor for RType
	 * 
	 * @param opcode
	 * @param rs
	 *            source register
	 * @param rt
	 *            target register or regimm opcode specifier
	 * @param rd
	 *            destination register
	 * @param sa
	 *            shift amount
	 * @param function
	 *            additional opcode value for special opcode
	 * @throws InstructionException
	 */
	public Instruction(int opcode, int rs, int rt, int rd, int sa, int function)
			throws InstructionException {
		setOpcode(opcode);
		setRs(rs);
		setRt(rt);
		setRd(rd);
		setSa(sa);
		setFunction(function);
	}

	/*
	 * ============================* Getter/Setter *============================
	 */
	// ================* instrType *================

	public InstructionType calcInstType() {
		return Instructions.instance().getType(this);
	}

	// ================* opcodeType *================
	public OpcodeType opcodeType() {
		int opcode = opcode();
		if (opcode == 0) {
			return OpcodeType.Special;
		} else if (opcode == 1) {
			return OpcodeType.Regimm;
		} else {
			return OpcodeType.Normal;
		}
	}

	// ================* skeleton *================
	public int skeleton() {
		OpcodeType type = opcodeType();
		if (type == OpcodeType.Special)
			return instrWord() & 0xFC00003F;
		else if (type == OpcodeType.Regimm)
			return instrWord() & 0xFC1F0000;
		else
			return instrWord() & 0xFC000000;
	}

	// ================* instrWord *================
	public int instrWord() {
		return instrWord_;
	}

	public void setInstrWord(int instrWord) {
		this.instrWord_ = instrWord;
	}

	// ================* opcode *================
	public int opcode() {
		return (instrWord_ >> 26) & 0x3F;
	}

	public void setOpcode(int opcode) throws InstructionException {
		if (opcode > 0x3F || opcode < 0x0)
			throw new InstructionException("opcode too long");
		instrWord_ = (instrWord_ & 0x03FFFFFF) | (opcode << 26);
	}

	// ================* rs *================
	public int rs() {
		return (instrWord_ >> 21) & 0x1F;
	}

	public void setRs(int rs) throws InstructionException {
		if (rs > 0x1F || rs < 0)
			throw new InstructionException("register too long");
		instrWord_ = (instrWord_ & 0xFC1FFFFF) | (rs << 21);
	}

	// ================* base *================
	public int base() {
		return (instrWord_ >> 21) & 0x1F;
	}

	public void setBase(int base) throws InstructionException {
		if (base > 0x1F || base < 0)
			throw new InstructionException("register too long");
		instrWord_ = (instrWord_ & 0xFC1FFFFF) | (base << 21);
	}

	// ================* rt *================
	public int rt() {
		return (instrWord_ >> 16) & 0x1F;
	}

	public void setRt(int rt) throws InstructionException {
		if (rt > 0x1F || rt < 0)
			throw new InstructionException("register too long");
		instrWord_ = (instrWord_ & 0xFFE0FFFF) | (rt << 16);
	}

	// ================* rd *================
	public int rd() {
		return (instrWord_ >> 11) & 0x1F;
	}

	public void setRd(int rd) throws InstructionException {
		if (rd > 0x1F || rd < 0)
			throw new InstructionException("register too long");
		instrWord_ = (instrWord_ & 0xFFFF07FF) | (rd << 11);
	}

	// ================* sa *================
	public int sa() {
		return (instrWord_ >> 6) & 0x1F;
	}

	public void setSa(int sa) throws InstructionException {
		if (sa > 0x1F || sa < 0)
			throw new InstructionException("shift amount too long");
		instrWord_ = (instrWord_ & 0xFFFFFF3F) | (sa << 6);
	}

	// ================* function *================
	public int function() {
		return instrWord_ & 0x3F;
	}

	public void setFunction(int function) throws InstructionException {
		if (function > 0x3F || function < 0)
			throw new InstructionException("function too long");
		instrWord_ = (instrWord_ & 0xFFFFFFC0) | function;
	}

	// ================* offset *================
	public int offset() {
		return instrWord_ & 0xFFFF;
	}

	public int offset2k() {
		int offset = instrWord_ & 0x7FFF;
		if ((instrWord_ & 0x8000) != 0) {
			//TODO???
			offset = offset | 0xFFFF8000;
		}
		return offset;
	}

	public void setOffset(int offset) throws InstructionException {
		if (offset > 0xFFFF || offset < -0xFFFF)
			throw new InstructionException("offset too big");
		instrWord_ = (instrWord_ & 0xFFFF0000) | (offset & 0xFFFF);
	}

	// ================* instrIndex *================
	public int instrIndex() {
		return instrWord_ & 0x03FFFFFF;
	}

	public int instrIndex2k() {
		int index = instrWord_ & 0x01FFFFFF;
		if ((instrWord_ & 0x02000000) != 0) {
			index = index | 0xfe000000;
		}
		return index;
	}

	public void setInstrIndex(int index) throws InstructionException {
		if (index > 0x3FFFFFF || index < 0)
			throw new InstructionException("index too big");
		instrWord_ = (instrWord_ & 0xFC000000) | (index & 0x3FFFFFF);
	}

	/*
	 * ===============================* String *===============================
	 */
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		String mnemonic = toMnemonic();
		if (mnemonic != null) {
			strBuf.append(mnemonic);
		} else {
			strBuf.append("unknown");
		}
		InstructionType type = calcInstType();
		if (type != null)
			switch (type) {
			case LOAD:
				strBuf.append(' ');
				strBuf.append('r');
				strBuf.append(rt());
				strBuf.append(',');
				strBuf.append(offset2k());
				strBuf.append('(');
				strBuf.append('r');
				strBuf.append(base());
				strBuf.append(')');
				break;
			case SAVE:
				strBuf.append(' ');
				strBuf.append(offset2k());
				strBuf.append('(');
				strBuf.append('r');
				strBuf.append(base());
				strBuf.append(')');
				strBuf.append(',');
				strBuf.append('r');
				strBuf.append(rt());
				break;
			case ALU_REGISTER:
				strBuf.append(' ');
				strBuf.append('r');
				strBuf.append(rd());
				strBuf.append(',');
				strBuf.append('r');
				strBuf.append(rs());
				strBuf.append(',');
				strBuf.append('r');
				strBuf.append(rt());
				break;
			case ALU_IMMEDIATE:
				strBuf.append(' ');
				strBuf.append('r');
				strBuf.append(rt());
				strBuf.append(',');
				strBuf.append('r');
				strBuf.append(rs());
				strBuf.append(',');
				strBuf.append(offset2k());
				break;
			case BRANCH:
				strBuf.append(' ');
				strBuf.append('r');
				strBuf.append(rs());
				strBuf.append(',');
				strBuf.append("0x");
				strBuf.append(Integer.toHexString(offset2k() << 2));
				break;
			case LOAD_IMMEDIATE:
				strBuf.append(' ');
				strBuf.append(rt());
				strBuf.append(',');
				strBuf.append(offset2k());
				break;
			case JUMP:
				strBuf.append(' ');
				strBuf.append("0x");
				strBuf.append(Integer.toHexString(instrIndex() << 2));
				break;
			case JUMP_REGISTER:
				strBuf.append(' ');
				strBuf.append('r');
				strBuf.append(rs());
				break;
			case SHIFT_IMMEDIATE:
				strBuf.append(' ');
				strBuf.append('r');
				strBuf.append(rd());
				strBuf.append(',');
				strBuf.append('r');
				strBuf.append(rt());
				strBuf.append(',');
				strBuf.append(sa());
				break;
			case TRAP:
				strBuf.append(' ');
				strBuf.append(rs());
				break;
			case NOP:
				//"nop" already in strBuf
			default:
			}
		return strBuf.toString();
	}

	public String toMnemonic() {
		return Instructions.instance().getMnemonic(this);
	}

	public String toHexString() {
		String hex = Integer.toHexString(instrWord());
		//leading zeros
		for (int diff = 8 - hex.length(); diff > 0; diff--) {
			hex = '0' + hex;
		}
		hex = "0x" + hex;

		return hex;
	}

	/*public String toFormatedBinString(InstructionType type) {
		// String printStr = toString() + "\n";
		String printStr = new String();
		int mask = 1;
		switch (type) {
		case IType:
			for (int i = 32; i > 0; i--) {
				if (i == 6 || i == 11 || i == 16)
					printStr = ' ' + printStr;
				if ((instrWord_ & mask) == 0)
					printStr = '0' + printStr;
				else
					printStr = '1' + printStr;
				mask = mask << 1;
			}
			printStr = "\n+-----------------------------------+\n"
					+ "|opcode  rs    rt        offset     |\n|" + printStr
					+ "|\n+-----------------------------------+\n";
			break;
		case JType:
			for (int i = 0; i < 32; i++) {
				if (i == 26)
					printStr = ' ' + printStr;
				if ((instrWord_ & mask) == 0)
					printStr = '0' + printStr;
				else
					printStr = '1' + printStr;
				mask = mask << 1;
			}
			printStr = "\n+---------------------------------+\n"
					+ "|opcode        instr_index        |\n|" + printStr
					+ "|\n+---------------------------------+\n";
			break;
		case RType:
			for (int i = 0; i < 32; i++) {
				if (i == 6 || i == 11 || i == 16 || i == 21 || i == 26)
					printStr = ' ' + printStr;
				if ((instrWord_ & mask) == 0)
					printStr = '0' + printStr;
				else
					printStr = '1' + printStr;
				mask = mask << 1;
			}
			printStr = "\n+-------------------------------------+\n"
					+ "|opcode  rs    rt    rd    sa    func |\n|" + printStr
					+ "|\n+-------------------------------------+\n";
			break;
		case Unknown:
			for (int i = 0; i < 32; i++) {
				if (i % 4 == 0 && i != 0)
					printStr = ' ' + printStr;
				if ((instrWord_ & mask) == 0)
					printStr = '0' + printStr;
				else
					printStr = '1' + printStr;
				mask = mask << 1;
			}
			printStr = "\n+---------------------------------------+\n|" + printStr
					+ "|\n+---------------------------------------+\n";
		}

		return printStr;
	}*/

	/*
	 * ==============================* Equality *==============================
	 */
	public boolean equals(Object o) {
		if (o instanceof Instruction) {
			Instruction i = (Instruction) o;
			if (i.instrWord() == instrWord())
				return true;
		}
		return false;
	}

	/**
	 * true if i is the same mnemonic e.g. "<b>add r1,r0,r0</b>" and
	 * "<b>add r9,r8,r7</b>"
	 * 
	 * @param i
	 * @return
	 */
	public boolean equalsFamily(Instruction i) {
		if (i.skeleton() == skeleton())
			return true;
		return false;
	}

	public Instruction clone() {
		return new Instruction(instrWord());
	}
}
