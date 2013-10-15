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

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoManager;

import openDLX.gui.command.Command;

/* TODO agarbade: Newly added functionality. Waiting for approval */
public class CommandPerformEditorRedo implements Command
{
	private UndoManager manager;

	/**
	 * 
	 * @param mgr Takes the UndoManager object
	 */
	public CommandPerformEditorRedo( UndoManager mgr )
	{
		this.manager = mgr;
	}
	
    @Override
    public void execute()
    {
    	try {
			manager.redo();
		} catch (CannotRedoException e) {
			/* This exception is thrown, when there is no more redo available.
			 * Nothing to be done here
			 */
		}
    }

}
