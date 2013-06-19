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

import openDLX.exception.PipelineDataTypeException;

public class CalculationHelper
{

	public static int generateBitStringOfOnes(int bit_vector_length)
	{
		int bit_string=0;
		for(int i = 0; i<bit_vector_length; i++)
		{
			bit_string = (bit_string << 1) | 1;
		}
		return bit_string;
		
	}

	public static int log2(int value) throws PipelineDataTypeException
	{
		if(Integer.bitCount(value) != 1)
		{
			throw new PipelineDataTypeException("log2 of " + value + " is not calculable, since it it not a power of 2");
		}
		return (Integer.numberOfTrailingZeros(value));
	}

}
