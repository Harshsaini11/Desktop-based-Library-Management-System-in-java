package harshsaini;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.PrinterException;
import java.sql.*;
import java.text.MessageFormat;

public class LibraryManagementSystem extends JFrame {
    private static final String DB_URL = "jdbc:sqlite:library.db";
    // Main Table
    private JTable booksTable;
    private DefaultTableModel tableModel;

    // Card Layout
    private JPanel cardPanel;
    private CardLayout cardLayout;

    // Form Fields
    private JTextField txtAddBookId, txtAddTitle, txtAddAuthor;
    private JTextField txtEditBookId, txtEditTitle, txtEditAuthor;
    private JTextField txtIssueBookId, txtIssueUserId;
    private JTextField txtReturnBookId;
    private JTextField txtDeleteBookId;

    // Search & Filter
    private JTextField txtSearch;
    private JComboBox<String> comboFilterStatus;

    public LibraryManagementSystem() {
        setTitle("Library Management System");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Init DB
        initDatabase();

        // ================= TOP PANEL: SIMPLE INPUT FORMS =================
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // 1. ADD BOOK FORM
        JPanel pnlAddBook = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        txtAddBookId = new JTextField(8);
        txtAddTitle = new JTextField(12);
        txtAddAuthor = new JTextField(12);
        JButton btnSubmitAdd = new JButton("Submit Add Book");

        pnlAddBook.add(new JLabel("Book ID:")); pnlAddBook.add(txtAddBookId);
        pnlAddBook.add(new JLabel("Title:")); pnlAddBook.add(txtAddTitle);
        pnlAddBook.add(new JLabel("Author:")); pnlAddBook.add(txtAddAuthor);
        pnlAddBook.add(btnSubmitAdd);

        // 2. EDIT BOOK FORM
        JPanel pnlEditBook = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        txtEditBookId = new JTextField(8);
        txtEditTitle = new JTextField(12);
        txtEditAuthor = new JTextField(12);
        JButton btnSubmitEdit = new JButton("Save Edits");

        pnlEditBook.add(new JLabel("Book ID to Edit:")); pnlEditBook.add(txtEditBookId);
        pnlEditBook.add(new JLabel("New Title:")); pnlEditBook.add(txtEditTitle);
        pnlEditBook.add(new JLabel("New Author:")); pnlEditBook.add(txtEditAuthor);
        pnlEditBook.add(btnSubmitEdit);

        // 3. ISSUE BOOK FORM
        JPanel pnlIssue = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        txtIssueBookId = new JTextField(8);
        txtIssueUserId = new JTextField(8);
        JButton btnSubmitIssue = new JButton("Confirm Issue");

        pnlIssue.add(new JLabel("Book ID:")); pnlIssue.add(txtIssueBookId);
        pnlIssue.add(new JLabel("User ID:")); pnlIssue.add(txtIssueUserId);
        pnlIssue.add(btnSubmitIssue);

        // 4. RETURN BOOK FORM
        JPanel pnlReturn = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        txtReturnBookId = new JTextField(8);
        JButton btnSubmitReturn = new JButton("Confirm Return");

        pnlReturn.add(new JLabel("Book ID to Return:")); pnlReturn.add(txtReturnBookId);
        pnlReturn.add(btnSubmitReturn);

        // 5. DELETE BOOK FORM
        JPanel pnlDelete = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        txtDeleteBookId = new JTextField(8);
        JButton btnSubmitDelete = new JButton("Delete Book");

        pnlDelete.add(new JLabel("Book ID to Delete:")); pnlDelete.add(txtDeleteBookId);
        pnlDelete.add(btnSubmitDelete);

        // Add Forms to Card Panel
        cardPanel.add(pnlAddBook, "ADD_BOOK");
        cardPanel.add(pnlEditBook, "EDIT_BOOK");
        cardPanel.add(pnlIssue, "ISSUE_BOOK");
        cardPanel.add(pnlReturn, "RETURN_BOOK");
        cardPanel.add(pnlDelete, "DELETE_BOOK");

        add(cardPanel, BorderLayout.NORTH);

        // ================= CENTER PANEL: SEARCH & BOOKS TABLE =================
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Books Inventory & View"));

        // Search Bar & Print PDF
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        txtSearch = new JTextField(15);
        JButton btnSearch = new JButton("Search");
        comboFilterStatus = new JComboBox<>(new String[]{"All Books", "Available", "Issued"});
        JButton btnPrintBooksPdf = new JButton("Print / Save PDF");

        searchPanel.add(new JLabel("Search (ID/Title/Author):"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(new JLabel(" Filter Status:"));
        searchPanel.add(comboFilterStatus);
        searchPanel.add(btnPrintBooksPdf);

        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Status", "Issued To"}, 0);
        booksTable = new JTable(tableModel);
        centerPanel.add(new JScrollPane(booksTable), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // ================= BOTTOM PANEL: NAVIGATION BUTTONS =================
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));

        JButton navAddBook = new JButton("Add Book View");
        JButton navEditBook = new JButton("Edit Book View");
        JButton navIssueBook = new JButton("Issue Book View");
        JButton navReturnBook = new JButton("Return Book View");
        JButton navDeleteBook = new JButton("Delete Book View");
        JButton navManageUsers = new JButton("Manage Users");
        JButton navViewHistory = new JButton("View History");
        JButton navRefresh = new JButton("Refresh List");

        buttonPanel.add(navAddBook);
        buttonPanel.add(navEditBook);
        buttonPanel.add(navIssueBook);
        buttonPanel.add(navReturnBook);
        buttonPanel.add(navDeleteBook);
        buttonPanel.add(navManageUsers);
        buttonPanel.add(navViewHistory);
        buttonPanel.add(navRefresh);

        add(buttonPanel, BorderLayout.SOUTH);

        // ================= LISTENERS =================
        btnSearch.addActionListener(e -> loadBooksData());
        comboFilterStatus.addActionListener(e -> loadBooksData());
        btnPrintBooksPdf.addActionListener(e -> printTableToPdf(booksTable, "Library Books Inventory"));

        navAddBook.addActionListener(e -> cardLayout.show(cardPanel, "ADD_BOOK"));
        navEditBook.addActionListener(e -> cardLayout.show(cardPanel, "EDIT_BOOK"));
        navIssueBook.addActionListener(e -> cardLayout.show(cardPanel, "ISSUE_BOOK"));
        navReturnBook.addActionListener(e -> cardLayout.show(cardPanel, "RETURN_BOOK"));
        navDeleteBook.addActionListener(e -> cardLayout.show(cardPanel, "DELETE_BOOK"));
        navManageUsers.addActionListener(e -> openUserManagerDialog());
        navViewHistory.addActionListener(e -> openHistoryDialog());
        navRefresh.addActionListener(e -> {
            txtSearch.setText("");
            comboFilterStatus.setSelectedIndex(0);
            loadBooksData();
        });

        // Add Book Logic
        btnSubmitAdd.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtAddBookId.getText().trim());
                String title = txtAddTitle.getText().trim();
                String author = txtAddAuthor.getText().trim();

                if (title.isEmpty() || author.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Title and Author are required!");
                    return;
                }

                try (Connection conn = DriverManager.getConnection(DB_URL);
                     PreparedStatement pstmt = conn.prepareStatement("INSERT INTO books(id, title, author) VALUES(?, ?, ?)")) {
                    pstmt.setInt(1, id); pstmt.setString(2, title); pstmt.setString(3, author);
                    pstmt.executeUpdate();

                    logActivity("ADD_BOOK", "Added Book ID " + id + " (" + title + ")");
                    JOptionPane.showMessageDialog(this, "Book Added Successfully!");
                    txtAddBookId.setText(""); txtAddTitle.setText(""); txtAddAuthor.setText("");
                    loadBooksData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding book! Check unique ID.");
            }
        });

        // Edit Book Logic
        btnSubmitEdit.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtEditBookId.getText().trim());
                String newTitle = txtEditTitle.getText().trim();
                String newAuthor = txtEditAuthor.getText().trim();

                if (newTitle.isEmpty() || newAuthor.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "New Title and Author required!");
                    return;
                }

                try (Connection conn = DriverManager.getConnection(DB_URL);
                     PreparedStatement pstmt = conn.prepareStatement("UPDATE books SET title = ?, author = ? WHERE id = ?")) {
                    pstmt.setString(1, newTitle); pstmt.setString(2, newAuthor); pstmt.setInt(3, id);
                    if (pstmt.executeUpdate() > 0) {
                        logActivity("EDIT_BOOK", "Updated Book ID " + id);
                        JOptionPane.showMessageDialog(this, "Book Updated Successfully!");
                        txtEditBookId.setText(""); txtEditTitle.setText(""); txtEditAuthor.setText("");
                        loadBooksData();
                    } else {
                        JOptionPane.showMessageDialog(this, "Book ID not found!");
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Enter valid Book ID and details!");
            }
        });

        // Issue Book Logic
        btnSubmitIssue.addActionListener(e -> {
            try {
                int bookId = Integer.parseInt(txtIssueBookId.getText().trim());
                int userId = Integer.parseInt(txtIssueUserId.getText().trim());

                try (Connection conn = DriverManager.getConnection(DB_URL)) {
                    PreparedStatement checkBook = conn.prepareStatement("SELECT is_issued FROM books WHERE id = ?");
                    checkBook.setInt(1, bookId);
                    ResultSet rsBook = checkBook.executeQuery();

                    if (!rsBook.next() || rsBook.getInt("is_issued") == 1) {
                        JOptionPane.showMessageDialog(this, "Book not available or already issued!");
                        return;
                    }

                    PreparedStatement checkUser = conn.prepareStatement("SELECT name FROM users WHERE id = ?");
                    checkUser.setInt(1, userId);
                    if (!checkUser.executeQuery().next()) {
                        JOptionPane.showMessageDialog(this, "User ID does not exist!");
                        return;
                    }

                    PreparedStatement issue = conn.prepareStatement("UPDATE books SET is_issued = 1, issued_to = ? WHERE id = ?");
                    issue.setInt(1, userId); issue.setInt(2, bookId);
                    issue.executeUpdate();

                    logActivity("ISSUE_BOOK", "Book ID " + bookId + " issued to User ID " + userId);
                    JOptionPane.showMessageDialog(this, "Book Issued Successfully!");
                    txtIssueBookId.setText(""); txtIssueUserId.setText("");
                    loadBooksData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Enter valid Book ID and User ID!");
            }
        });

        // Return Book Logic
        btnSubmitReturn.addActionListener(e -> {
            try {
                int bookId = Integer.parseInt(txtReturnBookId.getText().trim());

                try (Connection conn = DriverManager.getConnection(DB_URL);
                     PreparedStatement pstmt = conn.prepareStatement("UPDATE books SET is_issued = 0, issued_to = NULL WHERE id = ?")) {
                    pstmt.setInt(1, bookId);
                    if (pstmt.executeUpdate() > 0) {
                        logActivity("RETURN_BOOK", "Returned Book ID " + bookId);
                        JOptionPane.showMessageDialog(this, "Book Returned Successfully!");
                        txtReturnBookId.setText("");
                        loadBooksData();
                    } else {
                        JOptionPane.showMessageDialog(this, "Book ID not found!");
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Enter valid Book ID!");
            }
        });

        // Delete Book Logic
        btnSubmitDelete.addActionListener(e -> {
            try {
                int bookId = Integer.parseInt(txtDeleteBookId.getText().trim());

                try (Connection conn = DriverManager.getConnection(DB_URL);
                     PreparedStatement pstmt = conn.prepareStatement("DELETE FROM books WHERE id = ?")) {
                    pstmt.setInt(1, bookId);
                    if (pstmt.executeUpdate() > 0) {
                        logActivity("DELETE_BOOK", "Deleted Book ID " + bookId);
                        JOptionPane.showMessageDialog(this, "Book Deleted Successfully!");
                        txtDeleteBookId.setText("");
                        loadBooksData();
                    } else {
                        JOptionPane.showMessageDialog(this, "Book ID not found!");
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Enter valid Book ID!");
            }
        });

        // Initial Load
        loadBooksData();
    }

    // ================= PDF PRINT HELPER =================
    private void printTableToPdf(JTable table, String title) {
        try {
            MessageFormat header = new MessageFormat(title);
            MessageFormat footer = new MessageFormat("Page {0,number,integer}");
            boolean complete = table.print(JTable.PrintMode.FIT_WIDTH, header, footer);
            if (complete) {
                JOptionPane.showMessageDialog(this, "Printing / PDF Export Completed!");
            }
        } catch (PrinterException pe) {
            JOptionPane.showMessageDialog(this, "Printing Failed: " + pe.getMessage());
        }
    }

    // ================= LOAD & FILTER BOOKS DATA =================
    private void loadBooksData() {
        tableModel.setRowCount(0);
        String searchKeyword = txtSearch.getText().trim();
        String filterStatus = comboFilterStatus.getSelectedItem().toString();

        StringBuilder query = new StringBuilder(
                "SELECT b.id, b.title, b.author, b.is_issued, u.name FROM books b LEFT JOIN users u ON b.issued_to = u.id WHERE 1=1 "
        );

        if (!searchKeyword.isEmpty()) {
            query.append(" AND (CAST(b.id AS TEXT) LIKE '%").append(searchKeyword)
                 .append("%' OR b.title LIKE '%").append(searchKeyword)
                 .append("%' OR b.author LIKE '%").append(searchKeyword).append("%')");
        }

        if ("Available".equals(filterStatus)) {
            query.append(" AND b.is_issued = 0");
        } else if ("Issued".equals(filterStatus)) {
            query.append(" AND b.is_issued = 1");
        }

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query.toString())) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                boolean isIssued = rs.getInt("is_issued") == 1;
                String userName = rs.getString("name");

                String status = isIssued ? "Issued" : "Available";
                String issuedTo = isIssued ? userName : "-";

                tableModel.addRow(new Object[]{id, title, author, status, issuedTo});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching books: " + e.getMessage());
        }
    }

    // ================= AUDIT LOGS / VIEW HISTORY DIALOG =================
    private void openHistoryDialog() {
        JDialog historyDialog = new JDialog(this, "Audit Activity History Logs", true);
        historyDialog.setSize(750, 450);
        historyDialog.setLocationRelativeTo(this);
        historyDialog.setLayout(new BorderLayout(10, 10));

        DefaultTableModel historyTableModel = new DefaultTableModel(
                new String[]{"Log ID", "Timestamp", "Action", "Details"}, 0
        );
        JTable historyTable = new JTable(historyTableModel);
        historyDialog.add(new JScrollPane(historyTable), BorderLayout.CENTER);

        String sql = "SELECT * FROM logs ORDER BY id DESC";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                historyTableModel.addRow(new Object[]{
                        rs.getInt("id"), rs.getString("timestamp"),
                        rs.getString("action"), rs.getString("details")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(historyDialog, "Error loading history: " + ex.getMessage());
        }

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnPrintHistory = new JButton("Print / Save PDF");
        actionPanel.add(btnPrintHistory);

        btnPrintHistory.addActionListener(e -> printTableToPdf(historyTable, "Library Audit History Logs"));

        historyDialog.add(actionPanel, BorderLayout.SOUTH);
        historyDialog.setVisible(true);
    }

    // ================= USER MANAGEMENT DIALOG =================
    private void openUserManagerDialog() {
        JDialog userDialog = new JDialog(this, "User Management & Borrowed Books", true);
        userDialog.setSize(700, 450);
        userDialog.setLocationRelativeTo(this);
        userDialog.setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JTextField txtUserId = new JTextField(6);
        JTextField txtUserName = new JTextField(12);

        inputPanel.add(new JLabel("User ID:")); inputPanel.add(txtUserId);
        inputPanel.add(new JLabel("User Name:")); inputPanel.add(txtUserName);

        userDialog.add(inputPanel, BorderLayout.NORTH);

        DefaultTableModel userTableModel = new DefaultTableModel(
                new String[]{"User ID", "User Name", "Issued Count", "Borrowed Book Titles"}, 0
        );
        JTable userTable = new JTable(userTableModel);
        userDialog.add(new JScrollPane(userTable), BorderLayout.CENTER);

        Runnable loadUsers = () -> {
            userTableModel.setRowCount(0);
            String userQuery = "SELECT u.id, u.name, " +
                    "COUNT(b.id) AS issued_count, " +
                    "GROUP_CONCAT(b.title, ', ') AS borrowed_books " +
                    "FROM users u LEFT JOIN books b ON u.id = b.issued_to " +
                    "GROUP BY u.id, u.name";

            try (Connection conn = DriverManager.getConnection(DB_URL);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(userQuery)) {

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int count = rs.getInt("issued_count");
                    String books = rs.getString("borrowed_books");

                    userTableModel.addRow(new Object[]{
                            id, name, count, (books != null ? books : "None")
                    });
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(userDialog, "Error loading users: " + ex.getMessage());
            }
        };

        loadUsers.run();

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnAdd = new JButton("Add User");
        JButton btnEdit = new JButton("Edit User");
        JButton btnDelete = new JButton("Delete User");

        btnPanel.add(btnAdd); btnPanel.add(btnEdit); btnPanel.add(btnDelete);
        userDialog.add(btnPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtUserId.getText().trim());
                String name = txtUserName.getText().trim();
                if (name.isEmpty()) return;

                try (Connection conn = DriverManager.getConnection(DB_URL);
                     PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users(id, name) VALUES(?, ?)")) {
                    pstmt.setInt(1, id); pstmt.setString(2, name);
                    pstmt.executeUpdate();
                    logActivity("ADD_USER", "Added User ID " + id);
                    txtUserId.setText(""); txtUserName.setText("");
                    loadUsers.run();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(userDialog, "Error adding user!");
            }
        });

        btnEdit.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtUserId.getText().trim());
                String newName = txtUserName.getText().trim();
                try (Connection conn = DriverManager.getConnection(DB_URL);
                     PreparedStatement pstmt = conn.prepareStatement("UPDATE users SET name = ? WHERE id = ?")) {
                    pstmt.setString(1, newName); pstmt.setInt(2, id);
                    if (pstmt.executeUpdate() > 0) {
                        logActivity("EDIT_USER", "Updated User ID " + id);
                        txtUserId.setText(""); txtUserName.setText("");
                        loadUsers.run();
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(userDialog, "Enter User ID & New Name!");
            }
        });

        btnDelete.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtUserId.getText().trim());
                try (Connection conn = DriverManager.getConnection(DB_URL)) {
                    PreparedStatement check = conn.prepareStatement("SELECT id FROM books WHERE issued_to = ?");
                    check.setInt(1, id);
                    if (check.executeQuery().next()) {
                        JOptionPane.showMessageDialog(userDialog, "User has active borrowed books!");
                        return;
                    }

                    PreparedStatement pstmt = conn.prepareStatement("DELETE FROM users WHERE id = ?");
                    pstmt.setInt(1, id);
                    if (pstmt.executeUpdate() > 0) {
                        logActivity("DELETE_USER", "Deleted User ID " + id);
                        txtUserId.setText(""); txtUserName.setText("");
                        loadUsers.run();
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(userDialog, "Enter User ID to delete!");
            }
        });

        userDialog.setVisible(true);
    }

    // ================= DATABASE HELPERS =================
    private void initDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS books (" +
                    "id INTEGER PRIMARY KEY, " +
                    "title TEXT NOT NULL, " +
                    "author TEXT NOT NULL, " +
                    "is_issued INTEGER DEFAULT 0, " +
                    "issued_to INTEGER)");

            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY, " +
                    "name TEXT NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS logs (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "action TEXT, " +
                    "details TEXT, " +
                    "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }
    }

    private void logActivity(String action, String details) {
        String sql = "INSERT INTO logs(action, details) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, action);
            pstmt.setString(2, details);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error logging activity: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryManagementSystem().setVisible(true));
    }

}
