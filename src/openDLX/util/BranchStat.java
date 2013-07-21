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
package openDLX.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import openDLX.datatypes.uint32;

public class BranchStat
{
	private uint32 branch_addr;
	private int btb_idx;
	private ArrayList<uint32> branch_tgts;
	private int accesses;
	private int taken;
	private int not_taken;
	private int correctly_predicted;
	private int mispredicted;
	
	public BranchStat(uint32 branchAddr, int btbIdx, uint32 branchTgt, boolean branching, boolean correctPrediction)
	{
		branch_addr = new uint32(branchAddr);
		btb_idx = btbIdx;
		
		branch_tgts = new ArrayList<uint32>();
		
		update(branchTgt, branching, correctPrediction);
	}
	
	public boolean isBranchAddr(uint32 branchAddr)
	{
		return (branch_addr.getValue() == branchAddr.getValue());
	}
	
	
	public void update(uint32 branchTgt, boolean branching, boolean correctPrediction)
	{
		if(!branch_tgts.contains(branchTgt))
		{
			branch_tgts.add(new uint32(branchTgt));
		}
	    
		accesses++;
		
		if(branching)
		{
			taken++;
		}
		else
		{
			not_taken++;
		}
		if(correctPrediction)
		{
			correctly_predicted++;
		}
		else
		{
			mispredicted++;
		}
	}

	public int getAccesses()
	{
		return accesses;
	}
	
	public uint32 getBranchAddr()
	{
		return branch_addr;
	}
	
	public String toString()
	{
		DecimalFormat f = new DecimalFormat("###.##");
		return new String("bpc: " + branch_addr.getValueAsHexString() + " [" + btb_idx + "] tgts: " + branch_tgts.toString() + " a:" + accesses + " t/nt: " + taken + "/" + not_taken + " mp/cp: " + mispredicted + "/" + correctly_predicted + " mp-ratio: " + f.format((double)mispredicted/((double)accesses)));
	}
}
