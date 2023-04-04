import java.sql.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter a string: ");
        String input = sc.nextLine();
        sc.close();

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        String hashString = hexString.toString();

        Class.forName("oracle.jdbc.OracleDriver");
        Connection conn = null;
        try {
            String url = "jdbc:oracle:thin:@moniportal.com:6100/ORCL";
            conn = DriverManager.getConnection(url, "checker", "checker");

            String sql = "INSERT INTO hash_values (input, hash) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, input);
            pstmt.setString(2, hashString);
            pstmt.executeUpdate();

            System.out.println("Data has been successfully inserted into the CHCKER DB.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
}
