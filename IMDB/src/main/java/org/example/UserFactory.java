package org.example;

import java.util.List;
import java.util.SortedSet;

public class UserFactory<T> {

    public User createUser(AccountType accountType, User.Information info, String username,
                                  int experience, List<String> notifications, SortedSet<T> favorites,
                                  List<Request> requests, SortedSet<T> contributions) {
        switch (accountType) {
            case Regular: return new Regular<>(info, accountType, username, experience, notifications, favorites);
            case Contributor: return new Contributor<>(info, accountType, username, experience, notifications, favorites, requests, contributions);
            case Admin: return new Admin<>(info, accountType, username, -1, notifications, favorites, requests, contributions);
        }
        throw new IllegalArgumentException("The user type " + accountType + " is not recognized.");
    }
}
