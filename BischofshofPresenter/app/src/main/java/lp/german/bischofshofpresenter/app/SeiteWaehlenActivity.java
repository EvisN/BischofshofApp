package lp.german.bischofshofpresenter.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;

import java.io.File;

/**
 * Created by Fabi on 16.07.2014.
 */

public class SeiteWaehlenActivity extends Activity {


    File file;
    RelativeLayout relLayoutMain;
    Button btnChoose;
    PDFView pdfView;
    TextView textView;
    int countPages = 0;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_page);
        Intent i = getIntent();
        file = (File) i.getExtras().get("file");


        relLayoutMain = (RelativeLayout)findViewById(R.id.choose_page_relative_layout);
        ViewGroup group = (ViewGroup)  relLayoutMain;


        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.page_template, null);
        btnChoose = (Button) v.findViewById(R.id.btnChoosePage);
        textView = (TextView) v.findViewById(R.id.page_template_text);

        pdfView = (PDFView)v.findViewById(R.id.pdfview_single_page);
        //new LongOperation().execute(file);


        pdfView.fromFile(file).onLoad(new OnLoadCompleteListener() {
            @Override
            public void loadComplete(int nbPages) {
                countPages = pdfView.getPageCount();
                textView.setText("PDF Pages: " + countPages);
                setupPages();

            }
        }).load();
        group.addView(v, 0);


        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = pdfView.getPageCount();
            }
        });
    }
    private void setupPages(){
        for (int i = 1;i<countPages;i++){
            relLayoutMain = (RelativeLayout)findViewById(R.id.choose_page_relative_layout);
            ViewGroup group = (ViewGroup)  relLayoutMain;

            LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.page_template, null);
            Button btnChoose = (Button) v.findViewById(R.id.btnChoosePage);
            TextView textView = (TextView) v.findViewById(R.id.page_template_text);
            PDFView pdfView = (PDFView)v.findViewById(R.id.pdfview_single_page);
            textView.setText("Page " + i);


            pdfView.fromFile(file).pages(i).load();
            group.addView(v, 0);
            btnChoose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }

}
