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


public class Token extends GenericToken {

	private StringBuffer string_;
	private Position position_;

	/*
	 * ============================* Constructors *============================
	 */

	public Token() {
		super();
		this.position_ = new Position();
		this.string_ = new StringBuffer();
	}

	public Token(Position p) {
		super();
		this.position_ = p;
		this.string_ = new StringBuffer();
	}

	public Token(TokenType t, String str) {
		super(t);
		this.position_ = new Position();
		this.string_ = new StringBuffer(str);
	}

	public Token(Position p, TokenType type) {
		super(type);
		this.position_ = p;
		this.string_ = new StringBuffer();
	}

	public Token(Position p, TokenType type, String str) {
		super(type);
		this.position_ = p;
		this.string_ = new StringBuffer(str);
	}

	/*
	 * ============================* Getter/Setter *============================
	 */

	public String getString() {
		return string_.toString();
	}

	public void append(char c) {
		string_.append(c);
	}

	public Position getPosition() {
		return position_;
	}

	/*
	 * ==========================* Object overrides *==========================
	 */

	public String toString() {
		return string_ + "(" + type_ + ":" + position_ + ")";
	}

	/**
	 * returns true if token type and name are equal and false if not
	 */
	public boolean equals(Object o) {
		if (o instanceof Token) {
			Token t = (Token) o;
			return getTokenType() == t.getTokenType()
					&& getString().equalsIgnoreCase(t.getString());
		}
		return false;
	}
}
