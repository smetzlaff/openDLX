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
package openDLX.asm.tokenizer;

/**
 * Preceding T for tokenizer properties
 * 
 */
public class Properties {
	public static char[] T_WHITESPACE = { ' ', '\n', '\t', '\f', '\b' };
	public static char[] T_OPERATOR = { '+', '-', '*', '/' };
	public static char[] T_SEPARATOR = { ',', '(', ')' };
	public static char[] T_IDENTIFIER_START = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
			'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '_',
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
			'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
	public static char[] T_IDENTIFIER_PART = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
			'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
			'y', 'z' };
	public static char[] T_OCTAL_DIGIT = { '0', '1', '2', '3', '4', '5', '6', '7' };
	public static char[] T_DECIMAL_DIGIT = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
	public static char[] T_DECIMAL_DIGIT_BEGIN = { '1', '2', '3', '4', '5', '6', '7', '8', '9' };
	public static char[] T_HEX_DIGIT = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
			'a', 'B', 'b', 'C', 'c', 'D', 'd', 'E', 'e', 'F', 'f' };
}
