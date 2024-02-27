package org.example.GUI.Menu;

import org.example.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class ProductionsPanel extends JPanel {
    private JLabel imageLabel;
    private JTextArea productionInfoTextArea;
    private DefaultListModel<String> listModel;
    private SortedSet<Production> productions;
    private Production production;
    private List<Production> productionsCopy;

    public ProductionsPanel(CardLayout cardLayout, JPanel cardPanel) {
        setLayout(new BorderLayout());
        productions = new TreeSet<>(IMDB.getInstance().getProductions());
        productionsCopy = new ArrayList<>(productions);

        listModel = new DefaultListModel<>();
        for (Production p : productions) {
            listModel.addElement(p.title);
        }

        JList<String> productionsJList = new JList<>(listModel);
        productionsJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productionsJList.setFixedCellWidth(200);
        productionsJList.setPreferredSize(new Dimension(200, productionsJList.getPreferredSize().height));

        JScrollPane scrollPane = new JScrollPane(productionsJList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Productions"));

        JPanel imagePanel = new JPanel(new BorderLayout());
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new GridLayout(3, 1, 20, 10));

        // Add to Favourites button
        JButton addToFavouritesButton = new JButton("Add to Favourites");
        addToFavouritesButton.setPreferredSize(new Dimension(140, 30));
        addToFavouritesButton.addActionListener(e -> {
            IMDB.getInstance().addToFavourites(production);
        });

        // Add rating button
        JButton addRatingButton = new JButton("Add Rating");
        addRatingButton.setPreferredSize(new Dimension(140, 30));
        addRatingButton.addActionListener(e -> {
            IMDB.getInstance().addRating(production);
            updateDisplay(production);
        });

        // Request button
        JButton addRequestButton = new JButton("Make Request");
        addRequestButton.setPreferredSize(new Dimension(140, 30));
        addRequestButton.addActionListener(e -> {
            IMDB.getInstance().makeRequest(production);
        });

        buttonsPanel.add(addRequestButton);
        buttonsPanel.add(addToFavouritesButton);
        buttonsPanel.add(addRatingButton);

        JPanel imageAndButtonsPanel = new JPanel(new BorderLayout());
        imageAndButtonsPanel.add(imagePanel, BorderLayout.CENTER);
        imageAndButtonsPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Information about production
        productionInfoTextArea = new JTextArea();
        productionInfoTextArea.setEditable(false);
        productionInfoTextArea.setLineWrap(true);
        productionInfoTextArea.setWrapStyleWord(true);
        productionInfoTextArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel textAreaPanel = new JPanel(new BorderLayout());
        textAreaPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        textAreaPanel.add(new JScrollPane(productionInfoTextArea), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(new EmptyBorder(50, 0, 50, 0));
        rightPanel.add(imageAndButtonsPanel, BorderLayout.WEST);
        rightPanel.add(textAreaPanel, BorderLayout.CENTER);

        if (!productionsCopy.isEmpty()) {
            updateDisplay(productionsCopy.get(0));
        }
        productionsJList.addListSelectionListener(e -> {
            int selectedIndex = productionsJList.getSelectedIndex();

            if (selectedIndex != -1) {
                updateDisplay(productionsCopy.get(selectedIndex));
            }
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
                            filteredProductions = productions.stream()
                                    .filter(production -> filterByGenre(production, selectedGenre))
                                    .toList();

                            updateProductionsList(filteredProductions);
                            productionsCopy.clear();
                            productionsCopy.addAll(filteredProductions);
                            productionInfoTextArea.setText("");
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
                                filteredProductions = productions.stream()
                                        .filter(production -> filterByRatings(production, minRatingValue))
                                        .toList();

                                updateProductionsList(filteredProductions);
                                productionsCopy.clear();
                                productionsCopy.addAll(filteredProductions);
                            } catch (NumberFormatException exception) {
                                JOptionPane.showMessageDialog(this, "Invalid number of ratings!",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            } finally {
                                productionInfoTextArea.setText("");
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
                            filteredProductions = productions.stream()
                                    .filter(production -> filterByType(production, selectedType))
                                    .toList();

                            updateProductionsList(filteredProductions);
                            productionsCopy.clear();
                            productionsCopy.addAll(filteredProductions);
                            productionInfoTextArea.setText("");
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
                                filteredProductions = productions.stream()
                                        .filter(production -> filterByAvgRating(production, minAvgRatingValue))
                                        .toList();

                                updateProductionsList(filteredProductions);
                                productionsCopy.clear();
                                productionsCopy.addAll(filteredProductions);
                            } catch (NumberFormatException exception) {
                                JOptionPane.showMessageDialog(this, "Invalid average rating!",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            } finally {
                                productionInfoTextArea.setText("");
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
                                filteredProductions = productions.stream()
                                        .filter(production -> filterByActor(production, actor))
                                        .toList();

                                updateProductionsList(filteredProductions);
                                productionsCopy.clear();
                                productionsCopy.addAll(filteredProductions);
                                productionInfoTextArea.setText("");
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
                                filteredProductions = productions.stream()
                                        .filter(production -> filterByYear(production, yearValue))
                                        .toList();

                                updateProductionsList(filteredProductions);
                                productionsCopy.clear();
                                productionsCopy.addAll(filteredProductions);
                            } catch (NumberFormatException exception) {
                                JOptionPane.showMessageDialog(this, "Invalid year!",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            } finally {
                                productionInfoTextArea.setText("");
                                imageLabel.setIcon(null);
                                production = null;
                            }
                        }
                        break;
                    }
                }
            }
        });

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "MainMenu");
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(backButton);
        buttonPanel.add(filterButton);

        add(buttonPanel, BorderLayout.NORTH);
        add(rightPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.WEST);
    }
    private void updateDisplay(Production production) {
        productionInfoTextArea.setText(production.toString());
        productionInfoTextArea.setCaretPosition(0);
        ImageIcon imageIcon;
        if (production.image != null) {
            imageIcon = production.image;
        } else {
            imageIcon = new ImageIcon("IMDB\\src\\main\\java\\org\\example\\GUI\\images\\unknownImage.png");
        }
        imageIcon.setImage(imageIcon.getImage().getScaledInstance(300, 500, Image.SCALE_DEFAULT));
        imageLabel.setIcon(imageIcon);
        this.production = production;
    }
    private boolean filterByGenre(Production production, Genre genre) {
        return production.genres.contains(genre);
    }
    private boolean filterByRatings(Production production, int minRatings) {
        return production.ratings.size() >= minRatings;
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
    public void updateProductionsList(List<Production> productions) {
        listModel.clear();
        if (productions == null) {
            this.productions.clear();
            this.productions.addAll(IMDB.getInstance().getProductions());
            this.productionsCopy.clear();
            this.productionsCopy = new ArrayList<>(this.productions);
        }

        for (Production p : (productions == null ? this.productions : productions)) {
            listModel.addElement(p.title);
        }
    }
}
