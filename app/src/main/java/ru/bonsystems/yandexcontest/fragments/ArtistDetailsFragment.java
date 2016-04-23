package ru.bonsystems.yandexcontest.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ru.bonsystems.yandexcontest.Controller;
import ru.bonsystems.yandexcontest.R;
import ru.bonsystems.yandexcontest.data.Artist;
import ru.bonsystems.yandexcontest.internet.YApi;
import ru.bonsystems.yandexcontest.internet.YApiMethods;
import ru.bonsystems.yandexcontest.widgets.ExtendedTextView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArtistDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ArtistDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArtistDetailsFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private Artist artist;
    private ImageView coverImage;

    public ArtistDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param artist Выбранный для пожробной информации артист
     * @return A new instance of fragment ArtistDetailsFragment.
     */
    public static ArtistDetailsFragment newInstance(Artist artist) {
        ArtistDetailsFragment fragment = new ArtistDetailsFragment();
        fragment.setArtist(artist);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View self = inflater.inflate(R.layout.fragment_artist_details, container, false);
        customizeUI(self);
        return self;
    }

    private void customizeUI(View self) {
        ((ExtendedTextView) self.findViewById(R.id.artist_description)).setText(artist.description);
        ((ExtendedTextView) self.findViewById(R.id.artist_tracks)).setText(artist.getTracks());
        ((ExtendedTextView) self.findViewById(R.id.artist_albums)).setText(artist.getAlbums());
        ((ExtendedTextView) self.findViewById(R.id.artist_genres)).setText(artist.getGenres());
        View actionsLayout = (self.findViewById(R.id.artist_description_actions_layout));
        if (artist.link == null) {
            actionsLayout.setVisibility(View.GONE);
        } else {
            actionsLayout.setVisibility(View.VISIBLE);
            (self.findViewById(R.id.artist_description_goto_site)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(artist.link))); }
            });
        }
        coverImage = ((ImageView) self.findViewById(R.id.artist_cover));
        if (artist.cover.big == null) {
            coverImage.setImageDrawable(artist.cover.small);
            YApiMethods.artist.getBigCover(artist)
                    .setSuccessListener(onBigCoverLoadedListener)
                    .executeAsync();
        } else {
            coverImage.setImageDrawable(artist.cover.big);
        }
    }

    /**
     * После загрузки большого изображения исполнителя, вставим картинку в ImageView
     */
    YApi.SuccessListener<Bitmap> onBigCoverLoadedListener = new YApi.SuccessListener<Bitmap>() {
        @Override
        public void onSuccess(Bitmap response) {
            artist.cover.big = new BitmapDrawable(Controller.getInstance().getResources(), response);
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity != null) activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (coverImage != null)
                        coverImage.setImageDrawable(artist.cover.big);
                }
            });
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                customizeActionBar();
            }
        });
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            Toolbar toolbar = (Toolbar) activity.findViewById(R.id.base_toolbar);
            if (toolbar != null) {
                toolbar.setTitle(artist.name);
                activity.setSupportActionBar(toolbar);
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.onBackPressed();
                    }
                });
            }
        }
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    /**
     * Ставим свой крутой, кастомный тулбар
     */
    private void customizeActionBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(artist.name);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

    }
}
