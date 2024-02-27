package org.example.CommandStrategies;

import org.example.*;

import java.util.ArrayList;
import java.util.Scanner;

public class ManageRatings extends AuxMethods implements CommandStrategy {
    IMDB imdb = IMDB.getInstance();
    @Override
    public void execute(User user) {
        Scanner scanner = new Scanner(System.in);
        boolean loop = true;
        while (loop) {
            System.out.println("Your ratings:");
            boolean hasRatings = false;
            ArrayList<Rating> curr_ratings = new ArrayList<>();
            ArrayList<Production> rated_productions = new ArrayList<>();
            for (Production production : imdb.getProductions()) {
                for (Rating rating : production.ratings) {
                    if (rating.username.equals(user.username)) {
                        curr_ratings.add(rating);
                        rated_productions.add(production);
                        hasRatings = true;
                        System.out.println("<" + production.title + "> (" + rating.rating + ") : " + rating.comments);
                    }
                }
            }
            if (!hasRatings) {
                System.out.println("--no ratings--");
            }
            System.out.println("\nChoose action:\n\t1) Add\n\t2) Delete\n\t3) Back");
            int choice = readAction("Invalid action!", 3);
            if (choice == 1) {
                boolean ok = false;
                while (!ok) {
                    System.out.println("Enter production title:"); // TODO list to choose from
                    String title = scanner.nextLine();

                    for (Production production : imdb.getProductions()) {
                        if (production.title.equals(title)) {
                            boolean alreadyRate = false;
                            ok = true;
                            for (Rating r : curr_ratings) {
                                if (production.ratings.contains(r)) {
                                    System.out.println("You have already rated this production!\n");
                                    alreadyRate = true;
                                    break;
                                }
                            }
                            if (alreadyRate) {
                                break;
                            }
                            System.out.println("Enter your rating:(1 - 10)");
                            int score = readAction("Invalid rating!", 10);
                            System.out.println("Write a comment:");
                            String comment = scanner.nextLine();
                            ((Regular<?>) user).rate(production, score, comment);
                            production.updateAvgRating(score, true);
                        }
                    }
                    if (!ok) {
                        System.out.println("Production not found!");
                    }
                }
            } else if (choice == 2) {
                int i = 0;
                for (Rating rating : curr_ratings) {
                    System.out.println(++i + ") <" + rated_productions.get(i - 1).title + "> (" + rating.rating + ") : " + rating.comments);
                }
                System.out.println("\nEnter rating index to delete: (type 0 to cancel)");
                int idx = readIndex("Invalid rating!", curr_ratings.size());
                if (idx == -1) {
                    break;
                }
                // Remove rating from main db
                rated_productions.get(idx).ratings.remove(curr_ratings.get(idx));
                rated_productions.get(idx).updateAvgRating(curr_ratings.get(idx).rating, false);

                // Remove production from list of ratedProductions
                ((Regular<?>) user).ratedProductions.remove(rated_productions.get(idx));

                // Remove observers
                for (Rating rating : rated_productions.get(idx).ratings) {
                    rating.removeObserver(user);
                }
            } else if (choice == 3) {
                loop = false;
            }
        }
    }
}
