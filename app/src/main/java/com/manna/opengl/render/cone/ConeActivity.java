package com.manna.opengl.render.cone;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.manna.opengl.R;

/**
 * 绘制圆锥
 */
public class ConeActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triangle);

        glSurfaceView = findViewById(R.id.gl_view);

        drawCone();
    }

    private void drawCone() {
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(new ConeRender());
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
