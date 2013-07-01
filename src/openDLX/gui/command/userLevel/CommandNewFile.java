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

import openDLX.gui.MainFrame;
import openDLX.gui.command.Command;

public class CommandNewFile implements Command
{
    private MainFrame mf;

    public CommandNewFile(MainFrame mf)
    {
        this.mf = mf;
    }

    @Override
    public void execute()
    {
        if (!mf.isRunning())
        {
            if (!mf.getEditorText().isEmpty())
            {
                if (!mf.isEditorTextSaved())
                {
                    int result = JOptionPane.showConfirmDialog(mf,
                            "Save current file?");
                    if (result == JOptionPane.OK_OPTION)
                    {
                        new CommandSave().execute();
                        mf.setEditorText("");
                        mf.setEditorSavedState();
                    }
                    else if (result == JOptionPane.NO_OPTION)
                    {
                        mf.setEditorText("");
                        mf.setEditorSavedState();
                    }
                }
                else
                {
                    if (JOptionPane.showConfirmDialog(mf, "Clear file?") == JOptionPane.OK_OPTION)
                    {
                        mf.setEditorText("");
                        mf.setEditorSavedState();
                    }
                }
            }
        }
        
        // TODO Actually no file is assigned to the empty editor. 
        // (However, as long as the file is not saved, the simulator uses temporary files.)
    }

}
