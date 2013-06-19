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

import java.util.Iterator;
import java.util.Vector;

public class CharacterList implements Iterable<Character> {
	private Vector<Character> list_;

	public CharacterList(char c) {
		list_ = new Vector<Character>();
		list_.add(new Character(c));
	}

	public CharacterList(Character c) {
		list_ = new Vector<Character>();
		list_.add(c);
	}

	public CharacterList(char chars[]) {
		list_ = new Vector<Character>();
		for (char c : chars) {
			list_.add(new Character(c));
		}
	}

	public CharacterList(Character chars[]) {
		list_ = new Vector<Character>();
		for (char c : chars) {
			list_.add(new Character(c));
		}
	}

	public boolean isElement(Character c) {
		for (Character elem : list_)
			if (c.equals(elem))
				return true;
		return false;
	}

	/**
	 * equals if one element equals
	 */
	public boolean equals(Object o) {
		if (o instanceof Character) { // Character
			return isElement((Character) o);
		} else if (o instanceof InverseCharacterList) { //inverse list
			return ((InverseCharacterList) o).equals(this);
		} else if (o instanceof CharacterList) { // another list
			for (Character c : (CharacterList) o)
				if (isElement(c))
					return true;
		} else if (o instanceof Character[]) { // Character array
			for (Character c : (Character[]) o)
				if (isElement(c))
					return true;
		}
		return false;
	}

	public Iterator<Character> iterator() {
		return list_.iterator();
	}
}
