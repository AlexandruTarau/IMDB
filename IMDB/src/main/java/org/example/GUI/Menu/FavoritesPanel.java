package org.example.GUI.Menu;

import org.example.Actor;
import org.example.IMDB;
import org.example.Production;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.SortedSet;

public class FavoritesPanel extends JPanel {
    private JLabel imageLabel;
    private JTextArea favoritesInfoTextArea;
    private DefaultListModel<String> listModel;
    private SortedSet<?> favorites;
    private Object favorite;

    public FavoritesPanel (CardLayout cardLayout, JPanel cardPanel) {
        setLayout(new BorderLayout());
        favorites = IMDB.getInstance().loggedInUser.favorites;

        listModel = new DefaultListModel<>();
        updateFavouritesList();

        JList<String> favoritesJList = new JList<>(listModel);
        favoritesJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        favoritesJList.setFixedCellWidth(200);
        favoritesJList.setPreferredSize(new Dimension(200, favoritesJList.getPreferredSize().height));
        favoritesJList.setSelectedIndex(0);

        JScrollPane scrollPane = new JScrollPane(favoritesJList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Favorites"));

        JPanel imagePanel = new JPanel(new BorderLayout());
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new GridLayout(3, 1, 20, 10));

        // Remove from Favourites button
        JButton addToFavouritesButton = new JButton("Remove from Favourites");
        addToFavouritesButton.setPreferredSize(new Dimension(140, 30));
        addToFavouritesButton.addActionListener(e -> {
            if (favorite != null) {
                IMDB.getInstance().removeFromFavourites(favorite);
                imageLabel.setIcon(null);
                favoritesInfoTextArea.setText("");
                favorite = null;

                updateFavouritesList();
            } else {
                JOptionPane.showMessageDialog(this, "Movie/Series/Actor not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        });

        // Add rating button
        JButton addRatingButton = new JButton("Add Rating");
        addRatingButton.setPreferredSize(new Dimension(140, 30));
        addRatingButton.addActionListener(e -> {
            if (favorite instanceof Production) {
                IMDB.getInstance().addRating((Production) favorite);
                updateDisplay(favorite);
            } else {
                IMDB.getInstance().addRating(null);
            }

        });

        // Request button
        JButton addRequestButton = new JButton("Make Request");
        addRequestButton.setPreferredSize(new Dimension(140, 30));
        addRequestButton.addActionListener(e -> {
            IMDB.getInstance().makeRequest(favorite);
        });

        buttonsPanel.add(addRequestButton);
        buttonsPanel.add(addToFavouritesButton);
        buttonsPanel.add(addRatingButton);

        JPanel imageAndButtonsPanel = new JPanel(new BorderLayout());
        imageAndButtonsPanel.add(imagePanel, BorderLayout.CENTER);
        imageAndButtonsPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Information about production
        favoritesInfoTextArea = new JTextArea();
        favoritesInfoTextArea.setEditable(false);
        favoritesInfoTextArea.setLineWrap(true);
        favoritesInfoTextArea.setWrapStyleWord(true);
        favoritesInfoTextArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel textAreaPanel = new JPanel(new BorderLayout());
        textAreaPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        textAreaPanel.add(new JScrollPane(favoritesInfoTextArea), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(new EmptyBorder(50, 0, 50, 0));
        rightPanel.add(imageAndButtonsPanel, BorderLayout.WEST);
        rightPanel.add(textAreaPanel, BorderLayout.CENTER);

        if (!favorites.isEmpty()) {
            updateDisplay(favorites.toArray(new Object[0])[0]);
        }
        favoritesJList.addListSelectionListener(e -> {
            int selectedIndex = favoritesJList.getSelectedIndex();

            if (selectedIndex != -1) {
                Object selectedFavorite = favorites.toArray(new Object[0])[selectedIndex];
                updateDisplay(selectedFavorite);
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

        add(buttonPanel, BorderLayout.NORTH);
        add(rightPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.WEST);
    }
    private void updateDisplay(Object favorite) {
        favoritesInfoTextArea.setText(favorite.toString());
        favoritesInfoTextArea.setCaretPosition(0);
        ImageIcon imageIcon;
        if (favorite instanceof Production) {
            imageIcon = ((Production) favorite).image;
        } else {
            imageIcon = new ImageIcon("IMDB\\src\\main\\java\\org\\example\\GUI\\images\\unknownImage.png");
        }
        if (imageIcon == null) {
            imageIcon = new ImageIcon("IMDB\\src\\main\\java\\org\\example\\GUI\\images\\unknownImage.png");
        }
        imageIcon.setImage(imageIcon.getImage().getScaledInstance(300, 500, Image.SCALE_DEFAULT));
        imageLabel.setIcon(imageIcon);
        this.favorite = favorite;
    }
    public void updateFavouritesList() {
        listModel.clear();

        for (Object f : favorites) {
            if (f instanceof Production) {
                listModel.addElement(((Production) f).title);
            } else {
                listModel.addElement(((Actor) f).name);
            }
        }
    }
}
