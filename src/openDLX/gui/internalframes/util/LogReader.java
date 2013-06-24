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

public class LogReader
{
    //4 log types, Strings, to be found within log lines
    public static final String DEBUG_STRING = "DEBUG";
    public static final String INFO_STRING = "INFO";
    public static final String WARN_STRING = "WARN";
    public static final String ERROR_STRING = "ERROR";

    //data objects:
    private File logFile;
    private int lineNum = 0;
    // contains forbidden keys - that means log sources that shouldnt be
    // shown in the GUI
    private ArrayList<String> forbiddenKeys = new ArrayList<>();
    // contains the whole logFile
    private ArrayList<String> rawLog = new ArrayList<>();
    // contains the logFile without the forbidden key sources -
    // e.g. the registerset is not represented by the gui logger
    private ArrayList<String> log = new ArrayList<>();

    public LogReader(String filePath)
    {
        // this is the file the logger writes in
        logFile = new File(filePath);

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
            final int newLineNum = readFile(); // save the new logfile line
            String currentLine;
            for (int i = lineNum+1; i < rawLog.size(); ++i)
            {
                currentLine = rawLog.get(i);

                for (String key : forbiddenKeys)
                {
                    if (currentLine.contains(key))
                    {
                        currentLine = null;
                        break;
                    }

                    if (currentLine != null)
                        log.add(currentLine);
                }
            }
            lineNum = newLineNum; // assign the new logfile line
        }
    }

    private int readFile()
    {
        rawLog.clear();
        if (logFile.exists())
        {
            try
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        new DataInputStream(new FileInputStream(logFile))));
                String strLine;
                while ((strLine = br.readLine()) != null)
                    rawLog.add(strLine);

                br.close();
                return rawLog.size();
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
