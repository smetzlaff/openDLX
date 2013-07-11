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
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import openDLX.BranchPredictionModule;
import openDLX.datatypes.ArchCfg;
import openDLX.datatypes.BranchPredictorState;
import openDLX.datatypes.BranchPredictorType;
import openDLX.gui.MainFrame;
import openDLX.gui.Preference;

@SuppressWarnings("serial")
public class OptionDialog extends JDialog implements ActionListener
{
    // two control buttons, press confirm to save selected options
    private JButton confirm;
    private JButton cancel;

    // checkBoxes
    private JCheckBox forwardingCheckBox;
    private JCheckBox mipsCompatibilityCheckBox;

    /*
     * JComboBox may be represented by Vectors or Arrays of Objects (Object [])
     * we have chosen "String[]" to be the representation (in fact - String) for
     * the data within AsmFileLoader-class , but Vector is appropriate as well.
     */
    private JComboBox<String> bpTypeComboBox;
    private JComboBox<String> bpInitialStateComboBox;
    private JTextField btbSizeTextField;

    //input text fields
    private JTextField maxCyclesTextField;

    public OptionDialog(Frame owner)
    {
        //calls modal constructor, set to "false" to make dialog non-modal
        super(owner, true);
        setLayout(new BorderLayout());
        setTitle("options");
        //control buttons
        confirm = new JButton("OK");
        confirm.addActionListener(this);
        cancel = new JButton("Cancel");
        cancel.addActionListener(this);
        //the panel containing all the control buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(confirm);
        buttonPanel.add(cancel);
        add(buttonPanel, BorderLayout.SOUTH);

        /*instantiate all the components that you'd like to use as input,
         * as well as any labels describing them, HERE: */

        /*create a checkboxes
         *
         * checkboxes don't need a label -> the name is part of the constructor
         *-> its a single element, hence it doesn't need a JPanel  */
        forwardingCheckBox = new JCheckBox("Use Forwarding");
        forwardingCheckBox.setSelected(Preference.pref.getBoolean(Preference.forwardingPreferenceKey, true)); // load current value

        mipsCompatibilityCheckBox = new JCheckBox("MIPS compatibility mode (requires forwarding)");
        mipsCompatibilityCheckBox.setSelected(Preference.pref.getBoolean(Preference.mipsCompatibilityPreferenceKey, true)); // load current value

        // disable MIPS compatibility if no forwading is active
        if (!forwardingCheckBox.isSelected())
        {
            mipsCompatibilityCheckBox.setSelected(false);
        }

        /*create a JComboBoxes
         *
         * JComboBox need a Object[] or Vector as data representation
         Furthermore the  JComboBox gets a JLabel, describing it,
         * -> put both components into a JPanel*/

        // bpType:
        JLabel bpTypeComboBoxDescriptionLabel = new JLabel("Branch Predictor: ");
        bpTypeComboBox = new JComboBox<String>(ArchCfg.possibleBpTypeComboBoxValues);
        bpTypeComboBox.setSelectedItem(BranchPredictionModule.getBranchPredictorTypeFromString(
                Preference.pref.get(Preference.bpTypePreferenceKey, BranchPredictorType.UNKNOWN.toString())).toGuiString()); // load current value
        //surrounding panel
        JPanel bpTypeListPanel = new JPanel();
        //add the label
        bpTypeListPanel.add(bpTypeComboBoxDescriptionLabel);
        //add the box itself
        bpTypeListPanel.add(bpTypeComboBox);

        // bpInitialState:
        JLabel bpInitialStateComboBoxDescriptionLabel = new JLabel("Initial Predictor State: ");
        bpInitialStateComboBox = new JComboBox<String>(ArchCfg.possibleBpInitialStateComboBoxValues);
        bpInitialStateComboBox.setSelectedItem(BranchPredictionModule.getBranchPredictorInitialStateFromString(
                Preference.pref.get(Preference.bpInitialStatePreferenceKey, BranchPredictorState.UNKNOWN.toString())).toGuiString()); // load current value
        //surrounding panel
        JPanel bpInitialStateListPanel = new JPanel();
        //add the label
        bpInitialStateListPanel.add(bpInitialStateComboBoxDescriptionLabel);
        //add the box itself
        bpInitialStateListPanel.add(bpInitialStateComboBox);

        /*create a  JTextFields
         * the field and a JLabel description
         */

        // Max Cycles
        JLabel maxCyclesTextFieldDescription = new JLabel("Maximum Cycles: ");
        // the number in constructor means the number of lines in textfield
        maxCyclesTextField = new JTextField(10);
        //load current text from ArchCfg
        maxCyclesTextField.setText((new Integer(ArchCfg.max_cycles)).toString());
        //surrounding panel, containing both JLabel and JTextField
        JPanel maxCyclesTextFieldPanel = new JPanel();
        //add the label
        maxCyclesTextFieldPanel.add(maxCyclesTextFieldDescription);
        //add the field itself
        maxCyclesTextFieldPanel.add(maxCyclesTextField);

        // BTB Size
        JLabel btbSizeTextFieldDescription = new JLabel("BTB Size: ");
        // the number in constructor means the number of lines in textfield
        btbSizeTextField = new JTextField(5);
        //load current text from ArchCfg
        btbSizeTextField.setText((new Integer(ArchCfg.branch_predictor_table_size)).toString());
        //surrounding panel, containing both JLabel and JTextField
        JPanel btbSizeTextFieldPanel = new JPanel();
        //add the label
        btbSizeTextFieldPanel.add(btbSizeTextFieldDescription);
        //add the field itself
        btbSizeTextFieldPanel.add(btbSizeTextField);

        //this panel contains all input components = top level panel
        JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new GridLayout(0, 1));

        //dont forget adding the components to the panel !!!

        optionPanel.add(forwardingCheckBox);
        optionPanel.add(mipsCompatibilityCheckBox);
        optionPanel.add(bpTypeListPanel);
        optionPanel.add(bpInitialStateListPanel);
        optionPanel.add(btbSizeTextFieldPanel);
        optionPanel.add(maxCyclesTextFieldPanel);

        //adds the top-level-panel to the Dialog frame
        add(optionPanel, BorderLayout.CENTER);

        //dialog appears in the middle of the MainFrame
        setLocationRelativeTo(owner);
        pack();
        setResizable(false);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        //just close the dialog
        if (e.getSource().equals(cancel))
        {
            setVisible(false);
            dispose();
        }

        /* get all the values, assign them to data within ArchCfg
         * and save them as preference for future use
         */
        if (e.getSource().equals(confirm))
        {
            Preference.pref.putBoolean(Preference.forwardingPreferenceKey,
                    forwardingCheckBox.isSelected());
            Preference.pref.putBoolean(Preference.mipsCompatibilityPreferenceKey,
                    mipsCompatibilityCheckBox.isSelected());
            if (forwardingCheckBox.isSelected())
            {
                ArchCfg.use_forwarding = true;
                if(mipsCompatibilityCheckBox.isSelected())
                {
                    ArchCfg.use_load_stall_bubble = true;
                }
                else
                {
                    ArchCfg.use_load_stall_bubble = false;
                }

            }
            else
            {
                ArchCfg.use_forwarding = false;
                ArchCfg.use_load_stall_bubble = false;

                if(mipsCompatibilityCheckBox.isSelected())
                {
                    // reset the MIPS compatibility
                    mipsCompatibilityCheckBox.setSelected(false);
                    Preference.pref.putBoolean(Preference.mipsCompatibilityPreferenceKey, false);

                    JOptionPane.showMessageDialog(MainFrame.getInstance(), "Reset \"MIPS compatibility mode\", since it requires activated forwarding.", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            }

            // propagate forwarding to menu
            propagateFWToMenu(forwardingCheckBox.isSelected());

            // TODO also add a field for disabling the branch prediction
            // TODO do some checks for the setting of the BP initial state and sizes

            ArchCfg.branch_predictor_type = BranchPredictionModule.getBranchPredictorTypeFromGuiString(bpTypeComboBox.getSelectedItem().toString());
            Preference.pref.put(Preference.bpTypePreferenceKey, ArchCfg.branch_predictor_type.toString());

            ArchCfg.branch_predictor_initial_state = BranchPredictionModule.
                    getBranchPredictorInitialStateFromGuiString(
                            bpInitialStateComboBox.getSelectedItem().toString());
            Preference.pref.put(Preference.bpInitialStatePreferenceKey,
                    ArchCfg.branch_predictor_initial_state.toString());

            ArchCfg.branch_predictor_table_size = Integer.parseInt(btbSizeTextField.getText());
            Preference.pref.put(Preference.btbSizePreferenceKey, btbSizeTextField.getText());

            // correct user input
            switch(ArchCfg.branch_predictor_type)
            {
            case UNKNOWN:
            case S_ALWAYS_TAKEN:
            case S_ALWAYS_NOT_TAKEN:
            case S_BACKWARD_TAKEN:
                // unknown and static predictors have no initial state and no branch predictor table size
                ArchCfg.branch_predictor_initial_state = BranchPredictorState.UNKNOWN;
                Preference.pref.put(Preference.bpInitialStatePreferenceKey,
                        BranchPredictorState.UNKNOWN.toString());

                ArchCfg.branch_predictor_table_size = 1;
                Preference.pref.put(Preference.btbSizePreferenceKey,
                        new Integer(ArchCfg.branch_predictor_table_size).toString());
                break;
            case D_1BIT:
                switch(ArchCfg.branch_predictor_initial_state)
                {
                case PREDICT_STRONGLY_NOT_TAKEN:
                case PREDICT_WEAKLY_NOT_TAKEN:
                    // correct 2bit states to 1 bit state
                    ArchCfg.branch_predictor_initial_state = BranchPredictorState.PREDICT_NOT_TAKEN;
                    Preference.pref.put(Preference.bpInitialStatePreferenceKey,
                            ArchCfg.branch_predictor_initial_state.toString());
                    break;
                case PREDICT_STRONGLY_TAKEN:
                case PREDICT_WEAKLY_TAKEN:
                    // correct 2bit states to 1 bit state
                    ArchCfg.branch_predictor_initial_state = BranchPredictorState.PREDICT_TAKEN;
                    Preference.pref.put(Preference.bpInitialStatePreferenceKey,
                            ArchCfg.branch_predictor_initial_state.toString());
                    break;
                case UNKNOWN:
                default:
                    ArchCfg.branch_predictor_initial_state = BranchPredictorState.PREDICT_NOT_TAKEN;
                    Preference.pref.put(Preference.bpInitialStatePreferenceKey,
                            ArchCfg.branch_predictor_initial_state.toString());
                    // TODO Throw exception
                    break;
                }
                break;

            case D_2BIT_SATURATION:
            case D_2BIT_HYSTERESIS:
                switch(ArchCfg.branch_predictor_initial_state)
                {
                case PREDICT_NOT_TAKEN:
                    // correct 1bit states to 2 bit state
                    ArchCfg.branch_predictor_initial_state = BranchPredictorState.PREDICT_WEAKLY_NOT_TAKEN;
                    Preference.pref.put(Preference.bpInitialStatePreferenceKey,
                            ArchCfg.branch_predictor_initial_state.toString());
                    break;
                case PREDICT_TAKEN:
                    // correct 1bit states to 2 bit state
                    ArchCfg.branch_predictor_initial_state = BranchPredictorState.PREDICT_WEAKLY_TAKEN;
                    Preference.pref.put(Preference.bpInitialStatePreferenceKey,
                            ArchCfg.branch_predictor_initial_state.toString());
                    break;
                case UNKNOWN:
                default:
                    ArchCfg.branch_predictor_initial_state = BranchPredictorState.PREDICT_WEAKLY_NOT_TAKEN;
                    Preference.pref.put(Preference.bpInitialStatePreferenceKey,
                            ArchCfg.branch_predictor_initial_state.toString());
                    // TODO Throw exception
                    break;
                }
                break;
            }

            // the btb has to be a power of two
            if (ArchCfg.branch_predictor_table_size == 0)
            {
                ArchCfg.branch_predictor_table_size = 1;
                Preference.pref.put(Preference.btbSizePreferenceKey, (new Integer(ArchCfg.branch_predictor_table_size)).toString());
                // TODO Throw exception
            }

            ArchCfg.max_cycles = Integer.parseInt(maxCyclesTextField.getText());
            Preference.pref.put(Preference.maxCyclesPreferenceKey, maxCyclesTextField.getText());

            setVisible(false);
            dispose();
        }
    }

    private void propagateFWToMenu(boolean forwarding_enabled)
    {
        MainFrame.getInstance().getForwardingMenuItem().setSelected(forwarding_enabled);
    }

}
