package io.github.vkdisco.player;

import android.os.Parcelable;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.github.vkdisco.model.Track;
import io.github.vkdisco.model.jsonadapter.TrackAdapter;
import io.github.vkdisco.player.interfaces.OnPlaylistChangedListener;

public class Playlist implements Track.OnTrackDataLoadedListener {
    private static final GsonBuilder gsonBuilder;
    private static final Type type = new TypeToken<List<Track>>() {
    }.getType();

    static {
        gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Track.class, new TrackAdapter());
        gsonBuilder.setPrettyPrinting();
    }

    private List<Track> tracks = new ArrayList<>();
    private int index = 0;
    private OnPlaylistChangedListener listener;

    public Playlist(OnPlaylistChangedListener listener) {
        this.listener = listener;
    }

    public void addTrack(Track track) {
        tracks.add(track);
        track.setOnTrackDataLoadedListener(this);
        track.requestDataLoad();
        if (listener != null) {
            listener.onPlaylistChanged();
        }
    }

    public void addTracks(Collection<Track> tracks) {
        for (Track track : tracks) {
            this.tracks.add(track);
            track.setOnTrackDataLoadedListener(this);
            track.requestDataLoad();
        }
        if (listener != null) {
            listener.onPlaylistChanged();
        }
    }

    public Track removeTrack(int index) {
        if (index < this.index) {
            this.index--;
        }
        Track previous = tracks.remove(index);
        if (listener != null) {
            listener.onPlaylistChanged();
        }
        return previous;
    }

    public boolean swap(int indexA, int indexB) {
        int size = tracks.size();
        if (indexA > size || indexB > size) {
            return false;
        }
        Collections.swap(tracks, indexA, indexB);
        if (listener != null) {
            listener.onPlaylistChanged();
        }
        return true;
    }

    public String serialize() {
        return gsonBuilder.create().toJson(tracks, type);
    }

    public boolean deserialize(String serialized) {
        this.tracks = gsonBuilder.create().fromJson(serialized, type);
        return !tracks.isEmpty();
    }

    public boolean hasPreviousTrack() {
        int temp = index - 1;
        return temp >= 0;
    }

    public Track getPreviousTrack() {
        return tracks.get(--index);
    }

    public Track getCurrentTrack() {
        return tracks.get(index);
    }

    public int getCurrentTrackIndex() {
        return index;
    }

    public boolean hasNextTrack() {
        int temp = index + 1;
        return temp < tracks.size();
    }

    public int count() {
        return tracks.size();
    }

    public Track getNextTrack() {
        return tracks.get(++index);
    }

    public Track getTrack(int index) {
        return tracks.get(index);
    }

    public Track playTrack(int index) {
        this.index = index;
        return tracks.get(index);
    }

    @Override
    public void onTrackDataLoaded(boolean success) {
        if (listener != null) {
            listener.onPlaylistChanged();
        }
    }
}
