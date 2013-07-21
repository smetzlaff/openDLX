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

import openDLX.PipelineConstants;
import openDLX.memory.MainMemory;
import openDLX.datatypes.*;
import openDLX.exception.DLXTrapException;
import openDLX.exception.MemoryException;
import openDLX.gui.dialog.Input;
import org.apache.log4j.Logger;

public class DLXTrapHandler {
	
	public static final char LINE_END = '\0';
	public static final char FORMAT_DELIMITER = '%';
	public static final char FORMAT_INTEGER = 'd';
	public static final char FORMAT_HEX = 'x';
	public static final char INPUT_END = 10;
	
	
	private static Logger logger = Logger.getLogger("PrintHandler");
	private static final DLXTrapHandler instance = new DLXTrapHandler();
	private TrapObservable oOutput = null;
	private TrapObservable oInput = null;
	private Input input = null;
	
	private MainMemory mem=null;
	
	private DLXTrapHandler()
	{
	}
	
	public static DLXTrapHandler getInstance()
	{
		return instance;
	}

	public void setTrapObserverOutput(TrapObservable to)
	{
		oOutput = to;
	}
	
	public void setTrapObserverInput(TrapObservable to)
	{
		oInput = to;
	}

	public void setInput(Input input) 
	{
		this.input = input;
	}

	public void open(int parameter) throws DLXTrapException, MemoryException 
	{
		logger.error("TRAP " + PipelineConstants.DLX_TRAP_OPEN + " not implemented. Parameter: " + parameter + " mem");
		mem.dumpMemory(new uint32(parameter), new uint32(parameter+32));
		throw new DLXTrapException("TRAP " + PipelineConstants.DLX_TRAP_OPEN + " not implemented. Parameter: " + parameter);
	}

	public void close(int parameter) throws DLXTrapException, MemoryException 
	{
		logger.error("TRAP " + PipelineConstants.DLX_TRAP_CLOSE + " not implemented. Parameter: " + parameter + " mem");
		mem.dumpMemory(new uint32(parameter), new uint32(parameter+32));
		throw new DLXTrapException("TRAP " + PipelineConstants.DLX_TRAP_OPEN + " not implemented. Parameter: " + parameter);
	}

	public uint32 read(int parameter) throws MemoryException 
	{
		String user_input = null;
//		uint32 unknown = new uint32(parameter);
		uint32 write_addr = mem.read_u32(new uint32(parameter+4));
		int input_length = mem.read_u32(new uint32(parameter+8)).getValue();
		uint32 return_value = new uint32(-1);
		
		if((oInput != null) && (input != null))
		{
			oInput.notifyObservers("Input string...");
			user_input = input.getInput();
		}
		
		if(user_input != null)
		{
			System.out.println("Input: " + user_input);
			logger.info("Input: " + user_input + " @" + write_addr.getValueAsHexString());
			
			byte[] raw = user_input.getBytes();
			for(int i = 0; ((i < user_input.length()) && (i < input_length-1)); i++)
			{
				mem.write_u8(write_addr, new uint8(raw[i]));
				write_addr.setValue(write_addr.getValue()+1);
			}
			return_value.setValue(raw.length);
		}
		mem.write_u8(write_addr, new uint8(INPUT_END));
		
		return return_value;
	}
	
	public void write(int parameter) throws DLXTrapException, MemoryException 
	{
		logger.error("TRAP " + PipelineConstants.DLX_TRAP_WRITE + " not implemented. Parameter: " + parameter + " mem");
		mem.dumpMemory(new uint32(parameter), new uint32(parameter+32));
		throw new DLXTrapException("TRAP " + PipelineConstants.DLX_TRAP_WRITE + " not implemented. Parameter: " + parameter + " mem");
	}

	public void printf(int parameter) throws MemoryException, DLXTrapException 
	{
		uint32 format_addr = mem.read_u32(new uint32(parameter));
		String format_string = new String("");
		uint32 parameter_list_pointer = new uint32(parameter + 4);
		
		logger.debug("TRAP " + PipelineConstants.DLX_TRAP_PRINTF + " catched. Printf format string is at: " + format_addr.getValueAsHexString() + " Parameter list begins at: " + parameter_list_pointer.getValueAsHexString());
		
//		logger.debug("Format string dump:");
//		mem.dumpMemory(format_addr, new uint32(format_addr.getValue() + 20));
//		logger.debug("Parameter list dump:");
//		mem.dumpMemory(parameter_list_pointer, new uint32(parameter_list_pointer.getValue() + 20));
		
		char read_char = 0xFF;
		
		// read the format string from memory
		while(read_char != LINE_END)
		{
//			logger.debug("Read addr: " + format_addr.getHex() + " char: " + (char)mem.read_u8(format_addr).getValue() + "("+ mem.read_u8(format_addr).getHex() +")");
			
			read_char = (char)(mem.read_u8(format_addr).getValue());
			String read_s = String.valueOf(read_char);
			format_string += read_s;
			
//			logger.debug("Added :" + read_s + " to: " + format_string);
			
			format_addr.setValue(format_addr.getValue()+1);
		} 
		
		int format_string_index = 0;
		String print_string = new String("");
		
		// replace the format parameters with values
		while(format_string_index != -1)
		{
			int format_descr_pos = format_string.indexOf(FORMAT_DELIMITER, format_string_index);
			
			// found format delimiter "%"
			if(format_descr_pos != -1)
			{
//				logger.debug("Found format delimiter: " + format_string.substring(format_descr_pos, format_descr_pos+1) + " reading from: " + parameter_list_pointer.toString());
				
				// add the format string before the format delimiter to the print string
				print_string += format_string.substring(format_string_index, format_descr_pos);

				// replace the format delimiter with value from memory
				switch(format_string.charAt(format_descr_pos+1))
				{
				case FORMAT_INTEGER:
					print_string += mem.read_u32(parameter_list_pointer).getValue();
					// set pointer to next addr in the parameter list
					parameter_list_pointer.setValue(parameter_list_pointer.getValue()+4);
					// skip two characters: the "%" and the format descriptor
					format_descr_pos+=2;
					break;
				case FORMAT_HEX:
					print_string += mem.read_u32(parameter_list_pointer).getValueAsHexString();
					// set pointer to next addr in the parameter list
					parameter_list_pointer.setValue(parameter_list_pointer.getValue()+4);
					// skip two characters: the "%" and the format descriptor
					format_descr_pos+=2;
					break;
				default:
					throw new DLXTrapException("Unknown format identifier for printf: " + format_string.charAt(format_descr_pos+1));
				}
			}
			else
			{
//				logger.debug("End of format string. start: " + format_string_index + " end: " + format_string.length());
				
				// end of string, add the rest of the format string
				print_string += format_string.substring(format_string_index, format_string.length()-1);
			}
			format_string_index = format_descr_pos;
		}
		
		System.out.println(print_string);
		logger.info("Printf out: " + print_string);
		
		if (oOutput != null) 
		{
			oOutput.notifyObservers(print_string);
		}
		
		// TODO return the number of printed bytes to be written to R1 
	}
	


	public void setMemory(MainMemory mainMem) 
	{
		mem = mainMem;
	}

}
