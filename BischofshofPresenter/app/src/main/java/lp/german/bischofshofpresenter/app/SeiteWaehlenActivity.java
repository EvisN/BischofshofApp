package lp.german.bischofshofpresenter.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.qrcode.ByteArray;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFImage;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFPaint;

import net.sf.andpdf.nio.ByteBuffer;
import net.sf.andpdf.refs.HardReference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import java.util.ArrayList;

import com.itextpdf.text.Image;




/**
 * Created by Fabi on 16.07.2014.
 */

public class SeiteWaehlenActivity extends Activity {


    File file = null;
    String fileName = "";
    LinearLayout relLayoutMain;
    int countPages = 0;
    ArrayList<String> choosenPageURLs;
    ArrayList<String> allPageURLs;
    ArrayList<CheckBox> checkBoxes;
    int count = 1;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_page);
        Intent i = getIntent();
        file = (File) i.getExtras().get("file");
        fileName = file.getName();

        //Settings
        PDFImage.sShowImages = true; // show images
        PDFPaint.s_doAntiAlias = true; // make text smooth
        HardReference.sKeepCaches = true; // save images in cache

        choosenPageURLs = new ArrayList<String>();
        allPageURLs = new ArrayList<String>();
        checkBoxes = new ArrayList<CheckBox>() ;

        pdfLoadImages();
        setupUI();
    }
    private void setupUI(){
        Button btnOk =  (Button) findViewById(R.id.btnOk_single_page);
        Button btnCancel = (Button) findViewById(R.id.btnCancel_single_page);
        Button btnCheckAll = (Button) findViewById(R.id.btnSelectAll_single_page);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
        btnCheckAll.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0;i<checkBoxes.size();i++){
                    checkBoxes.get(i).setChecked(true);
                }
            }
        }));
        TextView txtDocName = (TextView) findViewById(R.id.txtDocumentName_single_page);
        txtDocName.setText(fileName);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(checkBoxes.size()>0){
                mergePages();
            } else {
                Toast.makeText(getApplicationContext(), "Keine Seiten ausgewählt!", Toast.LENGTH_SHORT).show();
            }

            }
        });
    }
    private void setupPages(Bitmap bmp, int pageNumber) {

        relLayoutMain = (LinearLayout) findViewById(R.id.content_single_page);
        ViewGroup group = (ViewGroup) relLayoutMain;

        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.page_template, null);
        LinearLayout llPreview = (LinearLayout)v.findViewById(R.id.choose_page_linear_layout);
       // llPreview.setId(count);

        CheckBox cbChoose = (CheckBox) v.findViewById(R.id.cbChoose_single_page);
        TextView textView = (TextView) v.findViewById(R.id.page_template_text);
        ImageView pdfPageImage = (ImageView) v.findViewById(R.id.img_single_page);
        textView.setText("Seite " + pageNumber);
        checkBoxes.add(cbChoose);
        cbChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox cb = (CheckBox) view;
                if(!cb.isChecked()){
                    choosenPageURLs.remove(checkBoxes.indexOf(cb));
                } else{
                    int indexCB = checkBoxes.indexOf(cb);
                    String path = allPageURLs.get(indexCB);
                    choosenPageURLs.add(path);
                }
            }
        });


        Bitmap bp = BitmapFactory.decodeFile(FileUtilities.PFAD_ROOT + "tempImages/tempPage" + pageNumber + ".jpeg");
        pdfPageImage.setImageBitmap(bp);




        group.addView(v);


        count++;
    }




    //http://stackoverflow.com/questions/8814758/need-help-to-convert-a-pdf-page-into-bitmap-in-android-java
    private void pdfLoadImages()
    {
        try
        {
            // run async
            new AsyncTask<Void, Void, Bitmap>()
            {
                // create and show a progress dialog
                ProgressDialog progressDialog = ProgressDialog.show(SeiteWaehlenActivity.this, "", "Lade Seiten...");

                @Override
                protected void onPostExecute(Bitmap result)
                {
                    //after async close progress dialog
                    for(int i = 1;i<=countPages;i++){
                        setupPages(result, i);
                    }

                    progressDialog.dismiss();
                }

                @Override
                protected Bitmap doInBackground(Void... params)
                {
                    try
                    {

                        int viewSize = 800;
                        RandomAccessFile raf = new RandomAccessFile(file, "r");
                        FileChannel channel = raf.getChannel();
                        ByteBuffer bb = ByteBuffer.NEW(channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()));

                        raf.close();
                        // create a pdf doc
                        PDFFile pdf = new PDFFile(bb);
                        //Get the first page from the pdf doc
                        //create a scaling value according to the WebView Width
                        final float scale = 1;
                        //convert the page into a bitmap with a scaling value
                        countPages = pdf.getNumPages();

                        //loop through the rest of the pages and repeat the above
                        for (int i = 1; i <= pdf.getNumPages(); i++) {
                            PDFPage PDFpage = pdf.getPage(i, true);
                            Bitmap page = PDFpage.getImage((int) (PDFpage.getWidth() * scale)/3, (int) (PDFpage.getHeight() * scale)/3, null, true, true);

                            File myDir = new File(FileUtilities.PFAD_ROOT + "tempImages");
                            myDir.mkdirs();
                            File outputFile = new File(myDir, "tempPage" + i + ".jpeg");
                            allPageURLs.add( FileUtilities.PFAD_ROOT + "tempImages/tempPage" + i + ".jpeg");
                            choosenPageURLs.add( FileUtilities.PFAD_ROOT + "tempImages/tempPage" + i + ".jpeg");
                            FileOutputStream out = new FileOutputStream(outputFile);
                            page.compress(Bitmap.CompressFormat.JPEG, 50, out);

                            out.flush();
                            out.close();

                        }
                    } catch(Exception e){}


                    return null;
                }
            }.execute();
            System.gc();// run GC
        }
        catch (Exception e)
        {
            Log.d("error", e.toString());
        }

    }

    private void mergePages(){

        ArrayList<Integer> indexSelectedPages = new ArrayList<Integer>();

        for(int i = 0;i<checkBoxes.size();i++){
            if(checkBoxes.get(i).isChecked()){
                indexSelectedPages.add(i);
            }
        }
        int size = indexSelectedPages.size();
        try {
            PdfReader reader = new PdfReader(file.getAbsolutePath());
            Document document = new Document();
            PdfCopy copy = new PdfCopy(document, new FileOutputStream(FileUtilities.PFAD_ROOT + "tempImages/EDIT_"+fileName));

            document.open();
            for (int i = 1;i<=size;i++){
               copy.addPage(copy.getImportedPage(reader, i));            }
               document.close();
        }catch(Exception e){

        }
        Intent i = new Intent();
        i.putExtra("fileName", fileName);
        setResult(RESULT_OK,i);
        finish();

    }

    private void mergeToPDF(){
        try {
            Document document = new Document();

            PdfWriter.getInstance(document, new FileOutputStream(FileUtilities.PFAD_ROOT + "tempImages/EDIT_"+fileName));
            document.open();
            for (int i = 0;i<choosenPageURLs.size();i++){
                File file = new File(choosenPageURLs.get(i));



                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                Bitmap bm = BitmapFactory.decodeStream(fis);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100 , baos);
                byte[] imageInByte = baos.toByteArray();
                Image image = Image.getInstance(imageInByte);
                document.add(image);
            }

            document.close();
        }catch(Exception e){

        }

        setResult(RESULT_OK);
        finish();

    }



    }



