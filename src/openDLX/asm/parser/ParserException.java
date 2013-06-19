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
package openDLX.asm.parser;

import openDLX.asm.AssemblerException;
import openDLX.asm.tokenizer.Token;

@SuppressWarnings("serial")
//package visibility
class ParserException extends AssemblerException {
	private Token token;

	public ParserException(String message, Token token) {
		super(message);
		setToken(token);
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token t) {
		this.token = t;
	}

	public int getLine() {
		if (token != null)
			return token.getPosition().line;
		else
			return -1;
	}

	public int getRow() {
		if (token != null)
			return token.getPosition().column;
		else
			return -1;
	}

	public String getMessage() {
		if (token != null)
			return super.getMessage() + ":" + token.getString();
		else
			return super.getMessage();
	}
}
