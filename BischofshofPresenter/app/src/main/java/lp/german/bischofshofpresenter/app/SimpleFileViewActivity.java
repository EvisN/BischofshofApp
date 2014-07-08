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
import android.widget.Toast;
import android.widget.VideoView;

import com.joanzapata.pdfview.PDFView;

import java.io.File;

public class SimpleFileViewActivity extends Activity  implements SimpleGestureFilter.SimpleGestureListener {

    private SimpleGestureFilter detector;
    private ImageView mNextFile, mPreviousFile;
    private PDFView pdfView;
    private ProgressDialog dialog;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detector = new SimpleGestureFilter(this,this);
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
        String path = getIntent().getExtras().getString("file");
        file = new File(path);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case 1 :
                if(resultCode == RESULT_OK){
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("path", data.getStringExtra("path"));
                    setResult(RESULT_OK,returnIntent);
                    finish();
                }
                if (resultCode == RESULT_CANCELED) {
                    //Write your code if there's no result
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onSwipe(int direction) {
        String str = "";

        switch (direction) {
            case SimpleGestureFilter.SWIPE_RIGHT :
                str = "Swipe Right";
                break;
            case SimpleGestureFilter.SWIPE_LEFT :
                str = "Swipe Left";
                break;
            case SimpleGestureFilter.SWIPE_DOWN :
                str = "Swipe Down";
                break;
            case SimpleGestureFilter.SWIPE_UP :
                str = "Swipe Up";
                Intent i = new Intent(this,SlideUpMenu.class);
                i.putStringArrayListExtra("filePaths", getIntent().getStringArrayListExtra("filePaths"));
                startActivityForResult(i,1);
                break;
        }
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDoubleTap() {
        //momentan unnötig
    }
}
