package com.manna.opengl.render.square;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * 绘制正方形
 */
public class Square {
    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;

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

    private float[] triangleCoordinate = {
            -0.5f, 0.5f, 0.0f, // top left
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f, // bottom right
            0.5f, 0.5f, 0.0f};
    //坐标数组中每个顶点的坐标数
    private int coordinateVertex = 3;

    private short index[] = {
            0, 1, 2, 0, 2, 3
    };

    private int mPositionHandle;
    private int mColorHandle;

    //顶点个数
    private int vertexCount = triangleCoordinate.length / coordinateVertex;
    //顶点之间的偏移量--每个顶点四个字节
    private int vertexStride = coordinateVertex * 4;

    //矩阵句柄
    private int mMatrixHandle;

    //设置颜色,rgba
    float[] color = {0.63671875f, 0.76953125f, 0.22265625f, 1.0f};

    private float[] mMVPMatrix;

    public Square(float[] mMVPMatrix) {

        this.mMVPMatrix = mMVPMatrix;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(triangleCoordinate.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(triangleCoordinate);
        vertexBuffer.position(0);

        ByteBuffer indexByteBufffer = ByteBuffer.allocateDirect(index.length * 2);
        indexByteBufffer.order(ByteOrder.nativeOrder());
        indexBuffer = indexByteBufffer.asShortBuffer();
        indexBuffer.put(index);
        indexBuffer.position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    public void drawFrame() {
        GLES20.glUseProgram(mProgram);

        //获取矩阵句柄
        mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);

        //获取顶点句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, coordinateVertex, GLES20.GL_FLOAT,
                false, vertexStride, vertexBuffer);

        //片元着色器
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        //索引法绘制
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, index.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
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
