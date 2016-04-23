package ru.bonsystems.yandexcontest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import ru.bonsystems.yandexcontest.data.Artist;
import ru.bonsystems.yandexcontest.fragments.ArtistDetailsFragment;
import ru.bonsystems.yandexcontest.fragments.ArtistsFragment;

public class MainActivity extends AppCompatActivity implements
        ArtistsFragment.OnFragmentInteractionListener,
        ArtistDetailsFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(new Runnable() {
            @Override public void run() { createUI(); }
        }).start();
    }

    private void createUI() {
        if (getCurrentFragment() == null) {
            loadFragment(ArtistsFragment.newInstance());
        }
    }

    private void loadFragment(final Fragment fragment) {
        fragment.setRetainInstance(true);
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName());
        if (!(fragment instanceof ArtistsFragment)) {
            transaction.addToBackStack(null);
        }
        //getSupportFragmentManager().executePendingTransactions();
        transaction.commit();
    }

    /**
     * @return Ссылку на фрагмент, который сейчас виден на экране
     */
    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Controller.getInstance().setActivity(this);
    }

    @Override
    protected void onPause() {
        Controller.getInstance().setActivity(null);
        super.onPause();
    }

    @Override
    public void onArtistSelect(Artist artist) {
        System.out.println(artist);
        loadFragment(ArtistDetailsFragment.newInstance(artist));
    }
}
