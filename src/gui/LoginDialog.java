package gui;

import java.awt.*;
import javax.swing.*;
import java.util.List;
import model.User;

public class LoginDialog extends JDialog {
    private JTextField userIdField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    private User loggedInUser;
    private List<User> users;

    public LoginDialog(List<User> users) {
        this.users = users;
        setTitle("Library Seat Reservation - Login");
        setSize(350, 200);
        setLocationRelativeTo(null);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Library Seat Reservation System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("User ID:"), gbc);
        gbc.gridx = 1;
        userIdField = new JTextField(12);
        panel.add(userIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(12);
        panel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> doLogin());
        panel.add(loginBtn, gbc);

        gbc.gridy = 4;
        statusLabel = new JLabel(" ", JLabel.CENTER);
        statusLabel.setForeground(Color.RED);
        panel.add(statusLabel, gbc);

        add(panel);
        getRootPane().setDefaultButton(loginBtn);
    }

    private void doLogin() {
        String id = userIdField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (id.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("Please enter both ID and password.");
            return;
        }

        for (User u : users) {
            if (u.getUserId().equals(id) && u.getPassword().equals(pass)) {
                if (u.isBanned()) {
                    statusLabel.setText("Your account is banned.");
                    return;
                }
                loggedInUser = u;
                dispose();
                return;
            }
        }
        statusLabel.setText("Invalid user ID or password.");
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }
}
