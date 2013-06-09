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
package openDLX.gui.command.userLevel;

import javax.swing.JOptionPane;

import openDLX.exception.PipelineException;
import openDLX.gui.MainFrame;
import openDLX.gui.Preference;
import openDLX.gui.command.Command;
import openDLX.gui.command.systemLevel.CommandSimulatorFinishedInfo;
import openDLX.gui.command.systemLevel.CommandUpdateFrames;
import openDLX.gui.internalframes.util.ValueInput;
import openDLX.OpenDLXSimulator;

public class CommandDoXCycles implements Command
{

    private MainFrame mf;
    private String preferenceKey = "doxcyclesnumber";
    //default cycle number 
    private int cycles = 5;

    public CommandDoXCycles(MainFrame mf)
    {
        this.mf = mf;
    }

    @Override
    public void execute()
    {
        if (mf.isExecuting() && mf.isUpdateAllowed())
        {
            try
            {
                OpenDLXSimulator openDLXSim = mf.getOpenDLXSim();
                // if there is a saved preference for x cycles, load it
                cycles = Preference.pref.getInt(preferenceKey, cycles);
                //show inputDialog and get input value
                Integer value = ValueInput.getValue("cycles: ", cycles);
                // value is null if there was an error during input
                if (value != null)
                {
                    // if value is valid, save it as new preference
                    Preference.pref.putInt(preferenceKey, value);
                    for (int i = 1; i <= value; ++i)
                    {
                        if (!openDLXSim.isFinished())
                        {
                            try
                            {
                                openDLXSim.step();
                            }
                            catch (PipelineException e)
                            {
                                mf.getPipelineExceptionHandler().handlePipelineExceptions(e);
                            }
                        }
                        else
                        {
                            break;
                        }

                    }
                    new CommandUpdateFrames(mf).execute();

                    if (openDLXSim.isFinished())
                    { // if the current openDLX has finished, dont allow any gui updates any more                
                        mf.setUpdateAllowed(false);
                        new CommandSimulatorFinishedInfo().execute();
                    }
                }
            }
            catch (NumberFormatException e)
            {
                JOptionPane.showMessageDialog(mf, "for input only Integer - decimal or hex (0x...) allowed");
                //if an error during input occured, restart input dialog to get new input
                execute();
            }
            catch (Exception e)
            {
                //something else went wrong
                System.err.println(e.toString());
                e.printStackTrace();
                JOptionPane.showMessageDialog(mf, "Executing commands failed");
            }
        }


    }

}
