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

import java.util.Queue;

import openDLX.datatypes.*;
import openDLX.exception.MemoryException;
import openDLX.exception.MemoryStageException;
import openDLX.memory.DataMemory;
import openDLX.util.Statistics;

import org.apache.log4j.Logger;

public class Memory
{
	private static Logger logger = Logger.getLogger("MEMORY");
	private Statistics stat = Statistics.getInstance(); 
	private DataMemory dmem;
	private Queue<ExecuteMemoryData> execute_memory_latch;
	private final boolean throwExceptionForUntestedAccesses = true;

	public Memory(DataMemory dmem)
	{
		this.dmem = dmem;
	}

	public void setInputLatch(Queue<ExecuteMemoryData> executeMemoryLatch)
	{
		execute_memory_latch = executeMemoryLatch;
	}

	public MemoryOutputData doCycle() throws MemoryStageException, MemoryException
	{
		ExecuteMemoryData emd = execute_memory_latch.element();
		uint32[] alu_out = emd.getAluOut();
		uint32 alu_outLO = alu_out[0];
		// uint32 alu_outHI = alu_out[1];
		uint32 store_value = emd.getStoreValue();
		Instruction inst = emd.getInst();
		uint32 pc = emd.getPc();
		boolean jump = emd.getJump();

		uint32 ld_result = new uint32(0);

		if (inst.getLoad())
		{
			if(dmem.getRequestDelay(RequestType.DATA_RD, alu_outLO)==0)
			{
				switch(inst.getMemoryWidth())
				{
				case BYTE:
					ld_result.setValue((int)dmem.read_u8(alu_outLO, true).getValue());
					logger.debug("PC: " + pc.getValueAsHexString() + " load from addr: " + alu_outLO.getValueAsHexString() + " value: " + ld_result.getValueAsHexString());
					break;
				case UBYTE:
					ld_result.setValue(dmem.read_u8(alu_outLO, true).getValue()&0xFF);
					logger.debug("PC: " + pc.getValueAsHexString() + " load from addr: " + alu_outLO.getValueAsHexString() + " value: " + ld_result.getValueAsHexString());
					break;
				case WORD:
					ld_result.setValue(dmem.read_u32(alu_outLO, true).getValue());
					logger.debug("PC: " + pc.getValueAsHexString() + " load from addr: " + alu_outLO.getValueAsHexString() + " value: " + ld_result.getValueAsHexString());
					break;
				case UWORD:
					ld_result.setValue(dmem.read_u32(alu_outLO, true).getValue());
					logger.debug("PC: " + pc.getValueAsHexString() + " load from addr: " + alu_outLO.getValueAsHexString() + " value: " + ld_result.getValueAsHexString());
					if(throwExceptionForUntestedAccesses)
					{
						throw new MemoryStageException("Untested memory width: " + inst.getMemoryWidth());
					}
					break;
				default:
					logger.error("wrong memory width: " + inst.getMemoryWidth()); 
					throw new MemoryStageException("Wrong memory width: " + inst.getMemoryWidth());
				}
				stat.countMemRead();
			}
			else
			{
				// stall
			}
		}
		else if (inst.getStore())
		{
			if(dmem.getRequestDelay(RequestType.DATA_WR, alu_outLO)==0)
			{
				switch(inst.getMemoryWidth())
				{
				case BYTE:
					logger.debug("PC: " + pc.getValueAsHexString() + " store value: " + store_value.getValueAsHexString() + " to addr: " + alu_outLO.getValueAsHexString());
					dmem.write_u8(alu_outLO, store_value);
					break;
				case UBYTE:
					logger.debug("PC: " + pc.getValueAsHexString() + " store value: " + store_value.getValueAsHexString() + " to addr: " + alu_outLO.getValueAsHexString());
					dmem.write_u8(alu_outLO, store_value);
					break;
				case WORD:
					logger.debug("PC: " + pc.getValueAsHexString() + " store value: " + store_value.getValueAsHexString() + " to addr: " + alu_outLO.getValueAsHexString());
					dmem.write_u32(alu_outLO, store_value);
					break;
				case UWORD:
					logger.debug("PC: " + pc.getValueAsHexString() + " store value: " + store_value.getValueAsHexString() + " to addr: " + alu_outLO.getValueAsHexString());
					dmem.write_u32(alu_outLO, store_value);
					break;
				case WORD_RIGHT_PART:
					// refer to page A-153 of the MIPS IV Instruction Set Rev. 3.2
					switch(alu_outLO.getValue()&0x3)
					{
					case 0:
						dmem.write_u32(alu_outLO, store_value);
						
						logger.warn("Verify operation of SWR (0)!");
						if(throwExceptionForUntestedAccesses)
						{
							throw new MemoryStageException("Verify operation of SWR (0)!");
						}
						break;
					case 1:
						dmem.write_u8(alu_outLO, new uint8((store_value.getValue())&0xFF));
						dmem.write_u8(new uint32(alu_outLO.getValue()+1), new uint8((store_value.getValue()>>8)&0xFF));
						dmem.write_u8(new uint32(alu_outLO.getValue()+2), new uint8((store_value.getValue()>>16)&0xFF));
						
						logger.warn("Verify operation of SWR (1)!");
						if(throwExceptionForUntestedAccesses)
						{
							throw new MemoryStageException("Verify operation of SWR (1)!");
						}
						break;
					case 2:
						dmem.write_u8(alu_outLO, new uint8((store_value.getValue())&0xFF));
						dmem.write_u8(new uint32(alu_outLO.getValue()+1), new uint8((store_value.getValue()>>8)&0xFF));
						
						logger.warn("Verify operation of SWR (3)!");
						if(throwExceptionForUntestedAccesses)
						{
							throw new MemoryStageException("Verify operation of SWR (2)!");
						}
						break;
					case 3:
						dmem.write_u8(alu_outLO, new uint8((store_value.getValue())&0xFF));
						
						logger.warn("Verify operation of SWR (3)!");
						if(throwExceptionForUntestedAccesses)
						{
							throw new MemoryStageException("Verify operation of SWR (3)!");
						}
						break;
					}
					break;
				case WORD_LEFT_PART:
					// refer to page A-150 of the MIPS IV Instruction Set Rev. 3.2
					switch(alu_outLO.getValue()&0x3)
					{
					case 0:
						dmem.write_u8(alu_outLO, new uint8((store_value.getValue()>>24)&0xFF));
						
						logger.warn("Verify operation of SWL (0)!");
						if(throwExceptionForUntestedAccesses)
						{
							throw new MemoryStageException("Verify operation of SWR (0)!");
						}
						break;
					case 1:
						dmem.write_u8(alu_outLO, new uint8((store_value.getValue()>>24)&0xFF));
						dmem.write_u8(new uint32(alu_outLO.getValue()-1), new uint8((store_value.getValue()>>16)&0xFF));
						
						logger.warn("Verify operation of SWL (1)!");
						if(throwExceptionForUntestedAccesses)
						{
							throw new MemoryStageException("Verify operation of SWR (1)!");
						}
						break;
					case 2:
						dmem.write_u8(alu_outLO, new uint8((store_value.getValue()>>24)&0xFF));
						dmem.write_u8(new uint32(alu_outLO.getValue()-1), new uint8((store_value.getValue()>>16)&0xFF));
						dmem.write_u8(new uint32(alu_outLO.getValue()-2), new uint8((store_value.getValue()>>8)&0xFF));
						
						logger.warn("Verify operation of SWL (2)!");
						if(throwExceptionForUntestedAccesses)
						{
							throw new MemoryStageException("Verify operation of SWR (2)!");
						}
						break;
					case 3:
						dmem.write_u32(new uint32(alu_outLO.getValue()-3), store_value);
						
						logger.warn("Verify operation of SWL (3)!");
						if(throwExceptionForUntestedAccesses)
						{
							throw new MemoryStageException("Verify operation of SWR (2)!");
						}
						break;
					}
					break;
				default:
					logger.error("Wrong memory width: " + inst.getMemoryWidth()); 
					throw new MemoryStageException("Wrong memory width: " + inst.getMemoryWidth());
				}
				stat.countMemWrite();
			}
			else
			{
				// stall
			}
		}
		else
		{
			logger.debug("PC: " + pc.getValueAsHexString() + " nothing to do");
		}

		MemoryWritebackData mwd = new MemoryWritebackData(inst, pc, alu_out, ld_result, jump);

		return new MemoryOutputData(mwd);
	}
}
