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

import java.util.prefs.BackingStoreException;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import openDLX.config.GlobalConfig;
import openDLX.gui.MainFrame;
import openDLX.gui.Preference;
import openDLX.gui.command.Command;
import openDLX.gui.command.systemLevel.CommandSaveFrameConfigurationSysLevel;
import openDLX.util.TmpFileCleaner;

public class CommandExitProgram implements Command
{

    private MainFrame mf;

    public CommandExitProgram(MainFrame mf)
    {
        this.mf = mf;
    }

    @Override
    public void execute()
    {
        if (close())
        {
            System.exit(0);
        }
    }

    public boolean close()
    {
        if (!mf.isEditorTextSaved())
        {
            if (JOptionPane.showConfirmDialog(mf, "Discard unsaved editor changes?") != JOptionPane.OK_OPTION)
            {
                return false;
            }
        }
        
        if (Preference.pref.getBoolean(Preference.showExitMessage, true))
        {
            final String exit_message = "Are you sure you want to exit?";
            final JCheckBox exit_checkbox = new JCheckBox("Do not show this message again.");
            final Object content[] = {exit_message, exit_checkbox};
            final int result = JOptionPane.showConfirmDialog(
                    mf,
                    content,
                    "Exit openDLX "+ GlobalConfig.VERSION,
                    JOptionPane.YES_NO_OPTION);

            if (result != JOptionPane.YES_OPTION)
            {
                return false;
            }

            Preference.pref.putBoolean(Preference.showExitMessage, !exit_checkbox.isSelected());
        }

        try
        {
            // push preferences to persistent store
            Preference.pref.flush();
        } catch (BackingStoreException e)
        {
            System.err.println("Could not save preferences.");
            e.printStackTrace();
        }

        // delete temporary files
        TmpFileCleaner.cleanUp();

        //save current window position
        new CommandSaveFrameConfigurationSysLevel(mf).execute();

        return true;
    }
}
