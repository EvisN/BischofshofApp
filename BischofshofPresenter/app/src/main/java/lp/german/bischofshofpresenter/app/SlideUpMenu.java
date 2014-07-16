package lp.german.bischofshofpresenter.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;


public class SlideUpMenu extends Activity{

    private FileUtilities fileUtilities = new FileUtilities();
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v("SlideUpMenu", "Activity started");
        //Setze Activity Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_slide_up_menu);

        index = getIntent().getExtras().getInt("index");

        GetFilePathsAsyncTask getFilePathsAsyncTask = new GetFilePathsAsyncTask();
        getFilePathsAsyncTask.execute();
    }

    private class GetFilePathsAsyncTask extends AsyncTask<String,Void,ArrayList<String>>{

        @Override
        protected ArrayList<String> doInBackground(String... params){
            ArrayList<String> tempList = getIntent().getStringArrayListExtra("filePaths");
            return tempList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result){
            setupScrollViewContent(result);
        }

    }

    private void setupScrollViewContent(final ArrayList<String> files){

        for(int i = 0; i<files.size(); i++)
        {
            final int position = i;
            final File currentFile = new File(files.get(i));
            String fileName = currentFile.getName();
            LayoutInflater vi = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.file_template, null);

            TextView textView = (TextView)v.findViewById(R.id.file_template_text);
            if(fileName.length()>10) {
                textView.setText(fileName.substring(2, 12) + "...");
            }else {
                textView.setText(fileName.substring(2));
            }

            ImageView imageView = (ImageView)v.findViewById(R.id.file_template_img);


            if(fileUtilities.getFileExtension(fileName).equals("pdf")){
                imageView.setImageResource(R.drawable.pdf2);
            }else {
                imageView.setImageResource(R.drawable.video);
            }

            View fileList = findViewById(R.id.file_scroll_view_linear_layout);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("path",currentFile.getAbsolutePath());

                        returnIntent.putExtra("index", position);

                    setResult(RESULT_OK,returnIntent);
                    finish();
                }
            });
            ((ViewGroup)fileList).addView(v);
        }
    }
}

