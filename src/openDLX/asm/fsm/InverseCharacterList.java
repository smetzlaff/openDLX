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
package openDLX.asm.fsm;

/**
 * This class represents the inverse of assigned characters. A character is
 * element if it is not in the list. Iterating over this list not recommended
 * because semantics are not obvious (you will iterate over negative of
 * represented list).
 * 
 */
public class InverseCharacterList extends CharacterList {

	public InverseCharacterList(char c) {
		super(c);
	}

	public InverseCharacterList(Character c) {
		super(c);
	}

	public InverseCharacterList(char[] chars) {
		super(chars);
	}

	public InverseCharacterList(Character[] chars) {
		super(chars);
	}

	public boolean isElement(Character c) {
		return !super.isElement(c);
	}
}
