package me.insertcoin.gltest;

import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by blazeq on 16. 2. 19..
 */
public class Renderer {
    private RendererHandler mHandler;
    private final Object mStateFence = new Object();
    private State mState = State.NOT_READY;
    private EglCore mEglCore;

    private enum State {
        NOT_READY,
        RUNNING,
        STOPPING,
    }

    public void start() {
        synchronized (mStateFence) {
            if (mState != State.NOT_READY)
                return;

            new Thread() {
                @Override
                public void run() {
                    initializeAndRun();
                }
            }.start();

            while (mState != State.RUNNING) {
                try {
                    mStateFence.wait();
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
    }

    private void initializeAndRun() {
        Looper.prepare();

        synchronized (mStateFence) {
            mHandler = new RendererHandler();
            initializeEgl();
            mState = State.RUNNING;
            mStateFence.notify();
        }

        Looper.loop();

        synchronized (mStateFence) {
            mHandler = null;
            releaseEgl();
            mState = State.NOT_READY;
        }
    }

    public void stop() {
        synchronized (mStateFence) {
            if (mState != State.RUNNING)
                return;

            mState = State.STOPPING;
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Looper.myLooper().quit();
            }
        });
    }

    public void queueEvent(Runnable r) {
        synchronized (mStateFence) {
            if (mState != State.RUNNING)
                return;
        }

        mHandler.post(r);
    }

    public class RendererHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    private void initializeEgl() {
        mEglCore = new EglCore();
        mEglCore.initialize();
    }

    private void releaseEgl() {
        mEglCore.release();
        mEglCore = null;
    }

    public EglWindowSurface createWindowSurface(SurfaceTexture surfaceTexture) {
        synchronized (mStateFence) {
            if (mState != State.RUNNING)
                return null;

            EglWindowSurface surface = new EglWindowSurface(mEglCore);
            surface.initialize(surfaceTexture);
            return surface;
        }
    }
}