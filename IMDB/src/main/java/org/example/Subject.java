package org.example;

import javax.management.Notification;
import java.util.ArrayList;

public interface Subject {
    void addObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers(String notification);
}
