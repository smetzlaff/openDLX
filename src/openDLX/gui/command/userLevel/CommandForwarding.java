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

import openDLX.datatypes.ArchCfg;
import openDLX.gui.Preference;
import openDLX.gui.command.Command;
import openDLX.gui.menubar.OpenDLXSimCheckBoxMenuItem;

public class CommandForwarding implements Command
{

    private static boolean forwardingEnabled;
    private OpenDLXSimCheckBoxMenuItem item;

    static
    {
        //get saved preference, default -> forwardingenabled = true
        forwardingEnabled = Preference.pref.getBoolean(Preference.forwardingPreferenceKey, true);
        if (forwardingEnabled)
        {
            ArchCfg.use_forwarding = true;
            ArchCfg.use_load_stall_bubble = Preference.pref.getBoolean(Preference.mipsCompatibilityPreferenceKey, true);
        }
        else
        {
            ArchCfg.use_forwarding = false;
            ArchCfg.use_load_stall_bubble = false;
        }
    }

    public CommandForwarding(OpenDLXSimCheckBoxMenuItem item)
    {
        this.item = item;
    }

//    TODO: Unused?!
//    public static boolean isForwardingEnabled()
//    {
//        return forwardingEnabled;
//    }

    public static void setForwardingEnabled(boolean forwardingEnabled)
    {

        CommandForwarding.forwardingEnabled = forwardingEnabled;
        //save new preference
        Preference.pref.putBoolean(Preference.forwardingPreferenceKey, forwardingEnabled);

        if (forwardingEnabled)
        {
            ArchCfg.use_forwarding = true;
//            ArchCfg.use_load_stall_bubble = true;
        }
        else
        {
            ArchCfg.use_forwarding = false;
            ArchCfg.use_load_stall_bubble = false;
        }
    }

    @Override
    public void execute()
    {
        setForwardingEnabled(item.isSelected());
    }

}
