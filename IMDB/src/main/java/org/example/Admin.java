package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class Admin<T> extends Staff<T> {
    public static SortedSet<Object> common_contributions = new TreeSet<>();

    public Admin (Information info, AccountType type, String username, int experience,
                  List<String> notifications, SortedSet<T> favorites,
                  List<Request> requests, SortedSet<T> contributions) {
        super(info, type, username, experience, notifications, favorites, requests, contributions);
    }

    public void addUser(User u) {
        IMDB imdb = IMDB.getInstance();
        imdb.getUsers().add(u);

        JSONParser parser = new JSONParser();
        FileWriter fileWriter = null;
        FileReader usersFile = null;
        try {
            usersFile = new FileReader("IMDB\\src\\main\\resources\\input\\accounts.json");
            JSONArray usersArray = (JSONArray) parser.parse(usersFile);

            // Updating database
            JSONObject newUser = new JSONObject();
            newUser.put("username", u.username);
            newUser.put("experience", u.experience == -1 ? null : u.experience);

            JSONObject informationObject = new JSONObject();
            JSONObject credentialsObject = new JSONObject();

            credentialsObject.put("email", u.info.getCredentials().getEmail());
            credentialsObject.put("password", u.info.getCredentials().getPassword());

            informationObject.put("credentials", credentialsObject);
            informationObject.put("name", u.info.name);
            informationObject.put("country", u.info.country);
            informationObject.put("age", u.info.age);
            informationObject.put("gender", String.valueOf(u.info.gender).equals("M") ? "Male" :
                    (String.valueOf(u.info.gender).equals("F") ? "Female" : "Non defined"));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = u.info.getBirth_date().format(formatter);
            informationObject.put("birthDate", formattedDate);

            newUser.put("information", informationObject);
            newUser.put("userType", u.type.name());

            if (u.favorites != null && !u.favorites.isEmpty()) {
                JSONArray favoriteProductions = new JSONArray();
                JSONArray favoriteActors = new JSONArray();

                for (Object favorite : u.favorites) {
                    if (favorite instanceof Production) {
                        favoriteProductions.add(favorite);
                    } else {
                        favoriteActors.add(favorite);
                    }
                }
                newUser.put("favoriteProductions", favoriteProductions);
                newUser.put("favoriteActors", favoriteActors);
            }
            if (u.notifications != null && !u.notifications.isEmpty()) {
                JSONArray notifications = new JSONArray();
                notifications.addAll(u.notifications);

                newUser.put("notifications", notifications);
            }

            if (u instanceof Staff<?>) {
                if (((Staff<?>) u).contributions != null && !((Staff<?>) u).contributions.isEmpty()) {
                    JSONArray productionsContribution = new JSONArray();
                    JSONArray actorsContribution = new JSONArray();

                    for (Object contribution : ((Staff<?>) u).contributions) {
                        if (contribution instanceof Production) {
                            productionsContribution.add(contribution);
                        } else {
                            actorsContribution.add(contribution);
                        }
                    }
                    newUser.put("productionsContribution", productionsContribution);
                    newUser.put("actorsContribution", actorsContribution);
                }
            }
            usersArray.add(newUser);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            try {
                fileWriter = new FileWriter("IMDB\\src\\main\\resources\\input\\accounts.json");
                fileWriter.write("[\n");
                for (int i = 0; i < usersArray.size(); i++) {
                    JSONObject jsonObject = (JSONObject) usersArray.get(i);

                    String formattedJson = objectMapper.writeValueAsString(jsonObject) + (i < usersArray.size() - 1 ? "," : "") + "\n";

                    fileWriter.write(formattedJson);
                }
                fileWriter.write("]");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
            try {
                assert usersFile != null;
                usersFile.close();
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
    public void removeUser(User u) {
        if (u instanceof Staff<?>) {
            common_contributions.addAll(((Staff<?>) u).contributions);
        }
        IMDB imdb = IMDB.getInstance();
        int index = imdb.getUsers().indexOf(u);
        imdb.getUsers().remove(u);

        JSONParser parser = new JSONParser();
        FileWriter fileWriter = null;
        FileReader usersFile = null;

        try {
            usersFile = new FileReader("IMDB\\src\\main\\resources\\input\\accounts.json");
            JSONArray usersArray = (JSONArray) parser.parse(usersFile);
            usersArray.remove(index);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            try {
                fileWriter = new FileWriter("IMDB\\src\\main\\resources\\input\\accounts.json");
                fileWriter.write("[\n");
                for (int i = 0; i < usersArray.size(); i++) {
                    JSONObject jsonObject = (JSONObject) usersArray.get(i);

                    String formattedJson = objectMapper.writeValueAsString(jsonObject) + (i < usersArray.size() - 1 ? "," : "") + "\n";

                    fileWriter.write(formattedJson);
                }
                fileWriter.write("]");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
            try {
                assert usersFile != null;
                usersFile.close();
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


    public int compareTo(@NotNull T o) {
        return username.compareTo(((Admin<?>) o).username);
    }

    @Override
    public void update(String notification) {
        this.notifications.add(notification);
    }
}
