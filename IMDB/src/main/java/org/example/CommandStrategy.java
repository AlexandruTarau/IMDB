package org.example;

import java.util.InputMismatchException;
import java.util.Scanner;

public interface CommandStrategy {
    void execute(User user);
}
