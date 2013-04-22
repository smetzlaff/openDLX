/*******************************************************************************
 * openDLX - A DLX/MIPS processor simulator.
 * Copyright (C) 2013 The openDLX project, University of Augsburg, Germany
 * Project URL: <http://sourceforge.net/projects/opendlx>
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
package openDLX.gui.menubar;

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import openDLX.gui.GUI_CONST.OpenDLXSimState;

public abstract class JMenuBarFactory
{

    private ActionListener al = null;
    private ItemListener il = null;
    protected JMenuBar jmb = new JMenuBar();

    public JMenuBarFactory(ActionListener al, ItemListener il)
    {
        assert al != null && il != null;
        this.al = al;
        this.il = il;
    }

    public abstract JMenuBar createJMenuBar();

    protected OpenDLXSimMenuItem addMenuItem(JMenu father, String name, KeyStroke accelerator, OpenDLXSimState state [])
    {
        OpenDLXSimMenuItem openDLXSimMenuItem = new OpenDLXSimMenuItem(state,name);
        initializeMenuItem(openDLXSimMenuItem, father, name, accelerator);
        openDLXSimMenuItem.addActionListener(al);
        return openDLXSimMenuItem;
    }

    protected OpenDLXSimCheckBoxMenuItem addCheckBoxMenuItem(JMenu father, String name, KeyStroke accelerator, OpenDLXSimState state [])
    {
        OpenDLXSimCheckBoxMenuItem jMenuItem = new OpenDLXSimCheckBoxMenuItem(state,name);
        jMenuItem.setState(false);
        initializeMenuItem(jMenuItem, father, name, accelerator);
        jMenuItem.addItemListener(il);
        return jMenuItem;

    }

    protected OpenDLXSimRadioButtonMenuItem addRadioButtonMenuItem(JMenu father, String name, KeyStroke accelerator, ButtonGroup group, OpenDLXSimState state [])
    {
        OpenDLXSimRadioButtonMenuItem jRadioButtonItem = new OpenDLXSimRadioButtonMenuItem(state,name);
        jRadioButtonItem.setSelected(true);
        jRadioButtonItem.addActionListener(al);
        group.add(jRadioButtonItem);
        initializeMenuItem(jRadioButtonItem, father, name, accelerator);
        return jRadioButtonItem;

    }

    protected void initializeMenuItem(JMenuItem jMenuItem, JMenu father, String name, KeyStroke accelerator)
    {
        jMenuItem.setAccelerator(accelerator);
        father.add(jMenuItem);

    }

}
