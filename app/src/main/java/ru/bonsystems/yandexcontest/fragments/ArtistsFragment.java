package ru.bonsystems.yandexcontest.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;

import ru.bonsystems.yandexcontest.R;
import ru.bonsystems.yandexcontest.adapters.YandexArtistAdapter;
import ru.bonsystems.yandexcontest.data.Artist;
import ru.bonsystems.yandexcontest.internet.YApi;
import ru.bonsystems.yandexcontest.internet.YApiMethods;
import ru.bonsystems.yandexcontest.widgets.AutofitRecyclerView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArtistsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ArtistsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArtistsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, YandexArtistAdapter.ArtistSelectionListener {
    private OnFragmentInteractionListener mListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AutofitRecyclerView recyclerView;
    private YandexArtistAdapter adapter;
    private LinearLayout connectionErrorLayout;

    public ArtistsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ArtistsFragment.
     */
    public static ArtistsFragment newInstance() {
        return new ArtistsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View self = inflater.inflate(R.layout.fragment_artists, container, false);
        customizeUI(self);
        setUIState(true);
        return self;
    }

    private void customizeUI(View self) {
        connectionErrorLayout = ((LinearLayout) self.findViewById(R.id.artists_connection_error_layout));
        self.findViewById(R.id.artists_connection_try_button).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { onRefresh(); }
        });
        swipeRefreshLayout = ((SwipeRefreshLayout) self.findViewById(R.id.artists_swipe_refresh_layout));
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = ((AutofitRecyclerView) self.findViewById(R.id.artists_recycler_view));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.setNestedScrollingEnabled(true);
        if (adapter == null) {
            adapter = new YandexArtistAdapter();
            adapter.setOnClickListener(this);
        }
        recyclerView.setAdapter(adapter);
        if (adapter.getItemCount() == 0) refreshArtistsList();
    }

    /**
     * Слушатель удачной загрузки списка исполнителей
     */
    private YApi.SuccessListener<String> onSuccess = new YApi.SuccessListener<String>() {
        @Override
        public void onSuccess(String response) {
            try {
                adapter.createCards(new JSONArray(response));
                // чтобы обновить recyclerView, надо заново присвоить ему тот же самый адаптер
                getActivity().runOnUiThread(new Runnable() {
                    @Override public void run() { recyclerView.setAdapter(adapter); }
                });
            } catch (JSONException e) {
                e.printStackTrace();
                onFail.onFail();
            } finally {
                swipeRefreshLayout.post(new Runnable() {
                    @Override public void run() { swipeRefreshLayout.setRefreshing(false); }
                });
            }
        }
    };

    /**
     * Слушатель неудачной попытки загрузить список исполнителей
     */
    private YApi.FailListener onFail = new YApi.FailListener() {
        @Override
        public void onFail() {
            swipeRefreshLayout.post(new Runnable() {
                @Override public void run() { swipeRefreshLayout.setRefreshing(false); }
            });
            if (adapter.getItemCount() == 0) setUIState(false);
        }
    };

    /**
     * Показываем, либо скрываем кнопку повторной попытки подключения
     * @param state true, для того чтобы срыть кнопку повторного подключения, false - чтобы показать
     */
    private void setUIState(final boolean state) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!state) {
                    recyclerView.setVisibility(View.GONE);
                    connectionErrorLayout.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() { swipeRefreshLayout.setRefreshing(false); }
                    });
                } else {
                    connectionErrorLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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

    /**
     * Ставим свой кастомный тулбар
     */
    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            Toolbar toolbar = (Toolbar) activity.findViewById(R.id.base_toolbar);
            if (toolbar != null) {
                toolbar.setTitle(R.string.label_artists);
                activity.setSupportActionBar(toolbar);
            }
        }
    }

    /**
     * Происходит по свайпу вниз (потяни, чтобы обновить)
     */
    @Override
    public void onRefresh() {
        refreshArtistsList();
    }

    private void refreshArtistsList() {
        // даёшь Java 8 и лямбда выражения!!!
        setUIState(true);
        swipeRefreshLayout.post(new Runnable() {
            @Override public void run() { swipeRefreshLayout.setRefreshing(true); }
        });
        YApiMethods.artist.get()
                .setSuccessListener(onSuccess)
                .setFailListener(onFail)
                .executeAsync();
    }

    @Override
    public void onArtistSelect(Artist artist) {
        if (mListener != null)
            mListener.onArtistSelect(artist);
    }

    public interface OnFragmentInteractionListener {
        void onArtistSelect(Artist artist);
    }
}
