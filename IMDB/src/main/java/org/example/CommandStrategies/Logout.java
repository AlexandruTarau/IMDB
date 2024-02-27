package org.example.CommandStrategies;

import org.example.CommandStrategy;
import org.example.IMDB;
import org.example.User;

public class Logout implements CommandStrategy {
    @Override
    public void execute(User user) {
        user.logOut();
    }
}
