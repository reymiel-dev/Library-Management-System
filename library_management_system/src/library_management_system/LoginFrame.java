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
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 19));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Library Login"),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));


        formPanel.add(new JLabel("Role:"));                formPanel.add(roleBox);
        formPanel.add(new JLabel("ID / Username:"));       formPanel.add(idField);
        formPanel.add(new JLabel("Password:"));            formPanel.add(passField);
        formPanel.add(new JLabel(""));                     formPanel.add(loginBtn);

        add(formPanel);
        getRootPane().setDefaultButton(loginBtn);
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
