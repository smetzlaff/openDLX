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
package openDLX.gui.internalframes.concreteframes.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.undo.*;

import openDLX.gui.GUI_CONST.OpenDLXSimState;
import openDLX.gui.MainFrame;
import openDLX.gui.command.EventCommandLookUp;
import openDLX.gui.command.userLevel.CommandClearEditor;
import openDLX.gui.command.userLevel.CommandLoadAndRunFile;
import openDLX.gui.command.userLevel.CommandLoadFile;
import openDLX.gui.command.userLevel.CommandPerformEditorRedo;
import openDLX.gui.command.userLevel.CommandPerformEditorUndo;
import openDLX.gui.command.userLevel.CommandRunFromEditor;
import openDLX.gui.command.userLevel.CommandSave;
import openDLX.gui.internalframes.FrameConfiguration;
import openDLX.gui.internalframes.OpenDLXSimInternalFrame;
import openDLX.gui.internalframes.factories.InternalFrameFactory;

@SuppressWarnings("serial")
public final class EditorFrame extends OpenDLXSimInternalFrame implements ActionListener, KeyListener, UndoableEditListener
{
    //the editor frame is a singleton
    
    private MainFrame mf;
    
    //default size values

    private final int size_x = 250;
    private final int size_y = 300;
    //text area
    //private JTextArea input;
    //buttons
    private JButton run;
    private JButton load;
    private JButton loadandrun;
    private JButton save;
    private JButton clear;
    
    /* TODO
     * For now the undo/redo functionality is limited to 
     * 	- character based actions
     */
    private JButton undo;
    private JButton redo;
    
    private static EditorFrame instance = null;
    private JTextArea jta;

    private UndoManager undoMgr;
    
    private CommandPerformEditorUndo undoCommand;
    private CommandPerformEditorRedo redoCommand;

    
    private int saved_state_hash;
    private String editor_frame_title;

    private EditorFrame(String title, MainFrame mf)
    {
        super(title, true);
        editor_frame_title = title;
        this.mf = mf;
        initialize();
    }

    public static EditorFrame getInstance(MainFrame mf)
    {
        if (instance == null)
        {
            instance = new EditorFrame(InternalFrameFactory.getFrameName(EditorFrame.class), mf);
            FrameConfiguration fc = new FrameConfiguration(instance);
            fc.loadFrameConfiguration();
        }
        return instance;
    }

    @Override
    protected void initialize()
    {
        super.initialize();
        setLayout(new BorderLayout());
        // input = new JTextArea();
        //input.setSize(size_x, size_y);
        //JScrollPane scroll = new JScrollPane(input);

       new JTextPane();
//       JTextPane jtp = new JTextPane();
        /*
         *  
         final JScrollPane jsp = new JScrollPane();
         jtp.setEditorKitForContentType("text/openDLX", new OpenDLXSimEditorKit());
         jtp.setContentType("text/openDLX");
         jtp.getDocument().addDocumentListener(new DocumentListener()
         {
         public String getText()
         {
         int caretPosition = jtp.getDocument().getLength();
         Element root = jtp.getDocument().getDefaultRootElement();
         String text = "1" + System.getProperty("line.separator");
         for (int i = 2; i < root.getElementIndex(caretPosition) + 2; i++)
         {
         text += i + System.getProperty("line.separator");
         }
         return text;
         }

         @Override
         public void changedUpdate(DocumentEvent de)
         {
         lines.setText(getText());
         jsp.repaint();
         System.out.println(getParent());

         }

         @Override
         public void insertUpdate(DocumentEvent de)
         {
         lines.setText(getText());
         jsp.repaint();
         System.out.println(getParent());

         }

         @Override
         public void removeUpdate(DocumentEvent de)
         {
         lines.setText(getText());
         jsp.repaint();
         System.out.println(getParent());

         }

         });
         */


        jta = new JTextArea();
        setSavedState();
        JScrollPane scrollPane = new JScrollPane(jta);
        TextNumberingPanel tln = new TextNumberingPanel(jta);
        jta.addKeyListener(this);
        jta.getDocument().addUndoableEditListener(this);
        
        scrollPane.setRowHeaderView(tln);
        add(scrollPane, BorderLayout.CENTER);
        run = createButton("Assemble", "Assemble and Run [ALT+A]", KeyEvent.VK_A, "/img/icons/tango/run.png");
        load = createButton("Load", "Load Program [CRTL+O]", KeyEvent.VK_O, "/img/icons/tango/load.png");
        loadandrun = createButton("Load and Run", "Load Program and Run [CRTL+R]", KeyEvent.VK_O, "/img/icons/tango/loadandrun.png");
        save = createButton("Save As...", "Save Program As... [ALT+S]", KeyEvent.VK_S, "/img/icons/tango/saveas.png");
        clear = createButton("Clear", "Clear All [ALT+C]", KeyEvent.VK_C, "/img/icons/tango/clear.png");
        undo = createButton("Undo", "Undo [CTRL+Z]", KeyEvent.VK_U, "/img/icons/tango/undo.png");
        redo = createButton("Redo", "Redo [CTRL+SHIFT+Z]", KeyEvent.VK_R, "/img/icons/tango/redo.png"); 
        
        // if  parameter command = null, command is not yet implemented and should be implemented soon   

        EventCommandLookUp.put(run, new CommandRunFromEditor(this));
        EventCommandLookUp.put(load, new CommandLoadFile(mf));
        EventCommandLookUp.put(loadandrun, new CommandLoadAndRunFile(mf));
        EventCommandLookUp.put(save, new CommandSave());
        EventCommandLookUp.put(clear, new CommandClearEditor());
//        EventCommandLookUp.put(undo, undoCommand); 
//        EventCommandLookUp.put(redo, redoCommand);

        
        run.addActionListener(this);
        load.addActionListener(this);
        loadandrun.addActionListener(this);
        save.addActionListener(this);
        clear.addActionListener(this);
        undo.addActionListener(this);
        redo.addActionListener(this);

        
        // TODO deactivate toolbar when simulator is in run mode
        JToolBar toolBar = new JToolBar("Editor toolbar");
        toolBar.add(run);
        toolBar.add(load);
        toolBar.add(loadandrun);
        toolBar.add(save);
        toolBar.add(clear);
        toolBar.add(undo);
        toolBar.add(redo);
        toolBar.setFloatable(false);
        toolBar.setRollover(false);
        toolBar.setFocusable(false);
        add(toolBar, BorderLayout.PAGE_START);
        
        
        setPreferredSize(new Dimension(size_x, size_y));
        pack();
        setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e)
    {
        clean();
        EventCommandLookUp.get(e.getSource()).execute();
    }

    @Override
	public void undoableEditHappened(UndoableEditEvent e) {
		undoMgr.addEdit(e.getEdit());
	}

    @Override
    public void update()
    {
    }

    public String getText()
    {
        return jta.getText();
    }

    public void setText(String text)
    {
        jta.setText(text);
    }

    public void insertText(String text)
    {
        String tmp = jta.getText();
        tmp += text;
        jta.setText(tmp);
    }

    public void colorLine(int l)
    {
        Highlighter.HighlightPainter redPainter =
                new DefaultHighlighter.DefaultHighlightPainter(Color.red);
        try
        {
            System.out.println(l);
            int startIndex = jta.getLineStartOffset(l);
            int endIndex = jta.getLineEndOffset(l);
            jta.getHighlighter().addHighlight(startIndex, endIndex, redPainter);
        }
        catch (BadLocationException ble)
        {
            System.err.println("Failed coloring editor line");
        }

    }

    @Override
    public void clean()
    {
        jta.getHighlighter().removeAllHighlights();
    }

    public void validateButtons(OpenDLXSimState currentState)
    {
        if (currentState == OpenDLXSimState.RUNNING)
        {
            run.setEnabled(false);
            clear.setEnabled(false);
            save.setEnabled(false);

        }
        else
        {
            run.setEnabled(true);
            clear.setEnabled(true);
            save.setEnabled(true);
        }
    }
    
    public void setSavedState()
    {
        saved_state_hash = getTextHash();
        updateTitle();
    }
    
    private int getTextHash()
    {
        // TODO hashCode() might not be the the most suitable function to safe the editor state
        return getText().hashCode();
    }

    public boolean isTextSaved()
    {
        return (saved_state_hash == getTextHash());
    }


    @Override
    public void keyReleased(KeyEvent arg0)
    {
        updateTitle();
    }

    @Override
    public void keyTyped(KeyEvent arg0)
    {
        // Unused
    }
    
    @Override
    public void keyPressed(KeyEvent arg0)
    {
        // Unused
    }
    
    private void updateTitle()
    {
        if(!isTextSaved())
        {
            setTitle("*"+editor_frame_title);
        }
        else
        {
            setTitle(editor_frame_title);
        }
    }

    public void setUndoManager(UndoManager UndoMgr) 
    {
        undoMgr = UndoMgr;
        undoCommand = new CommandPerformEditorUndo(undoMgr);
        redoCommand = new CommandPerformEditorRedo(undoMgr);
        
        EventCommandLookUp.put(undo, undoCommand); 
        EventCommandLookUp.put(redo, redoCommand);

    }

    private JButton createButton(String name, String tooltip, int mnemonic, String icon_path) 
    {
        JButton button = new JButton();
        URL icon_url;
        if((icon_path != null) && ((icon_url = getClass().getResource(icon_path)) != null))
        {
            button.setIcon(new ImageIcon(icon_url));
        }
        else
        {
            button.setText(name);
        }
        button.setMnemonic(mnemonic); 
        button.setToolTipText(tooltip);
        button.setFocusable(false);
        return button;
    }
}
