import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame{

    private Library library;
    private JComboBox<String> roleBox = new JComboBox<>(new String[]{"Member", "Admin"});
    private JTextField idField = new JTextField();
    private JPasswordField passField = new JPasswordField();
    private JButton loginBtn = new JButton("Login");

    public LoginFrame(Library library) {
        this.library = library;

        setTitle("Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 5, 5));

        add(new JLabel("Role:"));                add(roleBox);
        add(new JLabel("ID / Username:"));       add(idField);
        add(new JLabel("Password:"));            add(passField);
        add(new JLabel(""));                     add(loginBtn);

        loginBtn.addActionListener(e -> login());
    }

    private void login() {
        String id = idField.getText().trim();
        String pass = new String(passField.getPassword());

        if (roleBox.getSelectedItem().equals("Admin")) {
            if (id.equals("admin") && pass.equals("admin123")) {
                new AdminFrame(library).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Wrong admin login.");
            }
        } else {
            Member m = library.findMember(id);
            if (m == null) {
                JOptionPane.showMessageDialog(this, "Account not found.");
            } else if (!m.checkPassword(pass)) {
                JOptionPane.showMessageDialog(this, "Incorrect Password:");
            } else if (!m.isActive()) {
                JOptionPane.showConfirmDialog(this, "Your account is inactive.");
            } else {
                new MemberFrame(library, m).setVisible(true);
                dispose();
            }
        }
    }
}
