package com.manna.opengl;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.manna.opengl.render.triangle.TriangleActivity;

/**
 * OpenGL ES主页面
 */
public class MainActivity extends AppCompatActivity {

    private TextView tvTriangle,tvTriangleBoth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initOnClick();
    }

    private void init(){
        tvTriangle=findViewById(R.id.tv_triangle);
        tvTriangleBoth=findViewById(R.id.tv_triangle_both);
    }

    private void initOnClick(){
        //三角形
        tvTriangle.setOnClickListener(v->{
            startActivity(new Intent(this, TriangleActivity.class));
        });

        //等腰三角形
        tvTriangleBoth.setOnClickListener(v->{

        });
    }
}
