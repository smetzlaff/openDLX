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

import openDLX.datatypes.uint32;

public interface PipelineConstants
{
	public static final byte FETCH_STAGE = 0;
	public static final byte DECODE_STAGE = 1;
	public static final byte EXECUTE_STAGE = 2;
	public static final byte MEMORY_STAGE = 3;
	public static final byte WRITEBACK_STAGE = 4;
	public static final byte STAGES = 5;
	
	
	public static final byte REG_ZE = 0;
	public static final byte REG_ZERO = REG_ZE;
	public static final byte REG_R0 = REG_ZE;
	public static final byte REG_AT = 1;
	public static final byte REG_R1= REG_AT;
	public static final byte REG_V0 = 2;
	public static final byte REG_R2 = REG_V0;
	public static final byte REG_V1 = 3;
	public static final byte REG_R3 = REG_V1;
	public static final byte REG_A0 = 4;
	public static final byte REG_R4 = REG_A0;
	public static final byte REG_A1 = 5;
	public static final byte REG_R5 = REG_A1;
	public static final byte REG_A2 = 6;
	public static final byte REG_R6 = REG_A2;
	public static final byte REG_A3 = 7;
	public static final byte REG_R7 = REG_A3;
	public static final byte REG_A4 = 8;
	public static final byte REG_R8 = REG_A4;
	public static final byte REG_A5 = 9;
	public static final byte REG_R9 = REG_A5;
	public static final byte REG_A6 = 10;
	public static final byte REG_R10 = REG_A6;
	public static final byte REG_A7 = 11;
	public static final byte REG_R11 = REG_A7;
	public static final byte REG_T4 = 12;
	public static final byte REG_R12 = REG_T4;
	public static final byte REG_T5 = 13;
	public static final byte REG_R13 = REG_T5;
	public static final byte REG_T6 = 14;
	public static final byte REG_R14 = REG_T6;
	public static final byte REG_T7 = 15;
	public static final byte REG_R15 = REG_T7;
	public static final byte REG_S0 = 16;
	public static final byte REG_R16 = REG_S0;
	public static final byte REG_S1 = 17;
	public static final byte REG_R17 = REG_S1;
	public static final byte REG_S2 = 18;
	public static final byte REG_R18 = REG_S2;
	public static final byte REG_S3 = 19;
	public static final byte REG_R19 = REG_S3;
	public static final byte REG_S4 = 20;
	public static final byte REG_R20 = REG_S4;
	public static final byte REG_S5 = 21;
	public static final byte REG_R21 = REG_S5;
	public static final byte REG_S6 = 22;
	public static final byte REG_R22 = REG_S6;
	public static final byte REG_S7 = 23;
	public static final byte REG_R23 = REG_S7;
	public static final byte REG_T8 = 24;
	public static final byte REG_R24 = REG_T8;
	public static final byte REG_T9 = 25;
	public static final byte REG_R25 = REG_T9;
	public static final byte REG_K0 = 26;
	public static final byte REG_R26 = REG_K0;
	public static final byte REG_K1 = 27;
	public static final byte REG_R27 = REG_K1;
	public static final byte REG_GP = 28;
	public static final byte REG_R28 = REG_GP;
	public static final byte REG_SP = 29;
	public static final byte REG_R29 = REG_SP;
	public static final byte REG_S8 = 30;
	public static final byte REG_R30 = REG_S8;
	public static final byte REG_RA = 31;
	public static final byte REG_R31 = REG_RA;
	
	
	public static final int SYSCALL_PUTCHAR = 0xff0;
	
	public static final int DLX_TRAP_STOP = 0;
	public static final int DLX_TRAP_OPEN = 1;
	public static final int DLX_TRAP_CLOSE = 2;
	public static final int DLX_TRAP_READ = 3;
	public static final int DLX_TRAP_WRITE = 4;
	public static final int DLX_TRAP_PRINTF = 5;
	
	public static final uint32 PIPELINE_BUBBLE_INSTR = new uint32(0xffffffff);
	public static final uint32 PIPELINE_BUBBLE_ADDR = new uint32(0xffffffff);
	
	public static final int ADDR_WIDTH = 32;
	// DO NOT CHANGE THE WORD SIZE. IT HAS TO BE 4
	public static final int WORD_SIZE = 4;
	
}
