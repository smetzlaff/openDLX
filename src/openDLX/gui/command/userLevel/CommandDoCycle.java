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

import openDLX.OpenDLXSimulator;
import openDLX.exception.PipelineException;
import openDLX.gui.MainFrame;
import openDLX.gui.command.Command;
import openDLX.gui.command.systemLevel.CommandSimulatorFinishedInfo;
import openDLX.gui.command.systemLevel.CommandUpdateFrames;

public class CommandDoCycle implements Command
{

    private MainFrame mf;

    public CommandDoCycle(MainFrame mf)
    {
        this.mf = mf;
    }

    @Override
    public void execute()
    {

        if (mf.isExecuting() && mf.isUpdateAllowed())
        {
            OpenDLXSimulator openDLXSim = mf.getOpenDLXSim();
            try
            {
                openDLXSim.step();
            }
            catch (PipelineException e)
            {
                mf.getPipelineExceptionHandler().handlePipelineExceptions(e);
            }

            if (!openDLXSim.isFinished())
            {
                new CommandUpdateFrames(mf).execute();
            }
            else
            {
                mf.setUpdateAllowed(false);
                new CommandSimulatorFinishedInfo().execute();
            }
        }

    }

}
