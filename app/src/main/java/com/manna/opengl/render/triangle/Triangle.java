package com.manna.opengl.render.triangle;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * 三角形
 */
public class Triangle {
    //顶点buffer
    private FloatBuffer vertexBuffer;
    //声明顶点着色器代码
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    //三角坐标
//    private float[] triangleCoordinate = {
//            0.0f, 0.0f, 0.0f,//top
//            -1.0f, -1.0f, 0.0f,//left down
//            1.0f, -1.0f, 0.0f//right down
//    };
    float[] triangleCoordinate = {0.5f,  0.5f, 0.0f, // top
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f};//right
    //坐标数组中每个顶点的坐标数
    private int coordinateVertex = 3;

    //OpenGLES程序
    private int mProgram;
    //位置句柄
    private int mPositionHandle;
    //颜色句柄
    private int mColorHandle;
    //顶点个数
    private int vertexCount = triangleCoordinate.length / coordinateVertex;
    //顶点之间的偏移量--每个顶点四个字节
    private int vertexStride = coordinateVertex * 4;
    //设置颜色,rgba
    float[] color = {0.63671875f, 0.76953125f, 0.22265625f, 1.0f};

    public Triangle() {
        //每个浮点数:坐标个数* 4字节
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(triangleCoordinate.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        //实例化
        vertexBuffer = byteBuffer.asFloatBuffer();
        // 将坐标添加到FloatBuffer
        vertexBuffer.put(triangleCoordinate);
        //设置缓冲区以读取第一个坐标
        vertexBuffer.position(0);

        //获取顶点、片元着色变量
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        //创建OpenGL程序
        mProgram = GLES20.glCreateProgram();
        //加入顶点着色器
        GLES20.glAttachShader(mProgram, vertexShader);
        //加入片元着色器
        GLES20.glAttachShader(mProgram, fragmentShader);
        //连接Program
        GLES20.glLinkProgram(mProgram);
    }

    public void drawFrame() {
        //使用OpenGL
        GLES20.glUseProgram(mProgram);
        //获取顶点着色器vPosition属性句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        //启用vPosition句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        //设置三角坐标
        GLES20.glVertexAttribPointer(mPositionHandle, coordinateVertex, GLES20.GL_FLOAT,
                false, vertexStride, vertexBuffer);
        //获取vColor属性句柄
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        //设置三角形颜色
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        //禁止顶点数组坐标句柄
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
