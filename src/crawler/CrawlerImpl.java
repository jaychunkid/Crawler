package crawler;

import crawler.exception.WrongCharsetException;
import crawler.fetcher.FetchResult;
import crawler.fetcher.Fetcher;
import crawler.parser.Parser;
import crawler.parser.ParserResult;
import crawler.robotstxt.RobotstxtManager;
import crawler.url.URLPool;
import util.LogUtil;
import util.Mark;
import util.Operation;
import crawler.url.CrawledURL;
import util.Recorder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Set;

//实现对爬虫工作流程的控制
public class CrawlerImpl implements Crawler {

    private Fetcher fetcher;              //内容抓取器
    private Parser parser;                //内容解析器
    private URLPool urlPool;              //URL池
    private Recorder recorder;            //日志记录器
    private RobotstxtManager manager;     //robots.txt内容管理
    private int maxDeep = 1;              //最大抓取深度，默认为1
    private String userAgent = "NIR2018S201530541753";     //爬虫身份

    public CrawlerImpl(Fetcher fetcher, Parser parser, URLPool urlPool, RobotstxtManager manager){
        this.fetcher = fetcher;
        this.parser = parser;
        this.urlPool = urlPool;
        this.manager = manager;
    }

    public void setRecorder(Recorder recorder){
        this.recorder = recorder;
    }

    public void setUserAgent(String userAgent){
        this.userAgent = userAgent;
    }

    public void setMaxDeep(int maxDeep){
        this.maxDeep = maxDeep;
    }

    public void setCrawlContentType(String contentType){
        fetcher.setContentType(contentType);
    }

    public void start(){
        CrawledURL url = null;
        while((url = urlPool.getURL()) != null){
            //检查当前URL的抓取深度是否达到最大深度
            if(url.getDeep() < maxDeep) {
                processURL(url);
            }
        }
    }

    //URL处理
    private void processURL(CrawledURL URL){
        try{
            //检查当前URL指向页面是否禁止抓取
            if(!isDisallow(URL.getURL())){
                //抓取网页
                URL url = new URL(URL.getURL());
                FetchResult fetchResult = fetchURL(url);
                if(fetchResult.getResultCode() == FetchResult.RESULT_OK){
                    //抓取成功，进行解析
                    String charset = null;
                    Reader reader = null;
                    //根据抓取到的字符集包装输入流
                    if((charset = fetchResult.getCharset()) != null){
                        reader = new BufferedReader(new InputStreamReader(fetchResult.getInputStream(), charset));
                    } else {
                        reader = new BufferedReader(new InputStreamReader(fetchResult.getInputStream()));
                    }
                    try {
                        parseURL(reader, url, URL.getDeep(), charset);
                    } catch (WrongCharsetException e){
                        //包装输入流的字符集与网页文本中声明的字符集不同，则进行重新抓取网页内容进行处理
                        String nCharset = e.getCorrectCharset().name();
                        reprocessURL(URL, nCharset);
                    }
                    reader.close();
                } else if(fetchResult.getResultCode() == FetchResult.ERROR_RELOCATE){
                    //抓取结果为重定向时，获取重定向地址加入url池
                    String relocationUrl = fetchResult.getLocation();
                    urlPool.addURL(new CrawledURL(relocationUrl, URL.getDeep()));
                }
            }
        } catch (Exception e){
            //对应url对象创建失败（理论上不会出现）或者reader关闭失败情况
            //不做处理
        }
    }

    private void reprocessURL(CrawledURL URL, String charset){
        try{
            //抓取网页
            URL url = new URL(URL.getURL());
            FetchResult fetchResult = fetchURL(url);
            if(fetchResult.getResultCode() == FetchResult.RESULT_OK){
                //抓取成功，进行解析，直接使用传入的字符集包装输入流
                Reader reader = new BufferedReader(new InputStreamReader(fetchResult.getInputStream(), charset));
                parseURL(reader, url, URL.getDeep(), charset);
                reader.close();
            }
        } catch (Exception e){
            //对应字符集错误情况（理论上不会出现），url对象创建失败（理论上不会出现）或者reader关闭失败情况
            //不做处理
        }
    }

    //URL内容抓取
    private FetchResult fetchURL(URL url) {
        FetchResult result = fetcher.fetch(url, userAgent);
        //根据抓取结果输出日志
        switch (result.getResultCode()) {
            case FetchResult.RESULT_OK:
                log(Operation.CONNECT, url.toString(), Mark.SUCCESS);
                log(Operation.FETCH, url.toString(), Mark.SUCCESS);
                break;
            case FetchResult.ERROR_RELOCATE:
            case FetchResult.ERROR_CONNECT:
                log(Operation.CONNECT, url.toString(), Mark.ERROR);
                break;
            case FetchResult.ERROR_FETCH:
                log(Operation.CONNECT, url.toString(), Mark.SUCCESS);
                log(Operation.FETCH, url.toString(), Mark.ERROR);
                break;
            case FetchResult.ERROR_CONTENT_TYPE:
                //对不是返回指定文件类型的URL，仅连接输出连接成功日志，不进行之后的解析操作
                log(Operation.CONNECT, url.toString(), Mark.SUCCESS);
                break;
            default:
                break;
        }
        return result;
    }

    //URL内容解析
    private void parseURL(Reader reader, URL url, int deep, String charset) throws WrongCharsetException {
        parser.parse(reader, url, charset);
        ParserResult result = parser.getResult();
        //根据解析结果输出日志，并将URL加入线程池
        switch (result.getResultCode()) {
            case ParserResult.RESULT_OK:
                for (String parsedUrl : result.getUrlList()) {
                    urlPool.addURL(new CrawledURL(parsedUrl, deep + 1));
                }
            case ParserResult.ERROR_NO_FOLLOW:
                //对禁止跟踪的URL，仍输出解析成功日志
                log(Operation.PARSE, url.toString(), Mark.SUCCESS);
                break;
            case ParserResult.ERROR_PARSE_FAILED:
                log(Operation.PARSE, url.toString(), Mark.ERROR);
                break;
            case ParserResult.ERROR_RELOCATION:
                log(Operation.PARSE, url.toString(), Mark.ERROR);
                urlPool.addURL(new CrawledURL(result.getLocation(), deep));
                break;
            case ParserResult.ERROR_CHARSET:
                throw new WrongCharsetException("user wrong charset", Charset.forName(charset));
            default:
                break;
        }
    }

    //判断URL是否禁止抓取
    private boolean isDisallow(String url){
        Set<String> disallowSet = manager.getDisallow(url);
        for(String disallow : disallowSet){
            if(url.contains(disallow)){
                return true;
            }
        }
        return false;
    }

    //输出日志
    private void log(Operation operation, String url, Mark mark){
        String logStr = LogUtil.toLogString(userAgent, operation, url, mark);
        System.out.println(logStr);
        if(recorder != null){
            recorder.recordToFile(logStr);
        }
    }

}
