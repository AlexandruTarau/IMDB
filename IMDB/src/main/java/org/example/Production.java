package org.example;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Production implements Comparable<Object> {
    public String title;
    public List<String> directors;
    public List<String> actors;
    public List<Genre> genres;
    public List<Rating> ratings;
    public String plot;
    public Double averageRating;
    public String imagePath;
    public ImageIcon image;

    public Production(String title, List<String> directors, List<String> actors, List<Genre> genres,
                      List<Rating> ratings, String plot, Double averageRating, String imagePath) {
        this.title = title;
        this.directors = directors;
        this.actors = actors;
        this.genres = genres;
        this.ratings = ratings;
        this.plot = plot;
        this.averageRating = averageRating;
        this.imagePath = imagePath;
        this.image = new ImageIcon(imagePath);
    }

    public Production() {

    }
    public void updateAvgRating(int rating, boolean add) {
        if (averageRating == 0.0) {
            averageRating = rating + 0.0;
        } else {
            int n = ratings.size();
            if (add) {
                averageRating = (averageRating * (n - 1) + rating) / n;
            } else {
                averageRating = (averageRating * (n + 1) - rating) / n;
            }
            averageRating = Math.round(averageRating * 10) / 10.0;
        }
    }
    public abstract void displayInfo();

    public int compareTo(@NotNull Object o) {
        if (o instanceof Production) {
            return title.compareTo(((Production) o).title);
        }
        return title.compareTo(((Actor) o).name);
    }
    User getUser(String username) {
        for (User user : IMDB.getInstance().getUsers()) {
            if (user.username.equals(username)) {
                return user;
            }
        }
        return null;
    }
}
