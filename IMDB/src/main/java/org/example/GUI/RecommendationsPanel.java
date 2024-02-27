package org.example.GUI;

import org.example.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecommendationsPanel extends JPanel {
    public JLabel imageLabel;
    public JTextArea movieInfoTextArea;
    private DefaultListModel<String> listModel;
    private List<Production> recommendations;
    public Production production;
    public Actor actor;

    public RecommendationsPanel() {
        setLayout(new BorderLayout());
        List<Production> allProductions = new ArrayList<>(IMDB.getInstance().getProductions());
        Collections.shuffle(allProductions);
        recommendations = allProductions.subList(0, Math.min(5, allProductions.size()));
        List<Production> recommendationsCopy = new ArrayList<>(recommendations);

        listModel = new DefaultListModel<>();
        for (Production recommendation : recommendations) {
            listModel.addElement(recommendation.title);
        }

        // Recommendations list
        JList<String> recommendationsJList = new JList<>(listModel);
        recommendationsJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recommendationsJList.setFixedCellWidth(200);
        recommendationsJList.setPreferredSize(new Dimension(200, recommendationsJList.getPreferredSize().height));
        recommendationsJList.setSelectedIndex(0);

        JScrollPane scrollPane = new JScrollPane(recommendationsJList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Recommendations"));

        // Production image
        JPanel imagePanel = new JPanel(new BorderLayout());
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        // Rating and Add to Favourites buttons
        JPanel buttonsPanel = new JPanel(new GridLayout(3, 1, 20, 10));
        JButton addRatingButton = new JButton("Add Rating");
        JButton addToFavouritesButton = new JButton("Add to Favourites");
        JButton addRequestButton = new JButton("Make Request");

        // Set preferred size for buttons
        addRatingButton.setPreferredSize(new Dimension(140, 30));
        addToFavouritesButton.setPreferredSize(new Dimension(140, 30));
        addRequestButton.setPreferredSize(new Dimension(140, 30));

        buttonsPanel.add(addRatingButton);
        buttonsPanel.add(addToFavouritesButton);
        buttonsPanel.add(addRequestButton);

        JPanel imageAndButtonsPanel = new JPanel(new BorderLayout());
        imageAndButtonsPanel.add(imagePanel, BorderLayout.CENTER);
        imageAndButtonsPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Information about production
        movieInfoTextArea = new JTextArea();
        movieInfoTextArea.setEditable(false);
        movieInfoTextArea.setLineWrap(true);
        movieInfoTextArea.setWrapStyleWord(true);
        movieInfoTextArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel textAreaPanel = new JPanel(new BorderLayout());
        textAreaPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        textAreaPanel.add(new JScrollPane(movieInfoTextArea), BorderLayout.CENTER);

        // Right Panel
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(new EmptyBorder(30, 0, 50, 0));
        rightPanel.add(imageAndButtonsPanel, BorderLayout.WEST);
        rightPanel.add(textAreaPanel, BorderLayout.CENTER);

        // Recommendations list
        if (!recommendationsCopy.isEmpty()) {
            updateDisplay(recommendationsCopy.get(0));
        }
        recommendationsJList.addListSelectionListener(e -> {
            int selectedIndex = recommendationsJList.getSelectedIndex();

            if (selectedIndex != -1) {
                updateDisplay(recommendationsCopy.get(selectedIndex));
            }
        });

        // Buttons
        addRatingButton.addActionListener(e -> {
            IMDB.getInstance().addRating(production);
            updateDisplay(production);
        });
        addToFavouritesButton.addActionListener(e -> {
            IMDB.getInstance().addToFavourites(actor == null ? production : actor);
        });
        addRequestButton.addActionListener(e -> {
            IMDB.getInstance().makeRequest(actor == null ? production : actor);
        });

        JButton filterButton = new JButton("Filter");
        filterButton.addActionListener(e -> {
            String[] filterOptions = {"By Genre", "By Number of Ratings", "By Type", "By Average Rating", "By Actor", "By Year of Release"};
            String selectedOption = (String) JOptionPane.showInputDialog(
                    this,
                    "Choose filtering option:",
                    "Filter Options",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    filterOptions,
                    filterOptions[0]);

            if (selectedOption != null) {
                List<Production> filteredProductions;
                switch (selectedOption) {
                    case "By Genre": {
                        Genre selectedGenre = (Genre) JOptionPane.showInputDialog(
                                this,
                                "Choose a genre:",
                                "Genres",
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                Genre.values(),
                                Genre.values()[0]);

                        if (selectedGenre != null) {
                            filteredProductions = recommendations.stream()
                                    .filter(production -> filterByGenre(production, selectedGenre))
                                    .toList();

                            updateRecommendationsList(filteredProductions);
                            recommendationsCopy.clear();
                            recommendationsCopy.addAll(filteredProductions);
                            movieInfoTextArea.setText("");
                            imageLabel.setIcon(null);
                            production = null;
                        }
                        break;
                    }
                    case "By Number of Ratings": {
                        String minRatings = JOptionPane.showInputDialog(this, "Enter minimum number of ratings:");
                        if (minRatings != null && !minRatings.isEmpty()) {
                            try {
                                int minRatingValue = Integer.parseInt(minRatings);
                                filteredProductions = recommendations.stream()
                                        .filter(production -> filterByRatings(production, minRatingValue))
                                        .toList();

                                updateRecommendationsList(filteredProductions);
                                recommendationsCopy.clear();
                                recommendationsCopy.addAll(filteredProductions);
                            } catch (NumberFormatException exception) {
                                JOptionPane.showMessageDialog(this, "Invalid number of ratings!",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            } finally {
                                movieInfoTextArea.setText("");
                                imageLabel.setIcon(null);
                                production = null;
                            }
                        }
                        break;
                    }
                    case "By Type": {
                        String[] types = new String[]{"Movies", "Series"};
                        String selectedType = (String) JOptionPane.showInputDialog(
                                this,
                                "Choose a type:",
                                "Types",
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                types,
                                types[0]);
                        if (selectedType != null) {
                            filteredProductions = recommendations.stream()
                                    .filter(production -> filterByType(production, selectedType))
                                    .toList();

                            updateRecommendationsList(filteredProductions);
                            recommendationsCopy.clear();
                            recommendationsCopy.addAll(filteredProductions);
                            movieInfoTextArea.setText("");
                            imageLabel.setIcon(null);
                            production = null;
                        }
                        break;
                    }
                    case "By Average Rating": {
                        String minAvgRating = JOptionPane.showInputDialog(this, "Enter minimum average rating:");
                        if (minAvgRating != null && !minAvgRating.isEmpty()) {
                            try {
                                double minAvgRatingValue = Double.parseDouble(minAvgRating);
                                filteredProductions = recommendations.stream()
                                        .filter(production -> filterByAvgRating(production, minAvgRatingValue))
                                        .toList();

                                updateRecommendationsList(filteredProductions);
                                recommendationsCopy.clear();
                                recommendationsCopy.addAll(filteredProductions);
                            } catch (NumberFormatException exception) {
                                JOptionPane.showMessageDialog(this, "Invalid average rating!",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            } finally {
                                movieInfoTextArea.setText("");
                                imageLabel.setIcon(null);
                                production = null;
                            }
                        }
                        break;
                    }
                    case "By Actor": {
                        String actor = JOptionPane.showInputDialog(this, "Enter actor name:");
                        if (actor != null && !actor.isEmpty()) {
                            boolean ok = false;
                            for (Actor a : IMDB.getInstance().getActors()) {
                                if (a.name.equals(actor)) {
                                    ok = true;
                                    break;
                                }
                            }
                            if (ok) {
                                filteredProductions = recommendations.stream()
                                        .filter(production -> filterByActor(production, actor))
                                        .toList();

                                updateRecommendationsList(filteredProductions);
                                recommendationsCopy.clear();
                                recommendationsCopy.addAll(filteredProductions);
                                movieInfoTextArea.setText("");
                                imageLabel.setIcon(null);
                                production = null;
                            } else {
                                JOptionPane.showMessageDialog(this, "Invalid actor!",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                        break;
                    }
                    case "By Year of Release": {
                        String year = JOptionPane.showInputDialog(this, "Enter maximum year of release:");
                        if (year != null && !year.isEmpty()) {
                            try {
                                int yearValue = Integer.parseInt(year);
                                filteredProductions = recommendations.stream()
                                        .filter(production -> filterByYear(production, yearValue))
                                        .toList();

                                updateRecommendationsList(filteredProductions);
                                recommendationsCopy.clear();
                                recommendationsCopy.addAll(filteredProductions);
                            } catch (NumberFormatException exception) {
                                JOptionPane.showMessageDialog(this, "Invalid year!",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            } finally {
                                movieInfoTextArea.setText("");
                                imageLabel.setIcon(null);
                                production = null;
                            }
                        }
                        break;
                    }
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(filterButton);

        add(buttonPanel, BorderLayout.NORTH);
        add(rightPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.WEST);
    }
    public void updateDisplay(Object obj) {
        if (obj instanceof Production || obj instanceof Actor) {
            movieInfoTextArea.setText(obj.toString());
            movieInfoTextArea.setCaretPosition(0);
            ImageIcon imageIcon;
            if (obj instanceof Production) {
                imageIcon = ((Production) obj).image;
                production = (Production) obj;
                actor = null;
            } else {
                imageIcon = ((Actor) obj).image;
                actor = (Actor) obj;
                production = null;
            }
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(300, 500, Image.SCALE_DEFAULT));
            imageLabel.setIcon(imageIcon);
        } else {
            actor = null;
            production = null;
        }
    }
    private boolean filterByGenre(Production recommendation, Genre genre) {
        return recommendation.genres.contains(genre);
    }

    private boolean filterByRatings(Production recommendation, int minRatings) {
        return recommendation.ratings.size() >= minRatings;
    }
    private boolean filterByType(Production production, String type) {
        return type.equals("Movies") ? production instanceof Movie : production instanceof Series;
    }
    private boolean filterByAvgRating(Production production, double avgRating) {
        return production.averageRating >= avgRating;
    }
    private boolean filterByActor(Production production, String actor) {
        return production.actors.contains(actor);
    }
    private boolean filterByYear(Production production, int year) {
        return production instanceof Movie ? ((Movie) production).releaseYear <= year : ((Series) production).releaseYear <= year;
    }

    public void updateRecommendationsList(List<Production> filteredRecommendations) {
        listModel.clear();

        for (Production recommendation : filteredRecommendations == null ? recommendations : filteredRecommendations) {
            listModel.addElement(recommendation.title);
        }
    }
}
