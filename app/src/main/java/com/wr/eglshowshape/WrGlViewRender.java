package com.wr.eglshowshape;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.wr.external.gl.GlProgram;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class WrGlViewRender implements GLSurfaceView.Renderer {

    public static final String TAG = "Wr:EGLRender";

    public static final String GL_VERTEXSHADER_SOURCE = ""
            + "attribute vec4 uPos;                        \n"
            + "uniform mat4 mMatrix;                       \n"
            + "void main(void)                             \n"
            + "{                                           \n"
            + "    gl_Position = mMatrix * uPos;           \n"
            + "}                                           \n";

    public static final String GL_FRAGMENTSHADER_SOURCE = ""
            + "uniform vec4 vTexColor;                     \n"
            + "void main(void)                             \n"
            + "{                                           \n"
            + "    gl_FragColor = vTexColor;               \n"
            + "}                                           \n";

    private GlProgram mTriangleProgram;
    private GlProgram mRectProgram;
    private boolean mInitSucc = false;
    private float[] mMatrix = new float[16];

    public WrGlViewRender() {
        mRectProgram = new GlProgram();
        mTriangleProgram = new GlProgram();
    }

    private void drawRectangle(GL10 gl) {

        int colorField;
        Square square = new Square();

        Log.i(TAG, "onDrawFrame: " + mRectProgram);
        mRectProgram.use();

        mRectProgram.setAttribute("uPos", 2, 0, square.mVertexBuffer);

        // 4. 'vTexColor'是uniform修饰的，因此需要用glGetUniformLocation获取其位置
        colorField = GLES20.glGetUniformLocation(mRectProgram.program(), "vTexColor");
        // 5. 把片元着色器的属性'vTexColor'颜色设置为绿色
        GLES20.glUniform4f(colorField, square.mVertexColor[0], square.mVertexColor[1],
                square.mVertexColor[2], square.mVertexColor[3]);

        int mPos = GLES20.glGetUniformLocation(mRectProgram.program(), "mMatrix");
        GLES20.glUniformMatrix4fv(mPos, 1, false, mMatrix, 0);

        // 6. 绘制
        gl.glDrawArrays(gl.GL_TRIANGLE_STRIP, 0, 4);
    }

    private void drawTriangle(GL10 gl) {
        int colorField;
        Triangle triangle = new Triangle();

        mTriangleProgram.use();
        mTriangleProgram.setAttribute("uPos", 2, 0, triangle.mVertexBuffer);

        // 'vTexColor'是uniform修饰的，因此需要用glGetUniformLocation获取其位置
        colorField = GLES20.glGetUniformLocation(mTriangleProgram.program(), "vTexColor");
        // 把片元着色器的属性'vTexColor'颜色设置为绿色
        GLES20.glUniform4f(colorField, 0.0f, 1.0f, 0.0f, 1.0f);

        int mPos = GLES20.glGetUniformLocation(mTriangleProgram.program(), "mMatrix");
        GLES20.glUniformMatrix4fv(mPos, 1, false, mMatrix, 0);

        gl.glDrawArrays(gl.GL_TRIANGLES, 0, 3);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mInitSucc = mRectProgram.link(GL_VERTEXSHADER_SOURCE, GL_FRAGMENTSHADER_SOURCE) > 0;
        mInitSucc = mRectProgram.link(GL_VERTEXSHADER_SOURCE, GL_FRAGMENTSHADER_SOURCE) > 0;
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        if (width > height) {
            float fRatio = (float)width / (float)height;
            Matrix.orthoM(mMatrix, 0, -fRatio, fRatio, -1, 1, -1, 1);
        } else {
            float fRatio = (float)height / (float)width;
            Matrix.orthoM(mMatrix, 0, -1, 1, -fRatio, fRatio, -1, 1);
        }
    }

    public void onDrawFrame(GL10 gl) {
        if (mInitSucc) {

            gl.glClearColor(0, 0, 0, 1);
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

            drawRectangle(gl);

            // 初始化三角形顶点坐标和渲染颜色
            drawTriangle(gl);
        }
    }
}
