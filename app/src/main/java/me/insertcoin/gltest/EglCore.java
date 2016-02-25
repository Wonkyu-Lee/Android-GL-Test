package me.insertcoin.gltest;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.view.Surface;

import javax.microedition.khronos.egl.EGL11;

/**
 * Created by blazeq on 16. 2. 24..
 */
public class EglCore {
    private static final int EGL_RECORDABLE_ANDROID = 0x3142;

    public EGLDisplay display = EGL14.EGL_NO_DISPLAY;
    public EGLContext context = EGL14.EGL_NO_CONTEXT;
    public EGLConfig config = null;

    public void initialize() {
        EGLDisplay display;
        EGLConfig config;
        EGLContext context;

        // get display
        display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);

        // initialize display
        int[] version = new int[2];
        EGL14.eglInitialize(display, version, 0, version, 1);

        // get config
        {
            int[] attributes = {
                    EGL14.EGL_RED_SIZE, 8,
                    EGL14.EGL_GREEN_SIZE, 8,
                    EGL14.EGL_BLUE_SIZE, 8,
                    EGL14.EGL_ALPHA_SIZE, 8,
                    EGL14.EGL_DEPTH_SIZE, 16,
                    EGL14.EGL_STENCIL_SIZE, 8,
                    EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                    EGL_RECORDABLE_ANDROID, 1,
                    EGL14.EGL_NONE
            };

            EGLConfig[] configs = new EGLConfig[1];
            int[] numConfig = new int[1];

            EGL14.eglChooseConfig(display, attributes, 0, configs, 0, configs.length, numConfig, 0);
            config = configs[0];
        }

        // create context
        {
            int[] attributes = {
                    EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                    EGL14.EGL_NONE
            };

            context = EGL14.eglCreateContext(display, config, EGL14.EGL_NO_CONTEXT, attributes, 0);
        }

        this.display = display;
        this.context = context;
        this.config = config;
    }

    public void release() {
        if (display != EGL14.EGL_NO_DISPLAY) {
            EGL14.eglMakeCurrent(display, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
            EGL14.eglDestroyContext(display, context);
            EGL14.eglReleaseThread();
            EGL14.eglTerminate(display);
        }

        display = EGL14.EGL_NO_DISPLAY;
        context = EGL14.EGL_NO_CONTEXT;
        config = null;
    }

    public EGLSurface createWindowSurface(SurfaceTexture surfaceTexture) {
        int[] attributes = {
                EGL14.EGL_NONE
        };

        return EGL14.eglCreateWindowSurface(display, config, surfaceTexture, attributes, 0);
    }

    public void releaseSurface(EGLSurface eglSurface) {
        EGL14.eglDestroySurface(display, eglSurface);
    }

    public void makeCurrent(EGLSurface eglSurface) {
        EGL14.eglMakeCurrent(display, eglSurface, eglSurface, context);
    }

    public boolean swapBuffers(EGLSurface eglSurface) {
        return EGL14.eglSwapBuffers(display, eglSurface);
    }

    @Override
    protected void finalize() throws Throwable {
        release();
        super.finalize();
    }
}
