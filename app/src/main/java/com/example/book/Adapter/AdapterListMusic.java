package com.example.book.Adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

import com.example.book.Activity.MusicPlayerActivity;
import com.example.book.Model.audio.AudioModel;
import com.example.book.R;
import com.example.book.music.MyMediaPlayer;

public class AdapterListMusic extends RecyclerView.Adapter<AdapterListMusic.ViewHolder>{

    ArrayList<AudioModel> songsList;
    Context context;

    public AdapterListMusic(ArrayList<AudioModel> songsList, Context context) {
        this.songsList = songsList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_view_item,parent,false);
        return new AdapterListMusic.ViewHolder(view);
    }

    @Override
    @SuppressLint("RecyclerView")
    public void onBindViewHolder(AdapterListMusic.ViewHolder holder,  int position) {
        AudioModel songData = songsList.get(position);
        holder.titleTextView.setText(songData.getTitle());

        if(MyMediaPlayer.currentIndex==position){
            holder.titleTextView.setTextColor(Color.parseColor("#FF0000"));
        }
        else
        {
            holder.titleTextView.setTextColor(Color.parseColor("#000000"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //navigate to another acitivty

                MyMediaPlayer.getInstance().reset();
                MyMediaPlayer.currentIndex = position;
                Intent intent = new Intent(context, MusicPlayerActivity.class);
                intent.putExtra("LIST",songsList);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                }
            });

        }

        @Override
        public int getItemCount() {
            return songsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            TextView titleTextView;
            ImageView iconImageView;
            public ViewHolder(View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.music_title_text);
                iconImageView = itemView.findViewById(R.id.icon_view);
            }
        }
}

