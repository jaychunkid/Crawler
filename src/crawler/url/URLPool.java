package crawler.url;

import util.Recorder;

public interface URLPool {

    //设置URL记录器
    void setRecorder(Recorder recorder);

    //设置是否存储外部网站
    void setStoreOuterUrl(boolean storeOuterUrl);

    //添加本地域名
    void addLocalDomain(String domain);

    //清空本地域名
    void clearLocalDomain();

    //获取待处理的URL
    CrawledURL getURL();

    //添加URL
    boolean addURL(CrawledURL url);

    //返回已处理URL数目
    int sizeProcessed();

    //返回外部URL数目
    int sizeOuter();

    //返回本地URL数目
    int sizeLocal();

    //返回待处理URL数目
    int sizeWaiting();

    //返回总的URL数目
    int size();

}
