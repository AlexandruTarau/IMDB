package org.example;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Actor implements Comparable<Object> {
    public String name;
    public ArrayList<String[]> performances;
    public String biography;
    public ImageIcon image;

    public Actor(String name, ArrayList<String[]> performances, String biography, String imagePath) {
        this.name = name;
        this.performances = performances;
        this.biography = biography;
        this.image = new ImageIcon(imagePath);
    }

    public Actor() {

    }

    public int compareTo(Object o) {
        if (o instanceof Actor) {
            return name.compareTo(((Actor) o).name);
        }
        return name.compareTo(((Production) o).title);
    }

    public void displayInfo() {
        System.out.println(this);
    }

    public String toString() {
        String text = "";
        if (name != null) {
            text += "Name: " + name + "\n";
        }
        text += "Performances: " + "\n";
        for (int i = 0; i < performances.size(); i++) {
            text += "\tTitle: " + performances.get(i)[0] + "\n";
            text += "\tType: " + performances.get(i)[1] + "\n";
            if (i != performances.size() - 1) {
                text += "\n";
            }
        }
        if (biography != null) {
            text += "Biography: " + biography + "\n";
        }
        return text;
    }
}
