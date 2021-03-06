package fr.upem.soundroid.ui.player;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import fr.upem.soundroid.MainActivity;
import com.example.soundroid.R;
import fr.upem.soundroid.databaseComponents.model.music.Music;

import android.os.Handler;

public class MusicPlayerFragment extends Fragment {

    private ImageView album_art;
    private TextView title;
    private TextView album;
    private TextView artist;
    private TextView duration;

    private Button playButton;
    private TextView playTime;
    private boolean isPlaying = false;
    private int playingTime;
    private int songDuration;
    private Handler timeHandler;
    private int pauseTime;

    private Button toggleMute;
    private boolean isMute = false;

    private Button previousSong;
    private Button nextSong;
    private Button shuffleSongs;

    private ProgressBar songTimeProgressBar;
    private MainActivity main;

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MusicPlayerViewModel musicPlayerViewModel = new ViewModelProvider(this).get(MusicPlayerViewModel.class);

        final View root = inflater.inflate(R.layout.fragment_musicplayer, container, false);

        title = root.findViewById(R.id.title_player);
        artist = root.findViewById(R.id.artist_name_player);
        album = root.findViewById(R.id.album_name_player);
        album_art = root.findViewById(R.id.album_art_player);
        duration = root.findViewById(R.id.song_duration_text);

        playTime = root.findViewById(R.id.played_time_text);
        main = ((MainActivity) getActivity());
        setProgressBar(root);
        timeHandler = new Handler();
        startPlayTime();
        setButtonListeners(root);

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("SetTextI18n")
    private void displayMusic(Music music) {
        if(music == null) return;
        if(music.getThumbnail(getContext()) == null){
            return;
        }
        album_art.setImageBitmap(music.getThumbnail(getContext()));
        title.setText(music.getTitle());
        artist.setText(music.getAuthor());
        album.setText("unknown album");
        duration.setText(getTime(Integer.parseInt(music.getDuration())));
    }

    private String getTime(int timeInSeconds) {
        int seconds = timeInSeconds / 1000;
        int minutes = seconds / 60;
        seconds -= (minutes*60);
        return minutes + ":" + seconds;
    }

    private void setButtonListeners(View root) {
        playButton = root.findViewById(R.id.toggle_play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlayButtonClick(v);
            }
        });

        toggleMute = root.findViewById(R.id.toggle_sound_button);
        toggleMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToggleMuteClick(v);
            }
        });

        previousSong = root.findViewById(R.id.previous_song_button);
        previousSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPreviousSongClick(v);
            }
        });

        nextSong = root.findViewById(R.id.next_song_button);
        nextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextSongClick(v);
            }
        });

        shuffleSongs = root.findViewById(R.id.shuffle_song_list_button);
        shuffleSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShuffleClick(v);
            }
        });
    }

    private void onPlayButtonClick(View v) {
        if(!isPlaying) {
            ((MainActivity) getActivity()).togglePause();
            playingTime = pauseTime;
            //startPlayTime();
            Toast toast = Toast.makeText(v.getContext(), "Play music", Toast.LENGTH_SHORT);
            toast.show();
            playButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_media_pause,
                    0, 0, 0);
            isPlaying = true;
        } else {
            Toast toast = Toast.makeText(v.getContext(), "Pause music", Toast.LENGTH_SHORT);
            toast.show();
            playButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_media_play,
                    0, 0, 0);
            isPlaying = false;
            ((MainActivity) getActivity()).togglePause();
        }
    }

    private void onToggleMuteClick(View v) {
        if(!isMute) {
            Toast toast = Toast.makeText(v.getContext(), "Mute", Toast.LENGTH_SHORT);
            toast.show();
            toggleMute.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_media_unmute,
                    0, 0, 0);
            isMute = true;
        } else {
            Toast toast = Toast.makeText(v.getContext(), "Unmute", Toast.LENGTH_SHORT);
            toast.show();
            toggleMute.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_media_mute,
                    0, 0, 0);
            isMute = false;
        }
    }

    private void onPreviousSongClick(View v) {
        Toast toast = Toast.makeText(v.getContext(), "Play previous song", Toast.LENGTH_SHORT);
        toast.show();
        main.playPrev();
    }

    private void onNextSongClick(View v) {
        Toast toast = Toast.makeText(v.getContext(), "Play next song", Toast.LENGTH_SHORT);
        toast.show();
        main.playNext();
    }

    private void onShuffleClick(View v) {
        Toast toast = Toast.makeText(v.getContext(), "Shuffle songs", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void setProgressBar(View root) {
        songTimeProgressBar = root.findViewById(R.id.music_player_progressBar);
        songTimeProgressBar.setMax(255); // max value supported by progressbar
        songTimeProgressBar.setProgress(0);
    }

    private void setProcessBarProgress(int playingTime, int songDuration) {
        // règle de trois
        int progress = playingTime * songTimeProgressBar.getMax() / songDuration;
        songTimeProgressBar.setProgress(progress);
    }

    private Runnable playingSongTime = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void run() {
            Music m = main.currentlyPlayed();
            int currentpos = main.TimeofMusic();
            int duration = main.currentDuration();
            if(m != null && currentpos != -1 && duration != -1) {
                displayMusic(m);
                playTime.setText(getTime(currentpos));
                setProcessBarProgress(currentpos, duration);
                timeHandler.postDelayed(getPlayingSongTime(), 1000);
            }
        }
    };

    private Runnable getPlayingSongTime() {
        return playingSongTime;
    }

    private void startPlayTime() {
        playingSongTime.run();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }


}