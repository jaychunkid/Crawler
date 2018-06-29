package crawler.fetcher;

import util.ConnectUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

//实现服务器连接和网页抓取
public class FetcherImpl implements Fetcher {

    private String contentType;     //抓取文件类型

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public FetchResult fetch(URL url, String userAgent){
        int resultCode = FetchResult.RESULT_OK;
        HttpURLConnection httpURLConnection = null;
        try {
            //连接服务器
            httpURLConnection = ConnectUtil.connect(url, userAgent, contentType, 30000, 30000);
            int responseCode = httpURLConnection.getResponseCode();
            //判断服务器响应是否成功
            if (responseCode == 200 || responseCode == 304) {
                //若服务器响应成功，判断服务器响应文件类型是否为指定类型，如果不是返回错误码
                if (contentType != null && !httpURLConnection.getContentType().contains(contentType)) {
                    resultCode = FetchResult.ERROR_CONTENT_TYPE;
                    httpURLConnection = null;
                }
            } else if(responseCode == 301) {
                //若服务器响应永久重定向，返回错误码和重定向的url
                return new FetchResult(FetchResult.ERROR_RELOCATE, httpURLConnection.getHeaderField("Location"));
            } else if(responseCode == 302 || responseCode == 303 || responseCode == 307) {
                //若服务器响应临时重定向，直接获取临时重定向url尝试重新抓取
                httpURLConnection = ConnectUtil.connect(new URL(httpURLConnection.getHeaderField("Location")),
                        userAgent, contentType, 30000, 30000);
                int nResponseCode = httpURLConnection.getResponseCode();
                if(nResponseCode == 200 || nResponseCode == 304){
                    //若服务器响应成功，判断服务器响应文件类型是否为指定类型，如果不是返回错误码
                    if (contentType != null && !httpURLConnection.getContentType().contains(contentType)) {
                        resultCode = FetchResult.ERROR_CONTENT_TYPE;
                        httpURLConnection = null;
                    }
                } else {
                    //对临时重定向url，不对重定向响应码进行处理
                    throw new IOException("failed to follow temporary relocation");
                }
            } else {
                throw new IOException("failed to connect server");
            }
        } catch (IOException e){
            resultCode = FetchResult.ERROR_CONNECT;
            httpURLConnection = null;
        }
        InputStream inputStream = null;
        String charset = null;
        try{
            if(httpURLConnection != null) {
                //获取响应正文编码，响应头中未声明则默认utf-8
                String contentType = httpURLConnection.getContentType();
                if(contentType.contains("charset")){
                    charset = contentType.substring(contentType.indexOf("charset") + 8);
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getInputStream();
                }
            }
        } catch (IOException e){
            resultCode = FetchResult.ERROR_FETCH;
        }
        return new FetchResult(resultCode, inputStream, charset);
    }

}
