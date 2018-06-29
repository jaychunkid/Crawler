package crawler.robotstxt.Fetcher;

import util.ConnectUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

//连接服务器并抓取robots.txt内容
public class RobotstxtFetcher {

    //错误码
    public static final int RESULT_OK = 0;
    public static final int ERROR = 1;

    private String text;     //抓取到的文本内容

    public RobotstxtFetcher(){
        init();
    }

    //抓取指定URL
    public int fetch(URL url, String userAgent){
        init();
        try {
            HttpURLConnection httpURLConnection = ConnectUtil.connect(url, userAgent, null,
                    30000, 30000);
            //判断服务器返回的响应码是否为200
            if (httpURLConnection.getResponseCode() != 200) {
                throw new Exception("HTTP Request is not success, Response code is " +
                        httpURLConnection.getResponseCode());
            }
            //响应成功，读取抓取到的内容存入字符串中
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String str = null;
            while((str = reader.readLine()) != null){
                stringBuilder.append(str);
                stringBuilder.append("\r\n");
            }
            text = stringBuilder.toString();
            return RESULT_OK;
        } catch (Exception e){
            return ERROR;
        }
    }

    //获取抓取到的内容
    public String getText(){
        return text;
    }

    //初始化抓取结果
    private void init(){
        text = null;
    }

}
