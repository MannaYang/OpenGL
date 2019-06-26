package com.manna.opengl.render.oval;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Matrix绘制圆形
 */
public class Oval {

    private FloatBuffer vertexBuffer;
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
    private int mColorHandle;

    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix;

    private int mMatrixHandle;
    private int vertexStride = 0;
    //设置颜色,rgba
    float[] color = {0.63671875f, 0.76953125f, 0.22265625f, 1.0f};

    private float radius = 1.0f;
    private int nSide = 360;
    private float[] ovalPosition;


    public Oval(float[] mMVPMatrix) {
        this.mMVPMatrix = mMVPMatrix;

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

    public void drawFrame() {
        GLES20.glUseProgram(mProgram);
        mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, coordinateVertex, GLES20.GL_FLOAT,
                false, vertexStride, vertexBuffer);

        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, ovalPosition.length / 3);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    private float[] getOvalPoints() {
        List<Float> data = new ArrayList<>();
        data.add(0.0f);
        data.add(0.0f);
        data.add(0.0f);

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
