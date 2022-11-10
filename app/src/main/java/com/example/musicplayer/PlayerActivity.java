package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaParser;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class PlayerActivity extends AppCompatActivity {
Button btnpause, btnnext,btnprev, btnff, btnfr;
TextView txtsname, txtsstart, txtsstop;
SeekBar seekmusic;
ImageView imageView;


String sname;
public static final String EXTRA_NAME= "song_name";
static MediaPlayer mediaPlayer;
int position;
ArrayList<File> mySongs;
Thread updateseekbar;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            Intent mIntent =new Intent(PlayerActivity.this, MainActivity.class);
            sname=mySongs.get(position).getName();
            mIntent.putExtra(EXTRA_NAME,sname);
            startActivity(mIntent);
          //  onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

   /* @Override
    protected void onDestroy() {

        super.onDestroy();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnprev = findViewById(R.id.btnprev);
        btnnext= findViewById(R.id.btnnext);
        btnpause= findViewById(R.id.playbtn);
        btnff= findViewById(R.id.btnff);
        btnfr= findViewById(R.id.btnfr);
        txtsname= findViewById(R.id.txtsn);
        txtsstart= findViewById(R.id.txtsstart);
        txtsstop= findViewById(R.id.txtsstop);
        seekmusic =findViewById(R.id.seekbar);
        imageView =findViewById(R.id.imageview);

        if(mediaPlayer!= null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent i= getIntent();
        Bundle bundle=i.getExtras();


//        System.out.println("DUCK" + bundle.get("songs").getClass().getSimpleName());
        mySongs= new ArrayList(Arrays.asList((Object[]) bundle.get("songs")));
        String songName= i.getStringExtra("songname");
        position=bundle.getInt("pos", 0);
        txtsname.setSelected(true);
        Uri uri= Uri.parse(mySongs.get(position).toString());
        sname=mySongs.get(position).getName();
        txtsname.setText(sname);

        mediaPlayer= MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();

        updateseekbar = new Thread()
        {
            @Override
            public void run(){
                int totalDuration = mediaPlayer.getDuration();
                int currentposition=0;

                while(currentposition<totalDuration)
                {
                    try {
                        sleep(500);
                        currentposition=mediaPlayer.getCurrentPosition();
                        seekmusic.setProgress(currentposition);
                    }
                    catch(InterruptedException | IllegalStateException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };
        seekmusic.setMax(mediaPlayer.getDuration());
        updateseekbar.start();
        seekmusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
        seekmusic.getThumb().setColorFilter(getResources().getColor(R.color.white),PorterDuff.Mode.SRC_IN);

        seekmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
              mediaPlayer.seekTo(seekBar.getProgress());

            }
        });

        String endTime= createTime(mediaPlayer.getDuration());
        txtsstop.setText(endTime);

        final Handler handler=new Handler();
        final int delay=1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime= createTime(mediaPlayer.getCurrentPosition());
                txtsstart.setText(currentTime);
                handler.postDelayed(this,delay);

            }
        }, delay);


        btnpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying())
                {
                    btnpause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                    mediaPlayer.pause();
                }
                else
                {
                    btnpause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                    mediaPlayer.start();
                }
            }
        });
        //next listener

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                btnnext.performClick();

            }
        });

      //  int audiosessionId=mediaPlayer.getAudioSessionId();

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=((position+1)%mySongs.size());
                Uri u= Uri.parse(mySongs.get(position).toString());
                mediaPlayer= MediaPlayer.create(getApplicationContext(),u);
                sname=mySongs.get(position).getName();
                txtsname.setText(sname);
                mediaPlayer.start();
                btnpause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                startAnimation(imageView);
            }
        });
       btnprev.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
             mediaPlayer.stop();
             mediaPlayer.release();
             position=((position-1)<0)?(mySongs.size()-1):(position-1);

             Uri u= Uri.parse(mySongs.get(position).toString());
             mediaPlayer=MediaPlayer.create(getApplicationContext(),u);
             sname= mySongs.get(position).getName();
             txtsname.setText(sname);
             mediaPlayer.start();
             btnpause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
             startAnimation(imageView);
           }
       });

       btnff.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(mediaPlayer.isPlaying())
               {
                   mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
               }
           }
       });

       btnfr.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(mediaPlayer.isPlaying())
               {
                   mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
               }
           }
       });

    }
    public void startAnimation(View view)
    {
        ObjectAnimator animator= ObjectAnimator.ofFloat(imageView,"rotation",0f,360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet= new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }

    public String createTime(int duration)
    {
        String time="";
        int min= duration/1000/60;
        int sec= duration/1000%60;

        time+=min+":";

        if(sec<10)
        {
            time+="0";
        }
        time+=sec;

        return time;
    }

    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(PlayerActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}