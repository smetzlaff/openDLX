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
 * Utility class for encapsulating line and column
 */
public class Position {
	public int line;
	public int column;

	/**
	 * standard Constructor: sets both line and column to -1
	 */
	public Position() {
		this.line = -1;
		this.column = -1;
	}

	/**
	 * copy constructor: sets line and column to p's values
	 */
	public Position(Position p) {
		this.line = p.line;
		this.column = p.column;
	}

	/**
	 * value Constructor: sets line and column to specified values
	 */
	public Position(int line, int column) {
		this.line = line;
		this.column = column;
	}

	/**
	 * @return string of format "(line,column)";
	 */
	public String toString() {
		return "(" + (line + 1) + "," + (column + 1) + ")";
	}

	/**
	 * @param p
	 *            position object
	 * @return true if both line and column are equal false else
	 */
	public boolean equals(Position p) {
		return this.line == p.line && this.column == p.column;
	}

	/**
	 * sets position before beginning of next line
	 */
	public void nextLine() {
		this.line++;
		this.column = -1;
	}
}
