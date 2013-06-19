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

public class ExecuteOutputData
{
	// synchronous output
	private ExecuteMemoryData emd; // to memory stage
	private ExecuteFetchData efd; // to fetch stage
	private ExecuteBranchPredictionData ebd; // to branch prediction module
	
	// asynchronous output
	private boolean[] stall;
	
	public ExecuteOutputData(ExecuteMemoryData emd, ExecuteFetchData efd, ExecuteBranchPredictionData ebd, boolean[] stall)
	{
		this.emd = emd;
		this.efd = efd;
		this.ebd = ebd;
		this.stall = stall;
	}

	public ExecuteMemoryData getEmd()
	{
		return emd;
	}

	public ExecuteFetchData getEfd()
	{
		return efd;
	}
	
	public ExecuteBranchPredictionData getEbd()
	{
		return ebd;
	}

	public boolean[] getStall()
	{
		return stall;
	}
}
