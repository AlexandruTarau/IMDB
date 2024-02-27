package org.example.CommandStrategies;

import org.example.CommandStrategy;
import org.example.IMDB;
import org.example.User;

public class ViewNotifications implements CommandStrategy {
    @Override
    public void execute(User user) {
        System.out.println("Your notifications:");
        boolean hasNotifications = false;
        for (Object notification : user.notifications) {
            System.out.println(notification);
            hasNotifications = true;
        }
        if (!hasNotifications) {
            System.out.println("--no notifications--");
        }
        System.out.println();
    }
}
