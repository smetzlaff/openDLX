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
package openDLX.datatypes;

public class uint16 extends BasicNumber
{
    private short value;

    public uint16(int value)
    {
        this.value = (short) value;
    }

    public uint16(short value)
    {
        this.value = value;
    }

    public uint16(byte value)
    {
        this.value = (short) value;
    }

    public uint16(uint16 value)
    {
        this.value = (short) value.getValue();
    }

    public uint16()
    {
    }

    public short getValue()
    {
        return value;
    }

    public void setValue(short value)
    {
        this.value = value;
    }

    public String getValueAsHexString()
    {
        String s = Integer.toHexString(value);
        int diff = 4 - s.length();

        if (diff > 0)
        {
            // add leading zeros
            for (; diff > 0; diff--)
                s = "0" + s;
        }
        else if (diff < 0)
        {
            // chop off leading "f"s for negative numbers
            s = s.substring(s.length() - 4, s.length());
        }

        return "0x" + s;
    }

    public int hashCode()
    {
        return getValue();
    }

    public boolean equals(Object o)
    {
        if (o.getClass() == this.getClass())
        {
            return equals((uint16) o);
        }
        return false;
    }

    public boolean equals(uint16 other)
    {
        return (getValue() == other.getValue());
    }

    public String getValueAsDecimalString()
    {
        return Integer.toString(getValue());
    }
}
