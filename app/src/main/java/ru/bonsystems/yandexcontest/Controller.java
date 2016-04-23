package ru.bonsystems.yandexcontest;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Kolomeytsev Anton on 06.04.2016.
 * This class is a part of YandexContestApp project.
 */
public class Controller extends Application {
    private static Controller instance;
    private AppCompatActivity activity;
    private LowMemoryListener lowMemoryListener;

    public static Controller getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @Override
    public void onLowMemory() {
        clearMemory();
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        clearMemory();
        super.onTrimMemory(level);
    }

    private void clearMemory() {
        System.gc();
        if (lowMemoryListener != null) {
            lowMemoryListener.onLowMemory();
            System.gc();
        }
    }

    public AppCompatActivity getActivity() {
        return activity;
    }

    public Controller setActivity(AppCompatActivity activity) {
        this.activity = activity;
        return this;
    }

    public Controller setLowMemoryListener(LowMemoryListener lowMemoryListener) {
        this.lowMemoryListener = lowMemoryListener;
        return this;
    }

    public interface LowMemoryListener {
        void onLowMemory();
    }
}