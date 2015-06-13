package com.saggafarsyad.spotifystreamer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.saggafarsyad.spotifystreamer.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Muhammad on 13/06/2015.
 */
public class TrackListAdapter extends BaseAdapter {

    private Track mDataSet[];
    private Context mContext;

    public TrackListAdapter(Track[] dataSet, Context context) {
        this.mDataSet = dataSet;
        this.mContext = context;
    }

    public TrackListAdapter(List<Track> dataSet, Context context) {
        this.mDataSet = dataSet.toArray(new Track[dataSet.size()]);
        this.mContext = context;
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

        // Get Artist item
        Track track = mDataSet[position];
        // Get thumbnail
        Image image = null;
        for (Image tmp : track.album.images) {
            if (tmp.width == 200) {
                image = tmp;
                break;
            }
        }

        // Show thumbnail
        if (image != null) {
            // Show thumbnail if there is 64px images
            Picasso.with(mContext).load(image.url).into(holder.thumbnailImageView);
        } else {
            if (!track.album.images.isEmpty()) {
                // Show largest image
                Picasso.with(mContext).load(track.album.images.get(0).url).into(holder.thumbnailImageView);
            } else {
                // Show N/A image
            }
        }

        // Show Track Name
        holder.trackNameTextView.setText(track.name);

        // Show Album Name
        holder.albumkNameTextView.setText(track.album.name);

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
