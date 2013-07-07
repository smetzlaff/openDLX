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

import javax.swing.filechooser.FileFilter;

public class CodeFileChooser extends CommonFileChooser
{
    public CodeFileChooser()
    {
        super();
        preferenceKey = "codefilechooserpath";
    }

    @Override
    protected FileFilter getFileFilter()
    {
        return new FileFilter()
        {
            @Override
            public boolean accept(File f)
            {
                return f.getName().toLowerCase().endsWith(".s") || f.isDirectory();
            }

            @Override
            public String getDescription()
            {
                return "Assembler Files (*.s)";
            }
        };
    }
}
