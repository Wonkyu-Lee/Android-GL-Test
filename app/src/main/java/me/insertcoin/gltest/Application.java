package me.insertcoin.gltest;

import android.util.Log;

/**
 * Created by blazeq on 16. 2. 23..
 */
public class Application extends android.app.Application {
    private static final String TAG = Application.class.getSimpleName();
    private final Renderer mRenderer = new Renderer();

    @Override
    public void onCreate() {
        super.onCreate();
        mRenderer.start();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mRenderer.stop();
    }

    public Renderer getRenderer() {
        return mRenderer;
    }
}