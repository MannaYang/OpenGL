package com.manna.opengl.render.oval;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.v7.app.AppCompatActivity;

import com.manna.opengl.R;
import com.manna.opengl.render.square.SquareActivity;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Matrix矩阵绘制圆形
 */
public class OvalActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triangle);

        glSurfaceView = findViewById(R.id.gl_view);

        drawOval();
    }

    //添加render
    private void drawOval() {
        glSurfaceView.setEGLContextClientVersion(2);//设置OpenGL ES 2.0 context
        glSurfaceView.setRenderer(new GLRender());
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public class GLRender implements GLSurfaceView.Renderer {
        Oval oval;

        float[] mViewMatrix = new float[16];
        float[] mProjectMatrix = new float[16];
        float[] mMVPMatrix = new float[16];

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            oval = new Oval(mMVPMatrix);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);

            float ratio = (float) width / height;
            Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
            Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 7.0f, 0f, 0f,
                    0f, 0f, 1.0f, 0f);
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            oval.drawFrame();
        }
    }
}
