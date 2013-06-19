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

import java.util.Vector;

/**
 * This class represents a state in a finite state machine.
 * 
 */
public class State {
	private Vector<Transition> transitions;
	private String name;
	private boolean accepting;

	public State(boolean accepting) {
		transitions = new Vector<Transition>();
		this.accepting = accepting;
	}

	public State(boolean accepting, String name) {
		transitions = new Vector<Transition>();
		this.accepting = accepting;
		this.name = name;
	}

	/**
	 * add Transition to State
	 * 
	 * @param t
	 */
	public void addTransition(Transition t) {
		transitions.add(t);
	}

	/**
	 * 
	 * @param o
	 *            Object that is matched with transitions
	 * @return first state matched with transition of o null if no transition
	 *         possible
	 */
	public State doTransition(Object o) {
		for (Transition t : transitions) {
			State s = t.doTransition(o);
			if (s != null)
				return s;
		}
		return null;
	}

	/**
	 * 
	 * @return true if state is accepting false otherwise
	 */
	public boolean isAccepting() {
		return accepting;
	}

	public String toString() {
		if (name == null)
			return "<no name>";
		return name;
	}
}
