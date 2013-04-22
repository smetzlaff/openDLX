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
package openDLX.gui.LookAndFeel;

import javax.swing.UIManager;

public class LookAndFeelStrategyJava extends LookAndFeelStrategyMonoSpaced
{

    

    @Override
    public void setLookAndFeel()
    {
        try
        {
            super.setLookAndFeel();
            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName());

        }
        catch (Exception e)
        {
            System.err.println("Failed to set System look and Feel");
            e.printStackTrace();
        }
    }
    
     public static String getLookAndFeelName()
    {
        return UIManager.getCrossPlatformLookAndFeelClassName();
    }

}
