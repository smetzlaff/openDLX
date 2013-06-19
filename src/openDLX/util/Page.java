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

import openDLX.exception.MemoryException;

public class Page
{
	private byte[] page_memory;
	public static final int page_size = 16384; // has to be the power of two!!
	
	public Page() throws MemoryException
	{
		// enforce that the page size is a power of two
		if(Integer.bitCount(page_size) != 1)
		{
			throw new MemoryException("Page size has to be a power of two, but it is: " + page_size);
		}
		
		page_memory = new byte[page_size];
		for(int i = 0; i<page_size; i++)
		{
			page_memory[i] = 0;
		}
	}

	public void writeByte(int address, byte data)
	{
		page_memory[address] = data;
	}
	
	public byte readByte(int address)
	{
		return page_memory[address];
	}

}
