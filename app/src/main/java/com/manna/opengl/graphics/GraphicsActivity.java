package com.manna.opengl.graphics;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.manna.opengl.R;

import java.io.IOException;

/**
 * 图片处理
 */
public class GraphicsActivity extends AppCompatActivity {

    private TextView tvOld, tvBlackWhite, tvBlue, tvRed, tvBlur, tvScale;
    private GLSurfaceView glImgShow;
    private Bitmap bitmap;

    //------------------------------
    private float[] old = new float[]{0.0f, 0.0f, 0.0f};
    private float[] blackWhite = new float[]{0.299f, 0.587f, 0.114f};
    private float[] blue = new float[]{0.0f, 0.0f, 0.1f};
    private float[] red = new float[]{0.1f, 0.1f, 0.0f};
    private float[] blur = new float[]{0.006f, 0.004f, 0.002f};
    private float[] scale = new float[]{0.0f, 0.0f, 0.4f};

    private GraphicsRender render;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphics);

        init();
        initClick();
        drawTexture();
    }

    private void init() {
        tvOld = findViewById(R.id.tv_old);
        tvBlackWhite = findViewById(R.id.tv_black_white);
        tvBlue = findViewById(R.id.tv_blue);
        tvRed = findViewById(R.id.tv_red);
        tvBlur = findViewById(R.id.tv_blur);
        tvScale = findViewById(R.id.tv_scale);

        glImgShow = findViewById(R.id.gl_img_show);

        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_pie);
    }

    private void drawTexture() {
        render = new GraphicsRender(this, "texture_vertex.glsl", "texture_fragment.glsl");
        render.setType(0);
        render.setColor(old);
        render.setBitmap(bitmap);
        glImgShow.setEGLContextClientVersion(2);
        glImgShow.setRenderer(render);

        // RENDERMODE_WHEN_DIRTY -- 懒惰渲染，需要手动调用 glSurfaceView.requestRender() 才会进行更新
        // RENDERMODE_CONTINUOUSLY -- 不停的渲染
        glImgShow.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private void initClick() {
        tvOld.setOnClickListener(v -> {
            setRenderAttr(0, old);
        });
        tvBlackWhite.setOnClickListener(v -> {
            setRenderAttr(1, blackWhite);
        });
        tvBlue.setOnClickListener(v -> {
            setRenderAttr(2, blue);
        });
        tvRed.setOnClickListener(v -> {
            setRenderAttr(2, red);
        });
        tvBlur.setOnClickListener(v -> {
            setRenderAttr(3, blur);
        });
        tvScale.setOnClickListener(v -> {
            setRenderAttr(4, scale);
        });
    }

    private void setRenderAttr(int type, float[] color) {
        render.setType(type);
        render.setColor(color);
        glImgShow.requestRender();
    }
}
