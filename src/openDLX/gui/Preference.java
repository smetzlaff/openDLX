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
package openDLX.gui;

import java.util.prefs.Preferences;

import openDLX.config.GlobalConfig;

public class Preference
{
    public static Preferences pref = Preferences.userRoot().node(
            GlobalConfig.PREFERENCES_DIR);

    // global definitions of the different preference keys
    public static final String forwardingPreferenceKey = "forwadingenabled";
    public static final String mipsCompatibilityPreferenceKey = "mipscompatibilityenabled";
    public static final String isaTypePreferenceKey = "isatype";
    public static final String bpTypePreferenceKey = "bptype";
    public static final String bpInitialStatePreferenceKey = "bbinitialstate";
    public static final String btbSizePreferenceKey = "btbsize";
    public static final String maxCyclesPreferenceKey = "maxcycles";
    public static final String displayMemoryAsHex = "displayMemoryAsHex";
    public static final String showExitMessage = "showexitmessage";
    public static final String lookAndFeel = "lookandfeel";
    // TODO implement option
    public static final String displayRegistersAsHex = "displayRegistersAsHex";

    public static boolean displayMemoryAsHex()
    {
        // TODO: Add GUI switch
        // boolean isHex = pref.getBoolean(displayMemoryAsHex, true);
        return true;
    }

    public static boolean displayRegistersAsHex()
    {
        // TODO: Add GUI switch
        // boolean isHex = pref.getBoolean(displayRegistersAsHex, true);
        return true;
    }

    // TODO: Also move all configuration stuff into this file.
}
