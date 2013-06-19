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
 * This class represents a generic token of one token type. Compared to a
 * specific token it returns true if token type is the same
 */
public class GenericToken {
	protected TokenType type_;

	// ctors

	public GenericToken() {
		this.type_ = TokenType.Unknown;
	}

	public GenericToken(TokenType type) {
		this.type_ = type;
	}

	// getter/setter

	public TokenType getTokenType() {
		return type_;
	}

	public void setTokenType(TokenType type) {
		this.type_ = type;
	}

	public String toString() {
		return "generic " + getTokenType();
	}

	public boolean equals(Object o) {
		if (o instanceof GenericToken) {
			if (getTokenType() == ((GenericToken) o).getTokenType())
				return true;
		}
		return false;
	}
}
