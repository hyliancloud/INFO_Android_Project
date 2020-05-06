package com.example.soundroid.musiclist;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundroid.R;
import com.example.soundroid.databaseComponents.model.Music;
import com.example.soundroid.databaseComponents.providers.MusicViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by cloud on 06/05/2020.
 */
public class MusicListFragment extends Fragment {
    public MusicViewModel mMusicViewModel;

    private RecyclerView recyclerView;
    private MusicAdapter musicAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_music_list, container, false);

        mMusicViewModel = new ViewModelProvider(this).get(MusicViewModel.class);
        mMusicViewModel.getAllMusic().observe(this, new Observer<List<Music>>() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onChanged(@Nullable final List<Music> musicList) {
                assert musicList != null;
                //displayMusicList(musicList);
                recyclerView = root.findViewById(R.id.recycler_view_music_list);
                if(recyclerView == null) {
                    Log.d("Level.INFO", "recycler view null ??");
                    return;
                }
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
                recyclerView.setAdapter(new MusicAdapter(new ArrayList<Music>()));
                musicAdapter = new MusicAdapter(musicList);
                musicAdapter.setOnItemClickListener(new MusicAdapter.ClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Log.d(String.valueOf(Level.INFO), "hello, this is song <" + musicList.get(position).getTitle() + "> :)");
                    }
                });
                recyclerView.setAdapter(musicAdapter);
            }
        });

        return root;
    }
}