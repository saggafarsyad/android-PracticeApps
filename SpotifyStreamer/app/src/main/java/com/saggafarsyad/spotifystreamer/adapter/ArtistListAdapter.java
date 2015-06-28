package com.saggafarsyad.spotifystreamer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.saggafarsyad.spotifystreamer.R;
import com.saggafarsyad.spotifystreamer.model.ArtistItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by Muhammad on 13/06/2015.
 */
public class ArtistListAdapter extends BaseAdapter {

//    private Artist mDataSet[];

    private ArtistItem mDataSet[];
    private Context mContext;

    public ArtistListAdapter(ArtistItem[] dataSet, Context context) {
        this.mDataSet = dataSet;
        this.mContext = context;
    }

    public ArtistListAdapter(List<Artist> dataSet, Context context) {
        this.mContext = context;

        updateDataSet(dataSet);
    }

    public void updateDataSet(List<Artist> dataSet) {
        this.mDataSet = new ArtistItem[dataSet.size()];

        // Convert Artist to ArtistItem
        for (int i = 0; i < dataSet.size(); i++) {
            this.mDataSet[i] = new ArtistItem(dataSet.get(i));
        }
    }

    public ArtistItem[] getDataSet() {
        return mDataSet;
    }

    @Override
    public int getCount() {
        return mDataSet.length;
    }

    @Override
    public Object getItem(int position) {
        return mDataSet[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            // Inflate layout
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_artist, parent, false);

            // Init ViewHolder
            holder = new ViewHolder(
                    (ImageView) convertView.findViewById(R.id.thumbnail),
                    (TextView) convertView.findViewById(R.id.artist_name)
            );

            // Set Tag for recycling
            convertView.setTag(holder);
        } else {
            // Get available ViewHolder
            holder = (ViewHolder) convertView.getTag();
        }

        // Get Artist item
        ArtistItem artist = mDataSet[position];

        // Show thumbnailUrl if there is 64px images
        Picasso.with(mContext).load(artist.thumbnailUrl).into(holder.thumbnailImageView);

        // Show artist name
        holder.artistNameTextView.setText(artist.name);

        return convertView;
    }

    private static class ViewHolder {
        ImageView thumbnailImageView;
        TextView artistNameTextView;

        public ViewHolder(ImageView thumbnailImageView, TextView artistNameTextView) {
            this.thumbnailImageView = thumbnailImageView;
            this.artistNameTextView = artistNameTextView;
        }
    }
}
