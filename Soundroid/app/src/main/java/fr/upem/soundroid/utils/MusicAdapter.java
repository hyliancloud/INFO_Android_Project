package fr.upem.soundroid.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundroid.R;

import java.util.List;

import fr.upem.soundroid.MainActivity;
import fr.upem.soundroid.databaseComponents.model.music.Music;
import fr.upem.soundroid.databaseComponents.providers.playlist.PlayListViewModel;
import fr.upem.soundroid.utils.dialog.DialogMark;
import fr.upem.soundroid.utils.dialog.DialogPlaylist;
import fr.upem.soundroid.utils.dialog.DialogTag;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    private static ClickListener clickListener;
    private List<Music> musicList;
    private FragmentActivity ctx;
    private MainActivity main;

    public MusicAdapter(List<Music> musicList, FragmentActivity ctx) {
        super();
        this.musicList = musicList;
        this.ctx = ctx;
        main =  ((MainActivity) ctx);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.music_entry, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.update(musicList.get(position));
        final Context ctx = holder.threeDot.getContext();

        holder.threeDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(ctx, holder.threeDot);
                //inflating menu from xml resource
                popup.inflate(R.menu.menu_list_music);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            // set song as next
                            case R.id.menu1:
                                main.AddSongForLater(musicList.get(position));
                                return true;
                            // add song to a playlist
                            case R.id.menu2:
                                startFrag(musicList.get(position));
                                return true;
                            //expose to url
                            case R.id.menu3:
                                //if time
                                return true;
                             //add tagg to song
                            case R.id.menu4:
                                startTagDialog(musicList.get(position));
                                return true;
                             //add mark to song
                            case R.id.menu5:
                                startMarkDialog(musicList.get(position));
                                return true;

                            default:
                                //should not happend
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public interface ClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        MusicAdapter.clickListener = clickListener;
    }

    public void SetList(List<Music> mlist){
        musicList = mlist;
    }

    public Music getMusicAtPos(int i){
        return musicList.get(i);
    }

    public List<Music> getList(){ return musicList;}

    private void startFrag(final Music music){
        PlayListViewModel plvm = new ViewModelProvider(ctx).get(PlayListViewModel.class);
        plvm.getAllplaylist().observe(ctx, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                DialogPlaylist dpl = new DialogPlaylist(strings,music);
                dpl.show(ctx.getSupportFragmentManager(),"");
            }
        });
    }

    private void startMarkDialog(Music music) {
        DialogMark dm = new DialogMark(music);
        dm.show(ctx.getSupportFragmentManager(),"");
    }

    private void startTagDialog(Music music) {
        DialogTag dt = new DialogTag(music);
        dt.show(ctx.getSupportFragmentManager(),"");
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title;
        private TextView artistName;
        private TextView albumName;
        private TextView threeDot;
        private ImageView albumArt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            LinearLayout itemWrapper = itemView.findViewById(R.id.item_wrapper);
            itemWrapper.setOnClickListener(this);

            this.title = itemView.findViewById(R.id.name_view_list_entry);
            this.artistName = itemView.findViewById(R.id.artist_view_list_entry);
            this.albumName = itemView.findViewById(R.id.album_view_list_entry);
            this.albumArt = itemView.findViewById(R.id.album_art_list_entry);
            this.threeDot =  itemView.findViewById(R.id.textViewOptions);
        }

        @SuppressLint("SetTextI18n")
        @RequiresApi(api = Build.VERSION_CODES.Q)
        private void update(Music music) {
            this.title.setText(music.getTitle());
            this.artistName.setText(music.getAuthor());
            this.albumName.setText("Unknown Album");
            this.albumArt.setImageBitmap(music.getThumbnail(itemView.getContext()));
        }
        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, getAdapterPosition());
        }

    }
}
