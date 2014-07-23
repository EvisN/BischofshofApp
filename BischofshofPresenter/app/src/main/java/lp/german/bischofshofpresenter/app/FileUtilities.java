package lp.german.bischofshofpresenter.app;

import android.os.Environment;
import android.util.Log;

import com.joanzapata.pdfview.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by paullichtenberger on 28.05.14.
 *
 * Zuständig für alle Dateioperationen
 *
 */
public class FileUtilities {

    public static String PFAD_ROOT = Environment.getExternalStorageDirectory() + "/BischofsHofApp/";
    public static String PFAD_WB = Environment.getExternalStorageDirectory() + "/BischofsHofApp/Weltenburger/";
    public static String PFAD_BH = Environment.getExternalStorageDirectory() + "/BischofsHofApp/Bischofshof/";
    public static String PFAD_PROJEKTE = Environment.getExternalStorageDirectory() + "/BischofsHofApp/Projekte";
    public static String PFAD_PRAESENTATION = Environment.getExternalStorageDirectory() + "/BischofsHofApp/tempCurrent";

    //Gibt Dateiendung zurück
    public static String getFileExtension(String fileName) {
        String filenameArray[] = fileName.split("\\.");
        return filenameArray[filenameArray.length - 1];
    }

    //Gibt Absolute Pfade aller Dateien in Ordner zurück
    public static ArrayList<String> getAbsolutePathsFromFolder(String path) {
        File[] filesList = getAllFilesFromPath(path);

        ArrayList<String> filePaths = new ArrayList<String>();

        if (filesList != null) {
            for (int i = 0; i < filesList.length; i++) {
                filePaths.add(filesList[i].getName());
            }
        }

        Collections.sort(filePaths,String.CASE_INSENSITIVE_ORDER);

        return filePaths;
    }

    //Gibt alle Files von einem Pfad zurück
    public static File[] getAllFilesFromPath(String path) {
        File dir = new File(path);
        return dir.listFiles();
    }

    //Gibt Anzahl der Dateien unter bestimmten Pfad zurück
    public static int getNumberOfFilesFromPath(String path) {
        File dir = new File(path);
        return dir.listFiles().length;
    }

    public static ArrayList<String> getProjectNames(){
        File[] folders = getAllFilesFromPath(FileUtilities.PFAD_PROJEKTE);

        ArrayList<String> projectNames = new ArrayList<String>();

        if(folders != null){
            for(File file: folders){
                projectNames.add(file.getName());
            }
        }
        return projectNames;
    }

    //Löscht den temporären Ordner der aktuellen Präsentation
    public static void emptyTempFolder() {
        File directory = new File(PFAD_PRAESENTATION);

        File[] files = directory.listFiles();
        for (File file : files) {
            File fileToDelete = new File(file.getAbsolutePath());
            if (!fileToDelete.delete()) {
                Log.e("Fehler", "Datei konnte nicht gelöscht werden");
            }
        }
    }

    public static void deleteProject(String path){
        File folderToDelete = new File(path);
        File[] files = folderToDelete.listFiles();
        for(File file: files){
            file.delete();
        }
        folderToDelete.delete();
        Log.d("Delete:", path+" gelöscht");
    }

    //Fügt anhand der gewählten Präsentationen die Dateien dem temporären Ordner hinzu
    public static void addFilesToPresentationTemp(ArrayList<String> paths) {

        for (String path : paths) {
            File directory = new File(path);

            File[] files = directory.listFiles();

             for (int i= 0; i<files.length;i++) {
                File file = files[i];
                File zielFile = new File(PFAD_PRAESENTATION + "/"+String.valueOf(i)+ file.getName());
                Log.d("FILEVERGLIECH", file.getName() + "  "+zielFile.getName());
                copyFilesFromPathToPath(file, zielFile);
            }

        }
    }

    //Kopiert Datei von x nach y
    public static void copyFilesFromPathToPath(File sourceLocation, File targetLocation) {
        Log.d("COPY", "FROM "+ sourceLocation.getAbsolutePath() + "TO " + targetLocation.getAbsolutePath());
        try {

            if (sourceLocation.isDirectory()) {
                if (!targetLocation.exists() && !targetLocation.mkdirs()) {
                    throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
                }

                String[] children = sourceLocation.list();
                for (int i = 0; i < children.length; i++) {
                    copyFilesFromPathToPath(new File(sourceLocation, children[i]),
                            new File(targetLocation, children[i]));
                }
            } else {

                //Prüft ob Ordner vorhanden
                File directory = targetLocation.getParentFile();
                if (directory != null && !directory.exists() && !directory.mkdirs()) {
                    throw new IOException("Cannot create dir " + directory.getAbsolutePath());
                }

                InputStream in = new FileInputStream(sourceLocation);
                OutputStream out = new FileOutputStream(targetLocation);

                //Kopiert Byteweiße die Dateien
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            }
        } catch (IOException e) {
            Log.e("Fehler",e.getMessage());
        }
    }
}
