package org.example.CommandStrategies;

import org.example.*;

import java.util.Scanner;

public class Search implements CommandStrategy {
    IMDB imdb = IMDB.getInstance();
    @Override
    public void execute(User user) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Search:");
        String name = scanner.nextLine();
        for (Actor actor : imdb.getActors()) {
            if (actor.name.equals(name)) {
                actor.displayInfo();
                return;
            }
        }
        for (Production production : imdb.getProductions()) {
            if (production.title.equals(name)) {
                production.displayInfo();
                return;
            }
        }
        System.out.println("No result.");
    }
}
