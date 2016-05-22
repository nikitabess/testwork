package com.bessmertniyy.nikita.applicationb.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.bessmertniyy.nikita.applicationb.R;

public class MainActivity extends AppCompatActivity {

    private static final int COUNT_DOWN_MILLIS = 10000;
    private static final int COUNT_DOWN_MILLIS_INTERVAL = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView countdownNoticeTextView = (TextView)findViewById(R.id.countdown_timer_notice);

        startCountdownTimer(countdownNoticeTextView);

    }

    private void startCountdownTimer(final TextView countdownNoticeTextView){

        new CountDownTimer(COUNT_DOWN_MILLIS, COUNT_DOWN_MILLIS_INTERVAL){
            @Override
            public void onTick(long millisUntilFinished) {
                countdownNoticeTextView.setText(getString(R.string.countdown_notice_description_text)
                        + " "
                        + millisUntilFinished/ COUNT_DOWN_MILLIS_INTERVAL
                        + " "
                        + getString(R.string.countdown_notice_seconds_text));
            }

            @Override
            public void onFinish() {
                finish();
            }
        }.start();

    }


}
