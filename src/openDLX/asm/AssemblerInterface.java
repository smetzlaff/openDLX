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
package openDLX.asm;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Hashtable;

import openDLX.asm.AssemblerException;

/**
 * <p>
 * This is the interface connects this package to the outside world An stream
 * can be assembled with <b>assemble(BufferedReader)</b> . This method returns a
 * {@link MemoryBuffer} object, with attached meta values like entry point or
 * text begin.
 * </p>
 * <p>
 * After assembling, you have access to the generated labels hash table via
 * getter.
 * </p>
 * <p>
 * Further, you can query information on instructions.
 * </p>
 */
public interface AssemblerInterface {
	/**
	 * <p>
	 * This method expects one BufferedReader as input and assembles its
	 * contents. The assembled code is returned in a wrapped array of the type
	 * MemoryBuffer.
	 * </p>
	 * 
	 * @param stream
	 *            BufferedReader
	 * @return assembled code as MemoryBuffer
	 * @throws TokenizerException
	 * @throws IOException
	 */
	public MemoryBuffer assemble(BufferedReader stream) throws AssemblerException, IOException;

	/**
	 * <p>
	 * This method expects an array of BufferedReaders as input and assembles
	 * their contents. The assembled code is returned in a wrapped byte array of
	 * the type MemoryBuffer.
	 * </p>
	 * 
	 * @param streams
	 *            array of BufferedReaders
	 * @return assembled code as MemoryBuffer
	 * @throws IOException
	 * @throws AssemblerException
	 */
	public MemoryBuffer assemble(BufferedReader[] streams) throws IOException, AssemblerException;

	/**
	 * <p>
	 * This method returns a string representation of the passed instruction.
	 * </p>
	 * <p>
	 * E.g. <b>0x20507fff</b> returns "<b>addi r16,r2,32767</b>"
	 * </p>
	 * 
	 * @param instr
	 *            instruction word treated as big endian
	 * @return string representation of instruction word
	 */
	public String Instr2Str(int instr);

	/**
	 * <p>
	 * This method returns a short description of passed mnemonic
	 * </p>
	 * <p>
	 * E.g. "<b>add</b>" returns something like
	 * "<b>add rd,rs,rt: add rs and rt and save result in rd</b>"
	 * </p>
	 * 
	 * @param mnem
	 *            mnemonic name for instruction
	 * @return description of mnemonic
	 */
	public String InstrDescription(String mnem);

	/**
	 * <p>
	 * This methods returns the hash table that was created during assembly
	 * </p>
	 * 
	 * @return hash table with label names mapped to their memory location
	 */
	public Hashtable<String, Integer> getLabels();
}
