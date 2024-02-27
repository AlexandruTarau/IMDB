package org.example.GUI.Menu;

import org.example.Actor;
import org.example.Genre;
import org.example.IMDB;
import org.example.Production;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class ActorsPanel extends JPanel {
    private JLabel imageLabel;
    private JTextArea actorInfoTextArea;
    private DefaultListModel<String> listModel;
    private SortedSet<Actor> actors;
    private Actor actor;
    private List<Actor> actorsCopy;

    public ActorsPanel(CardLayout cardLayout, JPanel cardPanel, boolean fromMenu) {
        setLayout(new BorderLayout());
        actors = new TreeSet<>(IMDB.getInstance().getActors());
        actorsCopy = new ArrayList<>(actors);

        listModel = new DefaultListModel<>();
        updateActorsList(null);

        JList<String> actorsJList = new JList<>(listModel);
        actorsJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        actorsJList.setFixedCellWidth(200);
        actorsJList.setPreferredSize(new Dimension(200, actorsJList.getPreferredSize().height));

        JScrollPane scrollPane = new JScrollPane(actorsJList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Actors"));

        JPanel imagePanel = new JPanel(new BorderLayout());
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new GridLayout(2, 1, 20, 10));

        // Add to Favourites button
        JButton addToFavouritesButton = new JButton("Add to Favourites");
        addToFavouritesButton.setPreferredSize(new Dimension(140, 30));
        addToFavouritesButton.addActionListener(e -> {
            IMDB.getInstance().addToFavourites(actor);
        });

        // Request button
        JButton addRequestButton = new JButton("Make Request");
        addRequestButton.setPreferredSize(new Dimension(140, 30));
        addRequestButton.addActionListener(e -> {
            IMDB.getInstance().makeRequest(actor);
        });

        buttonsPanel.add(addRequestButton);
        buttonsPanel.add(addToFavouritesButton);

        JPanel imageAndButtonPanel = new JPanel(new BorderLayout());
        imageAndButtonPanel.add(imagePanel, BorderLayout.CENTER);
        imageAndButtonPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Information about actor
        actorInfoTextArea = new JTextArea();
        actorInfoTextArea.setEditable(false);
        actorInfoTextArea.setLineWrap(true);
        actorInfoTextArea.setWrapStyleWord(true);
        actorInfoTextArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel textAreaPanel = new JPanel(new BorderLayout());
        textAreaPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        textAreaPanel.add(new JScrollPane(actorInfoTextArea), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(new EmptyBorder(50, 0, 50, 0));
        rightPanel.add(imageAndButtonPanel, BorderLayout.WEST);
        rightPanel.add(textAreaPanel, BorderLayout.CENTER);

        if (!actorsCopy.isEmpty()) {
            updateDisplay(actorsCopy.get(0));
        }
        actorsJList.addListSelectionListener(e -> {
            int selectedIndex = actorsJList.getSelectedIndex();

            if (selectedIndex != -1) {
                updateDisplay(actorsCopy.get(selectedIndex));
            }
        });

        JButton filterButton = new JButton("Filter");
        filterButton.addActionListener(e -> {
            String[] filterOptions = {"By Number of Performances"};
            String selectedOption = (String) JOptionPane.showInputDialog(
                    this,
                    "Choose filtering option:",
                    "Filter Options",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    filterOptions,
                    filterOptions[0]);

            if (selectedOption != null) {
                List<Actor> filteredActors;
                if (selectedOption.equals("By Number of Performances")) {
                    String minPerformances = JOptionPane.showInputDialog(this, "Enter minimum number of performances:");
                    if (minPerformances != null && !minPerformances.isEmpty()) {
                        try {
                            int minPerformancesValue = Integer.parseInt(minPerformances);
                            filteredActors = actors.stream()
                                    .filter(actor -> filterByPerformances(actor, minPerformancesValue))
                                    .toList();

                            updateActorsList(filteredActors);
                            actorsCopy.clear();
                            actorsCopy.addAll(filteredActors);
                        } catch (NumberFormatException exception) {
                            JOptionPane.showMessageDialog(this, "Invalid number of performances!",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        } finally {
                            actorInfoTextArea.setText("");
                            imageLabel.setIcon(null);
                            actor = null;
                        }
                    }
                }
            }
        });

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, fromMenu ? "MainMenu" : "MainPage");
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(backButton);
        buttonPanel.add(filterButton);

        add(buttonPanel, BorderLayout.NORTH);
        add(rightPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.WEST);
    }
    private void updateDisplay(Actor actor) {
        actorInfoTextArea.setText(actor.toString());
        actorInfoTextArea.setCaretPosition(0);
        ImageIcon imageIcon = actor.image;
        imageIcon.setImage(imageIcon.getImage().getScaledInstance(300, 500, Image.SCALE_DEFAULT));
        imageLabel.setIcon(imageIcon);
        this.actor = actor;
    }
    private boolean filterByPerformances(Actor actor, int nr) {
        return actor.performances.size() >= nr;
    }
    public void updateActorsList(List<Actor> actors) {
        listModel.clear();
        if (actors == null) {
            this.actors.clear();
            this.actors.addAll(IMDB.getInstance().getActors());
            this.actorsCopy.clear();
            this.actorsCopy = new ArrayList<>(this.actors);
        }

        for (Actor a : (actors == null ? this.actors : actors)) {
            listModel.addElement(a.name);
        }
    }
}
