package lp.german.bischofshofpresenter.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import lp.german.slidingmenu.adapter.NavDrawerListAdapter;
import lp.german.slidingmenu.model.NavDrawerItem;


public class SchnellzugriffActivity extends Activity implements PopupMenu.OnMenuItemClickListener {

    private ListView mDrawerList;
    private View mLinearLayout;
    private ImageView mFrame;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    private File clickedFile;

    private ImageView btn_wb, btn_bh;

    private String mMarke;

    private int FOLDER_ICON;

    private int design_int = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_projekt_liste);

        setupUI();
        setupListeners();

        //Design prüfen
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mMarke = sharedPref.getString(SettingsActivity.KEY_PREF_MARKE, "");

        if(mMarke.equals("pref_bischofshof")){
            setDesignBischofshof();
        }else{
            setDesignWeltenburger();
        }
    }

    private void setDesignWeltenburger() {
        mFrame.setImageResource(R.drawable.frame_screen_wb);
        mLinearLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
        mDrawerList.setBackgroundResource(R.drawable.grey_gradient);
        FOLDER_ICON = R.drawable.icon_ordner_wb;
        btn_bh.setBackgroundColor(0xFFAAAAAA);
        btn_wb.setBackgroundColor(0x00AAAAAA);
        addItems(FileUtilities.PFAD_WB);
        addItemsToContainer(FileUtilities.PFAD_WB);
    }

    private void setDesignBischofshof() {
        mFrame.setImageResource(R.drawable.frame_screen);
        mLinearLayout.setBackgroundResource(R.drawable.skyline);
        mDrawerList.setBackgroundResource(R.drawable.navigation_background);
        FOLDER_ICON = R.drawable.icon_ordner_bh;
        btn_bh.setBackgroundColor(0x00AAAAAA);
        btn_wb.setBackgroundColor(0xFFAAAAAA);
        addItems(FileUtilities.PFAD_BH);
        addItemsToContainer(FileUtilities.PFAD_BH);
    }

    private void setupUI() {
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        mFrame = (ImageView)findViewById(R.id.frame_screen);
        mLinearLayout = findViewById(R.id.container);
        btn_bh = (ImageView)findViewById(R.id.btn_bh);
        btn_wb = (ImageView)findViewById(R.id.btn_wb);
    }

    private void setupListeners(){
        btn_bh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMarke = "pref_bischofshof";
                setDesignBischofshof();
                addItemsToContainer(FileUtilities.PFAD_BH);
                addItems(FileUtilities.PFAD_BH);
            }
        });

        btn_wb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMarke = "";
                setDesignWeltenburger();
                addItemsToContainer(FileUtilities.PFAD_WB);
                addItems(FileUtilities.PFAD_WB);
            }
        });
    }


    private void addItems(String path){
        File[] files = FileUtilities.getAllFilesFromPath(path);
        navDrawerItems = new ArrayList<NavDrawerItem>();

        int mCount = 0;
        for(int i = 0; i<files.length; i++) {

            if (files[i].isDirectory()){
                mCount = FileUtilities.getNumberOfFilesFromPath(files[i].getAbsolutePath());
            }

            //Ausschluss vom Projekt und Präsentationsordner und Dateien
            if(!files[i].getName().equals("Projekte")&&!files[i].getName().equals("tempCurrent")&&files[i].isDirectory()) {
                Log.d("Filenames:", files[i].getName());
                if(mCount==0){
                    navDrawerItems.add(new NavDrawerItem(files[i].getName(), files[i].getAbsolutePath()));
                }else{
                    navDrawerItems.add(new NavDrawerItem(files[i].getName(), files[i].getAbsolutePath(), true, String.valueOf(mCount)));
                }
            }

            mCount = 0;
        }


        File file = new File(path);
        if(!file.getName().equals("Bischofshof")&&!file.getName().equals("Weltenburger")) {
            navDrawerItems.add(new NavDrawerItem("Zurück", file.getParentFile().getAbsolutePath()));
        }


        //ListAdapter setzen
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems, mMarke);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());



    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            NavDrawerItem item = (NavDrawerItem)adapter.getItem(position);

            try {
                addItemsToContainer(item.getAbsolutePath());
                addItems(item.getAbsolutePath());
            }catch (Exception e){
                Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    //Hinzufügen der Dateien zum Auswahlfenster
    private void addItemsToContainer(String path){

        File[] files = FileUtilities.getAllFilesFromPath(path);

        ViewGroup group = (ViewGroup) mLinearLayout;
        group.removeAllViews();

        for(int i = 0; i<files.length; i++)
        {
            if (!files[i].getName().equals("tempCurrent")&&!files[i].getName().equals("Projekte")) {
                final File currentFile = files[i];
                String fileName = currentFile.getName();
                LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = vi.inflate(R.layout.file_template, null);

                TextView textView = (TextView) v.findViewById(R.id.file_template_text);

                if (fileName.length() > 10) {
                    textView.setText(fileName.substring(0, 10) + "...");
                } else {
                    textView.setText(fileName);
                }

                ImageView imageView = (ImageView) v.findViewById(R.id.file_template_img);


                if (FileUtilities.getFileExtension(fileName).equals("pdf")) {
                    imageView.setImageResource(R.drawable.pdf2);
                } else {
                    if (!files[i].isDirectory()) {
                        imageView.setImageResource(R.drawable.video);
                    } else {
                       imageView.setImageResource(FOLDER_ICON);
                    }
                }

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!currentFile.isDirectory()) {
                            clickedFile = currentFile;
                            showPopup(v);
                        } else {
                            addItems(currentFile.getAbsolutePath());
                            addItemsToContainer(currentFile.getAbsolutePath());
                        }
                    }
                });

                mLinearLayout = findViewById(R.id.container);
                group.addView(v);
            }
        }
        File file = new File(path);
        if(!file.getName().equals("Bischofshof")&&!file.getName().equals("Weltenburger")) {
            final File currentFile = files[0].getParentFile().getParentFile();
            String fileName = "...";
            LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.file_template, null);

            TextView textView = (TextView) v.findViewById(R.id.file_template_text);
            textView.setText(fileName);

            ImageView imageView = (ImageView) v.findViewById(R.id.file_template_img);

            imageView.setImageResource(R.drawable.previous);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addItemsToContainer(currentFile.getAbsolutePath());
                    addItems(currentFile.getAbsolutePath());
                }
            });
            group.addView(v, 0);
        }
    }


    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.context_menu, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.open:
                openFile(clickedFile);
                return true;
            /*case R.id.mail:
                //TODO Mailversenden
                return true;*/
            default:
                return false;
        }
    }

    //Öffnet ein File auf Click
    private void openFile(File file){
        //PDFActivity
        Toast toast = Toast.makeText(getApplicationContext(),"Open file: "+file.getAbsolutePath(),Toast.LENGTH_SHORT);
        toast.show();
        Intent i = new Intent(getApplicationContext(), SimpleFileViewActivity.class);
        i.putExtra("file", file.getAbsolutePath());
        i.putExtra("singleFile", true);
        startActivity(i);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
    }
}
