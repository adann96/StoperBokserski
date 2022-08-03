package com.example.stoperbokserski;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Chronometer chronometerStart, chronometerBreak;
    private Button startButton;
    private long pauseOffset;
    private boolean running;

    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.bell);

        String[] roundNo = {"first","second","third","fourth","fifth","sixth","seventh","eighth","ninth","tenth","eleventh","twelfth"};

        chronometerStart = findViewById(R.id.chronometerStart);
        chronometerBreak = findViewById(R.id.chronometerBreak);
        startButton = findViewById(R.id.startButton);
        chronometerStart.setBase(SystemClock.elapsedRealtime());
        chronometerBreak.setBase(SystemClock.elapsedRealtime());

        chronometerStart.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if ((SystemClock.elapsedRealtime() - chronometerStart.getBase()) >= 180000) {
                    if (i < 11) {
                        i+=1;
                        Toast.makeText(MainActivity.this, "The end of the " + roundNo[i-1] + " round!", Toast.LENGTH_SHORT).show();
                        stopChronometer();
                        running = false;
                        mediaPlayer.start();
                        chronometerBreak.setBase(SystemClock.elapsedRealtime());
                        chronometerBreak.start();
                        running = true;
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Training's over!", Toast.LENGTH_SHORT).show();
                        stopChronometer();
                        running = false;
                        mediaPlayer.start();
                        i = 0;
                    }
                }
            }
        });

        chronometerBreak.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if ((SystemClock.elapsedRealtime() - chronometerStart.getBase()) >= 60000) {
                    chronometerStart.setBase(SystemClock.elapsedRealtime());
                    chronometerStart.stop();
                    pauseOffset = 0;
                    running = false;

                    startButton.callOnClick();
                }
            }
        });
    }

    public void startChronometer(View v) {
        if (!running && startButton.getText().equals("Start")) {
            chronometerStart.setBase(SystemClock.elapsedRealtime());
            chronometerStart.start();
            running = true;
            final MediaPlayer mediaPlayerStart = MediaPlayer.create(this,R.raw.singlebell);
            mediaPlayerStart.start();
        }
        else if (!running && startButton.getText().equals("Go on")) {
            chronometerStart.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometerStart.start();
            running = true;
            startButton.setText("Start");
        }
    }

    public void pauseChronometer(View v) {
        if (running && startButton.getText().equals("Start")) {
            chronometerStart.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometerStart.getBase();
            running = false;
            startButton.setText("Go on");
        }
        else if (pauseOffset == 0) {
            chronometerStart.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometerStart.getBase();
            running = false;
        }
    }

    public void stopChronometer() {
        chronometerStart.setBase(SystemClock.elapsedRealtime());
        chronometerStart.stop();
        pauseOffset = 0;
    }

    public void resetChronometer(View v) {
        if (running && startButton.getText().equals("Start")) {
            stopChronometer();
            running = false;
        }
        else if (running == false && startButton.getText().equals("Go on")) {
            stopChronometer();
            startButton.setText("Start");
        }
        i = 0;
    }

}