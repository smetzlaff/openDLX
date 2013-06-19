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
package openDLX.gui.menubar;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import openDLX.gui.GUI_CONST.OpenDLXSimState;

public class StateValidator
{

    public static final OpenDLXSimState lazyStates[] =
    {
        OpenDLXSimState.IDLE
    };
    public static final OpenDLXSimState executingStates[] =
    {
        OpenDLXSimState.EXECUTING
    };
    public static final OpenDLXSimState executingOrRunningStates[] =
    {
        OpenDLXSimState.EXECUTING, OpenDLXSimState.RUNNING
    };
    public static final OpenDLXSimState RunningStates[] =
    {
        OpenDLXSimState.RUNNING
    };
    public static final OpenDLXSimState executingOrLazyStates[] =
    {
        OpenDLXSimState.EXECUTING, OpenDLXSimState.IDLE
    };
    public static final OpenDLXSimState allStates[] =
    {
        OpenDLXSimState.EXECUTING, OpenDLXSimState.IDLE, OpenDLXSimState.RUNNING
    };

    public static void validateMenu(JMenuBar jmb, OpenDLXSimState currentState)
    {

        for (int i = 0; i < jmb.getMenuCount(); ++i)
        {
            JMenu m = jmb.getMenu(i);
            for (int j = 0; j < m.getItemCount(); ++j)
            {
                OpenDLXSimState[] itemStates = null;
                if (m.getItem(j) instanceof OpenDLXSimMenuItem)
                {
                    OpenDLXSimMenuItem item = (OpenDLXSimMenuItem) m.getItem(j);
                    itemStates = item.state;
                    checkStates(item, currentState, itemStates);
                }
                else if (m.getItem(j) instanceof OpenDLXSimCheckBoxMenuItem)
                {
                    OpenDLXSimCheckBoxMenuItem item = (OpenDLXSimCheckBoxMenuItem) m.getItem(j);
                    itemStates = item.state;
                    checkStates(item, currentState, itemStates);
                }
                else if (m.getItem(j) instanceof OpenDLXSimRadioButtonMenuItem)
                {
                    OpenDLXSimRadioButtonMenuItem item = (OpenDLXSimRadioButtonMenuItem) m.getItem(j);
                    itemStates = item.state;
                    checkStates(item, currentState, itemStates);
                }

            }
        }
    }

    private static void checkStates(JMenuItem item, OpenDLXSimState currentState, OpenDLXSimState[] itemStates)
    {
        if (itemStates != null)
        {
            for (int k = 0; k < itemStates.length; ++k)
            {
                if (itemStates[k] == currentState)
                {
                    item.setEnabled(true);
                    return;
                }
            }
            item.setEnabled(false);
        }
    }

}
