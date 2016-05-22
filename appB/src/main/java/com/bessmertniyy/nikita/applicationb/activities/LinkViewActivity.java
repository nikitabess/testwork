package com.bessmertniyy.nikita.applicationb.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bessmertniyy.nikita.applicationb.R;
import com.bessmertniyy.nikita.applicationb.services.RemoveLinkService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;

public class LinkViewActivity extends AppCompatActivity {

    private static final int LINK_STATUS_OK = 0;
    private static final int LINK_STATUS_IMAGE_LOADING_ERROR = 1;
    private static final int LINK_STATUS_UNKNOWN = 2;
    public static final String TABLE_LINK_COLUMN_ID = "_id";
    public static final String TABLE_LINK_COLUMN_URL = "link_URL";
    public static final String TABLE_LINK_COLUMN_STATUS = "link_status";
    public static final String TABLE_LINK_COLUMN_ADD_DATE = "link_add_date";
    private static final String URI_AUTHORITY = "com.bessmertniyy.nikita.testwork.db.contentprovider";
    private static final String URI_BASE_PATH = "links";
    public static final Uri URI_CONTENT = Uri.parse("content://" +  URI_AUTHORITY + "/" + URI_BASE_PATH);

    private String BROADCAST_ACTION = " com.bessmertniyy.nikita.applicationb.UPDATE_DB";
    private long dateOfLinkOpening;
    private int linkStatus = -1;
    private ImageView urlImageView;
    private ProgressBar imageLoadingProgressBar;
    private Bitmap loadedImage;
    private String URL;
    private boolean downloadImage = false;
    private boolean isForUpdate = false;
    private boolean isForSave = false;
    private long linkMillis;
    private int linkId;
    private Uri linksURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        urlImageView = (ImageView)findViewById(R.id.link_viewer_image_view);
        imageLoadingProgressBar = (ProgressBar)findViewById(R.id.link_viewer_image_loading_progressbar);

        setSupportActionBar(toolbar);

        if(getIntent().getExtras() != null){
            if(getIntent().getExtras().getBoolean("isForSave")){
                isForSave = true;
            }
            if(getIntent().getExtras().getBoolean("isForDelete")){
//                deleteLinkFromDatabase(getIntent().getExtras().getInt("linkId"));
                    downloadImage = true;
                isForSave = false;
                linkMillis = getIntent().getExtras().getLong("linkDate");
                new LoadImageFromURL().execute(URL);

                Intent startMyService = new Intent(this, RemoveLinkService.class);
                    startMyService.putExtra("linkId", getIntent().getExtras().getInt("linkId"));
                    startService(startMyService);

            }if(getIntent().getExtras().getBoolean("isForUpdate")){
                isForUpdate = true;
                isForSave = false;
                linkId = getIntent().getExtras().getInt("linkId");

                new LoadImageFromURL().execute(URL);

            }

            URL = getIntent().getExtras().getString("linkURL");

            dateOfLinkOpening = new Date().getTime();

            new LoadImageFromURL().execute(URL);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_link_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendBroadcast(){
        Intent broadcast = new Intent();
        broadcast.setAction(BROADCAST_ACTION);
        sendBroadcast(broadcast);
    }

    private void addLinkToDatabase(){

        ContentValues values = new ContentValues();
        values.put(TABLE_LINK_COLUMN_URL, URL);
        values.put(TABLE_LINK_COLUMN_ADD_DATE, dateOfLinkOpening);
        values.put(TABLE_LINK_COLUMN_STATUS, linkStatus);

        linksURI = getContentResolver().insert(URI_CONTENT, values);

        sendBroadcast();
    }

    private void updateLinkInDatabase(){
        Uri updateLinkUri = Uri.parse(URI_CONTENT + "/" + linkId);

        ContentValues values = new ContentValues();
        values.put(TABLE_LINK_COLUMN_URL, URL);
//        values.put(TABLE_LINK_COLUMN_ADD_DATE, dateOfLinkOpening);
        values.put(TABLE_LINK_COLUMN_STATUS, linkStatus);

        getContentResolver().update(updateLinkUri, values, null, null);
    }

    private void saveImage(Bitmap imageToBeSaved){

        File directory = new File(Environment.getExternalStorageDirectory().toString() + "/BIGDIG/test/B");
        directory.mkdirs();
        String imageFileName = "Image_" + linkMillis + ".jpg";
        File file = new File(directory, imageFileName);
        if(!file.exists()){
            try{
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                imageToBeSaved.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();

                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));

            }catch (Exception e){
                e.printStackTrace();
            }
        }


    }

    private class LoadImageFromURL extends AsyncTask<String, String, Bitmap>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            imageLoadingProgressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected Bitmap doInBackground(String... params) {

            try{
                InputStream in = new java.net.URL(params[0]).openStream();
                loadedImage = BitmapFactory.decodeStream(in);

            }catch (Exception e){
                linkStatus = LINK_STATUS_IMAGE_LOADING_ERROR;
                e.printStackTrace();
            }

            return loadedImage;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if(bitmap != null){
                linkStatus = LINK_STATUS_OK;
                imageLoadingProgressBar.setVisibility(View.GONE);
                urlImageView.setImageBitmap(bitmap);

                if(isForUpdate) {
                    updateLinkInDatabase();
                }if(downloadImage) {
                    saveImage(bitmap);
                }if(isForSave){
                    addLinkToDatabase();

                }
            }else{

                if(linkStatus == -1) {
                    linkStatus = LINK_STATUS_UNKNOWN;
                }
                imageLoadingProgressBar.setVisibility(View.GONE);
                if(isForSave) {
                    addLinkToDatabase();
                }

            }

        }
    }




}
