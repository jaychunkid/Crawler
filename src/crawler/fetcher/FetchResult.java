package crawler.fetcher;

import java.io.InputStream;
import java.io.Reader;

public class FetchResult {

    //结果码
    public final static int RESULT_OK = 0;
    public final static int ERROR_CONNECT = 1;
    public final static int ERROR_FETCH = 2;
    public final static int ERROR_CONTENT_TYPE = 3;
    public final static int ERROR_RELOCATE = 4;

    private int resultCode;
    private InputStream inputStream;
    private String charset;
    private String location;

    FetchResult(int resultCode, InputStream inputStream, String charset){
        this.resultCode = resultCode;
        this.inputStream = inputStream;
        this.charset = charset;
    }

    FetchResult(int resultCode, String location){
        this.resultCode = resultCode;
        this.location = location;
    }

    public int getResultCode(){
        return resultCode;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getCharset() {
        return charset;
    }

    public String getLocation() {
        return location;
    }
}
