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

import java.util.Properties;

import openDLX.BranchPredictionModule;
import openDLX.gui.Preference;

public class ArchCfg
{

    public static ISAType isa_type = stringToISAType(Preference.pref.get(Preference.isaTypePreferenceKey, "DLX"));

    // forwarding implies the two boolean: use_forwarding and use_load_stall_bubble
    public static boolean use_forwarding = Preference.pref.getBoolean(Preference.forwardingPreferenceKey, true);

    // TODO: rename variable
    public static boolean  use_load_stall_bubble = Preference.pref.getBoolean(Preference.mipsCompatibilityPreferenceKey, true);

    public static final String[] GP_NAMES_MIPS =
    {
        "ze", "at", "v0", "v1", "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7", "t4", "t5", "t6", "t7",
        "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7", "t8", "t9", "k0", "k1", "gp", "sp", "s8", "ra"
    };
    public static final String[] GP_NAMES_DLX =
    {
        "r0 ", "r1 ", "r2 ", "r3 ", "r4 ", "r5 ", "r6 ", "r7 ", "r8 ", "r9 ", "r10", "r11", "r12", "r13", "r14", "r15",
        "r16", "r17", "r18", "r19", "r20", "r21", "r22", "r23", "r24", "r25", "r26", "r27", "r28", "r29", "r30", "r31"
    };

    public static BranchPredictorType branch_predictor_type =
            BranchPredictionModule.getBranchPredictorTypeFromString(
                    Preference.pref.get(Preference.bpTypePreferenceKey, ""));
    public static final String[] possibleBpTypeComboBoxValues =
    {
        BranchPredictorType.UNKNOWN.toGuiString(),
        BranchPredictorType.S_ALWAYS_NOT_TAKEN.toGuiString(),
        BranchPredictorType.S_ALWAYS_TAKEN.toGuiString(),
        BranchPredictorType.S_BACKWARD_TAKEN.toGuiString(),
        BranchPredictorType.D_1BIT.toGuiString(),
        BranchPredictorType.D_2BIT_SATURATION.toGuiString(),
        BranchPredictorType.D_2BIT_HYSTERESIS.toGuiString()
    };

    public static BranchPredictorState branch_predictor_initial_state =
            BranchPredictionModule.getBranchPredictorInitialStateFromString(
                    Preference.pref.get(Preference.bpInitialStatePreferenceKey, ""));
    public static final String[] possibleBpInitialStateComboBoxValues =
    {
        BranchPredictorState.UNKNOWN.toGuiString(),
        BranchPredictorState.PREDICT_NOT_TAKEN.toGuiString(),
        BranchPredictorState.PREDICT_TAKEN.toGuiString(),
        BranchPredictorState.PREDICT_WEAKLY_NOT_TAKEN.toGuiString(),
        BranchPredictorState.PREDICT_WEAKLY_TAKEN.toGuiString(),
        BranchPredictorState.PREDICT_STRONGLY_NOT_TAKEN.toGuiString(),
        BranchPredictorState.PREDICT_WEAKLY_TAKEN.toGuiString()
    };
    public static int branch_predictor_table_size = Preference.pref.getInt(
            Preference.btbSizePreferenceKey, 1);


    public static int max_cycles = Preference.pref.getInt(Preference.maxCyclesPreferenceKey, 1000);

    public static void registerArchitectureConfig(Properties config)
    {
        ArchCfg.isa_type = stringToISAType(config.getProperty("isa_type"));
        ArchCfg.use_forwarding = getUseForwardingCfg(config);
        ArchCfg.use_load_stall_bubble = getUseLoadStallBubble(config);
    }

    public static ISAType stringToISAType(String s)
    {
        if (s.compareTo("MIPS") == 0)
        {
            return ISAType.MIPS;
        }
        else if (s.compareTo("DLX") == 0)
        {
            return ISAType.DLX;
        }

        return ISAType.UNKNOWN_ISA;
    }

    private static boolean getUseForwardingCfg(Properties config)
    {
        if (ArchCfg.isa_type == ISAType.MIPS)
        {
            return true;
        }
        else if (ArchCfg.isa_type == ISAType.DLX)
        {
            if ((((config.getProperty("use_forwarding")).toLowerCase()).compareTo("true") == 0)
                    || ((config.getProperty("use_forwarding")).compareTo("1") == 0))
            {
                return true;
            }
            else
            {
                return false;
            }

        }
        return true;
    }

    private static boolean getUseLoadStallBubble(Properties config)
    {
        if (ArchCfg.isa_type == ISAType.MIPS)
        {
            return true;
        }
        else if (ArchCfg.isa_type == ISAType.DLX)
        {
            if ((((config.getProperty("use_load_stall_bubble")).toLowerCase()).compareTo("true") == 0)
                    || ((config.getProperty("use_load_stall_bubble")).compareTo("1") == 0))
            {
                return true;
            }
            else
            {
                return false;
            }

        }
        return true;
    }

    public static String getRegisterDescription(int reg_id)
    {
        if (isa_type == ISAType.MIPS)
        {
            return GP_NAMES_MIPS[reg_id];
        }
        else if (isa_type == ISAType.DLX)
        {
            return GP_NAMES_DLX[reg_id];
        }

        return "-";
    }

    public static int getRegisterCount()
    {
        if (isa_type == ISAType.MIPS)
        {
            return GP_NAMES_MIPS.length;
        }
        else if (isa_type == ISAType.DLX)
        {
            return GP_NAMES_DLX.length;
        }
        return 0;
    }

}
