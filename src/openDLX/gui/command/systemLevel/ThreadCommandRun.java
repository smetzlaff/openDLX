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
package openDLX.gui.command.systemLevel;

import java.awt.EventQueue;

import openDLX.exception.PipelineException;
import openDLX.gui.GUI_CONST.OpenDLXSimState;
import openDLX.gui.MainFrame;
import openDLX.OpenDLXSimulator;

public class ThreadCommandRun implements Runnable
{

    private MainFrame mf;

    public ThreadCommandRun(MainFrame mf)
    {
        this.mf = mf;
    }

    @Override
    public void run()
    {
        OpenDLXSimulator openDLXSim = mf.getOpenDLXSim();
        //start running
        mf.setOpenDLXSimState(OpenDLXSimState.RUNNING);
        //check if running was paused/quit or if openDLXSim has finished
        while (!openDLXSim.isFinished() && mf.isRunning())
        {
            // loop through openDLX, do a cycle
            try
            {
                openDLXSim.step();
            }
            catch (PipelineException e)
            {
                mf.getPipelineExceptionHandler().handlePipelineExceptions(e);
            }
        }
        // when running stops or openDLX has finished, set state back to executing, as executing means a openDLX is loaded but not running through
        mf.setOpenDLXSimState(OpenDLXSimState.EXECUTING);

        try
        {
            EventQueue.invokeAndWait(new Runnable()
            {
                @Override
                public void run()
                {
                    //update frames when loop is finished or paused (isRunning is set to false by event dispatch thread/user)
                    new CommandUpdateFrames(mf).execute();
                }
            });
        }
        catch (Exception e)
        {
            System.err.println(e.toString());
            e.printStackTrace();
        }

        if (openDLXSim.isFinished())
        { // if the current openDLX has finished, dont allow any gui updates any more                
            mf.setUpdateAllowed(false);
            new CommandSimulatorFinishedInfo().execute();
        }
    }

}
