package ru.bonsystems.yandexcontest.internet;

import android.graphics.Bitmap;

import ru.bonsystems.yandexcontest.data.Artist;

/**
 * Created by Kolomeytsev Anton on 09.04.2016.
 * This class is a part of Smart Quest Remastered project.
 * Пример использования:
 *
 * YApiMethods.artist.get()
 *                .setSuccessListener(callback)
 *                .executeAsync(); // подключится к серверу и получит JSON с исполнителями
 */
public class YApiMethods {
    public static ArtistMethods artist = new ArtistMethods();

    public static class ArtistMethods {
        public YApi.Request<String> get() {
            return YApi.get(YApi.GET_ARTISTS);
        }

        public YApi.Request<Bitmap> getSmallCover(Artist artist) {
            return new YApi.BitmapRequest(artist.cover.urlSmall);
        }

        public YApi.Request<Bitmap> getBigCover(Artist artist) {
            return new YApi.BitmapRequest(artist.cover.urlBig);
        }
    }
}
