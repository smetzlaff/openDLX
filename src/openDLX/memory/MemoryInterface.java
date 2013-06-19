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

import openDLX.datatypes.RequestType;
import openDLX.datatypes.uint32;
import openDLX.datatypes.uint8;
import openDLX.exception.MemoryException;

public interface MemoryInterface {

	short getRequestDelay(RequestType instrRd, uint32 addr) throws MemoryException;

	uint32 read_u32(uint32 addr) throws MemoryException;

	uint8 read_u8(uint32 addr, boolean log_output) throws MemoryException;

	uint32 read_u32(uint32 addr, boolean log_output) throws MemoryException;

	void write_u8(uint32 addr, uint32 value) throws MemoryException;

	void write_u32(uint32 addr, uint32 value) throws MemoryException;

	void write_u8(uint32 addr, uint8 value) throws MemoryException;

}
