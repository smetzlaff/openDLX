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
package openDLX.util;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

public class PrintHandler
{
	private static Logger logger = Logger.getLogger("PrintHandler");
	private static final PrintHandler instance = new PrintHandler();
	private FileOutputStream printf = null;
	private String filename;
	
	private PrintHandler()
	{
	}
	
	public static PrintHandler getInstance()
	{
		return instance;
	}
	
	public void setOutFileName(String filename)
	{
		this.filename = filename;
	}

	public void putChar(int character)
	{
		try
		{
			if(printf == null)
			{
				printf = new FileOutputStream(filename);
			}

			logger.debug("Printing character: " + (char)character + " (0x" + Integer.toHexString(character) + ")");
			printf.write(character);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
