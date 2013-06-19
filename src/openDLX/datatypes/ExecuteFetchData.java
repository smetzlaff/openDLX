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


public class ExecuteFetchData
{

	private Instruction inst;
	private uint32 pc;
	private uint32 new_pc;
	private boolean jump;
	private boolean mispredicted_branch;

	public ExecuteFetchData(Instruction inst, uint32 pc, uint32 new_pc, boolean jump, boolean mispredictedBranch)
	{
		this.inst = inst;
		this.pc = pc;
		this.new_pc = new_pc;
		this.jump = jump;
		this.mispredicted_branch = mispredictedBranch;
	}

	public uint32 getNewPc()
	{
		return new_pc;
	}

	public uint32 getPc()
	{
		return pc;
	}

	public boolean getJump()
	{
		return jump;
	}
	
	public Instruction getInst()
	{
		return inst;
	}
	
	public boolean getMispredictedBranch()
	{
		return mispredicted_branch;
	}
	
	public void flush()
	{
		inst = new Instruction(new uint32(0));
		new_pc = new uint32(0);
		jump = false;
		mispredicted_branch = false;
	}

}
