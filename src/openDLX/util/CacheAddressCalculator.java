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
import openDLX.exception.PipelineDataTypeException;

public class CacheAddressCalculator {
	
	private int tag_size;
	private int index_size;
	private int block_offset_size;
	private int words_per_line;
	
	public CacheAddressCalculator(int line_no, int line_size, int associativity, int address_width) throws PipelineDataTypeException
	{
		words_per_line = line_size / PipelineConstants.WORD_SIZE;
		block_offset_size = CalculationHelper.log2(line_size);
		index_size = CalculationHelper.log2(line_size * line_no) - block_offset_size - CalculationHelper.log2(associativity);
		tag_size = address_width - index_size - block_offset_size;
	}
	
	public int getTagSize()
	{
		return tag_size;
	}
	
	public int getIndexSize()
	{
		return index_size;
	}
	
	public int getBlockOffsetSize()
	{
		return block_offset_size;
	}
	
	public int getWordsPerLine()
	{
		return words_per_line;
	}

}
