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
package openDLX.asm.instruction;

import java.util.Hashtable;

public class Registers {
	private Hashtable<String, Integer> str2int_;
	private Hashtable<Integer, String> int2str_;

	private static Registers instance_;

	public static Registers instance() {
		if (instance_ == null)
			instance_ = new Registers();
		return instance_;
	}

	public Integer getInteger(String str) {
		Integer i = str2int_.get(str.toLowerCase());
		if (i == null)
			return null;
		else
			return new Integer(i);
	}

	public String getString(Integer i) {
		return int2str_.get(i);
	}

	private Registers() {
		str2int_ = new Hashtable<String, Integer>();
		int2str_ = new Hashtable<Integer, String>();

		for (int i = 0; i < 32; i++) {
			add("r" + i, i);
		}
	}

	private void add(String str, Integer i) {
		Integer integer = new Integer(i);
		String string = new String(str);
		str2int_.put(string, integer);
		int2str_.put(integer, string);
	}
}
