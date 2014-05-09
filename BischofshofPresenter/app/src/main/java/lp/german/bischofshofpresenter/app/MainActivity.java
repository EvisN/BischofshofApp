package lp.german.bischofshofpresenter.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends Activity {

    private ImageButton btnNeuesProjekt, btnProjektBearbeiten, btnEinstellungen, btnPraesentationStarten;
    private FileHandler fileHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUIElements();
        addUIClickListeners();

        fileHandler = new FileHandler(this);
    }

    private void setupUIElements(){

        //Navigationsbuttons
        btnNeuesProjekt = (ImageButton)findViewById(R.id.btn_neues_projekt);
        btnProjektBearbeiten = (ImageButton)findViewById(R.id.btn_projekt_bearbeiten);
        btnEinstellungen = (ImageButton)findViewById(R.id.btn_einstellungen);
        btnPraesentationStarten = (ImageButton)findViewById(R.id.btn_praesentation_starten);
    }

    private void addUIClickListeners(){

        //Intents
        btnNeuesProjekt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), NeuesProjektActivity.class);
                startActivity(i);
            }
        });

        btnProjektBearbeiten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ProjektBearbeitenActivity.class);
                startActivity(i);
            }
        });

        btnEinstellungen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), EinstellungenActivity.class);
                startActivity(i);
            }
        });

        btnPraesentationStarten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileHandler.getPräsentationen();
            }
        });

    }
}
