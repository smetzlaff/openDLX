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
package openDLX.gui.internalframes.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

import openDLX.gui.internalframes.util.LogReader;

public class LogFrameTableCellRenderer implements TableCellRenderer
{
    //log Colors, change here:
    private final Color debugColor = new Color(12,12,12);
    private final Color infoColor = Color.BLUE;
    private final Color warnColor = Color.ORANGE;
    private final Color errorColor = Color.RED;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column)
    {
        //set defaults
        JLabel label = new JLabel(value.toString());
        label.setOpaque(true);
        Border b = BorderFactory.createEmptyBorder(1, 1, 1, 1);
        label.setBorder(b);
        label.setFont(table.getFont());
        label.setForeground(table.getForeground());
        label.setBackground(table.getBackground());

        String help = table.getModel().getValueAt(row, column).toString();

            //DEBUG
            if (help.contains(LogReader.DEBUG_STRING))
                label.setForeground(debugColor);
            //INFO
            else  if (help.contains(LogReader.INFO_STRING))
                label.setForeground(infoColor);
            //WARN
            else  if (help.contains(LogReader.WARN_STRING))
                label.setForeground(warnColor);
            //ERROR
            else  if (help.contains(LogReader.ERROR_STRING))
                label.setForeground(errorColor);

        return label;
    }

}
