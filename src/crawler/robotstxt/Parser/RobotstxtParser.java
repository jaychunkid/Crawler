package crawler.robotstxt.Parser;

import java.util.HashSet;
import java.util.Set;

//解析robots.txt内容
public class RobotstxtParser {

    private Set<String> disallow = new HashSet<>();

    //解析传入字符串的内容
    public void parse(String text, String base, String user){
        init();
        String[] lines = text.split("\r\n");
        for(int i = 0; i < lines.length; ++i){
            //判断该行是否为user-agent域
            if(!lines[i].startsWith("#") && lines[i].toLowerCase().contains("user-agent")){
                String robot = lines[i].substring(lines[i].indexOf(":")+1).trim();
                //判断是否与当前用户名匹配
                if(robot.equals("*") || robot.equals(user)){
                    i+=1;
                    //获取匹配的user-agent域下的disallow域
                    while(i < lines.length && lines[i].toLowerCase().contains("disallow")){
                        String directory = lines[i].substring(lines[i].indexOf(":")+1).trim();
                        //判断域指定内容是否为空
                        if(!"".equals(directory)) {
                            if(directory.contains("*")){
                                //对于包含通配符的域，简单地将整个上级目录视为域内容
                                int index1 = directory.indexOf('*');
                                int index2 = directory.substring(0, index1).lastIndexOf('/');
                                if(index2 != 0 && index2 != -1) {
                                    directory = directory.substring(0, index2 + 1);
                                    disallow.add(base + directory);
                                } else {
                                    disallow.add(base);
                                }
                            } else {
                                disallow.add(base + directory);
                            }
                        }
                        ++i;
                    }
                }
            }
        }
    }

    //获取解析到的URL集合
    public Set<String> getDisallow(){
        return disallow;
    }

    //初始化解析内容
    private void init(){
        disallow.clear();
    }

}
