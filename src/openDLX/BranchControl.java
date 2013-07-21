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

import org.apache.log4j.Logger;

import openDLX.datatypes.*;

public class BranchControl
{

	private static Logger logger = Logger.getLogger("EXECUTE/BCTRL");
	
	public boolean checkBranch(Instruction inst, uint32 A, uint32 B)
	{
		boolean jump = false;
		switch(inst.getBranchCondition())
		{
		case BEQ:
			if(A.getValue() == B.getValue())
			{
				jump = true;
			}
			break;
		case BNE:
			if(A.getValue() != B.getValue())
			{
				jump = true;
			}
			break;
		case BGEZ:
			if(A.getValue() >= 0)
			{
				jump = true;
			}
		case BGTZ:
			if(A.getValue() > 0)
			{
				jump = true;
			}
			break;
		case BLEZ:
			if(A.getValue() <= 0)
			{
				jump = true;
			}
			break;
		case BLTZ:
			if(A.getValue() < 0)
			{
				jump = true;
			}
			break;
		case UNCOND:
			jump = true;
			break;
		default:
			jump = false;
		}
		if(inst.getBranch())
		{
			logger.debug("A: " + A.getValueAsHexString() + " " + inst.getBranchCondition() + " B: " + B.getValueAsHexString() + " jump: " + jump);
		}
		return jump;
	}
}
