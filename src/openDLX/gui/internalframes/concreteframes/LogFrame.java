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

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import openDLX.gui.MainFrame;
import openDLX.gui.internalframes.OpenDLXSimInternalFrame;
import openDLX.gui.internalframes.renderer.LogFrameTableCellRenderer;
import openDLX.gui.internalframes.util.LogReader;
import openDLX.gui.internalframes.util.NotSelectableTableModel;

@SuppressWarnings("serial")
public final class LogFrame extends OpenDLXSimInternalFrame
{

    //tables, scrollpane and table models
    private JTable infoTable;
    private NotSelectableTableModel model;
    private JScrollPane scrollpane;
    //data
    private final MainFrame mf;
    private LogReader logReader;
    private String logFileAddr;

    public LogFrame(String title)
    {
        super(title, true);
        initialize();
        mf = MainFrame.getInstance();
        try
        {
            logFileAddr = mf.getOpenDLXSim().getConfig().getProperty("log_file");
            logReader = new LogReader(logFileAddr);
        }
        catch (Exception e)
        {
            String err = "Reading log file failed -  " + e.toString();
            JOptionPane.showMessageDialog(mf, err);
        }
    }

    @Override
    public void update()
    {
        if (logFileAddr != null && logReader != null)
        {
            logReader.update();
            
            for(String s : logReader.getLog())
            {
                model.addRow(new String[]{s});
            }

            logReader.getLog().clear();

            if (scrollpane != null)
                infoTable.scrollRectToVisible(infoTable.getCellRect(
                        infoTable.getRowCount() - 1, 0, true));
        }
    }

    @Override
    public void clean()
    {
        setVisible(false);
        dispose();
    }

    @Override
    protected void initialize()
    {
        super.initialize();
        setLayout(new BorderLayout());
        model = new NotSelectableTableModel();
        infoTable = new JTable(model);
        infoTable.setFocusable(false);
        model.addColumn("");
        infoTable.setShowGrid(false);
        infoTable.setDefaultRenderer(Object.class, new LogFrameTableCellRenderer());
        scrollpane = new JScrollPane(infoTable);
        scrollpane.setFocusable(false);
        add(scrollpane, BorderLayout.CENTER);
        setSize(new Dimension(300, 200));
        setVisible(true);
    }

}
