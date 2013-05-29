/*******************************************************************************
 * openDLX - A DLX/MIPS processor simulator.
 * Copyright (C) 2013 The openDLX project, University of Augsburg, Germany
 * Project URL: <http://sourceforge.net/projects/opendlx>
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
package openDLX.gui.command.systemLevel;

import java.io.File;
import javax.swing.JOptionPane;

import openDLX.gui.MainFrame;
import openDLX.gui.command.Command;

public class CommandStartExecuting implements Command
{

    private File configFile; //in
    private MainFrame mf;

    public CommandStartExecuting(MainFrame mf, File f)
    {
        this.mf = mf;
        this.configFile = f;
    }

    @Override
    public void execute()
    {
        // call the openDLX constructor and assign a configFile to the new openDLX
        CommandCreateOpenDLXSim c10 = new CommandCreateOpenDLXSim(mf, configFile);
        c10.execute();
        if (mf.getOpenDLXSim() != null)
        {   //create all executing-frames   
            CommandCreateFrames c9 = new CommandCreateFrames(mf);
            c9.execute();
        }
        else
        {
        	System.out.println("Could not initiate openDLX simulator.");
            JOptionPane.showMessageDialog(mf, "Could not initiate openDLX simulator.");
        }
    }

}