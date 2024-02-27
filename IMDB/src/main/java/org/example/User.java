package org.example;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

public abstract class User<T extends Comparable<T>> implements Observer {
    public Information info;
    public AccountType type;
    public String username;
    public int experience;
    public List<String> notifications;

    public SortedSet<T> favorites;

    public User (Information info, AccountType type, String username, int experience,
                 List<String> notifications, SortedSet<T> favorites) {
        this.info = info;
        this.type = type;
        this.username = username;
        this.experience = experience;
        this.notifications = notifications;
        this.favorites = favorites;
    }

    public User() {

    }

    public boolean add(T o) {
        favorites.add(o);
        return true;
    }

    public boolean remove(T o) {
        favorites.remove(o);
        return true;
    }

    public void updateExperience(int experience) {
        this.experience += experience;
    }

    public void logOut() {
        IMDB.getInstance().setLoggedInUser(null);
    }
    public void displayInfo() {
        System.out.println("Username: " + username);
        if (experience == -1) {
            System.out.println("User experience: -");
        } else {
            System.out.println("User experience: " + experience);
        }
    }

    @Override
    public void update(String notification) {

    }

    public static class Information {
        private Credentials credentials;
        public String name;
        public String country;
        public int age;
        public char gender;
        private LocalDateTime birth_date;

        private Information(InformationBuilder builder) {
            this.credentials = builder.credentials;
            this.age = builder.age;
            this.name = builder.name;
            this.country = builder.country;
            this.gender = builder.gender;
            this.birth_date = builder.birth_date;
        }

        public LocalDateTime getBirth_date() {
            return birth_date;
        }

        public void setBirth_date(LocalDateTime birth_date) {
            this.birth_date = birth_date;
        }

        public Credentials getCredentials() {
            return credentials;
        }

        public void setCredentials(Credentials credentials) {
            this.credentials = credentials;
        }

        public static class InformationBuilder {
            private Credentials credentials;
            String name;
            String country;
            int age;
            char gender;
            private LocalDateTime birth_date;

            public InformationBuilder() {

            }

            public InformationBuilder credentials(Credentials credentials) {
                this.credentials = credentials;
                return this;
            }
            public InformationBuilder name(String name) {
                this.name = name;
                return this;
            }
            public InformationBuilder country(String country) {
                this.country = country;
                return this;
            }
            public InformationBuilder age(int age) {
                this.age = age;
                return this;
            }
            public InformationBuilder gender(char gender) {
                this.gender = gender;
                return this;
            }
            public InformationBuilder birth_date(LocalDateTime birth_date) {
                this.birth_date = birth_date;
                return this;
            }
            public Information build() {
                return new Information(this);
            }
        }
    }
}
