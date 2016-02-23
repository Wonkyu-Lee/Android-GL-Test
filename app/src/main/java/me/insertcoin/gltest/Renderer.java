package me.insertcoin.gltest;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by blazeq on 16. 2. 19..
 */


// TODO: EGLContext를 만들어두고 계속 사용할 수 있는지 보자. 중간에 release/restore가 필요하다면 어떻게 할까?
    // Application은 아주 오랜동안 종료가 안될 수도 있기 때문에 EGLContext를 갖고 있는 건 무리다. 필요할 때마다 없으면 만들고 있으면 쓰는게 맞다?
    // Renderer에 세션? 느낌으로 만들어 쓰고 그걸 반환하도록. 궁금한 것은 background에서 gl drawing이 가능할지.
public class Renderer {
    private RendererHandler mHandler;
    private final Object mStateFence = new Object();

    private enum State {
        NOT_READY,
        RUNNING,
        STOPPING,
    }

    private State mState = State.NOT_READY;

    public void start() {
        synchronized (mStateFence) {
            if (mState != State.NOT_READY)
                return;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    initializeAndRun();
                }
            }).start();

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
            mState = State.RUNNING;
            mStateFence.notify();
        }

        Looper.loop();

        synchronized (mStateFence) {
            mHandler = null;
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
}