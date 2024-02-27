package org.example;

import javax.management.Notification;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Rating implements Subject {
    public String username;
    public int rating;
    public String comments;
    ArrayList<Observer> obs = new ArrayList<>();
    public Rating(String username, int rating, String comments) {
        this.username = username;
        this.rating = rating;
        this.comments = comments;
    }

    public Rating() {

    }
    public void addObserver(Observer o) {
        if (!obs.contains(o)) {
            obs.add(o);
        }
    }
    public void removeObserver(Observer o) {
        obs.remove(o);
    }
    public void notifyObservers(String notification) {
        for (int i = obs.size() - 1; i >= 0; i--) {
            (obs.get(i)).update(notification);
        }
    }

    public void notifyRating(Production production) {
        if (production instanceof Movie) {
            notifyObservers("Filmul \"" + production.title + "\" pe care l-ai evaluat a primit un review de la utilizatorul \"" + username + "\" -> " + rating);
        } else {
            notifyObservers("Serialul \"" + production.title + "\" pe care l-ai evaluat a primit un review de la utilizatorul \"" + username + "\" -> " + rating);
        }
    }

    @Override
    public String toString() {
        return username + ": (" + rating + ") : " + comments;
    }
}
