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
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import openDLX.asm.DLXAssembler;
import openDLX.datatypes.uint32;
import openDLX.exception.MemoryException;
import openDLX.gui.GUI_CONST;
import openDLX.gui.MainFrame;
import openDLX.gui.internalframes.OpenDLXSimInternalFrame;
import openDLX.gui.internalframes.util.NotSelectableTableModel;
import openDLX.OpenDLXSimulator;
import openDLX.gui.internalframes.renderer.ClockCycleFrameTableCellRenderer;
import openDLX.util.ClockCycleLog;

@SuppressWarnings("serial")
public final class ClockCycleFrame extends OpenDLXSimInternalFrame implements GUI_CONST
{

    private OpenDLXSimulator openDLXSim;
    //frame text    
    final private String addr_header_text = "Address";
    final private String inst_header_text = "Instructions/Cycles";
    //default size values
    final int inst_name_max_column_width = 150;
    private int block = 80;
    //tables, scrollpane and table models
    private JTable table, codeTable, addrTable;
    private NotSelectableTableModel model, codeModel, addrModel;
    private JScrollPane clockCycleScrollPane;
    private JScrollPane addrScrollPane;
    private JScrollPane codeScrollPane;
    private JScrollBar clockCycleScrollBarVertical;
    private JScrollBar addrScrollBar;
    private JScrollBar codeScrollBar;
    //private JScrollBar clockCycleScrollBarHorizontal;

    public ClockCycleFrame(String title)
    {
        super(title);
        this.openDLXSim = MainFrame.getInstance().getOpenDLXSim();
        initialize();
    }

    @Override
    public void initialize()
    {
        setLayout(new BorderLayout());

        //Code Table 
        codeModel = new NotSelectableTableModel();
        codeTable = new JTable(codeModel);
        codeTable.setFocusable(false);
        codeTable.setShowGrid(false);
        codeTable.getTableHeader().setReorderingAllowed(false);
        codeTable.setShowHorizontalLines(true);
        codeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        codeModel.addColumn(inst_header_text);
        TableColumnModel tcm = codeTable.getColumnModel();
        tcm.getColumn(0).setMaxWidth(inst_name_max_column_width);
        tcm.getColumn(0).setMinWidth(inst_name_max_column_width);
        codeScrollPane = new JScrollPane(codeTable);
        codeScrollPane.setPreferredSize(new Dimension(tcm.getColumn(0).getMaxWidth(), codeScrollPane.getPreferredSize().height));
        codeScrollBar = codeScrollPane.getVerticalScrollBar();
        codeScrollBar.addAdjustmentListener(new AdjustmentListener()
        {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e)
            {
                clockCycleScrollBarVertical.setValue(e.getValue());
                addrScrollBar.setValue(e.getValue());
            }

        });
        //Address Table
        addrModel = new NotSelectableTableModel();
        addrTable = new JTable(addrModel);
        addrTable.setFocusable(false);
        addrTable.setShowGrid(false);
        addrTable.getTableHeader().setReorderingAllowed(false);
        addrTable.setShowHorizontalLines(true);
        addrTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        addrModel.addColumn(addr_header_text);
        TableColumnModel tcm2 = addrTable.getColumnModel();
        tcm2.getColumn(0).setMaxWidth(inst_name_max_column_width);
        tcm2.getColumn(0).setMinWidth(inst_name_max_column_width);
        addrScrollPane = new JScrollPane(addrTable);
        addrScrollPane.setPreferredSize(new Dimension(tcm2.getColumn(0).getMaxWidth(), addrScrollPane.getPreferredSize().height));
        addrScrollBar = addrScrollPane.getVerticalScrollBar();
        addrScrollBar.addAdjustmentListener(new AdjustmentListener()
        {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e)
            {
                clockCycleScrollBarVertical.setValue(e.getValue());
                codeScrollBar.setValue(e.getValue());
            }

        });

        //scroll pane and frame
        clockCycleScrollPane = makeTableScrollPane();
        add(addrScrollPane, BorderLayout.EAST);
        add(codeScrollPane, BorderLayout.WEST);
        add(clockCycleScrollPane, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    private JScrollPane makeTableScrollPane()
    {
        //Clock Cycle Table
        model = new NotSelectableTableModel();
        table = new JTable(model);
        table.setFocusable(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setReorderingAllowed(false);
        table.setDefaultRenderer(Object.class, new ClockCycleFrameTableCellRenderer());
        clockCycleScrollPane = new JScrollPane(table);
        clockCycleScrollBarVertical = clockCycleScrollPane.getVerticalScrollBar();
        clockCycleScrollBarVertical.addAdjustmentListener(new AdjustmentListener()
        {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e)
            {
                addrScrollBar.setValue(e.getValue());
                codeScrollBar.setValue(e.getValue());
            }

        });

        // scrolling synchronously causes view violations 
        // requires a more complex/sophisticated approach
//        clockCycleScrollBarHorizontal = clockCycleScrollPane.getHorizontalScrollBar();
        /*  clockCycleScrollBarHorizontal.addAdjustmentListener(new AdjustmentListener()
         {
         @Override
         public void adjustmentValueChanged(AdjustmentEvent e)
         {
         clockCycleScrollBarVertical.setValue(e.getValue());
                
         }

         });*/
        clockCycleScrollPane.setPreferredSize(new Dimension(3 * block, clockCycleScrollPane.getPreferredSize().height));
        return clockCycleScrollPane;

    }

    @Override
    public void update()
    {

        //clear table
        addrModel.setRowCount(0);
        codeModel.setRowCount(0);
        model.setColumnCount(0);
        model.setRowCount(0);
        Iterator<uint32> codeIterator = ClockCycleLog.code.iterator();
        DLXAssembler asm = new DLXAssembler();
        int i = 0;
        while (codeIterator.hasNext())
        {
            uint32 addr = codeIterator.next();
            try
            {

                uint32 inst = openDLXSim.getPipeline().getInstructionMemory().read_u32(addr);
                String instStr = asm.Instr2Str(inst.getValue());
                String[] instrAddrText =
                {
                    addr.getHex()
                };

                String[] instrText =
                {
                    instStr
                };
                addrModel.addRow(instrAddrText);
                codeModel.addRow(instrText);
                model.addColumn(i);
                String cycleValue[] =
                {
                    ""
                };
                model.addRow(cycleValue);
                HashMap h = ClockCycleLog.log.get(i);
                Iterator<uint32> setIterator = h.keySet().iterator();
                while (setIterator.hasNext())
                {
                    uint32 checkAddr = setIterator.next();

                    ArrayList forbidden = new ArrayList();
                    for (int k = addrModel.getRowCount() - 1; k >= 0; --k)
                    {
                        if (addrModel.getValueAt(k, 0).equals(checkAddr.getHex()) && !forbidden.contains(checkAddr) && !instStr.contains("bubble"))
                        {
                            model.setValueAt(h.get(checkAddr), k, i);
                            forbidden.add(checkAddr);

                        }
                    }
                }
                ++i;
            }
            catch (MemoryException e)
            {
                MainFrame.getInstance().getPipelineExceptionHandler().handlePipelineExceptions(e);
            }
        }
        for (int j = 0; j < table.getColumnModel().getColumnCount(); ++j)
        {
            TableColumn column = table.getColumnModel().getColumn(j);
            column.setMaxWidth(30);
            column.setResizable(false);
        }

        // clockCycleScrollBarHorizontal.setValue(clockCycleScrollBarHorizontal.getMaximum());
        table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, table.getColumnCount() - 1, true));
        codeTable.scrollRectToVisible(codeTable.getCellRect(table.getRowCount() - 1, 0, true));
        addrTable.scrollRectToVisible(addrTable.getCellRect(addrTable.getRowCount() - 1, 0, true));
        
    }

    @Override
    public void clean()
    {
        setVisible(false);
        dispose();
    }


}
