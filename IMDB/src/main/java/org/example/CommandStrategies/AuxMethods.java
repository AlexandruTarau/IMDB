package org.example.CommandStrategies;

import java.util.InputMismatchException;
import java.util.Scanner;

public class AuxMethods {
    public int readUIntLoop(String err, boolean canBeZero) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                int nr = scanner.nextInt();
                scanner.nextLine();
                if (nr < 0 || (nr == 0 && !canBeZero)) {
                    System.out.println(err);
                    continue;
                }
                return nr;
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println(err);
            }
        }
    }
    public int readIndex(String err, int size) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                int index = scanner.nextInt();
                scanner.nextLine();
                index--;
                if (index >= -1 && index < size) {
                    return index;
                }
                System.out.println(err);
            } catch (InputMismatchException e) {
                System.out.println(err);
                scanner.nextLine();
            }
        }
    }
    public int readAction(String err, int max) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                int action = scanner.nextInt();
                scanner.nextLine();
                if (action >= 1 && action <= max) {
                    return action;
                }
                System.out.println(err);
            } catch (InputMismatchException e) {
                System.out.println(err);
                scanner.nextLine();
            }
        }
    }
}
