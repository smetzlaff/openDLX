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
package openDLX.gui.internalframes.concreteframes;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableModel;

import openDLX.asm.Labels;
import openDLX.datatypes.uint32;
import openDLX.exception.MemoryException;
import openDLX.gui.MainFrame;
import openDLX.gui.Preference;
import openDLX.gui.command.systemLevel.CommandLoadFrameConfigurationSysLevel;
import openDLX.gui.internalframes.OpenDLXSimInternalFrame;
import openDLX.gui.internalframes.factories.InternalFrameFactory;
import openDLX.gui.internalframes.factories.tableFactories.MemoryTableFactory;
import openDLX.gui.internalframes.util.TableSizeCalculator;
import openDLX.gui.internalframes.util.ValueInput;

@SuppressWarnings("serial")
public final class MemoryFrame extends OpenDLXSimInternalFrame implements ActionListener, KeyListener, FocusListener
{

    //tables, scrollpane and table models
    private JTable memoryTable;
    private JButton reload;
    private static int rows = 100, startAddr = 0;
    private JTextField rowInput;
    private JTextField addrInput;
    private JLabel rowLabel;
    private JLabel addrLabel;
    private MainFrame mf;

    public MemoryFrame(String name, MainFrame mf)
    {
        super(name, false);
        this.mf = mf;
        initialize();
    }

    @Override
    public void update()
    {
        TableModel model = memoryTable.getModel();
        //get start-addr = first address in the memory table
        if (model.getColumnCount() > 0)
        {
            String startAddrString = model.getValueAt(0, 0).toString();

            try
            {
                for (int i = 0; i < model.getRowCount(); ++i)
                {
                    final uint32 uint_val = MainFrame.getInstance().getOpenDLXSim().getPipeline().getMainMemory().read_u32(new uint32(Integer.parseInt(startAddrString.substring(2), 16) + i * 4));
                    final Object value;
                    if (Preference.displayMemoryAsHex())
                        value = uint_val.getValueAsHexString();
                    else
                        value = uint_val.getValue();
                    model.setValueAt(value, i, 1);
                }
            }
            catch (MemoryException e)
            {
                mf.getPipelineExceptionHandler().handlePipelineExceptions(e);
            }
        }
    }

    @Override
    protected void initialize()
    {
        super.initialize();
        setLayout(new BorderLayout());
        memoryTable = new MemoryTableFactory(rows, startAddr).createTable();
        JScrollPane scrollpane = new JScrollPane(memoryTable);
        scrollpane.setFocusable(false);
        memoryTable.setFillsViewportHeight(true);
        TableSizeCalculator.setDefaultMaxTableSize(scrollpane, memoryTable, TableSizeCalculator.SET_SIZE_WIDTH);
        add(scrollpane, BorderLayout.CENTER);

        //input
        JPanel inputPanel = new JPanel();
        addrLabel = new JLabel("start addr");
        inputPanel.add(addrLabel);
        addrInput = new JTextField(10);
        addrInput.addKeyListener(this);
        addrInput.setText("" + startAddr);
        addrInput.addFocusListener(this);
        inputPanel.add(addrInput);
        rowLabel = new JLabel("rows");
        inputPanel.add(rowLabel);
        rowInput = new JTextField(10);
        rowInput.setText("" + rows);
        rowInput.addKeyListener(this);
        rowInput.addFocusListener(this);
        inputPanel.add(rowInput);
        add(inputPanel, BorderLayout.NORTH);
        //controls
        JPanel controlPanel = new JPanel();
        reload = new JButton("reload");
        reload.addActionListener(this);
        controlPanel.add(reload);
        add(controlPanel, BorderLayout.SOUTH);
        pack();
        setVisible(true);
    }

    @Override
    public void clean()
    {
        setVisible(false);
        dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        try
        {
            Integer value = ValueInput.getValueSilent(addrInput.getText());
            if (value != null)
                startAddr = value;

            value = ValueInput.getValueSilent(rowInput.getText());
            if (value != null)
                rows = value;

            clean();
            InternalFrameFactory.getInstance().createMemoryFrame(mf);
            new CommandLoadFrameConfigurationSysLevel(mf).execute();
        }
        catch (Exception ex)
        {
            boolean error = true;
            if (Labels.labels != null)
            {
                if (Labels.labels.containsKey(addrInput.getText()))
                {
                    startAddr = (Integer) Labels.labels.get(addrInput.getText());
                    clean();
                    InternalFrameFactory.getInstance().createMemoryFrame(mf);
                    new CommandLoadFrameConfigurationSysLevel(mf).execute();
                    error = false;
                }
            }

            if (error)
                JOptionPane.showMessageDialog(this, "for input only hex (0x..) " +
                        "address, decimal address or label (e.g. \"main\") allowed");
        }
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
            reload.doClick();
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
            reload.doClick();
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
    }

    @Override
    public void focusGained(FocusEvent e)
    {
        JTextField f = (JTextField) e.getSource();
        f.selectAll();
    }

    @Override
    public void focusLost(FocusEvent e)
    {
    }

}
