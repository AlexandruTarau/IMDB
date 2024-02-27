package org.example.CommandStrategies;

import org.example.*;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManageUsers extends AuxMethods implements CommandStrategy {
    @Override
    public void execute(User user) {
        Scanner scanner = new Scanner(System.in);
        IMDB imdb = IMDB.getInstance();
        boolean loop = true;
        while (loop) {
            System.out.println("Select action:\n\t1) Add\n\t2) Delete\n\t3) Exit");
            int action = readAction("Invalid action!", 3);
            switch (action) {
                case 1: {
                    User.Information.InformationBuilder builder = new User.Information.InformationBuilder();
                    System.out.print("User type: ");
                    String type = readType();
                    System.out.print("Name: ");
                    builder.name(scanner.nextLine());
                    System.out.print("Age: ");
                    builder.age(readUIntLoop("Invalid age!", false));
                    System.out.print("Country: ");
                    builder.country(scanner.nextLine());
                    System.out.print("Gender (F/M/N): ");
                    builder.gender(readGender());
                    System.out.print("Birth Date (YYYY-MM-DD): ");
                    builder.birth_date(readDate());
                    System.out.print("E-mail: ");
                    Random random = new Random();
                    String password = generatePassword(random.nextInt(11) + 10);
                    Credentials credentials = new Credentials(readEmail(), password);
                    System.out.println("Your generated password is: " + password);
                    builder.credentials(credentials);
                    User.Information information = builder.build();

                    UserFactory<?> userFactory = new UserFactory<>();
                    User new_user = userFactory.createUser(AccountType.valueOf(type), information, generateUsername(information.name), 0,
                            new ArrayList<>(), new TreeSet<>(), new ArrayList<>(), new TreeSet<>());
                    ((Admin) user).addUser(new_user);
                    break;
                }
                case 2: {
                    System.out.println("Users:");
                    for (int i = 0; i < imdb.getUsers().size(); i++) {
                        System.out.println((i + 1) + ") " + imdb.getUsers().get(i).username);
                    }
                    System.out.println("Select index of user to delete: (type 0 to cancel)");
                    int index = readIndex("Invalid user!", imdb.getUsers().size());
                    if (index == -1) {
                        break;
                    }
                    User userToDelete = imdb.getUsers().get(index);

                    // Delete requests
                    Iterator<Request> iterator = imdb.getRequests().iterator();
                    while (iterator.hasNext()) {
                        Request request = iterator.next();
                        if (request.requesterUsername.equals(userToDelete.username)) {
                            // Delete requests from staff
                            for (User u : imdb.getUsers()) {
                                if (u instanceof Staff<?>) {
                                    ((Staff<?>) u).requests.remove(request);
                                }
                            }
                            // Delete requests from main db
                            iterator.remove();
                        }
                    }

                    // Delete ratings
                    for (Production production : imdb.getProductions()) {
                        for (Rating rating : production.ratings) {
                            if (rating.username.equals(userToDelete.username)) {
                                production.ratings.remove(rating);
                                break;
                            }
                        }
                    }

                    // Delete user
                    ((Admin) user).removeUser(userToDelete);
                    break;
                }
                case 3: {
                    loop = false;
                    break;
                }
            }
        }
    }

    private char readGender() {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String gender = scanner.nextLine();
            if (gender.equals("M") || gender.equals("F") || gender.equals("N")) {
                return gender.charAt(0);
            }
            System.out.println("Invalid gender!");
        }
    }
    private LocalDateTime readDate() {
        while (true) {
            try {
                Scanner scanner = new Scanner(System.in);
                String date = scanner.nextLine();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                return LocalDateTime.of(LocalDate.parse(date, formatter), LocalTime.NOON);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid input format. Please enter the date and time in the correct format.");
            }
        }
    }
    private String readEmail() {
        Scanner scanner = new Scanner(System.in);
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        while (true) {
            String email = scanner.nextLine();
            Matcher matcher = pattern.matcher(email);
            if (matcher.matches()) {
                return email;
            }
            System.out.println("Invalid email!");
        }
    }
    private String generatePassword(int length) {
        String upperCaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseChars = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialChars = "!@#$%^&*()-_=+[]{}|;:,.<>?";

        String allChars = upperCaseChars + lowerCaseChars + numbers + specialChars;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(allChars.length());
            password.append(allChars.charAt(randomIndex));
        }

        return password.toString();
    }
    private String readType() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String type = scanner.nextLine();
            if (type.equals("Regular") || type.equals("Contributor") || type.equals("Admin")) {
                return type;
            }
            System.out.println("Invalid type!");
        }
    }
    private String generateUsername(String name) {
        String[] nameParts = name.toLowerCase().split(" ");
        String username = String.join("_", nameParts);

        Random random = new Random();
        while (true) {
            int randomNumber = random.nextInt(9900) + 100;
            username += "_" + randomNumber;
            boolean ok = true;
            for (User user : IMDB.getInstance().getUsers()) {
                if (user.username.equals(username)) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                break;
            }
        }

        return username;
    }
}
