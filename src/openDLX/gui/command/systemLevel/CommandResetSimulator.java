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

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import openDLX.gui.MainFrame;
import openDLX.gui.command.Command;
import openDLX.gui.internalframes.OpenDLXSimInternalFrame;
import openDLX.util.Statistics;

public class CommandResetSimulator implements Command
{

    private MainFrame mf;//in

    public CommandResetSimulator(MainFrame mf)
    {
        this.mf = mf;
    }

    @Override
    public void execute()
    {
        try
        {
            mf.setOpenDLXSim(null);
            mf.setUpdateAllowed(true);
            mf.setConfigFile(null);
            mf.setPause(false);
            mf.setRunSpeed(MainFrame.RUN_SPEED_DEFAULT);
            mf.output.clear();

            for (JInternalFrame jif : mf.getinternalFrames())
                if (jif instanceof OpenDLXSimInternalFrame)
                    ((OpenDLXSimInternalFrame) jif).clean();

            Statistics.getInstance().reset();
        }
        catch (Exception e)
        {
            System.err.println(e.toString());
            e.printStackTrace();
            JOptionPane.showMessageDialog(mf, "resetting simulator - removing/cleaning frames failed");
        }
    }

}
