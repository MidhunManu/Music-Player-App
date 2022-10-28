package com.calculations.music;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class player extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

    private TextView textView;
    private ImageView previous;
    private ImageView play;
    private ImageView next;
    ArrayList<File>Songs;
    MediaPlayer mediaPlayer;
    String songName;
    int position;
    SeekBar seekBar;
    Thread updateSeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        textView = findViewById(R.id.songName);
        previous = findViewById(R.id.previous);
        play = findViewById(R.id.play);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Songs = (ArrayList) bundle.getParcelableArrayList("songList");
        songName = intent.getStringExtra("currentSong");
        textView.setText(songName);
        textView.setSelected(true);
        position = intent.getIntExtra("position" , 0);

        Uri uri = Uri.parse(Songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this , uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        updateSeek = new Thread() {
            @Override
            public void run() {
                int currentPosition = 0;
                try {
                    while(currentPosition < mediaPlayer.getDuration()) {
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        sleep(800);
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                }

            }
        };
        updateSeek.start();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()) {
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.pause();
                }
                else {
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.start();
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();

                if (position != 0) {
                    position = position - 1;
                }
                else {
                    position = Songs.size() - 1;
                }
                Uri uri = Uri.parse(Songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext() , uri);
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration());
                songName = Songs.get(position).getName().toString();
                textView.setText(songName);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();

                if (position != Songs.size() - 1) {
                    position = position + 1;
                }
                else {
                    position = 0;
                }
                Uri uri = Uri.parse(Songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext() , uri);
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration());
                songName = Songs.get(position).getName().toString();
                textView.setText(songName);
            }
        });
    }
}