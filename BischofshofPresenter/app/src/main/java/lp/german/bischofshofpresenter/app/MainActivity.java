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
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;

import com.joanzapata.pdfview.PDFView;

public class MainActivity extends Activity{

    private ImageView btnStartPresentation, imgLogo, btnEinstellungen, btnNeuePraesentation, btnGespeichertePraesentationen, btnSchnellzugriff, backgroundStartPresentation, imgNavBackground;
    public static final int PROJEKTE_BEARBEITET = 105;
    private ArrayList<String> gewaehlteProjekte;
    private ProgressDialog dialog;
    private PDFView pdfView;
    private VideoView videoView;
    private File firstFile;
    private TextView txtNoPresentation;
    private Spinner navSpinner;
    private ArrayAdapter navSpinnerAdapter;
    private ArrayList<String> navSpinnerItems;
    private SharedPreferences sharedPref;
    private static Context staticContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        staticContext = this.getApplicationContext();
        setupUIElements();
        addUIClickListeners();
        setPreview();
    }

    private void setupUIElements(){

        imgLogo = (ImageView)findViewById(R.id.img_logo);
        backgroundStartPresentation = (ImageView)findViewById(R.id.background_start_presentation);
        imgNavBackground = (ImageView)findViewById(R.id.nav_background);

        //Navigationsbuttons
        btnStartPresentation = (ImageView)findViewById(R.id.btn_start_presentation);
        btnEinstellungen = (ImageView)findViewById(R.id.btn_einstellungen);
        btnNeuePraesentation = (ImageView)findViewById(R.id.btn_new_presentation);
        btnGespeichertePraesentationen = (ImageView)findViewById(R.id.btn_saved_presentations);
        btnSchnellzugriff = (ImageView)findViewById(R.id.btn_schnellzugriff);

        pdfView = (PDFView)findViewById(R.id.pdf_view);
        videoView = (VideoView)findViewById(R.id.videoview);

        setupSpinner(this);

        txtNoPresentation = (TextView)findViewById(R.id.no_presentation);
    }

    private void setupSpinner(final Context ctx) {
        final Context context = ctx;
        navSpinner = (Spinner)findViewById(R.id.nav_spinner);
        navSpinnerAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, FileUtilities.getProjectNames());
        navSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        navSpinner.setAdapter(navSpinnerAdapter);

        if(sharedPref.contains(SettingsActivity.KEY_PREF_PROJECT)) {
            int selected = Math.min(navSpinnerAdapter.getCount() - 1, sharedPref.getInt(SettingsActivity.KEY_PREF_PROJECT, 0));
            navSpinner.setSelection(selected);
        }

        navSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pdfView.recycle();
                String path = FileUtilities.PFAD_PROJEKTE+"/"+parent.getItemAtPosition(position).toString();
                gewaehlteProjekte = new ArrayList<String>();
                gewaehlteProjekte.add(path);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(SettingsActivity.KEY_PREF_PROJECT, position);
                editor.commit();
                new updatePresentationFolder(context).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
                if(resultCode==RESULT_OK) {
                    gewaehlteProjekte = (ArrayList<String>) data.getExtras().get("chosenProjects");
                    //Startbutton sperren solang nicht alles kopiert ist
                    btnStartPresentation.setOnClickListener(null);
                    //Aktualisieren von tempCurrent
                    new updatePresentationFolder(getApplicationContext()).execute();
                }
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
            dialog = new ProgressDialog(ctx);
            dialog.setMessage("Updating Current Presentation...");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }
        @Override
        protected String doInBackground(String... args) {
            //TODO Kopieren wahrscheinlich nicht notwendig
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
                            File directory = new File(gewaehlteProjekte.get(0));
                            File[] files = directory.listFiles();
                            if(files.length>1){
                                i.putExtra("nextFile", true);
                            }
                            i.putExtra("file",firstFile.getAbsolutePath());
                            i.putExtra("filePaths",FileUtilities.getAbsolutePathsFromFolder(FileUtilities.PFAD_PRAESENTATION));
                            i.putExtra("presentationPath", FileUtilities.PFAD_PROJEKTE+"/"+navSpinner.getSelectedItem().toString());
                            startActivity(i);
                            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                        }
                    });
                    setPreview();
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
           setupSpinner(this);

        setPreview();
    }

    @Override
    protected void onPause(){
        super.onPause();
        pdfView.recycle();
    }

    private void setLayoutBischofshof(){
        imgLogo.setImageResource(R.drawable.logo_bischofshof);
        imgNavBackground.setImageResource(R.drawable.navigation_background);
        btnStartPresentation.setImageResource(R.drawable.btn_frame);
        btnSchnellzugriff.setImageResource(R.drawable.btn_schnellzugriff_selector);
        btnNeuePraesentation.setImageResource(R.drawable.btn_neue_praesentation_selector);
        btnGespeichertePraesentationen.setImageResource(R.drawable.btn_gespeicherte_praesentationen_selector);
        btnEinstellungen.setImageResource(R.drawable.btn_einstellungen_selector);
        backgroundStartPresentation.setBackgroundResource(R.drawable.skyline);
    }

    private void setLayoutWeltenburger(){
        imgLogo.setImageResource(R.drawable.logo_weltenburger);
        imgNavBackground.setImageResource(R.drawable.grey_gradient);
        btnStartPresentation.setImageResource(R.drawable.btn_frame_wb);
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
                if(file.getName().matches("[10001].*")) {
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
