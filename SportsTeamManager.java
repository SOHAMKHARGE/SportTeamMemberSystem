import java.sql.*;
import java.util.Scanner;

public class SportsTeamManager {

    static final String DB_URL = "jdbc:mysql://localhost:3306/sportsdb";
    static final String USER = "root";
    static final String PASS = "pass123"; // Change this to your MySQL password

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Scanner sc = new Scanner(System.in);
            int choice;

            do {
                System.out.println("\n===== Sports Team Member Entry System =====");
                System.out.println("1. Add Member");
                System.out.println("2. View Members");
                System.out.println("3. Update Member");
                System.out.println("4. Delete Member");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");
                choice = sc.nextInt();

                switch (choice) {
                    case 1 -> addMember(conn, sc);
                    case 2 -> viewMembers(conn);
                    case 3 -> updateMember(conn, sc);
                    case 4 -> deleteMember(conn, sc);
                    case 5 -> System.out.println("Exiting program.");
                    default -> System.out.println("Invalid choice.");
                }
            } while (choice != 5);

            sc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addMember(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter Player ID: ");
        int id = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Sport: ");
        String sport = sc.nextLine();
        System.out.print("Enter Age: ");
        int age = sc.nextInt();

        String sql = "INSERT INTO team_members (player_id, name, sport, age) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.setString(2, name);
        stmt.setString(3, sport);
        stmt.setInt(4, age);
        stmt.executeUpdate();
        System.out.println("Member added successfully.");
    }

    private static void viewMembers(Connection conn) throws SQLException {
        String sql = "SELECT * FROM team_members";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        System.out.println("\n--- Team Members ---");
        while (rs.next()) {
            System.out.println("ID: " + rs.getInt("player_id") +
                    ", Name: " + rs.getString("name") +
                    ", Sport: " + rs.getString("sport") +
                    ", Age: " + rs.getInt("age"));
        }
    }

    private static void updateMember(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter Player ID to update: ");
        int id = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter New Name: ");
        String name = sc.nextLine();
        System.out.print("Enter New Sport: ");
        String sport = sc.nextLine();
        System.out.print("Enter New Age: ");
        int age = sc.nextInt();

        String sql = "UPDATE team_members SET name=?, sport=?, age=? WHERE player_id=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, name);
        stmt.setString(2, sport);
        stmt.setInt(3, age);
        stmt.setInt(4, id);
        int updated = stmt.executeUpdate();

        if (updated > 0) {
            System.out.println("Member updated successfully.");
        } else {
            System.out.println("Member not found.");
        }
    }

    private static void deleteMember(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter Player ID to delete: ");
        int id = sc.nextInt();

        String sql = "DELETE FROM team_members WHERE player_id=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        int deleted = stmt.executeUpdate();

        if (deleted > 0) {
            System.out.println("Member deleted successfully.");
        } else {
            System.out.println("Member not found.");
        }
    }
}
