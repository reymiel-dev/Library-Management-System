import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class AdminFrame extends JFrame {

    private final Library library;

    // Items
    private final DefaultTableModel itemModel = Theme.model(Theme.ITEM_COLS);
    private final JTable itemTable = Theme.table(itemModel);
    private final JTextField itemSearch = Theme.searchField();

    // Members
    private final DefaultTableModel memberModel = Theme.model(
            "Member ID", "Name", "Membership", "Limit", "Active Loans", "Status", "Unpaid Fines");
    private final JTable memberTable = Theme.table(memberModel);

    // Loans
    private final DefaultTableModel loanModel = Theme.model(
            "Member ID", "Member", "Item ID", "Title", "Borrowed", "Due", "Returned", "Status", "Fine");
    private final JTable loanTable = Theme.table(loanModel);
    private final JComboBox<String> loanFilter =
            new JComboBox<>(new String[]{"All", "Active", "Overdue", "Returned"});

    // Statistics
    private static final String[] STAT_NAMES = {
            "Books", "Magazines", "Members", "Total Copies",
            "Available Copies", "Borrowed Copies", "Active Loans", "Overdue Loans"};
    private final JLabel[] statValues = new JLabel[STAT_NAMES.length];
    private final JLabel libraryNameLabel = new JLabel();

    public AdminFrame(Library library) {
        this.library = library;

        setTitle("Admin Dashboard - " + library.getLibraryName());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1000, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LibraryStorage.save(AdminFrame.this.library);
            }
        });

        add(Theme.header("Admin Dashboard", "Signed in as administrator", () -> {
            LibraryStorage.save(this.library);
            dispose();
            new LoginFrame(this.library).setVisible(true);
        }), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Theme.BOLD);
        tabs.addTab("Items", buildItemsTab());
        tabs.addTab("Members", buildMembersTab());
        tabs.addTab("Loans", buildLoansTab());
        tabs.addTab("Statistics", buildStatsTab());
        tabs.addChangeListener(e -> refreshAll());
        add(tabs, BorderLayout.CENTER);

        refreshAll();
    }

    // ================= ITEMS =================

    private JPanel buildItemsTab() {
        JPanel bar = Theme.toolbar();
        bar.add(new JLabel("Search:"));
        bar.add(itemSearch);

        JButton addBook = Theme.button("+ Book", Theme.ACCENT);
        JButton addMag = Theme.button("+ Magazine", Theme.ACCENT);
        JButton edit = Theme.button("Edit", Theme.WARNING);
        JButton remove = Theme.button("Remove", Theme.DANGER);
        bar.add(addBook);
        bar.add(addMag);
        bar.add(edit);
        bar.add(remove);

        Theme.onChange(itemSearch, this::refreshItems);
        addBook.addActionListener(e -> addBook());
        addMag.addActionListener(e -> addMagazine());
        edit.addActionListener(e -> editItem());
        remove.addActionListener(e -> removeItem());

        return Theme.page(bar, Theme.scroll(itemTable));
    }

    private void refreshItems() {
        String key = itemSearch.getText().trim();
        List<LibraryItem> list = key.isEmpty() ? library.getItems() : library.searchItems(key);
        itemModel.setRowCount(0);
        for (LibraryItem i : list) {
            itemModel.addRow(Theme.itemRow(i));
        }
    }

    private boolean validItem(String title, Integer year, Integer copies, int minCopies) {
        if (title.isEmpty() || year == null || year < 0 || copies == null || copies < minCopies) {
            error("Please check the fields:\n- Title is required\n- Year must be a number (0 or more)\n"
                    + "- Copies must be a number of at least " + minCopies);
            return false;
        }
        return true;
    }

    private void addBook() {
        Theme.Form f = new Theme.Form()
                .text("Item ID", null, true)
                .text("Title", null, true)
                .text("Author", null, true)
                .text("Year", null, true)
                .text("Copies", "1", true)
                .text("ISBN", null, true)
                .text("Category", null, true);
        if (!f.show(this, "Add Book")) return;

        Integer year = Theme.parseInt(f.get(3));
        Integer copies = Theme.parseInt(f.get(4));
        if (f.get(0).isEmpty()) { error("Item ID is required."); return; }
        if (!validItem(f.get(1), year, copies, 1)) return;

        info(library.addItem(new Book(f.get(0), f.get(1), f.get(2), year, copies, f.get(5), f.get(6))));
        changed();
    }

    private void addMagazine() {
        Theme.Form f = new Theme.Form()
                .text("Item ID", null, true)
                .text("Title", null, true)
                .text("Publisher", null, true)
                .text("Year", null, true)
                .text("Copies", "1", true)
                .text("Issue number", "1", true);
        if (!f.show(this, "Add Magazine")) return;

        Integer year = Theme.parseInt(f.get(3));
        Integer copies = Theme.parseInt(f.get(4));
        Integer issue = Theme.parseInt(f.get(5));
        if (f.get(0).isEmpty()) { error("Item ID is required."); return; }
        if (!validItem(f.get(1), year, copies, 1)) return;
        if (issue == null || issue < 0) { error("Issue number must be 0 or more."); return; }

        info(library.addItem(new Magazine(f.get(0), f.get(1), f.get(2), year, copies, issue)));
        changed();
    }

    private void editItem() {
        String id = Theme.selectedId(this, itemTable);
        if (id == null) return;
        LibraryItem item = library.findItem(id);
        if (item == null) return;

        int borrowed = item.getTotalCopies() - item.getAvailableCopies();

        if (item instanceof Book) {
            Book b = (Book) item;
            Theme.Form f = new Theme.Form()
                    .text("Item ID", b.getItemId(), false)
                    .text("Title", b.getTitle(), true)
                    .text("Author", b.getAuthor(), true)
                    .text("Year", String.valueOf(b.getYear()), true)
                    .text("Total copies", String.valueOf(b.getTotalCopies()), true)
                    .text("ISBN", b.getIsbn(), true)
                    .text("Category", b.getCategory(), true);
            if (!f.show(this, "Edit Book")) return;

            Integer year = Theme.parseInt(f.get(3));
            Integer copies = Theme.parseInt(f.get(4));
            if (!validItem(f.get(1), year, copies, borrowed)) return;

            b.setTitle(f.get(1));
            b.setAuthor(f.get(2));
            b.setYear(year);
            b.setTotalCopies(copies);
            b.setIsbn(f.get(5));
            b.setCategory(f.get(6));

        } else if (item instanceof Magazine) {
            Magazine m = (Magazine) item;
            Theme.Form f = new Theme.Form()
                    .text("Item ID", m.getItemId(), false)
                    .text("Title", m.getTitle(), true)
                    .text("Publisher", m.getPublisher(), true)
                    .text("Year", String.valueOf(m.getYear()), true)
                    .text("Total copies", String.valueOf(m.getTotalCopies()), true)
                    .text("Issue number", String.valueOf(m.getIssueNumber()), true);
            if (!f.show(this, "Edit Magazine")) return;

            Integer year = Theme.parseInt(f.get(3));
            Integer copies = Theme.parseInt(f.get(4));
            Integer issue = Theme.parseInt(f.get(5));
            if (!validItem(f.get(1), year, copies, borrowed)) return;
            if (issue == null || issue < 0) { error("Issue number must be 0 or more."); return; }

            m.setTitle(f.get(1));
            m.setPublisher(f.get(2));
            m.setYear(year);
            m.setTotalCopies(copies);
            m.setIssueNumber(issue);
        }
        changed();
    }

    private void removeItem() {
        String id = Theme.selectedId(this, itemTable);
        if (id == null) return;
        if (JOptionPane.showConfirmDialog(this, "Remove item " + id + "?", "Confirm",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        info(library.removeItem(id));
        changed();
    }

    // ================= MEMBERS =================

    private JPanel buildMembersTab() {
        JPanel bar = Theme.toolbar();
        JButton register = Theme.button("+ Register", Theme.ACCENT);
        JButton edit = Theme.button("Edit", Theme.WARNING);
        JButton toggle = Theme.button("Activate / Deactivate", Theme.PRIMARY);
        JButton reset = Theme.button("Reset Password", Theme.PRIMARY);
        JButton delete = Theme.button("Delete", Theme.DANGER);
        bar.add(register);
        bar.add(edit);
        bar.add(toggle);
        bar.add(reset);
        bar.add(delete);

        register.addActionListener(e -> registerMember());
        edit.addActionListener(e -> editMember());
        toggle.addActionListener(e -> toggleMember());
        reset.addActionListener(e -> resetPassword());
        delete.addActionListener(e -> deleteMember());

        return Theme.page(bar, Theme.scroll(memberTable));
    }

    private void refreshMembers() {
        memberModel.setRowCount(0);
        for (Member m : library.getMembers()) {
            memberModel.addRow(new Object[]{
                    m.getMemberId(), m.getName(), m.getMembershipType(),
                    m.getMembershipType().getBorrowingLimit(),
                    library.countActiveLoans(m.getMemberId()),
                    m.isActive() ? "Active" : "Inactive",
                    Theme.money(m.getUnpaidFines())});
        }
    }

    private void registerMember() {
        Theme.Form f = new Theme.Form()
                .text("Member ID", null, true)
                .text("Name", null, true)
                .password("Password (min 6)")
                .combo("Membership", MembershipType.values(), MembershipType.REGULAR);
        if (!f.show(this, "Register Member")) return;

        if (f.get(0).isEmpty() || f.get(1).isEmpty()) { error("ID and name are required."); return; }
        if (f.get(2).length() < 6) { error("Password must be at least 6 characters."); return; }

        Member m = new Member(f.get(0), f.get(1), f.get(2), MembershipType.valueOf(f.get(3)));
        info(library.registerMember(m));
        changed();
    }

    private Member selectedMember() {
        String id = Theme.selectedId(this, memberTable);
        return id == null ? null : library.findMember(id);
    }

    private void editMember() {
        Member m = selectedMember();
        if (m == null) return;
        Theme.Form f = new Theme.Form()
                .text("Member ID", m.getMemberId(), false)
                .text("Name", m.getName(), true)
                .combo("Membership", MembershipType.values(), m.getMembershipType());
        if (!f.show(this, "Edit Member")) return;
        if (f.get(1).isEmpty()) { error("Name is required."); return; }
        m.setName(f.get(1));
        m.setMembershipType(MembershipType.valueOf(f.get(2)));
        changed();
    }

    private void toggleMember() {
        Member m = selectedMember();
        if (m == null) return;
        m.setActive(!m.isActive());
        changed();
    }

    private void resetPassword() {
        Member m = selectedMember();
        if (m == null) return;
        Theme.Form f = new Theme.Form().password("New password (min 6)");
        if (!f.show(this, "Reset Password - " + m.getMemberId())) return;
        if (f.get(0).length() < 6) { error("Password must be at least 6 characters."); return; }
        m.setPassword(f.get(0));
        info("Password updated.");
        changed();
    }

    private void deleteMember() {
        Member m = selectedMember();
        if (m == null) return;
        if (JOptionPane.showConfirmDialog(this, "Delete member " + m.getMemberId() + "?", "Confirm",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        info(library.removeMember(m.getMemberId()));
        changed();
    }

    // ================= LOANS =================

    private JPanel buildLoansTab() {
        JPanel bar = Theme.toolbar();
        bar.add(new JLabel("Show:"));
        bar.add(loanFilter);
        JButton refresh = Theme.button("Refresh", Theme.ACCENT);
        bar.add(refresh);

        loanFilter.addActionListener(e -> refreshLoans());
        refresh.addActionListener(e -> refreshAll());

        return Theme.page(bar, Theme.scroll(loanTable));
    }

    private void refreshLoans() {
        String filter = String.valueOf(loanFilter.getSelectedItem());
        loanModel.setRowCount(0);

        List<Loan> loans = library.getLoans();
        for (int i = loans.size() - 1; i >= 0; i--) {      // newest first
            Loan l = loans.get(i);
            String status = Theme.loanStatus(l);

            boolean show = filter.equals("All")
                    || (filter.equals("Active") && !l.isReturned())
                    || filter.equals(status);
            if (!show) continue;

            Member m = library.findMember(l.getMemberId());
            LibraryItem item = library.findItem(l.getItemId());
            loanModel.addRow(new Object[]{
                    l.getMemberId(),
                    m == null ? "(deleted)" : m.getName(),
                    l.getItemId(),
                    item == null ? "(removed)" : item.getTitle(),
                    l.getBorrowDate(),
                    l.getDueDate(),
                    l.isReturned() ? l.getReturnDate().toString() : "-",
                    status,
                    Theme.money(l.getFine())});
        }
    }

    // ================= STATISTICS =================

    private JPanel buildStatsTab() {
        Color[] colors = {Theme.ACCENT, Theme.ACCENT, Theme.PRIMARY, Theme.PRIMARY,
                Theme.SUCCESS, Theme.WARNING, Theme.WARNING, Theme.DANGER};

        JPanel grid = new JPanel(new GridLayout(2, 4, 14, 14));
        grid.setBackground(Theme.BG);
        for (int i = 0; i < STAT_NAMES.length; i++) {
            statValues[i] = new JLabel("0");
            grid.add(Theme.statCard(STAT_NAMES[i], statValues[i], colors[i]));
        }

        libraryNameLabel.setFont(Theme.TITLE);
        libraryNameLabel.setForeground(Theme.PRIMARY);
        JButton rename = Theme.button("Rename Library", Theme.PRIMARY);
        rename.addActionListener(e -> renameLibrary());

        JPanel top = Theme.toolbar();
        top.add(libraryNameLabel);
        top.add(rename);

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(Theme.BG);
        body.add(grid, BorderLayout.NORTH);

        return Theme.page(top, body);
    }

    private void refreshStats() {
        int books = 0, mags = 0, total = 0, avail = 0, active = 0, overdue = 0;

        for (LibraryItem i : library.getItems()) {
            total += i.getTotalCopies();
            avail += i.getAvailableCopies();
            if (i instanceof Book) books++;
            else if (i instanceof Magazine) mags++;
        }
        for (Loan l : library.getLoans()) {
            if (!l.isReturned()) {
                active++;
                if (l.calculateFine() > 0) overdue++;
            }
        }

        int[] values = {books, mags, library.getMembers().size(), total,
                avail, total - avail, active, overdue};
        for (int i = 0; i < values.length; i++) {
            statValues[i].setText(String.valueOf(values[i]));
        }
        libraryNameLabel.setText(library.getLibraryName());
    }

    private void renameLibrary() {
        String name = JOptionPane.showInputDialog(this, "New library name:", library.getLibraryName());
        if (name == null || name.trim().isEmpty()) return;
        library.setLibraryName(name);
        setTitle("Admin Dashboard - " + library.getLibraryName());
        changed();
    }

    // ================= shared =================

    private void refreshAll() {
        refreshItems();
        refreshMembers();
        refreshLoans();
        refreshStats();
    }

    private void changed() {
        LibraryStorage.save(library);
        refreshAll();
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Library", JOptionPane.INFORMATION_MESSAGE);
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
