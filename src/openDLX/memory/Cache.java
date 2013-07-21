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

import org.apache.log4j.Logger;

import openDLX.PipelineConstants;
import openDLX.datatypes.CacheReplacementPolicy;
import openDLX.datatypes.CacheType;
import openDLX.datatypes.DCacheWritePolicy;
import openDLX.datatypes.RequestType;
import openDLX.datatypes.uint32;
import openDLX.datatypes.uint8;
import openDLX.exception.CacheException;
import openDLX.exception.MemoryException;
import openDLX.exception.PipelineDataTypeException;
import openDLX.util.CacheAddressCalculator;
import openDLX.util.CalculationHelper;
import openDLX.util.Statistics;

public abstract class Cache implements MemoryInterface {

	private Logger logger; 
	
	protected Statistics stat = Statistics.getInstance();
	
	protected CacheType cache_type;
	
	protected MainMemory mem;
	/// Size of the cache line in bytes 
	protected int line_size;
	protected int words_per_line;
	protected int line_no;
	protected int lines_per_set;
	protected int associativity;
	
	protected CacheReplacementPolicy policy;	
	private DCacheWritePolicy write_policy;

	
	protected int block_offset_size;
	protected int tag_size;
	protected int index_size;
	
	protected CacheLine cache_memory[][];
	
	public Cache(CacheType type, int line_size, int line_no, int associativity, DCacheWritePolicy write_policy, MainMemory mem) throws CacheException, PipelineDataTypeException 
	{
		if(type != CacheType.DCACHE)
		{
			throw new CacheException("Cache type: " + type + " is not by this constructor.");
		}
		
		this.cache_type = type;
		this.line_size = line_size;
		this.line_no = line_no;
		this.associativity = associativity;
		this.mem = mem;
		this.write_policy = write_policy;
		
		initialize();
	}
	
	public Cache(CacheType type, int line_size, int line_no, int associativity, MainMemory mem) throws CacheException, PipelineDataTypeException 
	{
		if(type != CacheType.ICACHE)
		{
			throw new CacheException("Cache type: " + cache_type + " is by in this constructor.");
		}
		
		this.cache_type = type;
		this.line_size = line_size;
		this.line_no = line_no;
		this.associativity = associativity;
		this.mem = mem;
		// ICACHE has no write policy
		this.write_policy = DCacheWritePolicy.UNKNOWN;
		
		initialize();
	}
	
	private void initialize() throws CacheException, PipelineDataTypeException
	{
		if(cache_type == CacheType.ICACHE)
		{
			logger = Logger.getLogger("ICache");
		}
		else if(cache_type == CacheType.DCACHE)
		{
			logger = Logger.getLogger("DCache");
		}
		else
		{
			logger = Logger.getLogger("UNKNOWNCache");
		}
		
		// enforce that the line number, line size and the associativity is a power of two
		if(Integer.bitCount(line_size) != 1)
		{
			throw new CacheException("The size of the cache lines has to be a power of two, but it is: " + line_size);
		}
		if(Integer.bitCount(line_no) != 1)
		{
			throw new CacheException("The number of cache lines has to be a power of two, but it is: " + line_no);
		}
			
		if((Integer.bitCount(associativity) != 1) && (associativity != 0))
		{
			throw new CacheException("The cache associativity has to be a power of two, but it is: " + associativity);
		}
		
		if(associativity == 0)
		{
			// fully associative cache:
			associativity = line_no;
		}
		
		if(line_no < associativity)
		{
			throw new CacheException("The cache associativity cannot be higher than the number of cache lines. Associativity: " + associativity + " Cache lines: " + line_no);
		}
		
		lines_per_set = line_no / associativity;
		
		cache_memory = new CacheLine[this.associativity][this.lines_per_set];
		
		CacheAddressCalculator cac = new CacheAddressCalculator(this.line_no, this.line_size, this.associativity, PipelineConstants.ADDR_WIDTH);
		
		tag_size = cac.getTagSize();
		index_size = cac.getIndexSize();
		block_offset_size = cac.getBlockOffsetSize();
		
		if(line_size % PipelineConstants.WORD_SIZE != 0)
		{
			throw new CacheException("The cache line size has to be a multiple of the word size (" + PipelineConstants.WORD_SIZE + "), but it is set to " + line_size);
		}
		
		words_per_line = cac.getWordsPerLine();
		
		
		for(int j = 0; j < this.associativity; j++)
		{
			for(int i = 0; i < this.lines_per_set; i++)
			{
				cache_memory[j][i] = new CacheLine(this.line_size, this.tag_size);
			}
		}
		
		logger.info("Initialized " + cache_type + " with " + associativity + " ways, " + line_no + " lines, " + lines_per_set + " lines per set, " + words_per_line + " words per line, " + tag_size + " bits for tag, " + index_size + " bits for index, and " + block_offset_size + " bits for block offset. Cache is of type: " + this.getClass());
	}

	public uint32 read_u32(uint32 addr) throws MemoryException 
	{
		return read_u32(addr, false);
	}


	public uint32 read_u32(uint32 addr, boolean log_output) throws MemoryException 
	{
		
		uint32 value = new uint32();
		
		if(log_output)
		{
			logger.debug("Read u32 from addr: " + addr.getValueAsHexString());
		}
		
		if(isHit(addr))
		{
			int index = getIndex(addr);
			int way = getCacheWayForHit(addr);
			value.setValue(cache_memory[way][index].getWord(getBlockOffset(addr)));
			
			if(log_output)
			{
				logger.debug("Hit in way " + way + " in cache line " + index + " for address " + addr.getValueAsHexString() + " value: " + value.getValueAsHexString());
				dumpCacheLine(index);
			}
			
			updateReplacementCountersOnAccess(way, index);
			
			stat.countCacheHit(cache_type);
			
		}
		else
		{
			int cache_line_address = getCacheLineAddr(addr);
			
			byte line[] = new byte[line_size];
			
			for(int i = 0; i < line_size; i++)
			{
				line[i] = mem.read_u8(new uint32(cache_line_address + i)).getValue();
			}
			
			value.setValue(mem.read_u32(addr));
			int index = getIndex(addr);
			int way = getCacheWayForReplacement(addr);
			
			logger.debug("Accessing way: " + way + " index: " + index);
			
			cache_memory[way][index].setLine(getTagFromAddress(addr), line);
			
			if(log_output)
			{
				logger.debug("Miss in cache for address " + addr.getValueAsHexString() + " replaced cache line " + index + " in way " + way + " loaded value: " + value.getValueAsHexString());
				dumpCacheLine(index);
			}

			updateReplacementCountersOnMiss(way, index);
			
			stat.countCacheMiss(cache_type);
		}
		
		return value;
	}
	
	public uint8 read_u8(uint32 addr, boolean log_output) throws MemoryException 
	{
		if(cache_type != CacheType.DCACHE)
		{
			throw new CacheException("Method read_u8() only supports data caches, but cache type is: " + cache_type); 
		}
		
		uint8 value = new uint8();
		
		if(log_output)
		{
			logger.debug("Read u8 from addr: " + addr.getValueAsHexString());
		}
		
		if(isHit(addr))
		{
			int index = getIndex(addr);
			int way = getCacheWayForHit(addr);
			value.setValue(cache_memory[way][index].getByte(getBlockOffset(addr)));
			
			if(log_output)
			{
				logger.debug("Hit in way " + way + " in cache line " + index + " for address " + addr.getValueAsHexString() + " value: " + value.getValueAsHexString() + " (read byte " + (getBlockOffset(addr)&0x3) + " from word " + cache_memory[way][index].getWord(getBlockOffset(addr)&(~0x3)) + ")");
				dumpCacheLine(index);
			}
			
			updateReplacementCountersOnAccess(way, index);
		}
		else
		{
			int cache_line_address = getCacheLineAddr(addr);
			
			byte line[] = new byte[line_size];
			
			for(int i = 0; i < line_size; i++)
			{
				line[i] = mem.read_u8(new uint32(cache_line_address + i)).getValue();
			}
			
			value.setValue(mem.read_u8(addr));
			int index = getIndex(addr);
			int way = getCacheWayForReplacement(addr);
			cache_memory[way][index].setLine(getTagFromAddress(addr), line);
			
			if(log_output)
			{
				logger.debug("Miss in cache for address " + addr.getValueAsHexString() + " replaced cache line " + index + " in way " + way + " loaded value: " + value.getValueAsHexString() + " (read byte " + (getBlockOffset(addr)&0x3) + " from word " + cache_memory[way][index].getWord(getBlockOffset(addr)&(~0x3)) + ")");
				dumpCacheLine(index);
			}
			
			updateReplacementCountersOnMiss(way, index);
		}
		
		return value;
	}

	public void write_u32(uint32 addr, uint32 value) throws MemoryException 
	{
		if(cache_type != CacheType.DCACHE)
		{
			throw new CacheException("Method write_u32() only supports data caches, but cache type is: " + cache_type); 
		}
		
		if(write_policy != DCacheWritePolicy.WRITE_THROUGH)
		{
			throw new CacheException("Currently only write through caches are supported, but cache write policy is: " + write_policy);
		}
		
		
		if((addr.getValue()&0x3 ) != 0)
		{
			logger.error("Write u32 to unaligned addr: " + addr.getValueAsHexString());
			throw new CacheException("Write u32 to unaligned addr: " + addr.getValueAsHexString());
		}
		
		logger.debug("Write u32 to addr: " + addr.getValueAsHexString() + " value: " + value.getValueAsHexString());
		
		
		if(isHit(addr))
		{
			int index = getIndex(addr);
			int way = getCacheWayForHit(addr);
			uint32 old_value = cache_memory[way][index].getWord(getBlockOffset(addr));
			
			cache_memory[way][index].setWord(getBlockOffset(addr), value);
			
			
			logger.debug("Hit in way " + way + " in cache line " + index + " for address " + addr.getValueAsHexString() + " old value: " + old_value.getValueAsHexString() + " new value: " + cache_memory[way][index].getWord(getBlockOffset(addr)));
			dumpCacheLine(index);
			
			updateReplacementCountersOnAccess(way, index);
			
		}
		else
		{
			int cache_line_address = getCacheLineAddr(addr);
			uint32 old_value = new uint32();
			
			byte line[] = new byte[line_size];
			
			for(int i = 0; i < line_size; i++)
			{
				line[i] = mem.read_u8(new uint32(cache_line_address + i)).getValue();
			}
			
			old_value.setValue(mem.read_u32(addr));
			int index = getIndex(addr);
			int way = getCacheWayForReplacement(addr);

			// load cache line from memory
			cache_memory[way][index].setLine(getTagFromAddress(addr), line);
			
			// write word into cache
			cache_memory[way][index].setWord(getBlockOffset(addr), value);
			
			logger.debug("Miss in cache for address " + addr.getValueAsHexString() + " replaced cache line " + index + " in way " + way + " old_value: " + old_value.getValueAsHexString() + " new value: " + cache_memory[way][index].getWord(getBlockOffset(addr)));
			dumpCacheLine(index);

			updateReplacementCountersOnMiss(way, index);
		}

		if(write_policy == DCacheWritePolicy.WRITE_THROUGH)
		{
			// also always write value into memory
			mem.write_u32(addr, value);
		}

	}

	public void write_u8(uint32 addr, uint32 value) throws MemoryException 
	{
		write_u8(addr, new uint8(value.getValue()));
	}

	public void write_u8(uint32 addr, uint8 value) throws MemoryException 
	{
		if(cache_type != CacheType.DCACHE)
		{
			throw new CacheException("Method write_u32() only supports data caches, but cache type is: " + cache_type); 
		}
		
		if(write_policy != DCacheWritePolicy.WRITE_THROUGH)
		{
			throw new CacheException("Currently only write through caches are supported, but cache write policy is: " + write_policy);
		}
	
		
		logger.debug("Write u8 to addr: " + addr.getValueAsHexString() + " value: " + value.getValueAsHexString());
		
		
		if(isHit(addr))
		{
			int index = getIndex(addr);
			int way = getCacheWayForHit(addr);
			uint8 old_value = cache_memory[way][index].getByte(getBlockOffset(addr));
			
			cache_memory[way][index].setByte(getBlockOffset(addr), value);
			
			
			logger.debug("Hit in way " + way + " in cache line " + index + " for address " + addr.getValueAsHexString() + " old value: " + old_value.getValueAsHexString() + " new value: " + cache_memory[way][index].getByte(getBlockOffset(addr)) + " (written byte " + (getBlockOffset(addr)&0x3) + " of word " + cache_memory[way][index].getWord(getBlockOffset(addr)&(~0x3)) + ")");
			dumpCacheLine(index);
			
			updateReplacementCountersOnAccess(way, index);
			
		}
		else
		{
			int cache_line_address = getCacheLineAddr(addr);
			uint32 old_value = new uint32();
			
			byte line[] = new byte[line_size];
			
			for(int i = 0; i < line_size; i++)
			{
				line[i] = mem.read_u8(new uint32(cache_line_address + i)).getValue();
			}
			
			old_value.setValue(mem.read_u32(addr));
			int index = getIndex(addr);
			int way = getCacheWayForReplacement(addr);

			// load cache line from memory
			cache_memory[way][index].setLine(getTagFromAddress(addr), line);
			
			// write word into cache
			cache_memory[way][index].setByte(getBlockOffset(addr), value);
			
			logger.debug("Miss in cache for address " + addr.getValueAsHexString() + " replaced cache line " + index + " in way " + way + " old_value: " + old_value.getValueAsHexString() + " new value: " + cache_memory[way][index].getByte(getBlockOffset(addr)) + " (written byte " + (getBlockOffset(addr)&0x3) + " of word " + cache_memory[way][index].getWord(getBlockOffset(addr)&(~0x3)) + ")");
			dumpCacheLine(index);

			updateReplacementCountersOnMiss(way, index);
		}

		if(write_policy == DCacheWritePolicy.WRITE_THROUGH)
		{
			// also always write value into memory
			mem.write_u8(addr, value);
		}
		
	}
	
	protected uint32 getTagFromAddress(uint32 addr)
	{
		int mask = ~(CalculationHelper.generateBitStringOfOnes(index_size + block_offset_size));
		
		int tag = addr.getValue() & mask;
		
		return new uint32(tag);
	}
	
	protected int getIndex(uint32 addr)
	{
		int mask = (CalculationHelper.generateBitStringOfOnes(index_size));
		
		logger.debug("idx: " + Integer.toHexString(CalculationHelper.generateBitStringOfOnes(index_size)) + " index mask: 0x"  + Integer.toHexString(mask) + " value: " + addr.getValueAsHexString() + " result: 0x" + Integer.toHexString((addr.getValue()>> block_offset_size) & mask));
		
		int index = (addr.getValue()>> (block_offset_size)) & mask;
		
		return index;
	}
	
	protected int getBlockOffset(uint32 addr)
	{
		return addr.getValue() & (CalculationHelper.generateBitStringOfOnes(block_offset_size));
	}
	
	protected int getCacheLineAddr(uint32 addr)
	{
		return addr.getValue() & ~(CalculationHelper.generateBitStringOfOnes(block_offset_size));
	}
	
	
	public short getRequestDelay(RequestType type, uint32 addr) throws MemoryException 
	{
		short latency = 0;
		
		switch(this.cache_type)
		{
		case ICACHE:
		{
			if(type != RequestType.INSTR_RD)
			{
				throw new CacheException("Unsupported request type for instruction cache: " + type);
			}

			if(isHit(addr))
			{
				latency = 0;
			}
			else
			{
				latency = mem.getRequestDelay(type, addr);
			}
			break;
		}
		case DCACHE:
		{
			if((type != RequestType.DATA_RD) && (type != RequestType.DATA_WR))
			{
				throw new CacheException("Unsupported request type for data cache: " + type);
			}

			if(type == RequestType.DATA_RD)
			{
				if(isHit(addr))
				{
					latency = 0;
				}
				else
				{
					latency = mem.getRequestDelay(type, addr);
				}
			}
			else if(type == RequestType.DATA_WR)
			{
				switch(write_policy)
				{
				// TODO to be done.
//				case WRITE_BACK:
//					latency = 0;
//					break;
				case WRITE_THROUGH:
					latency = mem.getRequestDelay(type, addr);
					break;
				default:
					throw new CacheException("Currently only write through caches are supported, but cache write policy is: " + write_policy);
				}
			}
			break;
		}

		default:
			throw new CacheException("Unknown cache type: " + cache_type);
		}
		return latency;

	}
	
	
	public boolean isHit(uint32 addr) throws CacheException
	{
		uint32 tag = getTagFromAddress(addr);
		int index = getIndex(addr);
		boolean hit = false;
		
		logger.debug("Accessing index: " + index);
		
		for(int i = 0; i < associativity; i++)
		{
			if(cache_memory[i][index].compareTag(tag))
			{
				if(hit != false)
				{
					throw new CacheException("Error: multiple hits for " + addr.getValueAsHexString() + " in cache.");
				}
				hit = true;
			}
		}
		
		return hit;
	}

	protected int getCacheWayForHit(uint32 addr) throws CacheException
	{
		uint32 tag = getTagFromAddress(addr);
		int index = getIndex(addr);
		int hit_way = associativity;
		
		for(int i = 0; i < associativity; i++)
		{
			if(cache_memory[i][index].compareTag(tag))
			{
				if(hit_way != associativity)
				{
					throw new CacheException("Error: multiple hits for " + addr.getValueAsHexString() + " in cache.");
				}
				hit_way = i;
			}
		}
		
		return hit_way;
	}
	
	protected void dumpCacheLine(int index)
	{
		logger.debug("Dumping cache content for index: 0x" + Integer.toHexString(index));
		for(int i = 0; i < associativity; i++)
		{
			logger.debug("Way: " + i + " " + cache_memory[i][index].dumpLine());
		}
	}
	
	protected abstract int getCacheWayForReplacement(uint32 addr) throws CacheException;
	protected abstract void updateReplacementCountersOnAccess(int way, int index);
	protected abstract void updateReplacementCountersOnMiss(int way, int index);

	public static CacheReplacementPolicy getCacheReplacementPolicyFromString(String rpol) 
	{
		if(rpol.compareTo(CacheReplacementPolicy.FIFO.toString())==0)
		{
			return CacheReplacementPolicy.FIFO;
		}
		else if(rpol.compareTo(CacheReplacementPolicy.LRU.toString())==0)
		{
			return CacheReplacementPolicy.LRU;
		}
		
		return CacheReplacementPolicy.UNKNOWN;
	}

	public static DCacheWritePolicy getCacheWritePolicyFromString(String wpol) 
	{
		if(wpol.compareTo(DCacheWritePolicy.WRITE_THROUGH.toString())==0)
		{
			return DCacheWritePolicy.WRITE_THROUGH;
		}
		else if(wpol.compareTo(DCacheWritePolicy.WRITE_BACK.toString())==0)
		{
			return DCacheWritePolicy.WRITE_BACK;
		}
		return DCacheWritePolicy.UNKNOWN;
	}
}
