package lp.german.bischofshofpresenter.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends Activity {

    private ImageButton btnStartPresentation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUIElements();
        addUIClickListeners();

    }

    private void setupUIElements(){

        //Navigationsbuttons
        btnStartPresentation = (ImageButton)findViewById(R.id.btn_start_presentation);

    }

    private void addUIClickListeners(){

        //Intents
        btnStartPresentation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PraesentationsActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.zoom_in,R.anim.zoom_out);
            }
        });

    }
}
