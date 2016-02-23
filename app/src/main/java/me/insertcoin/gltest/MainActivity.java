package me.insertcoin.gltest;

import android.graphics.SurfaceTexture;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private Renderer mRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRenderer = ((Application)getApplication()).getRenderer();

        TextureView glView = (TextureView)findViewById(R.id.gl_view);
        glView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRenderer.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Renderer", "Renderer call!");
                    }
                });
            }
        });

        glView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
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
                // drawing은 queueing된다고 보자.
                // 1. make surface as current
                // 2. draw with gl
            }
        });
    }
}
