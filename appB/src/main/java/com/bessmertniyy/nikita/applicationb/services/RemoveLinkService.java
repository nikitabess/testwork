package com.bessmertniyy.nikita.applicationb.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.widget.Toast;

public class RemoveLinkService extends IntentService {

//    private Handler handler;
//    private CountDownTimer countDownTimer;
    private static final String URI_AUTHORITY = "com.bessmertniyy.nikita.testwork.db.contentprovider";
    private static final String URI_BASE_PATH = "links";
    public static final Uri URI_CONTENT = Uri.parse("content://" +  URI_AUTHORITY + "/" + URI_BASE_PATH);
    private String BROADCAST_ACTION = " com.bessmertniyy.nikita.applicationb.UPDATE_DB";

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        if(intent != null) {
//        handler = new Handler();
            new CountDownTimer(15000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    deleteLinkFromDatabase(intent.getExtras().getInt("linkId"));
                    Toast.makeText(getApplicationContext(), "Запись была удалена.", Toast.LENGTH_LONG).show();
                }
            }.start();
        }

        return super.onStartCommand(intent, flags, startId);
    }

//    private void deleteLinkFromDatabase(){
//        Uri uri = Uri.parse(MyTodoContentProvider.CONTENT_URI + "/"
//                + info.id);
//        getContentResolver().delete(uri, null, null);
//    }
    private void deleteLinkFromDatabase(int linkId){

        Uri deleteLinkUri = Uri.parse(URI_CONTENT + "/" + linkId);

        getContentResolver().delete(deleteLinkUri, null, null);
        sendBroadcast();
    }

    public RemoveLinkService() {
        super("RemoveLinkService");
    }
    private void sendBroadcast(){
        Intent broadcast = new Intent();
        broadcast.setAction(BROADCAST_ACTION);
        sendBroadcast(broadcast);
    }
    @Override
    protected void onHandleIntent(Intent intent) {

    }

}
