/*
 * Copyright (C) 2018 MegaMek team
 *
 * This file is part of MekHQ.
 *
 * MekHQ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * MekHQ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MekHQ.  If not, see <http://www.gnu.org/licenses/>.
 */

package mekhq.gui.dialog.scenarioAwards;

import mekhq.gui.filter.AwardNameFilter;
import mekhq.gui.filter.AwardSuggestionFilter;
import mekhq.gui.model.ScenarioAwardsAwardTableModel;
import mekhq.gui.utilities.JTableUtilities;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This panel is the rightmost panel of the ScenarioAwardsPanel. It is a list of all available awards, with filtering
 * and searching options.
 */
public class UnawardedAwardsPanel extends JPanel {

    private JTable unawardedAwardsTable;
    private JTable awardedAwardsTable;
    private ScenarioAwardsAwardTableModel awardTableModel;

    public UnawardedAwardsPanel(JTable unawardedAwardsTable, JTable awardedAwardsTable, ScenarioAwardsAwardTableModel awardTableModel) {
        super();

        this.unawardedAwardsTable = unawardedAwardsTable;
        this.awardedAwardsTable = awardedAwardsTable;
        this.awardTableModel = awardTableModel;

        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;

        setLayout(new GridBagLayout());
        TitledBorder borderUnawardedAwards = new TitledBorder("Awards");
        borderUnawardedAwards.setTitleJustification(TitledBorder.CENTER);
        borderUnawardedAwards.setTitlePosition(TitledBorder.TOP);
        setBorder(borderUnawardedAwards);

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new GridLayout(0,2));
        JRadioButton allButton = new JRadioButton("All");
        allButton.setSelected(true);
        allButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                TableRowSorter<ScenarioAwardsAwardTableModel> sorter = new TableRowSorter<>(awardTableModel);
                sorter.setRowFilter(null);
                unawardedAwardsTable.setRowSorter(sorter);
            }
        });

        JRadioButton suggestedButton = new JRadioButton("Suggested");
        suggestedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                TableRowSorter<ScenarioAwardsAwardTableModel> sorter = new TableRowSorter<>(awardTableModel);
                sorter.setRowFilter(new AwardSuggestionFilter());
                unawardedAwardsTable.setRowSorter(sorter);
            }
        });

        //TODO: Uncomment this and implement the filtering of the SUGGESTED awards, based on xml parameters
        /*
        ButtonGroup group = new ButtonGroup();
        group.add(allButton);
        group.add(suggestedButton);
        radioPanel.add(allButton);
        radioPanel.add(suggestedButton);
        add(radioPanel, gridBagConstraints);
        */

        JTextField searchTextField = new JTextField("search", 30);
        searchTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                TableRowSorter<ScenarioAwardsAwardTableModel> sorter = new TableRowSorter<>(awardTableModel);
                sorter.setRowFilter(new AwardNameFilter(searchTextField.getText()));
                unawardedAwardsTable.setRowSorter(sorter);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                TableRowSorter<ScenarioAwardsAwardTableModel> sorter = new TableRowSorter<>(awardTableModel);
                sorter.setRowFilter(new AwardNameFilter(searchTextField.getText()));
                unawardedAwardsTable.setRowSorter(sorter);
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) { }
        });
        gridBagConstraints.gridy = 1;
        add(searchTextField, gridBagConstraints);

        awardTableModel.populateWithAllAwards();
        unawardedAwardsTable.setModel(awardTableModel);
        unawardedAwardsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        unawardedAwardsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        TableRowSorter<ScenarioAwardsAwardTableModel> sorter = new TableRowSorter<ScenarioAwardsAwardTableModel>((ScenarioAwardsAwardTableModel) unawardedAwardsTable.getModel());
        unawardedAwardsTable.setRowSorter(sorter);
        JTableUtilities.resizeColumnWidth(unawardedAwardsTable);
        unawardedAwardsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                awardedAwardsTable.clearSelection();
            }
        });

        unawardedAwardsTable.removeColumn(unawardedAwardsTable.getColumnModel().getColumn(0));

        gridBagConstraints.gridy = 2;
        JScrollPane pane = new JScrollPane(unawardedAwardsTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        Dimension preferedSize = pane.getPreferredSize();
        preferedSize.width = 400;
        pane.setPreferredSize(preferedSize);
        add(pane, gridBagConstraints);
    }
}
