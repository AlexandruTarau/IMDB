package org.example.GUI.Menu;

import org.example.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class RequestsPanel extends JPanel {
    private JList<Request> requestsList;
    private DefaultListModel<Request> listModel;
    private List<Request> requests;

    public RequestsPanel(CardLayout cardLayout, JPanel cardPanel) {
        User user = IMDB.getInstance().loggedInUser;
        setLayout(new GridBagLayout());
        requests = IMDB.getInstance().getRequests();

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
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            String[] options = {"MOVIE_ISSUE", "ACTOR_ISSUE", "DELETE_ACCOUNT", "OTHERS"};
            String selectedOption = (String) JOptionPane.showInputDialog(
                    null,
                    "Choose an option:",
                    "Add Request",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (selectedOption != null) {
                switch (selectedOption) {
                    case "MOVIE_ISSUE": {
                        cardLayout.show(cardPanel, "ProductionsPanel");
                        break;
                    }
                    case "ACTOR_ISSUE": {
                        cardLayout.show(cardPanel, "ActorsPanelMenu");
                        break;
                    }
                    case "DELETE_ACCOUNT": {
                        IMDB.getInstance().makeRequest("DELETE_ACCOUNT");
                        break;
                    }
                    case "OTHERS": {
                        IMDB.getInstance().makeRequest("OTHERS");
                        break;
                    }
                }
                updateRequestsList();
            }
        });

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            int selectedIndex = requestsList.getSelectedIndex();
            if (selectedIndex != -1) {
                int dialogResult = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to delete this request?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    Request selectedRequest = requestsList.getSelectedValue();

                    // Remove request from main db
                    ((RequestsManager) user).removeRequest(selectedRequest);

                    // Remove request from staff requests
                    if (selectedRequest.solverUsername.equals("ADMIN")) {
                        RequestsHolder.requests.remove(selectedRequest);
                    } else {
                        for (User<?> sender : IMDB.getInstance().getUsers()) {
                            if (sender instanceof Staff<?>) {
                                if (((Staff<?>) sender).requests.remove(selectedRequest)) {
                                    break;
                                }
                            }
                        }
                    }
                    updateRequestsList();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a request to delete!");
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
        listModel.clear();

        for (Request request : requests) {
            if (request.requesterUsername.equals(IMDB.getInstance().loggedInUser.username)) {
                listModel.addElement(request);
            }
        }
    }
}
