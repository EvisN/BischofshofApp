package lp.german.buissultimo.app;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;


public class BuissUltimo extends Activity {

    private JSONObject jsonTeams, jsonMatches;
    private JSONArray matchArray, teamArray;
    private Hashtable<Integer,Integer> teamIdIndexMapping;
    private ArrayList<Match> matchList;
    private ArrayList<Team> teamList;
    private ListView liste;
    private LigaListAdapter ligaListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buiss_ultimo);


        GetJSONDataFromUrl getJSONDataFromUrl = new GetJSONDataFromUrl();
        getJSONDataFromUrl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"http://liga.buiss-ultimo.de/matches.php", "http://liga.buiss-ultimo.de/teams.php");

    }

    public void createMatchListFromJson(){

        matchList = new ArrayList<Match>();

        //Loopt durch die JSONObjecte im Matcharray und erstellt eine Liste an Matchobjekten
        for(int i = 0 ; i < matchArray.length() ; i++) {
            try {
                JSONObject match = matchArray.getJSONObject(i);
                //nehme nur Spiele auf die bereits abgeschlossen sind
                if(match.getBoolean("match_is_finished")) {
                    matchList.add(new Match(match.getBoolean("match_is_finished"),match.getInt("id_team1"),match.getInt("id_team2"),match.getInt("points_team1"),match.getInt("points_team2"),match.getString("name_team1"),match.getString("name_team2")));
                }
            }catch (JSONException e){
                Log.e("JSONException","Beim übertragen der JSONObjekte in die MatchObjekte lief was schief");
            }
        }
    }

    public void createTeamListFromJson(){
        try{
            teamList = new ArrayList<Team>();
            teamIdIndexMapping = new Hashtable<Integer, Integer>();
            for(int i = 0; i<teamArray.length(); i++){
                JSONObject team = teamArray.getJSONObject(i);
                teamList.add(new Team(team.getString("team_icon_url"),team.getString("team_name")));
                //Mappe die nicht durchgehenden TeamIds auf ihr Position in der TeamListe
                teamIdIndexMapping.put(team.getInt("team_id"),i);
            }
        }catch (JSONException e){
            Log.e("JSONException","Beim übertragen der JSONObjekte in die TeamObjekte lief was schief");
        }
    }

    //Erstellt die Tabelle
    public void createTeamTable(){

        Match match;
        int team1Index, team2Index, winnerId;

        //Loope durch alle Matches um Punkte zu berechnen
        for(int i = 0; i<matchList.size(); i++){

            match = matchList.get(i);
            //Ermittle die Position der Teams im Teamarray und den Matchgewinner
            team1Index = teamIdIndexMapping.get(match.getIdTeam1());
            team2Index = teamIdIndexMapping.get(match.getIdTeam2());
            winnerId = match.getWinningTeamId();

            //Bei Gleichstand 1 Punkt für beide
                if(winnerId==666){
                    teamList.get(team1Index).addTeam_points(1);
                    teamList.get(team2Index).addTeam_points(1);
            //Ansonsten 3 Punkte für das Siegerteam
                }else{
                    teamList.get(teamIdIndexMapping.get(winnerId)).addTeam_points(3);
                }

            //Füge Tore und Gegentore der Teamstatistik hinzu
                teamList.get(team1Index).addTeam_goals(match.getPointsTeam1());
                teamList.get(team2Index).addTeam_goals(match.getPointsTeam2());
                teamList.get(team1Index).addEnemy_goals(match.getPointsTeam2());
                teamList.get(team2Index).addEnemy_goals(match.getPointsTeam1());
        }

        //Sortiert die Liste
        Collections.sort(teamList);

        //Packe die errechnete Tabelle in die ListView
        liste = (ListView)findViewById(R.id.bundesliga_liste);
        ligaListAdapter = new LigaListAdapter(getApplicationContext(), R.layout.row, teamList);
        liste.setAdapter(ligaListAdapter);

    }

    //AsyncTask zum holen der Daten von den Urls
    private class GetJSONDataFromUrl extends AsyncTask<String,Void,String[]>{

        @Override
        protected String[] doInBackground(String... args) {
            String[] result = new String[2];
            URL url;

            for (int i = 0; i < args.length; i++) {


                StringBuilder stringBuilder = new StringBuilder();  //Baut im folgenden den Json-String zusammen
                try {
                    url = new URL(args[i]);

                    URLConnection urlConnection = url.openConnection();                                             //öffne URL-Verbindung
                    BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));  //lesen des Inputstreams der Verbindung
                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                } catch (MalformedURLException e) {
                    Log.e("Error","Ungültige Url übergeben");
                } catch (IOException e) {
                    Log.v("IOException", "Probleme bei der Verbindung bzw. lesen des Inputstreams");
                }

                result[i] = stringBuilder.toString(); //speichern des Strings im Array dass an die onPostExecutefunktion übergeben wird
            }
            return result;
        }

        @Override
        protected void onPostExecute(String[] result) {
            try {
                jsonMatches = new JSONObject(result[0]);
                jsonTeams = new JSONObject(result[1]);
                matchArray = jsonMatches.getJSONArray("matchdata");
                teamArray = jsonTeams.getJSONArray("team");

                createMatchListFromJson();
                createTeamListFromJson();
                createTeamTable();

            }catch (JSONException e){
                Log.e("JSONExc", "Wohl schiefgelaufen" + e.getMessage() + e.getLocalizedMessage());
            }
        }
    }
}
