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

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import openDLX.gui.GUI_CONST.OpenDLXSimState;
import openDLX.gui.MainFrame;

@SuppressWarnings("serial")
public class Player extends JDialog implements ActionListener
{
    private JButton play, pause, stop, times1, times2, times4, times8, times16;

    public Player(JFrame f)
    {
        super(f, false);
        setLayout(new FlowLayout());
        setTitle("OpenDLXSimulator run");

        play = new JButton("Run");
        play.addActionListener(this);
        add(play);
        play.setEnabled(false);
        pause = new JButton("Pause");
        pause.addActionListener(this);
        add(pause);
        stop = new JButton("Stop");
        stop.addActionListener(this);
        add(stop);
        times1 = new JButton("1x");
        times1.addActionListener(this);
        add(times1);
        times2 = new JButton("2x");
        times2.addActionListener(this);
        add(times2);
        times4 = new JButton("4x");
        times4.addActionListener(this);
        add(times4);
        times8 = new JButton("8x");
        times8.addActionListener(this);
        add(times8);
        times16 = new JButton("16x");
        times16.addActionListener(this);
        add(times16);

        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(f);

        pack();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        MainFrame mf = MainFrame.getInstance();
        if (e.getSource() == play)
        {
            pause.setEnabled(true);
            play.setEnabled(false);
            mf.setPause(false);
            mf.setRunSpeed(MainFrame.RUN_SPEED_DEFAULT);
        }
        else if (e.getSource() == pause)
        {
            play.setEnabled(true);
            mf.setPause(true);
            pause.setEnabled(false);
        }
        else if (e.getSource() == stop)
        {
            mf.setOpenDLXSimState(OpenDLXSimState.EXECUTING);
            mf.setPause(false);
            setVisible(false);
            mf.setRunSpeed(MainFrame.RUN_SPEED_DEFAULT);
            dispose();
        }
        else if (e.getSource() == times1)
        {
            mf.setRunSpeed(MainFrame.RUN_SPEED_DEFAULT);
        }
        else if (e.getSource() == times2)
        {
            mf.setRunSpeed(MainFrame.RUN_SPEED_DEFAULT / 2);
        }
        else if (e.getSource() == times4)
        {
            mf.setRunSpeed(MainFrame.RUN_SPEED_DEFAULT / 4);
        }
        else if (e.getSource() == times8)
        {
            mf.setRunSpeed(MainFrame.RUN_SPEED_DEFAULT / 8);
        }
        else if (e.getSource() == times16)
        {
            mf.setRunSpeed(MainFrame.RUN_SPEED_DEFAULT / 16);
        }
    }
}
