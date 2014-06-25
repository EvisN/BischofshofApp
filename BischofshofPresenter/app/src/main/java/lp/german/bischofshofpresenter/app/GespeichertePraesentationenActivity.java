package lp.german.bischofshofpresenter.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import lp.german.slidingmenu.adapter.NavDrawerListAdapter;
import lp.german.slidingmenu.model.NavDrawerItem;


public class GespeichertePraesentationenActivity extends Activity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private View mLinearLayout;
    private ImageView mFrame;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;


    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    private File[] filesList;
    private ArrayList<String> filePaths;

    private String mMarke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projekt_bearbeiten);

        mTitle = mDrawerTitle = "<< Bitte Projekt wählen";

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        mFrame = (ImageView)findViewById(R.id.frame_screen);
        mLinearLayout = findViewById(R.id.container);

        //Prüft welche Marke gewählt wurde und stellt Design ein
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mMarke = sharedPref.getString(SettingsActivity.KEY_PREF_MARKE, "");

        if(mMarke.equals("pref_bischofshof")){
            mFrame.setImageResource(R.drawable.frame_screen);
            mLinearLayout.setBackgroundResource(R.drawable.skyline);
        }else{
            mFrame.setImageResource(R.drawable.frame_screen_wb);
            mLinearLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
        }

        //Liest die Projekte und deren Pfad
        filesList = FileUtilities.getAllFilesFromPath(FileUtilities.PFAD_PROJEKTE);
        filePaths = FileUtilities.getAbsolutePathsFromFolder(FileUtilities.PFAD_PROJEKTE);
        addItems(filesList);

    }


    //Erstellt die Items für den Navigationdrawer und fügt sie hinzu
    private void addItems(File[] files){
        navDrawerItems = new ArrayList<NavDrawerItem>();

        //Zeigt die Zahl der enthaltenen Dokumente an
        int mCount = 0;
        for(int i = 0; i<files.length; i++){
            if(!files[i].getName().equals("Projekte")&&!files[i].getName().equals("tempCurrent")&&files[i].isDirectory()) {
                mCount = FileUtilities.getNumberOfFilesFromPath(files[i].getAbsolutePath());
                if (mCount == 0) {
                    navDrawerItems.add(new NavDrawerItem(filePaths.get(i), files[i].getAbsolutePath()));
                } else {
                    navDrawerItems.add(new NavDrawerItem(filePaths.get(i), files[i].getAbsolutePath(), true, String.valueOf(mCount)));
                }
            }
        }

        //Binden an den ListAdapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),navDrawerItems, mMarke);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        //Aktivieren des Action Bar Icons als Toogle Button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_browse,
                R.string.app_name,
                R.string.app_name
        ){
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    //Fängt Backbutton ab und finished dann die Activity nachdem die gewählten Projekte an den Intent angehängt wurden
    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("chosenProjects", getSelectedPaths());
        setResult(MainActivity.PROJEKTE_BEARBEITET, returnIntent);
        finish();
    }

    //Gibt die im Drawer gewählten Pfade zurück
    private ArrayList<String> getSelectedPaths(){
        ArrayList<String> paths = new ArrayList<String>();

        for(int i = 0; i<navDrawerItems.size();i++){
            if(navDrawerItems.get(i).isItemChecked()){
                paths.add(navDrawerItems.get(i).getAbsolutePath());
            }
        }

        return paths;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            NavDrawerItem item = (NavDrawerItem)adapter.getItem(position);

            //Setzt den Titel der Activity auf die gewählte Kategorie
            mTitle = item.getTitle();

            try {
                //Fügt die Dateien aus dem geklickten Ordner in der Ansicht rechts hinzu
                //Im Moment nur zum einzeln öffnen
                addItemsToContainer(item.getAbsolutePath());
            }catch (Exception e){

            }

            //Checkt oder Unchecked das Item
            mDrawerList.setItemChecked(position, item.isItemChecked() ? false : true);
            item.setChecked();

        }
    }

    //Füllt Container mit den Dateien des ausgewählten Ordners
    private void addItemsToContainer(String path){

        File[] files = FileUtilities.getAllFilesFromPath(path);

        ViewGroup group = (ViewGroup) mLinearLayout;
        group.removeAllViews();

        for(int i = 0; i<files.length; i++)
        {
            final File currentFile = files[i];
            String fileName = currentFile.getName();
            LayoutInflater vi = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.file_template, null);

            TextView textView = (TextView)v.findViewById(R.id.file_template_text);
            textView.setText(fileName.substring(0, 10)+"...");

            ImageView imageView = (ImageView)v.findViewById(R.id.file_template_img);

            if(FileUtilities.getFileExtension(fileName).equals("pdf")){
                imageView.setImageResource(R.drawable.pdf2);
            }else {
                imageView.setImageResource(R.drawable.video);
            }

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFile(currentFile);
                }
            });

            mLinearLayout = findViewById(R.id.container);
            group.addView(v);
        }
    }

    //Öffnet die Files auf Click
    private void openFile(File file){
        //PDFActivity
        Toast toast = Toast.makeText(getApplicationContext(),"Open file: "+file.getAbsolutePath(),Toast.LENGTH_SHORT);
        toast.show();
        Intent i = new Intent(getApplicationContext(), SimpleFileViewActivity.class);
        i.putExtra("file", file);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.projekt_liste_aktivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
