package lp.german.buissultimo.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Comparator;

/**
 * Created by paullichtenberger on 09.05.14.
 */
public class Team implements Comparable<Team>{


    private String team_name;
    private int team_points, team_goals, enemy_goals;
    private Bitmap bild;


    public Team(String team_icon_url, String team_name){
        this.team_name = team_name;

        //Bild wird asynchron geladen
        GetImageAsyncTask getImageAsyncTask = new GetImageAsyncTask();
        getImageAsyncTask.execute(team_icon_url);
    }

    private class GetImageAsyncTask extends AsyncTask<String,Void,Bitmap> {

        @Override
        protected Bitmap doInBackground(String... args) {

            Bitmap bitmap = downloadImage(args[0]);

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            bild = result;
        }

    }

    //Downloaded das Bild
    private Bitmap downloadImage(String url) {
        Bitmap bitmap = null;
        InputStream stream = null;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 1;

        try {
            //Inputstream der übergebenen URL wird geholt und als Bitmap dekodiert
            stream = getHttpConnection(url);
            bitmap = BitmapFactory.
                    decodeStream(stream, null, bmOptions);
            stream.close();
        } catch (IOException e) {
            Log.v("IOExeption","Downloaden eines Bildes fehlgeschlagen");
        }
        return bitmap;
    }

    //Gibt den InputStream einer URL zurück
    private InputStream getHttpConnection(String urlString) {
        InputStream stream = null;

        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                stream = httpConnection.getInputStream();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream;
    }

    public Bitmap getBild() {
        return bild;
    }

    public int getEnemy_goals() {
        return enemy_goals;
    }

    public String getTeam_name() {
        return team_name;
    }

    public int getTeam_points() {
        return team_points;
    }

    public int getTeam_goals() {
        return team_goals;
    }

    public void addTeam_points(int team_points) {
        this.team_points += team_points;
    }

    public void addTeam_goals(int team_goals) {
        this.team_goals += team_goals;
    }

    public void addEnemy_goals(int enemy_goals) {
        this.enemy_goals += enemy_goals;
    }

    //Beschreibt die Regeln nach denen sortiert werden soll
    @Override
    public int compareTo(Team t) {
        //Wenn kein Gleichstand an Punkten herrscht, sortiere das mit den höheren Punkten vor dem anderen ein
        if(this.team_points!=t.team_points) {
            return t.team_points - this.team_points;
        }else {
            //Bei Gleichstand bevorzuge Team mit größerem Torverhältnis
            if ((this.team_goals - this.enemy_goals) != (t.team_goals - t.enemy_goals)){
                return (t.team_goals - t.enemy_goals) - (this.team_goals - this.enemy_goals);
            //Wenn auch die Differenz gleich ist, bevorzuge Team mit mehr Toren
            }else{
                return t.team_goals-this.team_goals;
            }
        }
    }
}
