package org.example.CommandStrategies;

import org.example.Actor;
import org.example.CommandStrategy;
import org.example.IMDB;
import org.example.User;

import java.util.SortedSet;
import java.util.TreeSet;

public class ViewActors extends AuxMethods implements CommandStrategy {
    IMDB imdb = IMDB.getInstance();
    @Override
    public void execute(User user) {
        boolean loop = true;
        for (Actor actor : imdb.getActors()) {
            actor.displayInfo();
        }
        while (loop) {
            System.out.println("\nFilter by:\n\t1) Name\n\t2) Exit");
            int action = readAction("Invalid filtering option!", 2);
            switch (action) {
                case 1: {
                    SortedSet<Actor> actors = new TreeSet<>(imdb.getActors());
                    for (Actor actor : actors) {
                        actor.displayInfo();
                    }
                    break;
                }
                case 2: {
                    loop = false;
                    break;
                }
            }
        }
    }
}
