package lp.german.bischofshofpresenter.app;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by paullichtenberger on 28.05.14.
 */
public class FileUtilities {

    public static String getFileExtension(String fileName){
        String filenameArray[] = fileName.split("\\.");
        return filenameArray[filenameArray.length-1];
    }

    public static ArrayList<String> getAbsolutePathsFromFolder(String path){
        File[] filesList = getAllFilesFromPath(path);

        ArrayList<String> filePaths = new ArrayList<String>();

        if(filesList!=null){
            for (int i = 0; i< filesList.length; i++){
                filePaths.add(filesList[i].getName());
            }
        }

        return filePaths;
    }

    public static File[] getAllFilesFromPath(String path){
        File dir = new File(path);
        return dir.listFiles();
    }

}
