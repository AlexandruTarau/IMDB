package org.example.CommandStrategies;

import org.example.*;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ManageFavorites extends AuxMethods implements CommandStrategy {
    IMDB imdb = IMDB.getInstance();
    @Override
    public void execute(User user) {
        Scanner scanner = new Scanner(System.in);
        boolean loop = true;
        while (loop) {
            System.out.println("Favorites:");
            boolean hasFavorites = false;
            for (Object favorite : user.favorites) {
                if (favorite instanceof Actor) {
                    System.out.println("(A): " + ((Actor) favorite).name);
                } else if (favorite instanceof Movie) {
                    System.out.println("(M): " + ((Movie) favorite).title);
                } else {
                    System.out.println("(S): " + ((Series) favorite).title);
                }
                hasFavorites = true;
            }
            if (!hasFavorites) {
                System.out.println("--no favorites--");
            }
            System.out.println("\nChoose an action:\n\t1) Add\n\t2) Delete\n\t3) Back");
            int option = readAction("Invalid action!", 3);
            if (option == 1) {
                boolean found = false;
                System.out.println("Search:");
                String search = scanner.nextLine();
                for (Actor actor : imdb.getActors()) {
                    if (actor.name.equals(search)) {
                        found = true;
                        if (user.favorites.contains(actor)) {
                            System.out.println("Actor is already in your favorites list.");
                            break;
                        }
                        actor.displayInfo();
                        System.out.println("Add actor to favorites?\n\t1) Yes\n\t2) No");
                        int confirmation = readAction("Invalid action!", 2);
                        if (confirmation == 1) {
                            user.favorites.add(actor);
                        }
                        break;
                    }
                }
                for (Production production : imdb.getProductions()) {
                    if (production.title.equals(search)) {
                        found = true;
                        if (user.favorites.contains(production)) {
                            System.out.println("Production is already in your favorites list.");
                            break;
                        }
                        production.displayInfo();
                        System.out.println("\tAdd production to favorites?\n1) Yes\n2) No");
                        int confirmation = readAction("Invalid action!", 2);
                        if (confirmation == 1) {
                            user.favorites.add(production);
                        }
                        break;
                    }
                }
                if (!found) {
                    System.out.println("Production / Actor not found!");
                }
            } else if (option == 2) {
                boolean found = false;
                System.out.println("Search:");
                String search = scanner.nextLine();
                for (Object favorite : user.favorites) {
                    if (favorite instanceof Actor) {
                        if (((Actor) favorite).name.equals(search)) {
                            found = true;
                            System.out.println("Delete actor from favorites?\n\t1) Yes\n\t2) No");
                            int confirmation = readAction("Invalid action!", 2);
                            if (confirmation == 1) {
                                user.favorites.remove(favorite);
                            }
                            break;
                        }
                    } else if (favorite instanceof Production) {
                        if (((Production) favorite).title.equals(search)) {
                            found = true;
                            System.out.println("Delete production from favorites?\n\t1) Yes\n\t2) No");
                            int confirmation = readAction("Invalid action!", 2);
                            if (confirmation == 1) {
                                user.favorites.remove(favorite);
                            }
                            break;
                        }
                    }
                }
                if (!found) {
                    System.out.println("Production / Actor not found!");
                }
            } else if (option == 3) {
                loop = false;
            }
        }
    }
}
