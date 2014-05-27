package lp.german.buissultimo.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by paullichtenberger on 09.05.14.
 */
public class LigaListAdapter extends ArrayAdapter<Team> {

    private ArrayList<Team> teams;
    private View view;

    public LigaListAdapter(Context context, int textViewResourceId, ArrayList<Team> teams){
        super(context, textViewResourceId, teams);
        this.teams = teams;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        view = convertView;

        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row, null);
        }

        Team team = teams.get(position);

        if(team != null){
            TextView name = (TextView)view.findViewById(R.id.row_name);
            TextView tore = (TextView)view.findViewById(R.id.row_torverhaeltnis);
            TextView platz = (TextView)view.findViewById(R.id.row_platz);
            ImageView bild = (ImageView)view.findViewById(R.id.row_image);

            //Kodierung der Namen anpassen aufgrund der Umlaute
            try {
                name.setText(new String(team.getTeam_name().getBytes("ISO-8859-1"), "UTF-8"));
            }catch (UnsupportedEncodingException e) {
                Log.e("UnsupportedEncoding","Das Encoding wird nicht unterstützt");
            }

            tore.setText(String.valueOf(team.getTeam_goals()) + ":" + String.valueOf(team.getEnemy_goals()));
            platz.setText(String.valueOf(team.getTeam_points()));
            bild.setImageBitmap(team.getBild());


        }

        return view;
    }
}
