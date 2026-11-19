import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Shared colors, fonts and small UI helpers used by every frame. */
public class Theme {

    public static final Color PRIMARY = new Color(0x2C3E73);
    public static final Color ACCENT  = new Color(0x3F6FD8);
    public static final Color SUCCESS = new Color(0x2E8B57);
    public static final Color WARNING = new Color(0xD98A1F);
    public static final Color DANGER  = new Color(0xC0392B);
    public static final Color BG      = new Color(0xF4F6FB);
    public static final Color CARD    = Color.WHITE;

    public static final Font TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font BODY  = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font BOLD  = new Font("Segoe UI", Font.BOLD, 13);

    public static final String[] ITEM_COLS = {
            "Item ID", "Type", "Title", "Author / Publisher",
            "Year", "Details", "Total", "Available"
    };

    // ---------- components ----------

    public static JButton button(String text, Color bg) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = bg;
                if (!isEnabled()) c = Color.LIGHT_GRAY;
                else if (getModel().isPressed()) c = bg.darker();
                else if (getModel().isRollover()) c = bg.brighter();
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setRolloverEnabled(true);
        b.setForeground(Color.WHITE);
        b.setFont(BOLD);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return b;
    }

    public static DefaultTableModel model(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
    }

    public static JTable table(DefaultTableModel model) {
        JTable t = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF0F3FA));
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        };
        t.setRowHeight(28);
        t.setFont(BODY);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setSelectionBackground(new Color(0xCFDDFB));
        t.setSelectionForeground(Color.BLACK);
        t.setAutoCreateRowSorter(true);
        t.setFillsViewportHeight(true);

        JTableHeader h = t.getTableHeader();
        h.setFont(BOLD);
        h.setBackground(PRIMARY);
        h.setForeground(Color.WHITE);
        h.setPreferredSize(new Dimension(0, 32));
        h.setReorderingAllowed(false);
        return t;
    }

    public static JScrollPane scroll(JComponent c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createLineBorder(new Color(0xD5DAE8)));
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    public static JPanel toolbar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        p.setBackground(BG);
        return p;
    }

    /** Standard tab layout: a toolbar/north component and a centered body. */
    public static JPanel page(JComponent north, JComponent center) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 12, 12, 12));
        if (north != null) p.add(north, BorderLayout.NORTH);
        p.add(center, BorderLayout.CENTER);
        return p;
    }

    public static JPanel header(String title, String subtitle, Runnable onLogout) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(PRIMARY);
        p.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel t = new JLabel(title);
        t.setFont(TITLE);
        t.setForeground(Color.WHITE);
        JLabel s = new JLabel(subtitle);
        s.setFont(BODY);
        s.setForeground(new Color(0xC9D3F2));

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        left.add(t);
        left.add(s);

        JButton out = button("Logout", new Color(0x4A5A9A));
        out.addActionListener(e -> onLogout.run());
        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);
        right.add(out);

        p.add(left, BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    public static JPanel statCard(String label, JLabel value, Color accent) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, accent),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        JLabel l = new JLabel(label);
        l.setFont(BODY);
        l.setForeground(Color.GRAY);
        value.setFont(new Font("Segoe UI", Font.BOLD, 30));
        value.setForeground(PRIMARY);
        p.add(l, BorderLayout.NORTH);
        p.add(value, BorderLayout.CENTER);
        return p;
    }

    public static JTextField searchField() {
        JTextField f = new JTextField(22);
        f.setFont(BODY);
        return f;
    }

    // ---------- helpers ----------

    public static void onChange(JTextField field, Runnable action) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { action.run(); }
            public void removeUpdate(DocumentEvent e)  { action.run(); }
            public void changedUpdate(DocumentEvent e) { action.run(); }
        });
    }

    /** Returns the first column (ID) of the selected row, or null after warning. */
    public static String selectedId(Component parent, JTable t) {
        int r = t.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(parent, "Please select a row first.",
                    "Nothing selected", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return String.valueOf(t.getModel().getValueAt(t.convertRowIndexToModel(r), 0));
    }

    public static Integer parseInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    public static String money(double v) {
        return String.format("PHP %.2f", v);
    }

    public static String loanStatus(Loan l) {
        if (l.isReturned()) return "Returned";
        return l.calculateFine() > 0 ? "Overdue" : "Borrowed";
    }

    public static Object[] itemRow(LibraryItem i) {
        String by = "";
        String details = "";
        if (i instanceof Book) {
            Book b = (Book) i;
            by = b.getAuthor();
            details = "ISBN " + b.getIsbn() + " / " + b.getCategory();
        } else if (i instanceof Magazine) {
            Magazine m = (Magazine) i;
            by = m.getPublisher();
            details = "Issue #" + m.getIssueNumber();
        }
        return new Object[]{i.getItemId(), i.getItemType(), i.getTitle(), by,
                i.getYear(), details, i.getTotalCopies(), i.getAvailableCopies()};
    }

    // ---------- simple form dialog ----------

    public static class Form {
        private final JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        private final List<JComponent> fields = new ArrayList<>();

        public Form text(String label, String value, boolean editable) {
            JTextField f = new JTextField(value == null ? "" : value, 18);
            f.setEditable(editable);
            add(label, f);
            return this;
        }

        public Form password(String label) {
            add(label, new JPasswordField(18));
            return this;
        }

        public Form combo(String label, Object[] options, Object selected) {
            JComboBox<Object> c = new JComboBox<>(options);
            c.setSelectedItem(selected);
            add(label, c);
            return this;
        }

        private void add(String label, JComponent field) {
            JLabel l = new JLabel(label);
            l.setFont(BODY);
            panel.add(l);
            panel.add(field);
            fields.add(field);
        }

        public boolean show(Component parent, String title) {
            return JOptionPane.showConfirmDialog(parent, panel, title,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE)
                    == JOptionPane.OK_OPTION;
        }

        public String get(int i) {
            JComponent c = fields.get(i);
            if (c instanceof JPasswordField) return new String(((JPasswordField) c).getPassword());
            if (c instanceof JTextField) return ((JTextField) c).getText().trim();
            return String.valueOf(((JComboBox<?>) c).getSelectedItem());
        }
    }
}
