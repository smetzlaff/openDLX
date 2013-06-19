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

import java.io.File;
import javax.swing.JOptionPane;
import openDLX.gui.MainFrame;
import openDLX.gui.command.Command;
import openDLX.gui.command.systemLevel.CommandLoadCodeFileToEditor;
import openDLX.gui.command.systemLevel.CommandOpenCodeFile;

public class CommandLoadFileBelow implements Command
{

    private MainFrame mf;

    public CommandLoadFileBelow(MainFrame mf)
    {
        this.mf = mf;
    }

    @Override
    public void execute()
    {
        if (!mf.isRunning())
        {
            CommandOpenCodeFile c10 = new CommandOpenCodeFile(mf);
            c10.execute();
            File f = c10.getCodeFile();

            try
            {
                if (f != null)
                {
                    new CommandLoadCodeFileToEditor(mf, f, false).execute();
                }
            }
            catch (Exception e)
            {
                System.err.println(e.toString());
                e.printStackTrace();
                JOptionPane.showMessageDialog(mf, "Loading file into editor failed");
            }
        }
    }

}
