package org.example.GUI.Menu;

import org.example.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class RatingsPanel extends JPanel {
    private JList<Rating> ratingJList;
    private DefaultListModel<Rating> listModel;
    private ArrayList<Production> rated_productions = new ArrayList<>();

    public RatingsPanel(CardLayout cardLayout, JPanel cardPanel) {
        User user = IMDB.getInstance().loggedInUser;
        setLayout(new GridBagLayout());

        listModel = new DefaultListModel<>();
        updateRatingsList();

        // Creating a border for the panel
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(Color.BLACK)
        ));

        JPanel centeredPanel = new JPanel(new BorderLayout());
        centeredPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        ratingJList = new JList<>(listModel);
        ratingJList.setFixedCellHeight(50);
        ratingJList.setToolTipText("");

        // Display ratings information + hover to get full information
        ratingJList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel renderer = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Rating rating = (Rating) value;

                String text = "<" + rated_productions.get(index).title + "> (" + rating.rating + ") : " + rating.comments;
                renderer.setText(text);
                renderer.setToolTipText(text);

                return renderer;
            }
        });

        // Buttons
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            String[] options = {"Rate production", "Rate actor"};
            String selectedOption = (String) JOptionPane.showInputDialog(
                    null,
                    "Choose an option:",
                    "Add rating",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (selectedOption != null) {
                switch (selectedOption) {
                    case "Rate production": {
                        cardLayout.show(cardPanel, "ProductionsPanel");
                        break;
                    }
                    case "Rate actor": {
                        cardLayout.show(cardPanel, "ActorsPanelMenu");
                        break;
                    }
                }
            }
        });

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            int selectedIndex = ratingJList.getSelectedIndex();
            if (selectedIndex != -1) {
                int dialogResult = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to delete this rating?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    Rating selectedRating = ratingJList.getSelectedValue();

                    // Remove rating from main db
                    rated_productions.get(selectedIndex).ratings.remove(selectedRating);
                    rated_productions.get(selectedIndex).updateAvgRating(selectedRating.rating, false);

                    // Remove production from list of ratedProductions
                    ((Regular<?>) user).ratedProductions.remove(rated_productions.get(selectedIndex));

                    // Remove observers
                    for (Rating rating : rated_productions.get(selectedIndex).ratings) {
                        rating.removeObserver(user);
                    }
                    updateRatingsList();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a rating to delete!");
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        centeredPanel.add(buttonPanel, BorderLayout.NORTH);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "MainMenu");
            }
        });

        JScrollPane scrollPane = new JScrollPane(ratingJList);
        scrollPane.setPreferredSize(new Dimension(250, 300));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        centeredPanel.add(scrollPane, BorderLayout.CENTER);
        centeredPanel.add(backButton, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        add(centeredPanel, gbc);
    }

    public void updateRatingsList() {
        User user = IMDB.getInstance().loggedInUser;
        listModel.clear();
        rated_productions.clear();

        for (Production production : IMDB.getInstance().getProductions()) {
            for (Rating rating : production.ratings) {
                if (rating.username.equals(user.username)) {
                    rated_productions.add(production);
                    listModel.addElement(rating);
                }
            }
        }
    }
}
