package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ShiftChecker {
    public static void main(String[] args) {
        // MySQL データベースの接続情報
        String url = "jdbc:mysql://localhost:3306/shift_db"; // 接続先のデータベース名
        String user = "root"; // データベースのユーザー名
        String password = "Kk!qaz2wsx"; // データベースのパスワード

        // 現在の年月 (YYYYMM) を取得し、動的にテーブル名を指定
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));

        // `shiftYYYYMM` の time フィールドが現在時刻の 30 分以内のレコードを取得
        // `timecardYYYYMM` の status が '未出勤' のものを対象とする
        String sql = String.format("""
            SELECT u.line_id, u.name  -- LINE ID とユーザー名を取得
            FROM shift%s s  -- 動的に shift テーブルを指定
            JOIN timecard%s t ON s.date = t.date AND s.id = t.id  -- シフトとタイムカードを結合
            JOIN user u ON s.id = u.id  -- ユーザー情報と結合
            WHERE s.time BETWEEN NOW() AND NOW() + INTERVAL 30 MINUTE  -- 出勤30分前のシフトを抽出
            AND t.status = '未出勤'  -- 未出勤のユーザーのみ対象
        """, currentMonth, currentMonth);

        // データベース接続 & クエリ実行
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("データベース接続成功！");
            System.out.println("実行するSQL: " + sql); // デバッグ用 (確認しやすいように表示)

            // 通知対象の LINE ユーザー ID を格納するリスト
            List<String> usersToNotify = new ArrayList<>();

            // クエリの結果を処理
            while (rs.next()) {
                String lineId = rs.getString("line_id"); // LINE ID
                String name = rs.getString("name"); // ユーザー名

                if (lineId != null) { // LINE ID が存在する場合のみ通知対象
                    usersToNotify.add(lineId);
                    System.out.println("通知対象: " + name + " (LINE ID: " + lineId + ")");
                } else { // LINE ID がない場合、通知できないため警告を出力
                    System.out.println("ユーザー " + name + " に LINE ID が設定されていません！");
                }
            }

            // LINE通知を送信
            sendLineNotifications(usersToNotify);

        } catch (Exception e) {
            e.printStackTrace(); // エラー発生時のログ出力
        }
    }

    /**
     * LINE に通知を送信するメソッド
     * @param usersToNotify 通知対象のユーザーIDリスト
     */
    private static void sendLineNotifications(List<String> usersToNotify) {
        for (String lineId : usersToNotify) {
            // 各ユーザーに「出勤30分前の通知」を送信
            LineMessageSender.sendMessage(lineId, "出勤30分前です！忘れずに報告してください！");
        }
    }
}
