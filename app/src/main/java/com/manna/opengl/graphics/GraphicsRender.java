package com.manna.opengl.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.manna.opengl.utils.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GraphicsRender implements GLSurfaceView.Renderer {

    private Context context;
    private int program;
    private String vertex;
    private String fragment;

    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    //顶点坐标
    private float[] vertexPosition = {
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,//右上角
            1.0f, -1.0f
    };

    //纹理坐标
    private float[] texturePosition = {
            0f, 0f,
            0f, 1.0f,
            1.0f, 0f,//右上角
            1.0f, 1.0f
    };

    //顶点buffer
    private FloatBuffer vertexBuffer;
    //纹理buffer
    private FloatBuffer textureBuffer;

    private Bitmap bitmap;

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private int glHPosition;
    private int glHTexture;
    private int glHCoordinate;
    private int glHMatrix;
    private int glHUxy;

    //颜色类型切换
    private int changeType;
    //颜色值切换
    private int changeColor;
    //原始窗口宽高比
    private float uXY;

    //切换类型
    private int type;
    //切换颜色
    private float[] color;
    //纹理id
    private int textureId;

    /**
     * 读取着色器代码
     *
     * @param context :context
     * @param vertex  :顶点着色器代码
     * @param frag    ：片元着色器代码
     *                //     * @param changeColor : 切换的颜色数组
     *                //     * @param changeType  :切换的类型
     */
    public GraphicsRender(Context context, String vertex, String frag) {
        this.context = context;
        this.vertex = vertex;
        this.fragment = frag;
//        this.type = changeType;
//        this.color = changeColor;

        vertexBuffer = ByteBuffer.allocateDirect(vertexPosition.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexPosition);
        vertexBuffer.position(0);

        textureBuffer = ByteBuffer.allocateDirect(texturePosition.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(texturePosition);
        textureBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.0f);

        //启用2D纹理
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        //创建mProgram
        program = ShaderUtils.createProgram(context.getResources(), vertex, fragment);

        glHPosition = GLES20.glGetAttribLocation(program, "vPosition");
        glHCoordinate = GLES20.glGetAttribLocation(program, "vCoordinate");
        glHTexture = GLES20.glGetUniformLocation(program, "vTexture");
        glHMatrix = GLES20.glGetUniformLocation(program, "vMatrix");
        glHUxy = GLES20.glGetUniformLocation(program, "uXY");

        changeType = GLES20.glGetUniformLocation(program, "vChangeType");
        changeColor = GLES20.glGetUniformLocation(program, "vChangeColor");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        float sWH = w / (float) h;
        float sWidthHeight = width / (float) height;
        uXY = sWidthHeight;
        if (width > height) {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight * sWH, sWidthHeight * sWH, -1, 1, 3, 5);
            } else {
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight / sWH, sWidthHeight / sWH, -1, 1, 3, 5);
            }
        } else {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -1 / sWidthHeight * sWH, 1 / sWidthHeight * sWH, 3, 5);
            } else {
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -sWH / sWidthHeight, sWH / sWidthHeight, 3, 5);
            }
        }
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 5.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(program);

        GLES20.glUniform1i(changeType, getType());
        GLES20.glUniform3fv(changeColor, 1, getColor(), 0);

        GLES20.glUniform1f(glHUxy, uXY);
        GLES20.glUniformMatrix4fv(glHMatrix, 1, false, mMVPMatrix, 0);
        GLES20.glEnableVertexAttribArray(glHPosition);//顶点
        GLES20.glEnableVertexAttribArray(glHCoordinate);//纹理
        GLES20.glUniform1i(glHTexture, 0);

        textureId = createTextureID();
        GLES20.glVertexAttribPointer(glHPosition, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glVertexAttribPointer(glHCoordinate, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    //生成纹理
    private int createTextureID() {
        int[] texture = new int[1];
        if (bitmap != null && !bitmap.isRecycled()) {
            //生成纹理
            GLES20.glGenTextures(1, texture, 0);
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            return texture[0];
        }
        return 0;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float[] getColor() {
        return color;
    }

    public void setColor(float[] color) {
        this.color = color;
    }
}
