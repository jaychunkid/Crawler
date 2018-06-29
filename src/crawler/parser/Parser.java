package crawler.parser;

import java.io.Reader;
import java.net.URL;

public interface Parser {

    //利用传入的字符流解析内容
    void parse(Reader reader, URL base, String charset);

    //获取解析结果
    ParserResult getResult();

}
