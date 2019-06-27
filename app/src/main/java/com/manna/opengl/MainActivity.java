package com.manna.opengl;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.manna.opengl.render.cone.ConeActivity;
import com.manna.opengl.render.cube.CubeActivity;
import com.manna.opengl.render.oval.Oval;
import com.manna.opengl.render.oval.OvalActivity;
import com.manna.opengl.render.square.SquareActivity;
import com.manna.opengl.render.triangle.TriangleActivity;
import com.manna.opengl.render.triangle.TriangleBothActivity;

/**
 * OpenGL ES主页面
 */
public class MainActivity extends AppCompatActivity {

    private TextView tvTriangle, tvTriangleBoth, tvSquare, tvOval, tvCube, tvCone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initOnClick();
    }

    private void init() {
        tvTriangle = findViewById(R.id.tv_triangle);
        tvTriangleBoth = findViewById(R.id.tv_triangle_both);
        tvSquare = findViewById(R.id.tv_square);
        tvOval = findViewById(R.id.tv_oval);
        tvCube = findViewById(R.id.tv_cube);
        tvCone = findViewById(R.id.tv_cone);
    }

    private void initOnClick() {
        //三角形
        tvTriangle.setOnClickListener(v -> {
            startActivity(new Intent(this, TriangleActivity.class));
        });

        //等腰三角形
        tvTriangleBoth.setOnClickListener(v -> {
            startActivity(new Intent(this, TriangleBothActivity.class));
        });

        //正方形
        tvSquare.setOnClickListener(v -> {
            startActivity(new Intent(this, SquareActivity.class));
        });

        //圆形
        tvOval.setOnClickListener(v -> {
            startActivity(new Intent(this, OvalActivity.class));
        });

        //正方体
        tvCube.setOnClickListener(v -> {
            startActivity(new Intent(this, CubeActivity.class));
        });

        //圆锥
        tvCone.setOnClickListener(v -> {
            startActivity(new Intent(this, ConeActivity.class));
        });
    }
}
