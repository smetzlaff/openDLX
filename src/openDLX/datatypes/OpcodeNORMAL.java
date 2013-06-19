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
package openDLX.datatypes;

public enum OpcodeNORMAL {
	SPECIAL,
	REGIMM,
	J,
	JAL,
	BEQ,
	BNE,
	BLEZ,
	BGTZ,
	ADDI,
	ADDIU,
	SLTI,
	SLTIU,
	ANDI,
	ORI,
	XORI,
	LUI,
	COP0,
	SUBI, // for DLX ISA only
	COP1,
	SUBIU, // for DLX ISA only
	COP2,
	SGTI, // for DLX ISA only
	COP1X,
	SGTIU, // for DLX ISA only
	BEQL,
	BNEL,
	BLEZL,
	BGTZL,
	DADDI,
	DADDIU,
	LDL,
	LDR,
	LB,
	LH,
	LWL,
	LW,
	LBU,
	LHU,
	LWR,
	LWU,
	SB,
	SH,
	SWL,
	SW,
	SDL,
	SDR,
	SWR,
	LL,
	LWC1,
	SEQI, // for DLX ISA only
	LWC2,
	SNEI, // for DLX ISA only
	PREF,
	LLD,
	LDC1,
	SLEI, // for DLX ISA only
	LDC2,
	SGEI, // for DLX ISA only
	LD,
	SC,
	SWC1,
	SEQIU, // for DLX ISA only
	SWC2,
	SNEIU, // for DLX ISA only
	SCD,
	SDC1,
	SLEIU, // for DLX ISA only
	SDC2,
	SGEIU, // for DLX ISA only
	SD,
	NOP,
	UNKNOWN 
}
