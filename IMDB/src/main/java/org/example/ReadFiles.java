package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ReadFiles<T> {
    IMDB imdb = IMDB.getInstance();
    public void load_productions() {
        JSONParser parser = new JSONParser();
        FileReader reader = null;

        try {
            File f = new File("IMDB\\src\\main\\resources\\input\\production.json");

            reader = new FileReader(f);
            JSONArray productions = (JSONArray) parser.parse(reader);

            // Extract information for each production
            for (Object productionObj : productions) {
                JSONObject production = (JSONObject) productionObj;

                String title = (String) production.get("title");
                String type = (String) production.get("type");
                JSONArray directorsJSON = (JSONArray) production.get("directors");
                JSONArray actorsJSON = (JSONArray) production.get("actors");
                JSONArray genresJSON = (JSONArray) production.get("genres");
                JSONArray ratingsJSON = (JSONArray) production.get("ratings");
                String plot = (String) production.get("plot");
                double averageRating = ((Number) production.get("averageRating")).doubleValue();
                String imagePath = (String) production.get("imagePath");

                int releaseYear;
                if (production.get("releaseYear") == null) {
                    releaseYear = 0;
                } else {
                    releaseYear = (int) (long) production.get("releaseYear");
                }

                List<String> directors = new ArrayList<>();
                List<String> actors = new ArrayList<>();
                List<Genre> genres = new ArrayList<>();
                List<Rating> ratings = new ArrayList<>();

                for (Object director : directorsJSON) {
                    directors.add((String) director);
                }
                for (Object actor : actorsJSON) {
                    actors.add((String) actor);
                    boolean ok = false;
                    for (Actor a : imdb.getActors()) {
                        if (a.name.equals(actor)) {
                            ok = true;
                            break;
                        }
                    }
                    if (!ok) {
                        FileWriter fileWriter = null;
                        FileReader actorsFile = null;
                        try {
                            actorsFile = new FileReader("IMDB\\src\\main\\resources\\input\\actors.json");
                            JSONArray actorsArray = (JSONArray) parser.parse(actorsFile);

                            ArrayList<String[]> performances = new ArrayList<>();
                            String[] performance = new String[2];
                            performance[0] = title;
                            performance[1] = type;
                            performances.add(performance);

                            // Updating database
                            JSONObject newActor = new JSONObject();
                            newActor.put("name", actor);
                            newActor.put("imagePath", "IMDB\\src\\main\\java\\org\\example\\GUI\\images\\unknownImage.png");

                            JSONArray performancesArray = new JSONArray();
                            for (String[] p : performances) {
                                JSONObject performanceObject = new JSONObject();
                                performanceObject.put("title", p[0]);
                                performanceObject.put("type", p[1]);

                                performancesArray.add(performanceObject);
                            }
                            newActor.put("performances", performancesArray);
                            newActor.put("biography", null);

                            actorsArray.add(newActor);

                            ObjectMapper objectMapper = new ObjectMapper();
                            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
                            try {
                                fileWriter = new FileWriter("IMDB\\src\\main\\resources\\input\\actors.json");
                                fileWriter.write("[\n");
                                for (int i = 0; i < actorsArray.size(); i++) {
                                    JSONObject jsonObject = (JSONObject) actorsArray.get(i);

                                    String formattedJson = objectMapper.writeValueAsString(jsonObject) + (i < actorsArray.size() - 1 ? "," : "") + "\n";

                                    fileWriter.write(formattedJson);
                                }
                                fileWriter.write("]");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            // Add actor
                            Actor new_actor = new Actor((String) actor, performances, null, "IMDB\\src\\main\\java\\org\\example\\GUI\\images\\unknownImage.png");
                            imdb.getActors().add(new_actor);
                            Admin.common_contributions.add(new_actor);
                        } catch (IOException | ParseException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                assert actorsFile != null;
                                actorsFile.close();
                            } catch (IOException e) {
                                System.out.println("Couldn't close file.");
                                e.printStackTrace();
                            } finally {
                                try {
                                    assert fileWriter != null;
                                    fileWriter.close();
                                } catch (IOException e) {
                                    System.out.println("Couldn't close file.");
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                for (Object genre : genresJSON) {
                    genres.add(Genre.valueOf((String) genre));
                }
                for (Object ratingObj : ratingsJSON) {
                    JSONObject ratingJSON = (JSONObject) ratingObj;
                    String comment = (String) ratingJSON.get("comment");
                    Rating rating = new Rating((String) ratingJSON.get("username"), (int) (long) ratingJSON.get("rating"), comment);
                    ratings.add(rating);
                }

                if (type.equals("Movie")) {
                    String durationStr = (String) production.get("duration");
                    durationStr = durationStr.replaceAll("[^0-9]", "");
                    int duration;
                    if (durationStr.isEmpty()) {
                        duration = 0;
                    } else {
                        duration = Integer.parseInt(durationStr);
                    }

                    Movie movie = new Movie(title, directors, actors, genres, ratings, plot, averageRating, duration, releaseYear, imagePath);
                    imdb.getProductions().add(movie);
                } else {
                    int numSeasons = (int) (long) production.get("numSeasons");

                    JSONObject seasonsJSON = (JSONObject) production.get("seasons");
                    Map<String, List<Episode>> seasons = new LinkedHashMap<>(numSeasons);

                    for (int i = 1; i <= numSeasons; i++) {
                        String seasonName = "Season " + i;

                        JSONArray season = (JSONArray) seasonsJSON.get(seasonName);
                        List<Episode> episodes = new ArrayList<>();
                        for (Object episodeObj : season) {
                            JSONObject episodeJSON = (JSONObject) episodeObj;
                            String name = (String) episodeJSON.get("episodeName");
                            String durationStr = (String) episodeJSON.get("duration");
                            durationStr = durationStr.replaceAll("[^0-9]", "");
                            int duration;
                            if (durationStr.isEmpty()) {
                                duration = 0;
                            } else {
                                duration = Integer.parseInt(durationStr);
                            }

                            Episode episode = new Episode(name, duration);
                            episodes.add(episode);
                        }
                        seasons.put(seasonName, episodes);
                    }
                    Series series  = new Series(title, directors, actors, genres, ratings, plot, averageRating, releaseYear, numSeasons, seasons, imagePath);
                    imdb.getProductions().add(series);
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("Couldn't find file.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Couldn't read file.");
            e.printStackTrace();
        } catch (ParseException e) {
            System.out.println("Couldn't parse data.");
            e.printStackTrace();
        } finally {
            try {
                assert reader != null;
                reader.close();
            } catch (IOException e) {
                System.out.println("Couldn't close file.");
                e.printStackTrace();
            }
        }
    }

    public void load_accounts() {
        JSONParser parser = new JSONParser();
        FileReader reader = null;

        try {
            File f = new File("IMDB\\src\\main\\resources\\input\\accounts.json");
            reader = new FileReader(f);
            JSONArray accounts = (JSONArray) parser.parse(reader);

            for (Object userObject : accounts) {
                JSONObject user = (JSONObject) userObject;

                // Extract data from each user in object
                String username = (String) user.get("username");

                Object experienceObj = user.get("experience");
                int experience = 0;
                if (experienceObj instanceof String) {
                    experience = Integer.parseInt((String) experienceObj);
                } else if (experienceObj instanceof Number) {
                    experience = ((Number) experienceObj).intValue();
                } else if (experienceObj == null) {
                    experience = -1;
                }

                JSONObject info = (JSONObject) user.get("information");
                String name = (String) info.get("name");
                String country = (String) info.get("country");
                long age = (long)info.get("age");
                String gender = (String) info.get("gender");
                String birth_date = (String) info.get("birthDate");

                JSONObject credentials = (JSONObject) info.get("credentials");
                String email = (String) credentials.get("email");
                String password = (String) credentials.get("password");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDateTime date = LocalDateTime.of(LocalDate.parse(birth_date, formatter), LocalTime.NOON);

                // Information
                User.Information information = new User.Information.InformationBuilder()
                        .name(name)
                        .age((int) age)
                        .country(country)
                        .gender(gender.charAt(0))
                        .credentials(new Credentials(email, password))
                        .birth_date(date)
                        .build();

                // Favorites
                SortedSet<T> favorites = new TreeSet<>();
                JSONArray jsonArray;
                jsonArray = (JSONArray) user.get("favoriteProductions");
                if (jsonArray != null) {
                    for (Object item : jsonArray) {
                        for (Production production : imdb.getProductions()) {
                            // Find production
                            if (production.title.equals(item)) {
                                favorites.add((T) production);
                                break;
                            }
                        }
                    }
                }
                jsonArray = (JSONArray) user.get("favoriteActors");
                if (jsonArray != null) {
                    for (Object item : jsonArray) {
                        for (Actor actor : imdb.getActors()) {
                            // Find actor
                            if (actor.name.equals(item)) {
                                favorites.add((T) actor);
                                break;
                            }
                        }
                    }
                }

                // Contributions
                List<Request> requests = new ArrayList<>();
                SortedSet<T> contributions = new TreeSet<>();
                jsonArray = (JSONArray) user.get("productionsContribution");

                if (jsonArray != null) {
                    for (Object item : jsonArray) {
                        for (Production production : imdb.getProductions()) {
                            if (production.title.equals(item)) {
                                contributions.add((T) production);
                            }
                        }
                    }
                }

                jsonArray = (JSONArray) user.get("actorsContribution");
                if (jsonArray != null) {
                    for (Object item : jsonArray) {
                        for (Actor actor : imdb.getActors()) {
                            if (actor.name.equals(item)) {
                                contributions.add((T) actor);
                                break;
                            }
                        }
                    }
                }

                // Notifications
                List<String> notifications = new ArrayList<>();
                jsonArray = (JSONArray) user.get("notifications");
                if (jsonArray != null) {
                    for (Object item : jsonArray) {
                        notifications.add((String) item);
                    }
                }

                // Type
                AccountType type = AccountType.valueOf(((String) user.get("userType")));

                UserFactory<T> userFactory = new UserFactory<>();
                User new_user = userFactory.createUser(type, information, username, experience, notifications,
                        favorites, requests, contributions);
                imdb.getUsers().add(new_user);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't find file.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Couldn't read file.");
            e.printStackTrace();
        } catch (ParseException e) {
            System.out.println("Couldn't parse data.");
            e.printStackTrace();
        } finally {
            try {
                assert reader != null;
                reader.close();
            } catch (IOException e) {
                System.out.println("Couldn't close file.");
                e.printStackTrace();
            }
        }
    }

    public void load_actors() {
        JSONParser parser = new JSONParser();
        FileReader reader = null;

        try {
            File f = new File("IMDB\\src\\main\\resources\\input\\actors.json");
            reader = new FileReader(f);
            JSONArray actorsJSON = (JSONArray) parser.parse(reader);

            for (Object actor : actorsJSON) {
                JSONObject actorJSON = (JSONObject) actor;
                String name = (String) actorJSON.get("name");
                String biography = (String) actorJSON.get("biography");
                String imagePath = (String) actorJSON.get("imagePath");

                JSONArray performancesJSON = (JSONArray) actorJSON.get("performances");
                ArrayList<String[]> performances = new ArrayList<>();
                for (Object performanceObj : performancesJSON) {
                    JSONObject performanceJSON = (JSONObject) performanceObj;
                    String[] performance = new String[2];
                    performance[0] = (String) performanceJSON.get("title");
                    performance[1] = (String) performanceJSON.get("type");
                    performances.add(performance);
                }
                Actor new_actor = new Actor(name, performances, biography, imagePath);
                imdb.getActors().add(new_actor);
            }


        } catch (FileNotFoundException e) {
            System.out.println("Couldn't find file.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Couldn't read file.");
            e.printStackTrace();
        } catch (ParseException e) {
            System.out.println("Couldn't parse data.");
            e.printStackTrace();
        } finally {
            try {
                assert reader != null;
                reader.close();
            } catch (IOException e) {
                System.out.println("Couldn't close file.");
                e.printStackTrace();
            }
        }
    }

    public void load_requests() {
        JSONParser parser = new JSONParser();
        FileReader reader = null;

        try {
            File f = new File("IMDB\\src\\main\\resources\\input\\requests.json");
            reader = new FileReader(f);
            JSONArray requestsJSON = (JSONArray) parser.parse(reader);

            for (Object requestObj : requestsJSON) {
                JSONObject requestJSON = (JSONObject) requestObj;
                // Type
                String type = (String) requestJSON.get("type");
                // Date
                String date = (String) requestJSON.get("createdDate");
                DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                LocalDateTime createdDate = LocalDateTime.parse(date, formatter);
                // Username
                String username = (String) requestJSON.get("username");
                // Solver
                String solver = (String) requestJSON.get("to");
                // Description
                String description = (String) requestJSON.get("description");
                // Actor name
                String actorName = (String) requestJSON.get("actorName");
                String movieTitle = (String) requestJSON.get("movieTitle");

                Request request = new Request(type, createdDate, movieTitle, actorName, description, username, solver);
                imdb.getRequests().add(request);

                for (User user : imdb.getUsers()) {
                    if (user.username.equals(solver)) {
                        ((Staff) user).requests.add(request);
                        break;
                    } else if (user.type == AccountType.Admin && solver.equals("ADMIN")) {
                        RequestsHolder.requests.add(request);
                        break;
                    }
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("Couldn't find file.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Couldn't read file.");
            e.printStackTrace();
        } catch (ParseException e) {
            System.out.println("Couldn't parse data.");
            e.printStackTrace();
        } finally {
            try {
                assert reader != null;
                reader.close();
            } catch (IOException e) {
                System.out.println("Couldn't close file.");
                e.printStackTrace();
            }
        }
    }

}
