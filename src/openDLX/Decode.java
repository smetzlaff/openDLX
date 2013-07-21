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
package openDLX;

import java.util.Queue;

import org.apache.log4j.Logger;

import openDLX.datatypes.*;
import openDLX.exception.CacheException;
import openDLX.exception.DecodeStageException;
import openDLX.exception.PipelineDataTypeException;
import openDLX.exception.UnknownInstructionException;


public class Decode
{
	private static Logger logger = Logger.getLogger("DECODE");
	private Instruction current_inst;
	private RegisterSet reg_set;
	private Queue<FetchDecodeData> fetch_decode_latch;
	
	private final boolean throwExceptionForUntestedInstructions = true;
	
	public Decode(RegisterSet reg_set)
	{
		this.reg_set = reg_set;
	}

	Instruction decodeInstr(uint32 instr) throws UnknownInstructionException, CacheException, PipelineDataTypeException
	{
		current_inst = new Instruction(instr);
		
		if(instr.getValue() == 0)
		{
			// this is a nop
			current_inst.setOpNormal(OpcodeNORMAL.NOP);
			current_inst.setALUFunction(ALUFunction.NOP);
			current_inst.setALUPortA(ALUPort.ZERO);
			current_inst.setALUPortB(ALUPort.ZERO);
		}
		else
		{
			OpcodeNORMAL op_norm = determineOpcode();
			if (op_norm == OpcodeNORMAL.SPECIAL)
			{
				determineFunction();
			}
			else if (op_norm == OpcodeNORMAL.REGIMM)
			{
				determineRegimm();
			}
		}

		return current_inst;
	}

	private OpcodeNORMAL determineOpcode() throws UnknownInstructionException, CacheException, PipelineDataTypeException
	{
		OpcodeNORMAL op;

		switch (current_inst.getOpcode().getValue() & 0x3F)
		{
		// bits 31..29 000
		case 0x00:
			op = (OpcodeNORMAL.SPECIAL);
			break;
		case 0x01:
			op = (OpcodeNORMAL.REGIMM);
			break;
		case 0x02:
			op = (OpcodeNORMAL.J);
			current_inst.setType(InstructionType.JTYPE);
			
			current_inst.setUseInstrIndex(true);
			
			current_inst.setALUFunction(ALUFunction.BA);
			current_inst.setALUPortA(ALUPort.PC);
			current_inst.setALUPortB(ALUPort.IDX);
			
			current_inst.setBranch(true);
			current_inst.setBranchCondition(BranchCondition.UNCOND);
			current_inst.setBranchPortA(BranchCtrlPort.ZERO);
			current_inst.setBranchPortB(BranchCtrlPort.ZERO);
			break;
		case 0x03:
			op = (OpcodeNORMAL.JAL);
			current_inst.setType(InstructionType.JTYPE);
			
			current_inst.setUseInstrIndex(true);
			current_inst.setWriteRA(true);
			
			current_inst.setALUFunction(ALUFunction.BA);
			current_inst.setALUPortA(ALUPort.PC);
			current_inst.setALUPortB(ALUPort.IDX);
			
			current_inst.setBranch(true);
			current_inst.setBranchAndLink(true);
			current_inst.setBranchCondition(BranchCondition.UNCOND);
			current_inst.setBranchPortA(BranchCtrlPort.ZERO);
			current_inst.setBranchPortB(BranchCtrlPort.ZERO);
			break;
		case 0x04:
			op = (OpcodeNORMAL.BEQ);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setReadRt(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			
			current_inst.setALUFunction(ALUFunction.BR);
			current_inst.setALUPortA(ALUPort.PC);
			current_inst.setALUPortB(ALUPort.IMM);
			
			current_inst.setBranch(true);
			current_inst.setBranchCondition(BranchCondition.BEQ);
			current_inst.setBranchPortA(BranchCtrlPort.RS);
			current_inst.setBranchPortB(BranchCtrlPort.RT);
			
			break;
		case 0x05:
			op = (OpcodeNORMAL.BNE);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setReadRt(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			
			current_inst.setALUFunction(ALUFunction.BR);
			current_inst.setALUPortA(ALUPort.PC);
			current_inst.setALUPortB(ALUPort.IMM);
			
			current_inst.setBranch(true);
			current_inst.setBranchCondition(BranchCondition.BNE);
			current_inst.setBranchPortA(BranchCtrlPort.RS);
			current_inst.setBranchPortB(BranchCtrlPort.RT);
			break;
		case 0x06:
			op = (OpcodeNORMAL.BLEZ);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			
			current_inst.setALUFunction(ALUFunction.BR);
			current_inst.setALUPortA(ALUPort.PC);
			current_inst.setALUPortB(ALUPort.IMM);
			
			current_inst.setBranch(true);
			current_inst.setBranchCondition(BranchCondition.BLEZ);
			current_inst.setBranchPortA(BranchCtrlPort.RS);
			current_inst.setBranchPortB(BranchCtrlPort.ZERO);
			break;
		case 0x07:
			op = (OpcodeNORMAL.BGTZ);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			
			current_inst.setALUFunction(ALUFunction.BR);
			current_inst.setALUPortA(ALUPort.PC);
			current_inst.setALUPortB(ALUPort.IMM);
			
			current_inst.setBranch(true);
			current_inst.setBranchCondition(BranchCondition.BGTZ);
			current_inst.setBranchPortA(BranchCtrlPort.RS);
			current_inst.setBranchPortB(BranchCtrlPort.ZERO);
			
			
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;

		// bits 31..29 001
		case 0x08:
			op = (OpcodeNORMAL.ADDI);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setWriteRt(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			
			current_inst.setALUFunction(ALUFunction.ADD);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.IMM);
			break;
		case 0x09:
			op = (OpcodeNORMAL.ADDIU);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setWriteRt(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			
			current_inst.setALUFunction(ALUFunction.ADDU);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.IMM);
			break;
		case 0x0A:
			op = (OpcodeNORMAL.SLTI);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setWriteRt(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			
			current_inst.setALUFunction(ALUFunction.SLT);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.IMM);
			break;
		case 0x0B:
			op = (OpcodeNORMAL.SLTIU);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setWriteRt(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			
			current_inst.setALUFunction(ALUFunction.SLTU);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.IMM);
			break;
		case 0x0C:
			op = (OpcodeNORMAL.ANDI);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setWriteRt(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.ZERO);
			
			current_inst.setALUFunction(ALUFunction.AND);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.IMM);
			break;
		case 0x0D:
			op = (OpcodeNORMAL.ORI);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setWriteRt(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.ZERO);
			
			current_inst.setALUFunction(ALUFunction.OR);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.IMM);
			break;
		case 0x0E:
			op = (OpcodeNORMAL.XORI);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setWriteRt(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.ZERO);
			
			current_inst.setALUFunction(ALUFunction.XOR);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.IMM);
			break;
		case 0x0F:
			op = (OpcodeNORMAL.LUI);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setWriteRt(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			
			current_inst.setALUFunction(ALUFunction.LUI);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.IMM);
			break;

		// bits 31..29 010
		case 0x10:
			switch(ArchCfg.isa_type)
			{
			case DLX:
				// use COP0 for implementation of SUBI (because is normal opcode like ADDI and is one row below ADDI)
				op = (OpcodeNORMAL.SUBI);
				current_inst.setType(InstructionType.ITYPE);

				current_inst.setReadRs(true);
				current_inst.setWriteRt(true);
				current_inst.setUseImmediate(true);
				current_inst.setImmExtend(ImmExtend.SIGN);

				current_inst.setALUFunction(ALUFunction.SUB);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.IMM);
				break;
			case MIPS:
			default:
				op = (OpcodeNORMAL.COP0);
				logger.error("Instruction " + op + " not yet supported.");
				if(throwExceptionForUntestedInstructions)
				{
					throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
				}
			}
			break;
		case 0x11:
			switch(ArchCfg.isa_type)
			{
			case DLX:
				// use COP1 for implementation of SUBIU (because is normal opcode like ADDIU and is one row below ADDIU)
				op = (OpcodeNORMAL.SUBIU);
				current_inst.setType(InstructionType.ITYPE);

				current_inst.setReadRs(true);
				current_inst.setWriteRt(true);
				current_inst.setUseImmediate(true);
				current_inst.setImmExtend(ImmExtend.SIGN);

				current_inst.setALUFunction(ALUFunction.SUBU);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.IMM);
				break;
			case MIPS:
			default:
				op = (OpcodeNORMAL.COP1);
				logger.error("Instruction " + op + " not yet supported.");
				if(throwExceptionForUntestedInstructions)
				{
					throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
				}
			}
			break;
		case 0x12:
			switch(ArchCfg.isa_type)
			{
			case DLX:
				// use COP2 for implementation of SGTI
				op = (OpcodeNORMAL.SGTI);
				current_inst.setType(InstructionType.ITYPE);

				current_inst.setReadRs(true);
				current_inst.setWriteRt(true);
				current_inst.setUseImmediate(true);
				current_inst.setImmExtend(ImmExtend.SIGN);

				current_inst.setALUFunction(ALUFunction.SGT);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.IMM);
				break;
			case MIPS:
			default:
				op = (OpcodeNORMAL.COP2);
				logger.error("Instruction " + op + " not yet supported.");
				if(throwExceptionForUntestedInstructions)
				{
					throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
				}
			}
			break;
		case 0x13:
			switch(ArchCfg.isa_type)
			{
			case DLX:
				// use COP1X for implementation of SGTIU
				op = (OpcodeNORMAL.SGTIU);
				current_inst.setType(InstructionType.ITYPE);

				current_inst.setReadRs(true);
				current_inst.setWriteRt(true);
				current_inst.setUseImmediate(true);
				current_inst.setImmExtend(ImmExtend.SIGN);

				current_inst.setALUFunction(ALUFunction.SGTU);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.IMM);
				break;
			case MIPS:
			default:
				op = (OpcodeNORMAL.COP1X);
				logger.error("Instruction " + op + " not yet supported.");
				if(throwExceptionForUntestedInstructions)
				{
					throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
				}
			}
			break;
		case 0x14:
			op = (OpcodeNORMAL.BEQL);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setReadRt(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			
			current_inst.setALUFunction(ALUFunction.BR);
			current_inst.setALUPortA(ALUPort.PC);
			current_inst.setALUPortB(ALUPort.IMM);
			
			current_inst.setBranch(true);
			current_inst.setBranchLikely(true);
			current_inst.setBranchCondition(BranchCondition.BEQ);
			current_inst.setBranchPortA(BranchCtrlPort.RS);
			current_inst.setBranchPortB(BranchCtrlPort.RT);
			break;
		case 0x15:
			op = (OpcodeNORMAL.BNEL);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setReadRt(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			
			current_inst.setALUFunction(ALUFunction.BR);
			current_inst.setALUPortA(ALUPort.PC);
			current_inst.setALUPortB(ALUPort.IMM);
			
			current_inst.setBranch(true);
			current_inst.setBranchLikely(true);
			current_inst.setBranchCondition(BranchCondition.BNE);
			current_inst.setBranchPortA(BranchCtrlPort.RS);
			current_inst.setBranchPortB(BranchCtrlPort.RT);
			break;
		case 0x16:
			op = (OpcodeNORMAL.BLEZL);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			
			current_inst.setALUFunction(ALUFunction.BR);
			current_inst.setALUPortA(ALUPort.PC);
			current_inst.setALUPortB(ALUPort.IMM);
			
			current_inst.setBranch(true);
			current_inst.setBranchLikely(true);
			current_inst.setBranchCondition(BranchCondition.BLEZ);
			current_inst.setBranchPortA(BranchCtrlPort.RS);
			current_inst.setBranchPortB(BranchCtrlPort.ZERO);
			
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x17:
			op = (OpcodeNORMAL.BGTZL);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			
			current_inst.setALUFunction(ALUFunction.BR);
			current_inst.setALUPortA(ALUPort.PC);
			current_inst.setALUPortB(ALUPort.IMM);
			
			current_inst.setBranch(true);
			current_inst.setBranchLikely(true);
			current_inst.setBranchCondition(BranchCondition.BGTZ);
			current_inst.setBranchPortA(BranchCtrlPort.RS);
			current_inst.setBranchPortB(BranchCtrlPort.ZERO);
			break;

		// bits 31..29 011
		case 0x18:
			op = (OpcodeNORMAL.DADDI);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x19:
			op = (OpcodeNORMAL.DADDIU);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x1A:
			op = (OpcodeNORMAL.LDL);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x1B:
			op = (OpcodeNORMAL.LDR);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x1C:
		case 0x1D:
		case 0x1E:
		case 0x1F:
			op = (OpcodeNORMAL.UNKNOWN);
			break;

		// bits 31..29 100
		case 0x20:
			op = (OpcodeNORMAL.LB);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setWriteRt(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			current_inst.setLoad(true);
			current_inst.setMemoryWidth(MemoryWidth.BYTE);
			
			current_inst.setALUFunction(ALUFunction.ADD);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.IMM);
			break;
		case 0x21:
			op = (OpcodeNORMAL.LH);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x22:
			op = (OpcodeNORMAL.LWL);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x23:
			op = (OpcodeNORMAL.LW);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setWriteRt(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			current_inst.setLoad(true);
			current_inst.setMemoryWidth(MemoryWidth.WORD);
			
			current_inst.setALUFunction(ALUFunction.ADD);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.IMM);
			break;
		case 0x24:
			op = (OpcodeNORMAL.LBU);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setWriteRt(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			current_inst.setLoad(true);
			current_inst.setMemoryWidth(MemoryWidth.UBYTE);
			
			current_inst.setALUFunction(ALUFunction.ADD);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.IMM);
			break;
		case 0x25:
			op = (OpcodeNORMAL.LHU);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x26:
			op = (OpcodeNORMAL.LWR);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x27:
			op = (OpcodeNORMAL.LWU);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;

		// bits 31..29 101
		case 0x28:
			op = (OpcodeNORMAL.SB);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setReadRt(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			current_inst.setStore(true);
			current_inst.setMemoryWidth(MemoryWidth.BYTE);
			
			current_inst.setALUFunction(ALUFunction.ADD);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.IMM);
			break;
		case 0x29:
			op = (OpcodeNORMAL.SH);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x2A:
			op = (OpcodeNORMAL.SWL);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setReadRt(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			current_inst.setStore(true);
			current_inst.setMemoryWidth(MemoryWidth.WORD_LEFT_PART);
			
			current_inst.setALUFunction(ALUFunction.ADD);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.IMM);
			
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x2B:
			op = (OpcodeNORMAL.SW);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setReadRt(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			current_inst.setStore(true);
			current_inst.setMemoryWidth(MemoryWidth.WORD);
			
			current_inst.setALUFunction(ALUFunction.ADD);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.IMM);
			break;
		case 0x2C:
			op = (OpcodeNORMAL.SDL);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x2D:
			op = (OpcodeNORMAL.SDR);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x2E:
			op = (OpcodeNORMAL.SWR);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setReadRt(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			current_inst.setStore(true);
			current_inst.setMemoryWidth(MemoryWidth.WORD_RIGHT_PART);
			
			current_inst.setALUFunction(ALUFunction.ADD);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.IMM);
			break;
		case 0x2F:
			op = (OpcodeNORMAL.UNKNOWN);
			break;

		// bits 31..29 110
		case 0x30:
			op = (OpcodeNORMAL.LL);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x31:
			switch(ArchCfg.isa_type)
			{
			case DLX:
				// use LWC1 for implementation of SEQI
				op = (OpcodeNORMAL.SEQI);
				current_inst.setType(InstructionType.ITYPE);

				current_inst.setReadRs(true);
				current_inst.setWriteRt(true);
				current_inst.setUseImmediate(true);
				current_inst.setImmExtend(ImmExtend.SIGN);

				current_inst.setALUFunction(ALUFunction.SEQ);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.IMM);
				break;
			case MIPS:
			default:
				op = (OpcodeNORMAL.LWC1);
				logger.error("Instruction " + op + " not yet supported.");
				if(throwExceptionForUntestedInstructions)
				{
					throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
				}
			}
			break;
		case 0x32:
			switch(ArchCfg.isa_type)
			{
			case DLX:
				// use LWC2 for implementation of SNEI
				op = (OpcodeNORMAL.SNEI);
				current_inst.setType(InstructionType.ITYPE);

				current_inst.setReadRs(true);
				current_inst.setWriteRt(true);
				current_inst.setUseImmediate(true);
				current_inst.setImmExtend(ImmExtend.SIGN);

				current_inst.setALUFunction(ALUFunction.SNE);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.IMM);
				break;
			case MIPS:
			default:
				op = (OpcodeNORMAL.LWC2);
				logger.error("Instruction " + op + " not yet supported.");
				if(throwExceptionForUntestedInstructions)
				{
					throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
				}
			}
			break;
		case 0x33:
			op = (OpcodeNORMAL.PREF);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x34:
			op = (OpcodeNORMAL.LLD);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x35:
			switch(ArchCfg.isa_type)
			{
			case DLX:
				// use LDC1 for implementation of SLEI
				op = (OpcodeNORMAL.SLEI);
				current_inst.setType(InstructionType.ITYPE);

				current_inst.setReadRs(true);
				current_inst.setWriteRt(true);
				current_inst.setUseImmediate(true);
				current_inst.setImmExtend(ImmExtend.SIGN);

				current_inst.setALUFunction(ALUFunction.SLE);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.IMM);
				break;
			case MIPS:
			default:
				op = (OpcodeNORMAL.LDC1);
				logger.error("Instruction " + op + " not yet supported.");
				if(throwExceptionForUntestedInstructions)
				{
					throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
				}
			}
			break;
		case 0x36:
			switch(ArchCfg.isa_type)
			{
			case DLX:
				// use LDC2 for implementation of SGEI
				op = (OpcodeNORMAL.SGEI);
				current_inst.setType(InstructionType.ITYPE);

				current_inst.setReadRs(true);
				current_inst.setWriteRt(true);
				current_inst.setUseImmediate(true);
				current_inst.setImmExtend(ImmExtend.SIGN);

				current_inst.setALUFunction(ALUFunction.SGE);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.IMM);
				break;
			case MIPS:
			default:
				op = (OpcodeNORMAL.LDC2);
				logger.error("Instruction " + op + " not yet supported.");
				if(throwExceptionForUntestedInstructions)
				{
					throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
				}
			}
			break;
		case 0x37:
			op = (OpcodeNORMAL.LD);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;

			// bits 31..29 111
		case 0x38:
			op = (OpcodeNORMAL.SC);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x39:
			switch(ArchCfg.isa_type)
			{
			case DLX:
				// use SWC1 for implementation of SEQIU
				op = (OpcodeNORMAL.SEQIU);
				current_inst.setType(InstructionType.ITYPE);

				current_inst.setReadRs(true);
				current_inst.setWriteRt(true);
				current_inst.setUseImmediate(true);
				current_inst.setImmExtend(ImmExtend.SIGN);

				current_inst.setALUFunction(ALUFunction.SEQU);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.IMM);
				break;
			case MIPS:
			default:
				op = (OpcodeNORMAL.SWC1);
				logger.error("Instruction " + op + " not yet supported.");
				if(throwExceptionForUntestedInstructions)
				{
					throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
				}
			}
			break;
		case 0x3A:
			switch(ArchCfg.isa_type)
			{
			case DLX:
				// use SWC2 for implementation of SNEIU
				op = (OpcodeNORMAL.SNEIU);
				current_inst.setType(InstructionType.ITYPE);

				current_inst.setReadRs(true);
				current_inst.setWriteRt(true);
				current_inst.setUseImmediate(true);
				current_inst.setImmExtend(ImmExtend.SIGN);

				current_inst.setALUFunction(ALUFunction.SNEU);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.IMM);
				break;
			case MIPS:
			default:
				op = (OpcodeNORMAL.SWC2);
				logger.error("Instruction " + op + " not yet supported.");
				if(throwExceptionForUntestedInstructions)
				{
					throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
				}
			}
			break;
		case 0x3B:
			op = (OpcodeNORMAL.UNKNOWN);
			break;
		case 0x3C:
			op = (OpcodeNORMAL.SCD);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x3D:
			switch(ArchCfg.isa_type)
			{
			case DLX:
				// use SDC1 for implementation of SLEIU
				op = (OpcodeNORMAL.SLEIU);
				current_inst.setType(InstructionType.ITYPE);

				current_inst.setReadRs(true);
				current_inst.setWriteRt(true);
				current_inst.setUseImmediate(true);
				current_inst.setImmExtend(ImmExtend.SIGN);

				current_inst.setALUFunction(ALUFunction.SLEU);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.IMM);
				break;
			case MIPS:
			default:
				op = (OpcodeNORMAL.SDC1);
				logger.error("Instruction " + op + " not yet supported.");
				if(throwExceptionForUntestedInstructions)
				{
					throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
				}
			}
			break;
		case 0x3E:
			switch(ArchCfg.isa_type)
			{
			case DLX:
				// use SDC2 for implementation of SGEIU
				op = (OpcodeNORMAL.SGEIU);
				current_inst.setType(InstructionType.ITYPE);

				current_inst.setReadRs(true);
				current_inst.setWriteRt(true);
				current_inst.setUseImmediate(true);
				current_inst.setImmExtend(ImmExtend.SIGN);

				current_inst.setALUFunction(ALUFunction.SGEU);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.IMM);
				break;
			case MIPS:
			default:
				op = (OpcodeNORMAL.SDC2);
				logger.error("Instruction " + op + " not yet supported.");
				if(throwExceptionForUntestedInstructions)
				{
					throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
				}
			}
			break;
		case 0x3F:
			switch(ArchCfg.isa_type)
			{
			case MIPS:
				if(current_inst.getInstr() == PipelineConstants.PIPELINE_BUBBLE_INSTR)
				{
					op = (OpcodeNORMAL.NOP);
					current_inst.setALUFunction(ALUFunction.NOP);
					current_inst.setALUPortA(ALUPort.ZERO);
					current_inst.setALUPortB(ALUPort.ZERO);

					logger.info("Inserting is DLX pipeline bubble: " + current_inst.getInstr());
					
				}
				else
				{
					// TODO: what if and SD instruction with the bit mask of CONST.PIPELINE_BUBBLE_INSTR is issued??
					op = (OpcodeNORMAL.SD);
					logger.error("Instruction " + op + " not yet supported.");
					if(throwExceptionForUntestedInstructions)
					{
						throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
					}
				}
				break;
			case DLX:
				if(current_inst.getInstr() != PipelineConstants.PIPELINE_BUBBLE_INSTR)
				{
					throw new UnknownInstructionException("Wrong DLX Bubble Instruction: " + current_inst.getInstr().getValueAsHexString());
				}
				op = (OpcodeNORMAL.NOP);
				current_inst.setALUFunction(ALUFunction.NOP);
				current_inst.setALUPortA(ALUPort.ZERO);
				current_inst.setALUPortB(ALUPort.ZERO);
				
				logger.info("Inserting is DLX pipeline bubble: " + current_inst.getInstr());
				break;
			default:
				op = (OpcodeNORMAL.UNKNOWN);
			}
			break;

		default:
			op = (OpcodeNORMAL.UNKNOWN);
		}
		
		if(op == OpcodeNORMAL.UNKNOWN)
		{
			logger.error("Instruction " + op + " unknown. Type: NORMAL Opcode: " + current_inst.getOpcode().getValueAsHexString());
			throw new UnknownInstructionException("Instruction " + op + " unknown. Type: NORMAL Opcode: " + current_inst.getOpcode().getValueAsHexString());
		}
			
		current_inst.setOpNormal(op);
		return op;
	}

	private OpcodeSPECIAL determineFunction() throws UnknownInstructionException, PipelineDataTypeException
	{
		OpcodeSPECIAL op;

		switch (current_inst.getFunction().getValue() & 0x3F)
		{
		// bits 5..3 000
		case 0x00:
			op = (OpcodeSPECIAL.SLL);
			current_inst.setType(InstructionType.RTYPE);
			
			current_inst.setReadRt(true);
			current_inst.setWriteRd(true);
			current_inst.setUseShiftAmount(true);
			
			current_inst.setALUFunction(ALUFunction.SLL);
			current_inst.setALUPortA(ALUPort.RT);
			current_inst.setALUPortB(ALUPort.SA);
			break;
		case 0x01:
			op = (OpcodeSPECIAL.MOVCI);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x02:
			op = (OpcodeSPECIAL.SRL);
			current_inst.setType(InstructionType.RTYPE);
			
			current_inst.setReadRt(true);
			current_inst.setWriteRd(true);
			current_inst.setUseShiftAmount(true);
			
			current_inst.setALUFunction(ALUFunction.SRL);
			current_inst.setALUPortA(ALUPort.RT);
			current_inst.setALUPortB(ALUPort.SA);
			break;
		case 0x03:
			op = (OpcodeSPECIAL.SRA);
			current_inst.setType(InstructionType.RTYPE);
			
			current_inst.setReadRt(true);
			current_inst.setWriteRd(true);
			current_inst.setUseShiftAmount(true);
			
			current_inst.setALUFunction(ALUFunction.SRA);
			current_inst.setALUPortA(ALUPort.RT);
			current_inst.setALUPortB(ALUPort.SA);
			break;
		case 0x04:
			op = (OpcodeSPECIAL.SLLV);
			current_inst.setType(InstructionType.RTYPE);
			
			current_inst.setReadRs(true);
			current_inst.setReadRt(true);
			current_inst.setWriteRd(true);
			
			current_inst.setALUFunction(ALUFunction.SLLV);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.RT);
			break;
		case 0x05:
			op = (OpcodeSPECIAL.UNKNOWN);
			break;
		case 0x06:
			op = (OpcodeSPECIAL.SRLV);
			current_inst.setType(InstructionType.RTYPE);
			
			current_inst.setReadRs(true);
			current_inst.setReadRt(true);
			current_inst.setWriteRd(true);
			
			current_inst.setALUFunction(ALUFunction.SRLV);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.RT);
			break;
		case 0x07:
			op = (OpcodeSPECIAL.SRAV);
			current_inst.setType(InstructionType.RTYPE);
			
			current_inst.setReadRs(true);
			current_inst.setReadRt(true);
			current_inst.setWriteRd(true);
			
			current_inst.setALUFunction(ALUFunction.SRAV);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.RT);
			
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;

		// bits 5..3 001
		case 0x08:
			op = (OpcodeSPECIAL.JR);
			current_inst.setType(InstructionType.RTYPE);
			
			current_inst.setReadRs(true);
			
			current_inst.setALUFunction(ALUFunction.ADDU);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.ZERO);
			
			current_inst.setBranch(true);
			current_inst.setBranchCondition(BranchCondition.UNCOND);
			current_inst.setBranchPortA(BranchCtrlPort.ZERO);
			current_inst.setBranchPortB(BranchCtrlPort.ZERO);
			break;
		case 0x09:
			op = (OpcodeSPECIAL.JALR);
			current_inst.setType(InstructionType.RTYPE);
			
			current_inst.setReadRs(true);
			current_inst.setWriteRd(true);
			
			current_inst.setALUFunction(ALUFunction.ADDU);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.ZERO);
			
			current_inst.setBranch(true);
			current_inst.setBranchAndLink(true);
			current_inst.setBranchCondition(BranchCondition.UNCOND);
			current_inst.setBranchPortA(BranchCtrlPort.ZERO);
			current_inst.setBranchPortB(BranchCtrlPort.ZERO);
			break;
		case 0x0A:
			op = (OpcodeSPECIAL.MOVZ);
			
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x0B:
			op = (OpcodeSPECIAL.MOVN);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x0C:
			switch(ArchCfg.isa_type)
			{
			case MIPS:
				op = (OpcodeSPECIAL.SYSCALL);
				current_inst.setType(InstructionType.RTYPE);

				current_inst.setReadKernelRegisters(true);

				current_inst.setALUFunction(ALUFunction.SYSCALL);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.RT);
				break;
			case DLX:
				op = (OpcodeSPECIAL.TRAP);
				current_inst.setType(InstructionType.RTYPE);
			
				current_inst.setReadDLXTrapParameterRegister(true);
				if(current_inst.getRs().getValue() == PipelineConstants.DLX_TRAP_READ)
				{
					// prepare to write the number of read bytes into register R1
					current_inst.setWriteDLXTrapResultRegister(true);
				}
			
				current_inst.setALUFunction(ALUFunction.TRAP);
				current_inst.setALUPortA(ALUPort.RT);
				current_inst.setALUPortB(ALUPort.IMM);
				
				break;
			default:
				op = (OpcodeSPECIAL.UNKNOWN);
			}
			break;
		case 0x0D:
			op = (OpcodeSPECIAL.BREAK);
			current_inst.setType(InstructionType.RTYPE);
			break;
		case 0x0E:
			op = (OpcodeSPECIAL.UNKNOWN);
			break;
		case 0x0F:
			op = (OpcodeSPECIAL.SYNC);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;

		// bits 5..3 010
		case 0x10:
			op = (OpcodeSPECIAL.MFHI);
			current_inst.setType(InstructionType.RTYPE);
			
			current_inst.setReadHI(true);
			current_inst.setWriteRd(true);
			
			current_inst.setALUFunction(ALUFunction.ADD);
			current_inst.setALUPortA(ALUPort.HI);
			current_inst.setALUPortB(ALUPort.ZERO); // or ALUPORT.RT
			break;
		case 0x11:
			op = (OpcodeSPECIAL.MTHI);
			current_inst.setType(InstructionType.RTYPE);
			
			current_inst.setReadRs(true);
			current_inst.setWriteHI(true);
			
			current_inst.setALUFunction(ALUFunction.ADD);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.ZERO);
			break;
		case 0x12:
			op = (OpcodeSPECIAL.MFLO);
			current_inst.setType(InstructionType.RTYPE);
			
			current_inst.setReadLO(true);
			current_inst.setWriteRd(true);
			
			current_inst.setALUFunction(ALUFunction.ADD);
			current_inst.setALUPortA(ALUPort.LO);
			current_inst.setALUPortB(ALUPort.ZERO); // or ALUPORT.RT
			break;
		case 0x13:
			op = (OpcodeSPECIAL.MTLO);
			current_inst.setType(InstructionType.RTYPE);
			
			current_inst.setReadRs(true);
			current_inst.setWriteLO(true);
			
			current_inst.setALUFunction(ALUFunction.ADD);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.ZERO);
			break;
		case 0x14:
			op = (OpcodeSPECIAL.DSLLV);
			current_inst.setType(InstructionType.RTYPE);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x15:
			op = (OpcodeSPECIAL.UNKNOWN);
			break;
		case 0x16:
			op = (OpcodeSPECIAL.DSRLV);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x17:
			op = (OpcodeSPECIAL.DSRAV);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;

		// bits 5..3 011
		case 0x18:
			switch(ArchCfg.isa_type)
			{
			case MIPS:
				op = (OpcodeSPECIAL.MULT);
				current_inst.setType(InstructionType.RTYPE);

				current_inst.setReadRs(true);
				current_inst.setReadRt(true);
				current_inst.setWriteHI(true);
				current_inst.setWriteLO(true);

				current_inst.setALUFunction(ALUFunction.MULT);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.RT);
				break;
			case DLX:
				op = (OpcodeSPECIAL.MULT);
				// in DLX flavour the target register is determined by Rd
				current_inst.setType(InstructionType.RTYPE);
				current_inst.setReadRs(true);
				current_inst.setReadRt(true);
				current_inst.setWriteRd(true);

				current_inst.setALUFunction(ALUFunction.MULT);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.RT);
				break;
			default:
				op = (OpcodeSPECIAL.UNKNOWN);
			}
			break;
		case 0x19:
			switch(ArchCfg.isa_type)
			{
			case MIPS:
				op = (OpcodeSPECIAL.MULTU);
				current_inst.setType(InstructionType.RTYPE);

				current_inst.setReadRs(true);
				current_inst.setReadRt(true);
				current_inst.setWriteHI(true);
				current_inst.setWriteLO(true);

				current_inst.setALUFunction(ALUFunction.MULTU);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.RT);
				
				logger.error("Instruction " + op + " not yet supported.");
				if(throwExceptionForUntestedInstructions)
				{
					throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
				}
				break;
			case DLX:
				op = (OpcodeSPECIAL.MULTU);
				// in DLX flavour the target register is determined by Rd
				current_inst.setType(InstructionType.RTYPE);
				current_inst.setReadRs(true);
				current_inst.setReadRt(true);
				current_inst.setWriteRd(true);

				current_inst.setALUFunction(ALUFunction.MULTU);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.RT);
				break;
			default: 
				op = (OpcodeSPECIAL.UNKNOWN);
			}
			
			break;
		case 0x1A:
			switch(ArchCfg.isa_type)
			{
			case MIPS:
				op = (OpcodeSPECIAL.DIV);
				current_inst.setType(InstructionType.RTYPE);

				current_inst.setReadRs(true);
				current_inst.setReadRt(true);
				current_inst.setWriteHI(true);
				current_inst.setWriteLO(true);

				current_inst.setALUFunction(ALUFunction.DIV);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.RT);
				
				logger.error("Instruction " + op + " not yet supported.");
				if(throwExceptionForUntestedInstructions)
				{
					throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
				}
				break;
			case DLX:
				op = (OpcodeSPECIAL.DIV);
				// in DLX flavour the target register is determined by Rd
				current_inst.setType(InstructionType.RTYPE);

				current_inst.setReadRs(true);
				current_inst.setReadRt(true);
				current_inst.setWriteRd(true);

				current_inst.setALUFunction(ALUFunction.DIV);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.RT);
				break;
			default:
				op = (OpcodeSPECIAL.UNKNOWN);
			}
			
			break;
		case 0x1B:
			switch(ArchCfg.isa_type)
			{
			case MIPS:
				op = (OpcodeSPECIAL.DIVU);
				current_inst.setType(InstructionType.RTYPE);

				current_inst.setReadRs(true);
				current_inst.setReadRt(true);
				current_inst.setWriteHI(true);
				current_inst.setWriteLO(true);

				current_inst.setALUFunction(ALUFunction.DIVU);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.RT);
				break;
			case DLX:
				op = (OpcodeSPECIAL.DIVU);
				current_inst.setType(InstructionType.RTYPE);

				current_inst.setReadRs(true);
				current_inst.setReadRt(true);
				current_inst.setWriteRd(true);

				current_inst.setALUFunction(ALUFunction.DIVU);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.RT);
				break;
			default:
				op = (OpcodeSPECIAL.UNKNOWN);
			}
				
			break;
		case 0x1C:
			op = (OpcodeSPECIAL.DMULT);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x1D:
			op = (OpcodeSPECIAL.DMULTU);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x1E:
			op = (OpcodeSPECIAL.DDIV);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x1F:
			op = (OpcodeSPECIAL.DDIVU);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;

		// bits 5..3 100
		case 0x20:
			op = (OpcodeSPECIAL.ADD);
			current_inst.setType(InstructionType.RTYPE);
			
			current_inst.setReadRs(true);
			current_inst.setReadRt(true);
			current_inst.setWriteRd(true);
			
			current_inst.setALUFunction(ALUFunction.ADD);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.RT);
			break;
		case 0x21:
			op = (OpcodeSPECIAL.ADDU);
			current_inst.setType(InstructionType.RTYPE);
			
			current_inst.setReadRs(true);
			current_inst.setReadRt(true);
			current_inst.setWriteRd(true);
			
			current_inst.setALUFunction(ALUFunction.ADDU);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.RT);
			break;
		case 0x22:
			op = (OpcodeSPECIAL.SUB);
			current_inst.setType(InstructionType.RTYPE);
			
			current_inst.setReadRs(true);
			current_inst.setReadRt(true);
			current_inst.setWriteRd(true);
			
			current_inst.setALUFunction(ALUFunction.SUB);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.RT);
			break;
		case 0x23:
			op = (OpcodeSPECIAL.SUBU);
			current_inst.setType(InstructionType.RTYPE);
			
			current_inst.setReadRs(true);
			current_inst.setReadRt(true);
			current_inst.setWriteRd(true);
			
			current_inst.setALUFunction(ALUFunction.SUBU);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.RT);
			break;
		case 0x24:
			op = (OpcodeSPECIAL.AND);
			current_inst.setType(InstructionType.RTYPE);
			
			current_inst.setReadRs(true);
			current_inst.setReadRt(true);
			current_inst.setWriteRd(true);
			
			current_inst.setALUFunction(ALUFunction.AND);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.RT);
			break;
		case 0x25:
			op = (OpcodeSPECIAL.OR);
			current_inst.setType(InstructionType.RTYPE);
			
			current_inst.setReadRs(true);
			current_inst.setReadRt(true);
			current_inst.setWriteRd(true);
			
			current_inst.setALUFunction(ALUFunction.OR);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.RT);
			break;
		case 0x26:
			op = (OpcodeSPECIAL.XOR);
			current_inst.setType(InstructionType.RTYPE);
			
			current_inst.setReadRs(true);
			current_inst.setReadRt(true);
			current_inst.setWriteRd(true);
			
			current_inst.setALUFunction(ALUFunction.XOR);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.RT);
			break;
		case 0x27:
			op = (OpcodeSPECIAL.NOR);
			current_inst.setType(InstructionType.RTYPE);
			
			current_inst.setReadRs(true);
			current_inst.setReadRt(true);
			current_inst.setWriteRd(true);
			
			current_inst.setALUFunction(ALUFunction.NOR);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.RT);
			break;

		// bits 5..3 101
		case 0x28:
			switch(ArchCfg.isa_type)
			{
			case DLX:
				// using unused opcode of MIPS for SGT in DLX ISA
				op = (OpcodeSPECIAL.SGT);
				current_inst.setType(InstructionType.RTYPE);

				current_inst.setReadRs(true);
				current_inst.setReadRt(true);
				current_inst.setWriteRd(true);

				current_inst.setALUFunction(ALUFunction.SGT);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.RT);
				break;
			case MIPS:
			default:
				op = (OpcodeSPECIAL.UNKNOWN);
			}
			break;
		case 0x29:
			switch(ArchCfg.isa_type)
			{
			case DLX:
				// using unused opcode of MIPS for SGTU in DLX ISA
				op = (OpcodeSPECIAL.SGTU);
				current_inst.setType(InstructionType.RTYPE);

				current_inst.setReadRs(true);
				current_inst.setReadRt(true);
				current_inst.setWriteRd(true);

				current_inst.setALUFunction(ALUFunction.SGTU);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.RT);
				break;
			case MIPS:
			default:
				op = (OpcodeSPECIAL.UNKNOWN);
			}
			break;
		case 0x2A:
			op = (OpcodeSPECIAL.SLT);
			current_inst.setType(InstructionType.RTYPE);
			
			current_inst.setReadRs(true);
			current_inst.setReadRt(true);
			current_inst.setWriteRd(true);
			
			current_inst.setALUFunction(ALUFunction.SLT);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.RT);
			break;
		case 0x2B:
			op = (OpcodeSPECIAL.SLTU);
			current_inst.setType(InstructionType.RTYPE);
			
			current_inst.setReadRs(true);
			current_inst.setReadRt(true);
			current_inst.setWriteRd(true);
			
			current_inst.setALUFunction(ALUFunction.SLTU);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.RT);
			break;
		case 0x2C:
			op = (OpcodeSPECIAL.DADD);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x2D:
			op = (OpcodeSPECIAL.DADDU);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x2E:
			op = (OpcodeSPECIAL.DSUB);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x2F:
			op = (OpcodeSPECIAL.DSUBU);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;

		// bits 5..3 110
		case 0x30:
			op = (OpcodeSPECIAL.TGE);
			current_inst.setType(InstructionType.RTYPE);
			
			current_inst.setReadRs(true);
			current_inst.setReadRt(true);
			
			current_inst.setALUFunction(ALUFunction.TGE);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.RT);
			
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x31:
			op = (OpcodeSPECIAL.TGEU);
			current_inst.setType(InstructionType.RTYPE);
			
			current_inst.setReadRs(true);
			current_inst.setReadRt(true);
			
			current_inst.setALUFunction(ALUFunction.TGEU);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.RT);
			
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x32:
			op = (OpcodeSPECIAL.TLT);
			current_inst.setType(InstructionType.RTYPE);
			
			current_inst.setReadRs(true);
			current_inst.setReadRt(true);
			
			current_inst.setALUFunction(ALUFunction.TLT);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.RT);
			
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x33:
			op = (OpcodeSPECIAL.TLTU);
			current_inst.setType(InstructionType.RTYPE);
			
			current_inst.setReadRs(true);
			current_inst.setReadRt(true);
			
			current_inst.setALUFunction(ALUFunction.TLTU);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.RT);
			
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x34:
			op = (OpcodeSPECIAL.TEQ);
			current_inst.setType(InstructionType.RTYPE);
			
			current_inst.setReadRs(true);
			current_inst.setReadRt(true);
			
			current_inst.setALUFunction(ALUFunction.TEQ);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.RT);
			break;
		case 0x35:
			switch(ArchCfg.isa_type)
			{
			case DLX:
				// using unused opcode of MIPS for SEQ in DLX ISA
				op = (OpcodeSPECIAL.SEQ);
				current_inst.setType(InstructionType.RTYPE);

				current_inst.setReadRs(true);
				current_inst.setReadRt(true);
				current_inst.setWriteRd(true);

				current_inst.setALUFunction(ALUFunction.SEQ);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.RT);
				break;
			case MIPS:
			default:
				op = (OpcodeSPECIAL.UNKNOWN);
			}
			break;
		case 0x36:
			op = (OpcodeSPECIAL.TNE);
			current_inst.setType(InstructionType.RTYPE);
			
			current_inst.setReadRs(true);
			current_inst.setReadRt(true);
			
			current_inst.setALUFunction(ALUFunction.TNE);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.RT);
			
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x37:
			switch(ArchCfg.isa_type)
			{
			case DLX:
				// using unused opcode of MIPS for SNE in DLX ISA
				op = (OpcodeSPECIAL.SNE);
				current_inst.setType(InstructionType.RTYPE);

				current_inst.setReadRs(true);
				current_inst.setReadRt(true);
				current_inst.setWriteRd(true);

				current_inst.setALUFunction(ALUFunction.SNE);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.RT);
				break;
			case MIPS:
			default:
				op = (OpcodeSPECIAL.UNKNOWN);
			}
			break;

		// bits 5..3 111
		case 0x38:
			switch(ArchCfg.isa_type)
			{
			case DLX:
				// using unused opcode of MIPS for SLE in DLX ISA
				op = (OpcodeSPECIAL.SLE);
				current_inst.setType(InstructionType.RTYPE);

				current_inst.setReadRs(true);
				current_inst.setReadRt(true);
				current_inst.setWriteRd(true);

				current_inst.setALUFunction(ALUFunction.SLE);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.RT);
				break;
			case MIPS:
			default:
				op = (OpcodeSPECIAL.UNKNOWN);
			}
			break;
		case 0x39:
			switch(ArchCfg.isa_type)
			{
			case DLX:
				// using unused opcode of MIPS for SGE in DLX ISA
				op = (OpcodeSPECIAL.SGE);
				current_inst.setType(InstructionType.RTYPE);

				current_inst.setReadRs(true);
				current_inst.setReadRt(true);
				current_inst.setWriteRd(true);

				current_inst.setALUFunction(ALUFunction.SGE);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.RT);
				break;
			case MIPS:
			default:
				op = (OpcodeSPECIAL.UNKNOWN);
			}
			break;
		case 0x3A:
			switch(ArchCfg.isa_type)
			{
			case DLX:
				// using unused opcode of MIPS for SEQU in DLX ISA
				op = (OpcodeSPECIAL.SEQU);
				current_inst.setType(InstructionType.RTYPE);

				current_inst.setReadRs(true);
				current_inst.setReadRt(true);
				current_inst.setWriteRd(true);

				current_inst.setALUFunction(ALUFunction.SEQU);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.RT);
				break;
			case MIPS:
			default:
				op = (OpcodeSPECIAL.UNKNOWN);
			}
			break;
		case 0x3B:
			switch(ArchCfg.isa_type)
			{
			case DLX:
				// using unused opcode of MIPS for SNEU in DLX ISA
				op = (OpcodeSPECIAL.SNEU);
				current_inst.setType(InstructionType.RTYPE);

				current_inst.setReadRs(true);
				current_inst.setReadRt(true);
				current_inst.setWriteRd(true);

				current_inst.setALUFunction(ALUFunction.SNEU);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.RT);
				break;
			case MIPS:
			default:
				op = (OpcodeSPECIAL.UNKNOWN);
			}
			break;
		case 0x3C:
			switch(ArchCfg.isa_type)
			{
			case DLX:
				// using unused opcode of MIPS for SLEU in DLX ISA
				op = (OpcodeSPECIAL.SLEU);
				current_inst.setType(InstructionType.RTYPE);

				current_inst.setReadRs(true);
				current_inst.setReadRt(true);
				current_inst.setWriteRd(true);

				current_inst.setALUFunction(ALUFunction.SLEU);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.RT);
				break;
			case MIPS:
			default:
				op = (OpcodeSPECIAL.UNKNOWN);
			}
			break;
		case 0x3D:
			switch(ArchCfg.isa_type)
			{
			case DLX:
				// using unused opcode of MIPS for SGEU in DLX ISA
				op = (OpcodeSPECIAL.SGEU);
				current_inst.setType(InstructionType.RTYPE);

				current_inst.setReadRs(true);
				current_inst.setReadRt(true);
				current_inst.setWriteRd(true);

				current_inst.setALUFunction(ALUFunction.SGEU);
				current_inst.setALUPortA(ALUPort.RS);
				current_inst.setALUPortB(ALUPort.RT);
				break;
			case MIPS:
			default:
				op = (OpcodeSPECIAL.UNKNOWN);
			}
			break;
		case 0x3E:
			op = (OpcodeSPECIAL.DSRL32);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x3F:
			op = (OpcodeSPECIAL.DSRA32);
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		default:
			op = (OpcodeSPECIAL.UNKNOWN);
		}
		
		if(op == OpcodeSPECIAL.UNKNOWN)
		{
			logger.error("Instruction " + op + " unknown. Type: SPECIAL Opcode: " + current_inst.getOpcode().getValueAsHexString());
			throw new UnknownInstructionException("Instruction " + op + " unknown. Type: SPECIAL Opcode: " + current_inst.getOpcode().getValueAsHexString());
		}

		current_inst.setOpSpecial(op);
		return op;
	}

	private OpcodeREGIMM determineRegimm() throws UnknownInstructionException, PipelineDataTypeException
	{
		OpcodeREGIMM op;

		switch (current_inst.getRegimm().getValue() & 0x1F)
		{
		// bits 20..19 00
		case 0x00:
			op = (OpcodeREGIMM.BLTZ);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			
			current_inst.setALUFunction(ALUFunction.BR);
			current_inst.setALUPortA(ALUPort.PC);
			current_inst.setALUPortB(ALUPort.IMM);
			
			current_inst.setBranch(true);
			current_inst.setBranchCondition(BranchCondition.BLTZ);
			current_inst.setBranchPortA(BranchCtrlPort.RS);
			current_inst.setBranchPortB(BranchCtrlPort.ZERO);
			break;
		case 0x01:
			op = (OpcodeREGIMM.BGEZ);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			
			current_inst.setALUFunction(ALUFunction.BR);
			current_inst.setALUPortA(ALUPort.PC);
			current_inst.setALUPortB(ALUPort.IMM);
			
			current_inst.setBranch(true);
			current_inst.setBranchCondition(BranchCondition.BGEZ);
			current_inst.setBranchPortA(BranchCtrlPort.RS);
			current_inst.setBranchPortB(BranchCtrlPort.ZERO);
			break;
		case 0x02:
			op = (OpcodeREGIMM.BLTZL);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			
			current_inst.setALUFunction(ALUFunction.BR);
			current_inst.setALUPortA(ALUPort.PC);
			current_inst.setALUPortB(ALUPort.IMM);
			
			current_inst.setBranch(true);
			current_inst.setBranchLikely(true);
			current_inst.setBranchCondition(BranchCondition.BLTZ);
			current_inst.setBranchPortA(BranchCtrlPort.RS);
			current_inst.setBranchPortB(BranchCtrlPort.ZERO);
			
			
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x03:
			op = (OpcodeREGIMM.BGEZL);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			
			current_inst.setALUFunction(ALUFunction.BR);
			current_inst.setALUPortA(ALUPort.PC);
			current_inst.setALUPortB(ALUPort.IMM);
			
			current_inst.setBranch(true);
			current_inst.setBranchLikely(true);
			current_inst.setBranchCondition(BranchCondition.BGEZ);
			current_inst.setBranchPortA(BranchCtrlPort.RS);
			current_inst.setBranchPortB(BranchCtrlPort.ZERO);
			break;
		case 0x04:
		case 0x05:
		case 0x06:
		case 0x07:
			op = (OpcodeREGIMM.UNKNOWN);
			break;

		// bits 20..19 01
		case 0x08:
			op = (OpcodeREGIMM.TGEI);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			
			current_inst.setALUFunction(ALUFunction.TGE);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.IMM);
			
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x09:
			op = (OpcodeREGIMM.TGEIU);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			
			current_inst.setALUFunction(ALUFunction.TGEU);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.IMM);
			
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x0A:
			op = (OpcodeREGIMM.TLTI);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			
			current_inst.setALUFunction(ALUFunction.TLT);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.IMM);
			
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x0B:
			op = (OpcodeREGIMM.TLTIU);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			
			current_inst.setALUFunction(ALUFunction.TLTU);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.IMM);
			
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x0C:
			op = (OpcodeREGIMM.TEQI);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			
			current_inst.setALUFunction(ALUFunction.TEQ);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.IMM);
			
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x0D:
			op = (OpcodeREGIMM.UNKNOWN);
			break;
		case 0x0E:
			op = (OpcodeREGIMM.TNEI);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			
			current_inst.setALUFunction(ALUFunction.TNE);
			current_inst.setALUPortA(ALUPort.RS);
			current_inst.setALUPortB(ALUPort.IMM);
			
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x0F:
			op = (OpcodeREGIMM.UNKNOWN);
			break;

		// bits 20..19 10
		case 0x10:
			op = (OpcodeREGIMM.BLTZAL);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			current_inst.setWriteRA(true);
			
			current_inst.setALUFunction(ALUFunction.BR);
			current_inst.setALUPortA(ALUPort.PC);
			current_inst.setALUPortB(ALUPort.IMM);
			
			current_inst.setBranch(true);
			current_inst.setBranchAndLink(true);
			current_inst.setBranchCondition(BranchCondition.BLTZ);
			current_inst.setBranchPortA(BranchCtrlPort.RS);
			current_inst.setBranchPortB(BranchCtrlPort.ZERO);
			
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x11:
			op = (OpcodeREGIMM.BGEZAL);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			current_inst.setWriteRA(true);
			
			current_inst.setALUFunction(ALUFunction.BR);
			current_inst.setALUPortA(ALUPort.PC);
			current_inst.setALUPortB(ALUPort.IMM);
			
			current_inst.setBranch(true);
			current_inst.setBranchAndLink(true);
			current_inst.setBranchCondition(BranchCondition.BGEZ);
			current_inst.setBranchPortA(BranchCtrlPort.RS);
			current_inst.setBranchPortB(BranchCtrlPort.ZERO);
			
			logger.error("Instruction " + op + " not yet supported.");
			if(throwExceptionForUntestedInstructions)
			{
				throw new UnknownInstructionException("Instruction " + op + " not yet supported.");
			}
			break;
		case 0x12:
			op = (OpcodeREGIMM.BLTZALL);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			current_inst.setWriteRA(true);
			
			current_inst.setALUFunction(ALUFunction.BR);
			current_inst.setALUPortA(ALUPort.PC);
			current_inst.setALUPortB(ALUPort.IMM);
			
			current_inst.setBranch(true);
			current_inst.setBranchAndLink(true);
			current_inst.setBranchLikely(true);
			current_inst.setBranchCondition(BranchCondition.BLTZ);
			current_inst.setBranchPortA(BranchCtrlPort.RS);
			current_inst.setBranchPortB(BranchCtrlPort.ZERO);
			break;
		case 0x13:
			op = (OpcodeREGIMM.BGEZALL);
			current_inst.setType(InstructionType.ITYPE);
			
			current_inst.setReadRs(true);
			current_inst.setUseImmediate(true);
			current_inst.setImmExtend(ImmExtend.SIGN);
			current_inst.setWriteRA(true);
			
			current_inst.setALUFunction(ALUFunction.BR);
			current_inst.setALUPortA(ALUPort.PC);
			current_inst.setALUPortB(ALUPort.IMM);
			
			current_inst.setBranch(true);
			current_inst.setBranchAndLink(true);
			current_inst.setBranchLikely(true);
			current_inst.setBranchCondition(BranchCondition.BGEZ);
			current_inst.setBranchPortA(BranchCtrlPort.RS);
			current_inst.setBranchPortB(BranchCtrlPort.ZERO);
			break;
		case 0x14:
		case 0x15:
		case 0x16:
		case 0x17:
			op = (OpcodeREGIMM.UNKNOWN);
			break;

		// bits 20..19 11
		case 0x18:
		case 0x19:
		case 0x1A:
		case 0x1B:
		case 0x1C:
		case 0x1D:
		case 0x1E:
		case 0x1F:
			op = (OpcodeREGIMM.UNKNOWN);
			break;
		default:
			op = (OpcodeREGIMM.UNKNOWN);
		}
		
		if(op == OpcodeREGIMM.UNKNOWN)
		{
			logger.error("Instruction " + op + " unknown. Type: REGIMM Opcode: " + current_inst.getOpcode().getValueAsHexString());
			throw new UnknownInstructionException("Instruction " + op + " unknown. Type: REGIMM Opcode: " + current_inst.getOpcode().getValueAsHexString());
		}

		current_inst.setOpRegimm(op);
		return op;
	}
	
	public void setInputLatch(Queue<FetchDecodeData> fetchDecodeLatch)
	{
		fetch_decode_latch = fetchDecodeLatch;
	}

	public DecodeOutputData doCycle() throws DecodeStageException, CacheException, PipelineDataTypeException 
	{
		uint32 alu_in_a;
		uint32 alu_in_b;
		FetchDecodeData fdd = fetch_decode_latch.element();
		uint32 decode_instr = fdd.getInstr();
		uint32 pc = fdd.getPc();
		Instruction inst = decodeInstr(decode_instr);
		logger.debug("PC: " + pc.getValueAsHexString()
				+ " instruction decoded as " + inst.getString());

		// determination of input for ALU port A
		switch (inst.getALUPortA())
		{
		case RS:
			alu_in_a = reg_set.read(inst.getRs());
			break;
		case RT:
			alu_in_a = reg_set.read(inst.getRt());
			break;
		case LO:
			alu_in_a = reg_set.read_SP(SpecialRegisters.LO);
			break;
		case HI:
			alu_in_a = reg_set.read_SP(SpecialRegisters.HI);
			break;
		case PC:
			// increment the pc, because relative jumps assume the pc of the next instruction
			alu_in_a = new uint32(pc.getValue()+4);
			break;
		case ZERO:
			alu_in_a = new uint32(0);
			break;
		default:
			alu_in_a = new uint32(0);
			throw new DecodeStageException("Wrong ALU Port A");
		}

		// determination of input for ALU port B
		switch (inst.getALUPortB())
		{
		case RT:
			alu_in_b = reg_set.read(inst.getRt());
			break;
		case IDX:
			alu_in_b = new uint32(inst.getInstrIndex().getValue());
			break;
		case IMM:
			if(inst.getImmExtend()==ImmExtend.ZERO)
			{
				alu_in_b = new uint32((inst.getOffset().getValue())&0xFFFF);
			}
			else if(inst.getImmExtend()==ImmExtend.SIGN)
			{
				alu_in_b = new uint32((inst.getOffset().getValue()));
			}
			else if((ArchCfg.isa_type == ISAType.DLX) && (inst.getOpSpecial() == OpcodeSPECIAL.TRAP))
			{
				alu_in_b = new uint32(inst.getRs().getValue());
			}
			else
			{
				alu_in_b = new uint32(0);
				throw new DecodeStageException("Wrong IMM at ALU Port B");
			}
			break;
		case SA:
			alu_in_b = new uint32(inst.getSa().getValue());
			break;
		case ZERO:
			alu_in_b = new uint32(0);
			break;
		default:
			alu_in_b = new uint32(0);
			throw new DecodeStageException("Wrong ALU Port B");
		}

		uint32 branch_ctrl_in_a;
		uint32 branch_ctrl_in_b;
		// determination of input for BRANCH CONTROL port A
		switch(inst.getBrachControlPortA())
		{
		case RS:
			branch_ctrl_in_a = reg_set.read(inst.getRs());
			break;
		case ZERO:
			branch_ctrl_in_a = new uint32(0);
			break;
		default:
			branch_ctrl_in_a = new uint32(0);
			throw new DecodeStageException("Wrong Branch Port A");
		}

		// determination of input for BRANCH CONTROL port B
		switch(inst.getBrachControlPortB())
		{
		case RT:
			branch_ctrl_in_b = reg_set.read(inst.getRt());
			break;
		case ZERO:
			branch_ctrl_in_b = new uint32(0);
			break;
		default:
			branch_ctrl_in_b = new uint32(0);
			throw new DecodeStageException("Wrong Branch Port B");
		}


		// determination of the store value
		uint32 store_value = new uint32(0);
		if (inst.getStore())
		{
			store_value = reg_set.read(inst.getRt());
		}

		DecodeExecuteData ded = new DecodeExecuteData(inst, pc, alu_in_a, alu_in_b, branch_ctrl_in_a, branch_ctrl_in_b, store_value);
		
		return new DecodeOutputData(ded);
	}

}
