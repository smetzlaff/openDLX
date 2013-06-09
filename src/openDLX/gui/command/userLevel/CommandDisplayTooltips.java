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
package openDLX.gui.command.userLevel;

import openDLX.gui.Preference;
import openDLX.gui.command.Command;
import openDLX.gui.menubar.OpenDLXSimCheckBoxMenuItem;

public class CommandDisplayTooltips implements Command
{

    public static final String preferenceKey = "tooltipsenabled";
    private static boolean tooltipsEnabled = Preference.pref.getBoolean(preferenceKey, true);

    private OpenDLXSimCheckBoxMenuItem item;

    public CommandDisplayTooltips(OpenDLXSimCheckBoxMenuItem item)
    {
        this.item = item;
    }

    public static boolean isTooltipsEnabled()
    {
        return tooltipsEnabled;
    }

    public static void setTooltipsEnabled(boolean tooltipsEnabled)
    {
        CommandDisplayTooltips.tooltipsEnabled = tooltipsEnabled;
        //save new preference
        Preference.pref.putBoolean(preferenceKey, tooltipsEnabled);
    }

    @Override
    public void execute()
    {
        setTooltipsEnabled(item.isSelected());
    }

}
