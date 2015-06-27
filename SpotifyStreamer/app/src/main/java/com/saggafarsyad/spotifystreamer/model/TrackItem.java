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
    public String albumArtworkUrl;
    public String albumName;

    public TrackItem() {
    }

    protected TrackItem(Parcel in) {
        this.spotifyId = in.readString();
        this.name = in.readString();
        this.albumArtworkUrl = in.readString();
        this.albumName = in.readString();
    }

    public TrackItem(String spotifyId, String name, String albumName, String albumArtworkUrl) {
        this.spotifyId = spotifyId;
        this.name = name;
        this.albumArtworkUrl = albumArtworkUrl;
        this.albumName = albumName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.spotifyId);
        dest.writeString(this.name);
        dest.writeString(this.albumArtworkUrl);
        dest.writeString(this.albumName);
    }
}
