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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.VideoView;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;

import java.io.File;

public class SimpleFileViewActivity extends Activity{

    private PDFView mPdfView;
    private VideoView mVideoView;
    private ProgressDialog dialog;
    private File file;
    private ImageButton menuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_simple_fileview);

        setupUI(this);

        startFile();

    }

    private void startFile() {
        if(FileUtilities.getFileExtension(file.getName()).equals("pdf")) {

            //new loadPDFTask(this).execute(file);
            mPdfView.setVisibility(View.VISIBLE);
            setupPDFView();
            mVideoView.setVisibility(View.GONE);

        }else{
            try{
                mVideoView.setVisibility(View.VISIBLE);
                mPdfView.setVisibility(View.GONE);
                Uri uri = Uri.parse(file.getAbsolutePath());
                mVideoView.setVideoURI(uri);
                mVideoView.requestFocus();
                mVideoView.start();
            }catch (Exception e){
                Log.e("Fehler", "Video konnte nicht geladen werden");
            }
        }
    }

    private void setupPDFView() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading PDF...");
        mPdfView.fromFile(file)
                .defaultPage(1)
                .showMinimap(false)
                .enableSwipe(true)
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        dialog.dismiss();
                    }
                })
                .load();
    }

    @Override
    protected void onResume(){
        super.onResume();
        startFile();
    }

    private void setupUI(Context ctx) {

        final Context context = ctx;
        String path = getIntent().getExtras().getString("file");
        file = new File(path);
        mPdfView = (PDFView) findViewById(R.id.pdfview);
        menuButton = (ImageButton)findViewById(R.id.menuButton);
        mVideoView = (VideoView)findViewById(R.id.videoview);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,SlideUpMenu.class);
                i.putStringArrayListExtra("filePaths", getIntent().getStringArrayListExtra("filePaths"));
                startActivityForResult(i,1);
            }
        });
    }

    /*
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
            mPdfView.fromFile(file)
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
    */

    @Override
    public void onPause(){

        super.onPause();
        if(dialog != null)
            dialog.dismiss();
        mPdfView.recycle();
    }

    @Override
    protected void onStop(){
        super.onStop();
        mPdfView.recycle();
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
                    file = new File(getIntent().getStringExtra("presentationPath")+data.getExtras().getString("path"));
                    startFile();
                }
                if (resultCode == RESULT_CANCELED) {
                    //Write your code if there's no result
                }
                break;

            default:
                break;
        }
    }

    /*@Override
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
    }*/
}
