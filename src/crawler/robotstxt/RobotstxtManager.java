package crawler.robotstxt;

import java.util.Set;

public interface RobotstxtManager {

    //设置请求用户名
    void setUserAgent(String userAgent);

    //获取该URL对应服务器禁止访问的URL集合
    Set<String> getDisallow(String url);

}
