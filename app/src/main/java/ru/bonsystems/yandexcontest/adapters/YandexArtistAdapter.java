package ru.bonsystems.yandexcontest.adapters;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import ru.bonsystems.yandexcontest.Controller;
import ru.bonsystems.yandexcontest.R;
import ru.bonsystems.yandexcontest.data.Artist;
import ru.bonsystems.yandexcontest.widgets.ExtendedTextView;


/**
 * Created by Kolomeytsev Anton on 22.03.2016.
 * This class is a part of YandexContestApp project.
 *
 * Адаптер милых плиточек с исполнителями. Помимо viewHolder'ов для каждого исполнителя,
 * существует еще и экземпляр подробных данных для него (YandexArtistCard).
 */
public class YandexArtistAdapter extends RecyclerView.Adapter<YandexArtistAdapter.ViewHolder> implements Controller.LowMemoryListener {
    private final ArrayList<YandexArtistCard> cards = new ArrayList<>();
    private ArtistSelectionListener onClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements YandexArtistCard.ThumbnailLoadingListener, View.OnClickListener {
        public CardView container;
        public ExtendedTextView name;
        public ExtendedTextView genres;
        public ExtendedTextView stats;
        public ImageView thumbnail;
        private YandexArtistCard artistCard;

        public ViewHolder(View itemView) {
            super(itemView);
            container = (CardView) itemView.findViewById(R.id.artist_cardview);
            name = (ExtendedTextView) itemView.findViewById(R.id.artist_name);
            genres = (ExtendedTextView) itemView.findViewById(R.id.artist_genres);
            stats = (ExtendedTextView) itemView.findViewById(R.id.artist_stats);
            thumbnail = (ImageView) itemView.findViewById(R.id.artist_thumbnail);
            container.setOnClickListener(this);
        }

        @Override
        public void onThumbnailLoaded(final Drawable img) {
            AppCompatActivity activity = Controller.getInstance().getActivity();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override public void run() { thumbnail.setImageDrawable(img); }
                });
            }
        }

        public void setArtistCard(YandexArtistCard artistCard) {
            this.artistCard = artistCard;
        }

        @Override
        public void onClick(View v) {
            if (onClickListener != null)
                onClickListener.onArtistSelect(artistCard.getArtist());
        }
    }

    public YandexArtistAdapter() {
        Controller.getInstance().setLowMemoryListener(this);
    }

    public void createCards(JSONArray jsonArray) throws JSONException, IllegalArgumentException {
        if (jsonArray.length() == 0) throw new IllegalArgumentException("Artists count equals 0");
        cards.clear();
        for (int i = 0, ie = jsonArray.length(); i < ie; i++)
            cards.add(new YandexArtistCard(jsonArray.getJSONObject(i)));
    }

    public void setOnClickListener(ArtistSelectionListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.artist_card_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cards.get(position).updateViewHolder(holder);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.artistCard.onViewRecycled();
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }


    @Override
    public void onLowMemory() {
        Log.v("ARTIST ADAPTER", "Clear photo cache...");
        for (YandexArtistCard card : cards) {
            card.getArtist().cover.big = null;
        }
    }

    public interface ArtistSelectionListener {
        void onArtistSelect(Artist artist);
    }
}
