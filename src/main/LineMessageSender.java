package main;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LineMessageSender {
    /**
     * LINE メッセージを送信するメソッド
     * @param userId 送信先の LINE ユーザー ID（宛先）
     * @param message 送信するメッセージの内容（テキスト）
     */
    public static void sendMessage(String userId, String message) {
        try {
            // LINE のチャネルアクセストークン（LINE Developers から取得）
            // これを LINE の API リクエスト時にヘッダーに含めて認証を行う
            String accessToken = "uNWuShRI+a//xR7fB7NC06u23ofKtpCO0pOcf0+ofn2PHRaFiOdSNYOZ1VL/FzTSP8aKpFQAJmGzoeCX2aiBimY0fd4gSpQ/pIhYuTke4dVqHdSGdbzMAvHfzqCUhmgXWREA+yOEtCEb0jeTHybDrgdB04t89/1O/w1cDnyilFU="; 
            
            // LINE メッセージ送信用のエンドポイント URL
            // ここに POST リクエストを送信することで LINE へメッセージを送れる
            URL url = new URL("https://api.line.me/v2/bot/message/push");

            // HTTP 接続の準備（HttpURLConnection を使用）
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST"); // HTTP メソッドを POST に設定
            conn.setRequestProperty("Authorization", "Bearer " + accessToken); // 認証用のアクセストークンを設定
            conn.setRequestProperty("Content-Type", "application/json"); // リクエストのデータ形式を JSON に設定
            conn.setDoOutput(true); // データ送信を許可（POSTのため必要）

            // 送信する JSON ボディを作成（LINE の API 仕様に従う）
            String jsonBody = "{"
                + "\"to\":\"" + userId + "\","  // 宛先ユーザー ID
                + "\"messages\":[{\"type\":\"text\",\"text\":\"" + message + "\"}]" // 送信メッセージ
                + "}";

            // HTTP リクエストのボディ部分を送信
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes("UTF-8")); // JSON を UTF-8 で送信
                os.flush(); // バッファをクリア
            }

            // HTTP レスポンスコードを取得
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                // 正常にメッセージが送信できた場合
                System.out.println("メッセージ送信成功！ (UserID: " + userId + ")");
            } else {
                // 何かしらのエラーが発生した場合（エラーコードを表示）
                System.out.println("エラー発生: " + responseCode + " (UserID: " + userId + ")");
            }
        } catch (Exception e) {
            // 例外発生時のエラーログを出力
            e.printStackTrace();
        }
    }
}
