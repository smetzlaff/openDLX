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
package openDLX.gui.internalframes.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

public class LogReader
{
    //4 log types, Strings, to be found within log lines

    public static final String DEBUG_STRING = "DEBUG";
    public static final String INFO_STRING = "INFO";
    public static final String WARN_STRING = "WARN";
    public static final String ERROR_STRING = "ERROR";
    //data objects
    private File logFile;
    private ArrayList<String> forbiddenKeys;
    private ArrayList<String> rawLog;
    private ArrayList<String> log;
    int line;

    public LogReader(String filePath)
    {

        logFile = new File(filePath);
        // this is the file the logger writes in  

        rawLog = new ArrayList<String>();
        // contains the whole logFile   

        log = new ArrayList<String>();
        /*contains the logFile without the forbidden key sources -
         e.g. the registerset is not represented by the gui logger */

        forbiddenKeys = new ArrayList<String>();
        /*contains forbidden keys - that means log sources that shouldnt be
         shown in the GUI*/

        //enter here any forbidden log sources
        forbiddenKeys.add("REGISTERSET");

    }

    public ArrayList<String> getLog()
    {
        return log;
    }

    public void update()
    {
        if (logFile.exists())
        {
            int newLine = readFile(); // save the new logfile line
            Iterator<String> it = rawLog.iterator(); // iterate through the whole logfile
            String help;
            int i = 0;
            while (it.hasNext())
            {
                help = it.next().toString();
                ++i;
                if (i > line) // dont show old lines again
                {
                    Iterator<String> it2 = forbiddenKeys.iterator();
                    while (it2.hasNext())
                    {
                        //check if line contains forbidden keys 
                        // if true -> dont put line in log
                        if (help.contains(it2.next().toString()))
                        {
                            help = null;
                            break;
                        }

                        if (help != null)
                        {
                            log.add(help);
                        }

                    }

                }
            }
            line = newLine; // assign the new logfile line
        }
    }

    private int readFile()
    {
        rawLog.clear();
        if (logFile.exists())
        {
            int helpLine = 0;
            try
            {
                FileInputStream fstream = new FileInputStream(logFile);
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                while ((strLine = br.readLine()) != null)
                {
                    rawLog.add(strLine);
                    ++helpLine;
                }
                in.close();
                return helpLine;
            }
            catch (Exception e)
            {
                System.err.println("Error: " + e.getMessage());
                return 0;
            }
        }
        return 0;
    }

}
