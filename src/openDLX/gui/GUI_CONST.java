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

import java.awt.Color;

public interface GUI_CONST
{

    static final String FETCH = "IF";
    static final String DECODE = "ID";
    static final String EXECUTE = "EX";
    static final String MEMORY = "MEM";
    static final String WRITEBACK = "WB";
    static final Color IF_COLOR = new Color(250, 251, 119);
    static final Color EX_COLOR = Color.RED;
    static final Color ID_COLOR = new Color(182, 115, 8);
    static final Color WB_COLOR = Color.LIGHT_GRAY;
    static final Color MEM_COLOR = Color.GREEN;

    /*current state of the program
     LAZY = no openDLXSim loaded
     EXECUTING = openDLXSim Loaded 
     RUNNING = openDLXSim in step through loop */
    public enum OpenDLXSimState
    {
        RUNNING, IDLE, EXECUTING
    }
}
