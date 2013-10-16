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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.undo.UndoManager;

import openDLX.OpenDLXSimulator;
import openDLX.config.GlobalConfig;
import openDLX.gui.GUI_CONST.OpenDLXSimState;
import openDLX.gui.command.EventCommandLookUp;
import openDLX.gui.command.userLevel.CommandExitProgram;
import openDLX.gui.dialog.Input;
import openDLX.gui.dialog.Output;
import openDLX.gui.internalframes.OpenDLXSimInternalFrame;
import openDLX.gui.internalframes.concreteframes.editor.EditorFrame;
import openDLX.gui.menubar.MainFrameMenuBarFactory;
import openDLX.gui.menubar.StateValidator;
import openDLX.gui.util.PipelineExceptionHandler;
import openDLX.util.DLXTrapHandler;
import openDLX.util.TrapObservableDefault;

@SuppressWarnings("serial")
public class MainFrame extends JFrame implements ActionListener, ItemListener
{
    public static final int RUN_SPEED_DEFAULT = 16;

    // MainFrame is a Singleton.
    // hence it has a private constructor
    private static final MainFrame mf = new MainFrame();

    public Output output;
    public Input input;

    private OpenDLXSimulator openDLXSim = null;
    private UndoManager undoMgr;
    private EditorFrame editor;
    private JDesktopPane desktop;
    private boolean updateAllowed = true;
    private int runSpeed = RUN_SPEED_DEFAULT;
    private boolean pause = false;
    private OpenDLXSimState state = OpenDLXSimState.IDLE;
    private File configFile;
    private JMenuBar menuBar;
    private JMenuItem forwardingMenuItem;
    private PipelineExceptionHandler pexHandler = null;
    private String loadedCodeFilePath="code.s";//default

    private MainFrame()
    {
        initialize();
        final ImageIcon icon = new ImageIcon(getClass().getResource("/img/openDLX-quadrat128x128.png"), "openDLX icon");
        setIconImage(icon.getImage());

        setTitle("openDLX " + GlobalConfig.VERSION);

        // Register output for pipeline
        TrapObservableDefault observableOutput = new TrapObservableDefault();
        observableOutput.addObserver(output);
        DLXTrapHandler.getInstance().setTrapObserverOutput(observableOutput);

        // Register input for pipeline
        TrapObservableDefault observableInput = new TrapObservableDefault();
        observableInput.addObserver(input);
        DLXTrapHandler.getInstance().setTrapObserverInput(observableInput);
        DLXTrapHandler.getInstance().setInput(input);
    }

    //thus it has a static access method
    public static MainFrame getInstance()
    {
        return mf;
    }

    //main frame delegates all incoming events caused by its submembers to command classes
    @Override
    public void actionPerformed(ActionEvent e)
    {
        EventCommandLookUp.get(e.getSource()).execute();
    }

    @Override
    public void itemStateChanged(ItemEvent e)
    {
        EventCommandLookUp.get(e.getSource()).execute();
    }

    private void initialize()
    {
        undoMgr = new UndoManager();
        
        //uses a factory to outsource creation of the menuBar
        MainFrameMenuBarFactory menuBarFactory = new MainFrameMenuBarFactory(this, this, this);
        Hashtable<String, JMenuItem> importantItems = new Hashtable<>();
        menuBar = menuBarFactory.createJMenuBar(importantItems);
        setJMenuBar(menuBar);
        forwardingMenuItem = importantItems.get(MainFrameMenuBarFactory.STRING_MENU_SIMULATOR_FORWARDING);

        setMinimumSize(new Dimension(200, 200));
        desktop = new JDesktopPane();
        desktop.setBackground(Color.WHITE);
        setContentPane(desktop);

        editor = EditorFrame.getInstance(this);
        editor.setUndoManager(undoMgr);
        desktop.add(editor);

        output = Output.getInstance(mf);
        input = Input.getInstance(mf);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(200, 200));
        setExtendedState(MAXIMIZED_BOTH);
        setVisible(true);

        /// select editor frame
        try
        {
            editor.setSelected(true);
        } catch (PropertyVetoException e1)
        {
            e1.printStackTrace();
        }

        setOpenDLXSimState(OpenDLXSimState.IDLE);
        pexHandler = new PipelineExceptionHandler(this);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener( new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                MainFrame frame = (MainFrame)e.getSource();
                CommandExitProgram exit = new CommandExitProgram(frame);
                if (exit.close())
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });

    }

    //INTERFACE
    public OpenDLXSimulator getOpenDLXSim()
    {
        return openDLXSim;
    }

    public JInternalFrame[] getinternalFrames()
    {
        return desktop.getAllFrames();
    }

    public void setOpenDLXSim(OpenDLXSimulator openDLXSim)
    {
        this.openDLXSim = openDLXSim;
        pexHandler.setSimulator(openDLXSim);
    }

    public void setOpenDLXSimState(OpenDLXSimState s)
    {
        this.state = s;
        StateValidator.validateMenu(menuBar, s);
        editor.validateButtons(getOpenDLXSimState());
    }

    public OpenDLXSimState getOpenDLXSimState()
    {
        return this.state;
    }

    public boolean isRunning()
    {
        return (state == OpenDLXSimState.RUNNING);
    }

    public boolean isExecuting()
    {
        return (state == OpenDLXSimState.EXECUTING);
    }

    public boolean isLazy()
    {
        return (state == OpenDLXSimState.IDLE);
    }

    public boolean isUpdateAllowed()
    {
        return updateAllowed;
    }

    public void setUpdateAllowed(boolean updateAllowed)
    {
        this.updateAllowed = updateAllowed;
    }

    public void addInternalFrame(OpenDLXSimInternalFrame mif)
    {
        desktop.add(mif);
    }

    public String getEditorText()
    {
        return editor.getText();
    }

    public void setEditorText(String text)
    {
        editor.setText(text);

    }

    public void colorEditorLine(int l)
    {
        editor.colorLine(l);
    }

    public void insertEditorText(String text)
    {
        editor.insertText(text);

    }

    public void setEditorSavedState()
    {
        editor.setSavedState();
    }

    public boolean isEditorTextSaved()
    {
        return editor.isTextSaved();
    }

    public void setRunSpeed(int speed)
    {
        this.runSpeed = speed;
    }

    public int getRunSpeed()
    {
        return runSpeed;
    }

    public File getConfigFile()
    {
        return configFile;
    }

    public void setConfigFile(File configFile)
    {
        this.configFile = configFile;
    }

    public boolean isPause()
    {
        return pause;
    }

    public void setPause(boolean pause)
    {
        this.pause = pause;
    }

    public PipelineExceptionHandler getPipelineExceptionHandler()
    {
        return pexHandler;
    }

    public String getLoadedCodeFilePath()
    {
        return loadedCodeFilePath;
    }

    public void setLoadedCodeFilePath(String loadedCodeFilePath)
    {
        this.loadedCodeFilePath = loadedCodeFilePath;
    }

    public JMenuItem getForwardingMenuItem()
    {
        return forwardingMenuItem;
    }

    public UndoManager getEditorUndoManager() {
        return undoMgr;
    }

}
