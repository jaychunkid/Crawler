package crawler.url;

//封装URL信息
public class CrawledURL {

    private String URL;     //URL
    private int deep;       //抓取深度

    public CrawledURL(String URL, int deep){
        this.URL = URL;
        this.deep = deep;
    }

    public String getURL(){
        return URL;
    }

    public int getDeep() {
        return deep;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CrawledURL){
            //URL字符串相同的URL视为同一条URL
            return URL.equals(((CrawledURL) obj).URL);
        } else if(obj instanceof String){
            return URL.equals(obj);
        } else {
            return super.equals(obj);
        }
    }

    @Override
    public int hashCode() {
        return URL.hashCode();
    }

}
