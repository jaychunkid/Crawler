package util;

import java.text.SimpleDateFormat;
import java.util.Date;

//日志工具类
public class LogUtil {

    //将传入的操作内容（用户，操作名，URL，操作结果）转换为规范化的字符串
    public static String toLogString(String user, Operation operation, String url, Mark mark){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        stringBuilder.append(user);
        stringBuilder.append("][");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        stringBuilder.append(sdf.format(new Date()));
        stringBuilder.append("][");
        switch (operation) {
            case CONNECT:
                stringBuilder.append("connecting");
                break;
            case FETCH:
                stringBuilder.append("fetching");
                break;
            case PARSE:
                stringBuilder.append("parsing");
                break;
            default:
                break;
        }
        stringBuilder.append("][");
        stringBuilder.append(url);
        stringBuilder.append("][");
        switch (mark) {
            case SUCCESS:
                stringBuilder.append("Success");
                break;
            case ERROR:
                stringBuilder.append("Error");
                break;
            default:
                break;
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

}
