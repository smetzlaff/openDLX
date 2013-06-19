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

import java.io.BufferedReader;
import java.io.IOException;

/**
 * reads characters from underlying stream and buffers them deletes leading
 * whitespace and comments
 */
public class BufferedCharReader {
	private static char NO_CHAR = '_';
	Position position_;
	private BufferedReader reader_;
	private char[] buffer_;

	public BufferedCharReader(BufferedReader reader) throws IOException {
		if (reader == null)
			throw new IllegalArgumentException();
		this.reader_ = reader;
		this.position_ = new Position();
		buffer_ = new char[0];
	}

	/**
	 * reads next line to buffer and deletes comments
	 * 
	 * @return false if EOF reached true otherwise
	 * @throws IOException
	 */
	public boolean readLine() throws IOException {
		String str = reader_.readLine();
		if (str == null)
			return false;

		char[] tmp = str.toCharArray();

		// calculate bounding
		boolean inLiteral = false;
		int offset;
		int end = tmp.length;
		int i = 0;
		offset = i;
		// comments
		while (i < tmp.length) {
			if (tmp[i] == '"') {
				inLiteral = !inLiteral;
			} else if (!inLiteral && tmp[i] == ';') {
				end = i;
				break;
			}
			i++;
		}

		buffer_ = new char[end - offset];
		i = 0;
		while (offset < end) {
			buffer_[i] = tmp[offset];
			offset++;
			i++;
		}

		position_.nextLine();

		return true;
	}

	/**
	 * decrements current pointer
	 * 
	 * @return character preceding current or -1 if no preceding available
	 */
	public int previous() {
		if (position_.column >= 0) {
			position_.column--;
			return current();
		}
		return -1;
	}

	/**
	 * does not decrement pointer
	 * 
	 * @return character preceding current or -1 if no preceding available
	 */
	public int peekPrevious() {
		if (position_.column - 1 > 0) {
			return buffer_[position_.column - 1];
		}
		return -1;
	}

	/**
	 * 
	 * @return current character or -1 if not available
	 */
	public int current() {
		if (position_.column < 0 || position_.column >= buffer_.length)
			return -1;
		return buffer_[position_.column];
	}

	/**
	 * increments current pointer
	 * 
	 * @return character following current or -1 if no following available
	 */
	public int next() {
		if (position_.column < buffer_.length) {
			position_.column++;
			return current();
		}
		return -1;
	}

	/**
	 * does not increment pointer
	 * 
	 * @return character following current or -1 if no following available
	 */
	public int peekNext() {
		if (position_.column + 1 < buffer_.length) {
			return buffer_[position_.column + 1];
		}
		return -1;
	}

	/**
	 * 
	 * @return true if line is empty false otherwise
	 */
	public boolean isEmptyLine() {
		if (buffer_.length == 0)
			return true;
		else
			return false;
	}

	/**
	 * 
	 * @return new Position object copied from current position
	 */
	public Position position() {
		return new Position(position_);
	}

	/**
	 * 
	 * @return true is c is -1 otherwise false
	 */
	public boolean isEol() {
		return isEol(current());
	}

	/**
	 * 
	 * @param c
	 * @return true is c is -1 otherwise false
	 */
	public static boolean isEol(int c) {
		return c == -1;
	}

	/**
	 * @return current buffer state and position as string
	 */
	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append('|');

		for (int i = 0; i < buffer_.length; i++) {
			str.append(escapeChars(buffer_[i]));
		}

		str.append('|');
		str.append(position_.toString());
		return str.toString();
	}

	private String escapeChars(int c) {
		if (c < 0)
			return Character.toString(NO_CHAR) + NO_CHAR;
		else {
			switch (c) {
			case '\n':
				return "\\n";
			case '\t':
				return "\\t";
			case '\r':
				return "\\r";
			case '\b':
				return "\\b";
			case '\f':
				return "\\f";
			default:
				if (c < 0x20)
					return Character.toString((char) c);
				else
					return Character.toString((char) c) + ' ';
			}
		}
	}
}
