package org.example;

import java.util.*;

public class Series extends Production {
    public int releaseYear;
    public int numSeasons;
    private Map<String, List<Episode>> seasons;

    public Series(String title, List<String> directors, List<String> actors, List<Genre> genres, List<Rating> ratings,
                  String plot, Double avg_rating, int releaseYear, int numSeasons, Map<String, List<Episode>> seasons, String imagePath) {
        super(title, directors, actors, genres, ratings, plot, avg_rating, imagePath);
        this.releaseYear = releaseYear;
        this.numSeasons = numSeasons;
        this.seasons = seasons;
    }

    public Series() {

    }

    public Map<String, List<Episode>> getSeasons() {
        return seasons;
    }

    public void setSeasons(Map<String, List<Episode>> seasons) {
        this.seasons = seasons;
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
        if (releaseYear != 0) {
            text += "Release year: " + releaseYear + "\n";
        }
        if (numSeasons != 0) {
            text += "Number of seasons: " + numSeasons + "\n";
        }
        if (seasons != null) {
            text += "Seasons:" + "\n";
            for (Map.Entry<String, List<Episode>> entry : seasons.entrySet()) {
                String seasonName = entry.getKey();
                List<Episode> episodes = entry.getValue();

                text += seasonName + "\n";
                int i = 0;
                for (Episode episode : episodes) {
                    text += "\t" + ++i + ") " + episode + "\n";
                }
            }
        }

        return text;
    }
}
