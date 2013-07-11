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

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

import openDLX.gui.GUI_CONST;

public class ClockCycleFrameTableCellRenderer implements TableCellRenderer
{

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column)
    {
        //set defaults
        JLabel label = new JLabel((String) value);
        label.setOpaque(true);
        Border b = BorderFactory.createEmptyBorder(1, 1, 1, 1);
        label.setBorder(b);
        label.setFont(table.getFont());
        label.setForeground(table.getForeground());
        label.setBackground(table.getBackground());

        if (label.getText() != null)
        {
            //FETCH
            if (label.getText().contains(GUI_CONST.FETCH))
                label.setBackground(GUI_CONST.IF_COLOR);
            //DECODE
            else if (label.getText().contains(GUI_CONST.DECODE))
                label.setBackground(GUI_CONST.ID_COLOR);
            //EXECUTE
            else if (label.getText().contains(GUI_CONST.EXECUTE))
                label.setBackground(GUI_CONST.EX_COLOR);
            //MEMORY
            else if (label.getText().contains(GUI_CONST.MEMORY))
                label.setBackground(GUI_CONST.MEM_COLOR);
            //WRITEBACK
            else if (label.getText().contains(GUI_CONST.WRITEBACK))
                label.setBackground(GUI_CONST.WB_COLOR);
        }

        return label;
    }
}
