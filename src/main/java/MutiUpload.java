import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MutiUpload {
    private static Vector<String> resultList = new Vector<String>();
    //private static String tracker_ip = "192.168.4.189/";
    private static String tracker_ip = "";

    private <T> List<List<T>> splitAry(T[] ary, int count) {
        int subSize = ary.length % count == 0 ? ary.length / count: ary.length / count + 1;

        List<List<T>> subAryList = new ArrayList<List<T>>();

        for (int i = 0; i < count; i++) {
            int index = i * subSize;
            List<T> list = new ArrayList<T>();
            int j = 0;
            while (j < subSize && index < ary.length) {
                list.add(ary[index++]);
                j++;
            }
            subAryList.add(list);
        }

        return subAryList;
    }
     private void uploadFile(List<String> imageList, FastDFSTool fastDFSTool, String imagePath, Vector<String> resultList){

         /*for(String image: imageList){
             String fdfsFilePath = fastDFSTool.uploadFile(new StringBuffer().append(imagePath).append(image).toString());
             String timestamp = image.substring(0,image.length()-4);
             resultList.add(new StringBuffer(timestamp).append(",").append(fdfsFilePath).toString());
             //System.out.println("fastDFSTool.deleteFile(\"" + new StringBuffer(fdfsFilePath) + "\");");
             //System.out.println("timestamp = [" + timestamp + "]");
         }*/
         List<String> lineList = fastDFSTool.uploadFile(imagePath,imageList);
         resultList.addAll(lineList);

     }

    public static void main(String[] args) {
        final MutiUpload mutiUpload = new MutiUpload();

        int threadCount = 10;
        String localPath = "D:\\cicv\\BBB new data\\";
        //String localPath = "D:\\cicv\\test data\\";

        File localDir = new File(localPath);
        String[] fileList = localDir.list();
        final FastDFSTool fastDFSTool = new FastDFSTool();

        StringBuffer resultPath = new StringBuffer(localPath);
        File resultFile;
        FileWriter resultWritter = null;
        StringBuffer imagePath = new StringBuffer(localPath);
        File imageDir;
        String[] imageList;
        List<List<String>> subImageList = null;

        /*String[] fileList1 = new String[1];
        fileList1[0] = "haidian_2018-08-07-11-15-01_2";*/
        assert fileList != null;
        //for(String file: fileList1){
        for(String file: fileList){

            System.out.println("dir = [" + file + "]");
            resultFile =new File(resultPath.append(file).append("\\imageMarkedWebpList.csv").toString());
            try {
                if(!resultFile.exists()){
                    resultFile.createNewFile();
                }
                resultWritter = new FileWriter(resultFile);
                imagePath.append(file);
                imagePath.append("\\webp_image_marked\\");
                imageDir = new File(imagePath.toString());
                imageList = imageDir.list();

                assert imageList != null;
                subImageList = mutiUpload.splitAry(imageList,threadCount);
                ExecutorService fixedThreadPool = Executors.newFixedThreadPool(threadCount);
                final CountDownLatch latch = new CountDownLatch(threadCount);
                for (int i = 0; i < 10; i++) {
                    final int index = i;
                    final List<String> pathList = subImageList.get(index);
                    final String imgPath = imagePath.toString();
                    fixedThreadPool.execute(() -> {
                        mutiUpload.uploadFile(pathList,fastDFSTool,imgPath,resultList);
                        latch.countDown();
                    });
                }
                latch.await();
                Collections.sort(resultList);
                //System.out.println("resultList = [" + resultList.size() + "]");
                for(String line:resultList){
                    //System.out.println("line = [" + line + "]");
                    resultWritter.write(line+"\n");
                }
                resultWritter.flush();
                resultWritter.close();
            }catch (IOException | InterruptedException e){
                e.printStackTrace();
            }
            resultList.removeAllElements();
            resultPath = new StringBuffer(localPath);
            imagePath = new StringBuffer(localPath);

        }
        System.exit(0);

    }
}

