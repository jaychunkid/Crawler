package crawler;

import util.Recorder;

public interface Crawler {

    //设置日志记录器
    void setRecorder(Recorder recorder);

    //设置用户名
    void setUserAgent(String userAgent);

    //设置最大抓取深度
    void setMaxDeep(int maxDeep);

    //设置抓取文件内容
    void setCrawlContentType(String contentType);

    //开始抓取
    void start();

}
