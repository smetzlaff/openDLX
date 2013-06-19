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
package openDLX.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import openDLX.util.TrapObserver;

@SuppressWarnings("serial")
public class Input extends JDialog implements ActionListener, TrapObserver, FocusListener, KeyListener
{

    private JButton confirm;
    private JPanel emptyPanel;
    private JTextField inputField;
    private String inputReturn;
    private boolean notYetClicked = true;
    private final String dummy_text = "Write input here";
    private static Input instance;

    private Input(JFrame f)
    {
        super(f, true);
        setLayout(new BorderLayout());
        setTitle("Input");
        inputField = new JTextField(dummy_text);
        inputField.addFocusListener(this);
        confirm = new JButton("Ok");
        confirm.addActionListener(this);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(confirm);
        emptyPanel = new JPanel();
        emptyPanel.setBackground(Color.WHITE);
        add(inputField, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(f);
        addKeyListener(this);
        inputField.addKeyListener(this);
        pack();

    }

    public static Input getInstance(JFrame f)
    {
        if (instance == null)
        {
            instance = new Input(f);
        }
        return instance;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        inputReturn = inputField.getText();
        notYetClicked = true;
        inputField.setText(dummy_text);
        setVisible(false);
    }

    @Override
    public void update()
    {
        setVisible(true);

    }

    @Override
    public void update(String s)
    {
        update();
    }

    @Override
    public void focusGained(FocusEvent e)
    {
        if (notYetClicked)
        {
            notYetClicked = false;
            inputField.setText("");
        }
    }

    @Override
    public void focusLost(FocusEvent e)
    {
        if (notYetClicked)
        {
            notYetClicked = false;
            inputField.setText("");
        }
    }

    public String getInput()
    {
        return inputReturn;
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
