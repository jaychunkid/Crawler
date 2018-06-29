package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//实现文件记录操作
public class Recorder {

    private File file;

    public Recorder(String filePath){
        file = new File(filePath);
        //若指定文件已存在，则删除文件
        if(file.exists() && file.isFile()){
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    //向文件中写入一行字符串
    public void recordToFile(String string){
        try {
            FileWriter writer = new FileWriter(file, true);
            writer.append(string);
            writer.append("\r\n");
            writer.flush();
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
