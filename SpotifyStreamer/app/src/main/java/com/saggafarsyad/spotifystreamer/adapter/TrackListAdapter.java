package com.saggafarsyad.spotifystreamer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.saggafarsyad.spotifystreamer.R;
import com.saggafarsyad.spotifystreamer.model.TrackItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Muhammad on 13/06/2015.
 */
public class TrackListAdapter extends BaseAdapter {
    private TrackItem mDataSet[];
    private Context mContext;

    public TrackListAdapter(TrackItem[] mDataSet, Context mContext) {
        this.mDataSet = mDataSet;
        this.mContext = mContext;
    }

    public TrackListAdapter(List<Track> input, Context context) {
        // Convert Track to TrackItem
        // Get size
        int size = input.size();

        TrackItem trackDataset[] = new TrackItem[size];

        for (int i = 0; i < size; i++) {
            Track track = input.get(i);

            // Add track to dataset
            trackDataset[i] = new TrackItem(
                    track.id,
                    track.name,
                    track.album.name,
                    track.album.images.get(0).url
            );
        }
        this.mDataSet = trackDataset;
        this.mContext = context;
    }

    public TrackItem[] getDataSet() {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_track, parent, false);

            // Init Holder
            holder = new ViewHolder(
                    (ImageView) convertView.findViewById(R.id.thumbnail),
                    (TextView) convertView.findViewById(R.id.track_name),
                    (TextView) convertView.findViewById(R.id.album_name)
            );

            // Set tag to recycle holder
            convertView.setTag(holder);
        } else {
            // Get recycled holder
            holder = (ViewHolder) convertView.getTag();
        }

        TrackItem item = mDataSet[position];

        Picasso.with(mContext).load(item.albumArtworkUrl).into(holder.thumbnailImageView);

        // Show Track Name
        holder.trackNameTextView.setText(item.name);

        // Show Album Name
        holder.albumkNameTextView.setText(item.albumName);

        return convertView;
    }

    private static class ViewHolder {
        ImageView thumbnailImageView;
        TextView trackNameTextView;
        TextView albumkNameTextView;

        public ViewHolder(ImageView thumbnailImageView, TextView trackNameTextView, TextView albumkNameTextView) {
            this.thumbnailImageView = thumbnailImageView;
            this.trackNameTextView = trackNameTextView;
            this.albumkNameTextView = albumkNameTextView;
        }
    }
}
