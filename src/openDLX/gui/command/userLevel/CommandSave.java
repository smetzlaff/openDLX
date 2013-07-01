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

import java.awt.Cursor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

import openDLX.gui.MainFrame;
import openDLX.gui.command.Command;
import openDLX.gui.dialog.FileSaver;

public class CommandSave implements Command
{

    @Override
    public void execute()
    {
        MainFrame mf = MainFrame.getInstance();
        if (!mf.isRunning())
        {
            mf.getContentPane().setCursor(
                    Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            File saveFile = new FileSaver().saveAs(mf);
            mf.getContentPane().setCursor(
                    Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            if (saveFile != null)
            {
                try
                {
                    BufferedWriter out = new BufferedWriter(new FileWriter(saveFile.getAbsolutePath()));
                    out.write(mf.getEditorText());
                    out.close();
                    mf.setEditorSavedState();
                }
                catch (IOException e)
                {
                    System.out.println("Exception ");
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(mf, "Saving file failed: " + e.toString());
                }
            }
        }
    }

}
