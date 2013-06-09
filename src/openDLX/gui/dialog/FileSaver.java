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
package openDLX.gui.dialog;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import openDLX.gui.MainFrame;
import openDLX.gui.Preference;

public class FileSaver
{

    // FIXME using static string here ...
    private String path = "/home";
    private String preferenceKey = "savefilechooserpath";

    public File saveAs(MainFrame mf)
    {
        path = Preference.pref.get(preferenceKey, path);
        @SuppressWarnings("serial")
        final JFileChooser chooser = new JFileChooser(path)
        {
            @Override
            public void approveSelection()
            {
                File f = getSelectedFile();
                if (f.exists())
                {
                    int result = JOptionPane.showConfirmDialog(this, "The file exists, overwrite?", "Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
                    switch (result)
                    {
                        case JOptionPane.YES_OPTION:
                            super.approveSelection();
                            return;
                        case JOptionPane.CANCEL_OPTION:
                            cancelSelection();
                        case JOptionPane.NO_OPTION:
                        case JOptionPane.CLOSED_OPTION:
                            return;
                    }
                }
                super.approveSelection();
            }

        };
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setVisible(true);
        chooser.setFileFilter(new FileFilter()
        {
            @Override
            public boolean accept(File f)
            {
                return f.getName().toLowerCase().endsWith(".s") || f.isDirectory();
            }

            @Override
            public String getDescription()
            {
                return "Assembler Files(*.s)";
            }
        });

        chooser.setSelectedFile(new File(mf.getLoadedCodeFilePath()));

        if (chooser.showSaveDialog(mf) == JFileChooser.APPROVE_OPTION)
        {
            path = chooser.getSelectedFile().getParent();
            Preference.pref.put(preferenceKey, path);
            return chooser.getSelectedFile();
        }
        else
        {
            return null;
        }
    }

}
