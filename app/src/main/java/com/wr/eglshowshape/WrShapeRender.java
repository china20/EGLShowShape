package com.wr.eglshowshape;

import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.wr.external.gl.GlProgram;
import com.wr.external.gl.GlRunner;
import com.wr.external.gl.WrGlCore;
import com.wr.external.gl.WrRender;

public class WrShapeRender extends WrRender {

    public static final String TAG = "Wr:WrShapeRender";

    private GlProgram mProgram;
    private EGLSurface mSurface;
    private Object mTargetSurface;
    private Triangle mTriangle;

    private float[] mMatrix = new float[16];

    public static final String VERTEXSHADER_SOURCE = ""
            + "attribute vec4 uPos;                        \n"
            + "uniform mat4 mMatrix;                       \n"
            + "void main(void)                             \n"
            + "{                                           \n"
            + "    gl_Position = mMatrix * uPos;           \n"
            + "}                                           \n";

    public static final String FRAGMENTSHADER_SOURCE = ""
            + "uniform vec4 vTexColor;                     \n"
            + "void main(void)                             \n"
            + "{                                           \n"
            + "    gl_FragColor = vTexColor;               \n"
            + "}                                           \n";

    public WrShapeRender(Object surface) {
        mProgram = new GlProgram();
        mTargetSurface = surface;
        mTriangle = new Triangle();
        mSurface = WrGlCore.NO_SURFACE;
        Matrix.setIdentityM(mMatrix, 0);
    }

    public void onPrepare(final GlRunner eglRender) {
        mSurface = eglRender.createSurface(mTargetSurface);
        eglRender.attachThread(mSurface);
        mProgram.link(VERTEXSHADER_SOURCE, FRAGMENTSHADER_SOURCE);

        eglRender.requestRender();
    }

    public void setMatrix(float[] m) {
        mMatrix = m;
    }

    public void onRender(final GlRunner eglRender) {
        int colorAttri = 0;
        clear(0.3f, 0.3f, 0.3f, 1.0f);

        mProgram.use();
        // 1. 设置顶点坐标
        mProgram.setAttribute("uPos", 2, 0, mTriangle.mVertexBuffer);

        // 2. 'vTexColor'是uniform修饰的，因此需要用glGetUniformLocation获取其位置
        colorAttri = mProgram.getUniformLoc("vTexColor");
        mProgram.uniform4f(colorAttri, mTriangle.mTextureColor);

        // 3. 设置矩阵
        colorAttri = mProgram.getUniformLoc("mMatrix");
        mProgram.uniformMatrix4fv(colorAttri, mMatrix, 0);

        // 4. 绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);

        // 5. 显示
        eglRender.swapBuffers();
    }

    public void onFinish(final GlRunner eglRender) {
        mProgram.release();
        eglRender.releaseSurface(mSurface);
    }
}
