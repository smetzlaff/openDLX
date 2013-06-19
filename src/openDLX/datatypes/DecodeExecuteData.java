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

public class DecodeExecuteData
{

	private Instruction inst;
	private uint32 pc;
	private uint32 alu_in_a;
	private uint32 alu_in_b;
	private uint32 branch_ctrl_in_a;
	private uint32 branch_ctrl_in_b;
	private uint32 store_value;

	public DecodeExecuteData(Instruction inst, uint32 pc, uint32 alu_in_a, uint32 alu_in_b, uint32 branch_ctrl_in_a, uint32 branch_ctrl_in_b, uint32 store_value)
	{
		this.inst = inst;
		this.pc = pc;
		this.alu_in_a = alu_in_a;
		this.alu_in_b = alu_in_b;
		this.branch_ctrl_in_a = branch_ctrl_in_a;
		this.branch_ctrl_in_b = branch_ctrl_in_b;
		this.store_value = store_value;
	}

	public Instruction getInst()
	{
		return inst;
	}

	public uint32 getPc()
	{
		return pc;
	}
	
	public uint32 getAluInA()
	{
		return alu_in_a;
	}

	public uint32 getAluInB()
	{
		return alu_in_b;
	}

	public uint32 getBranchCtrlInA()
	{
		return branch_ctrl_in_a;
	}

	public uint32 getBranchCtrlInB()
	{
		return branch_ctrl_in_b;
	}

	public uint32 getStoreValue()
	{
		return store_value;
	}

	public void flush()
	{
		inst = new Instruction(PipelineConstants.PIPELINE_BUBBLE_INSTR);
		pc = PipelineConstants.PIPELINE_BUBBLE_ADDR;
		alu_in_a = new uint32(0);
		alu_in_b = new uint32(0);
		store_value = new uint32(0);
	}
}
