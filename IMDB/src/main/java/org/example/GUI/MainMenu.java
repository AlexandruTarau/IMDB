package org.example.GUI;

import org.example.AccountType;
import org.example.GUI.Menu.*;
import org.example.IMDB;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainMenu extends JPanel {
    ProductionsPanel productionsPanel;
    ActorsPanel actorsPanel;
    NotificationsPanel notificationsPanel;
    FavoritesPanel favoritesPanel;
    RequestsPanel requestsPanel;
    RatingsPanel ratingsPanel;
    ContributionsPanel contributionsPanel;
    SolveRequestPanel solveRequestPanel;
    UsersPanel usersPanel;
    private final List<String> regularButtons = List.of(
            "View productions details",
            "View actors details",
            "View notifications",
            "Manage favourites",
            "Add/Delete request",
            "Add/Delete rating",
            "Logout"
    );

    private final List<String> contributorButtons = List.of(
            "View productions details",
            "View actors details",
            "View notifications",
            "Manage favourites",
            "Add/Delete request",
            "Manage contributions",
            "Solve a request",
            "Logout"
    );

    private final List<String> adminButtons = List.of(
            "View productions details",
            "View actors details",
            "View notifications",
            "Manage favourites",
            "Manage contributions",
            "Solve a request",
            "Add/Delete user",
            "Logout"
    );

    public MainMenu(CardLayout cardLayout, JPanel cardPanel) {
        initOptions(cardLayout, cardPanel);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        AccountType userType = IMDB.getInstance().loggedInUser.type;
        for (String buttonLabel : getButtonsForUserType(userType)) {
            JButton button = new JButton(buttonLabel);
            button.setPreferredSize(new Dimension(200, 40));
            button.setMaximumSize(new Dimension(200, 40));
            button.addActionListener(e -> handleButtonAction(buttonLabel, cardLayout, cardPanel));

            add(button, gbc);
            gbc.gridy++;
        }

        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(200, 40));
        backButton.setMaximumSize(new Dimension(200, 40));
        backButton.addActionListener(e -> {
            IMDB.getInstance().showRecommendations();
        });

        gbc.gridy++;
        add(backButton, gbc);
    }

    private List<String> getButtonsForUserType(AccountType userType) {
        if (userType.equals(AccountType.Regular)) {
            return regularButtons;
        } else if (userType.equals(AccountType.Contributor)) {
            return contributorButtons;
        } else if (userType.equals(AccountType.Admin)) {
            return adminButtons;
        }
        return null;
    }
    private void handleButtonAction(String buttonLabel, CardLayout cardLayout, JPanel cardPanel) {
        switch (buttonLabel) {
            case "View productions details": {
                cardLayout.show(cardPanel, "ProductionsPanel");
                productionsPanel.updateProductionsList(null);
                break;
            }
            case "View actors details": {
                cardLayout.show(cardPanel, "ActorsPanelMenu");
                actorsPanel.updateActorsList(null);
                break;
            }
            case "View notifications": {
                cardLayout.show(cardPanel, "NotificationsPanel");
                break;
            }
            case "Manage favourites": {
                cardLayout.show(cardPanel, "FavouritesPanel");
                favoritesPanel.updateFavouritesList();
                break;
            }
            case "Add/Delete request": {
                cardLayout.show(cardPanel, "RequestsPanel");
                requestsPanel.updateRequestsList();
                break;
            }
            case "Add/Delete rating": {
                cardLayout.show(cardPanel, "RatingsPanel");
                ratingsPanel.updateRatingsList();
                break;
            }
            case "Manage contributions": {
                cardLayout.show(cardPanel, "ContributionsPanel");
                break;
            }
            case "Solve a request": {
                cardLayout.show(cardPanel, "SolveRequestPanel");
                solveRequestPanel.updateRequestsList();
                break;
            }
            case "Add/Delete user": {
                cardLayout.show(cardPanel, "UsersPanel");
                usersPanel.updateUsersList();
                break;
            }
            case "Logout":
                IMDB.getInstance().loggedInUser.logOut();
                cardLayout.show(cardPanel, "Authentication");
                break;
            default:
                break;
        }
    }
    private void initOptions(CardLayout cardLayout, JPanel cardPanel) {
        // Productions panel
        productionsPanel = new ProductionsPanel(cardLayout, cardPanel);
        cardPanel.add(productionsPanel, "ProductionsPanel");

        // Actors panel
        actorsPanel = new ActorsPanel(cardLayout, cardPanel, true);
        cardPanel.add(actorsPanel, "ActorsPanelMenu");

        // Notifications panel
        notificationsPanel = new NotificationsPanel(cardLayout, cardPanel, IMDB.getInstance().loggedInUser.notifications);
        cardPanel.add(notificationsPanel, "NotificationsPanel");

        // Favourites panel
        favoritesPanel = new FavoritesPanel(cardLayout, cardPanel);
        cardPanel.add(favoritesPanel, "FavouritesPanel");

        // Requests panel
        if (IMDB.getInstance().loggedInUser.type != AccountType.Admin) {
            requestsPanel = new RequestsPanel(cardLayout, cardPanel);
            cardPanel.add(requestsPanel, "RequestsPanel");
        }

        // Ratings panel
        if (IMDB.getInstance().loggedInUser.type == AccountType.Regular) {
            ratingsPanel = new RatingsPanel(cardLayout, cardPanel);
            cardPanel.add(ratingsPanel, "RatingsPanel");
        }

        if (IMDB.getInstance().loggedInUser.type != AccountType.Regular) {
            // Contributions panel
            contributionsPanel = new ContributionsPanel(cardLayout, cardPanel);
            cardPanel.add(contributionsPanel, "ContributionsPanel");

            // Solve request panel
            solveRequestPanel = new SolveRequestPanel(cardLayout, cardPanel);
            cardPanel.add(solveRequestPanel, "SolveRequestPanel");
        }

        if (IMDB.getInstance().loggedInUser.type == AccountType.Admin) {
            usersPanel = new UsersPanel(cardLayout, cardPanel);
            cardPanel.add(usersPanel, "UsersPanel");
        }
    }
}
