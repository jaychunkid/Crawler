package crawler.fetcher;

import java.net.URL;

public interface Fetcher {

    //设置抓取文件类型
    void setContentType(String contentType);

    //向指定URL抓取文件
    FetchResult fetch(URL url, String userAgent);
}
