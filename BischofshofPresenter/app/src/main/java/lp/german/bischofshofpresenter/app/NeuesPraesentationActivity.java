package lp.german.bischofshofpresenter.app;

/**
 * TODO
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class NeuesPraesentationActivity extends Activity{

    //Views
    private ImageButton btn_speichern, btn_scroll_left, btn_scroll_right, btn_wb, btn_bh, btn_folie_hinzufuegen, btn_folienseiten_hinzufuegen, btn_menu_schliesen;
    private View m_spacer_wb, m_spacer_bh;
    private ScrollView ordner_explorer, datei_explorer, praesentations_explorer;

    //Menu
    private RelativeLayout menu;

    //Dummy Button zum test des Sidemenues
    private Button dummy_button;

    //Flags
    private boolean BISCHOFSHOF_GEWAEHLT = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neues_projekt);

        setupUI();
        setupClickListeners();
    }

    private void setupUI(){

        //Buttons
        btn_speichern = (ImageButton)findViewById(R.id.btn_speichern);
        btn_scroll_left = (ImageButton)findViewById(R.id.btn_scroll_left);
        btn_scroll_right = (ImageButton)findViewById(R.id.btn_scroll_right);
        btn_wb = (ImageButton)findViewById(R.id.logo_weltenburger);
        btn_bh = (ImageButton)findViewById(R.id.logo_bh);
        btn_menu_schliesen = (ImageButton)findViewById(R.id.btn_menu_schliesen);
        btn_folie_hinzufuegen = (ImageButton)findViewById(R.id.btn_folie_hinzufuegen);
        btn_folienseiten_hinzufuegen = (ImageButton)findViewById(R.id.btn_folienseiten_hinzufuegen);

        //Menu
        menu = (RelativeLayout)findViewById(R.id.menu);

        dummy_button = (Button)findViewById(R.id.btn_dummy);

        //Spacer
        m_spacer_bh = findViewById(R.id.bhSpace);
        m_spacer_wb = findViewById(R.id.wbSpace);

        //Scrollviews
        ordner_explorer = (ScrollView)findViewById(R.id.ordner_struktur);
        datei_explorer = (ScrollView)findViewById(R.id.datei_explorer);
        praesentations_explorer = (ScrollView)findViewById(R.id.praesentations_explorer);

    }

    private void setupClickListeners(){
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

        dummy_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSideMenu();
            }
        });
    }

    private void openSideMenu(){
        menu.setVisibility(View.VISIBLE);
    }

    private void setBrauerei(){
        if(BISCHOFSHOF_GEWAEHLT){
            m_spacer_wb.setVisibility(View.VISIBLE);
            m_spacer_bh.setVisibility(View.GONE);

            //Hier Ordnerstruktur auf BH setzen
        }else{
            m_spacer_wb.setVisibility(View.GONE);
            m_spacer_bh.setVisibility(View.VISIBLE);

            //Hier Ordnerstruktur auf WB setzen
        }
    }

    private void fuegeFolieHinzu(){
        //Hier die ganze gewählte Folie zur Präsentation hinzufuegen
    }

    private void fuegeFolienseitenHinzu(){
        //Hier nur Bestimmte Seiten zur Präsentation hinzufuegen
    }

    private void savePresentation(){
        //Hier wird die Präsentation gespeichert
    }
}
