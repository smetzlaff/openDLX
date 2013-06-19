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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import openDLX.PipelineConstants;
import openDLX.datatypes.BranchPredictorType;
import openDLX.datatypes.BranchTargetBufferLookupResult;
import openDLX.datatypes.CacheReplacementPolicy;
import openDLX.datatypes.CacheType;
import openDLX.datatypes.DCacheWritePolicy;
import openDLX.datatypes.uint32;
import openDLX.exception.CacheException;

import org.apache.log4j.Logger;

public class Statistics
{
	// Logger
	private static Logger logger = Logger.getLogger("openDLX");
	
	// Singleton
	private static final Statistics instance = new Statistics();
	
	private int cycles;
	private int instructions;
	private int fetches;
	private int jumps_taken;
	private int jumps_nottaken;
	private int jumps_likely;
	private int jumps_link;
	private int jumps_correctly_predicted;
	private int jumps_mispredicted;
	private int memory_reads;
	private int memory_writes;
	private int alu_forward_from_wb;
	private int alu_forward_from_mem;
	private int alu_forward_from_ex;
	private int bcrtl_forward_from_wb;
	private int bcrtl_forward_from_mem;
	private int bcrtl_forward_from_ex;
	private int store_forward_from_wb;
	private int store_forward_from_mem;
	private int store_forward_from_ex;
	private int btb_size;
	private BranchPredictorType btb_predictor;
	private int btb_hits;
	private int btb_misses;
	private boolean icache = false;
	private int icache_accesses;
	private int icache_hits;
	private int icache_misses;
	private int icache_words_loaded;
	private int icache_line_size;
	private int icache_line_no;
	private int icache_associativity;
	private CacheReplacementPolicy icache_replacement_policy = CacheReplacementPolicy.UNKNOWN;
	private int icache_size;
	private boolean dcache = false;
	private int dcache_accesses;
	private int dcache_hits;
	private int dcache_misses;
	private int dcache_words_loaded;
	private int dcache_line_size;
	private int dcache_line_no;
	private int dcache_associativity;
	private CacheReplacementPolicy dcache_replacement_policy = CacheReplacementPolicy.UNKNOWN;
	private DCacheWritePolicy dcache_write_policy = DCacheWritePolicy.UNKNOWN;
	private int dcache_size;
	
	private Map<uint32,BranchStat>branches_map;

	private Properties config;
	
	private Statistics()
	{
		config = null;
		setBranches_map(new HashMap<uint32,BranchStat>());
	}
	
	/*
	 * obtain a reference to the object anywhere using:
	 * Statistics stat = Statistics.getInstance();
	 */
	public static Statistics getInstance()
	{
		return instance;
	}
	
	public int getCycles()
	{
		return cycles;
	}
	public int getInstructions()
	{
		return instructions;
	}
	public int getFetches()
	{
		return fetches;
	}
	public int getJumps_taken()
	{
		return jumps_taken;
	}
	public int getJumps_likely()
	{
		return jumps_likely;
	}
	public int getJumps_link()
	{
		return jumps_link;
	}
	public int getJumps_nottaken()
	{
		return jumps_nottaken;
	}
	public int getJumps_correctly_predicted()
	{
		return jumps_correctly_predicted;
	}
	public int getJumps_mispredicted()
	{
		return jumps_mispredicted;
	}
	public int getMemory_reads()
	{
		return memory_reads;
	}
	public int getMemory_writes()
	{
		return memory_writes;
	}
	public int getForward_from_wb()
	{
		return (alu_forward_from_wb + bcrtl_forward_from_wb + store_forward_from_wb);
	}
	public int getForward_from_mem()
	{
		return (alu_forward_from_mem + bcrtl_forward_from_mem + store_forward_from_mem);
	}
	public int getForward_from_ex()
	{
		return (alu_forward_from_ex + bcrtl_forward_from_ex + store_forward_from_ex);
	}
	
	public int getAlu_forward_from_wb()
	{
		return alu_forward_from_wb;
	}

	public int getAlu_forward_from_mem()
	{
		return alu_forward_from_mem;
	}

	public int getAlu_forward_from_ex()
	{
		return alu_forward_from_ex;
	}

	public int getBcrtl_forward_from_wb()
	{
		return bcrtl_forward_from_wb;
	}

	public int getBcrtl_forward_from_mem()
	{
		return bcrtl_forward_from_mem;
	}

	public int getBcrtl_forward_from_ex()
	{
		return bcrtl_forward_from_ex;
	}

	public int getStore_forward_from_wb()
	{
		return store_forward_from_wb;
	}

	public int getStore_forward_from_mem()
	{
		return store_forward_from_mem;
	}

	public int getStore_forward_from_ex()
	{
		return store_forward_from_ex;
	}

	/* 
	 * Implement count functions.
	 */
	public void countCycle()
	{
		cycles++;
	}
	
	public void countInstruction()
	{
		instructions++;
	}
	
	public void countFetch()
	{
		fetches++;
	}
	
	public void countJumpTaken()
	{
		jumps_taken++;
	}

	public void countJumpNotTaken()
	{
		jumps_nottaken++;
	}
	
	public void countJumpLikely()
	{
		jumps_likely++;
	}
	
	public void countJumpLink()
	{
		jumps_link++;
	}
	
	public void countMemRead()
	{
		memory_reads++;
	}

	public void countMemWrite()
	{
		memory_writes++;
	}
	
	private void countALUForwardFromEX()
	{
		alu_forward_from_ex++;
	}
	
	private void countALUForwardFromMEM()
	{
		alu_forward_from_mem++;
	}
	
	private void countALUForwardFromWB()
	{
		alu_forward_from_wb++;
	}
	
	public void countALUForward(boolean forwarded_ex, boolean forwared_mem, boolean forwarded_wb)
	{
		if ((config != null) && (config.containsKey("statistic_count_also_masked_forwardings")) && (Integer.decode(config.getProperty("statistic_count_also_masked_forwardings")) == 1))
		{
			if(forwarded_ex)
			{
				countALUForwardFromEX();
			}
			if(forwared_mem)
			{
				countALUForwardFromMEM();
			}
			if(forwarded_wb)
			{
				countALUForwardFromWB();
			}
		}
		else
		{

			// count the forwarding only once! And use the most recent value
			if(forwarded_ex)
			{
				countALUForwardFromEX();
			}
			else if(forwared_mem)
			{
				countALUForwardFromMEM();
			}
			else if(forwarded_wb)
			{
				countALUForwardFromWB();
			}
		}
	}
	
	private void countBCRTLForwardFromEX()
	{
		bcrtl_forward_from_ex++;
	}
	
	private void countBCRTLForwardFromMEM()
	{
		bcrtl_forward_from_mem++;
	}
	
	private void countBCRTLForwardFromWB()
	{
		bcrtl_forward_from_wb++;
	}
	
	public void countBCRTLForward(boolean forwarded_ex, boolean forwared_mem, boolean forwarded_wb)
	{
		if ((config != null) && (config.containsKey("statistic_count_also_masked_forwardings")) && (Integer.decode(config.getProperty("statistic_count_also_masked_forwardings")) == 1))
		{
			if (forwarded_ex)
			{
				countBCRTLForwardFromEX();
			}
			if (forwared_mem)
			{
				countBCRTLForwardFromMEM();
			}
			if (forwarded_wb)
			{
				countBCRTLForwardFromWB();
			}
		}
		else
		{
			// count the forwarding only once! And use the most recent value
			if (forwarded_ex)
			{
				countBCRTLForwardFromEX();
			}
			else if (forwared_mem)
			{
				countBCRTLForwardFromMEM();
			}
			else if (forwarded_wb)
			{
				countBCRTLForwardFromWB();
			}
		}
	}

	private void countSTOREForwardFromEX()
	{
		store_forward_from_ex++;
	}
	
	private void countSTOREForwardFromMEM()
	{
		store_forward_from_mem++;
	}
	
	private void countSTOREForwardFromWB()
	{
		store_forward_from_wb++;
	}
	
	public void countSTOREForward(boolean forwarded_ex, boolean forwared_mem, boolean forwarded_wb)
	{
		if ((config != null) && (config.containsKey("statistic_count_also_masked_forwardings")) && (Integer.decode(config.getProperty("statistic_count_also_masked_forwardings")) == 1))
		{
			if (forwarded_ex)
			{
				countSTOREForwardFromEX();
			}
			if (forwared_mem)
			{
				countSTOREForwardFromMEM();
			}
			if (forwarded_wb)
			{
				countSTOREForwardFromWB();
			}
		}
		else
		{
			// count the forwarding only once! And use the most recent value
			if (forwarded_ex)
			{
				countSTOREForwardFromEX();
			}
			else if (forwared_mem)
			{
				countSTOREForwardFromMEM();
			}
			else if (forwarded_wb)
			{
				countSTOREForwardFromWB();
			}
		}
	}
	
	/*
	 * Print the aggregated performance/statistic counter values.
	 */
	public void printStats()
	{
		String stats = toString();
		String[] lines = stats.split("\n");
		for(int i = 0; i < lines.length; i++)
		{
			logger.info(lines[i]);
		}
	}
	
	public String toString()
	{
		String stats = "";
		
		DecimalFormat f = new DecimalFormat("###.##");
		stats += "-------- SIMULATION STATISTICS --------\n";
		stats += "Cycles: " + getCycles() + "\n";
		stats += "Executed instructions: " + getInstructions() + "\n";
		stats += "Performed fetches: " + getFetches() + "\n";
		if(icache || dcache)
		{
			stats += "Cache statistics:\n";
			stats += "Icache: " + ((icache)?("used"):("not used")) + " rpol: " + icache_replacement_policy + " lines: " + icache_line_no + " associativity: " + icache_associativity + " line_size: " + icache_line_size + " total size: " + icache_size + "\n";
			if(icache)
			{
				stats += "Accesses:" + icache_accesses  + " hits: " + icache_hits + " misses: " + icache_misses;
				if(icache_accesses > 0)
				{
					stats += " hit rate: " + f.format((double)icache_hits/(double)icache_accesses * (double)100) + "%";
				}
				stats += " loaded words: " + icache_words_loaded + "\n";
			}
			stats += "Dcache: " + ((dcache)?("used"):("not used")) + " rpol: " + dcache_replacement_policy + " wpol: " + dcache_write_policy + " lines: " + dcache_line_no + " associativity: " + dcache_associativity + " line_size: " + dcache_line_size + " total size: " + dcache_size + "\n";
			if(dcache)
			{
				stats += "Accesses: " + dcache_accesses + " hits: " + dcache_hits + " misses: " + dcache_misses;
				if(dcache_accesses > 0)
				{
					stats += " hit rate: " + f.format((double)dcache_hits/(double)dcache_accesses * (double)100) + "%";
				}
				stats += " loaded words: " + dcache_words_loaded + "\n";
			}
		}
		stats += "Jumps: " + (getJumps_taken()+getJumps_nottaken()) + " (taken: " + getJumps_taken() + ", not taken: " + getJumps_nottaken() + ") branches_likely: " + getJumps_likely() +  " branches_and_link: " + getJumps_link() + "\n";
		stats += "Branch Target Buffer (" + getBtb_size() + ", " + getBtb_predictor() + "): hits: " + getBtb_hits() + " misses: " + getBtb_misses() + "\n";
		stats += "Jumps correctly predicted: " + getJumps_correctly_predicted() + " mispredicted: " + getJumps_mispredicted();
		if(getJumps_correctly_predicted()+getJumps_mispredicted()>0)
		{
			stats += " misprediction rate: " + f.format(((double)getJumps_mispredicted())/((double)(getJumps_correctly_predicted()+getJumps_mispredicted()))*100) + "%";
		}
		stats += "\n";
		stats += "Number of unique jumps: " + getBranches_map().size() + "\n";
		stats += printBranchInformation(0);
		stats += "Memory accesses: " + (getMemory_reads() + getMemory_writes()) + " (reads: " + getMemory_reads() + ", writes: " + getMemory_writes() + ")\n";
		stats += "ALU forwarded values: " + (getAlu_forward_from_ex() + getAlu_forward_from_mem() + getAlu_forward_from_wb()) + " (from execute: " + getAlu_forward_from_ex() + ", memory stage: " + getAlu_forward_from_mem() + ", write back: " + getAlu_forward_from_wb() + ")\n";
		stats += "BCRTL forwarded values: " + (getBcrtl_forward_from_ex() + getBcrtl_forward_from_mem() + getBcrtl_forward_from_wb()) + " (from execute: " + getBcrtl_forward_from_ex() + ", memory stage: " + getBcrtl_forward_from_mem() + ", write back: " + getBcrtl_forward_from_wb() + ")\n";
		stats += "STORE forwarded values: " + (getStore_forward_from_ex() + getStore_forward_from_mem() + getStore_forward_from_wb()) + " (from execute: " + getStore_forward_from_ex() + ", memory stage: " + getStore_forward_from_mem() + ", write back: " + getStore_forward_from_wb() + ")\n";
		stats += "Total forwarded values: " + (getForward_from_ex() + getForward_from_mem() + getForward_from_wb()) + " (from execute: " + getForward_from_ex() + ", memory stage: " + getForward_from_mem() + ", write back: " + getForward_from_wb() + ")\n";
		stats += "-------- SIMULATION STATISTICS --------\n";
		
		return stats;
	}

	public void setConfig(Properties config)
	{
		this.config = config;
	}
	
	public void setBTBConfig(int size, BranchPredictorType predictor)
	{
		setBtb_size(size);
		setBtb_predictor(predictor);
	}
	
	public void countBTBAccesses(BranchTargetBufferLookupResult btb_result)
	{
		if((btb_result == BranchTargetBufferLookupResult.HIT_PREDICT_TAKEN) || (btb_result == BranchTargetBufferLookupResult.HIT_PREDICT_NOT_TAKEN))
		{
			setBtb_hits(getBtb_hits() + 1);
		}
		else if(btb_result == BranchTargetBufferLookupResult.MISS) 
		{
			setBtb_misses(getBtb_misses() + 1);
		}
	}

	public void countPredictions(boolean correctPrediction)
	{
		if(correctPrediction)
		{
			jumps_correctly_predicted++;
		}
		else
		{
			jumps_mispredicted++;
		}
	}

	public void countBranchInformation(uint32 branch_addr, int btbIdx, uint32 branch_tgt, boolean branching, BranchTargetBufferLookupResult btb_result, boolean correctPrediction)
	{
	    if(!getBranches_map().containsKey(branch_addr))
	    {
	    	getBranches_map().put(new uint32(branch_addr), new BranchStat(branch_addr, btbIdx, branch_tgt, branching, correctPrediction));
	    }
	    else
	    {
	    	getBranches_map().get(branch_addr).update(branch_tgt, branching, correctPrediction);
	    }
	}
	
	public String printBranchInformation(int number)
	{
		List<BranchStat> list_branches = new ArrayList<BranchStat>();
		list_branches.addAll(getBranches_map().values());
		
		Comparator<BranchStat> c = new Comparator<BranchStat>()
                {
            		public int compare(BranchStat a, BranchStat b)
            		{
            			if(b.getAccesses() - a.getAccesses() != 0)
            			{
            				return (b.getAccesses() - a.getAccesses());
            			}
            			else
            			{
            				return (a.getBranchAddr().getValue() - b.getBranchAddr().getValue());
            			}
            		}
                };
		
		Collections.sort(list_branches, c);
		
		if(number == 0)
		{
			number = getBranches_map().size();
		}
		
		String tempString="";
		String retString="";
		for(int i = 0; i < number; i ++)
		{
			tempString=list_branches.get(i).toString();
			retString+=tempString + "\n";
			
		}
		
		return retString;
	}

	public void countCacheHit(CacheType type) throws CacheException 
	{
		switch(type)
		{
		case ICACHE:
			icache_hits++;
			icache_accesses++;
			break;
		case DCACHE:
			dcache_hits++;
			dcache_accesses++;
			break;
		default:
			throw new CacheException("Unknown cache replacement policy: " + type);
		}
	}

	public void countCacheMiss(CacheType type) throws CacheException 
	{
		switch(type)
		{
		case ICACHE:
			icache_misses++;
			icache_accesses++;
			icache_words_loaded += icache_line_size / PipelineConstants.WORD_SIZE;
			break;
		case DCACHE:
			dcache_misses++;
			dcache_accesses++;
			dcache_words_loaded += dcache_line_size / PipelineConstants.WORD_SIZE;
			break;
		default:
			throw new CacheException("Unknown cache replacement policy: " + type);
		}
	}

	public void setCacheParameters(CacheType type, CacheReplacementPolicy rpol, int lineSize, int lineNo, int associativity, DCacheWritePolicy wpol) throws CacheException 
	{
		switch(type)
		{
		case ICACHE:
			icache = true;
			icache_line_size = lineSize;
			icache_line_no = lineNo;
			icache_associativity = associativity;
			icache_replacement_policy = rpol;
			icache_size = icache_line_no * icache_line_size;
			break;
		case DCACHE:
			dcache = true; 
			dcache_line_size = lineSize;
			dcache_line_no = lineNo;
			dcache_associativity = associativity;
			dcache_replacement_policy = rpol;
			dcache_write_policy = wpol;
			dcache_size = dcache_line_no * dcache_line_size;
			break;
		default:
			throw new CacheException("Unknown cache replacement policy: " + type);
		}
	}

	public int getBtb_size() {
		return btb_size;
	}

	public void setBtb_size(int btb_size) {
		this.btb_size = btb_size;
	}

	public BranchPredictorType getBtb_predictor() {
		return btb_predictor;
	}

	public void setBtb_predictor(BranchPredictorType btb_predictor) {
		this.btb_predictor = btb_predictor;
	}

	public int getBtb_hits() {
		return btb_hits;
	}

	public void setBtb_hits(int btb_hits) {
		this.btb_hits = btb_hits;
	}

	public int getBtb_misses() {
		return btb_misses;
	}

	public void setBtb_misses(int btb_misses) {
		this.btb_misses = btb_misses;
	}

	public Map<uint32,BranchStat> getBranches_map() {
		return branches_map;
	}

	public void setBranches_map(Map<uint32,BranchStat> branches_map) {
		this.branches_map = branches_map;
	}

	public void reset() 
	{
		cycles = 0;
		instructions = 0;
		fetches = 0;
		jumps_taken = 0;
		jumps_nottaken = 0;
		jumps_likely = 0;
		jumps_link = 0;
		jumps_correctly_predicted = 0;
		jumps_mispredicted = 0;
		memory_reads = 0;
		memory_writes = 0;
		alu_forward_from_wb = 0;
		alu_forward_from_mem = 0;
		alu_forward_from_ex = 0;
		bcrtl_forward_from_wb = 0;
		bcrtl_forward_from_mem = 0;
		bcrtl_forward_from_ex = 0;
		store_forward_from_wb = 0;
		store_forward_from_mem = 0;
		store_forward_from_ex = 0;
		btb_size = 0;
		btb_predictor = BranchPredictorType.UNKNOWN;
		btb_hits = 0;
		btb_misses = 0;
		icache = false;
		icache_accesses = 0;
		icache_hits = 0;
		icache_misses = 0;
		icache_words_loaded = 0;
		icache_line_size = 0;
		icache_line_no = 0;
		icache_associativity = 0;
		icache_replacement_policy = CacheReplacementPolicy.UNKNOWN;
		icache_size = 0;
		dcache = false;
		dcache_accesses = 0;
		dcache_hits = 0;
		dcache_misses = 0;
		dcache_words_loaded = 0;
		dcache_line_size = 0;
		dcache_line_no = 0;
		dcache_associativity = 0;
		dcache_replacement_policy = CacheReplacementPolicy.UNKNOWN;
		dcache_write_policy = DCacheWritePolicy.UNKNOWN;
		dcache_size = 0;
		setBranches_map(new HashMap<uint32,BranchStat>());
	}

}
