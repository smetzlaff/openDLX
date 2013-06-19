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

import openDLX.datatypes.uint32;
import openDLX.exception.MemoryException;

import org.apache.log4j.Logger;

public class PagedMemory
{	
	private static Logger logger = Logger.getLogger("PagedMem");
	private Page[] pages;
	private int page_addr_bits;
	private int page_count;
	
	public PagedMemory()
	{
		// allocate array of 2^(32-13) 16k pages
		page_addr_bits = 32 - ((int) (Math.log(Page.page_size)/Math.log(2)));
		page_count = (int) Math.pow(2, page_addr_bits);
		pages = new Page[page_count];
		
		logger.debug("Reserving " + page_count + " pages, using the upper " + page_addr_bits + " bits for page addressing");
	}
	
	public void writeByte(uint32 address, byte data) throws MemoryException
	{
		writeByte(address.getValue(), data);
	}
	
	public void writeByte(int address, byte data) throws MemoryException
	{
		// determine the page by the upper 18 bit
		int page_number = address >>> (32-page_addr_bits);
		
		//logger.trace("Accessing page: " + page_number + " 0x" + Integer.toHexString(page_number));
		if(page_number >= page_count)
		{
			throw new MemoryException("Page number out of bounds: " + page_number + "/" + page_count);
		}
		
		// allocate page if not already allocated
		if(pages[page_number] == null)
		{
			pages[page_number] = new Page();
		}
		
		// determine the lower 13 bit for addressing within the 16k pages
		// and write data to page
		pages[page_number].writeByte((address&(Page.page_size-1)), data);
	}
	
	public byte readByte(uint32 address) throws MemoryException
	{
		return readByte(address.getValue());
	}
	
	public byte readByte(int address) throws MemoryException
	{
		// determine the page by the upper 18 bit
		int page_number = address >>> (32-page_addr_bits);
		
		//logger.trace("Accessing page: " + page_number + " 0x" + Integer.toHexString(page_number));
		if(page_number >= page_count)
		{
			throw new MemoryException("Page number out of bounds: " + page_number + "/" + page_count);
		}
		
		// allocate page if not already allocated
		if(pages[page_number] == null)
		{
			logger.warn("Reading from unallocated page!");
			pages[page_number] = new Page();
		}
		
		// determine the lower 13 bit for addressing within the 16k pages
		// and write data to page
		return pages[page_number].readByte((address&(Page.page_size-1)));
	}
	
	public String readByteAsString(uint32 address) throws MemoryException
	{
		return readByteAsString(address.getValue());
	}
	
	public String readByteAsString(int address) throws MemoryException
	{
		byte value = readByte(address);
		
		String s = Integer.toHexString((byte)value);
		int diff = 2 - s.length();
		
		if(diff > 0)
		{
		for(;diff > 0; diff--)
			s = "0"+s;
		}
		else if(diff < 0)
		{
			s = s.substring(s.length()-2, s.length());
		}
		
		return "0x"+s;
	}
	

	/**
	 * TODO ..
	 * @throws MemoryException 
	 */
	public int readByteDump(int address) throws MemoryException
	{
		// determine the page by the upper 18 bit
		int page_number = address >>> (32-page_addr_bits);

		if(page_number >= page_count)
		{
			throw new MemoryException("Page number out of bounds: " + page_number + "/" + page_count);
		}

		// check if page was not allocated
		if(pages[page_number] == null)
		{
			// do not allocate page here, since only a memory dump is created
			// show non allocated pages in a memory dump with values of 0x0
			return 0;
		}
		else
		{
			// determine the lower 13 bit for addressing within the 16k pages
			// and write data to page
			return pages[page_number].readByte((address&(Page.page_size-1)));
		}
	}
	

}
