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
package openDLX.main;

import openDLX.OpenDLXSimulator;
import openDLX.config.GlobalConfig;
import openDLX.datatypes.ArchCfg;
import openDLX.datatypes.ISAType;
import openDLX.exception.PipelineException;
import openDLX.gui.*;

public class OpenDLXSimulatorMain
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {

    	if(args.length == 0)
    	{
    		main_gui();
    	}
    	else
    	{

    		System.out.println("Parameters: ");
    		for (int i = 0; i < args.length; i++)
    		{
    			System.out.println(i + " " + args[i]);
    		}

    		if (args[0].compareTo("-c") == 0)
    		{
    			String[] cmd_args = new String[args.length-1];
    			for(int i = 0; i < args.length-1; i++)
    			{
    				cmd_args[i] = args[i+1];
    			}
    			main_cmd(cmd_args);
    		}
    		else if (args[0].compareTo("-g") == 0)
    		{
    			main_gui();
    		}
    		else if (args[0].compareTo("-about") == 0)
    		{
    			about();
    		}
    		else
    		{
    			usage();
    		}
    	}
    }

    static void usage()
    {
    	String blanks = "";
    	for(int i = GlobalConfig.VERSION.length(); i < 35; i++)
    	{
    		blanks +=" ";
    	}
    	
    	System.out.println("+==============================================+");
    	System.out.println("|   openDLX - a DLX/MIPS processor simulator   |");
    	System.out.println("|   Version "+GlobalConfig.VERSION+blanks+"|");
    	System.out.println("|   Copyright (C) 2013 University of Augsburg  |");
    	System.out.println("+==============================================+");
    	System.out.println("| Usage:                                       |");
    	System.out.println("| For GUI version:                             |");
    	System.out.println("|   java -jar openDLX.jar [-g]                 |");
    	System.out.println("| For non interactive version:                 |");
    	System.out.println("|   java -jar openDLX.jar -c config_file.cfg   |");
    	System.out.println("| This help message:                           |");
    	System.out.println("|   java -jar openDLX.jar -h                   |");
      	System.out.println("| About & license information:                 |");
    	System.out.println("|   java -jar openDLX.jar -about               |");
    	System.out.println("+==============================================+");
		
	}
    
    static void about()
    {
    	System.out.println("====================================================================");
    	System.out.println(GlobalConfig.ABOUT);
    	System.out.println("====================================================================");
    }
    
    static void main_gui()
    {
    	// DLX is the default for graphical mode.
    	ArchCfg.isa_type = ISAType.DLX;
    	OpenDLXSimGui.openDLXGui_main();
    }
    
    static void main_cmd(String[] args)
    {
    	OpenDLXSimulator cmdl;
		try {
			cmdl = new OpenDLXSimulator(args);
		} catch (PipelineException e) {
			e.printStackTrace();
			cmdl = null;
			System.exit(1);
			
		}
		cmdl.openDLXCmdl_main();
    }

}
