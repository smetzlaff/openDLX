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
package openDLX.gui.internalframes.factories;

import java.util.ArrayList;
import java.util.Hashtable;

import openDLX.gui.MainFrame;
import openDLX.gui.command.systemLevel.CommandLoadFrameConfigurationSysLevel;
import openDLX.gui.internalframes.concreteframes.ClockCycleFrame;
import openDLX.gui.internalframes.concreteframes.CodeFrame;
import openDLX.gui.internalframes.concreteframes.LogFrame;
import openDLX.gui.internalframes.concreteframes.MemoryFrame;
import openDLX.gui.internalframes.concreteframes.RegisterFrame;
import openDLX.gui.internalframes.concreteframes.StatisticsFrame;
import openDLX.gui.internalframes.concreteframes.editor.EditorFrame;

public class InternalFrameFactory
{
    //here you find every frames title -> preferences depend on names

    private MainFrame mf;
    static final private Hashtable<Class<?>, String> frameNames = new Hashtable<Class<?>, String>();
    private static InternalFrameFactory instance = null;

    static
    {
        //add here new frames, -> global names, id-interface
        frameNames.put(EditorFrame.class, "coding frame");
        frameNames.put(MemoryFrame.class, "memory");
        frameNames.put(RegisterFrame.class, "register set");
        frameNames.put(CodeFrame.class, "code");
        frameNames.put(StatisticsFrame.class, "statistics");
        frameNames.put(LogFrame.class, "log");
        frameNames.put(ClockCycleFrame.class, "cycles and pipeline");
    }

    public static InternalFrameFactory getInstance()
    {
        if (instance == null)
        {
            instance = new InternalFrameFactory();
        }
        return instance;
    }

    private InternalFrameFactory()
    {
        mf = MainFrame.getInstance();
    }

    public void createAllFrames(){
        createAllFrames(new String[]{});
    }

    public void createAllFrames(String[] intFrameOrder)
    {
        ArrayList<String> frameOrder = new ArrayList<>(frameNames.size());
        for (String s : intFrameOrder)
            frameOrder.add(s);
        for (String s : frameNames.values())
            if (!frameOrder.contains(s))
                frameOrder.add(s);

        for (String s : frameOrder)
        {
            if (s.equals("register set"))
                createRegisterFrame();
            else if (s.equals("code"))
                createCodeFrame();
            else if (s.equals("log"))
                createLogFrame();
            else if (s.equals("statistics"))
                createStatisticsFrame();
            else if (s.equals("memory"))
                createMemoryFrame(mf);
            else if (s.equals("cycles and pipeline"))
                createClockCycleFrame();
        }

        CommandLoadFrameConfigurationSysLevel c10 = new CommandLoadFrameConfigurationSysLevel(mf);
        c10.execute();
    }

    public static String getFrameName(Class<?> c)
    {
        return frameNames.get(c);
    }

    public void createMemoryFrame(MainFrame mf)
    {
        MemoryFrame f = new MemoryFrame(frameNames.get(MemoryFrame.class).toString(),mf);
        this.mf.addInternalFrame(f);
    }

    private void createRegisterFrame()
    {
        RegisterFrame rf = new RegisterFrame(frameNames.get(RegisterFrame.class).toString());
        mf.addInternalFrame(rf);
    }

    private void createCodeFrame()
    {
        CodeFrame cf = new CodeFrame(frameNames.get(CodeFrame.class).toString());
        mf.addInternalFrame(cf);
    }

    private void createStatisticsFrame()
    {
        StatisticsFrame sf = new StatisticsFrame(frameNames.get(StatisticsFrame.class).toString());
        mf.addInternalFrame(sf);
    }

    private void createLogFrame()
    {
        LogFrame lf = new LogFrame(frameNames.get(LogFrame.class).toString());
        mf.addInternalFrame(lf);
    }
     private void createClockCycleFrame()
    {
        ClockCycleFrame ccf = new ClockCycleFrame(frameNames.get(ClockCycleFrame.class).toString());
        mf.addInternalFrame(ccf);
    }


}
