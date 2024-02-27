package org.example.CommandStrategies;

import org.example.*;
import org.example.ExperienceStrategies.RewardRequest;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class SolveRequest extends AuxMethods implements CommandStrategy {
    IMDB imdb = IMDB.getInstance();
    @Override
    public void execute(User user) {
        while (true) {
            System.out.println("Your requests:");
            boolean hasRequests = false;
            int i = 0;
            ArrayList<Request> curr_requests = new ArrayList<>();
            for (Request request : ((Staff<?>) user).requests) {
                curr_requests.add(request);
                System.out.println(++i + ") " + request);
                hasRequests = true;
            }
            if (user instanceof Admin) {
                for (Request request : RequestsHolder.requests) {
                    curr_requests.add(request);
                    System.out.println(++i + ") " + request);
                    hasRequests = true;
                }
            }
            if (!hasRequests) {
                System.out.println("--no requests--");
            }

            System.out.println("\nEnter request index to solve: (type 0 to exit)");
            int idx = readIndex("Invalid request!", curr_requests.size());
            if (idx == -1) {
                break;
            }
            System.out.println();
            System.out.println(curr_requests.get(idx));
            System.out.println("Choose action:\n\t1) Accept\n\t2) Reject\n\t3) Cancel");
            int option = readAction("Invalid action!", 3);
            if (option == 1) {
                ((Staff<?>) user).solveRequest(curr_requests.get(idx));

                // Update experience
                for (User u : imdb.getUsers()) {
                    if (u.username.equals(curr_requests.get(idx).requesterUsername)) {
                        u.updateExperience(new RewardRequest().calculateExperience());
                        break;
                    }
                }

                // Remove request from staff member
                if (idx < ((Staff<?>) user).requests.size()) {
                    ((Staff<?>) user).requests.remove(curr_requests.get(idx));
                } else {
                    RequestsHolder.requests.remove(curr_requests.get(idx));
                }

                // Remove request from main db
                RequestsManager requester = findRequester(curr_requests.get(idx).requesterUsername);
                if (requester != null) {
                    requester.removeRequest(curr_requests.get(idx));
                }
            } else if (option == 2) {
                curr_requests.get(idx).notifyRequest(false);

                // Remove request from staff member
                if (idx < ((Staff<?>) user).requests.size()) {
                    ((Staff<?>) user).requests.remove(curr_requests.get(idx));
                } else {
                    RequestsHolder.requests.remove(curr_requests.get(idx));
                }

                // Remove request from main db
                RequestsManager requester = findRequester(curr_requests.get(idx).requesterUsername);
                if (requester != null) {
                    requester.removeRequest(curr_requests.get(idx));
                }
            } else {
                break;
            }
        }
    }
    private RequestsManager findRequester(String username) {
        for (User user : imdb.getUsers()) {
            if (user.username.equals(username)) {
                return (RequestsManager) user;
            }
        }
        return null;
    }
}
