package lp.german.bischofshofpresenter.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

public class PraesentationsActivity extends Activity {

    private HorizontalScrollView scrollViewFiles;
    private PresentationFile[] files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_praesentations);

        //Hardcoded File Array Zum Testen
        files = new PresentationFile[10];
        for (int i = 0; i<files.length; i++)
        {
            files[i] = new PresentationFile("File"+String.valueOf(i),"PDF");
            Log.v("FILENAME:",files[i].Name());
        }

        setupUI();

        setupScrollViewContent();
    }

    private void setupUI(){
        scrollViewFiles = (HorizontalScrollView)findViewById(R.id.file_scroll_view);
    }

    private void setupScrollViewContent(){



        for(int i = 0; i<files.length; i++)
        {
            LayoutInflater vi = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.file_template, null);

            TextView textView = (TextView)v.findViewById(R.id.file_template_text);
            textView.setText(files[i].Name());
            Log.v("FILENAME:",files[i].Name());
            View fileList = findViewById(R.id.file_scroll_view_linear_layout);
            ((ViewGroup)fileList).addView(v);
        }

    }

}
