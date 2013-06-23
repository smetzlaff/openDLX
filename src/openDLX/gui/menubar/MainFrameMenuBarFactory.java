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
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import openDLX.gui.MainFrame;
import openDLX.gui.Preference;
import openDLX.gui.LookAndFeel.LookAndFeelStrategyJava;
import openDLX.gui.LookAndFeel.LookAndFeelStrategySystemMonoSpaced;
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
import openDLX.gui.command.userLevel.CommandResetCurrentProgram;
import openDLX.gui.command.userLevel.CommandRun;
import openDLX.gui.command.userLevel.CommandRunFromConfigurationFile;
import openDLX.gui.command.userLevel.CommandRunSlowly;
import openDLX.gui.command.userLevel.CommandRunToAddressX;
import openDLX.gui.command.userLevel.CommandSave;
import openDLX.gui.command.userLevel.CommandSaveFrameConfigurationUsrLevel;
import openDLX.gui.command.userLevel.CommandSetLaFJava;
import openDLX.gui.command.userLevel.CommandSetLaFSystem;
import openDLX.gui.command.userLevel.CommandShowAbout;
import openDLX.gui.command.userLevel.CommandShowOptionDialog;
import openDLX.gui.command.userLevel.CommandStopRunning;
import openDLX.gui.command.userLevel.CommandTutorial;
import openDLX.gui.internalframes.concreteframes.ClockCycleFrame;
import openDLX.gui.internalframes.concreteframes.CodeFrame;
import openDLX.gui.internalframes.concreteframes.LogFrame;
import openDLX.gui.internalframes.concreteframes.MemoryFrame;
import openDLX.gui.internalframes.concreteframes.RegisterFrame;
import openDLX.gui.internalframes.concreteframes.StatisticsFrame;
import openDLX.gui.internalframes.concreteframes.editor.EditorFrame;
import openDLX.gui.internalframes.factories.InternalFrameFactory;

public class MainFrameMenuBarFactory extends JMenuBarFactory
{
    private static final String STRING_MENU_FILE = "File";
    public static final String STRING_MENU_SIMULATOR = "Simulator";
    private static final String STRING_MENU_WINDOW = "Window";
    private static final String STRING_MENU_HELP = "Help";

    private static final String STRING_MENU_FILE_OPEN = "Open";
    private static final String STRING_MENU_FILE_OPEN_AND_ASSEMBLE = "Open and assemble";
    private static final String STRING_MENU_FILE_ADD_CODE = "Add code";
    private static final String STRING_MENU_FILE_SAVE = "Save";
    private static final String STRING_MENU_FILE_RUN_FROM_CONF = "Run from configuration file";
    private static final String STRING_MENU_FILE_EXIT = "Exit program";

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
    private static final String STRING_MENU_WINDOW_LF_SYSTEM = "System Look and Feel enabled (" + System.getProperty("os.name") + ")";
    private static final String STRING_MENU_WINDOW_LF_JAVA = "Java Look and Feel enabled";
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
    private static final KeyStroke KEY_MENU_WINDOW_LF_SYSTEM = null;
    private static final KeyStroke KEY_MENU_WINDOW_LF_JAVA = null;
    private static final KeyStroke KEY_MENU_WINDOW_DISPLAY_EDITOR = null;
    private static final KeyStroke KEY_MENU_WINDOW_DISPLAY_LOG = null;
    private static final KeyStroke KEY_MENU_WINDOW_DISPLAY_CODE = null;
    private static final KeyStroke KEY_MENU_WINDOW_DISPLAY_RS = null;
    private static final KeyStroke KEY_MENU_WINDOW_DISPLAY_CC = null;
    private static final KeyStroke KEY_MENU_WINDOW_DISPLAY_STATS = null;
    private static final KeyStroke KEY_MENU_WINDOW_DISPLAY_MEM = null;

    private static final String STRING_MENU_HELP_TOOLTIPS = "Display tooltips";
    private static final String STRING_MENU_HELP_TUTORIAL = "Tutorial";
    private static final String STRING_MENU_HELP_ABOUT = "About";

    private static final KeyStroke KEY_MENU_HELP_TOOLTIPS = null;
    private static final KeyStroke KEY_MENU_HELP_TUTORIAL = null;
    private static final KeyStroke KEY_MENU_HELP_ABOUT = null;

    private static Map<String,Integer> MENU_IDS;
    private static Map<String,Integer> MENU_ITEM_IDS;

    MainFrame mf;

    public MainFrameMenuBarFactory(ActionListener al, ItemListener il, MainFrame mf)
    {
        super(al, il);
        this.mf = mf;

        // FIXME: very dirty implementation
        MENU_IDS = new HashMap<String,Integer>();
        MENU_ITEM_IDS = new HashMap<String,Integer>();
    }

    @Override
    public JMenuBar createJMenuBar()
    {
        JMenu fileMenu = new JMenu(STRING_MENU_FILE);
        JMenu simulatorMenu = new JMenu(STRING_MENU_SIMULATOR);
        JMenu windowMenu = new JMenu(STRING_MENU_WINDOW);
        JMenu helpMenu = new JMenu(STRING_MENU_HELP);

        int menu_id = 0;
        jmb.add(fileMenu);
        MENU_IDS.put(STRING_MENU_FILE, menu_id);

        menu_id++;
        jmb.add(simulatorMenu);
        MENU_IDS.put(STRING_MENU_SIMULATOR, menu_id);

        menu_id++;
        jmb.add(windowMenu);
        MENU_IDS.put(STRING_MENU_WINDOW, menu_id);

        menu_id++;
        jmb.add(helpMenu);
        MENU_IDS.put(STRING_MENU_HELP, menu_id);

        //if  parameter command = null, command is not yet implemented and should be implemented soon
        EventCommandLookUp.put(addMenuItem(fileMenu, STRING_MENU_FILE_OPEN, KEY_MENU_FILE_OPEN, StateValidator.executingOrLazyStates).hashCode(), new CommandLoadFile(mf));
        EventCommandLookUp.put(addMenuItem(fileMenu, STRING_MENU_FILE_OPEN_AND_ASSEMBLE, KEY_MENU_FILE_OPEN_AND_ASSEMBLE, StateValidator.executingOrLazyStates).hashCode(), new CommandLoadAndRunFile(mf));
        EventCommandLookUp.put(addMenuItem(fileMenu, STRING_MENU_FILE_ADD_CODE, KEY_MENU_FILE_ADD_CODE, StateValidator.executingOrLazyStates).hashCode(), new CommandLoadFileBelow(mf));
        EventCommandLookUp.put(addMenuItem(fileMenu, STRING_MENU_FILE_SAVE, KEY_MENU_FILE_SAVE, StateValidator.executingOrLazyStates).hashCode(), new CommandSave());
        EventCommandLookUp.put(addMenuItem(fileMenu, STRING_MENU_FILE_RUN_FROM_CONF, KEY_MENU_FILE_RUN_FROM_CONF, StateValidator.executingOrLazyStates).hashCode(), new CommandRunFromConfigurationFile(mf));
        EventCommandLookUp.put(addMenuItem(fileMenu, STRING_MENU_FILE_EXIT, KEY_MENU_FILE_EXIT, StateValidator.allStates).hashCode(), new CommandExitProgram(mf));

        int item_id = 0;

        EventCommandLookUp.put(addMenuItem(simulatorMenu, STRING_MENU_SIMULATOR_RUN_PROGRAM, KEY_MENU_SIMULATOR_RUN_PROGRAM, StateValidator.executingStates).hashCode(), new CommandRun(mf));
        MENU_ITEM_IDS.put(STRING_MENU_SIMULATOR_RUN_PROGRAM, item_id);

        item_id++;
        EventCommandLookUp.put(addMenuItem(simulatorMenu, STRING_MENU_SIMULATOR_RUN_PROGRAM_SLOWLY, KEY_MENU_SIMULATOR_RUN_PROGRAM_SLOWLY, StateValidator.executingStates).hashCode(), new CommandRunSlowly(mf));
        MENU_ITEM_IDS.put(STRING_MENU_SIMULATOR_RUN_PROGRAM_SLOWLY, item_id);

        item_id++;
        EventCommandLookUp.put(addMenuItem(simulatorMenu, STRING_MENU_SIMULATOR_STOP_RUNNING, KEY_MENU_SIMULATOR_STOP_RUNNING, StateValidator.RunningStates).hashCode(), new CommandStopRunning(mf));
        MENU_ITEM_IDS.put(STRING_MENU_SIMULATOR_STOP_RUNNING, item_id);

        item_id++;
        simulatorMenu.addSeparator();

        item_id++;
        EventCommandLookUp.put(addMenuItem(simulatorMenu, STRING_MENU_SIMULATOR_DO_CYCLE, KEY_MENU_SIMULATOR_DO_CYCLE, StateValidator.executingStates).hashCode(), new CommandDoCycle(mf));
        MENU_ITEM_IDS.put(STRING_MENU_SIMULATOR_DO_CYCLE, item_id);

        item_id++;
        EventCommandLookUp.put(addMenuItem(simulatorMenu, STRING_MENU_SIMULATOR_DO_X_CYCLES, KEY_MENU_SIMULATOR_DO_X_CYCLES, StateValidator.executingStates).hashCode(), new CommandDoXCycles(mf));
        MENU_ITEM_IDS.put(STRING_MENU_SIMULATOR_DO_X_CYCLES, item_id);

        item_id++;
        EventCommandLookUp.put(addMenuItem(simulatorMenu, STRING_MENU_SIMULATOR_RUN_TO, KEY_MENU_SIMULATOR_RUN_TO, StateValidator.executingStates).hashCode(), new CommandRunToAddressX(mf));
        MENU_ITEM_IDS.put(STRING_MENU_SIMULATOR_RUN_TO, item_id);

        item_id++;
        EventCommandLookUp.put(addMenuItem(simulatorMenu, STRING_MENU_SIMULATOR_RESTART, KEY_MENU_SIMULATOR_RESTART, StateValidator.executingStates).hashCode(), new CommandResetCurrentProgram(mf));
        MENU_ITEM_IDS.put(STRING_MENU_SIMULATOR_RESTART, item_id);

        item_id++;
        simulatorMenu.addSeparator();

        item_id++;
        EventCommandLookUp.put(addMenuItem(simulatorMenu, STRING_MENU_SIMULATOR_OPTIONS, KEY_MENU_SIMULATOR_OPTIONS, StateValidator.executingOrLazyStates).hashCode(), new CommandShowOptionDialog());
        MENU_ITEM_IDS.put(STRING_MENU_SIMULATOR_OPTIONS, item_id);

        item_id++;
        {
            // TODO: update the menu entry for forwarding after changing it in the options dialog
            OpenDLXSimCheckBoxMenuItem fw_checkitem = addCheckBoxMenuItem(simulatorMenu, STRING_MENU_SIMULATOR_FORWARDING, KEY_MENU_SIMULATOR_FORWARDING, StateValidator.executingOrLazyStates);
            EventCommandLookUp.put(fw_checkitem.hashCode(), new CommandForwarding(fw_checkitem));
            fw_checkitem.setSelected(Preference.pref.getBoolean(Preference.forwardingPreferenceKey, true));
        }
        MENU_ITEM_IDS.put(STRING_MENU_SIMULATOR_FORWARDING, item_id);

        EventCommandLookUp.put(addMenuItem(windowMenu, STRING_MENU_WINDOW_SAVE, KEY_MENU_WINDOW_SAVE, StateValidator.executingOrLazyStates).hashCode(), new CommandSaveFrameConfigurationUsrLevel(mf));
        EventCommandLookUp.put(addMenuItem(windowMenu, STRING_MENU_WINDOW_LOAD, KEY_MENU_WINDOW_LOAD, StateValidator.executingOrLazyStates).hashCode(), new CommandLoadFrameConfigurationUsrLevel(mf));
        EventCommandLookUp.put(addMenuItem(windowMenu, STRING_MENU_WINDOW_CLEAR, KEY_MENU_WINDOW_CLEAR, StateValidator.executingOrLazyStates).hashCode(), new CommandClearAllPreferences());


        /*this is a submenu of windowMenu
         JMenu defaultWindowConfigurationMenu = new JMenu("Load default window configuration");
         windowMenu.add(defaultWindowConfigurationMenu);
         EventCommandLookUp.put(addMenuItem(defaultWindowConfigurationMenu, "standard", null).hashCode(), null);
         EventCommandLookUp.put(addMenuItem(defaultWindowConfigurationMenu, "full", null).hashCode(), null);
         EventCommandLookUp.put(addMenuItem(defaultWindowConfigurationMenu, "edit only", null).hashCode(), null);*/
        windowMenu.addSeparator();

        createWindowCheckboxes(windowMenu); //see below

        windowMenu.addSeparator();
        // a group of radio buttons
        ButtonGroup lookAndFeelOptionsGroup = new ButtonGroup();
        //add here new LookAndFeel options
        JRadioButtonMenuItem item = addRadioButtonMenuItem(windowMenu, STRING_MENU_WINDOW_LF_SYSTEM, KEY_MENU_WINDOW_LF_SYSTEM, lookAndFeelOptionsGroup, StateValidator.allStates);
        item.setName(LookAndFeelStrategySystemMonoSpaced.getLookAndFeelName());
        String laf = UIManager.getLookAndFeel().getClass().toString();

        /*quite dirty code follows here now, breaking every known code convention :-) - luckily its just a small piece of
         * code in a huge program so we can afford to leave it like it its- so better dont try to change it*/
        if (laf.substring(laf.length() - 17).equals(item.getName().substring(item.getName().length() - 17)))
        {
            item.setSelected(true);
        }
        EventCommandLookUp.put(item.hashCode(), new CommandSetLaFSystem(item));

        item = addRadioButtonMenuItem(windowMenu, STRING_MENU_WINDOW_LF_JAVA, KEY_MENU_WINDOW_LF_JAVA, lookAndFeelOptionsGroup, StateValidator.allStates);
        item.setName(LookAndFeelStrategyJava.getLookAndFeelName());

        if (laf.substring(laf.length() - 17).equals(item.getName().substring(item.getName().length() - 17)))
        {
            item.setSelected(true);
        }
        EventCommandLookUp.put(item.hashCode(), new CommandSetLaFJava(item));

        /*end of dirty code*/
        //help
        OpenDLXSimCheckBoxMenuItem checkitem = addCheckBoxMenuItem(helpMenu, STRING_MENU_HELP_TOOLTIPS, KEY_MENU_HELP_TOOLTIPS, StateValidator.executingOrLazyStates);
        EventCommandLookUp.put(checkitem.hashCode(), new CommandDisplayTooltips(checkitem));
        // get preference and set selected if tooltips are enabled
        checkitem.setSelected(Preference.pref.getBoolean(CommandDisplayTooltips.preferenceKey, true));
        EventCommandLookUp.put(addMenuItem(helpMenu, STRING_MENU_HELP_TUTORIAL, KEY_MENU_HELP_TUTORIAL, StateValidator.executingOrLazyStates).hashCode(), new CommandTutorial());
        EventCommandLookUp.put(addMenuItem(helpMenu, STRING_MENU_HELP_ABOUT, KEY_MENU_HELP_ABOUT, StateValidator.executingOrLazyStates).hashCode(), new CommandShowAbout());
        return jmb;
    }

    private void createWindowCheckboxes(JMenu windowMenu)
    {
        //box name = frame title
        String name = InternalFrameFactory.getFrameName(EditorFrame.class);
        OpenDLXSimMenuItem frame_item = addMenuItem(windowMenu, STRING_MENU_WINDOW_DISPLAY_EDITOR, KEY_MENU_WINDOW_DISPLAY_EDITOR, StateValidator.allStates);
        frame_item.setName(name);
        EventCommandLookUp.put(frame_item.hashCode(), new CommandChangeWindowVisibility(frame_item, mf));

        name = InternalFrameFactory.getFrameName(LogFrame.class);
        frame_item = addMenuItem(windowMenu, STRING_MENU_WINDOW_DISPLAY_LOG, KEY_MENU_WINDOW_DISPLAY_LOG, StateValidator.executingOrRunningStates);
        frame_item.setName(name);
        EventCommandLookUp.put(frame_item.hashCode(), new CommandChangeWindowVisibility(frame_item, mf));

        name = InternalFrameFactory.getFrameName(CodeFrame.class);
        frame_item = addMenuItem(windowMenu, STRING_MENU_WINDOW_DISPLAY_CODE, KEY_MENU_WINDOW_DISPLAY_CODE, StateValidator.executingOrRunningStates);
        frame_item.setName(name);
        EventCommandLookUp.put(frame_item.hashCode(), new CommandChangeWindowVisibility(frame_item, mf));

        name = InternalFrameFactory.getFrameName(RegisterFrame.class);
        frame_item = addMenuItem(windowMenu, STRING_MENU_WINDOW_DISPLAY_RS, KEY_MENU_WINDOW_DISPLAY_RS, StateValidator.executingOrRunningStates);
        frame_item.setName(name);
        EventCommandLookUp.put(frame_item.hashCode(), new CommandChangeWindowVisibility(frame_item, mf));

        name = InternalFrameFactory.getFrameName(ClockCycleFrame.class);
        frame_item = addMenuItem(windowMenu, STRING_MENU_WINDOW_DISPLAY_CC, KEY_MENU_WINDOW_DISPLAY_CC, StateValidator.executingOrRunningStates);
        frame_item.setName(name);
        EventCommandLookUp.put(frame_item.hashCode(), new CommandChangeWindowVisibility(frame_item, mf));

        name = InternalFrameFactory.getFrameName(StatisticsFrame.class);
        frame_item = addMenuItem(windowMenu, STRING_MENU_WINDOW_DISPLAY_STATS, KEY_MENU_WINDOW_DISPLAY_STATS, StateValidator.executingOrRunningStates);
        frame_item.setName(name);
        EventCommandLookUp.put(frame_item.hashCode(), new CommandChangeWindowVisibility(frame_item, mf));

        name = InternalFrameFactory.getFrameName(MemoryFrame.class);
        frame_item = addMenuItem(windowMenu, STRING_MENU_WINDOW_DISPLAY_MEM, KEY_MENU_WINDOW_DISPLAY_MEM, StateValidator.executingOrRunningStates);
        frame_item.setName(name);
        EventCommandLookUp.put(frame_item.hashCode(), new CommandChangeWindowVisibility(frame_item, mf));
    }

    public static Map<String, Integer> getMenuIDs()
    {
        return MENU_IDS;
    }

    public static Map<String, Integer> getMenuItemIDs()
    {
        return MENU_ITEM_IDS;
    }

}
