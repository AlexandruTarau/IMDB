package org.example.CommandStrategies;

import org.example.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ManageRequests extends AuxMethods implements CommandStrategy {
    IMDB imdb = IMDB.getInstance();
    public User getContributor (Object contribution) {
        for (User user : imdb.getUsers()) {
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
    @Override
    public void execute(User user) {
        Scanner scanner = new Scanner(System.in);
        boolean loop = true;
        while (loop) {
            boolean hasRequests = false;
            System.out.println("Your requests:");
            for (Request request : imdb.getRequests()) {
                if (request.requesterUsername.equals(user.username)) {
                    System.out.println(request);
                    hasRequests = true;
                }
            }
            if (!hasRequests) {
                System.out.println("--no requests--");
            }
            System.out.println("\nChoose action:\n\t1) Add\n\t2) Delete\n\t3) Back");
            int choice = readAction("Invalid action!", 3);
            if (choice == 1) {
                System.out.println("""
                                Select a request type:
                                    1) MOVIE_ISSUE
                                    2) ACTOR_ISSUE
                                    3) DELETE_ACCOUNT
                                    4) OTHERS
                                """);
                int option = readAction("Invalid request type!", 4);
                switch (option) {
                    case 1: {
                        for (int i = 0; i < imdb.getProductions().size(); i++) {
                            System.out.println((i + 1) + ") " + imdb.getProductions().get(i).title);
                        }
                        System.out.println("\nSelect production index: (type 0 to cancel)");
                        int prod_idx = readIndex("Invalid production!", imdb.getProductions().size());
                        if (prod_idx == -1) {
                            break;
                        }
                        User sender = getContributor(imdb.getProductions().get(prod_idx));
                        boolean sameUser = false;
                        if (sender != null) {
                            sameUser = sender.equals(user);
                        }

                        if (sameUser) {
                            System.out.println("Can't add request to your own contribution.");
                            continue;
                        }
                        System.out.println("Enter request description:");
                        String description = scanner.nextLine();

                        Request request = new Request("MOVIE_ISSUE", LocalDateTime.now().withNano(0), imdb.getProductions().get(prod_idx).title,
                                null, description, user.username, sender != null ? sender.username : "ADMIN");
                        ((RequestsManager) user).createRequest(request);
                        if (sender != null) {
                            ((Staff<?>) sender).requests.add(request);
                        } else {
                            RequestsHolder.requests.add(request);
                        }
                        break;
                    }
                    case 2: {
                        for (int i = 0; i < imdb.getActors().size(); i++) {
                            System.out.println((i + 1) + ") " + imdb.getActors().get(i).name);
                        }
                        System.out.println("\nSelect actor index: (type 0 to cancel)");
                        int act_idx = readIndex("Invalid actor!", imdb.getActors().size());
                        if (act_idx == -1) {
                            break;
                        }
                        User sender = getContributor(imdb.getActors().get(act_idx));
                        boolean sameUser = false;
                        if (sender != null) {
                            sameUser = sender.equals(user);
                        }

                        if (sameUser) {
                            System.out.println("Can't add request to your own contribution.");
                            continue;
                        }
                        System.out.println("Enter request description:");
                        String description = scanner.nextLine();

                        Request request = new Request("ACTOR_ISSUE", LocalDateTime.now().withNano(0), null,
                                imdb.getActors().get(act_idx).name, description, user.username, sender != null ? sender.username : "ADMIN");
                        ((RequestsManager) user).createRequest(request);
                        if (sender != null) {
                            ((Staff<?>) sender).requests.add(request);
                        } else {
                            RequestsHolder.requests.add(request);
                        }
                        break;
                    }
                    case 3: {
                        Request request = new Request("DELETE_ACCOUNT", LocalDateTime.now().withNano(0), null, null,
                                "I want to delete my account.", user.username, "ADMIN");
                        ((RequestsManager) user).createRequest(request);
                        RequestsHolder.requests.add(request);
                        break;
                    }
                    case 4: {
                        System.out.println("Enter request description:");
                        String description = scanner.nextLine();
                        Request request = new Request("OTHERS", LocalDateTime.now().withNano(0), null, null,
                                description, user.username, "ADMIN");
                        ((RequestsManager) user).createRequest(request);
                        RequestsHolder.requests.add(request);
                        break;
                    }
                }
            } else if (choice == 2) {
                if (hasRequests) {
                    ArrayList<Request> curr_requests = new ArrayList<>();
                    int i = 0;
                    for (Request request : imdb.getRequests()) {
                        if (request.requesterUsername.equals(user.username)) {
                            curr_requests.add(request);
                            System.out.println((++i) + ") " + request);
                        }
                    }
                    while (true) {
                        System.out.println("\nEnter request index to delete: (type 0 to cancel)");
                        try {
                            int idx = scanner.nextInt();
                            scanner.nextLine();
                            if (idx == 0) {
                                break;
                            }
                            idx--;
                            // Remove request from main db
                            ((RequestsManager) user).removeRequest(curr_requests.get(idx));

                            // Remove request from staff requests
                            if (curr_requests.get(idx).solverUsername.equals("ADMIN")) {
                                RequestsHolder.requests.remove(curr_requests.get(idx));
                            } else {
                                for (User<?> sender : imdb.getUsers()) {
                                    if (sender instanceof Staff<?>) {
                                        if (((Staff<?>) sender).requests.remove(curr_requests.get(idx))) {
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Invalid request!");
                        } catch (InputMismatchException e) {
                            scanner.nextLine();
                            System.out.println("Invalid request!");
                        }
                    }
                }
            } else if (choice == 3) {
                loop = false;
            }
        }
    }
}
