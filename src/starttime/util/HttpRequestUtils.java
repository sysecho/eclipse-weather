package starttime.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class HttpRequestUtils {

  public static String sendGet(String url, String param) {
    String result = "";
    BufferedReader in = null;
    try {
        String urlNameString = url + "?" + param;
        URL realUrl = new URL(urlNameString);
        // 打开和URL之间的连接
        URLConnection connection = realUrl.openConnection();
        // 设置通用的请求属性
        connection.setRequestProperty("accept", "*/*");
        connection.setRequestProperty("connection", "Keep-Alive");
        connection.setRequestProperty("user-agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        // 建立实际的连接
        connection.connect();
        // 获取所有响应头字段
        // 定义 BufferedReader输入流来读取URL的响应
        in = new BufferedReader(new InputStreamReader(
                connection.getInputStream(), "utf-8"));
        String line;
        while ((line = in.readLine()) != null) {
            result += line;
        }
    } catch (Exception e) {
    	return null;
    }
    // 使用finally块来关闭输入流
    finally {
        try {
            if (in != null) {
                in.close();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
    return result;
}
}
