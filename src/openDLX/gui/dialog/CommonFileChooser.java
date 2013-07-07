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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import openDLX.gui.Preference;

public abstract class CommonFileChooser {

    protected String preferenceKey;
    private String path = "/home";

    public CommonFileChooser() {
        super();
    }

    protected abstract FileFilter getFileFilter();

    public File chooseFile() {
        final JFileChooser chooser = new JFileChooser("Choose file");
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        chooser.setFileFilter(getFileFilter());

        path = Preference.pref.get(preferenceKey, path);
        chooser.setCurrentDirectory(new File(path));
        chooser.addPropertyChangeListener(new PropertyChangeListener()
        {
            @Override
            public void propertyChange(PropertyChangeEvent e)
            {
                if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)
                        || e.getPropertyName().equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY))
                {
                    e.getNewValue();
                }
            }

        });

        chooser.setVisible(true);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
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