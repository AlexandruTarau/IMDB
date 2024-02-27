package org.example.GUI;

import org.example.IMDB;
import org.example.Production;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SearchBar extends JPanel {
    private JTextField searchField;

    public SearchBar() {
        setLayout(new BorderLayout());
        searchField = new JTextField();
        JButton searchButton = new JButton("Search");

        add(searchField, BorderLayout.CENTER);
        add(searchButton, BorderLayout.EAST);

        setPreferredSize(new Dimension(100, 40));
        setBorder(BorderFactory.createEmptyBorder(10, 100, 10, 100));

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchText = searchField.getText();
                IMDB.getInstance().Search(searchText);
            }
        });
    }

    public JTextField getSearchField() {
        return searchField;
    }
}
