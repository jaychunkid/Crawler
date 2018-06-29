package crawler.url;

import util.Recorder;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

//实现URL线程池
public class URLPoolImpl implements URLPool {

    private static URLPoolImpl INSTANCE = new URLPoolImpl();              //单例，线程安全

    private List<CrawledURL> urlWaiting = new LinkedList<>();     //等待处理的URL队列
    private Set<CrawledURL> urlProcessed = new HashSet<>();       //已处理的URL集合
    private Set<CrawledURL> urlOuter = new HashSet<>();           //外部网站URL集合
    private Set<String> localDomain = new HashSet<>();            //本地域名集合
    private Recorder recorder = null;                             //URL记录器
    private boolean storeOuterUrl = false;                        //是否存储外部网站

    //获取单例对象
    public static URLPoolImpl getInstance(){
        return INSTANCE;
    }

    public void setRecorder(Recorder recorder){
        this.recorder = recorder;
    }

    public void setStoreOuterUrl(boolean storeOuterUrl){
        this.storeOuterUrl = storeOuterUrl;
    }

    public void addLocalDomain(String domain){
        localDomain.add(domain);
    }

    public void clearLocalDomain(){
        localDomain.clear();
    }

    public CrawledURL getURL(){
        if(urlWaiting.size() > 0){
            //若待处理队列不为空，从队列首取出返回，并放入已处理集合
            CrawledURL URL = urlWaiting.get(0);
            urlWaiting.remove(0);
            urlProcessed.add(URL);
            return URL;
        } else {
            return null;
        }
    }

    public boolean addURL(CrawledURL url){
        boolean isLocal = false;
        if(localDomain.size() > 0) {
            //若已设置本地域名，检查URL是否属于本地域名
            for (String domain : localDomain) {
                if (url.getURL().contains(domain)) {
                    isLocal = true;
                    break;
                }
            }
        } else {
            isLocal = true;
        }
        if(storeOuterUrl && !isLocal && !urlOuter.contains(url)){
            //URL不属于本地域名，且不再外部URL集合中，加入外部URL集合，返回添加成功
            urlOuter.add(url);
            recordURL(url);
            return true;
        } else if(isLocal && !urlProcessed.contains(url) && !urlWaiting.contains(url)){
            //URL属于本地域名，且不在已处理集合和待处理队列中，加入待处理队列，返回添加成功
            urlWaiting.add(url);
            recordURL(url);
            return true;
        } else {
            //添加失败
            return false;
        }
    }

    //向文档中记录URL
    private void recordURL(CrawledURL url){
        if(recorder != null){
            recorder.recordToFile(url.getURL());
        }
    }

    public int sizeProcessed(){
        return urlProcessed.size();
    }

    public int sizeOuter(){
        return urlOuter.size();
    }

    public int sizeLocal(){
        return urlProcessed.size() + urlWaiting.size();
    }

    public int sizeWaiting(){
        return urlWaiting.size();
    }

    public int size(){
        return urlProcessed.size() + urlOuter.size() + urlWaiting.size();
    }

}
