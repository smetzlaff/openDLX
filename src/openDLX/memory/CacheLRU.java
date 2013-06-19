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

public class CacheLRU extends Cache {
	
	private int lru_replacement_counters[][];


	public CacheLRU(CacheType type, int line_size, int line_no,	int associativity, MainMemory mem) throws CacheException, PipelineDataTypeException 
	{
		super(type, line_size, line_no, associativity, mem);
		
		initializeRPolCounters();
	}
	
	public CacheLRU(CacheType type, int line_size, int line_no,	int associativity, DCacheWritePolicy write_policy, MainMemory mem) throws CacheException, PipelineDataTypeException 
	{
		super(type, line_size, line_no, associativity, write_policy, mem);
		
		initializeRPolCounters();
	}
	
	private void initializeRPolCounters()
	{
		lru_replacement_counters = new int[associativity][lines_per_set];
		for(int j = 0; j < associativity; j++)
		{
			for(int i = 0; i < lines_per_set; i++)
			{
				lru_replacement_counters[j][i]=associativity;
			}
		}
	}

	protected int getCacheWayForReplacement(uint32 addr) throws CacheException 
	{
		int way = associativity;
		int lru_counter = 0;
		int index = getIndex(addr);
		
		for(int i = 0; i < associativity; i++)
		{
			if(lru_counter < lru_replacement_counters[i][index])
			{
				lru_counter = lru_replacement_counters[i][index];
				way = i;
			}
		}
		
		if((way == associativity) || (lru_counter == 0))
		{
			throw new CacheException("Cache way could not be found for cache hit.");
		}
		
		return way;
	}

	protected void updateReplacementCountersOnAccess(int way, int index) 
	{
		for(int i = 0; i < associativity; i++)
		{
			if(i == way)
			{
				lru_replacement_counters[i][index] = 0;
			}
			else
			{
				if(lru_replacement_counters[i][index] < associativity-1)
				{
					lru_replacement_counters[i][index]++;
				}
			}
		}

	}

	protected void updateReplacementCountersOnMiss(int way, int index) 
	{
		updateReplacementCountersOnAccess(way, index);
	}

}
