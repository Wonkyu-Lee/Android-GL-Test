package me.insertcoin.gltest;

import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "GlTest";
    private Renderer mRenderer;
    private GlView[] mGlViews = new GlView[2];

    class GlView {
        private final int mResId;
        private int mColor = Color.GRAY;
        private final TextureView mView;
        private EglWindowSurface mEglWindowSurface;
        private Thread mAnimationThread;

        private Object mLock = new Object();
        private boolean mReady = false;

        GlView(int resId) {
            mResId = resId;
            mView = (TextureView)findViewById(resId);
            mView.setOpaque(false);

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mAnimationThread != null) {
                        try {
                            mAnimationThread.interrupt();
                            mAnimationThread.join();
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    }
                    mAnimationThread = startAnimation();
                }
            });

            mView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(final SurfaceTexture surface, int width, int height) {
                    Log.d(TAG, String.format("[TextView: %d] onSurfaceTextureAvailable()", mResId));
                    mRenderer.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            mEglWindowSurface = mRenderer.createWindowSurface(surface);
                            drawFrame();

                            synchronized (mLock) {
                                mReady = true;
                                mLock.notify();
                            }
                        }
                    });

                    synchronized (mLock) {
                        while (!mReady) {
                            try {
                                mLock.wait();
                            } catch(InterruptedException e) {
                                // ignore
                            }
                        }
                    }
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    Log.d(TAG, String.format("[TextView: %d] onSurfaceTextureDestroyed()", mResId));
                    mRenderer.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            mEglWindowSurface.release();
                            mEglWindowSurface = null;
                        }
                    });

                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                }
            });
        }

        void requestDraw() {
            mRenderer.queueEvent(new Runnable() {
                @Override
                public void run() {
                    drawFrame();
                }
            });
        }

        void drawFrame() {
            Log.d(TAG, String.format("[TextView: %d] drawFrame() start", mResId));
            if (mEglWindowSurface == null) {
                Log.d(TAG, String.format("[TextView: %d] drawFrame() exit", mResId));
                return;
            }

            mEglWindowSurface.makeCurrent();
            float[] colorf = getColorf(mColor);
            GLES20.glClearColor(colorf[0], colorf[1], colorf[2], colorf[3]);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            mEglWindowSurface.swapBuffers();
            Log.d(TAG, String.format("[TextView: %d] drawFrame() end", mResId));
        }

        Thread startAnimation() {
            final int startColor = Color.RED;
            final int endColor = Color.argb(100, 255, 255, 0);
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        int frameCount = 30;
                        long frameIntervalMSec = 1000/30; // 30 fps
                        for (int i = 0; i < frameCount; ++i) {
                            mColor = interpolate(startColor, endColor, 1.0f/frameCount * i);
                            requestDraw();
                            sleep(frameIntervalMSec);
                        }

                    } catch (InterruptedException e) {
                        // ignore
                    }
                }
            };
            thread.start();
            return thread;
        }

        int interpolate(int color1, int color2, float rate) {
            float r = (Color.red(color1) * (1 - rate) + Color.red(color2) * rate);
            float g = (Color.green(color1) * (1 - rate) + Color.green(color2) * rate);
            float b = (Color.blue(color1) * (1 - rate) + Color.blue(color2) * rate);
            float a = (Color.alpha(color1) * (1 - rate) + Color.alpha(color2) * rate);
            return Color.argb((int)a, (int)r, (int)g, (int)b);
        }

        float[] getColorf(int color) {
            float r = Color.red(color) / 255.0f;
            float g = Color.green(color) / 255.0f;
            float b = Color.blue(color) / 255.0f;
            float a = Color.alpha(color) / 255.0f;
            return new float[] { r, g, b, a };
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRenderer = ((Application) getApplication()).getRenderer();

        mGlViews[0] = new GlView(R.id.gl_view1);
        mGlViews[1] = new GlView(R.id.gl_view2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }
}
