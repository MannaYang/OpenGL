package com.manna.opengl.render.cube;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * 索引法绘制正方体
 */
public class Cube {
    private FloatBuffer vertexBuffer, colorBuffer;
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
    private String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;" +
                    "varying vec4 vColor;" +
                    "attribute vec4 aColor;" +
                    "void main() {" +
                    "  gl_Position = vMatrix*vPosition;" +
                    "  vColor = aColor;" +
                    "}";
    private String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 vColor;" +
                    "void main() {" +
                    "gl_FragColor = vColor;" +
                    "}";

    private int mProgram;
    //坐标数组中每个顶点的坐标数
    private int coordinateVertex = 3;
    private int mPositionHandle;
    private int mColorHandle;

    private float[] mMVPMatrix;

    private int mMatrixHandle;
    private int vertexStride = 0;

    //正方体8个顶点
    private float[] cubePoints = {
            -1.0f, 1.0f, 1.0f,    //正面左上0
            -1.0f, -1.0f, 1.0f,   //正面左下1
            1.0f, -1.0f, 1.0f,    //正面右下2
            1.0f, 1.0f, 1.0f,     //正面右上3
            -1.0f, 1.0f, -1.0f,    //反面左上4
            -1.0f, -1.0f, -1.0f,   //反面左下5
            1.0f, -1.0f, -1.0f,    //反面右下6
            1.0f, 1.0f, -1.0f    //反面右上7
    };

    private short[] cubeIndex = {
            6, 7, 4, 6, 4, 5,    //后面
            6, 3, 7, 6, 2, 3,    //右面
            6, 5, 1, 6, 1, 2,    //下面
            0, 3, 2, 0, 2, 1,    //正面
            0, 1, 5, 0, 5, 4,    //左面
            0, 7, 3, 0, 4, 7    //上面
    };

    //正方体8个顶点颜色
    private float[] cubeColor = {
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            0.63671875f, 0.76953125f, 0.22265625f, 1.0f,
            0.63671875f, 0.76953125f, 0.22265625f, 1.0f,
            0.63671875f, 0.76953125f, 0.22265625f, 1.0f,
            0.63671875f, 0.76953125f, 0.22265625f, 1.0f,
    };

    public Cube(float[] mMVPMatrix) {

        this.mMVPMatrix = mMVPMatrix;

        //顶点
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(cubePoints.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(cubePoints);
        vertexBuffer.position(0);
        //颜色
        ByteBuffer colorByteBuffer = ByteBuffer.allocateDirect(cubeColor.length * 4);
        colorByteBuffer.order(ByteOrder.nativeOrder());
        colorBuffer = colorByteBuffer.asFloatBuffer();
        colorBuffer.put(cubeColor);
        colorBuffer.position(0);
        //索引
        ByteBuffer indexByteBuffer = ByteBuffer.allocateDirect(cubeIndex.length * 2);
        indexByteBuffer.order(ByteOrder.nativeOrder());
        indexBuffer = indexByteBuffer.asShortBuffer();
        indexBuffer.put(cubeIndex);
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

        mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                0, vertexBuffer);

        mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false,
                0, colorBuffer);

        //索引绘制
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, cubeIndex.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
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
