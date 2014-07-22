package lp.german.bischofshofpresenter.app;

/**
 * TODO
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

import lp.german.slidingmenu.model.NavDrawerItem;

public class NeuesPraesentationActivity extends Activity {

    //Views
    private ImageButton btn_speichern, btn_scroll_left, btn_scroll_right, btn_wb, btn_bh, btn_folie_hinzufuegen, btn_folienseiten_hinzufuegen, btn_menu_schliesen;
    private View m_spacer_wb, m_spacer_bh;
    private ScrollView ordner_explorer, datei_explorer, praesentations_explorer;

    private View mLinearLayout;
    private View layoutExplorerFile;
    private int FOLDER_ICON;
    private File clickedFile;
    private ArrayList<NavDrawerItem> navDrawerItems;


    private ArrayList<File> currentPresentation;



    //Menu
    private RelativeLayout menu;

    //Dummy Button zum test des Sidemenues
    private Button dummy_button;

    //Flags
    private boolean BISCHOFSHOF_GEWAEHLT = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neues_projekt);
        setupUI();
        setupClickListeners();


        if(getIntent().getExtras() != null){
            Intent intent = getIntent();
            String path = intent.getStringExtra("projectPath");
            addItemsToContainer(FileUtilities.PFAD_BH);
            File[] fList = FileUtilities.getAllFilesFromPath(path);
            for(int i = 0;i<fList.length;i++){
                addFileToExplorer(fList[i]);
            }

        } else{
            addItemsToContainer(FileUtilities.PFAD_BH);
        }

    }

    private void setupUI() {

        //Buttons
        btn_speichern = (ImageButton) findViewById(R.id.btn_speichern);
        btn_scroll_left = (ImageButton) findViewById(R.id.btn_scroll_left);
        btn_scroll_right = (ImageButton) findViewById(R.id.btn_scroll_right);
        btn_wb = (ImageButton) findViewById(R.id.logo_weltenburger);
        btn_bh = (ImageButton) findViewById(R.id.logo_bh);
        btn_menu_schliesen = (ImageButton) findViewById(R.id.btn_menu_schliesen);
        btn_folie_hinzufuegen = (ImageButton) findViewById(R.id.btn_folie_hinzufuegen);
        btn_folienseiten_hinzufuegen = (ImageButton) findViewById(R.id.btn_folienseiten_hinzufuegen);

        //Menu
        menu = (RelativeLayout) findViewById(R.id.menu);

        //Spacer
        m_spacer_bh = findViewById(R.id.bhSpace);
        m_spacer_wb = findViewById(R.id.wbSpace);

        //Scrollviews
        ordner_explorer = (ScrollView) findViewById(R.id.ordner_struktur);
        datei_explorer = (ScrollView) findViewById(R.id.datei_explorer);
        praesentations_explorer = (ScrollView) findViewById(R.id.praesentations_explorer);

        FOLDER_ICON = R.drawable.icon_ordner_bh;
        mLinearLayout = findViewById(R.id.childFile);
        layoutExplorerFile = findViewById(R.id.explorer_file);
        currentPresentation = new ArrayList<File>();


    }

    private void setupClickListeners() {
        btn_speichern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePresentation();
            }
        });

        btn_scroll_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hier wird die presentation view nach links gescrollt
            }
        });

        btn_scroll_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hier wird die presentation view nach rechts gescrollt
            }
        });

        btn_bh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BISCHOFSHOF_GEWAEHLT = true;
                setBrauerei();
            }
        });

        btn_wb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BISCHOFSHOF_GEWAEHLT = false;
                setBrauerei();
            }
        });

        btn_menu_schliesen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.setVisibility(View.GONE);
            }
        });

        btn_folie_hinzufuegen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fuegeFolieHinzu();
            }
        });

        btn_folienseiten_hinzufuegen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fuegeFolienseitenHinzu();
            }
        });


    }

    private void openSideMenu() {
        menu.setVisibility(View.VISIBLE);
    }

    private void setBrauerei() {
        if (BISCHOFSHOF_GEWAEHLT) {
            m_spacer_wb.setVisibility(View.VISIBLE);
            m_spacer_bh.setVisibility(View.GONE);

            //Hier Ordnerstruktur auf BH setzen
        } else {
            m_spacer_wb.setVisibility(View.GONE);
            m_spacer_bh.setVisibility(View.VISIBLE);

            //Hier Ordnerstruktur auf WB setzen
        }
    }

    private void fuegeFolieHinzu() {
        //Hier die ganze gewählte Folie zur Präsentation hinzufuegen
        currentPresentation.add(clickedFile);
        addFileToExplorer(clickedFile);


    }
    private void addFileToExplorer(File file){

        ViewGroup group = (ViewGroup) layoutExplorerFile;


        String fileName = file.getName();
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.file_template, null);

        TextView textView = (TextView) v.findViewById(R.id.file_template_text);
        textView.setText(fileName);
        ImageView imageView = (ImageView) v.findViewById(R.id.file_template_img);

        if (FileUtilities.getFileExtension(fileName).equals("pdf")) {
            imageView.setImageResource(R.drawable.pdf2);
        } else {
            if (!clickedFile.isDirectory()) {
                imageView.setImageResource(R.drawable.video);
            } else {
                imageView.setImageResource(FOLDER_ICON);
            }
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                removeFromView(view);
            }
        });

        layoutExplorerFile = findViewById(R.id.explorer_file);

        group.addView(v);

    }

    private void fuegeFolienseitenHinzu() {
        //Hier nur Bestimmte Seiten zur Präsentation hinzufuegen
        Intent i = new Intent(getApplicationContext(), SeiteWaehlenActivity.class);
        i.putExtra("file", clickedFile);
        startActivityForResult(i, 1);
    }
    private void removeFromView(View view){
        LinearLayout ll = (LinearLayout)view.getParent();
        TextView tv = (TextView)ll.getChildAt(1);
        String fileName = tv.getText().toString();

        for(int i = 0;i<currentPresentation.size();i++){
            String tempName = currentPresentation.get(i).getName();
            String filePath = currentPresentation.get(i).getAbsolutePath();


            if(tempName.equals(fileName)){
                if(filePath.contains("EDIT_")){
                    File file = new File(filePath);
                    file.delete();
                }
                currentPresentation.remove(i);
            }
        }
        ll.removeAllViews();

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            String fName = intent.getStringExtra("fileName");
            File file = new File(FileUtilities.PFAD_ROOT+"/tempImages/EDIT_"+fName);
            ViewGroup group = (ViewGroup) layoutExplorerFile;
            currentPresentation.add(file);
            String fileName = file.getName();
            LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.file_template, null);
            TextView textView = (TextView) v.findViewById(R.id.file_template_text);

                textView.setText(fileName);


            ImageView imageView = (ImageView) v.findViewById(R.id.file_template_img);
            imageView.setImageResource(R.drawable.pdf2);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    removeFromView(view);
                }
            });

            layoutExplorerFile = findViewById(R.id.explorer_file);
            group.addView(v);

        }
    }

    private void savePresentation() {
        //Hier wird die Präsentation gespeichert
        if(currentPresentation.size()==0){
            Toast.makeText(getApplicationContext(), "Keine Dateien ausgewählt!", Toast.LENGTH_SHORT).show();
        } else{
            saveDialog();
        }

    }

    private void addItemsToContainer(String path) {

        File[] files = FileUtilities.getAllFilesFromPath(path);

        ViewGroup group = (ViewGroup) mLinearLayout;
        group.removeAllViews();

        for (int i = 0; i < files.length; i++) {
            if (!files[i].getName().equals("tempCurrent") && !files[i].getName().equals("Projekte")&& !files[i].getName().equals("tempImages")) {
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
                            openSideMenu();
                        } else {
                            BISCHOFSHOF_GEWAEHLT = currentFile.getAbsolutePath().toString().matches(".*Bischofshof.*");


                            addItems(currentFile.getAbsolutePath());
                            addItemsToContainer(currentFile.getAbsolutePath());
                        }
                    }
                });

                mLinearLayout = findViewById(R.id.childFile);
                group.addView(v);
            }

        }
        if(!files[0].getParentFile().getName().equals("sdcard")&&!files[0].getParentFile().getName().equals("BischofsHofApp")) {
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

    private void addItems(String path) {
        File[] files = FileUtilities.getAllFilesFromPath(path);
        navDrawerItems = new ArrayList<NavDrawerItem>();

        int mCount = 0;
        for (int i = 0; i < files.length; i++) {

            if (files[i].isDirectory()) {
                mCount = FileUtilities.getNumberOfFilesFromPath(files[i].getAbsolutePath());
            }

            //Ausschluss vom Projekt und Präsentationsordner und Dateien
            if (!files[i].getName().equals("Projekte") && !files[i].getName().equals("tempCurrent") && files[i].isDirectory() && !files[i].getName().equals("tempImages")) {

                if (mCount == 0) {
                    navDrawerItems.add(new NavDrawerItem(files[i].getName(), files[i].getAbsolutePath()));
                } else {
                    navDrawerItems.add(new NavDrawerItem(files[i].getName(), files[i].getAbsolutePath(), true, String.valueOf(mCount)));
                }
            }

            mCount = 0;
        }
    }

    //Verwenden damit Inhalt von tempImages gelöscht wird
    //TODO Methode ändern
    public void deleteFilesFromDir(String filePath) {
        File dir = new File(filePath);
        File[] filesInDir =  dir.listFiles();
            for (File child : filesInDir){
                child.delete();
            }

    }

    public void copyDirectory(ArrayList<File> sourceFiles, String newProjectName)
    {
         File destinationFolder = new File(FileUtilities.PFAD_PROJEKTE +"/"+ newProjectName);
        if (!destinationFolder.exists()) {
            destinationFolder.mkdir();
        }


            for (int i=0; i<sourceFiles.size(); i++) {
                File source = sourceFiles.get(i);

                String sourcePath = source.getAbsolutePath();

                String destinationPath = destinationFolder+"/"+source.getName();


                try
                {
                    InputStream in = new FileInputStream(sourcePath);
                    OutputStream out = new FileOutputStream(destinationPath);

                    // Copy the bits from instream to outstream
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }


        }
    private void saveDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Präsentation Speichern");
        alert.setMessage("Neuen Namen eingeben");

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                copyDirectory(currentPresentation, value);

                deleteFilesFromDir(FileUtilities.PFAD_ROOT + "tempImages");
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }
    }

