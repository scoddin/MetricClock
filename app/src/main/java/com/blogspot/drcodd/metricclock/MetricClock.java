package com.blogspot.drcodd.metricclock;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;


import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Handler;
import android.os.Message;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.TimeZone;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.ImageView;
import android.view.View;


public class MetricClock extends AppCompatActivity {
    int count = 0;
    String timeString;

    ImageView clockView;
    TextView timeTextView;
    Paint backPaint;
    Paint facePaint;
    Paint handPaintSec;
    Paint handPaintMin;
    Paint handPaintHr;
    Paint textPaint;
    Canvas canvas;
    Bitmap bitmap;
    float[] mSin = new float[100];
    float[] mCos = new float[100];
    float xCenter= 350, yCenter = 350, radius ;
    int secCount =0, minCount = 0, hrCount =0;
    int startX = 50;
    int startY = 50;
    int lineDirction = 0;
    int lastSec = 0, lastMin = 0, lastHr = 0;
    TimeZone timeeZone;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String string = bundle.getString("myKey");
            timeTextView = (TextView)findViewById(R.id.clockText);
            timeTextView.setText(string);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metric_clock);
        bitmap = Bitmap.createBitmap(700, 700, Bitmap.Config.ARGB_8888);

        clockView = (ImageView)this.findViewById(R.id.clockImage);
        clockView.setImageBitmap(bitmap);

        canvas = new Canvas(bitmap);
        //xCenter= this.getResources().getDisplayMetrics().widthPixels /2;
        //yCenter = this.getResources().getDisplayMetrics().heightPixels/2;
        radius = (float) (.75 *yCenter);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Button clockButton = (Button)findViewById(R.id.clockButton);
        clockButton.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View v) {
              //  goToClock();
                }
        });

        final Button stopButton = (Button)findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View v) {

               // goToStopWatch();
            }
        });

        final Button timerButton = (Button)findViewById(R.id.timerButton);
        timerButton.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View v) {

                //goToTimer();
            }
        });

        final Button alarmButton = (Button)findViewById(R.id.alarmButton);
        alarmButton.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View v) {
                //goToAlarm();
            }
        });


        setUpPaints();
        startClock();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_metric_clock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ConstraintLayout mainLayout =
                (ConstraintLayout)findViewById(R.id.contentLayout);

        switch (item.getItemId()) {
            case R.id.pacific:
                if (item.isChecked())item.setChecked(false);
                else item.setChecked(true);
                int offSet = -8;
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void startClock() {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Message msg = handler.obtainMessage();
                    Bundle bundle = new Bundle();

                    String dateString = getCurrentTime();
                    bundle.putString("myKey", timeString);
                    msg.setData(bundle);
                    handler.sendMessage(msg);

                    try {
                        // sleep for about 1 metric second
                        Thread.sleep(50);
                    } catch (InterruptedException e) {}
                }
            }
        };

        Thread myThread = new Thread(runnable);
        myThread.start();

    }
    public String getCurrentTime(){
        Calendar c = new GregorianCalendar();
        int h = c.get(c.HOUR_OF_DAY);
        int m = c.get(c.MINUTE);
        int s = c.get(c.SECOND);
        int millis = c.get(c.MILLISECOND);
        int day = c.get(c.DAY_OF_YEAR);
        double timeDouble = (double)(millis/1000. + s + 60 * m + 3600*h)/86400.;
        int milliPart = (int)(timeDouble * 100000);
        int hourPart = (int)(timeDouble* 10);
        int minPart = (int)(timeDouble * 1000 - hourPart *100);
        int secPart = (int)(milliPart - hourPart * 10000 - minPart *100);

        drawClock(secPart, minPart, hourPart);
        timeString = ("Metric time = " + day + "::" + String.format("%01d", hourPart) + ":" + String.format("%02d" , minPart) + ":" + String.format("%02d", secPart));
        return timeString;
    }

    public void drawClock(int seconds, int minutes, int hours) {

        handPaintSec.setColor(Color.WHITE);
        handPaintMin.setColor(Color.WHITE);
        handPaintHr.setColor(Color.WHITE);
        canvas.drawLine(xCenter, yCenter,
                xCenter + radius * mSin[lastSec],
                yCenter - radius * mCos[lastSec],
                handPaintSec);
        canvas.drawLine(xCenter, yCenter,
                xCenter + radius * mSin[lastMin],
                yCenter - radius * mCos[lastMin],
                handPaintMin);
        canvas.drawLine(xCenter, yCenter,
                xCenter + radius * mSin[lastHr],
                yCenter - radius * mCos[lastHr],
                handPaintHr);

        secCount = seconds;
        minCount = minutes;
        hrCount = hours * 10;

        handPaintSec.setColor(Color.BLACK);
        handPaintMin.setColor(Color.BLACK);
        handPaintHr.setColor(Color.BLACK);
        canvas.drawLine(xCenter, yCenter,
                xCenter + radius * mSin[secCount],
                yCenter - radius * mCos[secCount],
                handPaintSec);
        canvas.drawLine(xCenter, yCenter,
                xCenter + radius * mSin[minCount],
                yCenter - radius * mCos[minCount],
                handPaintMin);
        canvas.drawLine(xCenter, yCenter,
                (float)(xCenter + .8*radius * mSin[hrCount]),
                (float)(yCenter - .8*radius * mCos[hrCount]),
                handPaintHr);

        lastSec = secCount;
        lastMin = minCount;
        lastHr = hrCount;
    }

    public void setUpPaints() {

        for (int i = 0; i< 100; i++) {
            mSin[i] = (float)Math.sin(i* .02 * Math.PI );
            mCos[i] = (float)Math.cos(i* .02 * Math.PI );
        }

        textPaint = new Paint();
        textPaint.setTextSize(80);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        facePaint = new Paint();
        facePaint.setColor(Color.BLACK);
        facePaint.setStrokeWidth(5);
        facePaint.setStyle(Paint.Style.STROKE);

        float clockRadius = radius + 10;
        float pipRadius = 10;
        canvas.drawCircle(xCenter, yCenter, clockRadius, facePaint);

        for (int i = 0; i < 100; i+=10) {
            canvas.drawCircle(xCenter + mSin[i] *
                    clockRadius, yCenter - mCos[i] * clockRadius,
                    pipRadius, facePaint);
        }
        float textRadius = clockRadius + 60;
        for (int i = 0 ; i < 100; i+= 20) {
            String s = ("" + i/10);
            canvas.drawText(s, xCenter + mSin[i] * textRadius,
                    yCenter - mCos[i] * textRadius + 40,
                    textPaint);
        }

        handPaintSec = new Paint();
        handPaintSec.setColor(Color.BLACK);
        handPaintSec.setStyle(Paint.Style.STROKE);
        handPaintSec.setStrokeWidth(3);

        handPaintMin = new Paint();
        handPaintMin.setColor(Color.BLACK);
        handPaintMin.setStyle(Paint.Style.STROKE);
        handPaintMin.setStrokeWidth(6);

        handPaintHr = new Paint();
        handPaintHr.setColor(Color.BLACK);
        handPaintHr.setStyle(Paint.Style.STROKE);
        handPaintHr.setStrokeWidth(9);

    }
}

