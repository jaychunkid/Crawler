package util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectUtil {

    public static HttpURLConnection connect(URL url, String userAgent, String contentType, int connectTimeout,
                                            int readTimeout) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        if(contentType != null){
            httpURLConnection.setRequestProperty("Content-Type", contentType);
        }
        httpURLConnection.setRequestProperty("User-Agent", userAgent);
        httpURLConnection.setConnectTimeout(connectTimeout);
        httpURLConnection.setReadTimeout(readTimeout);
        httpURLConnection.connect();
        return httpURLConnection;
    }

}
