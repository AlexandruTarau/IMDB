package org.example;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Request implements Subject {
    public RequestTypes getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestTypes requestType) {
        this.requestType = requestType;
    }

    private RequestTypes requestType;
    private LocalDateTime creationDate;
    public String productionTitle;
    public String actorName;
    public String problemDescription;
    public String requesterUsername;
    public String solverUsername;
    ArrayList<Observer> obs = new ArrayList<>();

    public Request(String type, LocalDateTime creationDate, String productionTitle, String actorName,
                   String problemDescription, String requesterUsername, String solverUsername) {
        this.requestType = RequestTypes.valueOf(type);
        setCreation_date(creationDate);
        this.productionTitle = productionTitle;
        this.actorName = actorName;
        this.problemDescription = problemDescription;
        this.requesterUsername = requesterUsername;
        this.solverUsername = solverUsername;
    }

    public Request() {

    }
    public void addObserver(Observer o) {
        if (!obs.contains(o)) {
            obs.add(o);
        }
    }
    public void removeObserver(Observer o) {
        obs.remove(o);
    }
    public void notifyObservers(String notification) {
        for (int i = obs.size() - 1; i >= 0; i--) {
            (obs.get(i)).update(notification);
        }
    }
    public void setCreation_date(LocalDateTime creation_date) {
        this.creationDate = creation_date;
    }

    public LocalDateTime getCreation_date() {
        return creationDate;
    }

    public void notifyRequest(boolean accepted) {
        if (accepted) {
            notifyObservers("Your request \"" + this.problemDescription + "\" was accepted!");
        } else {
            notifyObservers("Your request \"" + this.problemDescription + "\" was rejected!");
        }
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestType=" + requestType +
                ", creationDate=" + creationDate +
                (productionTitle != null ?
                ", productionTitle='" + productionTitle + '\'' : "") +
                (actorName != null ?
                ", actorName='" + actorName + '\'' : "")+
                ", problemDescription='" + problemDescription + '\'' +
                ", requesterUsername='" + requesterUsername + '\'' +
                ", solverUsername='" + solverUsername + '\'' +
                '}';
    }
}
