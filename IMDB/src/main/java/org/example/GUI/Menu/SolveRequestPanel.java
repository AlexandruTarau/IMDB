package org.example.GUI.Menu;

import org.example.*;
import org.example.ExperienceStrategies.RewardRequest;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class SolveRequestPanel extends JPanel {
    private JList<Request> requestsList;
    private DefaultListModel<Request> listModel;
    private List<Request> requests = new ArrayList<>();

    public SolveRequestPanel(CardLayout cardLayout, JPanel cardPanel) {
        User user = IMDB.getInstance().loggedInUser;
        setLayout(new GridBagLayout());

        listModel = new DefaultListModel<>();
        updateRequestsList();

        // Creating a border for the panel
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(Color.BLACK)
        ));

        JPanel centeredPanel = new JPanel(new BorderLayout());
        centeredPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        requestsList = new JList<>(listModel);
        requestsList.setFixedCellHeight(50);
        requestsList.setToolTipText("");

        // Display request information + hover to get full information
        requestsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel renderer = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Request request = (Request) value;

                String text = "<" + request.getRequestType() + "> " + (request.actorName == null ? (request.productionTitle == null ? "" : request.productionTitle) : request.actorName)
                        + ": " + request.problemDescription;
                renderer.setText(text);
                renderer.setToolTipText(text);

                return renderer;
            }
        });

        // Buttons
        JButton addButton = new JButton("Accept");
        addButton.addActionListener(e -> {
            int selectedIndex = requestsList.getSelectedIndex();
            if (selectedIndex != -1) {
                Request selectedRequest = requestsList.getSelectedValue();
                ((Staff<?>) user).solveRequest(selectedRequest);

                // Update experience
                for (User u : IMDB.getInstance().getUsers()) {
                    if (u.username.equals(selectedRequest.requesterUsername)) {
                        u.updateExperience(new RewardRequest().calculateExperience());
                        break;
                    }
                }

                // Remove request from staff member
                if (selectedIndex < ((Staff<?>) user).requests.size()) {
                    ((Staff<?>) user).requests.remove(selectedRequest);
                } else {
                    RequestsHolder.requests.remove(selectedRequest);
                }

                // Remove request from main db
                RequestsManager requester = findRequester(selectedRequest.requesterUsername);
                if (requester != null) {
                    requester.removeRequest(selectedRequest);
                }
                updateRequestsList();
                JOptionPane.showMessageDialog(null, "Request accepted!");
            }
        });

        JButton deleteButton = new JButton("Reject");
        deleteButton.addActionListener(e -> {
            int selectedIndex = requestsList.getSelectedIndex();
            if (selectedIndex != -1) {
                Request selectedRequest = requestsList.getSelectedValue();
                ((Staff<?>) user).solveRequest(selectedRequest);

                selectedRequest.notifyRequest(false);

                // Remove request from staff member
                if (selectedIndex < ((Staff<?>) user).requests.size()) {
                    ((Staff<?>) user).requests.remove(selectedRequest);
                } else {
                    RequestsHolder.requests.remove(selectedRequest);
                }

                // Remove request from main db
                RequestsManager requester = findRequester(selectedRequest.requesterUsername);
                if (requester != null) {
                    requester.removeRequest(selectedRequest);
                }
                updateRequestsList();
                JOptionPane.showMessageDialog(null, "Request rejected!");
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        centeredPanel.add(buttonPanel, BorderLayout.NORTH);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "MainMenu");
            }
        });

        JScrollPane scrollPane = new JScrollPane(requestsList);
        scrollPane.setPreferredSize(new Dimension(250, 300));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        centeredPanel.add(scrollPane, BorderLayout.CENTER);
        centeredPanel.add(backButton, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        add(centeredPanel, gbc);
    }

    public void updateRequestsList() {
        User user = IMDB.getInstance().loggedInUser;
        listModel.clear();
        requests.clear();

        requests.addAll(((Staff<?>) user).requests);
        if (user instanceof Admin) {
            requests.addAll(RequestsHolder.requests);
        }

        for (Request request : requests) {
            if (request.solverUsername.equals(IMDB.getInstance().loggedInUser.username)) {
                listModel.addElement(request);
            }
        }
        if (user instanceof Admin) {
            listModel.addAll(RequestsHolder.requests);
        }
    }
    private RequestsManager findRequester(String username) {
        for (User user : IMDB.getInstance().getUsers()) {
            if (user.username.equals(username)) {
                return (RequestsManager) user;
            }
        }
        return null;
    }
}
