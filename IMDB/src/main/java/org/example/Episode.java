package org.example;

public class Episode {
    public String name;
    public int duration;

    public Episode(String name, int duration) {
        this.name = name;
        this.duration = duration;
    }

    public Episode() {

    }

    @Override
    public String toString() {
        return "<" + name + " : " + duration + " min>";
    }
}
