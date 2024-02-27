package org.example.GUI.Menu;

import org.example.*;
import org.example.ExperienceStrategies.RewardContribution;
import org.intellij.lang.annotations.Flow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ContributionsPanel extends JPanel {
    private JLabel imageLabel;
    private DefaultListModel<String> listModel;
    private List<Object> contributions = new ArrayList<>();
    JPanel actorPanel;
    JPanel moviePanel;
    JPanel seriesPanel;
    private AtomicBoolean newContribution = new AtomicBoolean(false);

    public ContributionsPanel (CardLayout cardLayout, JPanel cardPanel) {
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        updateContributions();

        JList<String> contributionsJList = new JList<>(listModel);
        contributionsJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contributionsJList.setFixedCellWidth(200);
        contributionsJList.setPreferredSize(new Dimension(200, contributionsJList.getPreferredSize().height));

        JScrollPane scrollPane = new JScrollPane(contributionsJList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Contributions"));

        // Image Panel
        JPanel imagePanel = new JPanel(new BorderLayout());
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        // Information fields Panel
        JPanel cards = new JPanel(new CardLayout());

        actorPanel = new JPanel(new BorderLayout());
        moviePanel = new JPanel(new BorderLayout());
        seriesPanel = new JPanel(new BorderLayout());
        JPanel emptyPanel = new JPanel(new BorderLayout());

        cards.add(actorPanel, "Actor");
        cards.add(moviePanel, "Movie");
        cards.add(seriesPanel, "Series");
        cards.add(emptyPanel, "Empty");

        // Contribution Panel
        JPanel contributionPanel = new JPanel(new BorderLayout());
        contributionPanel.add(imagePanel, BorderLayout.CENTER);
        contributionsJList.addListSelectionListener(e -> {
            int selectedIndex = contributionsJList.getSelectedIndex();

            if (selectedIndex != -1) {
                CardLayout layout = (CardLayout) cards.getLayout();
                if (contributions.get(selectedIndex) instanceof Actor) {
                    layout.show(cards, "Actor");
                } else if (contributions.get(selectedIndex) instanceof Movie) {
                    layout.show(cards, "Movie");
                } else {
                    layout.show(cards, "Series");
                }

                updateDisplay(contributions.get(selectedIndex));
            }
        });

        // Back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "MainMenu");
            }
        });

        // Add new contribution button
        JButton addContribution = new JButton("Add");
        addContribution.setPreferredSize(new Dimension(140, 30));
        addContribution.addActionListener(e -> {
            String[] options = {"Actor", "Movie", "Series"};
            String type = (String) JOptionPane.showInputDialog(null, "Select a Contribution Type", "Add Contribution",
                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            if (type != null) {
                CardLayout layout = (CardLayout) cards.getLayout();
                layout.show(cards, type);
                switch (type) {
                    case "Actor": {
                        Actor newActor = new Actor("", new ArrayList<>(), null, "IMDB\\src\\main\\java\\org\\example\\GUI\\images\\unknownImage.png");
                        newContribution.set(true);
                        updateDisplay(newActor);
                        break;
                    }
                    case "Movie": {
                        Movie newMovie = new Movie("", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                                "", 0.0, 0, 0, "IMDB\\src\\main\\java\\org\\example\\GUI\\images\\unknownImage.png");
                        newContribution.set(true);
                        updateDisplay(newMovie);
                        break;
                    }
                    case "Series": {
                        Series newSeries = new Series("", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                                "", 0.0, 0, 0, new LinkedHashMap<>(), "IMDB\\src\\main\\java\\org\\example\\GUI\\images\\unknownImage.png");
                        newContribution.set(true);
                        updateDisplay(newSeries);
                        break;
                    }
                }
                IMDB.getInstance().loggedInUser.updateExperience(new RewardContribution().calculateExperience());
                updateContributions();
            }
        });

        // Delete contribution button
        JButton deleteContribution = new JButton("Delete");
        deleteContribution.setPreferredSize(new Dimension(140, 30));
        deleteContribution.addActionListener(e -> {
            int selectedIndex = contributionsJList.getSelectedIndex();

            if (selectedIndex != -1) {
                // Remove contribution from system
                if (selectedIndex < ((Staff<?>) IMDB.getInstance().loggedInUser).contributions.size()) {
                    if (contributions.get(selectedIndex) instanceof Production) {
                        ((Staff<?>) IMDB.getInstance().loggedInUser).removeProductionSystem(((Production) contributions.get(selectedIndex)).title);
                    } else {
                        ((Staff<?>) IMDB.getInstance().loggedInUser).removeActorSystem(((Actor) contributions.get(selectedIndex)).name);
                    }
                } else {
                    Admin.common_contributions.remove(contributions.get(selectedIndex));
                }
                updateContributions();
                CardLayout layout = (CardLayout) cards.getLayout();
                layout.show(cards, "Empty");
            }
        });

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 3, 20, 10));

        buttonsPanel.add(backButton);
        buttonsPanel.add(addContribution);
        buttonsPanel.add(deleteContribution);

        add(buttonsPanel, BorderLayout.NORTH);
        add(cards, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.WEST);
    }
    private void updateDisplay(Object contribution) {
        if (contribution instanceof Actor) {
            actorPanel.removeAll();
            JPanel newActorPanel = createPanel(contribution);
            actorPanel.add(newActorPanel);
            actorPanel.revalidate();
            actorPanel.repaint();
        } else if (contribution instanceof Movie) {
            moviePanel.removeAll();
            JPanel newMoviePanel = createPanel(contribution);
            moviePanel.add(newMoviePanel);
            moviePanel.revalidate();
            moviePanel.repaint();
        } else {
            seriesPanel.removeAll();
            JPanel newSeriesPanel = createPanel(contribution);
            seriesPanel.add(newSeriesPanel);
            seriesPanel.revalidate();
            seriesPanel.repaint();
        }
    }
    public void updateContributions() {
        User user = IMDB.getInstance().loggedInUser;
        listModel.clear();
        contributions.clear();

        contributions.addAll(((Staff<?>) user).contributions);
        if (user instanceof Admin) {
            contributions.addAll(Admin.common_contributions);
        }

        for (Object contribution : contributions) {
            if (contribution instanceof Production) {
                listModel.addElement(((Production) contribution).title);
            } else {
                listModel.addElement(((Actor) contribution).name);
            }
        }
    }
    private JPanel createPanel(Object contribution) {
        JPanel panel = null;
        if (contribution instanceof Actor) {
            panel = new JPanel(new GridBagLayout());
            panel.setPreferredSize(new Dimension(300, 200));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel nameLabel = new JLabel("Name:");
            JTextField nameField = new JTextField(15);
            nameField.setText(((Actor) contribution).name);
            nameLabel.setPreferredSize(new Dimension(100, 20));
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(nameLabel, gbc);
            gbc.gridx = 1;
            panel.add(nameField, gbc);

            JLabel performancesLabel = new JLabel("Performances:");
            performancesLabel.setPreferredSize(new Dimension(100, 20));
            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add(performancesLabel, gbc);

            JPanel performancesPanel = new JPanel(new GridLayout(0, 1));
            JScrollPane scrollPane = new JScrollPane(performancesPanel);
            scrollPane.setPreferredSize(new Dimension(350, 100));
            gbc.gridx = 1;
            panel.add(scrollPane, gbc);

            // Performances
            List<JPanel> performancePairPanels = new ArrayList<>();
            String[] types = {"Movie", "Series"};
            for (String[] performance : ((Actor) contribution).performances) {
                JComboBox<String> typeField = new JComboBox<>(types);
                JLabel typeLabel = new JLabel("Type");
                typeField.setSelectedItem(performance[1]);

                JTextField titleField = new JTextField(15);
                JLabel titleLabel = new JLabel("Title");
                titleField.setText(performance[0]);

                // Panel to hold type and title fields together
                JPanel performancePairPanel = new JPanel(new FlowLayout());
                performancePairPanel.add(typeLabel);
                performancePairPanel.add(typeField);
                performancePairPanel.add(titleLabel);
                performancePairPanel.add(titleField);

                JButton removeButton = new JButton("Remove");
                removeButton.addActionListener(e -> {
                    performancesPanel.remove(performancePairPanel);
                    performancePairPanels.remove(performancePairPanel);
                    performancesPanel.revalidate();
                    performancesPanel.repaint();
                });
                performancePairPanel.add(removeButton);

                performancesPanel.add(performancePairPanel);

                performancePairPanels.add(performancePairPanel);

                performancesPanel.revalidate();
                performancesPanel.repaint();
            }

            JButton addPerformanceButton = new JButton("Add Performance");
            addPerformanceButton.addActionListener(e -> {
                JComboBox<String> typeField = new JComboBox<>(types);
                JLabel typeLabel = new JLabel("Type");

                JTextField titleField = new JTextField(15);
                JLabel titleLabel = new JLabel("Title");

                // Panel to hold type and title fields together
                JPanel performancePairPanel = new JPanel(new FlowLayout());
                performancePairPanel.add(typeLabel);
                performancePairPanel.add(typeField);
                performancePairPanel.add(titleLabel);
                performancePairPanel.add(titleField);

                JButton removeButton = new JButton("Remove");
                removeButton.addActionListener(ee -> {
                    performancesPanel.remove(performancePairPanel);
                    performancePairPanels.remove(performancePairPanel);
                    performancesPanel.revalidate();
                    performancesPanel.repaint();
                });
                performancePairPanel.add(removeButton);

                performancesPanel.add(performancePairPanel);

                performancePairPanels.add(performancePairPanel);

                performancesPanel.revalidate();
                performancesPanel.repaint();
                typeField.requestFocusInWindow();
            });
            gbc.gridx = 1;
            gbc.gridy = 2;
            panel.add(addPerformanceButton, gbc);

            // Biography
            JLabel biographyLabel = new JLabel("Biography:");
            JTextArea biographyArea = new JTextArea(4, 15);
            biographyArea.setText(((Actor) contribution).biography);
            biographyLabel.setPreferredSize(new Dimension(100, 20));
            biographyArea.setLineWrap(true);
            biographyArea.setWrapStyleWord(true);

            JScrollPane biographyScrollPane = new JScrollPane(biographyArea);
            biographyScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
            gbc.gridx = 0;
            gbc.gridy = 3;
            panel.add(biographyLabel, gbc);
            gbc.gridx = 1;
            panel.add(biographyArea, gbc);

            JButton submitButton = new JButton("Submit");
            submitButton.addActionListener(e -> {
                String name = nameField.getText();
                String biography = biographyArea.getText();

                // Get performances
                ArrayList<String[]> performances = new ArrayList<>();

                boolean validData = !contributionExists(name) || name.equals(((Actor) contribution).name);
                if (validData) {
                    for (JPanel performancePairPanel : performancePairPanels) {
                        boolean ok = false;
                        String[] performance = new String[2];
                        for (Component component : performancePairPanel.getComponents()) {
                            if (component instanceof JComboBox) {
                                JComboBox<String> typeField = (JComboBox<String>) component;
                                performance[1] = (String) typeField.getSelectedItem();
                            } else if (component instanceof JTextField) {
                                JTextField titleField = (JTextField) component;
                                performance[0] = titleField.getText();
                            }
                        }
                        for (Production p : IMDB.getInstance().getProductions()) {
                            if (performance[1].equals("Movie") && p instanceof Movie || performance[1].equals("Series") && p instanceof Series) {
                                if (p.title.equals(performance[0])) {
                                    ok = true;
                                    break;
                                }
                            }
                        }
                        if (!ok) {
                            validData = false;
                            JOptionPane.showMessageDialog(null, "Invalid performance: (" + performance[1] + ") " + performance[0], "Error", JOptionPane.ERROR_MESSAGE);
                            break;
                        }
                        ok = true;
                        for (String[] p : performances) {
                            if (p[0].equals(performance[0])) {
                                ok = false;
                                break;
                            }
                        }
                        if (ok) {
                            performances.add(performance);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Actor \"" + name + "\" already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                if (validData) {
                    ((Actor) contribution).name = name;
                    ((Actor) contribution).performances = performances;
                    ((Actor) contribution).biography = biography.isEmpty() ? null : biography;
                    JOptionPane.showMessageDialog(null, "Actor updated/added successfully!");
                    if (newContribution.get()) {
                        ((Staff<?>) IMDB.getInstance().loggedInUser).addActorSystem((Actor) contribution);
                        newContribution.set(false);
                    }
                    updateContributions();
                }
            });
            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(submitButton, gbc);
        } else if (contribution instanceof Movie) {
            panel = new JPanel(new GridBagLayout());
            panel.setPreferredSize(new Dimension(300, 200));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Title
            JLabel nameLabel = new JLabel("Title:");
            JTextField nameField = new JTextField(15);
            nameField.setText(((Movie) contribution).title);
            nameLabel.setPreferredSize(new Dimension(100, 20));
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(nameLabel, gbc);
            gbc.gridx = 1;
            panel.add(nameField, gbc);

            // Directors
            JLabel directorsLabel = new JLabel("Directors:");
            directorsLabel.setPreferredSize(new Dimension(100, 20));
            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add(directorsLabel, gbc);

            JPanel directorsPanel = new JPanel(new GridLayout(0, 1));
            JScrollPane scrollDirectors = new JScrollPane(directorsPanel);
            scrollDirectors.setPreferredSize(new Dimension(350, 100));
            gbc.gridx = 1;
            panel.add(scrollDirectors, gbc);


            List<JPanel> directorPairPanels = new ArrayList<>();
            for (String director : ((Movie) contribution).directors) {
                JTextField directorField = new JTextField(15);
                JLabel directorLabel = new JLabel("Name");
                directorField.setText(director);

                JPanel directorPairPanel = new JPanel(new FlowLayout());
                directorPairPanel.add(directorLabel);
                directorPairPanel.add(directorField);

                JButton removeButton = new JButton("Remove");
                removeButton.addActionListener(e -> {
                    directorsPanel.remove(directorPairPanel);
                    directorPairPanels.remove(directorPairPanel);
                    directorsPanel.revalidate();
                    directorsPanel.repaint();
                });
                directorPairPanel.add(removeButton);

                directorsPanel.add(directorPairPanel);
                directorPairPanels.add(directorPairPanel);

                directorsPanel.revalidate();
                directorsPanel.repaint();
            }

            JButton addDirectorButton = new JButton("Add Director");
            addDirectorButton.addActionListener(e -> {
                JTextField directorField = new JTextField(15);
                JLabel directorLabel = new JLabel("Name");

                JPanel directorPairPanel = new JPanel(new FlowLayout());
                directorPairPanel.add(directorLabel);
                directorPairPanel.add(directorField);

                directorsPanel.add(directorPairPanel);
                directorPairPanels.add(directorPairPanel);

                JButton removeButton = new JButton("Remove");
                removeButton.addActionListener(ee -> {
                    directorsPanel.remove(directorPairPanel);
                    directorPairPanels.remove(directorPairPanel);
                    directorsPanel.revalidate();
                    directorsPanel.repaint();
                });
                directorPairPanel.add(removeButton);

                directorsPanel.revalidate();
                directorsPanel.repaint();
                directorField.requestFocusInWindow();
            });
            gbc.gridx = 1;
            gbc.gridy = 2;
            panel.add(addDirectorButton, gbc);

            // Actors
            JLabel actorsLabel = new JLabel("Actors:");
            actorsLabel.setPreferredSize(new Dimension(100, 20));
            gbc.gridx = 0;
            gbc.gridy = 3;
            panel.add(actorsLabel, gbc);

            JPanel actorsPanel = new JPanel(new GridLayout(0, 1));
            JScrollPane scrollActors = new JScrollPane(actorsPanel);
            scrollActors.setPreferredSize(new Dimension(350, 100));
            gbc.gridx = 1;
            panel.add(scrollActors, gbc);

            List<JPanel> actorPairPanels = new ArrayList<>();
            for (String actor : ((Movie) contribution).actors) {
                JTextField actorField = new JTextField(15);
                JLabel actorLabel = new JLabel("Name");
                actorField.setText(actor);

                JPanel actorPairPanel = new JPanel(new FlowLayout());
                actorPairPanel.add(actorLabel);
                actorPairPanel.add(actorField);

                JButton removeButton = new JButton("Remove");
                removeButton.addActionListener(e -> {
                    actorsPanel.remove(actorPairPanel);
                    actorPairPanels.remove(actorPairPanel);
                    actorsPanel.revalidate();
                    actorsPanel.repaint();
                });
                actorPairPanel.add(removeButton);

                actorsPanel.add(actorPairPanel);
                actorPairPanels.add(actorPairPanel);

                actorsPanel.revalidate();
                actorsPanel.repaint();
            }

            JButton addActorButton = new JButton("Add Actor");
            addActorButton.addActionListener(e -> {
                JTextField actorField = new JTextField(15);
                JLabel actorLabel = new JLabel("Name");

                JPanel actorPairPanel = new JPanel(new FlowLayout());
                actorPairPanel.add(actorLabel);
                actorPairPanel.add(actorField);

                JButton removeButton = new JButton("Remove");
                removeButton.addActionListener(ee -> {
                    actorsPanel.remove(actorPairPanel);
                    actorPairPanels.remove(actorPairPanel);
                    actorsPanel.revalidate();
                    actorsPanel.repaint();
                });
                actorPairPanel.add(removeButton);

                actorsPanel.add(actorPairPanel);
                actorPairPanels.add(actorPairPanel);

                actorsPanel.revalidate();
                actorsPanel.repaint();
                actorField.requestFocusInWindow();
            });
            gbc.gridx = 1;
            gbc.gridy = 4;
            panel.add(addActorButton, gbc);

            // Genres
            JLabel genresLabel = new JLabel("Genres:");
            genresLabel.setPreferredSize(new Dimension(100, 20));
            gbc.gridx = 0;
            gbc.gridy = 5;
            panel.add(genresLabel, gbc);

            JList<Genre> genresJList = new JList<>(Genre.values());
            int[] selectedIndexes = new int[Genre.values().length];
            int j = 0;
            for (Genre genre : ((Movie) contribution).genres) {
                for (int i = 0; i < Genre.values().length; i++) {
                    if (genre.equals(Genre.values()[i])) {
                        selectedIndexes[j++] = i;
                        break;
                    }
                }
            }
            genresJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            genresJList.setSelectedIndices(selectedIndexes);

            JScrollPane scrollGenres = new JScrollPane(genresJList);
            gbc.gridx = 1;
            panel.add(scrollGenres, gbc);

            // Plot
            JLabel plotLabel = new JLabel("Plot:");
            JTextField plotField = new JTextField(15);
            plotField.setText(((Movie) contribution).plot);
            plotLabel.setPreferredSize(new Dimension(100, 20));
            gbc.gridx = 0;
            gbc.gridy = 6;
            panel.add(plotLabel, gbc);
            gbc.gridx = 1;
            panel.add(plotField, gbc);

            // Duration
            JLabel durationLabel = new JLabel("Duration:");
            SpinnerModel durationModel = new SpinnerNumberModel(((Movie) contribution).duration, 0, 1000, 1);
            JSpinner durationSpinner = new JSpinner(durationModel);
            durationLabel.setPreferredSize(new Dimension(100, 20));
            gbc.gridx = 0;
            gbc.gridy = 7;
            panel.add(durationLabel, gbc);
            gbc.gridx = 1;
            panel.add(durationSpinner, gbc);

            // Release year
            JLabel yearLabel = new JLabel("Release year:");
            SpinnerModel yearModel = new SpinnerNumberModel(((Movie) contribution).releaseYear, 0, 10000, 1);
            JSpinner yearSpinner = new JSpinner(yearModel);
            yearLabel.setPreferredSize(new Dimension(100, 20));
            gbc.gridx = 0;
            gbc.gridy = 8;
            panel.add(yearLabel, gbc);
            gbc.gridx = 1;
            panel.add(yearSpinner, gbc);

            // Submit
            JButton submitButton = new JButton("Submit");
            submitButton.addActionListener(e -> {
                String title = nameField.getText();

                List<String> directors = new ArrayList<>();
                List<String> actors = new ArrayList<>();
                List<Genre> genres = new ArrayList<>();
                int duration = 0;
                int releaseYear = 0;
                String plot = "";

                boolean validData = !contributionExists(title) || title.equals(((Movie) contribution).title);
                if (validData) {
                    // Get directors
                    for (JPanel directorPairPanel : directorPairPanels) {
                        String director = "";
                        for (Component component : directorPairPanel.getComponents()) {
                            if (component instanceof JTextField) {
                                JTextField directorField = (JTextField) component;
                                director = directorField.getText();
                                break;
                            }
                        }
                        boolean ok = true;
                        for (String d : directors) {
                            if (d.equals(director)) {
                                ok = false;
                                break;
                            }
                        }
                        if (ok) {
                            directors.add(director);
                        }
                    }

                    // Get actors
                    for (JPanel actorPairPanel : actorPairPanels) {
                        String actor = "";
                        for (Component component : actorPairPanel.getComponents()) {
                            if (component instanceof JTextField) {
                                JTextField actorField = (JTextField) component;
                                actor = actorField.getText();
                                break;
                            }
                        }
                        boolean ok = true;
                        if (contributionExists(actor)) {
                            for (String a : actors) {
                                if (a.equals(actor)) {
                                    ok = false;
                                    break;
                                }
                            }
                            if (ok) {
                                actors.add(actor);
                            }
                        } else {
                            validData = false;
                            JOptionPane.showMessageDialog(null, "Actor \"" + actor + "\" does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
                            break;
                        }
                    }

                    // Get genres
                    genres = genresJList.getSelectedValuesList();

                    // Get plot
                    plot = plotField.getText();

                    // Get duration
                    Object value = durationSpinner.getValue();
                    boolean ok = true;
                    if (value instanceof Integer) {
                        duration = (int) value;
                        if (duration <= 0) {
                            ok = false;
                        }
                    } else {
                        ok = false;
                    }
                    if (!ok) {
                        validData = false;
                        JOptionPane.showMessageDialog(null, "Invalid duration!", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    // Get release year
                    value = yearSpinner.getValue();
                    ok = true;
                    if (value instanceof Integer) {
                        releaseYear = (int) value;
                        if (releaseYear < 0) {
                            ok = false;
                        }
                    } else {
                        ok = false;
                    }
                    if (!ok) {
                        validData = false;
                        JOptionPane.showMessageDialog(null, "Invalid year!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Production \"" + title + "\" already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                }

                if (validData) {
                    ((Movie) contribution).title = title;
                    ((Movie) contribution).directors = directors;
                    ((Movie) contribution).actors = actors;
                    ((Movie) contribution).genres = genres;
                    ((Movie) contribution).plot = plot;
                    ((Movie) contribution).duration = duration;
                    ((Movie) contribution).releaseYear = releaseYear;
                    JOptionPane.showMessageDialog(null, "Movie updated/added successfully!");
                    if (newContribution.get()) {
                        ((Staff<?>) IMDB.getInstance().loggedInUser).addProductionSystem((Movie) contribution);
                        newContribution.set(false);
                    }
                    updateContributions();
                }
            });
            gbc.gridx = 0;
            gbc.gridy = 9;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(submitButton, gbc);
        } else if (contribution instanceof Series) {
            CardLayout cardLayout = new CardLayout();
            panel = new JPanel(new BorderLayout());
            JPanel cards = new JPanel(cardLayout);
            JPanel panel1 = new JPanel(new GridBagLayout());
            JPanel panel2 = new JPanel(new GridBagLayout());
            cards.add(panel1, "Panel1");
            cards.add(panel2, "Panel2");
            panel.add(cards, BorderLayout.CENTER);
            cardLayout.show(cards, "Panel1");
            panel1.setPreferredSize(new Dimension(300, 200));
            panel2.setPreferredSize(new Dimension(300, 200));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Title
            JLabel nameLabel = new JLabel("Title:");
            JTextField nameField = new JTextField(15);
            nameField.setText(((Series) contribution).title);
            nameLabel.setPreferredSize(new Dimension(100, 20));
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel1.add(nameLabel, gbc);
            gbc.gridx = 1;
            panel1.add(nameField, gbc);

            // Directors
            JLabel directorsLabel = new JLabel("Directors:");
            directorsLabel.setPreferredSize(new Dimension(100, 20));
            gbc.gridx = 0;
            gbc.gridy = 1;
            panel1.add(directorsLabel, gbc);

            JPanel directorsPanel = new JPanel(new GridLayout(0, 1));
            JScrollPane scrollDirectors = new JScrollPane(directorsPanel);
            scrollDirectors.setPreferredSize(new Dimension(350, 100));
            gbc.gridx = 1;
            panel1.add(scrollDirectors, gbc);


            List<JPanel> directorPairPanels = new ArrayList<>();
            for (String director : ((Series) contribution).directors) {
                JTextField directorField = new JTextField(15);
                JLabel directorLabel = new JLabel("Name");
                directorField.setText(director);

                JPanel directorPairPanel = new JPanel(new FlowLayout());
                directorPairPanel.add(directorLabel);
                directorPairPanel.add(directorField);

                JButton removeButton = new JButton("Remove");
                removeButton.addActionListener(e -> {
                    directorsPanel.remove(directorPairPanel);
                    directorPairPanels.remove(directorPairPanel);
                    directorsPanel.revalidate();
                    directorsPanel.repaint();
                });
                directorPairPanel.add(removeButton);

                directorsPanel.add(directorPairPanel);
                directorPairPanels.add(directorPairPanel);

                directorsPanel.revalidate();
                directorsPanel.repaint();
            }

            JButton addDirectorButton = new JButton("Add Director");
            addDirectorButton.addActionListener(e -> {
                JTextField directorField = new JTextField(15);
                JLabel directorLabel = new JLabel("Name");

                JPanel directorPairPanel = new JPanel(new FlowLayout());
                directorPairPanel.add(directorLabel);
                directorPairPanel.add(directorField);

                JButton removeButton = new JButton("Remove");
                removeButton.addActionListener(ee -> {
                    directorsPanel.remove(directorPairPanel);
                    directorPairPanels.remove(directorPairPanel);
                    directorsPanel.revalidate();
                    directorsPanel.repaint();
                });
                directorPairPanel.add(removeButton);

                directorsPanel.add(directorPairPanel);
                directorPairPanels.add(directorPairPanel);

                directorsPanel.revalidate();
                directorsPanel.repaint();
                directorField.requestFocusInWindow();
            });
            gbc.gridx = 1;
            gbc.gridy = 2;
            panel1.add(addDirectorButton, gbc);

            // Actors
            JLabel actorsLabel = new JLabel("Actors:");
            actorsLabel.setPreferredSize(new Dimension(100, 20));
            gbc.gridx = 0;
            gbc.gridy = 3;
            panel1.add(actorsLabel, gbc);

            JPanel actorsPanel = new JPanel(new GridLayout(0, 1));
            JScrollPane scrollActors = new JScrollPane(actorsPanel);
            scrollActors.setPreferredSize(new Dimension(350, 100));
            gbc.gridx = 1;
            panel1.add(scrollActors, gbc);
            List<JPanel> actorPairPanels = new ArrayList<>();
            for (String actor : ((Series) contribution).actors) {
                JTextField actorField = new JTextField(15);
                JLabel actorLabel = new JLabel("Name");
                actorField.setText(actor);

                JPanel actorPairPanel = new JPanel(new FlowLayout());
                actorPairPanel.add(actorLabel);
                actorPairPanel.add(actorField);

                JButton removeButton = new JButton("Remove");
                removeButton.addActionListener(e -> {
                    actorsPanel.remove(actorPairPanel);
                    actorPairPanels.remove(actorPairPanel);
                    actorsPanel.revalidate();
                    actorsPanel.repaint();
                });
                actorPairPanel.add(removeButton);

                actorsPanel.add(actorPairPanel);
                actorPairPanels.add(actorPairPanel);

                actorsPanel.revalidate();
                actorsPanel.repaint();
            }

            JButton addActorButton = new JButton("Add Actor");
            addActorButton.addActionListener(e -> {
                JTextField actorField = new JTextField(15);
                JLabel actorLabel = new JLabel("Name");

                JPanel actorPairPanel = new JPanel(new FlowLayout());
                actorPairPanel.add(actorLabel);
                actorPairPanel.add(actorField);

                JButton removeButton = new JButton("Remove");
                removeButton.addActionListener(ee -> {
                    actorsPanel.remove(actorPairPanel);
                    actorPairPanels.remove(actorPairPanel);
                    actorsPanel.revalidate();
                    actorsPanel.repaint();
                });
                actorPairPanel.add(removeButton);

                actorsPanel.add(actorPairPanel);
                actorPairPanels.add(actorPairPanel);

                actorsPanel.revalidate();
                actorsPanel.repaint();
                actorField.requestFocusInWindow();
            });
            gbc.gridx = 1;
            gbc.gridy = 4;
            panel1.add(addActorButton, gbc);

            // Genres
            JLabel genresLabel = new JLabel("Genres:");
            genresLabel.setPreferredSize(new Dimension(100, 20));
            gbc.gridx = 0;
            gbc.gridy = 5;
            panel1.add(genresLabel, gbc);

            JList<Genre> genresJList = new JList<>(Genre.values());
            int[] selectedIndexes = new int[Genre.values().length];
            int j = 0;
            for (Genre genre : ((Series) contribution).genres) {
                for (int i = 0; i < Genre.values().length; i++) {
                    if (genre.equals(Genre.values()[i])) {
                        selectedIndexes[j++] = i;
                        break;
                    }
                }
            }
            genresJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            genresJList.setSelectedIndices(selectedIndexes);

            JScrollPane scrollGenres = new JScrollPane(genresJList);
            gbc.gridx = 1;
            panel1.add(scrollGenres, gbc);

            // Plot
            JLabel plotLabel = new JLabel("Plot:");
            JTextField plotField = new JTextField(15);
            plotField.setText(((Series) contribution).plot);
            plotLabel.setPreferredSize(new Dimension(100, 20));
            gbc.gridx = 0;
            gbc.gridy = 6;
            panel1.add(plotLabel, gbc);
            gbc.gridx = 1;
            panel1.add(plotField, gbc);

            // Release year
            JLabel yearLabel = new JLabel("Release year:");
            SpinnerModel yearModel = new SpinnerNumberModel(((Series) contribution).releaseYear, 0, 10000, 1);
            JSpinner yearSpinner = new JSpinner(yearModel);
            yearLabel.setPreferredSize(new Dimension(100, 20));
            gbc.gridx = 0;
            gbc.gridy = 8;
            panel1.add(yearLabel, gbc);
            gbc.gridx = 1;
            panel1.add(yearSpinner, gbc);

            // Seasons
            JLabel seasonsLabel = new JLabel("Seasons:");
            seasonsLabel.setPreferredSize(new Dimension(100, 20));
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel2.add(seasonsLabel, gbc);

            JPanel seasonsPanel = new JPanel(new GridLayout(0, 1));
            JScrollPane scrollSeasons = new JScrollPane(seasonsPanel);
            scrollSeasons.setPreferredSize(new Dimension(700, 400));
            gbc.gridx = 1;
            panel2.add(scrollSeasons, gbc);

            Map<JPanel, List<JPanel>> seasonPairPanels = new LinkedHashMap<>();
            for (Map.Entry<String, List<Episode>> entry : ((Series) contribution).getSeasons().entrySet()) {
                String seasonName = entry.getKey();
                List<Episode> episodes = entry.getValue();
                JPanel seasonPanel = new JPanel(new BorderLayout());

                JTextField seasonTitleField = new JTextField(15);
                JLabel seasonLabel = new JLabel("Title");
                seasonLabel.setPreferredSize(new Dimension(100, 20));
                seasonPanel.add(seasonLabel, BorderLayout.WEST);
                seasonTitleField.setText(seasonName);

                JLabel episodesLabel = new JLabel("Episodes:");
                seasonLabel.setPreferredSize(new Dimension(100, 20));
                seasonPanel.add(episodesLabel, BorderLayout.NORTH);

                JPanel episodesPanel = new JPanel(new GridLayout(0, 1));
                JScrollPane scrollEpisodes = new JScrollPane(episodesPanel);
                scrollEpisodes.setPreferredSize(new Dimension(350, 100));
                seasonPanel.add(scrollEpisodes, BorderLayout.CENTER);

                List<JPanel> episodePairPanels = new ArrayList<>();
                for (Episode episode : episodes) {
                    JTextField titleField = new JTextField(15);
                    JLabel titleLabel = new JLabel("Title");
                    SpinnerModel durationModel = new SpinnerNumberModel(episode.duration, 0, 1000, 1);
                    JSpinner durationSpinner = new JSpinner(durationModel);
                    JLabel durationLabel = new JLabel("Duration");
                    titleField.setText(episode.name);

                    JPanel episodePairPanel = new JPanel(new FlowLayout());
                    episodePairPanel.add(titleLabel);
                    episodePairPanel.add(titleField);
                    episodePairPanel.add(durationLabel);
                    episodePairPanel.add(durationSpinner);

                    episodesPanel.add(episodePairPanel);
                    episodePairPanels.add(episodePairPanel);

                    episodesPanel.revalidate();
                    episodesPanel.repaint();
                }

                JButton addEpisodeButton = new JButton("Add Episode");
                addEpisodeButton.addActionListener(e -> {
                    JTextField titleField = new JTextField(15);
                    JLabel titleLabel = new JLabel("Title");
                    SpinnerModel durationModel = new SpinnerNumberModel(0, 0, 1000, 1);
                    JSpinner durationSpinner = new JSpinner(durationModel);
                    JLabel durationLabel = new JLabel("Duration");

                    JPanel episodePairPanel = new JPanel(new FlowLayout());
                    episodePairPanel.add(titleLabel);
                    episodePairPanel.add(titleField);
                    episodePairPanel.add(durationLabel);
                    episodePairPanel.add(durationSpinner);

                    JButton removeButton = new JButton("Remove");
                    removeButton.addActionListener(ee -> {
                        episodesPanel.remove(episodePairPanel);
                        episodePairPanels.remove(episodePairPanel);
                        episodesPanel.revalidate();
                        episodesPanel.repaint();
                    });
                    episodePairPanel.add(removeButton);

                    episodesPanel.add(episodePairPanel);
                    episodePairPanels.add(episodePairPanel);

                    episodesPanel.revalidate();
                    episodesPanel.repaint();
                    titleField.requestFocusInWindow();
                });
                seasonPanel.add(addEpisodeButton, BorderLayout.SOUTH);

                JPanel seasonPairPanel = new JPanel(new FlowLayout());
                seasonPairPanel.add(seasonLabel);
                seasonPairPanel.add(seasonTitleField);
                seasonPairPanel.add(seasonPanel);

                JButton removeButton = new JButton("Remove");
                removeButton.addActionListener(e -> {
                    seasonsPanel.remove(seasonPairPanel);
                    seasonPairPanels.remove(seasonPairPanel);
                    seasonsPanel.revalidate();
                    seasonsPanel.repaint();
                });
                seasonPairPanel.add(removeButton);

                seasonPairPanels.put(seasonPairPanel, episodePairPanels);

                seasonsPanel.add(seasonPairPanel);
                seasonsPanel.revalidate();
                seasonsPanel.repaint();
            }

            JButton addSeasonButton = new JButton("Add Season");
            addSeasonButton.addActionListener(e -> {
                JPanel seasonPanel = new JPanel(new BorderLayout());

                JTextField seasonTitleField = new JTextField(15);
                JLabel seasonLabel = new JLabel("Title");
                seasonLabel.setPreferredSize(new Dimension(100, 20));
                seasonPanel.add(seasonLabel, BorderLayout.WEST);

                JLabel episodesLabel = new JLabel("Episodes:");
                seasonLabel.setPreferredSize(new Dimension(100, 20));
                seasonPanel.add(episodesLabel, BorderLayout.NORTH);

                JPanel episodesPanel = new JPanel(new GridLayout(0, 1));
                JScrollPane scrollEpisodes = new JScrollPane(episodesPanel);
                scrollEpisodes.setPreferredSize(new Dimension(350, 100));
                seasonPanel.add(scrollEpisodes, BorderLayout.CENTER);

                List<JPanel> episodePairPanels = new ArrayList<>();

                JButton addEpisodeButton = new JButton("Add Episode");
                addEpisodeButton.addActionListener(ee -> {
                    JTextField titleField = new JTextField(15);
                    JLabel titleLabel = new JLabel("Title");
                    SpinnerModel durationModel = new SpinnerNumberModel(0, 0, 1000, 1);
                    JSpinner durationSpinner = new JSpinner(durationModel);
                    JLabel durationLabel = new JLabel("Duration");

                    JPanel episodePairPanel = new JPanel(new FlowLayout());
                    episodePairPanel.add(titleLabel);
                    episodePairPanel.add(titleField);
                    episodePairPanel.add(durationLabel);
                    episodePairPanel.add(durationSpinner);

                    JButton removeButton = new JButton("Remove");
                    removeButton.addActionListener(eee -> {
                        episodesPanel.remove(episodePairPanel);
                        episodePairPanels.remove(episodePairPanel);
                        episodesPanel.revalidate();
                        episodesPanel.repaint();
                    });
                    episodePairPanel.add(removeButton);

                    episodesPanel.add(episodePairPanel);
                    episodePairPanels.add(episodePairPanel);

                    episodesPanel.revalidate();
                    episodesPanel.repaint();
                    titleField.requestFocusInWindow();
                });
                seasonPanel.add(addEpisodeButton, BorderLayout.SOUTH);

                JPanel seasonPairPanel = new JPanel(new FlowLayout());
                seasonPairPanel.add(seasonLabel);
                seasonPairPanel.add(seasonTitleField);
                seasonPairPanel.add(seasonPanel);

                JButton removeButton = new JButton("Remove");
                removeButton.addActionListener(ee -> {
                    seasonsPanel.remove(seasonPairPanel);
                    seasonPairPanels.remove(seasonPairPanel);
                    seasonsPanel.revalidate();
                    seasonsPanel.repaint();
                });
                seasonPairPanel.add(removeButton);

                seasonPairPanels.put(seasonPairPanel, episodePairPanels);

                seasonsPanel.add(seasonPairPanel);
                seasonsPanel.revalidate();
                seasonsPanel.repaint();
                seasonTitleField.requestFocusInWindow();
            });
            gbc.gridy = 1;
            panel2.add(addSeasonButton, gbc);

            // Switch panel
            JButton switchButton = new JButton("Switch Panel");
            switchButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cardLayout.next(cards);
                }
            });
            JPanel buttonsPanel = new JPanel();
            buttonsPanel.add(switchButton, BorderLayout.SOUTH);

            // Submit
            JButton submitButton = new JButton("Submit");
            submitButton.addActionListener(e -> {
                String title = nameField.getText();

                List<String> directors = new ArrayList<>();
                List<String> actors = new ArrayList<>();
                List<Genre> genres = new ArrayList<>();
                int numSeasons = 0;
                int releaseYear = 0;
                String plot = "";
                Map<String, List<Episode>> seasons = new LinkedHashMap<>();

                boolean validData = !contributionExists(title) || title.equals(((Series) contribution).title);
                if (validData) {
                    // Get directors
                    for (JPanel directorPairPanel : directorPairPanels) {
                        String director = "";
                        for (Component component : directorPairPanel.getComponents()) {
                            if (component instanceof JTextField) {
                                JTextField directorField = (JTextField) component;
                                director = directorField.getText();
                                break;
                            }
                        }
                        boolean ok = true;
                        for (String d : directors) {
                            if (d.equals(director)) {
                                ok = false;
                                break;
                            }
                        }
                        if (ok) {
                            directors.add(director);
                        }
                    }

                    // Get actors
                    for (JPanel actorPairPanel : actorPairPanels) {
                        String actor = "";
                        for (Component component : actorPairPanel.getComponents()) {
                            if (component instanceof JTextField) {
                                JTextField actorField = (JTextField) component;
                                actor = actorField.getText();
                                break;
                            }
                        }
                        boolean ok = true;
                        if (contributionExists(actor)) {
                            for (String a : actors) {
                                if (a.equals(actor)) {
                                    ok = false;
                                    break;
                                }
                            }
                            if (ok) {
                                actors.add(actor);
                            }
                        } else {
                            validData = false;
                            JOptionPane.showMessageDialog(null, "Actor \"" + actor + "\" does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
                            break;
                        }
                    }

                    // Get genres
                    genres = genresJList.getSelectedValuesList();

                    // Get plot
                    plot = plotField.getText();

                    // Get release year
                    Object value = yearSpinner.getValue();
                    boolean ok = true;
                    if (value instanceof Integer) {
                        releaseYear = (int) value;
                        if (releaseYear < 0) {
                            ok = false;
                        }
                    } else {
                        ok = false;
                    }
                    if (!ok) {
                        validData = false;
                        JOptionPane.showMessageDialog(null, "Invalid year!", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    // Get seasons
                    for (Map.Entry<JPanel, List<JPanel>> entry : seasonPairPanels.entrySet()) {
                        JPanel seasonPanel = entry.getKey();
                        List<JPanel> episodesPanels = entry.getValue();

                        String seasonTitle = "";
                        for (Component component : seasonPanel.getComponents()) {
                            if (component instanceof JTextField) {
                                JTextField titleField = (JTextField) component;
                                seasonTitle = titleField.getText();
                                break;
                            }
                        }

                        List<Episode> episodes = new ArrayList<>();
                        for (JPanel p : episodesPanels) {
                            String epTitle = "";
                            int epDuration = 0;
                            for (Component component : p.getComponents()) {
                                if (component instanceof JTextField) {
                                    JTextField episodeTitle = (JTextField) component;
                                    epTitle = episodeTitle.getText();
                                } else if (component instanceof JSpinner) {
                                    JSpinner episodeDuration = (JSpinner) component;
                                    value = episodeDuration.getValue();
                                    ok = true;
                                    if (value instanceof Integer) {
                                        epDuration = (int) value;
                                        if (epDuration <= 0) {
                                            ok = false;
                                        }
                                    } else {
                                        ok = false;
                                    }
                                    if (!ok) {
                                        validData = false;
                                        JOptionPane.showMessageDialog(null, "Invalid duration! (" + epTitle + ")", "Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            }

                            episodes.add(new Episode(epTitle, epDuration));
                        }

                        seasons.put(seasonTitle, episodes);
                    }

                    numSeasons = seasons.size();
                } else {
                    JOptionPane.showMessageDialog(null, "Production \"" + title + "\" already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                }

                if (validData) {
                    ((Series) contribution).title = title;
                    ((Series) contribution).directors = directors;
                    ((Series) contribution).actors = actors;
                    ((Series) contribution).genres = genres;
                    ((Series) contribution).plot = plot;
                    ((Series) contribution).releaseYear = releaseYear;
                    ((Series) contribution).numSeasons = numSeasons;
                    ((Series) contribution).setSeasons(seasons);
                    JOptionPane.showMessageDialog(null, "Series updated/added successfully!");
                    if (newContribution.get()) {
                        ((Staff<?>) IMDB.getInstance().loggedInUser).addProductionSystem((Series) contribution);
                        newContribution.set(false);
                    }
                    updateContributions();
                }
            });
            gbc.gridx = 0;
            gbc.gridy = 9;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            buttonsPanel.add(submitButton, gbc);
            panel.add(buttonsPanel, BorderLayout.SOUTH);
        }
        return panel;
    }
    private boolean contributionExists(String name) {
        for (Actor actor : IMDB.getInstance().getActors()) {
            if (actor.name.equals(name)) {
                return true;
            }
        }
        for (Production production : IMDB.getInstance().getProductions()) {
            if (production.title.equals(name)) {
                return true;
            }
        }
        return false;
    }

}
