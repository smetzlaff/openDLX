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
package openDLX.gui.internalframes.factories.tableFactories;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import openDLX.RegisterSet;
import openDLX.datatypes.ArchCfg;
import openDLX.datatypes.uint8;
import openDLX.gui.Preference;
import openDLX.gui.command.userLevel.CommandChangeRegister;
import openDLX.gui.internalframes.renderer.ChangeableFrameTableCellRenderer;
import openDLX.gui.internalframes.util.NotSelectableTableModel;

public class RegisterTableFactory extends TableFactory
{

    private RegisterSet rs;

    public RegisterTableFactory(RegisterSet rs)
    {
        this.rs = rs;
    }

    @Override
    public JTable createTable()
    {
        model = new NotSelectableTableModel();
        table = new JTable(model);
        table.setFocusable(false);
        model.addColumn("Register");
        model.addColumn("Values");

        for (int i = 0; i < ArchCfg.getRegisterCount(); ++i)
        {
            final Object secondItem;
            if (Preference.displayRegistersAsHex())
                secondItem = rs.read(new uint8(i));
            else
                secondItem = rs.read(new uint8(i)).getValue();

            model.addRow(new Object[] {
                    ArchCfg.getRegisterDescription(i), secondItem
            });
        }

        //default max width values change here
        TableColumnModel tcm = table.getColumnModel();
        tcm.getColumn(0).setMaxWidth(60);
        tcm.getColumn(1).setMaxWidth(150);
        table.setDefaultRenderer(Object.class, new ChangeableFrameTableCellRenderer());

        table.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                Point p = e.getPoint();
                int row = table.rowAtPoint(p);

                if (e.getClickCount() == 2)
                    new CommandChangeRegister(row).execute();
            }
        });

        return table;
    }

}
