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

public class ExecuteBranchPredictionData
{
	private Instruction inst;
	private uint32 branch_pc;
	private uint32 branch_tgt;
	private boolean jump_taken;

	public ExecuteBranchPredictionData(Instruction inst, uint32 branchPc, uint32 branchTgt, boolean jumpTaken)
	{
		this.inst = inst;
		this.branch_pc = branchPc;
		this.branch_tgt = branchTgt;
		this.jump_taken = jumpTaken;
	}

	public Instruction getInst()
	{
		return inst;
	}

	public uint32 getBranchPc()
	{
		return branch_pc;
	}

	public uint32 getBranchTgt()
	{
		return branch_tgt;
	}

	public boolean getJumpTaken()
	{
		return jump_taken;
	}

}
