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
package openDLX.gui.internalframes.util;

import javax.swing.JOptionPane;

public class ValueInput
{

    public static Integer getValue(String message, Object defaultValue) throws NumberFormatException
    {
        String valueString = JOptionPane.showInputDialog(message, defaultValue);
        return getValueSilent(valueString);
    }

    public static Integer getValueSilent(String valueString)
    {
        if (valueString == null)
            return null;
        else if (valueString.contains("0x"))
            return Integer.parseInt(valueString.substring(2), 16);
        else
            return Integer.parseInt(valueString);
    }
}
