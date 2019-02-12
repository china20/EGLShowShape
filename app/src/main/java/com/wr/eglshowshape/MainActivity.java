package com.wr.eglshowshape;

import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.TextureView;

import com.wr.external.gl.GlRunner;
import com.wr.external.gl.WrGlCore;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Wr:MainActivity";

    TextureView mTexView;
    GLSurfaceView mGlsfView;
    GlRunner mGlRunner;
    WrShapeRender mGlRender;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTexView = findViewById(R.id.texView);
        mGlsfView = findViewById(R.id.glsView);

        mTexView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                drawTriangle();
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

    private void drawByGLSurfaceView() {
        mGlsfView.setEGLContextClientVersion(2);
        mGlsfView.setRenderer(new WrGlViewRender());
        mGlsfView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private void drawTriangle() {
        mGlRunner = new GlRunner();
        mGlRender = new WrShapeRender(mTexView.getSurfaceTexture());
        mGlRunner.setRender(mGlRender);
        mGlRunner.init(WrGlCore.NO_CONTEXT);
        mGlRunner.start();
    }
}
