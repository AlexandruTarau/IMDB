package org.example.CommandStrategies;

import org.example.CommandStrategy;
import org.example.Production;
import org.example.Staff;
import org.example.User;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class UpdateProduction extends AuxMethods implements CommandStrategy {

    @Override
    public void execute(User user) {
        ArrayList<Production> curr_productions = new ArrayList<>();
        while (true) {
            int i = 1;
            System.out.println("\nMy productions:");
            for (Object contribution : ((Staff<?>) user).contributions) {
                if (contribution instanceof Production) {
                    System.out.println(i++ + ") " + ((Production) contribution).title);
                    curr_productions.add((Production) contribution);
                }
            }
            System.out.println("\nEnter production index to update: (type 0 to exit)");
            int idx = readIndex("Invalid production!", curr_productions.size());
            if (idx == -1) {
                break;
            }
            System.out.println();
            ((Staff<?>) user).updateProduction(curr_productions.get(idx));
        }
    }
}
