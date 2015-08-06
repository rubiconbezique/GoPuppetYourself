package com.nakedape.gopuppetyourself;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.facebook.FacebookSdk;

import java.io.File;

import bolts.AppLinks;

public class PlayerActivity extends Activity {
    private static String LOG_TAG = "Player Activity";

    private Context mContext;
    private RelativeLayout playerStage;
    private ImageButton playButton;
    private String showUrl;
    private long downloadId;
    private BroadcastReceiver downloadReceiver;
    private File puppetShowFile;
    private PuppetShowPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mContext = this;
        playerStage = (RelativeLayout)findViewById(R.id.player_stage);
        playButton = (ImageButton) findViewById(R.id.player_play_button);

        // Hide the action bar
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.hide();

        // Initialize Facebook Sdk
        FacebookSdk.sdkInitialize(this);

        // Load data from intent
        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
        if (targetUrl != null) {
            String data = getIntent().getData().toString();
            showUrl = data.substring(19, data.indexOf("target_url") - 1);
            Log.i(LOG_TAG, "showUrl " + showUrl);
            // Delete old file if it exists before downloading new file
            puppetShowFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "puppet_show.show");
            if (puppetShowFile.exists())
                puppetShowFile.delete();
            String url = showUrl;
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription("Puppet Show");
            request.setTitle("Go Puppet Yourself!");
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "puppet_show.show");

            // get download service and enqueue file
            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            downloadId = manager.enqueue(request);
            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            downloadReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    if (downloadId == reference) {
                        Log.d(LOG_TAG, "File downloaded");
                        unregisterReceiver(downloadReceiver);
                        playerStage.post(new Runnable() {
                            @Override
                            public void run() {
                                ImageButton playButton = (ImageButton)findViewById(R.id.player_play_button);
                                playButton.setAlpha(0f);
                                playButton.setVisibility(View.VISIBLE);
                                AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.animator.pop_in);
                                set.setTarget(playButton);
                                set.start();
                            }
                        });
                    }
                }
            };
            registerReceiver(downloadReceiver, filter);

        }
    }

    @Override
    protected void onStop(){
        super.onStop();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player, menu);
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

    public void PlayClick(View v){
        if (player == null) {
            player = new PuppetShowPlayer(mContext, playerStage);
            player.setOnPlayFinishedListener(new PuppetShowPlayer.OnPlayFinishedListener() {
                @Override
                public void OnPlayFinish() {
                    playButton.setBackground(getResources().getDrawable(R.drawable.ic_av_replay_large));
                    AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.animator.implode_in);
                    set.setTarget(playButton);
                    set.start();
                    player = null;
                }
            });
            player.LoadShowFromZipFile(puppetShowFile);
            if (player.prepareToPlay()) {
                playButton.setBackground(getResources().getDrawable(R.drawable.ic_av_pause_large));
                AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.animator.explode_out);
                set.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        player.Play();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                set.setTarget(playButton);
                set.start();
            }
        } else if (player.isPlaying()){
            player.Pause();
            playButton.setBackground(getResources().getDrawable(R.drawable.ic_av_play_arrow_large));
            AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.animator.implode_in);
            set.setTarget(playButton);
            set.start();
        } else {
            playButton.setBackground(getResources().getDrawable(R.drawable.ic_av_pause_large));
            AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.animator.explode_out);
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    player.Play();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            set.setTarget(playButton);
            set.start();
        }
    }
}
