package com.saggafarsyad.spotifystreamer;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.saggafarsyad.spotifystreamer.model.TrackItem;
import com.squareup.picasso.Picasso;

import java.io.IOException;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerFragment extends DialogFragment implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    private static final String ARGS_TABLET_PANE = "tablet_pane";
    // Playback Handler
    final Handler playbackHandler = new Handler();
    private final String LOG_TAG = "PlayerFragment";
    // Track Playlist
    private TrackItem mPlaylist[];
    private int mPlaylistPosition;
    // Media Player
    private MediaPlayer mMediaPlayer;
    // Tablet
    private boolean isTabletPane;
    // Views
    private TextView artistNameTextView;
    private TextView albumNameTextView;
    private ImageView albumArtworkImageView;
    private TextView trackNameTextView;
    private TextView durationTextView;
    private SeekBar playbackSeekBar;
    final Runnable playbackRunnable = new Runnable() {
        @Override
        public void run() {
            // Get position in second
            if (mMediaPlayer != null) {
                int mCurrentPosition = mMediaPlayer.getCurrentPosition();
                // Set progress
                playbackSeekBar.setProgress(mCurrentPosition);
                // Repeat after 100ms
                playbackHandler.postDelayed(this, 1000);
            }
        }
    };
    private ImageButton playPauseButton;

    public PlayerFragment() {
    }

    public static PlayerFragment newInstance(boolean isTabletPane) {
        PlayerFragment fragment = new PlayerFragment();

        // Add Arguments
        Bundle args = new Bundle();
        args.putBoolean(ARGS_TABLET_PANE, isTabletPane);

        fragment.setArguments(args);

        return fragment;
    }

    public void setPlaylist(TrackItem[] mPlaylist) {
        this.mPlaylist = mPlaylist;
    }

    public void setPlaylistPosition(int mCurrentPosition) {
        this.mPlaylistPosition = mCurrentPosition;

        fetchTrack();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get Arguments
        if (getArguments() != null) {
            isTabletPane = getArguments().getBoolean(ARGS_TABLET_PANE);
        }

        // Inflate Layout
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        artistNameTextView = (TextView) rootView.findViewById(R.id.artist_name);
        albumNameTextView = (TextView) rootView.findViewById(R.id.album_name);
        albumArtworkImageView = (ImageView) rootView.findViewById(R.id.album_artwork);
        trackNameTextView = (TextView) rootView.findViewById(R.id.track_name);
        durationTextView = (TextView) rootView.findViewById(R.id.track_end);
        playbackSeekBar = (SeekBar) rootView.findViewById(R.id.playback_seeker);
        playPauseButton = (ImageButton) rootView.findViewById(R.id.play_pause);
        ImageButton prevButton = (ImageButton) rootView.findViewById(R.id.prev);
        ImageButton nextButton = (ImageButton) rootView.findViewById(R.id.next);

        if (savedInstanceState == null) {
            if (!isTabletPane) {
                // Get Intent
                Intent intent = getActivity().getIntent();
                // Add Track list to play list
                Parcelable tmp[] = intent.getParcelableArrayExtra(getString(R.string.intent_extra_playlist));
                mPlaylist = new TrackItem[tmp.length];
                System.arraycopy(tmp, 0, mPlaylist, 0, tmp.length);
                // Get Position
                mPlaylistPosition = intent.getIntExtra(getString(R.string.intent_extra_current_track_position), 0);
            }
        } else {
            // Get from savedInstance
            mPlaylistPosition = savedInstanceState.getInt(getString(R.string.args_playlist_position), 0);
            mPlaylist = (TrackItem[]) savedInstanceState
                    .getParcelableArray(getString(R.string.args_track_list));
        }

        playbackSeekBar.setOnSeekBarChangeListener(this);

        // Set Play/Pause Button on click listener
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayer.isPlaying()) {
                    pauseTrack();
                } else {
                    playTrack();
                }
            }
        });
        // Set previous track button on click listener
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlaylistPosition == 0) {
                    // This is the first track,
                    // Select the last track
                    mPlaylistPosition = mPlaylist.length - 1;
                } else {
                    // Select previous track
                    mPlaylistPosition--;
                }

                fetchTrack();
            }
        });
        // Set next track button on click listener
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlaylistPosition == mPlaylist.length - 1) {
                    // This is the last track
                    // Select the first track
                    mPlaylistPosition = 0;
                } else {
                    // Select next track
                    mPlaylistPosition++;
                }
                // Start fetching track
                fetchTrack();
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArray(getString(R.string.args_track_list), mPlaylist);
        outState.putInt(getString(R.string.args_playlist_position), mPlaylistPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Init Media player
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);

        // Fetch track
        if (!isTabletPane)
            fetchTrack();
    }

    @Override
    public void onPause() {
        // Release Media Player
        mMediaPlayer.release();
        mMediaPlayer = null;

        super.onPause();
    }

    private void playTrack() {
        // Get playback position
        int progress = playbackSeekBar.getProgress();

        if (progress > 0 && progress <= playbackSeekBar.getMax()) {
            mMediaPlayer.seekTo(progress);
        }

        getActivity().runOnUiThread(playbackRunnable);
        mMediaPlayer.start();
        playPauseButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_av_pause, null));
    }

    private void pauseTrack() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();

            playbackHandler.removeCallbacks(playbackRunnable);
            playPauseButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_av_play_arrow, null));
        }
    }

    private void fetchTrack() {
        mMediaPlayer.reset();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        TrackItem mCurrentTrack = mPlaylist[mPlaylistPosition];

        try {
            mMediaPlayer.setDataSource(mCurrentTrack.previewUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.prepareAsync();

        playbackSeekBar.setProgress(0);
        artistNameTextView.setText(mCurrentTrack.artistName);
        albumNameTextView.setText(mCurrentTrack.albumName);
        trackNameTextView.setText(mCurrentTrack.name);
        durationTextView.setText(getDurationString(0));
        // Load artwork
        Picasso.with(getActivity()).load(mCurrentTrack.albumArtworkUrl).into(albumArtworkImageView);
        playPauseButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_av_play_arrow, null));
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        // Setup seekbar
        playbackSeekBar.setMax(mMediaPlayer.getDuration());
        // Set end track duration
        durationTextView.setText(getDurationString(mp.getDuration()));
        // Play
        playTrack();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // Stop seek bar handler
        playbackHandler.removeCallbacks(playbackRunnable);
        // Change to Play button
        playPauseButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_av_play_arrow, null));
        // Reset position
        playbackSeekBar.setProgress(0);
        mMediaPlayer.seekTo(0);
    }

    private String getDurationString(long millis) {
        long second = millis / 1000;
        int minutes = 0;

        while (second > 59) {
            minutes++;
            second -= 60;
        }

        if (second < 10) {
            return minutes + ":0" + second;
        }

        return minutes + ":" + second;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // If user changed the seekbar, set seekTo
        if (mMediaPlayer != null && fromUser) {
            mMediaPlayer.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Do nothing
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Do nothing
    }


}

