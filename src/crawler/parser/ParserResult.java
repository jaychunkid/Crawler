package crawler.parser;

import java.util.List;

public class ParserResult {

    //结果码
    public final static int RESULT_OK = 0;
    public final static int ERROR_NO_FOLLOW = 1;
    public final static int ERROR_PARSE_FAILED = 2;
    public final static int ERROR_RELOCATION = 3;
    public final static int ERROR_CHARSET = 4;

    private int resultCode;
    private List<String> urlList;
    private String location;
    private String charset;

    ParserResult(int resultCode, List<String> urlList, String location, String charset){
        this.resultCode = resultCode;
        this.urlList = urlList;
        this.location = location;
        this.charset = charset;
    }

    public int getResultCode(){
        return resultCode;
    }

    public List<String> getUrlList(){
        return urlList;
    }

    public String getLocation() {
        return location;
    }

    public String getCharset() {
        return charset;
    }

}
