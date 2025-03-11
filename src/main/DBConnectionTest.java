package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBConnectionTest {
    public static void main(String[] args) {
        // データベースの接続情報
        String url = "jdbc:mysql://localhost:3306/shift_management";
        String user = "root";
        String password = "Kk!qaz2wsx";


        try {
            // DBに接続
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ データベース接続成功！");

            // SQLを実行して結果を取得
            String sql = "SELECT * FROM shifts"; // ここを変更して、シフトデータを取得
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            // 結果を表示
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String shiftTime = rs.getString("shift_time");
                System.out.println("ID: " + id + ", 名前: " + name + ", シフト: " + shiftTime);
            }

            // リソースを閉じる
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
