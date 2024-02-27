package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public abstract class Staff<T> extends User implements StaffInterface {
    public List<Request> requests;
    public SortedSet<T> contributions;

    public Staff (Information info, AccountType type, String username, int experience,
                    List<String> notifications, SortedSet<T> favorites,
                  List<Request> requests, SortedSet<T> contributions) {
        super(info, type, username, experience, notifications, favorites);
        this.requests = requests;
        this.contributions = contributions;
    }

    public Staff() {

    }
    @Override
    public void addProductionSystem(Production p) {
        contributions.add((T) p);
        IMDB.getInstance().getProductions().add(p);

        // Update database
        JSONParser parser = new JSONParser();
        FileWriter fileWriter = null;
        FileReader productionsFile = null;
        try {
            productionsFile = new FileReader("IMDB\\src\\main\\resources\\input\\production.json");
            JSONArray productionsArray = (JSONArray) parser.parse(productionsFile);

            // Updating database
            JSONObject newProduction = new JSONObject();
            newProduction.put("title", p.title);
            newProduction.put("type", p instanceof Movie ? "Movie" : "Series");
            newProduction.put("imagePath", "IMDB\\src\\main\\java\\org\\example\\GUI\\images\\unknownImage.png");

            JSONArray directorsArray = new JSONArray();
            JSONArray actorsArray = new JSONArray();
            JSONArray genresArray = new JSONArray();
            directorsArray.addAll(p.directors);
            actorsArray.addAll(p.actors);
            genresArray.addAll(p.genres);
            newProduction.put("directors", directorsArray);
            newProduction.put("actors", actorsArray);
            newProduction.put("genres", genresArray);

            JSONArray ratingsArray = new JSONArray();
            for (Rating rating : p.ratings) {
                JSONObject ratingObject = new JSONObject();
                ratingObject.put("username", rating.username);
                ratingObject.put("rating", rating.rating);
                ratingObject.put("comment", rating.comments);

                ratingsArray.add(ratingObject);
            }
            newProduction.put("ratings", ratingsArray);

            newProduction.put("plot", p.plot);
            newProduction.put("averageRating", p.averageRating);
            if (p instanceof Movie) {
                newProduction.put("duration", String.valueOf(((Movie) p).duration));
                newProduction.put("releaseYear", ((Movie) p).releaseYear);
            } else {
                newProduction.put("releaseYear", ((Series) p).releaseYear);
                newProduction.put("numSeasons", ((Series) p).numSeasons);

                JSONObject seasonsObject = new JSONObject();
                for (Map.Entry<String, List<Episode>> entry : ((Series) p).getSeasons().entrySet()) {
                    String seasonTitle = entry.getKey();
                    List<Episode> episodes = entry.getValue();
                    JSONArray episodesArray = new JSONArray();
                    for (Episode episode : episodes) {
                        JSONObject episodeObject = new JSONObject();
                        episodeObject.put("episodeName", episode.name);
                        episodeObject.put("duration", String.valueOf(episode.duration));

                        episodesArray.add(episodeObject);
                    }

                    seasonsObject.put(seasonTitle, episodesArray);
                }
            }
            productionsArray.add(newProduction);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            try {
                fileWriter = new FileWriter("IMDB\\src\\main\\resources\\input\\production.json");
                fileWriter.write("[\n");
                for (int i = 0; i < productionsArray.size(); i++) {
                    JSONObject jsonObject = (JSONObject) productionsArray.get(i);

                    String formattedJson = objectMapper.writeValueAsString(jsonObject) + (i < productionsArray.size() - 1 ? "," : "") + "\n";

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
                assert productionsFile != null;
                productionsFile.close();
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

    @Override
    public void addActorSystem(Actor a) {
        contributions.add((T) a);
        IMDB.getInstance().getActors().add(a);

        // Update database
        JSONParser parser = new JSONParser();
        FileWriter fileWriter = null;
        FileReader actorsFile = null;
        try {
            actorsFile = new FileReader("IMDB\\src\\main\\resources\\input\\actors.json");
            JSONArray actorsArray = (JSONArray) parser.parse(actorsFile);

            // Updating database
            JSONObject newActor = new JSONObject();
            newActor.put("name", a.name);
            newActor.put("imagePath", "IMDB\\src\\main\\java\\org\\example\\GUI\\images\\unknownImage.png");

            JSONArray performancesArray = new JSONArray();
            for (String[] p : a.performances) {
                JSONObject performanceObject = new JSONObject();
                performanceObject.put("title", p[0]);
                performanceObject.put("type", p[1]);

                performancesArray.add(performanceObject);
            }
            newActor.put("performances", performancesArray);
            newActor.put("biography", a.biography);

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

    @Override
    public void removeProductionSystem(String name) {
        for (T contribution : contributions) {
            if (contribution instanceof Production) {
                if (((Production) contribution).title.equals(name)) {
                    contributions.remove(contribution);
                    int index = IMDB.getInstance().getProductions().indexOf(contribution);
                    IMDB.getInstance().getProductions().remove(contribution);

                    // Update database
                    JSONParser parser = new JSONParser();
                    FileWriter fileWriter = null;
                    FileReader productionsFile = null;

                    try {
                        productionsFile = new FileReader("IMDB\\src\\main\\resources\\input\\production.json");
                        JSONArray productionsArray = (JSONArray) parser.parse(productionsFile);
                        productionsArray.remove(index);

                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
                        try {
                            fileWriter = new FileWriter("IMDB\\src\\main\\resources\\input\\production.json");
                            fileWriter.write("[\n");
                            for (int i = 0; i < productionsArray.size(); i++) {
                                JSONObject jsonObject = (JSONObject) productionsArray.get(i);

                                String formattedJson = objectMapper.writeValueAsString(jsonObject) + (i < productionsArray.size() - 1 ? "," : "") + "\n";

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
                            assert productionsFile != null;
                            productionsFile.close();
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
                    break;
                }
            }
        }
    }

    @Override
    public void removeActorSystem(String name) {
        for (T contribution : contributions) {
            if (contribution instanceof Actor) {
                if (((Actor) contribution).name.equals(name)) {
                    int index = IMDB.getInstance().getActors().indexOf(contribution);
                    contributions.remove(contribution);
                    IMDB.getInstance().getActors().remove(contribution);

                    // Update database
                    JSONParser parser = new JSONParser();
                    FileWriter fileWriter = null;
                    FileReader actorsFile = null;
                    try {
                        actorsFile = new FileReader("IMDB\\src\\main\\resources\\input\\actors.json");
                        JSONArray actorsArray = (JSONArray) parser.parse(actorsFile);
                        actorsArray.remove(index);

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
                    break;
                }
            }
        }
    }

    @Override
    public void updateProduction(Production p) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            p.displayInfo();
            System.out.print("""
            1) Modify title
            2) Modify directors
            3) Modify actors
            4) Modify genres
            5) Modify plot
            """);
            if (p instanceof Movie) {
                System.out.println("""
                6) Modify duration
                7) Modify release year
                8) Exit
                """);
            } else {
                System.out.println("""
                6) Modify release year
                7) Modify seasons
                8) Exit
                """);
            }
            System.out.println("Select index of action:");
            int idx = readAction("Invalid action!", 8);
            boolean fork = false;
            switch (idx) {
                case 1: {
                    System.out.println("Current title: " + p.title);
                    System.out.println("New title: ");
                    while (true) {
                        String title = scanner.nextLine();
                        boolean ok = true;
                        for (Production production : IMDB.getInstance().getProductions()) {
                            if (production.title.equals(title)) {
                                ok = false;
                                System.out.println("Title taken!");
                                break;
                            }
                        }
                        if (ok) {
                            p.title = title;
                            break;
                        }
                    }
                    break;
                }
                case 2: {
                    boolean loop = true;
                    while (loop) {
                        System.out.println("Current directors:");
                        for (int i = 0; i < p.directors.size(); i++) {
                            System.out.println((i + 1) + ") " + p.directors.get(i));
                        }
                        System.out.println("Choose action:\n\t1) Add\n\t2) Delete\n\t3) Modify\n\t4) Cancel");
                        int action = readAction("Invalid action!", 4);
                        switch (action) {
                            case 1: {
                                System.out.println("Enter new director:");
                                while (true) {
                                    String director = scanner.nextLine();
                                    boolean ok = true;
                                    for (String d : p.directors) {
                                        if (d.equals(director)) {
                                            ok = false;
                                            System.out.println("Director already exists!");
                                            break;
                                        }
                                    }
                                    if (ok) {
                                        p.directors.add(director);
                                        break;
                                    }
                                }
                                break;
                            }
                            case 2: {
                                System.out.println("Enter index of director to delete: (type 0 to cancel)");
                                int index = readIndex("Invalid director!", p.directors.size());
                                if (index == -1) {
                                    break;
                                }
                                p.directors.remove(index);
                                break;
                            }
                            case 3: {
                                System.out.println("\nEnter index of director to modify: (type 0 to cancel)");
                                int index = readIndex("Invalid director!", p.directors.size());
                                if (index == -1) {
                                    break;
                                }
                                System.out.println("\nCurrent director: " + p.directors.get(index));
                                System.out.println("New director: ");
                                p.directors.set(index, scanner.nextLine());
                                break;
                            }
                            case 4: {
                                loop = false;
                                break;
                            }
                        }
                    }
                    break;
                }
                case 3: {
                    boolean loop = true;
                    while (loop) {
                        System.out.println("Current actors:");
                        for (int i = 0; i < p.actors.size(); i++) {
                            System.out.println((i + 1) + ") " + p.actors.get(i));
                        }
                        System.out.println("Choose action:\n\t1) Add\n\t2) Delete\n\t3) Modify\n\t4) Cancel");
                        int action = readAction("Invalid action!", 4);
                        switch (action) {
                            case 1: {
                                System.out.println("Enter new actor:");
                                while (true) {
                                    boolean ok = true;
                                    String actor = scanner.nextLine();
                                    boolean found = false;
                                    for (Actor a : IMDB.getInstance().getActors()) {
                                        if (a.name.equals(actor)) {
                                            for (String ac : p.actors) {
                                                if (ac.equals(actor)) {
                                                    ok = false;
                                                    System.out.println("Actor already exists!");
                                                    break;
                                                }
                                            }
                                            found = true;
                                            break;
                                        }
                                    }
                                    if (!found) {
                                        System.out.println("Actor does not exist!");
                                    }
                                    if (ok) {
                                        p.actors.add(actor);
                                        break;
                                    }
                                }
                                break;
                            }
                            case 2: {
                                System.out.println("Enter index of actor to delete: (type 0 to cancel)");
                                int index = readIndex("Invalid actor!", p.actors.size());
                                if (index == -1) {
                                    break;
                                }
                                p.actors.remove(index);
                                break;
                            }
                            case 3: {
                                System.out.println("\nEnter index of actor to modify: (type 0 to cancel)");
                                int index = readIndex("Invalid actor!", p.actors.size());
                                if (index == -1) {
                                    break;
                                }
                                System.out.println("\nCurrent actor: " + p.actors.get(index));
                                System.out.println("New actor: ");
                                p.actors.set(index, scanner.nextLine());
                                break;
                            }
                            case 4: {
                                loop = false;
                                break;
                            }
                        }
                    }
                    break;
                }
                case 4: {
                    while (true) {
                        System.out.println("Current genres:");
                        for (int i = 0; i < p.genres.size(); i++) {
                            System.out.println((i + 1) + ") " + p.genres.get(i));
                        }

                        System.out.println("\nSelect option:\n\t1) Add\n\t2) Delete\n\t3) Exit");
                        int option = readAction("Invalid action!", 3);
                        if (option == 3) {
                            break;
                        }
                        if (option == 1) {
                            System.out.println("Enter new genre:");
                            String genre = scanner.nextLine();
                            try {
                                Genre new_genre = Genre.valueOf(genre);
                                if (!p.genres.contains(new_genre)) {
                                    p.genres.add(new_genre);
                                }
                            } catch (IllegalArgumentException e) {
                                System.out.println("Invalid genre!");
                            }
                        } else {
                            System.out.println("\nSelect index of genre to delete: (type 0 to cancel)");
                            int index = readIndex("Invalid genre!", p.genres.size());
                            if (index == -1) {
                                break;
                            }
                            p.genres.remove(index);
                        }
                    }
                    break;
                }
                case 5: {
                    System.out.println("Current plot:");
                    System.out.println(p.plot);
                    System.out.println("Enter new plot: (type 0 to cancel)");
                    String new_plot = scanner.nextLine();
                    if (new_plot.equals("0")) {
                        break;
                    }
                    p.plot = new_plot;
                    break;
                }
                case 8: {
                    return;
                }
                default: {
                    fork = true;
                    break;
                }
            }
            if (fork) {
                if (p instanceof Movie) {
                    switch (idx) {
                        case 6: {
                            System.out.println("Current duration: " + ((Movie) p).duration);
                            System.out.println("Enter new duration: (type 0 to cancel)");
                            int new_duration = readUIntLoop("Invalid duration!", true);
                            if (new_duration == 0) {
                                break;
                            }
                            ((Movie) p).duration = new_duration;
                            break;
                        }
                        case 7: {
                            System.out.println("Current release year: " + ((Movie) p).releaseYear);
                            System.out.println("Enter new release year: (type 0 to cancel)");
                            int year = readUIntLoop("Invalid year!", true);
                            if (year == 0) {
                                break;
                            }
                            ((Movie) p).releaseYear = year;
                            break;
                        }
                    }
                } else {
                    switch (idx) {
                        case 6: {
                            System.out.println("Current release year: " + ((Series) p).releaseYear);
                            System.out.println("Enter new release year: (type 0 to cancel)");
                            int year = readUIntLoop("Invalid year!", true);
                            if (year == 0) {
                                break;
                            }
                            ((Series) p).releaseYear = year;
                            break;
                        }
                        case 7: {
                            boolean loop = true;
                            while (loop) {
                                int i = 1;
                                ArrayList<String> keys = new ArrayList<>();
                                System.out.println("Current seasons:");
                                for (var mapEntry : ((Series) p).getSeasons().entrySet()){
                                    System.out.println(i + ") " + mapEntry.getKey() + ": " + mapEntry.getValue());
                                    keys.add(i - 1, mapEntry.getKey());
                                    i++;
                                }
                                if (((Series) p).getSeasons().isEmpty()) {
                                    System.out.println("--no seasons--");
                                }
                                System.out.println("Select action:\n\t1) Add\n\t2) Delete\n\t3) Modify\n\t4) Cancel");
                                int action = readAction("Invalid action!", 4);
                                switch (action) {
                                    case 1: {
                                        System.out.println("New season title:");
                                        String name = scanner.nextLine();
                                        System.out.println("Number of episodes:");
                                        List<Episode> episodes = new ArrayList<>();
                                        int nr = readUIntLoop("Invalid number!", false);
                                        for (int j = 1; j <= nr; j++) {
                                            System.out.println("Episode " + j + ":");
                                            System.out.print("\tname: ");
                                            String ep_name = scanner.nextLine();
                                            System.out.print("\tduration: ");
                                            int duration = readUIntLoop("Invalid duration!", false);
                                            Episode episode = new Episode(ep_name, duration);
                                            episodes.add(episode);
                                        }
                                        ((Series) p).getSeasons().put(name, episodes);
                                        break;
                                    }
                                    case 2: {
                                        System.out.println("Enter index of season to delete: (type 0 to cancel)");
                                        int index = readIndex("Invalid season!", ((Series) p).getSeasons().size());
                                        if (index == -1) {
                                            break;
                                        }
                                        ((Series) p).getSeasons().remove(keys.get(index));
                                        break;
                                    }
                                    case 3: {
                                        System.out.println("Select action:\n\t1) Modify season\n\t2) Modify order\n\t3) Cancel");
                                        int option = readAction("Invalid action!", 3);
                                        switch (option) {
                                            case 1: {
                                                System.out.println("Select index of season to modify: (type 0 to cancel)");
                                                int index = readIndex("Invalid season!", ((Series) p).getSeasons().size());
                                                if (index == -1) {
                                                    break;
                                                }

                                                System.out.println(keys.get(index) + ": ");
                                                int j = 1;
                                                for (Episode episode : ((Series) p).getSeasons().get(keys.get(index))) {
                                                    System.out.println(j + ") " + episode);
                                                }

                                                System.out.println("\nChoose action:\n\t1) Modify season title\n\t2) Modify episode\n\t3) Cancel");
                                                int action2 = readAction("Invalid action!", 3);
                                                switch (action2) {
                                                    case 1: {
                                                        System.out.println("Current title: " + keys.get(index));
                                                        System.out.println("New title: ");
                                                        ((Series) p).getSeasons().put(scanner.nextLine(), ((Series) p).getSeasons().remove(keys.get(index)));
                                                        break;
                                                    }
                                                    case 2: {
                                                        System.out.println("Select index of episode to modify: (type 0 to cancel)");
                                                        int id = readIndex("Invalid episode!", ((Series) p).getSeasons().get(keys.get(index)).size());
                                                        if (id == -1) {
                                                            break;
                                                        }
                                                        System.out.println("Current episode:");
                                                        System.out.println("name: " + ((Series) p).getSeasons().get(keys.get(index)).get(id).name);
                                                        System.out.println("duration: " + ((Series) p).getSeasons().get(keys.get(index)).get(id).duration);
                                                        System.out.println("\nModified episode:");
                                                        System.out.print("name: ");
                                                        ((Series) p).getSeasons().get(keys.get(index)).get(id).name = scanner.nextLine();
                                                        System.out.print("duration: ");
                                                        ((Series) p).getSeasons().get(keys.get(index)).get(id).duration = readUIntLoop("Invalid duration!", false);
                                                        break;
                                                    }
                                                }
                                                break;
                                            }
                                            case 2: {
                                                System.out.println("Enter indexes of seasons to switch places: (type 0 to cancel)");
                                                int index1 = readIndex("Invalid season!", ((Series) p).getSeasons().size());
                                                if (index1 == -1) {
                                                    break;
                                                }
                                                int index2 = readIndex("Invalid season!", ((Series) p).getSeasons().size());
                                                if (index2 == -1) {
                                                    break;
                                                }
                                                if (index1 != index2) {
                                                    swapSeasons(((Series) p).getSeasons(), keys.get(index1), keys.get(index2));
                                                }
                                                break;
                                            }
                                            case 3: {
                                                break;
                                            }
                                        }
                                        break;
                                    }
                                    case 4: {
                                        loop = false;
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void updateActor(Actor a) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            a.displayInfo();
            System.out.print("""
                    1) Modify name
                    2) Modify performances
                    3) Modify biography
                    4) Exit
                    """);
            System.out.println("Select index of action:");
            int idx = readAction("Invalid action!", 4);
            switch (idx) {
                case 1: {
                    System.out.println("Current name: " + a.name);
                    System.out.println("New name: ");
                    while (true) {
                        String name = scanner.nextLine();
                        boolean ok = true;
                        for (Actor actor : IMDB.getInstance().getActors()) {
                            if (actor.name.equals(name)) {
                                ok = false;
                                break;
                            }
                        }
                        if (ok) {
                            a.name = name;
                            break;
                        } else {
                            System.out.println("Name taken!");
                        }
                    }
                    break;
                }
                case 2: {
                    for (int i = 0; i < a.performances.size(); i++) {
                        System.out.println((i + 1) + ") <" + a.performances.get(i)[1] + "> : " + a.performances.get(i)[0]);
                    }
                    System.out.println("Select index of performance to modify: (type 0 to cancel)");
                    int index = readIndex("Invalid performance!", a.performances.size());
                    if (index == -1) {
                        break;
                    }
                    System.out.println("\nCurrent name:" + a.performances.get(index)[0]);
                    System.out.println("Current type:" + a.performances.get(index)[1]);
                    System.out.println("\nNew name:");
                    a.performances.get(index)[0] = scanner.nextLine();
                    System.out.println("New type:");
                    a.performances.get(index)[1] = scanner.nextLine();
                    break;
                }
                case 3: {
                    System.out.println("Current biography: " + a.biography);
                    System.out.println("New biography: ");
                    a.biography = scanner.nextLine();
                    break;
                }
                case 4: {
                    return;
                }
            }
        }
    }

    public void solveRequest(Request r) {
        switch (r.getRequestType()) {
            case ACTOR_ISSUE:
                if (r.actorName != null) {
                    r.notifyRequest(true);
                }
            case MOVIE_ISSUE:
                if (r.productionTitle != null) {
                    r.notifyRequest(true);
                }
            case DELETE_ACCOUNT:
                if (this instanceof Admin) {
                    r.notifyRequest(true);
                }
            case OTHERS:
                if (this instanceof Admin) {
                    r.notifyRequest(true);
                }
        }
    }

    private int readUIntLoop(String err, boolean canBeZero) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                int nr = scanner.nextInt();
                scanner.nextLine();
                if (nr < 0 || (nr == 0 && !canBeZero)) {
                    System.out.println(err);
                    continue;
                }
                return nr;
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println(err);
            }
        }
    }
    private int readIndex(String err, int size) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                int index = scanner.nextInt();
                scanner.nextLine();
                index--;
                if (index >= -1 && index < size) {
                    return index;
                }
                System.out.println(err);
            } catch (InputMismatchException e) {
                System.out.println(err);
                scanner.nextLine();
            }
        }
    }
    private int readAction(String err, int max) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                int action = scanner.nextInt();
                scanner.nextLine();
                if (action >= 1 && action <= max) {
                    return action;
                }
                System.out.println(err);
            } catch (InputMismatchException e) {
                System.out.println(err);
                scanner.nextLine();
            }
        }
    }
    private void swapSeasons(Map<String, List<Episode>> map, String key1, String key2) {
        List<String> keys = new ArrayList<>(map.keySet());
        List<List<Episode>> values = new ArrayList<>(map.values());

        int index1 = keys.indexOf(key1);
        int index2 = keys.indexOf(key2);

        if (index1 != -1 && index2 != -1) {
            Collections.swap(keys, index1, index2);
            Collections.swap(values, index1, index2);

            map.clear();

            for (int i = 0; i < keys.size(); i++) {
                map.put(keys.get(i), values.get(i));
            }
        }
    }
}
