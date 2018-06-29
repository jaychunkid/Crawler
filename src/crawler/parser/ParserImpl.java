package crawler.parser;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//抓取内容解析
public class ParserImpl implements Parser {

    private URL base;                 //解析内容的URL
    private List<String> urlList;     //解析得到的URL
    private int result;               //解析结果
    private String charset;           //解析用的字符集
    private String location;          //重定向url

    public ParserImpl(){
        init(null, null);
    }

    public void parse(Reader inputReader, URL base, String charset) {
        init(base, charset);
        try {
            new ParserDelegator().parse(inputReader, new ParserImpl.ParserCallback(), true);
        } catch (IOException e){
            result = ParserResult.ERROR_PARSE_FAILED;
        }
    }

    public ParserResult getResult() {
        return new ParserResult(result, urlList, location, charset);
    }

    //初始化解析结果
    private void init(URL base, String charset){
        this.base = base;
        if(urlList == null) {
            urlList = new ArrayList<>();
        } else {
            urlList.clear();
        }
        if(charset == null){
            this.charset = "utf-8";
        } else {
            this.charset = charset;
        }
        result = ParserResult.RESULT_OK;
    }

    //实现解析网页内容，提取链接
    private class ParserCallback extends HTMLEditorKit.ParserCallback {

        private boolean followAllowed = true;     //该网页上的链接是否可以跟踪
        private boolean parse = true;          //是否需要解析网页

        @Override
        public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
            handleSimpleTag(t, a, pos);
        }

        @Override
        public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos) {
            if (t == HTML.Tag.META) {
                //解析meta标签，检查content中是否声明nofollow或none
                String name = (String) a.getAttribute(HTML.Attribute.NAME);
                if ("robots".equalsIgnoreCase(name)) {
                    String content = (String) a.getAttribute(HTML.Attribute.CONTENT);
                    if (content != null && (content.contains("nofollow") || content.contains("none"))) {
                        //该网页不可跟踪，清空已经解析到的URL，设置错误码
                        ParserImpl.this.urlList.clear();
                        followAllowed = false;
                        ParserImpl.this.result = ParserResult.ERROR_NO_FOLLOW;
                    }
                } else {
                    String httpEquiv = (String) a.getAttribute(HTML.Attribute.HTTPEQUIV);
                    if ("Content-Type".equalsIgnoreCase(httpEquiv)) {
                        String content = (String) a.getAttribute(HTML.Attribute.CONTENT);
                        if (content != null && content.contains("charset")) {
                            String charset = content.substring(content.indexOf("charset=") + "charset=".length(),
                                    content.length());
                            if (!"".equals(charset) && !ParserImpl.this.charset.equalsIgnoreCase(charset)) {
                                ParserImpl.this.charset = charset;
                                ParserImpl.this.result = ParserResult.ERROR_CHARSET;
                                parse = false;
                            }
                        }
                    } else if ("Refresh".equalsIgnoreCase(httpEquiv)) {
                        String content = (String) a.getAttribute(HTML.Attribute.CONTENT);
                        if (content != null && content.toLowerCase().contains("url=")) {
                            String url = content.substring(content.toLowerCase().indexOf("url=") +
                                    "url=".length(), content.length());
                            if (!"".equals(url)) {
                                try {
                                    url = new URL(base, url).toString();
                                    ParserImpl.this.location = url;
                                    ParserImpl.this.result = ParserResult.ERROR_RELOCATION;
                                    parse = false;
                                } catch (MalformedURLException e){
                                    //JavaScript链接在这里会报错，视为解析成功
                                    ParserImpl.this.result = ParserResult.RESULT_OK;
                                    parse = false;
                                }
                            }
                        }
                    }
                }
            } else if (followAllowed && parse) {
                //尝试获取href内容
                String href = (String) a.getAttribute(HTML.Attribute.HREF);
                //若对应标签为frame，则尝试获取src内容
                if (href == null && t == HTML.Tag.FRAME) {
                    href = (String) a.getAttribute(HTML.Attribute.SRC);
                }
                if (href == null) {
                    return;
                }
                //去除mailto标签链接
                if (href.toLowerCase().startsWith("mailto:")) {
                    return;
                }
                //去除file标签连接
                if (href.toLowerCase().startsWith("file:")) {
                    return;
                }
                //去除链接中的#定位符
                int index = href.indexOf("#");
                if (index != -1) {
                    href = href.substring(0, index);
                }
                //将链接与网页URL合并为规范化URL
                try {
                    URL url = new URL(ParserImpl.this.base, href);
                    ParserImpl.this.urlList.add(url.toString());
                } catch (MalformedURLException e) {
                    //JavaScript链接在这里会报错，不做处理
                }
            }
        }

    }

}
