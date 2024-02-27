package org.example;

import java.util.ArrayList;
import java.util.List;

public class RequestsHolder {
    public static List<Request> requests = new ArrayList<>();

    public static boolean add(Object o) {
        if (o instanceof Request) {
            Request request = (Request) o;
            requests.add(request);
            return true;
        }
        return false;
    }

    public static boolean remove(Object o) {
        if (o instanceof Request) {
            Request request = (Request) o;
            requests.remove(request);
            return true;
        }
        return false;
    }
}
