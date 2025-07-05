import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SportsTeamGUI extends JFrame {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/sportsdb";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "pass123";

    private JTable memberTable;
    private DefaultTableModel model;

    public SportsTeamGUI() {
        setTitle("Sports Team Member Entry System");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Team Member Panel
        JPanel teamPanel = new JPanel(new BorderLayout());
        model = new DefaultTableModel(new String[]{"ID", "Name", "Sport", "Age"}, 0);
        memberTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(memberTable);

        JButton addBtn = new JButton("Add Member");
        addBtn.addActionListener(e -> addMember());

        JButton updateBtn = new JButton("Update Member");
        updateBtn.addActionListener(e -> updateMember());

        JButton deleteBtn = new JButton("Delete Member");
        deleteBtn.addActionListener(e -> deleteMember());

        JPanel btnPanel = new JPanel();
        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);

        teamPanel.add(scrollPane, BorderLayout.CENTER);
        teamPanel.add(btnPanel, BorderLayout.SOUTH);

        // Panel Selection
        JPanel selectionPanel = new JPanel(new BorderLayout());
        JButton selectBtn = new JButton("Select Player");
        selectBtn.addActionListener(e -> selectPlayer());

        JButton viewTeamBtn = new JButton("View Formed Team");
        viewTeamBtn.addActionListener(e -> viewFormedTeam());

        JButton removeBtn = new JButton("Remove from Panel");
        removeBtn.addActionListener(e -> removeFromPanel());

        JPanel selBtnPanel = new JPanel();
        selBtnPanel.add(selectBtn);
        selBtnPanel.add(removeBtn);
        selBtnPanel.add(viewTeamBtn);


        selectionPanel.add(scrollPane, BorderLayout.CENTER);
        selectionPanel.add(selBtnPanel, BorderLayout.SOUTH);

        tabbedPane.add("Team Member", teamPanel);
        tabbedPane.add("Panel Member", selectionPanel);

        add(tabbedPane);
        loadMembers();

        JPanel playerPanel = new JPanel(new BorderLayout());

        JTextField searchField = new JTextField(10);
        JButton searchBtn = new JButton("View My Info");
        JTextArea playerInfoArea = new JTextArea(8, 40);
        playerInfoArea.setEditable(false);

        searchBtn.addActionListener(e -> {
            String idText = searchField.getText().trim();
            if (!idText.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Enter valid Player ID.");
                return;
            }
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String sql = "SELECT * FROM team_members WHERE player_id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(idText));
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Player ID: ").append(rs.getInt("player_id")).append("\n");
                    sb.append("Name: ").append(rs.getString("name")).append("\n");
                    sb.append("Sport: ").append(rs.getString("sport")).append("\n");
                    sb.append("Age: ").append(rs.getInt("age")).append("\n");


                    sql = "SELECT * FROM panel_selection WHERE player_id=?";
                    ps = conn.prepareStatement(sql);
                    ps.setInt(1, Integer.parseInt(idText));
                    ResultSet rs2 = ps.executeQuery();
                    if (rs2.next()) {
                        sb.append("📌 Status: Selected in team panel.\n");
                    } else {
                        sb.append("📌 Status: Not selected.\n");
                    }
                    playerInfoArea.setText(sb.toString());
                } else {
                    playerInfoArea.setText("No player found with that ID.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Enter Player ID:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        playerPanel.add(searchPanel, BorderLayout.NORTH);
        playerPanel.add(new JScrollPane(playerInfoArea), BorderLayout.CENTER);

        tabbedPane.add("Player Panel", playerPanel);  // 👈 Add to your main tabbedPane

    }

    private void loadMembers() {
        model.setRowCount(0);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT * FROM team_members";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("player_id"),
                        rs.getString("name"),
                        rs.getString("sport"),
                        rs.getInt("age")
                });
            }
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void addMember() {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField sportField = new JTextField();
        JTextField ageField = new JTextField();

        Object[] fields = {
                "Player ID:", idField,
                "Name:", nameField,
                "Sport:", sportField,
                "Age:", ageField
        };

        int result = JOptionPane.showConfirmDialog(null, fields, "Add New Member", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

                // First check if player_id already exists
                String checkSql = "SELECT COUNT(*) FROM team_members WHERE player_id = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                checkStmt.setInt(1, Integer.parseInt(idField.getText()));
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    showError("Player ID already exists. Use a different ID.");
                    return;
                }

                // Insert new player
                String sql = "INSERT INTO team_members (player_id, name, sport, age) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(idField.getText()));
                ps.setString(2, nameField.getText());
                ps.setString(3, sportField.getText());
                ps.setInt(4, Integer.parseInt(ageField.getText()));
                ps.executeUpdate();
                loadMembers();
            } catch (Exception e) {
                showError(e.getMessage());
            }
        }
    }


    private void updateMember() {
        int row = memberTable.getSelectedRow();
        if (row == -1) {
            showError("Please select a member to update.");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        JTextField nameField = new JTextField(model.getValueAt(row, 1).toString());
        JTextField sportField = new JTextField(model.getValueAt(row, 2).toString());
        JTextField ageField = new JTextField(model.getValueAt(row, 3).toString());

        Object[] fields = {
                "Name:", nameField,
                "Sport:", sportField,
                "Age:", ageField
        };

        int result = JOptionPane.showConfirmDialog(null, fields, "Update Member", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String sql = "UPDATE team_members SET name=?, sport=?, age=? WHERE player_id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, nameField.getText());
                ps.setString(2, sportField.getText());
                ps.setInt(3, Integer.parseInt(ageField.getText()));
                ps.setInt(4, id);
                ps.executeUpdate();
                loadMembers();
            } catch (Exception e) {
                showError(e.getMessage());
            }
        }
    }

    private void deleteMember() {
        int row = memberTable.getSelectedRow();
        if (row == -1) {
            showError("Please select a member to delete.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "DELETE FROM team_members WHERE player_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            loadMembers();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void selectPlayer() {
        int row = memberTable.getSelectedRow();
        if (row == -1) {
            showError("Please select a player to add to the panel.");
            return;
        }

        int playerId = (int) model.getValueAt(row, 0);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

            String checkSql = "SELECT COUNT(*) FROM panel_selection WHERE player_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, playerId);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                showError("Player already added to panel.");
                return;
            }


            String sql = "INSERT INTO panel_selection (player_id) VALUES (?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, playerId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Player added to selection panel.");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void removeFromPanel() {
        int row = memberTable.getSelectedRow();
        if (row == -1) {
            showError("Please select a player to remove from the panel.");
            return;
        }

        int playerId = (int) model.getValueAt(row, 0);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "DELETE FROM panel_selection WHERE player_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, playerId);
            int deleted = ps.executeUpdate();
            if (deleted > 0) {
                JOptionPane.showMessageDialog(this, "Player removed from panel.");
            } else {
                JOptionPane.showMessageDialog(this, "Player was not in the panel.");
            }
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }


    private void viewFormedTeam() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT t.player_id, t.name, t.sport, t.age FROM team_members t " +
                    "JOIN panel_selection p ON t.player_id = p.player_id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            StringBuilder sb = new StringBuilder("Formed Team:\n\n");
            while (rs.next()) {
                sb.append("ID: ").append(rs.getInt("player_id"))
                        .append(", Name: ").append(rs.getString("name"))
                        .append(", Sport: ").append(rs.getString("sport"))
                        .append(", Age: ").append(rs.getInt("age")).append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString());
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, "Error: " + message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SportsTeamGUI gui = new SportsTeamGUI();
            gui.setVisible(true);
        });
    }
}
