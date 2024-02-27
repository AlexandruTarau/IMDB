package org.example.CommandStrategies;

import org.example.*;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class UpdateActor extends AuxMethods implements CommandStrategy {
    @Override
    public void execute(User user) {
        ArrayList<Actor> curr_actors = new ArrayList<>();
        while (true) {
            int i = 1;
            System.out.println("\nMy actors:");
            for (Object contribution : ((Staff<?>) user).contributions) {
                if (contribution instanceof Actor) {
                    System.out.println(i++ + ") " + ((Actor) contribution).name);
                    curr_actors.add((Actor) contribution);
                }
            }
            System.out.println("\nEnter actor index to update: (type 0 to exit)");
            int idx = readIndex("Invalid actor!", curr_actors.size());
            if (idx == -1) {
                break;
            }
            System.out.println();
            ((Staff<?>) user).updateActor(curr_actors.get(idx));
        }
    }
}
