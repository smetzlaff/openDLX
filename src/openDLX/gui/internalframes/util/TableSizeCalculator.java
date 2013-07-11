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

import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

public class TableSizeCalculator
{
    public static final int SET_SIZE_HEIGHT = 0;
    public static final int SET_SIZE_WIDTH = 1;
    public static final int SET_SIZE_BOTH = 2;

    public static void setDefaultMaxTableSize(JScrollPane scrollpane, JTable table, int type)
    {
        int width = 0;
        final int height = (table.getRowCount() + 2) * table.getRowHeight();

        final int colCount = table.getModel().getColumnCount();
        final TableColumnModel cm = table.getColumnModel();

        for (int i = 0; i < colCount; ++i)
        {
            width += cm.getColumn(i).getMaxWidth();
        }

        switch (type)
        {
            case SET_SIZE_HEIGHT:
                scrollpane.setPreferredSize(new Dimension(
                        scrollpane.getPreferredSize().width, height));
                break;
            case SET_SIZE_WIDTH:
                scrollpane.setPreferredSize(new Dimension(width,
                        scrollpane.getPreferredSize().height));
                break;
            default:
                scrollpane.setPreferredSize(new Dimension(width, height));
        }
    }
}
