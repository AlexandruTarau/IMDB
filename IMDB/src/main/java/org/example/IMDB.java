package org.example;

import org.example.CommandStrategies.*;
import org.example.GUI.*;
import org.example.GUI.Menu.ActorsPanel;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

public class IMDB extends JFrame {
    public List<User> getUsers() {
        return users;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public List<Request> getRequests() {
        return requests;
    }

    public List<Production> getProductions() {
        return productions;
    }

    private static IMDB instance;
    private List<User> users = new ArrayList<>();
    private List<Actor> actors = new ArrayList<>();
    private List<Request> requests = new ArrayList<>();
    private List<Production> productions = new ArrayList<>();

    JPanel cardPanel = new JPanel(new CardLayout());

    public RecommendationsPanel getRecommendations() {
        return recommendations;
    }

    public SearchBar getSearchBar() {
        return searchBar;
    }

    private RecommendationsPanel recommendations;
    private SearchBar searchBar;
    private UserPanel userPanel = null;

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public User loggedInUser = null;
    private IMDB() {}

    public static IMDB getInstance() {
        if (instance == null) {
            instance = new IMDB();
        }
        return instance;
    }
    public void printActions(AccountType type) {
        System.out.println("Choose action:");
        System.out.println("""
                    1) View productions details
                    2) View actors details
                    3) View notifications
                    4) Search for actor/movie/series
                    5) Add/Delete actor/movie/series to/from favourites""");
        if (type == AccountType.Regular) {
            System.out.println("""
                        6) Add/Delete request
                        7) Add/Delete rating
                        8) Logout""");
        } else if (type == AccountType.Contributor) {
            System.out.println("""
                        6) Add/Delete request
                        7) Add/Delete actor/movie/series from system
                        8) Solve a request
                        9) Update Movie Details
                        10) Update Actor Details
                        11) Logout""");
        } else {  // Admin
            System.out.println("""
                        6) Add/Delete actor/movie/series from system
                        7) Solve a request
                        8) Update Movie Details
                        9) Update Actor Details
                        10) Add/Delete user
                        11) Logout""");
        }
    }
    public void executeAction(int index, AccountType accountType, User user) {
        CommandStrategy strategy = null;
        try {
            switch (index) {
                case 1: {
                    strategy = new ViewProductions();
                    break;
                }
                case 2: {
                    strategy = new ViewActors();
                    break;
                }
                case 3: {
                    strategy = new ViewNotifications();
                    break;
                }
                case 4: {
                    strategy = new Search();
                    break;
                }
                case 5: {
                    strategy = new ManageFavorites();
                    break;
                }
                default: {
                    if (accountType == AccountType.Regular) {
                        switch (index) {
                            case 6: {
                                strategy = new ManageRequests();
                                break;
                            }
                            case 7: {
                                strategy = new ManageRatings();
                                break;
                            }
                            case 8: {
                                strategy = new Logout();
                                break;
                            }
                            default: {
                                throw new InvalidCommandException("Invalid command!");
                            }
                        }
                    } else if (accountType == AccountType.Contributor) {
                        switch (index) {
                            case 6: {
                                strategy = new ManageRequests();
                                break;
                            }
                            case 7: {
                                strategy = new ManageContributions();
                                break;
                            }
                            case 8: {
                                strategy = new SolveRequest();
                                break;
                            }
                            case 9: {
                                strategy = new UpdateProduction();
                                break;
                            }
                            case 10: {
                                strategy = new UpdateActor();
                                break;
                            }
                            case 11: {
                                strategy = new Logout();
                                break;
                            }
                        }
                    } else {
                        switch (index) {
                            case 6: {
                                strategy = new ManageContributions();
                                break;
                            }
                            case 7: {
                                strategy = new SolveRequest();
                                break;
                            }
                            case 8: {
                                strategy = new UpdateProduction();
                                break;
                            }
                            case 9: {
                                strategy = new UpdateActor();
                                break;
                            }
                            case 10: {
                                strategy = new ManageUsers();
                                break;
                            }
                            case 11: {
                                strategy = new Logout();
                            }
                        }
                    }
                }
            }
            assert strategy != null;
            strategy.execute(user);
        } catch (InvalidCommandException e) {
            System.out.println(e.getMessage());
        }
    }
    public void linkObservers() {
        for (Production production : productions) {
            for (Rating src : production.ratings) {
                for (Rating dest : production.ratings) {
                    if (src != dest) {
                        for (User user : users) {
                            if (user.username.equals(dest.username)) {
                                src.addObserver(user);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    public void logIn(User user) {
        loggedInUser = user;
    }
    public void run() {
        Scanner scanner = new Scanner(System.in);
        ReadFiles<?> readFiles = new ReadFiles<>();
        readFiles.load_actors();
        readFiles.load_productions();
        readFiles.load_accounts();
        readFiles.load_requests();
        linkObservers();

        int choice;
        String email;
        String password;

        while (true) {
            System.out.println("Please select an interface:");
            System.out.println("1. Terminal");
            System.out.println("2. GUI");
            System.out.println("3. Exit");

            try {
                choice = scanner.nextInt();
                scanner.nextLine();
                if (choice != 1 && choice != 2 && choice != 3) {
                    System.out.println("Invalid interface!");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid interface!");
                scanner.nextLine();
            }
        }

        if (choice == 1) {
            while (true) {
                if (loggedInUser == null) {
                    System.out.println("Welcome back! Enter your credentials!\n");
                    while (loggedInUser == null) {
                        System.out.print("\temail: ");
                        email = scanner.nextLine();

                        System.out.print("\tpassword: ");
                        password = scanner.nextLine();

                        System.out.println();
                        for (User user : users) {
                            Credentials credentials = user.info.getCredentials();
                            if (credentials.getEmail().equals(email) && credentials.getPassword().equals(password)) {
                                System.out.println("Welcome back user " + user.username + "!");
                                loggedInUser = user;
                                break;
                            }
                        }
                        if (loggedInUser == null) {
                            System.out.println("Wrong credentials!");
                        }
                    }
                }

                // Successfully logged in!
                loggedInUser.displayInfo();
                printActions(loggedInUser.type);
                try {
                    int action_idx = scanner.nextInt();
                    scanner.nextLine();
                    executeAction(action_idx, loggedInUser.type, loggedInUser);
                } catch (InputMismatchException e) {
                    System.out.println("Invalid command!");
                    scanner.nextLine();
                }
            }
        }
        else if (choice == 2) {
            JFrame frame = new JFrame("Authentication");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 800);
            frame.setLocationRelativeTo(null);
            CardLayout cardLayout = (CardLayout) cardPanel.getLayout();

            // Authentication page
            Authentication authentication = new Authentication(cardLayout, cardPanel);
            cardPanel.add(authentication, "Authentication");

            frame.add(cardPanel);
            frame.setVisible(true);
        }

    }

    public JPanel mainPage() {
        JPanel mainPage = new JPanel(new BorderLayout());
        recommendations = new RecommendationsPanel();
        searchBar = new SearchBar();

        // Actors page button
        JButton actorsButton = new JButton("View Actors");
        actorsButton.addActionListener(e -> showActorsPanel());

        // Main menu button
        JButton menuButton = new JButton("Menu");
        menuButton.addActionListener(e -> showMainMenu());

        // User panel button
        JButton userInfoButton = new JButton("User Info");
        userInfoButton.addActionListener(e -> showUserPanel());

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel buttonsPanel = new JPanel(new FlowLayout());

        buttonsPanel.add(actorsButton);
        buttonsPanel.add(menuButton);
        buttonsPanel.add(userInfoButton);

        topPanel.add(searchBar, BorderLayout.NORTH);
        topPanel.add(buttonsPanel, BorderLayout.EAST);

        mainPage.add(topPanel, BorderLayout.NORTH);
        mainPage.add(recommendations, BorderLayout.CENTER);

        return mainPage;
    }
    public void initPanels() {
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();

        // Main page
        cardPanel.add(mainPage(), "MainPage");

        // Actors page
        ActorsPanel actorsPanel = new ActorsPanel(cardLayout, cardPanel, false);
        cardPanel.add(actorsPanel, "ActorsPanel");

        // Main Menu
        MainMenu mainMenu = new MainMenu(cardLayout, cardPanel);
        cardPanel.add(mainMenu, "MainMenu");

        // User Panel
        userPanel = new UserPanel(cardLayout, cardPanel);
        cardPanel.add(userPanel, "UserPanel");
    }
    public void Search(String name) {
        for (Production production : productions) {
            if (production.title.equals(name)) {
                recommendations.updateDisplay(production);
                return;
            }
        }
        for (Actor actor : actors) {
            if (actor.name.equals(name)) {
                System.out.println(name);
                recommendations.updateDisplay(actor);
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Movie/Series/Actor not found!", "Error", JOptionPane.ERROR_MESSAGE);
    }
    public void showActorsPanel() {
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
        cardLayout.show(cardPanel, "ActorsPanel");
    }
    public void showMainMenu() {
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
        cardLayout.show(cardPanel, "MainMenu");
    }
    public void showRecommendations() {
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
        cardLayout.show(cardPanel, "MainPage");
        recommendations.updateRecommendationsList(null);
    }
    public void showUserPanel() {
        if (userPanel != null) {
            CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
            cardLayout.show(cardPanel, "UserPanel");
            userPanel.updateUser();
        }
    }
    public void addRating(Production production) {
        if (loggedInUser.type != AccountType.Regular) {
            JOptionPane.showMessageDialog(null, "Only regulars can rate productions!");
            return;
        }
        if (production == null) {
            JOptionPane.showMessageDialog(null, "Invalid production! Cannot add rating.");
            return;
        }

        // Check if the user has already rated this production
        boolean changeRating = false;
        if (((Regular<?>) loggedInUser).ratedProductions.contains(production)) {
            int option = JOptionPane.showConfirmDialog(null, "You have already rated this production. Do you want to change your rating?", "Already Rated",
                    JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.NO_OPTION || option == JOptionPane.CLOSED_OPTION) {
                return;
            }
            changeRating = true;
        }

        String ratingInput = JOptionPane.showInputDialog(null, "Enter your rating (1-10):");

        if (ratingInput == null) {
            return;
        }

        try {
            int rating = Integer.parseInt(ratingInput);

            if (rating < 1 || rating > 10) {
                JOptionPane.showMessageDialog(null, "Invalid rating! Please enter a number between 1 and 10.");
            } else {
                String commentInput = JOptionPane.showInputDialog(null, "Write a comment:");

                if (commentInput == null) {
                    return;
                }

                if (changeRating) {
                    // Remove rating
                    for (Rating r : production.ratings) {
                        if (r.username.equals(loggedInUser.username)) {
                            ((Regular<?>) loggedInUser).ratedProductions.remove(production);
                            production.ratings.remove(r);
                            production.updateAvgRating(r.rating, false);
                            break;
                        }
                    }
                }
                ((Regular<?>) loggedInUser).rate(production, rating, commentInput);
                production.updateAvgRating(rating, true);

                JOptionPane.showMessageDialog(null, "Rating added successfully!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input! Please enter a valid number.");
        }
    }
    public void addToFavourites(Object favorite) {
        if (favorite == null) {
            JOptionPane.showMessageDialog(null, "Invalid production/actor! Cannot add to favorites.");
            return;
        }

        if (loggedInUser.favorites.contains(favorite)) {
            int option = JOptionPane.showConfirmDialog(null, "Already in your list of favorites. Remove from favorites?", "Already Favorite",
                    JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.NO_OPTION || option == JOptionPane.CLOSED_OPTION) {
                return;
            }
            removeFromFavourites(favorite);
            return;
        }

        loggedInUser.favorites.add(favorite);
        JOptionPane.showMessageDialog(null, "Favorite added successfully!");
    }
    public void makeRequest(Object obj) {
        if (loggedInUser.type == AccountType.Admin) {
            JOptionPane.showMessageDialog(null, "Admins can not make requests!");
            return;
        }
        if (obj == null) {
            JOptionPane.showMessageDialog(null, "Invalid production/actor!");
            return;
        }

        // obj is either Production or Actor
        if (!(obj instanceof String) && loggedInUser instanceof Staff) {
            if (((Staff) loggedInUser).contributions.contains(obj)) {
                JOptionPane.showMessageDialog(null, "Can't add request to your own contribution.");
                return;
            }
        }

        User sender = getContributor(obj);
        String description = JOptionPane.showInputDialog(null, "Enter request description:");
        if (description == null) {
            return;
        }
        if (!description.isEmpty()) {
            Request request;
            if (obj instanceof Production) {
                request = new Request("MOVIE_ISSUE", LocalDateTime.now().withNano(0), ((Production) obj).title, null,
                        description, loggedInUser.username, sender != null ? sender.username : "ADMIN");
            } else if (obj instanceof Actor) {
                request = new Request("ACTOR_ISSUE", LocalDateTime.now().withNano(0), null, ((Actor) obj).name,
                        description, loggedInUser.username, sender != null ? sender.username : "ADMIN");
            } else {
                assert obj instanceof String;
                if (obj.equals("DELETE_ACCOUNT")) {
                    request = new Request("DELETE_ACCOUNT", LocalDateTime.now().withNano(0), null, null,
                            description, loggedInUser.username, "ADMIN");
                } else {
                    request = new Request("OTHERS", LocalDateTime.now().withNano(0), null, null,
                            description, loggedInUser.username, "ADMIN");
                }
            }
            ((RequestsManager) loggedInUser).createRequest(request);
            if (sender != null) {
                ((Staff<?>) sender).requests.add(request);
            } else {
                RequestsHolder.requests.add(request);
            }
            JOptionPane.showMessageDialog(null, "Request created successfully!");
        } else {
            JOptionPane.showMessageDialog(null, "Invalid description! Request not created.");
        }
    }
    public void removeFromFavourites(Object favorite) {
        if (loggedInUser.favorites.contains(favorite)) {
            loggedInUser.favorites.remove(favorite);
            JOptionPane.showMessageDialog(null, "Favorite removed successfully!");
        }
    }
    public User getContributor(Object contribution) {
        for (User user : users) {
            if (user instanceof Staff) {
                for (Object contrib : ((Staff) user).contributions) {
                    if (contribution.equals(contrib)) {
                        return user;
                    }
                }
            }
        }
        return null;
    }
    public static void main(String args[]) {
        IMDB imdb = IMDB.getInstance();
        imdb.run();
    }
}
