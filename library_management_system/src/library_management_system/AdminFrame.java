import javax.swing.*;

public class AdminFrame extends JFrame {
    public AdminFrame(Library library) {
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 550);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Items", new JPanel());
        tabs.addTab("Members", new JPanel());
        tabs.addTab("Loans", new JPanel());
        tabs.addTab("Statistics", new JPanel());

        add(tabs);
    }
}
