package org.example.CommandStrategies;

import org.example.*;
import org.example.ExperienceStrategies.RewardContribution;

import java.util.*;

public class ManageContributions extends AuxMethods implements CommandStrategy {
    IMDB imdb = IMDB.getInstance();
    @Override
    public void execute(User user) {
        Scanner scanner = new Scanner(System.in);
        boolean loop = true;
        while (loop) {
            System.out.println("Your contributions:");
            boolean hasContributions = false;
            for (Object contribution : ((Staff<?>) user).contributions) {
                if (contribution instanceof Production) {
                    System.out.println("<Production> " + ((Production) contribution).title);
                } else {
                    System.out.println("<Actor> " + ((Actor) contribution).name);
                }
                hasContributions = true;
            }
            if (user instanceof Admin) {
                System.out.println("\nShared contributions:");
                for (Object contribution : Admin.common_contributions) {
                    if (contribution instanceof Production) {
                        System.out.println("<Production> " + ((Production) contribution).title);
                    } else {
                        System.out.println("<Actor> " + ((Actor) contribution).name);
                    }
                    hasContributions = true;
                }
            }
            if (!hasContributions) {
                System.out.println("--no contributions--");
            }
            System.out.println("\nChoose action:\n\t1) Add\n\t2) Delete\n\t3) Back");

            int choice = readAction("Invalid action!", 3);
            if (choice == 1) {
                System.out.println("""
                Choose contribution type: (type 0 to cancel)
                    1) Movie
                    2) Series
                    3) Actor
                """);
                int type = readIndex("Invalid type!", 3) + 1;
                if (type == 0) {
                    break;
                }
                user.updateExperience(new RewardContribution().calculateExperience());
                if (type == 1 || type == 2) {
                    System.out.println("Enter production title:");
                    String title = scanner.nextLine();
                    List<String> directors = new ArrayList<>();
                    List<String> actors = new ArrayList<>();
                    List<Genre> genres = new ArrayList<>();
                    String plot;
                    Double averageRating = 0.0;

                    boolean alreadyExists = false;
                    for (Production production : imdb.getProductions()) {
                        if (production.title.equals(title)) {
                            alreadyExists = true;
                            break;
                        }
                    }
                    if (alreadyExists) {
                        System.out.println("Production already exists!");
                        continue;
                    }

                    System.out.println("Enter directors: (type 0 on a new line when finished)");
                    while (true) {
                        String name = scanner.nextLine();
                        if (name.equals("0")) {
                            break;
                        }
                        boolean ok = true;
                        for (String d : directors) {
                            if (d.equals(name)) {
                                ok = false;
                                break;
                            }
                        }
                        if (ok) {
                            directors.add(name);
                        }
                    }
                    System.out.println();

                    System.out.println("Enter actors: (type 0 on a new line when finished)");
                    while (true) {
                        String name = scanner.nextLine();
                        boolean actorExists = false;
                        if (name.equals("0")) {
                            break;
                        }
                        for (Actor actor : imdb.getActors()) {
                            if (actor.name.equals(name)) {
                                actorExists = true;
                                boolean ok = true;
                                for (String a : actors) {
                                    if (a.equals(actor.name)) {
                                        ok = false;
                                        break;
                                    }
                                }
                                if (ok) {
                                    actors.add(name);
                                }
                                break;
                            }
                        }
                        if (!actorExists) {
                            System.out.println("Actor " + name + " does not exist!");
                        }
                    }
                    System.out.println();

                    System.out.println("Enter production genres: (type 0 on a new line when finished)");
                    while (true) {
                        String name = scanner.nextLine();
                        boolean genreExists = false;
                        if (name.equals("0")) {
                            break;
                        }
                        for (Genre genre : Genre.values()) {
                            if (genre.toString().equals(name)) {
                                genreExists = true;
                                if (!genres.contains(genre)) {
                                    genres.add(genre);
                                }
                                break;
                            }
                        }
                        if (!genreExists) {
                            System.out.println("Genre " + name + " does not exist!");
                        }
                    }
                    System.out.println();

                    System.out.println("Enter production plot:");
                    plot = scanner.nextLine();
                    System.out.println();

                    if (type == 1) {
                        System.out.println("Enter movie duration in minutes:");
                        int duration = readUIntLoop("Invalid duration!", false);

                        System.out.println("Enter movie release year:");
                        int release_year = readUIntLoop("Invalid year!", false);

                        Movie movie = new Movie(title, directors, actors, genres, new ArrayList<>(), plot, averageRating, duration, release_year, "IMDB\\src\\main\\java\\org\\example\\GUI\\images\\unknownImage.png");
                        ((Staff<?>) user).addProductionSystem(movie);
                    } else {
                        System.out.println("Enter series release year:");
                        int release_year = readUIntLoop("Invalid year!", false);

                        System.out.println("Enter series number of seasons:");
                        int numSeasons = readUIntLoop("Invalid number!", false);

                        Map<String, List<Episode>> seasons = new LinkedHashMap<>(numSeasons);
                        for (int i = 0; i < numSeasons; i++) {
                            System.out.println("Enter name of season " + (i + 1) + ":");
                            String season_name = scanner.nextLine();

                            System.out.println("Enter number of episodes for this season:");
                            int numEpisodes = readUIntLoop("Invalid number!", false);

                            System.out.println("\nEnter episodes:");
                            List<Episode> episodes = new ArrayList<>(numEpisodes);
                            for (int j = 0; j < numEpisodes; j++) {
                                String ep_name;

                                System.out.println("Episode " + (j + 1) + ":");
                                System.out.println("\tname: ");
                                ep_name = scanner.nextLine();
                                int ep_duration = readUIntLoop("Invalid duration!", false);

                                Episode episode = new Episode(ep_name, ep_duration);
                                episodes.add(episode);
                            }

                            seasons.put(season_name, episodes);
                        }

                        Series series = new Series(title, directors, actors, genres, new ArrayList<>(), plot, averageRating, release_year, numSeasons, seasons, "IMDB\\src\\main\\java\\org\\example\\GUI\\images\\unknownImage.png");
                        ((Staff<?>) user).addProductionSystem(series);
                    }
                } else if (type == 3) {
                    System.out.println("Enter actor name:");
                    String name = scanner.nextLine();

                    boolean alreadyExists = false;
                    for (Actor actor : imdb.getActors()) {
                        if (actor.name.equals(name)) {
                            alreadyExists = true;
                            break;
                        }
                    }
                    if (alreadyExists) {
                        System.out.println("Actor already exists!");
                        continue;
                    }

                    System.out.println("\nEnter performances:");
                    ArrayList<String[]> performances = new ArrayList<>();

                    while (true) {
                        System.out.println("Select type:\n\t1) Movie\n\t2) Series\n\t3) Done");
                        String performance_name;
                        int option = readAction("Invalid type!", 3);
                        if (option == 1 || option == 2) {
                            System.out.println("Production title: ");
                            performance_name = scanner.nextLine();
                            boolean foundProduction = false;
                            for (Production production : imdb.getProductions()) {
                                if (production.title.equals(performance_name)) {
                                    String[] performance = new String[2];
                                    performance[0] = performance_name;
                                    performance[1] = (option == 1 ? "Movie" : "Series");
                                    performances.add(performance);
                                    foundProduction = true;
                                    break;
                                }
                            }
                            if (!foundProduction) {
                                System.out.println("Production does not exist!");
                            }
                        } else if (option == 3) {
                            break;
                        }
                    }
                    System.out.println("Enter actor biography:");
                    String biography = scanner.nextLine();

                    Actor actor = new Actor(name, performances, biography, "IMDB\\src\\main\\java\\org\\example\\GUI\\images\\unknownImage.png");
                    ((Staff<?>) user).addActorSystem(actor);
                }
            } else if (choice == 2) {
                int i = 0;
                ArrayList<String> curr_contributions = new ArrayList<>();
                for (Object contribution : ((Staff<?>) user).contributions) {
                    if (contribution instanceof Production) {
                        System.out.println(++i + ") <Production> " + ((Production) contribution).title);
                        curr_contributions.add(((Production) contribution).title);
                    } else {
                        System.out.println(++i + ") <Actor> " + ((Actor) contribution).name);
                        curr_contributions.add(((Actor) contribution).name);
                    }
                }
                if (user instanceof Admin) {
                    for (Object contribution : Admin.common_contributions) {
                        if (contribution instanceof Production) {
                            System.out.println(++i + ") <Production> " + ((Production) contribution).title);
                            curr_contributions.add(((Production) contribution).title);
                        } else {
                            System.out.println(++i + ") <Actor> " + ((Actor) contribution).name);
                            curr_contributions.add(((Actor) contribution).name);
                        }
                    }
                }
                System.out.println("\nEnter contribution index to delete: (type 0 to cancel)");
                int idx = readIndex("Invalid contribution!", curr_contributions.size());
                if (idx == -1) {
                    break;
                }

                // Remove contribution from system
                if (idx < ((Staff<?>) user).contributions.size()) {
                    ((Staff<?>) user).removeProductionSystem(curr_contributions.get(idx));
                    ((Staff<?>) user).removeActorSystem(curr_contributions.get(idx));
                } else {
                    Admin.common_contributions.remove(curr_contributions.get(idx));
                }

            } else if (choice == 3) {
                loop = false;
            }
        }
    }
}
