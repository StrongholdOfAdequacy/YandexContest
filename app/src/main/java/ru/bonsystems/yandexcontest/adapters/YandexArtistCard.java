package ru.bonsystems.yandexcontest.adapters;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import ru.bonsystems.yandexcontest.Controller;
import ru.bonsystems.yandexcontest.R;
import ru.bonsystems.yandexcontest.data.Artist;
import ru.bonsystems.yandexcontest.internet.YApi;
import ru.bonsystems.yandexcontest.internet.YApiMethods;

/**
 * Created by Kolomeytsev Anton on 22.03.2016.
 * This class is a part of YandexContestApp project.
 *
 * Класс подробных данных, каждый экземпляр которого соответствует каждому viewHolder'у исполнителя.
 * В этом классе также реализована логика обновления viewHolder на основе хранящихся тут данных.
 */
public class YandexArtistCard {
    // картинка пустой фотографии
    private static Drawable photoDummy = Controller.getInstance().getResources().getDrawable(R.drawable.photo_dummy);
    // вся инфомация по артисту
    private Artist artist;
    private ThumbnailLoadingListener thumbnailLoadingListener;

    public YandexArtistCard(JSONObject jsonCardDescription) throws JSONException {
        this.artist = new Artist(jsonCardDescription);
    }

    private void loadThumbnail() {
        YApiMethods.artist.getSmallCover(artist)
                .setSuccessListener(new YApi.SuccessListener<Bitmap>() {

                    @Override
                    public void onSuccess(Bitmap response) {
                        artist.cover.small = new BitmapDrawable(Controller.getInstance().getResources(), response);
                        AppCompatActivity activity = Controller.getInstance().getActivity();
                        if (activity != null) activity.runOnUiThread(new Runnable() {
                            @Override public void run() {
                                if (thumbnailLoadingListener != null)
                                    thumbnailLoadingListener.onThumbnailLoaded(artist.cover.small);
                            }
                        });
                    }
                })
                .executeAsync(); // запрос произойдет в новом потоке
    }

    public void updateViewHolder(YandexArtistAdapter.ViewHolder viewHolder) {
        viewHolder.setArtistCard(this);
        viewHolder.name.setText(artist.name);
        viewHolder.stats.setText(artist.getStats());
        viewHolder.genres.setText(artist.getGenres());
        if (artist.cover.small == null) {
            viewHolder.onThumbnailLoaded(photoDummy);
            this.thumbnailLoadingListener = viewHolder;
            loadThumbnail();
        } else {
            viewHolder.onThumbnailLoaded(artist.cover.small);
        }
    }

    public void onViewRecycled() {
        thumbnailLoadingListener = null;
    }

    public Artist getArtist() {
        return artist;
    }

    public interface ThumbnailLoadingListener {
        void onThumbnailLoaded(Drawable drawable);
    }
}
