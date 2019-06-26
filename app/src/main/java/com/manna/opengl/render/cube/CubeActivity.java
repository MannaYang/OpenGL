package com.manna.opengl.render.cube;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.manna.opengl.R;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 绘制正方体
 */
public class CubeActivity extends AppCompatActivity {

    GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triangle);

        glSurfaceView = findViewById(R.id.gl_view);

        drawCube();
    }

    private void drawCube() {
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(new GLRender());
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public class GLRender implements GLSurfaceView.Renderer {

        float[] mViewMatrix = new float[16];
        float[] mProjectMatrix = new float[16];
        float[] mMVPMatrix = new float[16];

        Cube cube;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            cube = new Cube(mMVPMatrix);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);

            float ratio = (float) width / height;
            Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
            Matrix.setLookAtM(mViewMatrix, 0, 5.0f, 5.0f, 10.0f,
                    0f, 0f, 0f, 0f, 1f, 0f);
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            cube.drawFrame();
        }
    }
}
