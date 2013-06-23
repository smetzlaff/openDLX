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
package openDLX.gui.menubar.container;

import java.util.ArrayList;
import java.util.Iterator;

import openDLX.gui.menubar.OpenDLXSimMenuItem;

public class InternalFrameList
{

    private static InternalFrameList instance;
    private ArrayList<OpenDLXSimMenuItem> boxes = new ArrayList<OpenDLXSimMenuItem>();

    private InternalFrameList()
    {
    }

    public static InternalFrameList getInstance()
    {
        if (instance == null)
        {
            instance = new InternalFrameList();
        }
        return instance;
    }

    public void add(OpenDLXSimMenuItem item)
    {
        boxes.add(item);
    }

    public OpenDLXSimMenuItem get(String s)
    {
        Iterator<OpenDLXSimMenuItem> it = boxes.iterator();
        while (it.hasNext())
        {
            OpenDLXSimMenuItem tmp = it.next();
            if (tmp != null && tmp.getName() != null)
            {
                if (tmp.getName().equals(s))
                {
                    return tmp;
                }
            }
        }
        return null;
    }

}
