package com.manna.opengl.render.cone;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 绘制圆锥
 */
public class ConeRender implements GLSurfaceView.Renderer {

    private FloatBuffer vertexBuffer, colorBuffer;

    /**
     * attribute：各个顶点不同的量
     * vec4     ：vector 表示向量，vec4表示4维向量
     * uniform  ：表示3D物体中顶点相同的量，例如光源位置、统一变换矩阵
     * mat4     : Matrix矩阵，mat4表示4*4矩阵
     * varying  ：顶点着色器传递到片元着色器的量
     * precision：精度，用作申明
     * mediump  : 浮点精度，lowp：低精度，8位；mediump：中精度，10位；highp：高精度，16位
     * gl_Position ：顶点着色器内建变量，表示顶点坐标(gl_PointSize，表示点的大小，默认为1)
     * gl_FragColor ：片元着色器内建变量,表示当前片元颜色
     * gl_FragCoord：当前片元相对窗口位置所处的坐标
     * gl_FragFacing：bool型，表示是否为属于光栅化生成此片元的对应图元的正面
     * gl_FragData：vec4类型的数组,向其写入的信息，供渲染管线的后继过程使用
     */
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;" +
                    "void main() {" +
                    "  gl_Position = vMatrix*vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private int mProgram;
    //坐标数组中每个顶点的坐标数
    private int coordinateVertex = 3;
    private int mPositionHandle;
    private int mMatrixHandle;
    private int mColorHandle;
    private int vertexStride = 0;

    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    //设置颜色,rgba
    float[] color = {0.63671875f, 0.76953125f, 0.22265625f, 1.0f};

    private float radius = 1.0f;
    private int nSide = 360;
    private float[] ovalPosition;


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        ovalPosition = getOvalPoints();

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(ovalPosition.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(ovalPosition);
        vertexBuffer.position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
        Matrix.setLookAtM(mViewMatrix, 0, 1.0f, -10.0f, -4.0f,
                0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);

        mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, coordinateVertex, GLES20.GL_FLOAT,
                false, vertexStride, vertexBuffer);

        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, ovalPosition.length / 3);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    private float[] getOvalPoints() {
        List<Float> data = new ArrayList<>();
        data.add(0.0f);
        data.add(0.0f);
        data.add(2.0f);//设置圆锥高度

        float angle = 360f / nSide;
        for (int i = 0; i < 360 + angle; i += angle) {
            data.add((float) (radius * Math.sin(i * Math.PI / 180f)));
            data.add((float) (radius * Math.cos(i * Math.PI / 180f)));
            data.add(0.0f);
        }
        float[] pointData = new float[data.size()];
        for (int j = 0; j < pointData.length; j++) {
            pointData[j] = data.get(j);
        }
        return pointData;
    }

    private int loadShader(int type, String shaderCode) {
        //根据type创建顶点着色器或者片元着色器
        int shader = GLES20.glCreateShader(type);
        //将资源加入到着色器中，并编译
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}
