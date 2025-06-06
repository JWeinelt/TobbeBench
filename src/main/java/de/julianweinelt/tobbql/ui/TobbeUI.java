package de.julianweinelt.tobbql.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TobbeUI {
    private JFrame frame;
    private JPanel currentPanel;

    public void start() {
        FlatDarkLaf.setup();

        frame = new JFrame();
        frame.setBounds(50, 50, 1600, 900);
        frame.setName("TobbeBench");
        frame.setTitle("TobbeBench");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        createMenuBar();
        createStartPage();

        frame.setVisible(true);
    }

    private void createStartPage() {
        JPanel mainPanel = new JPanel();
        //mainPanel.setLayout(new FlowLayout());
        mainPanel.setPreferredSize(frame.getSize());

        JLabel title = new JLabel("TobbeBench");
        Font originalFont = title.getFont();
        Font resizedFont = originalFont.deriveFont(Font.BOLD, 24f);
        title.setFont(resizedFont);
        mainPanel.add(title);

        JPanel cards = new JPanel();
        GridLayout l = new GridLayout();
        l.setColumns(3);
        cards.setLayout(l);
        cards.setPreferredSize(new Dimension(frame.getSize().width - 200, frame.getSize().height - 400));
        JButton createProfile = new JButton("Create new profile");
        createProfile.addActionListener(e -> {
            JFrame popup = new JFrame("Create profile");
            popup.setSize(600, 900);
            popup.setLayout(new FlowLayout());

            popup.add(new JLabel("Name: "));
            popup.add(new JTextField());

            popup.setVisible(true);
        });
        mainPanel.add(createProfile);

        mainPanel.add(cards);
        currentPanel = mainPanel;
        frame.add(currentPanel);
    }


    private void createMenuBar() {
        JMenuBar bar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        bar.add(fileMenu);
        frame.setJMenuBar(bar);
    }
}