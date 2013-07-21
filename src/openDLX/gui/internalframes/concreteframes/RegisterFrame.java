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
package openDLX.gui.internalframes.concreteframes;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import openDLX.RegisterSet;
import openDLX.datatypes.ArchCfg;
import openDLX.datatypes.uint32;
import openDLX.datatypes.uint8;
import openDLX.gui.MainFrame;
import openDLX.gui.Preference;
import openDLX.gui.internalframes.OpenDLXSimInternalFrame;
import openDLX.gui.internalframes.factories.tableFactories.RegisterTableFactory;
import openDLX.gui.internalframes.util.TableSizeCalculator;

@SuppressWarnings("serial")
public final class RegisterFrame extends OpenDLXSimInternalFrame
{

    private RegisterSet rs;
    private JTable registerTable;

    public RegisterFrame(String title)
    {
        super(title, false);
        this.rs = MainFrame.getInstance().getOpenDLXSim().getPipeline().getRegisterSet();
        initialize();
    }

    @Override
    public void update()
    {
        for (int i = 0; i < ArchCfg.getRegisterCount(); ++i)
        {
            final String value;
            final uint32 register_value = rs.read(new uint8(i));
            if (Preference.displayRegistersAsHex())
                value = register_value.getValueAsHexString();
            else
                value = register_value.getValueAsDecimalString();
            
            registerTable.getModel().setValueAt(value, i, 1);
        }
    }

    @Override
    protected void initialize()
    {
        super.initialize();
        //make the scrollpane
        registerTable = new RegisterTableFactory(rs).createTable();
        JScrollPane scrollpane = new JScrollPane(registerTable);
        scrollpane.setFocusable(false);
        registerTable.setFillsViewportHeight(true);
        TableSizeCalculator.setDefaultMaxTableSize(scrollpane, registerTable,
                TableSizeCalculator.SET_SIZE_BOTH);
        //config internal frame
        setLayout(new BorderLayout());
        add(scrollpane, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    @Override
    public void clean()
    {
        setVisible(false);
        dispose();
    }

}
