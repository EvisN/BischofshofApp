package lp.german.bischofshofpresenter.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import lp.german.bischofshofpresenter.app.util.ProjektItem;


public class GespeichertePraesentationenActivity extends Activity{
    private ListView mDrawerList;
    private View mLinearLayout;
    private ImageView mFrame;
    private File[] files;
    private ArrayList<String> filePaths;

    private String mMarke;
    private String selectedPath = "";
    private boolean[] selected;
    private boolean updateTaskFinished = false;

    private ArrayList<ProjektItem> projektItems;
    private ListView projektListe;
    private ProjectListAdapter adapter;
    private ArrayList<String> gewaehlteProjekte;
    private int result = RESULT_CANCELED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_projekt_bearbeiten);

        setupUI();

        //Prüft welche Marke gewählt wurde und stellt Design ein
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mMarke = sharedPref.getString(SettingsActivity.KEY_PREF_MARKE, "");

        if (mMarke.equals("pref_bischofshof")) {
            setDesignBischofshof();
        } else {
            setDesignWeltenburger();
        }

        //Liest die Projekte und deren Pfad
        filePaths = FileUtilities.getAbsolutePathsFromFolder(FileUtilities.PFAD_PROJEKTE);
        selected = new boolean[filePaths.size()];
        for (boolean b : selected) {
            b = false;
        }

        addItemsToContainer(FileUtilities.PFAD_PROJEKTE);
    }

    private void setDesignWeltenburger() {
        mFrame.setImageResource(R.drawable.frame_screen_wb);
        mLinearLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
    }

    private void setDesignBischofshof() {
        mFrame.setImageResource(R.drawable.frame_screen);
        mLinearLayout.setBackgroundResource(R.drawable.skyline);
    }

    private void setupUI() {
        projektListe = (ListView) findViewById(R.id.projekt_list);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        mFrame = (ImageView) findViewById(R.id.frame_screen);
        mLinearLayout = findViewById(R.id.container);
        gewaehlteProjekte = new ArrayList<String>();

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals("Bearbeiten")) {
                    Intent i = new Intent(getApplicationContext(), NeuesPraesentationActivity.class);
                    i.putExtra("projectPath", selectedPath);
                    startActivity(i);
                    return;
                }
                if (parent.getItemAtPosition(position).toString().equals("Umbenennen")) {
                    updateTaskFinished = false;
                    Toast t = Toast.makeText(getApplicationContext(), "Umbenennen", Toast.LENGTH_SHORT);
                    t.show();
                    callRenameDialog();
                    return;
                }
                if (parent.getItemAtPosition(position).toString().equals("Löschen")) {
                    updateTaskFinished = false;
                    callDeleteDialog();
                    return;
                }
                if (parent.getItemAtPosition(position).toString().equals("Versenden")) {

                    return;
                }
                if (parent.getItemAtPosition(position).toString().equals("Präsentieren")) {
                    File dir = new File(selectedPath);
                    files = dir.listFiles();
                    gewaehlteProjekte.add(selectedPath);

                    new updatePresentationFolder().execute();
                }
            }
        });
    }

    private void callDeleteDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Löschen");
        alert.setMessage("Möchten sie das Projekt wirklich löschen?");

        alert.setNegativeButton("Löschen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FileUtilities.deleteProject(selectedPath);
                adapter.notifyDataSetChanged();
                result = RESULT_OK;
                Toast toast = Toast.makeText(getApplicationContext(), "Erfolgreich gelöscht", Toast.LENGTH_SHORT);
                toast.show();
                addItemsToContainer(FileUtilities.PFAD_PROJEKTE);
            }
        });

        alert.setPositiveButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.show();
    }

    private void callRenameDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Umbenennen");
        alert.setMessage("Neuen Namen eingeben");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                File file = new File(selectedPath);
                File fileNew = new File(FileUtilities.PFAD_PROJEKTE + "/" + value);
                Log.d("RENAME WAS: ", String.valueOf(file.renameTo(fileNew)));
                addItemsToContainer(FileUtilities.PFAD_PROJEKTE);
                result = RESULT_OK;
                Toast toast = Toast.makeText(getApplicationContext(), "Erfolgreich umbenannt", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();

    }

    //Fängt Backbutton ab und finished dann die Activity nachdem die gewählten Projekte an den Intent angehängt wurden
    @Override
    public void onBackPressed() {

            Intent returnIntent = new Intent();
            returnIntent.putExtra("chosenProjects", getSelectedPaths());
            setResult(result, returnIntent);
        while(true) {
            if (updateTaskFinished){
                finish();
            }
        }
    }

    //Gibt die im Drawer gewählten Pfade zurück
    private ArrayList<String> getSelectedPaths() {
        ArrayList<String> paths = new ArrayList<String>();
        for (int i = 0; i < selected.length; i++) {
            if (selected[i]) {
                paths.add(FileUtilities.PFAD_PROJEKTE + "/" + filePaths.get(i));
            }
        }
        Collections.reverse(paths);
        return paths;
    }

    //Füllt Container mit den Dateien des ausgewählten Ordners
    private void addItemsToContainer(String path) {
        /*
        File[] files = FileUtilities.getAllFilesFromPath(path);

        ViewGroup group = (ViewGroup) mLinearLayout;
        group.removeAllViews();

        projektItems = new ArrayList<ProjektItem>();
        for(int i = 0; i<files.length; i++)
        {
            final int index = i;
            Log.d("FILE:",files[i].getName());
            final File currentFile = files[i];
            String fileName = currentFile.getName();
            LayoutInflater vi = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.file_template, null);

            TextView textView = (TextView)v.findViewById(R.id.file_template_text);
            if(fileName.length()>10) {
                textView.setText(fileName.substring(0, 12) + "...");
            }else {
                textView.setText(fileName.substring(0));
            }

            ImageView imageView = (ImageView)v.findViewById(R.id.file_template_img);

            imageView.setImageResource(FOLDER_ICON);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedFile = currentFile;
                    selectedPath = currentFile.getAbsolutePath();
                    v.setBackgroundResource(       R.drawable.list_item_bg_pressed);
                    selected[index] = !selected[index];
                }
            });

            mLinearLayout = findViewById(R.id.container);
            group.addView(v);
            projektItems.add(new ProjektItem(fileName, currentFile.getAbsolutePath()));
        }*/
        File[] files = FileUtilities.getAllFilesFromPath(path);
        projektItems = new ArrayList<ProjektItem>();
        for (File f : files) {
            projektItems.add(new ProjektItem(f.getName(), f.getAbsolutePath()));
        }

        adapter = new ProjectListAdapter(this, projektItems);
        projektListe.setAdapter(adapter);
        projektListe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProjektItem projektItem = projektItems.get(position);
                selectedPath = projektItem.getAbsolutePath();
                selected[position] = !selected[position];
                view.setSelected(!view.isSelected());
                view.setBackgroundResource(R.drawable.list_item_bg_pressed);
            }
        });

        updateTaskFinished = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.projekt_liste_aktivity, menu);
        return true;
    }


    private class updatePresentationFolder extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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

            if (FileUtilities.getNumberOfFilesFromPath(FileUtilities.PFAD_PRAESENTATION) != 0) {
                Intent i = new Intent(getApplicationContext(), SimpleFileViewActivity.class);
                i.putExtra("filePaths", FileUtilities.getAbsolutePathsFromFolder(selectedPath));
                i.putExtra("file", files[0].getAbsolutePath());
                startActivity(i);
            }
        }
    }

}
