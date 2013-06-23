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

    private OpenDLXSimMenuItem internal_frame_item;
    private MainFrame mf;

    public CommandChangeWindowVisibility(OpenDLXSimMenuItem frame_item, MainFrame mf)
    {
        this.internal_frame_item = frame_item;
        this.mf = mf;
    }

    @Override
    public void execute()
    {
        for (JInternalFrame internalFrame : mf.getinternalFrames())
        {
            if (internalFrame.getTitle().equals(internal_frame_item.getName()))
            {
            	if(internalFrame.isIcon())
				 {
					try {
						internalFrame.setIcon(false);
					} catch (PropertyVetoException e) {
						e.printStackTrace();
					}
				}
            	if(internalFrame.isClosed() || !internalFrame.isVisible())
            	{
            		internalFrame.setVisible(true);
            	}
        		internalFrame.moveToFront();
            	
                /* // if users closes or opens frame - should it be a preference automatically ?
                 new FrameConfiguration(internalFrame).saveFrameConfiguration();*/
            }
        }
    }

}
