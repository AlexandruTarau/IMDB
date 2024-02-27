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
import java.util.List;
import java.util.SortedSet;

public class Contributor<T> extends Staff<T> implements RequestsManager {

    public Contributor (Information info, AccountType type, String username, int experience,
                        List<String> notifications, SortedSet<T> favorites,
                        List<Request> requests, SortedSet<T> contributions) {
        super(info, type, username, experience, notifications, favorites, requests, contributions);
    }

    public void createRequest(Request r) {
        for (User user : IMDB.getInstance().getUsers()) {
            if (r.solverUsername.equals("ADMIN")) {
                if (user instanceof Admin<?>) {
                    r.addObserver(user);
                }
            } else if (user.username.equals(r.solverUsername)) {
                r.addObserver(user);
                break;
            }
        }

        r.notifyObservers("Ai primit un request de la \"" + this.username + "\".");
        for (User user : IMDB.getInstance().getUsers()) {
            if (r.solverUsername.equals("ADMIN")) {
                if (user instanceof Admin<?>) {
                    r.removeObserver(user);
                }
            } else if (user.username.equals(r.solverUsername)) {
                r.removeObserver(user);
                break;
            }
        }
        r.addObserver(this);
        IMDB.getInstance().getRequests().add(r);

        JSONParser parser = new JSONParser();
        FileWriter fileWriter = null;
        FileReader requestsFile = null;
        try {
            requestsFile = new FileReader("IMDB\\src\\main\\resources\\input\\requests.json");
            JSONArray requestsArray = (JSONArray) parser.parse(requestsFile);

            // Updating database
            JSONObject newRequest = new JSONObject();
            newRequest.put("type", r.getRequestType().name());
            newRequest.put("createdDate", r.getCreation_date().toString());
            newRequest.put("username", r.requesterUsername);
            newRequest.put("to", r.solverUsername);
            newRequest.put("description", r.problemDescription);
            if (r.actorName != null && !r.actorName.isEmpty()) {
                newRequest.put("actorName", r.actorName);
            } else if (r.productionTitle != null && !r.productionTitle.isEmpty()) {
                newRequest.put("movieTitle", r.productionTitle);
            }

            requestsArray.add(newRequest);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            try {
                fileWriter = new FileWriter("IMDB\\src\\main\\resources\\input\\requests.json");
                fileWriter.write("[\n");
                for (int i = 0; i < requestsArray.size(); i++) {
                    JSONObject jsonObject = (JSONObject) requestsArray.get(i);

                    String formattedJson = objectMapper.writeValueAsString(jsonObject) + (i < requestsArray.size() - 1 ? "," : "") + "\n";

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
                assert requestsFile != null;
                requestsFile.close();
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

    public void removeRequest(Request r) {
        int index = IMDB.getInstance().getRequests().indexOf(r);
        IMDB.getInstance().getRequests().remove(r);

        JSONParser parser = new JSONParser();
        FileWriter fileWriter = null;
        FileReader requestsFile = null;
        try {
            requestsFile = new FileReader("IMDB\\src\\main\\resources\\input\\requests.json");
            JSONArray requestsArray = (JSONArray) parser.parse(requestsFile);
            requestsArray.remove(index);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            try {
                fileWriter = new FileWriter("IMDB\\src\\main\\resources\\input\\requests.json");
                fileWriter.write("[\n");
                for (int i = 0; i < requestsArray.size(); i++) {
                    JSONObject jsonObject = (JSONObject) requestsArray.get(i);

                    String formattedJson = objectMapper.writeValueAsString(jsonObject) + (i < requestsArray.size() - 1 ? "," : "") + "\n";

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
                assert requestsFile != null;
                requestsFile.close();
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
        return username.compareTo(((Contributor<?>) o).username);
    }

    @Override
    public void update(String notification) {
        this.notifications.add(notification);
    }
}
