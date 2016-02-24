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
        mSurface = mCore.createWindowSurface(surfaceTexture);
    }

    public void release() {
        mCore.releaseSurface(mSurface);
        mSurface = null;
    }

    public void makeCurrent() {
        mCore.makeCurrent(mSurface);
    }

    public boolean swapBuffers() {
        return mCore.swapBuffers(mSurface);
    }
}
