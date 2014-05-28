package lp.german.bischofshofpresenter.app;

/**
 * Created by paullichtenberger on 28.05.14.
 */
public class FileUtilities {

    public FileUtilities(){

    }

    public String getFileExtension(String fileName){
        String filenameArray[] = fileName.split("\\.");
        return filenameArray[filenameArray.length-1];
    }

}
