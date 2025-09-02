package de.julianweinelt.tobbql.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import de.julianweinelt.tobbql.data.ConfigManager;
import de.julianweinelt.tobbql.data.Configuration;
import de.julianweinelt.tobbql.data.Project;
import de.julianweinelt.tobbql.parser.Connection;
import lombok.Getter;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

@Getter
public class TobbeUI {
    private final HashMap<Project, Connection> connections = new HashMap<>();

    private JFrame frame;
    private JPanel currentPanel;
    private JTabbedPane tabbedPane;

    public void start() {
        FlatDarkLaf.setup();
        tabbedPane = new JTabbedPane();

        frame = new JFrame();
        frame.setBounds(50, 50, 1600, 900);
        frame.setName("TobbeBench");
        frame.setTitle("TobbeBench");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        createMenuBar();
        createStartPage();
        frame.add(tabbedPane);

        frame.setVisible(true);
    }

    public void connect(Project project) {
        Connection connection = new Connection(project);
        connections.put(project, connection);
        if (connection.testConnection()) {
            JOptionPane.showMessageDialog(frame, "Connection successful", "Connection", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "No connection could be established.", "Failure", JOptionPane.ERROR_MESSAGE);
            connections.remove(project);
        }
        createNewConnectionTab(connection);
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
        l.setColumns(5);
        l.setRows(2);
        cards.setLayout(l);
        cards.setPreferredSize(new Dimension(frame.getSize().width - 200, frame.getSize().height - 400));

        for (Project p : Configuration.getConfiguration().getProjects()) cards.add(p.createCard(this));

        JButton createProfile = new JButton("Add profile");
        createProfile.addActionListener(e -> {
            JTextField nameField = new JTextField(10);
            JTextField connectToField = new JTextField(10);
            JTextField portField = new JTextField(5);
            JTextField usernameField = new JTextField();
            JPasswordField passwordField = new JPasswordField();

            JFrame popup = new JFrame("Add profile");
            popup.setResizable(false);
            popup.setLocation(frame.getX() + frame.getWidth() / 2 - 300, frame.getY() + frame.getHeight() / 2 - 60);
            popup.setSize(600, 120);
            popup.setLayout(new FlowLayout());

            popup.add(new JLabel("Name: "));
            popup.add(nameField);

            popup.add(new JLabel("Connect to: "));
            popup.add(connectToField);

            popup.add(new JLabel("Port: "));
            popup.add(portField);

            popup.add(new JLabel("Username: "));
            popup.add(usernameField);

            popup.add(new JLabel("Password: "));
            popup.add(passwordField);
            JLabel resultLabel = new JLabel("");

            JButton create = new JButton("Create");
            create.addActionListener(ev -> {
                if (nameField.getText().isBlank()) {
                    resultLabel.setText("Please enter a valid name");
                    resultLabel.setForeground(Color.RED);
                    return;
                }
                if (connectToField.getText().isBlank() || portField.getText().isBlank() || !isInt(portField.getText())) {
                    resultLabel.setText("Please enter a valid address and port");
                    resultLabel.setForeground(Color.RED);
                    return;
                }
                if (usernameField.getText().isBlank()) {
                    resultLabel.setText("Please enter a valid username");
                    resultLabel.setForeground(Color.RED);
                    return;
                }
                if (passwordField.getPassword().length == 0) {
                    resultLabel.setText("Please enter a valid username");
                    resultLabel.setForeground(Color.RED);
                    return;
                }
                Configuration.getConfiguration().addProject(new Project(
                        nameField.getText(),
                        connectToField.getText() + ":" + portField.getText(),
                        usernameField.getText(),
                        new String(passwordField.getPassword())
                        ));
                ConfigManager.getInstance().saveConfig();
                popup.dispose();
            });
            JLabel escape = new JLabel("");
            escape.setSize(600, 20);
            popup.add(escape);
            popup.add(create);
            JButton testConn = new JButton("Test connection");
            testConn.addActionListener(ev -> {
                Project testProject = new Project(
                        nameField.getText(),
                        connectToField.getText() + ":" + portField.getText(),
                        usernameField.getText(),
                        new String(passwordField.getPassword()));
                if (new Connection(testProject).testConnection()) {
                    resultLabel.setText("Test connection successful");
                    resultLabel.setForeground(Color.GREEN);
                } else {
                    resultLabel.setText("Test connection failed");
                    resultLabel.setForeground(Color.RED);
                }
            });
            popup.add(testConn);
            popup.add(resultLabel);

            popup.setVisible(true);
        });
        mainPanel.add(createProfile);

        mainPanel.add(cards);
        addClosableTab(tabbedPane, "Home", mainPanel);
    }
    private void createNewConnectionTab(Connection connection) {
        JPanel panel = new JPanel(new BorderLayout());

        JTabbedPane workTabs = new JTabbedPane();
        panel.setPreferredSize(new Dimension(frame.getSize().width - 50, frame.getSize().height - 100));

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(new JButton("Connect"));
        JButton btnNewQuery = new JButton("New Query");
        btnNewQuery.addActionListener(e -> {
            addEditorTab(workTabs, "New query");
        });
        toolBar.add(btnNewQuery);
        toolBar.add(new JButton("Refresh"));

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(connection.getProject().getName());

        JTree tree = new JTree(root);
        JScrollPane treeScroll = new JScrollPane(tree);
        treeScroll.setPreferredSize(new Dimension(250, 600));
        workTabs.addTab("Willkommen", new JLabel("Arbeitsfläche für " + connection.getProject().getName()));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroll, workTabs);
        splitPane.setDividerLocation(250);
        splitPane.setResizeWeight(0);

        panel.add(toolBar, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);

        addClosableTab(tabbedPane, connection.getProject().getName(), panel);
    }



    private void createMenuBar() {
        JMenuBar bar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.add("Open");
        bar.add(fileMenu);
        frame.setJMenuBar(bar);
    }

    private boolean isInt(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void addClosableTab(JTabbedPane tabbedPane, String title, Component content) {
        tabbedPane.add(content);

        int index = tabbedPane.indexOfComponent(content);

        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabPanel.setOpaque(false);

        JLabel label = new JLabel(title + " ");
        JButton closeButton = new JButton("x");
        closeButton.setMargin(new Insets(0, 2, 0, 2));
        closeButton.setBorder(BorderFactory.createEmptyBorder());
        closeButton.setFocusable(false);
        closeButton.setContentAreaFilled(false);

        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int i = tabbedPane.indexOfComponent(content);
                if (i != -1) {
                    tabbedPane.remove(i);
                }
            }
        });

        tabPanel.add(label);
        tabPanel.add(closeButton);

        tabbedPane.setTabComponentAt(index, tabPanel);
    }

    private void addEditorTab(JTabbedPane workTabs, String title) {
        JPanel editorPanel = new JPanel(new BorderLayout());

        JToolBar editorToolBar = new JToolBar();
        editorToolBar.setFloatable(false);
        JButton runButton = new JButton("▶ Ausführen");
        JButton saveButton = new JButton("💾 Speichern");
        JButton formatButton = new JButton("✨ Formatieren");
        editorToolBar.add(runButton);
        editorToolBar.add(saveButton);
        editorToolBar.add(formatButton);

        JTextArea editorArea = new JTextArea();
        editorArea.setFont(new Font("Consolas", Font.PLAIN, 14)); // Monospace-Font
        editorArea.setTabSize(4);
        JScrollPane editorScroll = new JScrollPane(editorArea);

        editorPanel.add(editorToolBar, BorderLayout.NORTH);
        editorPanel.add(editorScroll, BorderLayout.CENTER);

        workTabs.addTab(title, editorPanel);
        workTabs.setSelectedComponent(editorPanel);
    }

}