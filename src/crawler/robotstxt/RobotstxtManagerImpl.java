package crawler.robotstxt;

import crawler.robotstxt.Fetcher.RobotstxtFetcher;
import crawler.robotstxt.Parser.RobotstxtParser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//robots.txt内容管理
public class RobotstxtManagerImpl implements RobotstxtManager {

    private static RobotstxtManagerImpl INSTANCE = new RobotstxtManagerImpl();     //单例，线程安全

    private String userAgent = "NIR2018S201530541753";                     //向服务器请求时的用户名
    private Map<String, Set<String>> disallowMap = new HashMap<>();        //服务器网址，禁止访问的URL集合的键值对

    //获取单例对象
    public static RobotstxtManagerImpl getInstance(){
        return INSTANCE;
    }

    public void setUserAgent(String userAgent){
        this.userAgent = userAgent;
    }

    public Set<String> getDisallow(String url){
        //获取对应服务器根目录
        String base = getBaseUrl(url);
        //判断本地是否存在缓存
        if(disallowMap.containsKey(base)){
            return disallowMap.get(base);
        } else {
            //不存在缓存则先更新缓存
            updateDisallow(base);
            return disallowMap.get(base);
        }
    }

    //更新指定URL的缓存
    private void updateDisallow(String base){
        try{
            RobotstxtFetcher fetcher = new RobotstxtFetcher();
            RobotstxtParser parser = new RobotstxtParser();
            if(fetcher.fetch(new URL(base + "/robots.txt"), userAgent) == RobotstxtFetcher.RESULT_OK){
                //更新成功，将URL存入缓存
                parser.parse(fetcher.getText(), base, userAgent);
                disallowMap.put(base, parser.getDisallow());
            } else {
                //更新失败，连接失败或服务器不存在该文件，放入空集合
                disallowMap.put(base , new HashSet<>());
            }
        } catch (MalformedURLException e){
            //URL规范化失败（理论上不会发生），放入空集合
            disallowMap.put(base , new HashSet<>());
        }
    }

    //获取URL对应的服务器根目录URL
    private String getBaseUrl(String url){
        int index = url.indexOf("//") + 2;
        int tailIndex = url.indexOf("/", index);
        if(tailIndex != -1){
            return url.substring(0, tailIndex);
        } else {
            return url;
        }
    }

}
