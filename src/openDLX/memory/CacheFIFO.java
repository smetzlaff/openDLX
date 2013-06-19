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
package openDLX.memory;

import openDLX.datatypes.CacheType;
import openDLX.datatypes.DCacheWritePolicy;
import openDLX.datatypes.uint32;
import openDLX.exception.CacheException;
import openDLX.exception.PipelineDataTypeException;

public class CacheFIFO extends Cache 
{
	private int fifo_way_counter[];

	public CacheFIFO(CacheType type, int line_size, int line_no, int associativity, DCacheWritePolicy write_policy, MainMemory mem) throws CacheException, PipelineDataTypeException 
	{
		super(type, line_size, line_no, associativity, write_policy, mem);
		
		initializeRPolCounters();
	}

	public CacheFIFO(CacheType type, int line_size, int line_no, int associativity, MainMemory mem) throws CacheException, PipelineDataTypeException 
	{
		super(type, line_size, line_no, associativity, mem);
		
		initializeRPolCounters();
	}
	
	private void initializeRPolCounters()
	{
		fifo_way_counter = new int[lines_per_set];
	}

	protected int getCacheWayForReplacement(uint32 addr) 
	{
		int index = getIndex(addr);
		
		return fifo_way_counter[index];
	}

	protected void updateReplacementCountersOnAccess(int way, int index) 
	{
		// nothing to do
	}

	protected void updateReplacementCountersOnMiss(int way, int index) 
	{
		fifo_way_counter[index]++;
		fifo_way_counter[index] = fifo_way_counter[index] % associativity;
	}

}
