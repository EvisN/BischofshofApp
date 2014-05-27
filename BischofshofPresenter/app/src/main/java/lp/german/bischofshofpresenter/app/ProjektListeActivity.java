package lp.german.bischofshofpresenter.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;


public class ProjektListeActivity extends Activity {

    private ListView listProjekte;
    private ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projekt_liste_aktivity);

        setupList();
    }

    private void setupList(){
        listProjekte = (ListView)findViewById(R.id.listProjekte);


    }


}
