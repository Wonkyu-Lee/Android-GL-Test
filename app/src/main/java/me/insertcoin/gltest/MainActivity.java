package me.insertcoin.gltest;

import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Renderer mRenderer;
    private GlView[] mGlViews = new GlView[2];

    class GlView {
        private final int[] COLORS = { Color.RED, Color.GREEN, Color.BLUE };
        private final TextureView mView;
        private int mColorIndex = 0;
        private EglWindowSurface mEglWindowSurface;

        GlView(int resId) {
            mView = (TextureView)findViewById(resId);
            mView.setOpaque(false);

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mColorIndex = (mColorIndex + 1) % COLORS.length;
                    draw();
                }
            });

            mView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    mEglWindowSurface = mRenderer.createWindowSurface(surface);
                    draw();
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    mEglWindowSurface.release();
                    mEglWindowSurface = null;
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                }
            });
        }

        void draw() {
            mRenderer.queueEvent(new Runnable() {
                @Override
                public void run() {
                    if (mEglWindowSurface == null)
                        return;

                    mEglWindowSurface.makeCurrent();
                    int color = COLORS[mColorIndex];
                    float r = Color.red(color) / 255.0f;
                    float g = Color.green(color) / 255.0f;
                    float b = Color.blue(color) / 255.0f;
                    float a = Color.alpha(color) / 255.0f;
                    GLES20.glClearColor(r, g, b, a * 0.5f);
                    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                    mEglWindowSurface.swapBuffers();
                }
            });
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
}
