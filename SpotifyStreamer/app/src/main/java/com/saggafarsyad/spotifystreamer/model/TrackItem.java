package com.saggafarsyad.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Muhammad on 25/06/2015.
 */
public class TrackItem implements Parcelable {
    public static final Parcelable.Creator<TrackItem> CREATOR = new Parcelable.Creator<TrackItem>() {
        public TrackItem createFromParcel(Parcel source) {
            return new TrackItem(source);
        }

        public TrackItem[] newArray(int size) {
            return new TrackItem[size];
        }
    };
    public String spotifyId;
    public String name;
    public String artistName;
    public String albumName;
    public String albumArtworkUrl;
    public String previewUrl;

    public TrackItem(String spotifyId, String name, String artistName, String albumName, String albumArtworkUrl, String previewUrl) {
        this.spotifyId = spotifyId;
        this.name = name;
        this.artistName = artistName;
        this.albumName = albumName;
        this.albumArtworkUrl = albumArtworkUrl;
        this.previewUrl = previewUrl;
    }

    public TrackItem() {
    }

    protected TrackItem(Parcel in) {
        this.spotifyId = in.readString();
        this.name = in.readString();
        this.artistName = in.readString();
        this.albumName = in.readString();
        this.albumArtworkUrl = in.readString();
        this.previewUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.spotifyId);
        dest.writeString(this.name);
        dest.writeString(this.artistName);
        dest.writeString(this.albumName);
        dest.writeString(this.albumArtworkUrl);
        dest.writeString(this.previewUrl);
    }
}
