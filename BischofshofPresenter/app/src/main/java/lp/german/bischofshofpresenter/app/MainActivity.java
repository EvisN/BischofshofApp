package lp.german.bischofshofpresenter.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends Activity {

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

    }

    private void addUIClickListeners(){

        //Intents


    }
}
