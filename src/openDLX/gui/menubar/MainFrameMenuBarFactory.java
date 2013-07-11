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

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import openDLX.gui.GUI_CONST.OpenDLXSimState;
import openDLX.gui.MainFrame;
import openDLX.gui.Preference;
import openDLX.gui.command.Command;
import openDLX.gui.command.EventCommandLookUp;
import openDLX.gui.command.userLevel.CommandChangeWindowVisibility;
import openDLX.gui.command.userLevel.CommandClearAllPreferences;
import openDLX.gui.command.userLevel.CommandDisplayTooltips;
import openDLX.gui.command.userLevel.CommandDoCycle;
import openDLX.gui.command.userLevel.CommandDoXCycles;
import openDLX.gui.command.userLevel.CommandExitProgram;
import openDLX.gui.command.userLevel.CommandForwarding;
import openDLX.gui.command.userLevel.CommandLoadAndRunFile;
import openDLX.gui.command.userLevel.CommandLoadFile;
import openDLX.gui.command.userLevel.CommandLoadFileBelow;
import openDLX.gui.command.userLevel.CommandLoadFrameConfigurationUsrLevel;
import openDLX.gui.command.userLevel.CommandNewFile;
import openDLX.gui.command.userLevel.CommandResetCurrentProgram;
import openDLX.gui.command.userLevel.CommandRun;
import openDLX.gui.command.userLevel.CommandRunFromConfigurationFile;
import openDLX.gui.command.userLevel.CommandRunSlowly;
import openDLX.gui.command.userLevel.CommandRunToAddressX;
import openDLX.gui.command.userLevel.CommandSave;
import openDLX.gui.command.userLevel.CommandSaveFrameConfigurationUsrLevel;
import openDLX.gui.command.userLevel.CommandSetLaF;
import openDLX.gui.command.userLevel.CommandShowAbout;
import openDLX.gui.command.userLevel.CommandShowOptionDialog;
import openDLX.gui.command.userLevel.CommandStopRunning;
import openDLX.gui.internalframes.concreteframes.ClockCycleFrame;
import openDLX.gui.internalframes.concreteframes.CodeFrame;
import openDLX.gui.internalframes.concreteframes.LogFrame;
import openDLX.gui.internalframes.concreteframes.MemoryFrame;
import openDLX.gui.internalframes.concreteframes.RegisterFrame;
import openDLX.gui.internalframes.concreteframes.StatisticsFrame;
import openDLX.gui.internalframes.concreteframes.editor.EditorFrame;
import openDLX.gui.internalframes.factories.InternalFrameFactory;

public class MainFrameMenuBarFactory
{
    private static final String STRING_MENU_FILE = "File";
    public static final String STRING_MENU_SIMULATOR = "Simulator";
    private static final String STRING_MENU_WINDOW = "Window";
    private static final String STRING_MENU_LAF = "Look & Feels";
    private static final String STRING_MENU_HELP = "Help";

    private static final String STRING_MENU_FILE_NEW = "New";
    private static final String STRING_MENU_FILE_OPEN = "Open";
    private static final String STRING_MENU_FILE_OPEN_AND_ASSEMBLE = "Open and assemble";
    private static final String STRING_MENU_FILE_ADD_CODE = "Add code";
    private static final String STRING_MENU_FILE_SAVE = "Save";
    private static final String STRING_MENU_FILE_RUN_FROM_CONF = "Run from configuration file";
    private static final String STRING_MENU_FILE_EXIT = "Exit program";

    private static final KeyStroke KEY_MENU_FILE_NEW = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.Event.CTRL_MASK);
    private static final KeyStroke KEY_MENU_FILE_OPEN = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.Event.CTRL_MASK);
    private static final KeyStroke KEY_MENU_FILE_OPEN_AND_ASSEMBLE = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.Event.CTRL_MASK);
    private static final KeyStroke KEY_MENU_FILE_ADD_CODE = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.Event.CTRL_MASK);
    private static final KeyStroke KEY_MENU_FILE_SAVE = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK);
    private static final KeyStroke KEY_MENU_FILE_RUN_FROM_CONF = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.Event.ALT_MASK);
    private static final KeyStroke KEY_MENU_FILE_EXIT = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.Event.ALT_MASK);

    private static final String STRING_MENU_SIMULATOR_RUN_PROGRAM = "Run program";
    private static final String STRING_MENU_SIMULATOR_RUN_PROGRAM_SLOWLY = "Run program slowly";
    private static final String STRING_MENU_SIMULATOR_STOP_RUNNING = "Stop running";
    private static final String STRING_MENU_SIMULATOR_DO_CYCLE = "Do cycle";
    private static final String STRING_MENU_SIMULATOR_DO_X_CYCLES = "Do X cycles";
    private static final String STRING_MENU_SIMULATOR_RUN_TO = "Run to address X";
    private static final String STRING_MENU_SIMULATOR_RESTART = "Restart program";
    private static final String STRING_MENU_SIMULATOR_OPTIONS = "Options";
    public static final String STRING_MENU_SIMULATOR_FORWARDING = "Forwarding";

    private static final KeyStroke KEY_MENU_SIMULATOR_RUN_PROGRAM = KeyStroke.getKeyStroke("F5");
    private static final KeyStroke KEY_MENU_SIMULATOR_RUN_PROGRAM_SLOWLY = KeyStroke.getKeyStroke("F6");
    private static final KeyStroke KEY_MENU_SIMULATOR_STOP_RUNNING = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.Event.CTRL_MASK);
    private static final KeyStroke KEY_MENU_SIMULATOR_DO_CYCLE = KeyStroke.getKeyStroke("F7");
    private static final KeyStroke KEY_MENU_SIMULATOR_DO_X_CYCLES = KeyStroke.getKeyStroke("F8");
    private static final KeyStroke KEY_MENU_SIMULATOR_RUN_TO = KeyStroke.getKeyStroke("F9");
    private static final KeyStroke KEY_MENU_SIMULATOR_RESTART = KeyStroke.getKeyStroke("F4");
    private static final KeyStroke KEY_MENU_SIMULATOR_OPTIONS = null;
    private static final KeyStroke KEY_MENU_SIMULATOR_FORWARDING = null;

    private static final String STRING_MENU_WINDOW_SAVE = "Save current window configuration";
    private static final String STRING_MENU_WINDOW_LOAD = "Load saved window configuration";
    private static final String STRING_MENU_WINDOW_CLEAR = "Clear all preferences";
    private static final String STRING_MENU_WINDOW_DISPLAY_EDITOR = "Display Editor";
    private static final String STRING_MENU_WINDOW_DISPLAY_LOG = "Display Log";
    private static final String STRING_MENU_WINDOW_DISPLAY_CODE = "Display Code";
    private static final String STRING_MENU_WINDOW_DISPLAY_RS = "Display Register Set";
    private static final String STRING_MENU_WINDOW_DISPLAY_CC = "Display Clock Cycle Diagram";
    private static final String STRING_MENU_WINDOW_DISPLAY_STATS = "Display Statistics";
    private static final String STRING_MENU_WINDOW_DISPLAY_MEM = "Display Memory";

    private static final KeyStroke KEY_MENU_WINDOW_SAVE = null;
    private static final KeyStroke KEY_MENU_WINDOW_LOAD = null;
    private static final KeyStroke KEY_MENU_WINDOW_CLEAR = null;
    private static final KeyStroke KEY_MENU_WINDOW_DISPLAY_EDITOR = null;
    private static final KeyStroke KEY_MENU_WINDOW_DISPLAY_LOG = null;
    private static final KeyStroke KEY_MENU_WINDOW_DISPLAY_CODE = null;
    private static final KeyStroke KEY_MENU_WINDOW_DISPLAY_RS = null;
    private static final KeyStroke KEY_MENU_WINDOW_DISPLAY_CC = null;
    private static final KeyStroke KEY_MENU_WINDOW_DISPLAY_STATS = null;
    private static final KeyStroke KEY_MENU_WINDOW_DISPLAY_MEM = null;

    private static final String STRING_MENU_HELP_TOOLTIPS = "Display tooltips";
    // currently unused:
//    private static final String STRING_MENU_HELP_TUTORIAL = "Tutorial";
    private static final String STRING_MENU_HELP_ABOUT = "About";

    private static final KeyStroke KEY_MENU_HELP_TOOLTIPS = null;
    // currently unused:
//    private static final KeyStroke KEY_MENU_HELP_TUTORIAL = null;
    private static final KeyStroke KEY_MENU_HELP_ABOUT = null;

    private MainFrame mf;
    private ActionListener al = null;
    private ItemListener il = null;
    protected JMenuBar jmb = new JMenuBar();

    public MainFrameMenuBarFactory(ActionListener al, ItemListener il, MainFrame mf)
    {
        assert al != null && il != null;
        this.al = al;
        this.il = il;
        this.mf = mf;
    }

    public JMenuBar createJMenuBar(Map<String, JMenuItem> importantItems)
    {
        JMenu fileMenu = new JMenu(STRING_MENU_FILE);
        JMenu simulatorMenu = new JMenu(STRING_MENU_SIMULATOR);
        JMenu windowMenu = new JMenu(STRING_MENU_WINDOW);
        JMenu lookAndFeelMenu = new JMenu(STRING_MENU_LAF);
        JMenu helpMenu = new JMenu(STRING_MENU_HELP);

        jmb.add(fileMenu);
        jmb.add(simulatorMenu);
        jmb.add(windowMenu);
        jmb.add(helpMenu);

        //if  parameter command = null, command is not yet implemented and should be implemented soon
        addMenuItem(fileMenu, STRING_MENU_FILE_NEW, KEY_MENU_FILE_NEW, StateValidator.executingOrLazyStates, new CommandNewFile(mf));
        addMenuItem(fileMenu, STRING_MENU_FILE_OPEN, KEY_MENU_FILE_OPEN, StateValidator.executingOrLazyStates, new CommandLoadFile(mf));
        addMenuItem(fileMenu, STRING_MENU_FILE_OPEN_AND_ASSEMBLE, KEY_MENU_FILE_OPEN_AND_ASSEMBLE, StateValidator.executingOrLazyStates, new CommandLoadAndRunFile(mf));
        addMenuItem(fileMenu, STRING_MENU_FILE_ADD_CODE, KEY_MENU_FILE_ADD_CODE, StateValidator.executingOrLazyStates, new CommandLoadFileBelow(mf));
        addMenuItem(fileMenu, STRING_MENU_FILE_SAVE, KEY_MENU_FILE_SAVE, StateValidator.executingOrLazyStates, new CommandSave());
        addMenuItem(fileMenu, STRING_MENU_FILE_RUN_FROM_CONF, KEY_MENU_FILE_RUN_FROM_CONF, StateValidator.executingOrLazyStates, new CommandRunFromConfigurationFile(mf));
        addMenuItem(fileMenu, STRING_MENU_FILE_EXIT, KEY_MENU_FILE_EXIT, StateValidator.allStates, new CommandExitProgram(mf));

        addMenuItem(simulatorMenu, STRING_MENU_SIMULATOR_RUN_PROGRAM, KEY_MENU_SIMULATOR_RUN_PROGRAM, StateValidator.executingStates, new CommandRun(mf));
        addMenuItem(simulatorMenu, STRING_MENU_SIMULATOR_RUN_PROGRAM_SLOWLY, KEY_MENU_SIMULATOR_RUN_PROGRAM_SLOWLY, StateValidator.executingStates, new CommandRunSlowly(mf));
        addMenuItem(simulatorMenu, STRING_MENU_SIMULATOR_STOP_RUNNING, KEY_MENU_SIMULATOR_STOP_RUNNING, StateValidator.RunningStates, new CommandStopRunning(mf));

        simulatorMenu.addSeparator();

        addMenuItem(simulatorMenu, STRING_MENU_SIMULATOR_DO_CYCLE, KEY_MENU_SIMULATOR_DO_CYCLE, StateValidator.executingStates, new CommandDoCycle(mf));
        addMenuItem(simulatorMenu, STRING_MENU_SIMULATOR_DO_X_CYCLES, KEY_MENU_SIMULATOR_DO_X_CYCLES, StateValidator.executingStates, new CommandDoXCycles(mf));
        addMenuItem(simulatorMenu, STRING_MENU_SIMULATOR_RUN_TO, KEY_MENU_SIMULATOR_RUN_TO, StateValidator.executingStates, new CommandRunToAddressX(mf));
        addMenuItem(simulatorMenu, STRING_MENU_SIMULATOR_RESTART, KEY_MENU_SIMULATOR_RESTART, StateValidator.executingStates, new CommandResetCurrentProgram(mf));

        simulatorMenu.addSeparator();

        addMenuItem(simulatorMenu, STRING_MENU_SIMULATOR_OPTIONS, KEY_MENU_SIMULATOR_OPTIONS, StateValidator.executingOrLazyStates, new CommandShowOptionDialog());

        {
            // update the menu entry for forwarding after changing it in the options dialog
            final OpenDLXSimCheckBoxMenuItem fw_checkitem = addCheckBoxMenuItem(simulatorMenu, STRING_MENU_SIMULATOR_FORWARDING, KEY_MENU_SIMULATOR_FORWARDING, StateValidator.executingOrLazyStates);
            EventCommandLookUp.put(fw_checkitem, new CommandForwarding(fw_checkitem));
            fw_checkitem.setSelected(Preference.pref.getBoolean(Preference.forwardingPreferenceKey, true));
            importantItems.put(STRING_MENU_SIMULATOR_FORWARDING, fw_checkitem);
        }

        addMenuItem(windowMenu, STRING_MENU_WINDOW_SAVE, KEY_MENU_WINDOW_SAVE, StateValidator.executingOrLazyStates, new CommandSaveFrameConfigurationUsrLevel(mf));
        addMenuItem(windowMenu, STRING_MENU_WINDOW_LOAD, KEY_MENU_WINDOW_LOAD, StateValidator.executingOrLazyStates, new CommandLoadFrameConfigurationUsrLevel(mf));
        addMenuItem(windowMenu, STRING_MENU_WINDOW_CLEAR, KEY_MENU_WINDOW_CLEAR, StateValidator.executingOrLazyStates, new CommandClearAllPreferences());


        /*this is a submenu of windowMenu
         JMenu defaultWindowConfigurationMenu = new JMenu("Load default window configuration");
         windowMenu.add(defaultWindowConfigurationMenu);
         addMenuItem(defaultWindowConfigurationMenu, "standard", null, null);
         addMenuItem(defaultWindowConfigurationMenu, "full", null, null);
         addMenuItem(defaultWindowConfigurationMenu, "edit only", null, null);*/
        windowMenu.addSeparator();

        createWindowCheckboxes(windowMenu); //see below

        windowMenu.addSeparator();

        // add submenu for Look&Feels
        windowMenu.add(lookAndFeelMenu);

        // a group of radio buttons so only one L&F item can be selected
        ButtonGroup lookAndFeelOptionsGroup = new ButtonGroup();

        // get current L&F class name
        final String currentLaF = UIManager.getLookAndFeel().getClass().getCanonicalName();

        // add selector items for all available L&Fs
        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
        {
            OpenDLXSimRadioButtonMenuItem item = addRadioButtonMenuItem(lookAndFeelMenu, info.getName(), null, lookAndFeelOptionsGroup, StateValidator.allStates);
            if (currentLaF.equals(info.getClassName()))
                item.setSelected(true);
            EventCommandLookUp.put(item, new CommandSetLaF(info.getClassName()));
        }

        //help
        OpenDLXSimCheckBoxMenuItem checkitem = addCheckBoxMenuItem(helpMenu, STRING_MENU_HELP_TOOLTIPS, KEY_MENU_HELP_TOOLTIPS, StateValidator.executingOrLazyStates);
        EventCommandLookUp.put(checkitem, new CommandDisplayTooltips(checkitem));
        // get preference and set selected if tooltips are enabled
        checkitem.setSelected(Preference.pref.getBoolean(CommandDisplayTooltips.preferenceKey, true));
        // currently unused:
        //addMenuItem(helpMenu, STRING_MENU_HELP_TUTORIAL, KEY_MENU_HELP_TUTORIAL, StateValidator.executingOrLazyStates, new CommandTutorial());
        addMenuItem(helpMenu, STRING_MENU_HELP_ABOUT, KEY_MENU_HELP_ABOUT, StateValidator.executingOrLazyStates, new CommandShowAbout());
        return jmb;
    }

    private void createWindowCheckboxes(JMenu windowMenu)
    {
        //box name = frame title
        String name = InternalFrameFactory.getFrameName(EditorFrame.class);
        OpenDLXSimMenuItem frame_item = addMenuItem(windowMenu, STRING_MENU_WINDOW_DISPLAY_EDITOR, KEY_MENU_WINDOW_DISPLAY_EDITOR, StateValidator.allStates);
        frame_item.setName(name);
        EventCommandLookUp.put(frame_item, new CommandChangeWindowVisibility(frame_item, mf));

        name = InternalFrameFactory.getFrameName(LogFrame.class);
        frame_item = addMenuItem(windowMenu, STRING_MENU_WINDOW_DISPLAY_LOG, KEY_MENU_WINDOW_DISPLAY_LOG, StateValidator.executingOrRunningStates);
        frame_item.setName(name);
        EventCommandLookUp.put(frame_item, new CommandChangeWindowVisibility(frame_item, mf));

        name = InternalFrameFactory.getFrameName(CodeFrame.class);
        frame_item = addMenuItem(windowMenu, STRING_MENU_WINDOW_DISPLAY_CODE, KEY_MENU_WINDOW_DISPLAY_CODE, StateValidator.executingOrRunningStates);
        frame_item.setName(name);
        EventCommandLookUp.put(frame_item, new CommandChangeWindowVisibility(frame_item, mf));

        name = InternalFrameFactory.getFrameName(RegisterFrame.class);
        frame_item = addMenuItem(windowMenu, STRING_MENU_WINDOW_DISPLAY_RS, KEY_MENU_WINDOW_DISPLAY_RS, StateValidator.executingOrRunningStates);
        frame_item.setName(name);
        EventCommandLookUp.put(frame_item, new CommandChangeWindowVisibility(frame_item, mf));

        name = InternalFrameFactory.getFrameName(ClockCycleFrame.class);
        frame_item = addMenuItem(windowMenu, STRING_MENU_WINDOW_DISPLAY_CC, KEY_MENU_WINDOW_DISPLAY_CC, StateValidator.executingOrRunningStates);
        frame_item.setName(name);
        EventCommandLookUp.put(frame_item, new CommandChangeWindowVisibility(frame_item, mf));

        name = InternalFrameFactory.getFrameName(StatisticsFrame.class);
        frame_item = addMenuItem(windowMenu, STRING_MENU_WINDOW_DISPLAY_STATS, KEY_MENU_WINDOW_DISPLAY_STATS, StateValidator.executingOrRunningStates);
        frame_item.setName(name);
        EventCommandLookUp.put(frame_item, new CommandChangeWindowVisibility(frame_item, mf));

        name = InternalFrameFactory.getFrameName(MemoryFrame.class);
        frame_item = addMenuItem(windowMenu, STRING_MENU_WINDOW_DISPLAY_MEM, KEY_MENU_WINDOW_DISPLAY_MEM, StateValidator.executingOrRunningStates);
        frame_item.setName(name);
        EventCommandLookUp.put(frame_item, new CommandChangeWindowVisibility(frame_item, mf));
    }

    protected OpenDLXSimMenuItem addMenuItem(final JMenu parent, String name, KeyStroke accelerator,
            OpenDLXSimState state [])
    {
        final OpenDLXSimMenuItem item = new OpenDLXSimMenuItem(state,name);
        initializeMenuItem(item, parent, name, accelerator);
        item.addActionListener(al);
        return item;
    }

    protected OpenDLXSimMenuItem addMenuItem(final JMenu parent, String name, KeyStroke accelerator,
            OpenDLXSimState state [], Command eventCommand)
    {
        final OpenDLXSimMenuItem item = addMenuItem(parent, name, accelerator, state);
        EventCommandLookUp.put(item, eventCommand);
        return item;
    }

    protected OpenDLXSimCheckBoxMenuItem addCheckBoxMenuItem(JMenu father, String name,
            KeyStroke accelerator, OpenDLXSimState state [])
    {
        OpenDLXSimCheckBoxMenuItem jMenuItem = new OpenDLXSimCheckBoxMenuItem(state,name);
        jMenuItem.setState(false);
        initializeMenuItem(jMenuItem, father, name, accelerator);
        jMenuItem.addItemListener(il);
        return jMenuItem;

    }

    protected OpenDLXSimRadioButtonMenuItem addRadioButtonMenuItem(JMenu father, String name,
            KeyStroke accelerator, ButtonGroup group, OpenDLXSimState state [])
    {
        OpenDLXSimRadioButtonMenuItem jRadioButtonItem = new OpenDLXSimRadioButtonMenuItem(state,name);
        jRadioButtonItem.setSelected(true);
        jRadioButtonItem.addActionListener(al);
        group.add(jRadioButtonItem);
        initializeMenuItem(jRadioButtonItem, father, name, accelerator);
        return jRadioButtonItem;

    }

    protected void initializeMenuItem(JMenuItem jMenuItem, JMenu father, String name,
            KeyStroke accelerator)
    {
        jMenuItem.setAccelerator(accelerator);
        father.add(jMenuItem);
    }

}
