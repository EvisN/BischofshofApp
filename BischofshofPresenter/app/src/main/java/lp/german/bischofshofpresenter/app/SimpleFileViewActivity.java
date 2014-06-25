package lp.german.bischofshofpresenter.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.VideoView;

import com.joanzapata.pdfview.PDFView;

import java.io.File;

public class SimpleFileViewActivity extends Activity {

    private ImageView mNextFile, mPreviousFile;
    private PDFView pdfView;
    private ProgressDialog dialog;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_simple_fileview);

        setupUI();

        checkIfNextPreviousFileExists();

        startFile();

    }

    private void startFile() {
        if(FileUtilities.getFileExtension(file.getName()).equals("pdf")) {

            new loadPDFTask(this).execute(file);

        }else{
            try{
                VideoView mVideoView = (VideoView)findViewById(R.id.videoview);
                mVideoView.setVisibility(View.VISIBLE);
                Uri uri = Uri.parse(file.getAbsolutePath());
                mVideoView.setVideoURI(uri);
                mVideoView.requestFocus();
                mVideoView.start();
            }catch (Exception e){
                Log.e("Fehler", "Video konnte nicht geladen werden");
            }
        }
    }

    private void setupUI() {
        file = (File)getIntent().getExtras().get("file");
        mNextFile = (ImageView)findViewById(R.id.next_file);
        mPreviousFile = (ImageView)findViewById(R.id.previous_file);
        pdfView = (PDFView) findViewById(R.id.pdfview);
    }

    private void checkIfNextPreviousFileExists() {
        if(getIntent().getBooleanExtra("hasNextFile", false)){
            mNextFile.setVisibility(View.VISIBLE);
            mNextFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent returnIntent = new Intent();
                    setResult(PraesentationsActivity.NEXT_FILE,returnIntent);
                    finish();
                }
            });
        }

        if(getIntent().getBooleanExtra("hasPreviousFile", false)){
            mPreviousFile.setVisibility(View.VISIBLE);
            mPreviousFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent returnIntent = new Intent();
                    setResult(PraesentationsActivity.PREVIOUS_FILE,returnIntent);
                    finish();
                }
            });
        }
    }

    //lädt PDF asynchron und zeigt ProgressDialog an
    private class loadPDFTask extends AsyncTask<Object,String,File> {

        private Context context;


        public loadPDFTask(Context ctx){
            context = ctx;
            dialog = new ProgressDialog(context);
            dialog.setMessage("Loading PDF...");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }
        @Override
        protected File doInBackground(Object... args) {
            File file = (File)args[0];
            pdfView.fromFile(file)
                    .defaultPage(1)
                    .showMinimap(false)
                    .enableSwipe(true)
                    .load();
            return file;
        }
        @Override
        protected void onPostExecute(File unused) {
            super.onPostExecute(unused);
            if(dialog!=null) {
                dialog.dismiss();
            }
        }
    }

    @Override
    public void onPause(){

        super.onPause();
        if(dialog != null)
            dialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.simple_pdfview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
