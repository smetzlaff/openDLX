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
package openDLX.gui.command.userLevel;

import javax.swing.JRadioButtonMenuItem;
import openDLX.gui.LookAndFeel.LookAndFeelStrategySystemMonoSpaced;
import openDLX.gui.OpenDLXSimGui;
import openDLX.gui.Preference;
import openDLX.gui.command.Command;

public class CommandSetLaFSystem implements Command
{

    private JRadioButtonMenuItem item;

    public CommandSetLaFSystem(JRadioButtonMenuItem item)
    {
        this.item = item;
    }

    @Override
    public void execute()
    {
        OpenDLXSimGui.setLookAndFeel(new LookAndFeelStrategySystemMonoSpaced());
        item.setSelected(true);
        Preference.pref.put(OpenDLXSimGui.preferenceKey, LookAndFeelStrategySystemMonoSpaced.class.toString());
    }

}
