import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Test {
    public static void main(String[] args) {
        String localPath = "D:\\cicv\\BBB new data\\";
        File localDir = new File(localPath);
        String[] fileList = localDir.list();
        FastDFSTool fastDFSTool = new FastDFSTool();
        String tracker_ip = "192.168.4.189/";

        StringBuffer resultPath = new StringBuffer(localPath);
        File resultFile;
        FileWriter resultWritter = null;
        StringBuffer imagePath = new StringBuffer(localPath);
        File imageDir;
        String[] imageList;
        String fdfsFilePath;
        String timestamp;

        for(String file: fileList){
            System.out.println("dir = [" + file + "]");
            resultFile =new File(resultPath.append(file).append("\\imageList.csv").toString());
            try {
                if(!resultFile.exists()){
                    resultFile.createNewFile();
                }
                resultWritter = new FileWriter(resultFile.getName(),true);
                imagePath.append(file);
                imagePath.append("\\image\\");
                imageDir = new File(imagePath.toString());
                imageList = imageDir.list();
                for(String image: imageList){
                    //此处改为多线程
                    fdfsFilePath = fastDFSTool.uploadFile(new StringBuffer().append(imagePath).append(image).toString());
                    timestamp = image.substring(0,image.length()-4);
                    resultWritter.write(new StringBuffer(timestamp).append(",").append(fdfsFilePath).append("\n").toString());
                    System.out.println("line = [" + new StringBuffer(timestamp).append(",").append(fdfsFilePath).toString() + "]");

                }
                resultWritter.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            resultPath = new StringBuffer(localPath);
            imagePath = new StringBuffer(localPath);

        }

    }
}

