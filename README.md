## OpenGL ES绘制图形系列
#### 1.完成OpenGL ES 绘制三角形
#### 2.完成基于Matrix矩阵绘制等腰三角形
#### 3.完成基于Matrix矩阵绘制正方形
#### 4.完成基于Matrix矩阵绘制圆形
#### 5.完成基于Matrix矩阵绘制正方体
```
    -------------------------------
    GLSL着色器语言类型、申明参数解析
    -------------------------------
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
```
#### 6.完成基于Matrix矩阵绘制圆锥
#### 7.完成基于Texture2D图片纹理处理(黑白、冷色、暖色、模糊、放大)