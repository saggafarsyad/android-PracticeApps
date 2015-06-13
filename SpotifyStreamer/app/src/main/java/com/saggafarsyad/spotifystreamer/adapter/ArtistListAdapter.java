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

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by Muhammad on 13/06/2015.
 */
public class ArtistListAdapter extends BaseAdapter {

    private Artist mDataSet[];
    private Context mContext;

    public ArtistListAdapter(Artist[] dataSet, Context context) {
        this.mDataSet = dataSet;
        this.mContext = context;
    }

    public ArtistListAdapter(List<Artist> dataSet, Context context) {
        this.mDataSet = dataSet.toArray(new Artist[dataSet.size()]);
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
        Artist artist = mDataSet[position];

        // Get Image with width 64px
        Image image = null;
        for (Image tmp : artist.images) {
            if (tmp.width == 200) {
                image = tmp;
                break;
            }
        }

        if (image != null) {
            // Show thumbnail if there is 64px images
            Picasso.with(mContext).load(image.url).into(holder.thumbnailImageView);
        } else {
            if (!artist.images.isEmpty()) {
                // Show largest image
                Picasso.with(mContext).load(artist.images.get(0).url).into(holder.thumbnailImageView);
            } else {
                // Show N/A image
            }
        }

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
