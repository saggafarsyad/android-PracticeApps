package com.saggafarsyad.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by Muhammad on 27/06/2015.
 */
public class ArtistItem implements Parcelable {
    public static final Parcelable.Creator<ArtistItem> CREATOR = new Parcelable.Creator<ArtistItem>() {
        public ArtistItem createFromParcel(Parcel source) {
            return new ArtistItem(source);
        }

        public ArtistItem[] newArray(int size) {
            return new ArtistItem[size];
        }
    };

    public String spotifyId;
    public String name;
    public String thumbnailUrl;

    public ArtistItem(String spotifyId, String name, String thumbnailUrl) {
        this.spotifyId = spotifyId;
        this.name = name;
        this.thumbnailUrl = thumbnailUrl;
    }

    public ArtistItem(Artist artist) {
        this.spotifyId = artist.id;
        this.name = artist.name;

        if (artist.images.size() > 0) {
            this.thumbnailUrl = artist.images.get(0).url;
        }
    }

    public ArtistItem() {
    }

    protected ArtistItem(Parcel in) {
        this.spotifyId = in.readString();
        this.name = in.readString();
        this.thumbnailUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.spotifyId);
        dest.writeString(this.name);
        dest.writeString(this.thumbnailUrl);
    }
}
