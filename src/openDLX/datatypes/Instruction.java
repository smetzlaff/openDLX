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
package openDLX.datatypes;


import openDLX.PipelineConstants;
import openDLX.exception.PipelineDataTypeException;

public class Instruction
{
	private uint32 instr;
	private uint8 opcode;
	private uint8 function;
	private uint8 rs;
	private uint8 rt;
	private uint8 rd;
	private uint8 sa;
	private uint16 offset;
	private uint32 instr_index;

	private InstructionType type;
	private OpcodeNORMAL op_normal;
	private OpcodeSPECIAL op_special;
	private OpcodeREGIMM op_regimm;
	
	private MemoryWidth mem_width;

	private boolean load;
	private boolean store;
	private boolean read_rs;
	private boolean read_rt;
	private boolean read_hi;
	private boolean read_lo;
	private boolean write_rt;
	private boolean write_rd;
	private boolean write_hi;
	private boolean write_lo;
	private boolean use_immediate;
	private boolean use_shift_amount;
	private boolean use_instr_index;
	private boolean branch;
	private boolean branch_likely;
	private boolean branch_and_link;
	private ALUFunction alu_func;
	private ALUPort alu_port_a;
	private ALUPort alu_port_b;
	private BranchCondition branch_cond;
	
	private ImmExtend imm_extend;
	
	private byte read_regs;
	private byte write_regs;
	private BranchCtrlPort branch_ctrl_port_a;
	private BranchCtrlPort branch_ctrl_port_b;

	/**
	 * @param instr
	 */
	public Instruction(uint32 instr)
	{
		this.instr = instr;
		opcode = new uint8();
		function = new uint8();
		rs = new uint8();
		rt = new uint8();
		rd = new uint8();
		sa = new uint8();
		offset = new uint16();
		instr_index = new uint32();

		type = InstructionType.UNKNOWN;
		op_normal = OpcodeNORMAL.UNKNOWN;
		op_special = OpcodeSPECIAL.UNKNOWN;
		op_regimm = OpcodeREGIMM.UNKNOWN;
		
		alu_func = ALUFunction.NOP;
		alu_port_a = ALUPort.ZERO;
		alu_port_b = ALUPort.ZERO;
		
		imm_extend = ImmExtend.UNKNOWN;
		
		branch_cond = BranchCondition.NO_BRANCH;
		branch_ctrl_port_a = BranchCtrlPort.ZERO;
		branch_ctrl_port_b = BranchCtrlPort.ZERO;

		// by default do not load/store
		load = false;
		store = false;
		
		mem_width = MemoryWidth.WORD;

		// by default do not read or write any register
		read_rs = false;
		read_rt = false;
		write_rt = false;
		write_rd = false;

		// by default do not use immediates or shift amounts
		use_immediate = false;
		use_shift_amount = false;
		use_instr_index = false;
		
		// by default do not branch
		branch = false;
		branch_likely = false;
		branch_and_link = false;
		
		// by default no special register is read nor written
		read_lo = false;
		read_hi = false;
		write_lo = false;
		write_hi = false;
		
		read_regs = 0;
		write_regs = 0;

		chopInstruction();
	}

	private void chopInstruction()
	{
		opcode.setValue((byte) ((instr.getValue() >> 26) & 0x3F));
		function.setValue((byte) (instr.getValue() & 0x3F));
		offset.setValue((short) (instr.getValue() & 0xFFFF));
		instr_index.setValue(instr.getValue() & 0x1FFFFFF);
		sa.setValue((byte) ((instr.getValue() >> 6) & 0x1F));
		rd.setValue((byte) ((instr.getValue() >> 11) & 0x1F));
		rt.setValue((byte) ((instr.getValue() >> 16) & 0x1F));
		rs.setValue((byte) ((instr.getValue() >> 21) & 0x1F));

		//logger.trace("Opcode is: " + opcode.getHex() + " Special: "
			//	+ function.getHex() + " Regimm: " + rt.getHex());
	}

	/**
	 * Returns the type of the instruction.
	 * @see InstructionType
	 * @return The type of the instruction.
	 */
	public InstructionType getType()
	{
		return type;
	}

	public void setType(InstructionType type)
	{
		this.type = type;
	}

	public OpcodeNORMAL getOpNormal()
	{
		return op_normal;
	}

	public void setOpNormal(OpcodeNORMAL opNormal)
	{
		this.op_normal = opNormal;
	}

	public OpcodeSPECIAL getOpSpecial()
	{
		return op_special;
	}

	public void setOpSpecial(OpcodeSPECIAL opSpecial)
	{
		op_special = opSpecial;
	}

	public OpcodeREGIMM getOpRegimm()
	{
		return op_regimm;
	}

	public void setOpRegimm(OpcodeREGIMM opRegimm)
	{
		op_regimm = opRegimm;
	}

	public uint32 getInstr()
	{
		return instr;
	}

	public uint8 getOpcode()
	{
		return opcode;
	}

	public uint8 getFunction()
	{
		return function;
	}
	
	public uint8 getRegimm()
	{
		return rt;
	}

	public uint16 getOffset()
	{
		return offset;
	}

	public uint32 getInstrIndex()
	{
		return instr_index;
	}

	public void setReadRs(boolean b)
	{
		if(b && !read_rs)
		{
			read_regs++;
		}
		read_rs = b;
	}

	public boolean getReadRs()
	{
		return read_rs;
	}

	public void setReadRt(boolean b)
	{
		if(b && !read_rt)
		{
			read_regs++;
		}
		read_rt = b;
	}

	public boolean getReadRt()
	{
		return read_rt;
	}

	public void setWriteRd(boolean b)
	{
		if(b && !write_rd)
		{
			write_regs ++;
		}
		write_rd = b;
	}

	public boolean getWriteRd()
	{
		return write_rd;
	}

	public void setWriteRt(boolean b)
	{
		if(b && !write_rt)
		{
			write_regs ++;
		}
		write_rt = b;
	}

	public boolean getWriteRt()
	{
		return write_rt;
	}

	public boolean getReadHI()
	{
		return read_hi;
	}

	public void setReadHI(boolean b)
	{
		if(b && !read_hi)
		{
			read_regs ++;
		}
		read_hi = b;
	}

	public boolean getReadLO()
	{
		return read_lo;
	}

	public void setReadLO(boolean b)
	{
		if(b && !read_lo)
		{
			read_regs ++;
		}
		read_lo = b;
	}

	
	public boolean getWriteHI()
	{
		return write_hi;
	}

	public void setWriteHI(boolean b)
	{
		if(b && !write_hi)
		{
			write_regs ++;
		}
		write_hi = b;
	}

	public boolean getWriteLO()
	{
		return write_lo;
	}

	public void setWriteLO(boolean b)
	{
		if(b && !write_lo)
		{
			write_regs ++;
		}
		write_lo = b;
	}

	public void setUseImmediate(boolean b)
	{
		use_immediate = b;
	}

	public boolean getUseImmediate()
	{
		return use_immediate;
	}

	public void setUseShiftAmount(boolean b)
	{
		use_shift_amount = b;
	}

	public boolean getUseShiftAmount()
	{
		return use_shift_amount;
	}

	public void setLoad(boolean b)
	{
		load = b;
	}

	public boolean getLoad()
	{
		return load;
	}

	public void setStore(boolean b)
	{
		store = b;
	}

	public boolean getStore()
	{
		return store;
	}

	public void setBranch(boolean b)
	{
		branch = b;
	}
	
	public boolean getBranch()
	{
		return branch;
	}
	
	public void setBranchLikely(boolean b)
	{
		branch_likely = b;
	}

	public boolean getBranchLikely()
	{
		return branch_likely;
	}
	
	public void setALUFunction(ALUFunction fnct)
	{
		alu_func = fnct;
	}
	
	public ALUFunction getALUFunction()
	{
		return alu_func;
	}

	public uint8 getRs()
	{
		return rs;
	}

	public uint8 getRt()
	{
		return rt;
	}

	public uint8 getRd()
	{
		return rd;
	}

	public uint8 getSa()
	{
		return sa;
	}

	public byte getRegReadCount()
	{
		return read_regs;
	}

	public byte getRegWriteCount()
	{
		return write_regs;
	}


	public void setALUPortA(ALUPort port)
	{
		alu_port_a = port;
	}
	
	public ALUPort getALUPortA()
	{
		return alu_port_a;
	}
	

	public void setALUPortB(ALUPort port)
	{
		alu_port_b = port;
	}
	
	public ALUPort getALUPortB()
	{
		return alu_port_b;
	}
	
	public void setBranchCondition(BranchCondition cond)
	{
		branch_cond = cond;
	}
	
	public BranchCondition getBranchCondition()
	{
		return branch_cond;
	}

	public void setBranchPortA(BranchCtrlPort port)
	{
		branch_ctrl_port_a = port;
	}
	
	public BranchCtrlPort getBrachControlPortA()
	{
		return branch_ctrl_port_a;
	}

	public void setBranchPortB(BranchCtrlPort port)
	{
		branch_ctrl_port_b = port;
	}
	
	public BranchCtrlPort getBrachControlPortB()
	{
		return branch_ctrl_port_b;
	}
	
	public void setUseInstrIndex(boolean b)
	{
		use_instr_index = true; 
	}

	public boolean getUseInstrIndex()
	{
		return use_instr_index; 
	}
	
	public void setBranchAndLink(boolean b)
	{
		branch_and_link = b;
		
	}
	
	public boolean getBranchAndLink()
	{
		return branch_and_link;
	}
	
	public void setWriteRA(boolean b) throws PipelineDataTypeException
	{
		if(write_rd == true)
		{
			throw new PipelineDataTypeException("Return address already set.");
		}
		
		if(b)
		{
			// if writing the return address set the RD value to the RA register, which is 31
			rd.setValue(PipelineConstants.REG_RA);
			write_rd = true;
		}
		else
		{
			throw new PipelineDataTypeException("Cannot reset return address.");
		}
	}
	
	public void setReadKernelRegisters(boolean b) throws PipelineDataTypeException
	{
		if(read_rs == true || read_rt == true)
		{
			throw new PipelineDataTypeException("MIPS kernel registers already set.");
		}

		if(b)
		{
			rs.setValue(PipelineConstants.REG_K0);
			read_rs = true;
			rt.setValue(PipelineConstants.REG_K1);
			read_rt = true;
		}
		else
		{
			throw new PipelineDataTypeException("Cannot reset kernel registers.");
		}
	}
	
	public void setReadDLXTrapParameterRegister(boolean b) throws PipelineDataTypeException
	{
		if(read_rt == true)
		{
			throw new PipelineDataTypeException("DLX trap parameters already set.");
		}

		if(b)
		{
			rt.setValue(PipelineConstants.REG_R14);
			read_rt = true;
		}
		else
		{
			throw new PipelineDataTypeException("Cannot reset trap parameters.");
		}
	}
	
	public void setWriteDLXTrapResultRegister(boolean b) throws PipelineDataTypeException
	{
		if(write_rd == true)
		{
			throw new PipelineDataTypeException("DLX trap result register parameter already set.");
		}

		if(b)
		{
			rd.setValue(PipelineConstants.REG_R1);
			write_rd = true;
		}
		else
		{
			throw new PipelineDataTypeException("Cannot reset trap result parameters.");
		}
	}


	public void setMemoryWidth(MemoryWidth mem_width)
	{
		this.mem_width = mem_width;
	}
	
	public MemoryWidth getMemoryWidth()
	{
		return mem_width;
	}
	
	public void setImmExtend(ImmExtend imm_extend)
	{
		this.imm_extend = imm_extend;
	}
	
	public ImmExtend getImmExtend()
	{
		return imm_extend;
	}

	public String getString()
	{
		String s;
		s = getOpcodeName()
				+ " ALU:" + getALUFunction()
				+ " rRs:"
				+ getReadRs()
				+ ((getReadRs()) ? (" (" + rs.getValue() + "/" + ArchCfg.getRegisterDescription(rs.getValue()) + ")") : (""))
				+ " rRt:"
				+ getReadRt()
				+ ((getReadRt()) ? (" (" + rt.getValue() + "/" + ArchCfg.getRegisterDescription(rt.getValue()) + ")") : (""))
				+ " wRt:"
				+ getWriteRt()
				+ ((getWriteRt()) ? (" (" + rt.getValue() + "/" + ArchCfg.getRegisterDescription(rt.getValue()) + ")") : (""))
				+ " wRd:"
				+ getWriteRd()
				+ ((getWriteRd()) ? (" (" + rd.getValue() + "/" + ArchCfg.getRegisterDescription(rd.getValue()) + ")") : (""))
				+ " uIMM:"
				+ getUseImmediate() 
				+ ((getUseImmediate()) ? (" (" + offset.getValue() + ")")
						: (""))
				+ " uSA:" + getUseShiftAmount() + ((getUseShiftAmount())?(" (" + sa.getValue() + ")"):(""))
				+ " uIDX:" + getUseInstrIndex() + ((getUseInstrIndex())?(" (" + instr_index.getValueAsHexString() + ")"):(""))
				+ " LD:" + getLoad() + " ST:" + getStore() + " BR:" + getBranch();

		return s;
	}
	
	private String getOpcodeName()
	{
		String s;
		
		if(getOpNormal() == OpcodeNORMAL.SPECIAL)
		{
			s = ""+getOpSpecial();
		}
		else if(getOpNormal() == OpcodeNORMAL.REGIMM)
		{
			s = ""+getOpRegimm();
		}
		else
		{
			s = ""+getOpNormal();
		}
		
		return s;
	}

}
