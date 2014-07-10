package lp.german.bischofshofpresenter.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;

import com.joanzapata.pdfview.PDFView;

public class MainActivity extends Activity {

    private ImageView btnStartPresentation, imgLogo, btnEinstellungen, btnNeuePraesentation, btnGespeichertePraesentationen, btnSchnellzugriff, backgroundStartPresentation;
    public static final int PROJEKTE_BEARBEITET = 105;
    private ArrayList<String> gewaehlteProjekte;
    private ProgressDialog dialog;
    private PDFView pdfView;
    private VideoView videoView;
    private File firstFile;
    private TextView txtNoPresentation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUIElements();
        addUIClickListeners();
        setPreview();
    }

    private void setupUIElements(){

        imgLogo = (ImageView)findViewById(R.id.img_logo);
        backgroundStartPresentation = (ImageView)findViewById(R.id.background_start_presentation);

        //Navigationsbuttons
        btnStartPresentation = (ImageView)findViewById(R.id.btn_start_presentation);
        btnEinstellungen = (ImageView)findViewById(R.id.btn_einstellungen);
        btnNeuePraesentation = (ImageView)findViewById(R.id.btn_new_presentation);
        btnGespeichertePraesentationen = (ImageView)findViewById(R.id.btn_saved_presentations);
        btnSchnellzugriff = (ImageView)findViewById(R.id.btn_schnellzugriff);

        pdfView = (PDFView)findViewById(R.id.pdf_view);
        videoView = (VideoView)findViewById(R.id.videoview);

        txtNoPresentation = (TextView)findViewById(R.id.no_presentation);
    }

    private void addUIClickListeners(){

        btnEinstellungen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(i);
            }
        });

        btnNeuePraesentation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), NeuesPraesentationActivity.class);
                startActivity(i);
            }
        });

        btnSchnellzugriff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),SchnellzugriffActivity.class);
                startActivity(i);
            }
        });

        btnGespeichertePraesentationen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),GespeichertePraesentationenActivity.class);
                startActivityForResult(i, PROJEKTE_BEARBEITET);
            }
        });
    }

    //Wird aufgerufen wenn die aktuelle Präsentation geändert wurde
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case PROJEKTE_BEARBEITET:
                gewaehlteProjekte = (ArrayList<String>)data.getExtras().get("chosenProjects");
                //Startbutton sperren solang nicht alles kopiert ist
                btnStartPresentation.setOnClickListener(null);
                //Aktualisieren von tempCurrent
                new updatePresentationFolder(this).execute();
                break;
            default:
                break;
        }
    }

    //Aktualisieren von tempCurrent, erst löschen des Ordners und dann kopieren der gewählten Projekte
    private class updatePresentationFolder extends AsyncTask<String, Void, String>{
        private Context context;

        public updatePresentationFolder(Context ctx){
            context = ctx;
            dialog = new ProgressDialog(context);
            dialog.setMessage("Updating Current Presentation...");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }
        @Override
        protected String doInBackground(String... args) {
            FileUtilities.emptyTempFolder();
            FileUtilities.addFilesToPresentationTemp(gewaehlteProjekte);
            return "";
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(dialog!=null) {
                dialog.dismiss();
                if(FileUtilities.getNumberOfFilesFromPath(FileUtilities.PFAD_PRAESENTATION)!=0) {
                    btnStartPresentation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pdfView.recycle();
                            Intent i = new Intent(getApplicationContext(), SimpleFileViewActivity.class);
                            i.putExtra("file",firstFile.getAbsolutePath());
                            startActivity(i);
                            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                        }
                    });
                }
            }
        }
    }

    //On Resume immer prüfen ob die Einstellungen wegem dem Design geändert wurden
    @Override
    protected void onResume(){
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String gewaehlteMarke = sharedPref.getString(SettingsActivity.KEY_PREF_MARKE, "");

        if(gewaehlteMarke.equals("pref_bischofshof")){
            setLayoutBischofshof();
        }else{
            setLayoutWeltenburger();
        }

        setPreview();
    }

    @Override
    protected void onPause(){
        super.onPause();
        pdfView.recycle();
    }

    private void setLayoutBischofshof(){
        imgLogo.setImageResource(R.drawable.logo_bischofshof);
        btnStartPresentation.setImageResource(R.drawable.btn_frame);
        btnStartPresentation.invalidate();
        btnSchnellzugriff.setImageResource(R.drawable.btn_schnellzugriff_selector);
        btnNeuePraesentation.setImageResource(R.drawable.btn_neue_praesentation_selector);
        btnGespeichertePraesentationen.setImageResource(R.drawable.btn_gespeicherte_praesentationen_selector);
        btnEinstellungen.setImageResource(R.drawable.btn_einstellungen_selector);
        backgroundStartPresentation.setBackgroundResource(R.drawable.skyline);
    }

    private void setLayoutWeltenburger(){
        imgLogo.setImageResource(R.drawable.logo_weltenburger);
        btnStartPresentation.setImageResource(R.drawable.btn_frame_wb);
        btnStartPresentation.invalidate();
        btnSchnellzugriff.setImageResource(R.drawable.btn_schnellzugriff_wb_selector);
        btnNeuePraesentation.setImageResource(R.drawable.btn_neue_praesentation_wb_selector);
        btnGespeichertePraesentationen.setImageResource(R.drawable.btn_gespeicherte_praesentationen_wb_selector);
        btnEinstellungen.setImageResource(R.drawable.btn_einstellungen_selector_wb);
        backgroundStartPresentation.setBackgroundColor(Color.WHITE);
    }

    private void setPreview(){
        try {
            File directory = new File(gewaehlteProjekte.get(0));
            File[] files = directory.listFiles();
            for(File file: files) {
                if(file.getName().matches(".*[1]\\.(pdf|mov|mp4)")) {
                    firstFile = file;
                }
            }

            if(firstFile==null){
                firstFile = files[0];
            }

            if (FileUtilities.getFileExtension(firstFile.getName()).equals("pdf")) {
                videoView.setVisibility(View.GONE);
                pdfView.setVisibility(View.VISIBLE);
                pdfView.fromFile(firstFile).pages(0).load();
                txtNoPresentation.setVisibility(View.GONE);
            } else {
                pdfView.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);
                Log.d("PREVIEW", "Set Preview to Video");
                try {
                    VideoView mVideoView = (VideoView) findViewById(R.id.videoview);
                    mVideoView.setVisibility(View.VISIBLE);
                    Uri uri = Uri.parse(firstFile.getAbsolutePath());
                    mVideoView.setVideoURI(uri);
                    mVideoView.requestFocus();
                } catch (Exception e) {
                    Log.e("Fehler", "Video konnte nicht geladen werden");
                }
                txtNoPresentation.setVisibility(View.GONE);
            }
        }catch (Exception e){
            videoView.setVisibility(View.GONE);
            pdfView.setVisibility(View.GONE);
            txtNoPresentation.setVisibility(View.VISIBLE);
        }
    }
}
