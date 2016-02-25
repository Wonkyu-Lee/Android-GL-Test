package me.insertcoin.gltest;

import android.graphics.SurfaceTexture;
import android.opengl.EGLSurface;

/**
 * Created by blazeq on 16. 2. 24..
 */
public class EglWindowSurface {
    private final EglCore mCore;
    private EGLSurface mSurface = null;

    public EglWindowSurface(EglCore core) {
        mCore = core;
    }

    public void initialize(SurfaceTexture surfaceTexture) {
        release();
        mSurface = mCore.createWindowSurface(surfaceTexture);
    }

    public void release() {
        if (mSurface != null) {
            mCore.releaseSurface(mSurface);
            mSurface = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        release();
        super.finalize();
    }

    public void makeCurrent() {
        if (mSurface == null)
            return;

        mCore.makeCurrent(mSurface);
    }

    public boolean swapBuffers() {
        if (mSurface == null)
            return false;

        return mCore.swapBuffers(mSurface);
    }
}