package com.manna.opengl.render.triangle;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * 等腰三角形
 */
public class TriangleBoth {
    //顶点buffer
    private FloatBuffer vertexBuffer, colorBuffer;
    //声明顶点着色器代码
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;" +
                    "varying  vec4 vColor;" +
                    "attribute vec4 aColor;" +
                    "void main() {" +
                    "  gl_Position = vMatrix*vPosition;" +
                    "  vColor=aColor;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    //声明OpenGL程序
    private int mProgram;
    //坐标数组中每个顶点的坐标数
    private int coordinateVertex = 3;
    //三角坐标
    float[] triangleCoordinate = {
            0.5f, 0.5f, 0.0f, // top
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f};//right

    //位置句柄
    private int mPositionHandle;
    //颜色句柄
    private int mColorHandle;
    //顶点个数
    private int vertexCount = triangleCoordinate.length / coordinateVertex;
    //顶点之间的偏移量--每个顶点四个字节
    private int vertexStride = coordinateVertex * 4;
    //设置颜色,rgba
    float color[] = {
            0.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f
    };

    private float[] mMVPMatrix;

    private int mMatrixHandle;

    public TriangleBoth(float[] mMVPMatrix) {
        this.mMVPMatrix = mMVPMatrix;

        //顶点
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(triangleCoordinate.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(triangleCoordinate);
        vertexBuffer.position(0);

        //颜色值
        ByteBuffer colorByteBuffer = ByteBuffer.allocateDirect(color.length * 4);
        colorByteBuffer.order(ByteOrder.nativeOrder());
        colorBuffer = colorByteBuffer.asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.position(0);

        //shader
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        //创建OpenGL 程序
        mProgram = GLES20.glCreateProgram();
        //将着色器加入到程序
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        //连接Program
        GLES20.glLinkProgram(mProgram);
    }

    public void drawFrame() {
        //使用OpenGL
        GLES20.glUseProgram(mProgram);
        //获取变换矩阵vMatrix成员句柄
        mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        //指定Matrix的值
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);

        //获取顶点vPosition成员句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        //启动三角形顶点句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        //添加三角形坐标
        GLES20.glVertexAttribPointer(mPositionHandle, coordinateVertex, GLES20.GL_FLOAT,
                false, vertexStride, vertexBuffer);

        //获取vColor成员句柄
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        //启用颜色句柄
        GLES20.glEnableVertexAttribArray(mColorHandle);
        //设置三角形颜色
        GLES20.glVertexAttribPointer(mColorHandle, 4,
                GLES20.GL_FLOAT, false,
                0, colorBuffer);

        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        //禁止顶点句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle);
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
