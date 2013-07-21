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
import openDLX.exception.ExecuteStageException;
import openDLX.exception.PipelineException;
import openDLX.util.Statistics;

public class Execute
{
	private static Logger logger = Logger.getLogger("EXECUTE");
	private Statistics stat = Statistics.getInstance(); 
	private ALU alu;
	private BranchControl branch_control;
	private Queue<DecodeExecuteData> decode_execute_latch;
	private Queue<BranchPredictionModuleExecuteData> branchprediction_execute_latch;
	private Queue<ExecuteMemoryData> fw_eml;
	private Queue<MemoryWritebackData> fw_mwl;
	private Queue<WriteBackData> fw_wbl;

	public Execute()
	{
		// TODO handle ISA DLX/MIPS flavour
		alu = new ALU();
		branch_control = new BranchControl();
	}

	public void setInputLatches(Queue<DecodeExecuteData> decodeExecuteLatch, Queue<BranchPredictionModuleExecuteData> branchpredictionExecuteLatch)
	{
		decode_execute_latch = decodeExecuteLatch;
		branchprediction_execute_latch = branchpredictionExecuteLatch;
	}

	public void setForwardingLatches(Queue<ExecuteMemoryData> executeMemoryLatch, Queue<MemoryWritebackData> memoryWritebackLatch, Queue<WriteBackData> writebackLatch)
	{
		fw_eml = executeMemoryLatch;
		fw_mwl = memoryWritebackLatch;
		fw_wbl = writebackLatch;
	}
	
	public ExecuteOutputData doCycle() throws PipelineException
	{
		
		boolean[] stall_out = new boolean[PipelineConstants.STAGES];
		for(byte i = 0; i < PipelineConstants.STAGES; i++)
		{
			stall_out[i] = false;
		}

		DecodeExecuteData ded = decode_execute_latch.element();
		Instruction inst = ded.getInst();
		uint32 alu_in_a = ded.getAluInA();
		uint32 alu_in_b = ded.getAluInB();
		uint32 branch_ctrl_in_a = ded.getBranchCtrlInA();
		uint32 branch_ctrl_in_b = ded.getBranchCtrlInB();
		uint32 pc = ded.getPc();
		uint32 store_value = ded.getStoreValue();

		// STRUCTURE ALLOCATION FOR DATA FORWARDING

		// FROM EXECUTE TO MEM STAGE 
		ExecuteMemoryData fw_emd = fw_eml.element();
		Instruction fw_emd_inst = fw_emd.getInst();
		uint32[] fw_emd_alu_result = fw_emd.getAluOut();
		uint32 fw_emd_ld_result = new uint32(0); // cannot be forwarded due to stall of the pipeline

		// FROM MEM TO WRITE BACK STAGE
		MemoryWritebackData fw_mwd = fw_mwl.element();
		Instruction fw_mwd_inst = fw_mwd.getInst();
		uint32[] fw_mwd_alu_result = fw_mwd.getAluOut();
		uint32 fw_mwd_ld_result = fw_mwd.getLdResult();

		// FROM WRITE BACK OUT STAGE
		WriteBackData fw_wbd = fw_wbl.element();
		Instruction fw_wbd_inst = fw_wbd.getInst();
		uint32[] fw_wbd_alu_result = fw_wbd.getAluOut();
		uint32 fw_wbd_ld_result = fw_wbd.getLdResult();

		// MIPS ISA flavour always uses forwarding, DLX only if enabled
		if((ArchCfg.isa_type == ISAType.MIPS) || (ArchCfg.use_forwarding == true))
		{
			// DATA FORWARDING
			
			// forward already calculated results from the WB stage and from
			// the EX stage

			// for ALU PORT A 
			int old_alu_in_a = alu_in_a.getValue();
			switch (inst.getALUPortA())
			{
			case RS:
			{
				forwarding(inst.getRs(), alu_in_a, fw_wbd_alu_result[0], fw_wbd_ld_result, fw_wbd_inst);
				boolean fw_wb = (old_alu_in_a != alu_in_a.getValue()) ? true : false;
				forwarding(inst.getRs(), alu_in_a, fw_mwd_alu_result[0], fw_mwd_ld_result, fw_mwd_inst);
				boolean fw_mem = (old_alu_in_a != alu_in_a.getValue()) ? true : false;
				forwarding(inst.getRs(), alu_in_a, fw_emd_alu_result[0], fw_emd_ld_result, fw_emd_inst);
				boolean fw_ex = (old_alu_in_a != alu_in_a.getValue()) ? true : false;
				stat.countALUForward(fw_ex, fw_mem, fw_wb);
				break;
			}
			case RT:
			{
				forwarding(inst.getRt(), alu_in_a, fw_wbd_alu_result[0], fw_wbd_ld_result, fw_wbd_inst);
				boolean fw_wb = (old_alu_in_a != alu_in_a.getValue()) ? true : false;
				forwarding(inst.getRt(), alu_in_a, fw_mwd_alu_result[0], fw_mwd_ld_result, fw_mwd_inst);
				boolean fw_mem = (old_alu_in_a != alu_in_a.getValue()) ? true : false;
				forwarding(inst.getRt(), alu_in_a, fw_emd_alu_result[0], fw_emd_ld_result, fw_emd_inst);
				boolean fw_ex = (old_alu_in_a != alu_in_a.getValue()) ? true : false;
				stat.countALUForward(fw_ex, fw_mem, fw_wb);
				break;
			}
			case LO:
			{
				forwarding(SpecialRegisters.LO, alu_in_a, fw_wbd_alu_result, fw_wbd_inst);
				boolean fw_wb = (old_alu_in_a != alu_in_a.getValue()) ? true : false;
				forwarding(SpecialRegisters.LO, alu_in_a, fw_mwd_alu_result, fw_mwd_inst);
				boolean fw_mem = (old_alu_in_a != alu_in_a.getValue()) ? true : false;
				forwarding(SpecialRegisters.LO, alu_in_a, fw_emd_alu_result, fw_emd_inst);
				boolean fw_ex = (old_alu_in_a != alu_in_a.getValue()) ? true : false;
				stat.countALUForward(fw_ex, fw_mem, fw_wb);
				break;
			}
			case HI:
			{
				forwarding(SpecialRegisters.HI, alu_in_a, fw_wbd_alu_result, fw_wbd_inst);
				boolean fw_wb = (old_alu_in_a != alu_in_a.getValue()) ? true : false;
				forwarding(SpecialRegisters.HI, alu_in_a, fw_mwd_alu_result, fw_mwd_inst);
				boolean fw_mem = (old_alu_in_a != alu_in_a.getValue()) ? true : false;
				forwarding(SpecialRegisters.HI, alu_in_a, fw_emd_alu_result, fw_emd_inst);
				boolean fw_ex = (old_alu_in_a != alu_in_a.getValue()) ? true : false;
				stat.countALUForward(fw_ex, fw_mem, fw_wb);
				break;
			}
			default:
				// do nothing
			}


			if(old_alu_in_a != alu_in_a.getValue())
			{
				logger.debug("{FW} PC: " + pc.getValueAsHexString() + " forwarding changed value for ALU port A " + inst.getALUPortA() + " from: 0x" + Integer.toHexString(old_alu_in_a) + " to: " + alu_in_a.getValueAsHexString());
			}

			// for ALU PORT B 
			int old_alu_in_b = alu_in_b.getValue();
			switch (inst.getALUPortB())
			{
			case RT:
			{
				forwarding(inst.getRt(), alu_in_b, fw_wbd_alu_result[0], fw_wbd_ld_result, fw_wbd_inst);
				boolean fw_wb = (old_alu_in_b != alu_in_b.getValue())?true:false;
				forwarding(inst.getRt(), alu_in_b, fw_mwd_alu_result[0], fw_mwd_ld_result, fw_mwd_inst);
				boolean fw_mem = (old_alu_in_b != alu_in_b.getValue())?true:false;
				forwarding(inst.getRt(), alu_in_b, fw_emd_alu_result[0], fw_emd_ld_result, fw_emd_inst);
				boolean fw_ex = (old_alu_in_b != alu_in_b.getValue())?true:false;
				stat.countALUForward(fw_ex, fw_mem, fw_wb);
				break;
			}
			default:
				// do nothing
			}
			if(old_alu_in_b != alu_in_b.getValue())
			{
				logger.debug("{FW} PC: " + pc.getValueAsHexString() + " forwarding changed value for ALU port B " + inst.getALUPortB() + " from: 0x" + Integer.toHexString(old_alu_in_b) + " to: " + alu_in_b.getValueAsHexString());
			}

			// for BRANCH CONTROL PORT A
			int old_branch_ctrl_in_a = branch_ctrl_in_a.getValue();
			switch(inst.getBrachControlPortA())
			{
			case RS:
			{
				forwarding(inst.getRs(), branch_ctrl_in_a, fw_wbd_alu_result[0], fw_wbd_ld_result, fw_wbd_inst);
				boolean fw_wb = (old_branch_ctrl_in_a != branch_ctrl_in_a.getValue())?true:false;
				forwarding(inst.getRs(), branch_ctrl_in_a, fw_mwd_alu_result[0], fw_mwd_ld_result, fw_mwd_inst);
				boolean fw_mem = (old_branch_ctrl_in_a != branch_ctrl_in_a.getValue())?true:false;
				forwarding(inst.getRs(), branch_ctrl_in_a, fw_emd_alu_result[0], fw_emd_ld_result, fw_emd_inst);
				boolean fw_ex = (old_branch_ctrl_in_a != branch_ctrl_in_a.getValue())?true:false;
				stat.countBCRTLForward(fw_ex, fw_mem, fw_wb);
				break;
			}
			default:
				// do nothing
			}

			if(old_branch_ctrl_in_a != branch_ctrl_in_a.getValue())
			{
				logger.debug("{FW} PC: " + pc.getValueAsHexString() + " forwarding changed value for BCTRL port A " + inst.getBrachControlPortA() + " from: 0x" + Integer.toHexString(old_branch_ctrl_in_a) + " to: " + branch_ctrl_in_a.getValueAsHexString());
			}

			// for BRANCH CONTROL PORT B
			int old_branch_ctrl_in_b = branch_ctrl_in_b.getValue();
			switch(inst.getBrachControlPortB())
			{
			case RT:
				forwarding(inst.getRt(), branch_ctrl_in_b, fw_wbd_alu_result[0], fw_wbd_ld_result, fw_wbd_inst);
				boolean fw_wb = (old_branch_ctrl_in_b != branch_ctrl_in_b.getValue())?true:false;
				forwarding(inst.getRt(), branch_ctrl_in_b, fw_mwd_alu_result[0], fw_mwd_ld_result, fw_mwd_inst);
				boolean fw_mem = (old_branch_ctrl_in_b != branch_ctrl_in_b.getValue())?true:false;
				forwarding(inst.getRt(), branch_ctrl_in_b, fw_emd_alu_result[0], fw_emd_ld_result, fw_emd_inst);
				boolean fw_ex = (old_branch_ctrl_in_b != branch_ctrl_in_b.getValue())?true:false;
				stat.countBCRTLForward(fw_ex, fw_mem, fw_wb);
				break;
			default:
				// do nothing
			}

			if(old_branch_ctrl_in_b != branch_ctrl_in_b.getValue())
			{
				logger.debug("{FW} PC: " + pc.getValueAsHexString() + " forwarding changed value for BCTRL port B " + inst.getBrachControlPortB() + " from: 0x" + Integer.toHexString(old_branch_ctrl_in_b) + " to: " + branch_ctrl_in_b.getValueAsHexString());
			}

			// for STORE value
			int old_store_value = store_value.getValue();
			if (inst.getStore())
			{
				forwarding(inst.getRt(), store_value, fw_wbd_alu_result[0], fw_wbd_ld_result, fw_wbd_inst);
				boolean fw_wb = (old_store_value != store_value.getValue())?true:false;
				forwarding(inst.getRt(), store_value, fw_mwd_alu_result[0], fw_mwd_ld_result, fw_mwd_inst);
				boolean fw_mem = (old_store_value != store_value.getValue())?true:false;
				forwarding(inst.getRt(), store_value, fw_emd_alu_result[0], fw_emd_ld_result, fw_emd_inst);
				boolean fw_ex = (old_store_value != store_value.getValue())?true:false;
				stat.countSTOREForward(fw_ex, fw_mem, fw_wb);
			}

			if(old_store_value != store_value.getValue())
			{
				logger.debug("{FW} PC: " + pc.getValueAsHexString() + " forwarding changed store_value for RT from: 0x" + Integer.toHexString(old_store_value) + " to: " + store_value.getValueAsHexString());
			}

			// DATA FORWARDING END
		}
		else
		{
			// PSEUDO DATA FORWARDING
			// the DLX pipeline needs two bubbles for data dependencies, because the 
			// register set is pipelined:
			// - first part of a cycle, the write back stage writes a register
			// - second part of cycle, the decode reads a register
			// This behavior is emulated in this simulator by forwarding the data that 
			// is at the input ports of the write back stage.
			//////////
			
			// forward already calculated results from the WB stage

			// for ALU PORT A 
			int old_alu_in_a = alu_in_a.getValue();
			switch (inst.getALUPortA())
			{
			case RS:
			{
				forwarding(inst.getRs(), alu_in_a, fw_wbd_alu_result[0], fw_wbd_ld_result, fw_wbd_inst);
				break;
			}
			case RT:
			{
				forwarding(inst.getRt(), alu_in_a, fw_wbd_alu_result[0], fw_wbd_ld_result, fw_wbd_inst);
				break;
			}
			case LO:
			{
				forwarding(SpecialRegisters.LO, alu_in_a, fw_wbd_alu_result, fw_wbd_inst);
				break;
			}
			case HI:
			{
				forwarding(SpecialRegisters.HI, alu_in_a, fw_wbd_alu_result, fw_wbd_inst);
				break;
			}
			default:
				// do nothing
			}


			if(old_alu_in_a != alu_in_a.getValue())
			{
				logger.debug("{RS/WB-FW} PC: " + pc.getValueAsHexString() + " forwarding changed value for ALU port A " + inst.getALUPortA() + " from: 0x" + Integer.toHexString(old_alu_in_a) + " to: " + alu_in_a.getValueAsHexString());
			}

			// for ALU PORT B 
			int old_alu_in_b = alu_in_b.getValue();
			switch (inst.getALUPortB())
			{
			case RT:
			{
				forwarding(inst.getRt(), alu_in_b, fw_wbd_alu_result[0], fw_wbd_ld_result, fw_wbd_inst);
				break;
			}
			default:
				// do nothing
			}
			if(old_alu_in_b != alu_in_b.getValue())
			{
				logger.debug("{RS/WB-FW} PC: " + pc.getValueAsHexString() + " forwarding changed value for ALU port B " + inst.getALUPortB() + " from: 0x" + Integer.toHexString(old_alu_in_b) + " to: " + alu_in_b.getValueAsHexString());
			}

			// for BRANCH CONTROL PORT A
			int old_branch_ctrl_in_a = branch_ctrl_in_a.getValue();
			switch(inst.getBrachControlPortA())
			{
			case RS:
			{
				forwarding(inst.getRs(), branch_ctrl_in_a, fw_wbd_alu_result[0], fw_wbd_ld_result, fw_wbd_inst);
				break;
			}
			default:
				// do nothing
			}

			if(old_branch_ctrl_in_a != branch_ctrl_in_a.getValue())
			{
				logger.debug("{RS/WB-FW} PC: " + pc.getValueAsHexString() + " forwarding changed value for BCTRL port A " + inst.getBrachControlPortA() + " from: 0x" + Integer.toHexString(old_branch_ctrl_in_a) + " to: " + branch_ctrl_in_a.getValueAsHexString());
			}

			// for BRANCH CONTROL PORT B
			int old_branch_ctrl_in_b = branch_ctrl_in_b.getValue();
			switch(inst.getBrachControlPortB())
			{
			case RT:
				forwarding(inst.getRt(), branch_ctrl_in_b, fw_wbd_alu_result[0], fw_wbd_ld_result, fw_wbd_inst);
				break;
			default:
				// do nothing
			}

			if(old_branch_ctrl_in_b != branch_ctrl_in_b.getValue())
			{
				logger.debug("{RS/WB-FW} PC: " + pc.getValueAsHexString() + " forwarding changed value for BCTRL port B " + inst.getBrachControlPortB() + " from: 0x" + Integer.toHexString(old_branch_ctrl_in_b) + " to: " + branch_ctrl_in_b.getValueAsHexString());
			}

			// for STORE value
			int old_store_value = store_value.getValue();
			if (inst.getStore())
			{
				forwarding(inst.getRt(), store_value, fw_wbd_alu_result[0], fw_wbd_ld_result, fw_wbd_inst);
			}

			if(old_store_value != store_value.getValue())
			{
				logger.debug("{RS/WB-FW} PC: " + pc.getValueAsHexString() + " forwarding changed store_value for RT from: 0x" + Integer.toHexString(old_store_value) + " to: " + store_value.getValueAsHexString());
			}

			// DATA FORWARDING END
		}

		// ALU OPERATION BEGIN
		uint32[] alu_out = alu.doOperation(inst.getALUFunction(),
				alu_in_a, alu_in_b);
		uint32 alu_outLO = alu_out[0];
		// uint32 alu_outHI = alu_out[1];

		logger.debug("PC: " + pc.getValueAsHexString() + " ALU calculated: "
				+ alu_outLO.getValue() + "(" + alu_outLO.getValueAsHexString()
				+ ") by: " + alu_in_a.getValue() + "("
				+ alu_in_a.getValueAsHexString() + ") " + inst.getALUFunction()
				+ " " + alu_in_b.getValue() + "(" + alu_in_b.getValueAsHexString()
				+ ")");

		// ALU OPERATION END

		// BRANCH CONTROL
		boolean jump = branch_control.checkBranch(inst, branch_ctrl_in_a, branch_ctrl_in_b);
		// BRANCH CONTROL END

		// count the jumps if there is a branch
		if(inst.getBranch()==true)
		{
			if(jump)
			{
				stat.countJumpTaken();
			}
			else
			{
				stat.countJumpNotTaken();
			}
			if(inst.getBranchLikely())
			{
				stat.countJumpLikely();
			}
			if(inst.getBranchAndLink())
			{
				stat.countJumpLink();
			}
		}
		
		// check if branch prediction was correct
		BranchPredictionModuleExecuteData bpmed = branchprediction_execute_latch.element();
		boolean mispredicted_branch = false;
		if(inst.getBranch()== true)
		{
			if(bpmed.getPc().getValue() != pc.getValue())
			{
				throw new ExecuteStageException("Wrong PC :" + bpmed.getPc().getValueAsHexString() + " != " + pc.getValueAsHexString());
			}
			
			// either the branch direction was falsely predicted 
			// or the branch target is wrong (but only if the jump is respectively was predicted to be taken)
			if((bpmed.getDoSpeculativeJump() != jump) || ((jump == true) && (bpmed.getDoSpeculativeJump() == true) && (bpmed.getBranchTgt().getValue() != alu_outLO.getValue())))
			{
				mispredicted_branch = true;
			}
			
		}
		
		// to MEM STAGE
		ExecuteMemoryData emd = new ExecuteMemoryData(inst, pc, alu_out, store_value, jump);

		// to FETCH STAGE
		ExecuteFetchData efd = new ExecuteFetchData(inst, pc, alu_outLO, jump, mispredicted_branch);
		
		// to BRANCH PREDICTION MODULE
		ExecuteBranchPredictionData ebd = new ExecuteBranchPredictionData(inst, pc, alu_outLO, jump);
	
		// MIPS ISA flavour always is allowed to stall, DLX only if enabled
		if((ArchCfg.isa_type == ISAType.MIPS) || (ArchCfg.use_load_stall_bubble == true))
		{
			// check if the instruction before was a load that writes into a src register,
			// if so the fetch, decode, and execute stages have to be stalled for 1 cycle to let this instruction enter the memory stage 
			if(fw_emd_inst.getLoad())
			{
				boolean do_stall=false;
				byte ld_tgt_register = 0;
				if(fw_emd_inst.getWriteRd())
				{
					ld_tgt_register = (byte)fw_emd_inst.getRd().getValue();
				}
				else if(fw_emd_inst.getWriteRt())
				{
					ld_tgt_register = (byte)fw_emd_inst.getRt().getValue();
				}
				else
				{
					throw new ExecuteStageException("Missing load target register.");
				}

				if(inst.getReadRs() && (inst.getRs().getValue() == ld_tgt_register))
				{
					do_stall = true;
				}

				if(inst.getReadRt() && (inst.getRt().getValue() == ld_tgt_register))
				{
					do_stall = true;
				}

				if(do_stall)
				{
					stall_out[PipelineConstants.FETCH_STAGE] = true;
					stall_out[PipelineConstants.DECODE_STAGE] = true;
					stall_out[PipelineConstants.EXECUTE_STAGE] = true;
				}
			}
		}
		
		return new ExecuteOutputData(emd, efd, ebd, stall_out);

	}

	private void forwarding(SpecialRegisters reg_read, uint32 alu_in, uint32[] old_alu_result, Instruction old_inst)
	{

		if (old_inst.getWriteLO() && (reg_read == SpecialRegisters.LO))
		{
			logger.debug("{FW} using " + old_alu_result[0].getValueAsHexString() + " for register " + reg_read + " instead of value: " + alu_in.getValueAsHexString());
			alu_in.setValue(old_alu_result[0]);
		}

		if (old_inst.getWriteHI() && (reg_read == SpecialRegisters.HI))
		{
			logger.debug("{FW} using " + old_alu_result[1].getValueAsHexString() + " for register " + reg_read + " instead of value: " + alu_in.getValueAsHexString());
			alu_in.setValue(old_alu_result[1]);
		}

	}

	private void forwarding(uint8 reg_read, uint32 alu_in, uint32 old_alu_result, uint32 old_ld_result, Instruction old_inst)
	{

		if (old_inst.getWriteRd() && (reg_read.getValue() == old_inst.getRd().getValue()))
		{
			if(old_inst.getRd().getValue() != 0)
			{
				if(old_inst.getLoad())
				{
					logger.debug("{FW} using LD result " + old_ld_result.getValueAsHexString() + " for register " + reg_read.getValue() + "/" + ArchCfg.getRegisterDescription(reg_read.getValue()) + " instead of value: " + alu_in.getValueAsHexString());
					alu_in.setValue(old_ld_result);
				}
				else
				{
					logger.debug("{FW} using ALU result " + old_alu_result.getValueAsHexString() + " for register " + reg_read.getValue() + "/" + ArchCfg.getRegisterDescription(reg_read.getValue()) + " instead of value: " + alu_in.getValueAsHexString());
					alu_in.setValue(old_alu_result);
				}
			}
			else
			{
				logger.info("{FW} suppressing forwarding of register 0/" + ArchCfg.getRegisterDescription(0));
			}
		}

		if (old_inst.getWriteRt() && (reg_read.getValue() == old_inst.getRt().getValue()))
		{
			if(old_inst.getRt().getValue() != 0)
			{
				if(old_inst.getLoad())
				{
					logger.debug("{FW} using LD result " + old_ld_result.getValueAsHexString() + " for register " + reg_read.getValue() + "/" + ArchCfg.getRegisterDescription(reg_read.getValue()) + " instead of value: " + alu_in.getValueAsHexString());
					alu_in.setValue(old_ld_result);
				}
				else
				{
					logger.debug("{FW} using ALU result " + old_alu_result.getValueAsHexString() + " for register " + reg_read.getValue() + "/" + ArchCfg.getRegisterDescription(reg_read.getValue()) + " instead of value: " + alu_in.getValueAsHexString());
					alu_in.setValue(old_alu_result);
				}
			}
			else
			{
				logger.info("{FW} suppressing forwarding of register 0/" + ArchCfg.getRegisterDescription(0));
			}
		}
	}

}
