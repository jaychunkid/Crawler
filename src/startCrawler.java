import crawler.CrawlerImpl;
import crawler.fetcher.FetcherImpl;
import crawler.robotstxt.RobotstxtManagerImpl;
import crawler.url.CrawledURL;
import crawler.url.URLPoolImpl;
import crawler.parser.ParserImpl;
import util.Recorder;

import java.util.Scanner;

public class startCrawler {

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入种子URL: ");
        String root = scanner.nextLine();
        System.out.println("请输入本地域名: ");
        String localDomain = scanner.nextLine();
        System.out.println("请输入爬取深度: ");
        int deep = Integer.parseInt(scanner.nextLine());
        //设置URLPool参数
        URLPoolImpl pool = URLPoolImpl.getInstance();
        pool.setRecorder(new Recorder("doc/urls.txt"));
        pool.addLocalDomain(localDomain);
        pool.addURL(new CrawledURL(root, 0));
        pool.setStoreOuterUrl(true);
        //设置Crawler参数
        CrawlerImpl crawler = new CrawlerImpl(new FetcherImpl(), new ParserImpl(), pool, RobotstxtManagerImpl.getInstance());
        crawler.setRecorder(new Recorder("doc/log.txt"));
        crawler.setMaxDeep(deep);
        crawler.setCrawlContentType("text/html");
        long startTime = System.currentTimeMillis();
        crawler.start();
        double runningTime = System.currentTimeMillis() - startTime;
        //输出爬取信息
        System.out.println("抓取深度: " + deep);
        System.out.println("运行时间: " + runningTime/1000 + "s");
        System.out.println("抓取URL数目: " + pool.size() + " 其中站内网站数目: " + pool.sizeLocal() +
        " 站外网站数目: " + pool.sizeOuter() );
        System.out.println("抓取速度: " + (int)(pool.size()*1000/runningTime) + "个URL每秒");
    }

}
