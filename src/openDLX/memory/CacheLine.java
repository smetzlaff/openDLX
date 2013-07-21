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

import openDLX.PipelineConstants;
import openDLX.datatypes.uint32;
import openDLX.datatypes.uint8;
import openDLX.exception.CacheException;

public class CacheLine {

	private uint32 tag;
	private byte line[];
	private int bytes_per_line;
	private boolean dirty;
	private boolean valid;
	
	public CacheLine(int line_size, int tag_size) throws CacheException
	{
		if(line_size % PipelineConstants.WORD_SIZE != 0)
		{
			throw new CacheException("The cache line size has to be a multiple of the word size (" + PipelineConstants.WORD_SIZE + "), but it is set to " + line_size);
		}
		
		bytes_per_line = line_size;
		tag = new uint32(0);
		line = new byte[line_size];
		
		for(int i = 0; i < line_size; i++)
		{
			line[i] = 0;
		}
		
		dirty = false;
		valid = false;
	}	
	
	public boolean compareTag(uint32 tag)
	{
		return (this.tag.getValue() == tag.getValue());
	}
	
	public uint32 getWord(int block_offset) throws CacheException
	{
		if(!valid)
		{
			throw new CacheException("Cannot read from cache line, it is not valid. tag: " + tag.getValueAsHexString());
		}
		if(block_offset % PipelineConstants.WORD_SIZE != 0)
		{
			throw new CacheException("Block offset of readWord() has to be a multiple of the word size (" + PipelineConstants.WORD_SIZE + "), but it is set to " + block_offset);
		}
		if(block_offset >= bytes_per_line)
		{
			throw new CacheException("Block offset out of range: " + block_offset + "/" + bytes_per_line);
		}
		
		return new uint32((line[block_offset] & 0xFF) | ((line[block_offset+1] & 0xFF) << 8) | ((line[block_offset+2] & 0xFF) << 16) | ((line[block_offset+3] & 0xFF) << 24));
	}
	
	public void setWord(int block_offset, uint32 value) throws CacheException 
	{
		if(!valid)
		{
			throw new CacheException("Cannot write to cache line, it is not valid. tag: " + tag.getValueAsHexString());
		}
		if(block_offset % PipelineConstants.WORD_SIZE != 0)
		{
			throw new CacheException("Block offset of setWord() has to be a multiple of the word size (" + PipelineConstants.WORD_SIZE + "), but it is set to " + block_offset);
		}
		if(block_offset >= bytes_per_line)
		{
			throw new CacheException("Block offset out of range: " + block_offset + "/" + bytes_per_line);
		}
		
		
		line[block_offset] = (byte) (value.getValue() & 0xFF);
		line[block_offset + 1] = (byte) ((value.getValue() >> 8) & 0xFF);
		line[block_offset + 2] = (byte) ((value.getValue() >> 16) & 0xFF);
		line[block_offset + 3] = (byte) ((value.getValue() >> 24) & 0xFF);
	}
	
	public uint8 getByte(int block_offset) throws CacheException
	{
		if(!valid)
		{
			throw new CacheException("Cannot read from cache line, it is not valid. tag: " + tag.getValueAsHexString());
		}
		if(block_offset >= bytes_per_line)
		{
			throw new CacheException("Block offset out of range: " + block_offset + "/" + bytes_per_line);
		}
		
		return new uint8(line[block_offset]);
	}
	
	public void setByte(int block_offset, uint8 value) throws CacheException
	{
		if(!valid)
		{
			throw new CacheException("Cannot write to cache line, it is not valid. tag: " + tag.getValueAsHexString());
		}
		if(block_offset >= bytes_per_line)
		{
			throw new CacheException("Block offset out of range: " + block_offset + "/" + bytes_per_line);
		}
		
		line[block_offset]= value.getValue();
		
	}
	
	public void setLine(uint32 tag, byte line[])
	{
		this.tag.setValue(tag);
		
		for(int i = 0; i < bytes_per_line; i++)
		{
			this.line[i] = line[i];
		}
		
		valid = true;
	}
	
	public void setDirty(boolean dirty)
	{
		this.dirty = dirty;
	}
	
	public boolean isDirty()
	{
		return dirty;
	}

	public String dumpLine() 
	{
		String s;
		s = tag.getValueAsHexString() + " ";
		for(int i = 0; i < bytes_per_line; i++)
		{
			s += "0x" + Integer.toHexString(line[i]) + " "; 
		}
		s += "valid: " + valid + " dirty: " + dirty;
		return s;
	}

}
