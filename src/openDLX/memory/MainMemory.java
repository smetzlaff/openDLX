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
package openDLX.memory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;

import openDLX.datatypes.*;
import openDLX.exception.MemoryException;
import openDLX.util.PagedMemory;

public class MainMemory implements MemoryInterface
{
    private static Logger logger = Logger.getLogger("MainMemory");
    private PagedMemory memory;
    private short memory_latency;

    public MainMemory(String raw_file, int raw_file_code_start_address, short memory_latency) throws MemoryException
    {
        this.memory_latency = memory_latency;
        memory = new PagedMemory();

        try
        {
            FileInputStream fileinputstream = new FileInputStream(raw_file);

            int raw_file_size = fileinputstream.available();

            byte bytearray[] = new byte[raw_file_size];

            fileinputstream.read(bytearray);

            int i;
            // write memory code dump into memory, with base address and start
            // offset
            for (i = 0; i < raw_file_size; i++)
            {
                memory.writeByte(raw_file_code_start_address + i, bytearray[i]);
            }

            fileinputstream.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public uint8 read_u8(uint32 address, boolean log_output) throws MemoryException
    {
        if (log_output)
        {
            dumpMemory(new uint32(address.getValue() - 4), new uint32(address.getValue() + 4));
        }
        return new uint8(memory.readByte(address));
    }

    public uint8 read_u8(uint32 address) throws MemoryException
    {
        return read_u8(address, false);
    }

    public uint32 read_u32(uint32 address, boolean log_output) throws MemoryException
    {
        if((address.getValue()&0x3) != 0)
        {
            logger.error("Read u32 from unaligned addr: " + address.getValueAsHexString());
            throw new MemoryException("Read u32 from unaligned addr: " + address.getValueAsHexString());
        }

        uint32 value = new uint32((memory.readByte(address.getValue()) & 0xFF) + ((memory.readByte(address.getValue() + 1) & 0xFF) << 8) + ((memory.readByte(address.getValue() + 2) & 0xFF) << 16) + ((memory.readByte(address.getValue() + 3) & 0xFF) << 24));
        if (log_output)
        {
            //logger.trace("Read u32 from addr: " + address.getHex());
            //logger.trace("Read: " + Integer.toHexString(memory.readByte(address.getValue())) + " " + Integer.toHexString(memory.readByte(address.getValue() + 1)) + " "
            //	+ Integer.toHexString(memory.readByte(address.getValue() + 2)) + " " + Integer.toHexString(memory.readByte(address.getValue() + 3)) + " -> " + value.getHex());
            dumpMemory(new uint32(address.getValue() - 4), new uint32(address.getValue() + 4));
        }
        return value;
    }

    public uint32 read_u32(uint32 address) throws MemoryException
    {
        return read_u32(address, false);
    }

    /**
     * TODO ...
     * @throws MemoryException 
     */
    private uint32 read_u32_dump(uint32 address) throws MemoryException
    {
        return new uint32((memory.readByteDump(address.getValue()) & 0xFF) + ((memory.readByteDump(address.getValue() + 1) & 0xFF) << 8) + ((memory.readByteDump(address.getValue() + 2) & 0xFF) << 16) + ((memory.readByteDump(address.getValue() + 3) & 0xFF) << 24));
    }

    public uint16 read_u16(uint32 address, boolean log_output) throws MemoryException
    {
        if((address.getValue()&0x1 ) != 0)
        {
            logger.error("Read u16 from unaligned addr: " + address.getValueAsHexString());
            throw new MemoryException("Read u16 from unaligned addr: " + address.getValueAsHexString());
        }

        uint16 value = new uint16((memory.readByte(address.getValue()) & 0xFF) + ((memory.readByte(address.getValue() + 1) & 0xFF) << 8));
        if(log_output)
        {
            dumpMemory(new uint32(address.getValue() - 4), new uint32(address.getValue() + 4));
        }
        return value;
    }

    public uint16 read_u16(uint32 address) throws MemoryException
    {
        return read_u16(address, false);
    }

    public void write_u32(uint32 address, uint32 value) throws MemoryException
    {
        if((address.getValue()&0x3 ) != 0)
        {
            logger.error("Write u32 to unaligned addr: " + address.getValueAsHexString());
            throw new MemoryException("Write u32 to unaligned addr: " + address.getValueAsHexString());
        }

        logger.debug("Write u32 to addr: " + address.getValueAsHexString() + " value: " + value.getValueAsHexString());

        memory.writeByte(address.getValue(), (byte) (value.getValue() & 0xFF));
        memory.writeByte(address.getValue() + 1, (byte) ((value.getValue() >> 8) & 0xFF));
        memory.writeByte(address.getValue() + 2, (byte) ((value.getValue() >> 16) & 0xFF));
        memory.writeByte(address.getValue() + 3, (byte) ((value.getValue() >> 24) & 0xFF));

        logger.debug("Written: " + value.getValueAsHexString() + " -> " + memory.readByteAsString(address.getValue() + 3) + " " + memory.readByteAsString(address.getValue() + 2) + " "
                + memory.readByteAsString(address.getValue() + 1) + " " + memory.readByteAsString(address.getValue() + 0));
        dumpMemory(new uint32(address.getValue() - 4), new uint32(address.getValue() + 4));
    }


    public void write_u8(uint32 address, uint32 value) throws MemoryException
    {
        write_u8(address, new uint8(value.getValue()));
    }

    public void write_u8(uint32 address, uint8 value) throws MemoryException
    {
        logger.debug("Write u8 to addr: " + address.getValueAsHexString() + " value: " + value.getValueAsHexString());

        memory.writeByte(address.getValue(), value.getValue());

        logger.debug("Written: " + value.getValueAsHexString() + " -> " + memory.readByteAsString(address.getValue()));
        dumpMemory(new uint32(address.getValue() - 4), new uint32(address.getValue() + 4));
    }

    public void dumpMemory(uint32 start, uint32 end) throws MemoryException
    {
        uint32 start_aligned = new uint32(start.getValue() & 0xFFFFFFE0);

        logger.debug("             | 0          4          8          c          10         14         18         1c         |");
        logger.debug("-------------+-----------------------------------------------------------------------------------------+");

        for (uint32 addr = start_aligned; addr.getValue() <= end.getValue(); addr.setValue(addr.getValue() + 32))
        {
            String s = " " + addr.getValueAsHexString() + "  | ";

            for (int i = 0; i <= 28; i += 4)
            {
                s = s + read_u32_dump(new uint32(addr.getValue() + i)).getValueAsHexString() + " ";
            }
            s = s + "|";

            logger.debug(s);
        }
        logger.debug("-------------+-----------------------------------------------------------------------------------------+");

    }

    public uint32 getEndOfMemory()
    {
        return new uint32(-1);
    }

    public short getRequestDelay(RequestType type, uint32 address) throws MemoryException
    {
        switch (type)
        {
        case INSTR_RD:
        case DATA_RD:
        case DATA_WR:
            break;
        default:
            throw new MemoryException("Unknown memory request type.");
        }

        return getLatency();
    }

    public short getLatency()
    {
        return memory_latency;
    }

}
