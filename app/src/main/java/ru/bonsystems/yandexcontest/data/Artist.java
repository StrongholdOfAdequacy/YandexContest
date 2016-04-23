package ru.bonsystems.yandexcontest.data;

import android.graphics.drawable.Drawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.bonsystems.yandexcontest.Controller;
import ru.bonsystems.yandexcontest.R;

/**
 * Created by Kolomeytsev Anton on 13.04.2016.
 * This class is a part of Yandex Contest project.
 * Класс, хранящий данные для каждого исполнителя
 */
public class Artist {
    public final long id;
    public final String name;
    public final List<String> genres;
    public final int tracks;
    public final int albums;
    public final String link;
    public final String description;
    public final Cover cover;

    public Artist(JSONObject json) throws JSONException {
        this(
                json.getLong("id"),
                json.getString("name"),
                asList(json.getJSONArray("genres")),
                json.getInt("tracks"),
                json.getInt("albums"),
                (json.has("link") ? json.getString("link") : null),
                json.getString("description"),
                new Cover(json.getJSONObject("cover"))
        );
    }

    private static List<String> asList(JSONArray jsonArray) throws JSONException {
        ArrayList<String> genres = new ArrayList<>();
        for (int i = 0, ie = jsonArray.length(); i < ie; i++)
            genres.add(jsonArray.getString(i));
        return genres;
    }

    public Artist(long id, String name, List<String> genres, int tracks, int albums, String link, String description, Cover cover) {
        this.id = id;
        this.name = name;
        this.genres = genres;
        this.tracks = tracks;
        this.albums = albums;
        this.link = link;
        this.description = description;
        this.cover = cover;
    }

    public String getStats() {
        return tracks + " " + Controller.getInstance().getString(R.string.artist_tracks1)
                + ", " + albums + " " + Controller.getInstance().getString(R.string.artist_albums1);
    }

    public String getGenres() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0, ie = genres.size(); i < ie; i++) {
            builder.append(genres.get(i));
            if (i < ie - 1) builder.append(", ");
        }
        return builder.toString();
    }

    public String getAlbums() {
        return albums + " " + Controller.getInstance().getString(R.string.artist_albums1);
    }

    public String getTracks() {
        return tracks + " " + Controller.getInstance().getString(R.string.artist_tracks1);
    }

    public static class Cover {
        public final String urlSmall;
        public Drawable small;
        public final String urlBig;
        public Drawable big;

        public Cover(JSONObject json) throws JSONException {
            this(json.getString("small"), json.getString("big"));
        }

        public Cover(String urlSmall, String urlBig) {
            this.urlSmall = urlSmall;
            this.urlBig = urlBig;
        }
    }
}
