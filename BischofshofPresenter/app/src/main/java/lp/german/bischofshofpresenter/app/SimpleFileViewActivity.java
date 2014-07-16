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
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import java.io.File;
import java.util.ArrayList;

public class SimpleFileViewActivity extends Activity{

    private PDFView mPdfView;
    private VideoView mVideoView;
    private ProgressDialog dialog;
    private File file;
    private ImageButton menuButton;
    private ArrayList<String> filepaths;
    private boolean hasNextFile, hasPreviousFile;
    private ImageView previous, next;
    private String nextPath, previousPath;
    private int currentFileIndex, lastIndex = 0;
    private int nOfElements;
    private boolean isSingleFile = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_simple_fileview);

        if(getIntent().hasExtra("singleFile")) {
            isSingleFile = true;
        }

        setupUI(this);
        setListeners(this);
        startFile(false);

    }

    private void setMultiFileView() {
        filepaths = getIntent().getStringArrayListExtra("filePaths");
        nOfElements = filepaths.size();
        file = new File(FileUtilities.PFAD_PRAESENTATION + "/" + filepaths.get(currentFileIndex));
    }

    private void setSingleFileView() {
        isSingleFile = true;
        file = new File(getIntent().getExtras().get("file").toString());
        menuButton.setVisibility(View.INVISIBLE);
    }

    private void startFile(Boolean previousFileClicked) {
        if(isSingleFile){
            setSingleFileView();
        }else {
            setMultiFileView();
        }

        lastIndex = currentFileIndex;
        if(FileUtilities.getFileExtension(file.getName()).equals("pdf")) {

            //new loadPDFTask(this).execute(file);
            mPdfView.setVisibility(View.VISIBLE);
            if(!isSingleFile) {
                setupPDFView(file, previousFileClicked);
            } else{
                if(file!=null){
                    setupPDFView(file, false);
                }
            }
            mVideoView.setVisibility(View.GONE);
            checkPage(mPdfView.getCurrentPage());

        }else{
            try{
                mVideoView.setVisibility(View.VISIBLE);
                mPdfView.setVisibility(View.GONE);
                Uri uri;
                uri = Uri.parse(file.getAbsolutePath());
                mVideoView.setVideoURI(uri);
                mVideoView.requestFocus();
                mVideoView.start();
                if(currentFileIndex<nOfElements-1){
                    next.setVisibility(View.VISIBLE);
                }else {
                    next.setVisibility(View.GONE);
                }
                if(currentFileIndex!=0){
                    previous.setVisibility(View.VISIBLE);
                }else {
                    previous.setVisibility(View.GONE);
                }
            }catch (Exception e){
                Log.e("Fehler", "Video konnte nicht geladen werden");
            }
        }
    }

    private void setupPDFView(File newFile, final Boolean previousFileClicked) {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading PDF...");
        final Boolean pre = previousFileClicked;
        mPdfView.fromFile(newFile)
                .defaultPage(1)
                .showMinimap(false)
                .enableSwipe(true)
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        if(pre){
                            mPdfView.jumpTo(nbPages);
                        }
                        dialog.dismiss();
                    }
                })
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        checkPage(page);
                    }
                })
                .load();
    }

    private void checkPage(int page){

        if(page==mPdfView.getPageCount()&&currentFileIndex<nOfElements-1){
            next.setVisibility(View.VISIBLE);
        }else {
            next.setVisibility(View.GONE);
        }
        if(page==1&&currentFileIndex!=0){
            previous.setVisibility(View.VISIBLE);
        }else {
            previous.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

        startFile(false);
    }

    private void setupUI(Context ctx) {

        final Context context = ctx;
        String path = getIntent().getExtras().getString("file");
        file = new File(path);
        mPdfView = (PDFView) findViewById(R.id.pdfview);
        menuButton = (ImageButton)findViewById(R.id.menuButton);
        mVideoView = (VideoView)findViewById(R.id.videoview);
        previous = (ImageView)findViewById(R.id.previous_file);
        next = (ImageView)findViewById(R.id.next_file);
    }

    private void setListeners(final Context context) {
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,SlideUpMenu.class);
                i.putStringArrayListExtra("filePaths", filepaths);
                i.putExtra("index", currentFileIndex);
                startActivityForResult(i,1);
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentFileIndex!=0){
                    mPdfView.recycle();
                    currentFileIndex--;
                    startFile(true);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentFileIndex!=nOfElements-1){
                    mPdfView.recycle();
                    currentFileIndex++;
                    startFile(false);
                }
            }
        });
    }

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
                    currentFileIndex=data.getExtras().getInt("index");
                    if(lastIndex<currentFileIndex) {
                        startFile(false);
                    }else {
                        startFile(true);
                    }
                }
                if (resultCode == RESULT_CANCELED) {
                    //Write your code if there's no result
                }
                break;
            default:
                break;
        }
    }
}
