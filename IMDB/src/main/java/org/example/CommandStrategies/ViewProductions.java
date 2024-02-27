package org.example.CommandStrategies;

import org.example.*;

import java.util.*;

public class ViewProductions extends AuxMethods implements CommandStrategy {
    IMDB imdb = IMDB.getInstance();
    @Override
    public void execute(User user) {
        boolean loop = true;
        for (Production production : IMDB.getInstance().getProductions()) {
            production.displayInfo();
        }
        while (loop) {
            System.out.println("\nFilter by:\n\t1) Genre\n\t2) Number of ratings\n\t3) Exit");
            int action = readAction("Invalid filtering option!", 3);
            switch (action) {
                case 1: {
                    System.out.println("Select genre: (type 0 to cancel)");
                    for (int i = 0; i < Genre.values().length; i++) {
                        System.out.println((i + 1) + ") " + Genre.values()[i]);
                    }
                    int index = readIndex("Invalid genre!", Genre.values().length);
                    if (index == -1) {
                        break;
                    }
                    for (Production production : imdb.getProductions()) {
                        if (production.genres.contains(Genre.values()[index])) {
                            production.displayInfo();
                        }
                    }
                    break;
                }
                case 2: {
                    System.out.println("Enter minimum number of ratings:");
                    int number = readUIntLoop("Invalid number of ratings!", true);
                    for (Production production : IMDB.getInstance().getProductions()) {
                        if (production.ratings.size() >= number) {
                            production.displayInfo();
                        }
                    }
                    break;
                }
                case 3: {
                    loop = false;
                    break;
                }
            }
        }

    }
}
