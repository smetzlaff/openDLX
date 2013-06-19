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

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.PlainView;
import javax.swing.text.Segment;
import javax.swing.text.Utilities;
import openDLX.datatypes.ArchCfg;

public class OpenDLXSimView extends PlainView
{
    //TODO:
    // just test mnemonics to check if syntax highlighting works
    // add here full mnemonics list with ASM-function

    private static HashMap<Pattern, Color> patternColors;
    private final static String ADDI = "(addi )";
    private final static String ADDUI = "(addui )";
    private final static String J = "(j )";

    static
    {
        // TODO: make loop here to add all keywords, need menmnonic-list
        patternColors = new HashMap<Pattern, Color>();
        patternColors.put(Pattern.compile(ADDI), Color.BLUE);
        patternColors.put(Pattern.compile(ADDUI), Color.BLUE);
        patternColors.put(Pattern.compile(J), Color.BLUE);

        for (int i = 0; i<ArchCfg.getRegisterCount(); ++i)
        {
              patternColors.put(Pattern.compile("("+ArchCfg.getRegisterDescription(i).trim()+")"),Color.GRAY);
        }
    }

    public OpenDLXSimView(Element element)
    {

        super(element);

        // Set tabsize to 4 (instead of the default 8)
        getDocument().putProperty(PlainDocument.tabSizeAttribute, 4);
    }

    @Override
    protected int drawUnselectedText(Graphics graphics, int x, int y, int p0,
            int p1) throws BadLocationException
    {

        Document doc = getDocument();
        String text = doc.getText(p0, p1 - p0);

        Segment segment = getLineBuffer();

        SortedMap<Integer, Integer> startMap = new TreeMap<Integer, Integer>();
        SortedMap<Integer, Color> colorMap = new TreeMap<Integer, Color>();

        for (Map.Entry<Pattern, Color> entry : patternColors.entrySet())
        {

            Matcher matcher = entry.getKey().matcher(text);

            while (matcher.find())
            {
                startMap.put(matcher.start(1), matcher.end());
                colorMap.put(matcher.start(1), entry.getValue());
            }
        }


        int i = 0;

        // Colour the parts
        for (Map.Entry<Integer, Integer> entry : startMap.entrySet())
        {
            int start = entry.getKey();
            int end = entry.getValue();

            if (i < start)
            {
                graphics.setColor(Color.black);
                doc.getText(p0 + i, start - i, segment);
                x = Utilities.drawTabbedText(segment, x, y, graphics, this, i);
            }

            graphics.setColor(colorMap.get(start));
            i = end;
            doc.getText(p0 + start, i - start, segment);
            x = Utilities.drawTabbedText(segment, x, y, graphics, this, start);
        }

        // Paint possible remaining text black
        if (i < text.length())
        {
            graphics.setColor(Color.black);
            doc.getText(p0 + i, text.length() - i, segment);
            x = Utilities.drawTabbedText(segment, x, y, graphics, this, i);
        }

        return x;
    }

}
