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

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LoggerConfigurator
{
	// Singleton
	private static final LoggerConfigurator instance = new LoggerConfigurator();
	
	private LoggerConfigurator()
	{
	}
	
	public static LoggerConfigurator getInstance()
	{
		return instance;
	}
	
	public void setLogLevel(Level newLevel)
	{
        Logger.getRootLogger().setLevel(newLevel);
	}
	
	public void configureLogger(String log4jPropertyFile, String logFile)
	{
		Properties prop = new Properties();
		try
		{
			URL propertyFileURL = getClass().getResource(log4jPropertyFile);
			
			if(propertyFileURL != null)
			{
				prop.load(propertyFileURL.openStream());
			}
			else
			{
				System.out.println("No suitable log4j property file found. Using default.");
				prop.load(getClass().getResource("/openDLX/log4j.properties").openStream());
			}
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		// overwrite logfile from log4j properties, if log_file is set in config file.
		if(logFile != null)
		{
			prop.setProperty("log4j.appender.file.File", logFile);
		}
		PropertyConfigurator.configure(prop);	
	}
	
	public void setLogOutputFile(String filename)
	{
		System.setProperty("flog4j.appender.file.File", filename);
	}
}
