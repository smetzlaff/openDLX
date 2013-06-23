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

import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;

import openDLX.gui.MainFrame;
import openDLX.gui.command.Command;
import openDLX.gui.menubar.OpenDLXSimMenuItem;

public class CommandChangeWindowVisibility implements Command
{

    private OpenDLXSimMenuItem internalFrameMenuItem;
    private MainFrame mf;

    public CommandChangeWindowVisibility(OpenDLXSimMenuItem internalFrameMenuItem, MainFrame mf)
    {
        this.internalFrameMenuItem = internalFrameMenuItem;
        this.mf = mf;
    }

    @Override
    public void execute()
    {
        for (JInternalFrame internalFrame : mf.getinternalFrames())
        {
            if (internalFrame.getTitle().equals(internalFrameMenuItem.getName()))
            {
                if (internalFrame.isIcon())
                {
                    try {
                        internalFrame.setIcon(false);
                    } catch (PropertyVetoException e) {
                        e.printStackTrace();
                    }
                }
                if (internalFrame.isClosed() || !internalFrame.isVisible() ||
                        !internalFrame.isEnabled())
                {
                    internalFrame.setVisible(true);
                }
                internalFrame.moveToFront();

                try
                {
                    internalFrame.setSelected(true);
                }
                catch (PropertyVetoException e)
                {
                    e.printStackTrace();
                }

                /* // if users closes or opens frame - should it be a preference automatically ?
                 new FrameConfiguration(internalFrame).saveFrameConfiguration();*/
                break;
            }
        }
    }

}
