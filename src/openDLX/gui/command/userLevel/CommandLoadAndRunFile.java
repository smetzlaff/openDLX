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
import openDLX.gui.command.systemLevel.CommandCompileCode;
import openDLX.gui.command.systemLevel.CommandLoadCodeFileToEditor;
import openDLX.gui.command.systemLevel.CommandOpenCodeFile;
import openDLX.gui.command.systemLevel.CommandResetSimulator;
import openDLX.gui.command.systemLevel.CommandSaveFrameConfigurationSysLevel;
import openDLX.gui.command.systemLevel.CommandStartExecuting;

public class CommandLoadAndRunFile implements Command
{

    private MainFrame mf;

    public CommandLoadAndRunFile(MainFrame mf)
    {
        this.mf = mf;
    }

    @Override
    public void execute()
    {
        if (!mf.isRunning())
        {
            if (!mf.isEditorTextSaved())
            {
                if (JOptionPane.showConfirmDialog(mf, "Discard unsaved editor changes?") != JOptionPane.OK_OPTION)
                {
                    return;
                }
            }
            //save current window position
            new CommandSaveFrameConfigurationSysLevel(mf).execute();
            //show filechooser dialog and choose file
            CommandOpenCodeFile c10 = new CommandOpenCodeFile(mf);
            c10.execute();
            //get chosen file
            File f = c10.getCodeFile();

            //file is null if user canceled filechooser dialog
            if (f != null)
            {
                //reset simulator before loading new content
                new CommandResetSimulator(mf).execute();

                //put code into editorFrame
                new CommandLoadCodeFileToEditor(mf, f, true).execute();
                mf.setLoadedCodeFilePath(f.getAbsolutePath());
                //compile/assemble code with asm package
                CommandCompileCode c8 = new CommandCompileCode(mf, f);
                c8.execute();
                //get result = config file
                File configFile = c8.getConfigFile();

                //check if assembly was successfull
                if (configFile != null)
                {
                    //initialize openDLX and create internal frames, set status to executing
                    new CommandStartExecuting(mf, configFile).execute();
                }
            }

        }
    }

}
