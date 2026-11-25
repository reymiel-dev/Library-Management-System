import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class MemberFrame extends JFrame {

    private final Library library;
    private final Member member;
    private final String mid;

    // Browse
    private final DefaultTableModel browseModel = Theme.model(Theme.ITEM_COLS);
    private final JTable browseTable = Theme.table(browseModel);
    private final JTextField browseSearch = Theme.searchField();
    private final CardLayout browseCards = new CardLayout();
    private final JPanel browseViewContainer = new JPanel(browseCards);
    private final JPanel photoGridPanel = new JPanel(new GridLayout(0, 3, 12, 12));
    private final JButton toggleViewBtn = Theme.button("List with Photo", Theme.ACCENT);
    private boolean photoMode = false;

    // Borrow / Return
    private final DefaultTableModel borrowModel = Theme.model(Theme.ITEM_COLS);
    private final JTable borrowTable = Theme.table(borrowModel);
    private final JTextField borrowSearch = Theme.searchField();
    private final DefaultTableModel myLoansModel = Theme.model(
            "Item ID", "Title", "Borrowed", "Due", "Status", "Current Fine");
    private final JTable myLoansTable = Theme.table(myLoansModel);
    private final JLabel borrowInfo = new JLabel();

    // History
    private final DefaultTableModel historyModel = Theme.model(
            "Item ID", "Title", "Borrowed", "Due", "Returned", "Status", "Fine");
    private final JTable historyTable = Theme.table(historyModel);

    // Fines
    private final JLabel finesLabel = new JLabel();
    private final DefaultTableModel fineModel = Theme.model(
            "Item ID", "Title", "Status", "Fine");
    private final JTable fineTable = Theme.table(fineModel);

    // Profile
    private final JLabel pId = new JLabel();
    private final JLabel pName = new JLabel();
    private final JLabel pType = new JLabel();
    private final JLabel pLimit = new JLabel();
    private final JLabel pStatus = new JLabel();
    private final JLabel pFines = new JLabel();

    public MemberFrame(Library library, Member member) {
        this.library = library;
        this.member = member;
        this.mid = member.getMemberId();

        setTitle("Member Dashboard - " + member.getName());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(980, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LibraryStorage.save(MemberFrame.this.library);
            }
        });

        add(Theme.header("Member Dashboard", "Welcome, " + member.getName(), () -> {
            LibraryStorage.save(this.library);
            dispose();
            new LoginFrame(this.library).setVisible(true);
        }), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Theme.BOLD);
        tabs.addTab("Browse", buildBrowseTab());
        tabs.addTab("Borrow / Return", buildBorrowTab());
        tabs.addTab("History", buildHistoryTab());
        tabs.addTab("Fines", buildFinesTab());
        tabs.addTab("Profile", buildProfileTab());
        tabs.addChangeListener(e -> refreshAll());
        add(tabs, BorderLayout.CENTER);

        refreshAll();
    }

    // ================= BROWSE =================

    // ================= BROWSE =================

    private JPanel buildBrowseTab() {
        JPanel bar = Theme.toolbar();
        bar.add(new JLabel("Search title, author, ISBN or category:"));
        bar.add(browseSearch);
        bar.add(toggleViewBtn);

        photoGridPanel.setBackground(Theme.BG);
        photoGridPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane photoScroll = Theme.scroll(photoGridPanel);
        photoScroll.getVerticalScrollBar().setUnitIncrement(16);

        browseViewContainer.add(Theme.scroll(browseTable), "LIST");
        browseViewContainer.add(photoScroll, "PHOTO");
        browseCards.show(browseViewContainer, "LIST");

        Theme.onChange(browseSearch, this::refreshBrowse);
        toggleViewBtn.addActionListener(e -> toggleBrowseView());

        return Theme.page(bar, browseViewContainer);
    }

    private void toggleBrowseView() {
        photoMode = !photoMode;
        if (photoMode) {
            toggleViewBtn.setText("List View");
            browseCards.show(browseViewContainer, "PHOTO");
        } else {
            toggleViewBtn.setText("List with Photo");
            browseCards.show(browseViewContainer, "LIST");
        }
        refreshBrowse();
    }

    private void refreshBrowse() {
        String key = browseSearch.getText().trim();
        fillItems(browseModel, key, false);
        rebuildPhotoGrid(key);
    }

    private void rebuildPhotoGrid(String key) {
        photoGridPanel.removeAll();
        List<LibraryItem> list = key.isEmpty() ? library.getItems() : library.searchItems(key);

        for (LibraryItem item : list) {
            photoGridPanel.add(buildPhotoCard(item));
        }

        photoGridPanel.revalidate();
        photoGridPanel.repaint();
    }

    private JPanel buildPhotoCard(LibraryItem item) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(Theme.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD5DAE8)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel imgLabel = new JLabel("", SwingConstants.CENTER);
        imgLabel.setPreferredSize(new Dimension(120, 160));
        imgLabel.setOpaque(true);
        imgLabel.setBackground(new Color(0xEEF2FA));
        imgLabel.setFont(Theme.BOLD);

        ImageIcon icon = loadCoverIcon(item.getItemId(), 120, 160);
        if (icon != null) {
            imgLabel.setIcon(icon);
        } else {
            imgLabel.setText(item.getItemId());
            imgLabel.setForeground(Theme.PRIMARY);
        }

        String subtitle = "";
        if (item instanceof Book) {
            Book b = (Book) item;
            subtitle = b.getAuthor() + " • " + b.getCategory();
        } else if (item instanceof Magazine) {
            Magazine m = (Magazine) item;
            subtitle = m.getPublisher() + " • Issue #" + m.getIssueNumber();
        }

        JPanel info = new JPanel(new GridLayout(0, 1, 2, 2));
        info.setOpaque(false);

        JLabel titleLbl = new JLabel(item.getTitle());
        titleLbl.setFont(Theme.BOLD);
        titleLbl.setForeground(Color.BLACK);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(Theme.BODY);
        subLbl.setForeground(Color.DARK_GRAY);

        JLabel availLbl = new JLabel(item.getItemType() + " (" + item.getYear() + ")  |  Avail: "
                + item.getAvailableCopies() + "/" + item.getTotalCopies());
        availLbl.setFont(Theme.BODY);
        availLbl.setForeground(item.isAvailable() ? Theme.SUCCESS : Theme.DANGER);

        info.add(titleLbl);
        info.add(subLbl);
        info.add(availLbl);

        card.add(imgLabel, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);
        return card;
    }

    private ImageIcon loadCoverIcon(String itemId, int w, int h) {
        String[] extensions = {".jpg", ".jpeg", ".png"};
        for (String ext : extensions) {
            java.io.File f = new java.io.File("covers/" + itemId + ext);
            if (f.exists() && f.isFile()) {
                ImageIcon raw = new ImageIcon(f.getAbsolutePath());
                if (raw.getIconWidth() > 0) {
                    Image scaled = raw.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaled);
                }
            }
        }
        return null;
    }

    private void fillItems(DefaultTableModel model, String key, boolean availableOnly) {
        List<LibraryItem> list = key.isEmpty() ? library.getItems() : library.searchItems(key);
        model.setRowCount(0);
        for (LibraryItem i : list) {
            if (availableOnly && !i.isAvailable()) continue;
            model.addRow(Theme.itemRow(i));
        }
    }

    // ================= BORROW / RETURN =================

    private JPanel buildBorrowTab() {
        JPanel bar = Theme.toolbar();
        bar.add(new JLabel("Search:"));
        bar.add(borrowSearch);
        JButton borrow = Theme.button("Borrow Selected", Theme.SUCCESS);
        bar.add(borrow);
        borrowInfo.setFont(Theme.BOLD);
        borrowInfo.setForeground(Theme.PRIMARY);
        bar.add(borrowInfo);

        Theme.onChange(borrowSearch, this::refreshBorrow);
        borrow.addActionListener(e -> borrowSelected());

        JPanel top = Theme.page(bar, Theme.scroll(borrowTable));

        JPanel retBar = Theme.toolbar();
        JLabel title = new JLabel("My current loans");
        title.setFont(Theme.BOLD);
        retBar.add(title);
        JButton ret = Theme.button("Return Selected", Theme.WARNING);
        retBar.add(ret);
        ret.addActionListener(e -> returnSelected());

        JPanel bottom = Theme.page(retBar, Theme.scroll(myLoansTable));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top, bottom);
        split.setResizeWeight(0.55);
        split.setBorder(null);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(split, BorderLayout.CENTER);
        return wrap;
    }

    private void refreshBorrow() {
        fillItems(borrowModel, borrowSearch.getText().trim(), true);

        myLoansModel.setRowCount(0);
        int active = 0;
        for (Loan l : library.getLoans()) {
            if (!l.getMemberId().equalsIgnoreCase(mid) || l.isReturned()) continue;
            active++;
            LibraryItem item = library.findItem(l.getItemId());
            myLoansModel.addRow(new Object[]{
                    l.getItemId(),
                    item == null ? "(removed)" : item.getTitle(),
                    l.getBorrowDate(), l.getDueDate(),
                    Theme.loanStatus(l), Theme.money(l.calculateFine())});
        }
        borrowInfo.setText("   Loans: " + active + " / " + member.getMembershipType().getBorrowingLimit());
    }

    private void borrowSelected() {
        String id = Theme.selectedId(this, borrowTable);
        if (id == null) return;
        info(library.borrowItem(mid, id));
        changed();
    }

    private void returnSelected() {
        String id = Theme.selectedId(this, myLoansTable);
        if (id == null) return;
        info(library.returnItem(mid, id));
        changed();
    }

    // ================= HISTORY =================

    private JPanel buildHistoryTab() {
        return Theme.page(null, Theme.scroll(historyTable));
    }

    private void refreshHistory() {
        historyModel.setRowCount(0);
        List<Loan> loans = library.getLoans();
        for (int i = loans.size() - 1; i >= 0; i--) {      // newest first
            Loan l = loans.get(i);
            if (!l.getMemberId().equalsIgnoreCase(mid)) continue;
            LibraryItem item = library.findItem(l.getItemId());
            historyModel.addRow(new Object[]{
                    l.getItemId(),
                    item == null ? "(removed)" : item.getTitle(),
                    l.getBorrowDate(), l.getDueDate(),
                    l.isReturned() ? l.getReturnDate().toString() : "-",
                    Theme.loanStatus(l), Theme.money(l.getFine())});
        }
    }

    // ================= FINES =================

    private JPanel buildFinesTab() {
        finesLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        finesLabel.setForeground(Theme.DANGER);

        JButton payAll = Theme.button("Pay All", Theme.SUCCESS);
        JButton payPart = Theme.button("Pay Amount...", Theme.ACCENT);
        payAll.addActionListener(e -> payAll());
        payPart.addActionListener(e -> payAmount());

        JPanel bar = Theme.toolbar();
        bar.add(finesLabel);
        bar.add(Box.createHorizontalStrut(20));
        bar.add(payAll);
        bar.add(payPart);

        JLabel note = new JLabel("Fines are added to your account when an overdue item is returned. "
                + "You cannot borrow while you have unpaid fines.");
        note.setFont(Theme.BODY);
        note.setForeground(Color.GRAY);

        JPanel north = new JPanel(new BorderLayout());
        north.setBackground(Theme.BG);
        north.add(bar, BorderLayout.NORTH);
        north.add(note, BorderLayout.SOUTH);

        return Theme.page(north, Theme.scroll(fineTable));
    }

    private void refreshFines() {
        finesLabel.setText("Unpaid fines: " + Theme.money(member.getUnpaidFines()));
        fineModel.setRowCount(0);
        for (Loan l : library.getLoans()) {
            if (!l.getMemberId().equalsIgnoreCase(mid) || l.getFine() <= 0) continue;
            LibraryItem item = library.findItem(l.getItemId());
            fineModel.addRow(new Object[]{
                    l.getItemId(),
                    item == null ? "(removed)" : item.getTitle(),
                    Theme.loanStatus(l), Theme.money(l.getFine())});
        }
    }

    private void payAll() {
        double owed = member.getUnpaidFines();
        if (owed <= 0) { info("You have no unpaid fines."); return; }
        if (JOptionPane.showConfirmDialog(this, "Pay " + Theme.money(owed) + "?", "Confirm",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        member.payFine(owed);
        info("Payment received. Thank you!");
        changed();
    }

    private void payAmount() {
        double owed = member.getUnpaidFines();
        if (owed <= 0) { info("You have no unpaid fines."); return; }
        String s = JOptionPane.showInputDialog(this, "Amount to pay (max " + Theme.money(owed) + "):");
        if (s == null) return;
        try {
            double amount = Double.parseDouble(s.trim());
            if (amount <= 0 || amount > owed) {
                error("Amount must be more than 0 and not above " + Theme.money(owed) + ".");
                return;
            }
            member.payFine(amount);
            info("Payment received. Thank you!");
            changed();
        } catch (NumberFormatException ex) {
            error("Please enter a valid number.");
        }
    }

    // ================= PROFILE =================

    private JPanel buildProfileTab() {
        JPanel grid = new JPanel(new GridLayout(0, 2, 10, 12));
        grid.setBackground(Theme.CARD);
        grid.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        String[] keys = {"Member ID", "Name", "Membership", "Borrowing limit", "Status", "Unpaid fines"};
        JLabel[] vals = {pId, pName, pType, pLimit, pStatus, pFines};
        for (int i = 0; i < keys.length; i++) {
            JLabel k = new JLabel(keys[i]);
            k.setFont(Theme.BODY);
            k.setForeground(Color.GRAY);
            vals[i].setFont(Theme.BOLD);
            grid.add(k);
            grid.add(vals[i]);
        }

        JButton editName = Theme.button("Edit Name", Theme.WARNING);
        JButton changePass = Theme.button("Change Password", Theme.PRIMARY);
        editName.addActionListener(e -> editName());
        changePass.addActionListener(e -> changePassword());

        JPanel bar = Theme.toolbar();
        bar.add(editName);
        bar.add(changePass);

        JPanel card = new JPanel(new BorderLayout());
        card.add(grid, BorderLayout.CENTER);
        card.setBorder(BorderFactory.createLineBorder(new Color(0xD5DAE8)));

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Theme.BG);
        center.add(card, BorderLayout.NORTH);

        return Theme.page(bar, center);
    }

    private void refreshProfile() {
        pId.setText(member.getMemberId());
        pName.setText(member.getName());
        pType.setText(member.getMembershipType().toString());
        pLimit.setText(String.valueOf(member.getMembershipType().getBorrowingLimit()));
        pStatus.setText(member.isActive() ? "Active" : "Inactive");
        pFines.setText(Theme.money(member.getUnpaidFines()));
    }

    private void editName() {
        String name = JOptionPane.showInputDialog(this, "New name:", member.getName());
        if (name == null || name.trim().isEmpty()) return;
        member.setName(name);
        setTitle("Member Dashboard - " + member.getName());
        changed();
    }

    private void changePassword() {
        Theme.Form f = new Theme.Form()
                .password("Current password")
                .password("New password (min 6)")
                .password("Confirm new password");
        if (!f.show(this, "Change Password")) return;

        if (!member.checkPassword(f.get(0))) { error("Current password is incorrect."); return; }
        if (f.get(1).length() < 6) { error("New password must be at least 6 characters."); return; }
        if (!f.get(1).equals(f.get(2))) { error("New passwords do not match."); return; }

        member.setPassword(f.get(1));
        info("Password changed.");
        changed();
    }

    // ================= shared =================

    private void refreshAll() {
        refreshBrowse();
        refreshBorrow();
        refreshHistory();
        refreshFines();
        refreshProfile();
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
