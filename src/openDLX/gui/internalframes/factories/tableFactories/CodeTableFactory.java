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
package openDLX.gui.internalframes.factories.tableFactories;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import openDLX.OpenDLXSimulator;
import openDLX.asm.DLXAssembler;
import openDLX.datatypes.uint32;
import openDLX.exception.MemoryException;
import openDLX.gui.MainFrame;
import openDLX.gui.internalframes.renderer.CodeFrameTableCellRenderer;
import openDLX.gui.internalframes.util.NotSelectableTableModel;

public class CodeTableFactory extends TableFactory
{

    private OpenDLXSimulator openDLXSim;

    public CodeTableFactory(OpenDLXSimulator openDLXSim)
    {
        this.openDLXSim = openDLXSim;
    }

    @Override
    public JTable createTable()
    {

        model = new NotSelectableTableModel();
        table = new JTable(model);
        table.setFocusable(false);

        model.addColumn("address");
        model.addColumn("code hex");
        model.addColumn("code DLX");

        //default max width values change here
        TableColumnModel tcm = table.getColumnModel();
        final int defaultWidth = 150;
        tcm.getColumn(0).setMaxWidth(defaultWidth);
        tcm.getColumn(1).setMaxWidth(defaultWidth);
        tcm.getColumn(2).setMaxWidth(defaultWidth);
        table.setDefaultRenderer(Object.class, new CodeFrameTableCellRenderer());

        //insert code
        int start;
        if (!openDLXSim.getConfig().containsKey("text_begin"))
            start = openDLXSim.getPipeline().getFetchStage().getPc().getValue();
        else
            start = stringToInt(openDLXSim.getConfig().getProperty("text_begin"));

        int end = openDLXSim.getSimCycles();
        if (!openDLXSim.getConfig().containsKey("text_end"))
            end = start + 4 * openDLXSim.getSimCycles();
        else
            end = stringToInt(openDLXSim.getConfig().getProperty("text_end"));

        DLXAssembler asm = new DLXAssembler();
        try
        {
            for (int i = start; i < end; i += 4)
            {
                final uint32 addr = new uint32(i);
                final uint32 inst = openDLXSim.getPipeline().getInstructionMemory().read_u32(addr);

                model.addRow(new Object[]
                        {
                            addr,
                            inst,
                            asm.Instr2Str(inst.getValue())
                        });
            }
        }
        catch (MemoryException e)
        {
            MainFrame.getInstance().getPipelineExceptionHandler().handlePipelineExceptions(e);
        }
        return table;
    }

    private int stringToInt(String s)
    {
        return Long.decode(s).intValue();
    }

}
