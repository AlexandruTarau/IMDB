package org.example;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Movie extends Production {
    public int duration;
    public int releaseYear;

    public Movie(String title, List<String> directors, List<String> actors, List<Genre> genres,
                 List<Rating> ratings, String plot, Double avg_rating, int duration, int releaseYear, String imagePath) {
        super(title, directors, actors, genres, ratings, plot, avg_rating, imagePath);
        this.duration = duration;
        this.releaseYear = releaseYear;
    }

    public Movie() {

    }

    public void displayInfo() {
        System.out.println(this);
    }

    public String toString() {
        String text = "";
        if (title != null) {
            text += "Title: " + title + "\n";
        }
        if (directors != null) {
            text += "Directors: " + directors + "\n";
        }
        if (actors != null) {
            text += "Actors: " + actors + "\n";
        }
        if (genres != null) {
            text += "Genres: " + genres + "\n";
        }
        if (ratings != null && ratings.size() > 1) {
            List<Rating> sortedRatings = new ArrayList<>(ratings);
            sortedRatings.sort(new Comparator<>() {
                @Override
                public int compare(Rating obj1, Rating obj2) {
                    User user1 = Objects.requireNonNull(getUser(obj1.username));
                    User user2 = Objects.requireNonNull(getUser(obj2.username));
                    return Integer.compare(user2.experience == -1 ? Integer.MAX_VALUE : user2.experience,
                            user1.experience == -1 ? Integer.MAX_VALUE : user1.experience);
                }
            });
            text += "Ratings:" + "\n";
            int i = 0;
            for (Rating rating : sortedRatings) {
                text += "\t" + ++i + ") " + rating + "\n";
            }
        }
        if (plot != null) {
            text += "Plot: " + plot + "\n";
        }
        if (averageRating != null) {
            text += "Average rating: " + (averageRating == 0.0 ? "undetermined" : averageRating) + "\n";
        }
        if (duration != 0) {
            text += "Duration: " + duration + " min" + "\n";
        }
        if (releaseYear != 0) {
            text += "Release year: " + releaseYear + "\n";
        }

        return text;
    }
}
