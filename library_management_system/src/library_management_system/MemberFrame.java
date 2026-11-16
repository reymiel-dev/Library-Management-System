import javax.swing.*;

public class MemberFrame extends JFrame {
    
    public MemberFrame(Library library, Member member) {
        setTitle("Member Dashboard - " + member.getName());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Browse", new JPanel());
        tabs.addTab("Borrow / Return", new JPanel());
        tabs.addTab("History", new JPanel());
        tabs.addTab("Fines", new JPanel());
        tabs.addTab("Profile", new JPanel());

        add(tabs);
    }
}