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
package openDLX.gui;

import javax.swing.SwingUtilities;
import openDLX.gui.LookAndFeel.LookAndFeelStrategy;
import openDLX.gui.LookAndFeel.LookAndFeelStrategyJava;
import openDLX.gui.LookAndFeel.LookAndFeelStrategySystemMonoSpaced;

public class OpenDLXSimGui
{

    static final public String preferenceKey = "lookandfeel";    

    public static void openDLXGui_main()
    {
        //set default
        String lookAndFeel = LookAndFeelStrategySystemMonoSpaced.class.toString();
        //get user preference
        lookAndFeel = Preference.pref.get(preferenceKey, lookAndFeel);
        //find fitting LaF
        if (LookAndFeelStrategySystemMonoSpaced.class.toString().equals(lookAndFeel))
        {
            new LookAndFeelStrategySystemMonoSpaced().setLookAndFeel();
            
        }
        else if (LookAndFeelStrategyJava.class.toString().equals(lookAndFeel))
        {
            new LookAndFeelStrategyJava().setLookAndFeel();
        }


        MainFrame.getInstance();

    }

    //set look and feel for the whole program
    public static void setLookAndFeel(LookAndFeelStrategy laf)
    {
        laf.setLookAndFeel();
        SwingUtilities.updateComponentTreeUI(MainFrame.getInstance());
    }

}
