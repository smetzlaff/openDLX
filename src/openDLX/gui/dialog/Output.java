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
package openDLX.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import openDLX.util.TrapObserver;

@SuppressWarnings("serial")
public class Output extends JDialog implements ActionListener, TrapObserver, KeyListener
{

    private JTextArea textArea;
    private JButton confirm;
    private JPanel emptyPanel;
    private static Output instance;

    private Output(JFrame f)
    {
        super(f, true);
        setLayout(new BorderLayout());
        setTitle("Output");
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane textScroller = new JScrollPane(textArea);
        textScroller.setBackground(Color.WHITE);
        confirm = new JButton("Ok");
        confirm.addActionListener(this);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(confirm);
        emptyPanel = new JPanel();
        emptyPanel.setBackground(Color.WHITE);
        add(textScroller, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(f);
        addKeyListener(this);
        pack();
        setMinimumSize(new Dimension(250, 250));

    }

    public static Output getInstance(JFrame f)
    {
        if (instance == null)
        {
            instance = new Output(f);
        }
        return instance;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        setVisible(false);
    }

    @Override
    public void update(String arg)
    {
        if (arg != null)
        {
            addText(arg);
        }
        //pack(); // enables constantly growing output frame 
        setVisible(true);
        textArea.setCaretPosition(textArea.getText().length());

    }

    @Override
    public void update()
    {

        setVisible(true);


    }

    private void addText(String textToInsert)
    {
        textArea.setText(textArea.getText() + textToInsert);
    }
    
    public void clear()
    {	
    	textArea.setText("");
    }

    public String getText()
    {
        return textArea.getText();
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
    }

    @Override
    public void keyPressed(KeyEvent e)
    {

        if (e.getKeyCode() == KeyEvent.VK_ENTER)
        {
            confirm.doClick();
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
    }

}