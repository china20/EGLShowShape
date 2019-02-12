package com.wr.external.gl;

import android.opengl.GLES20;
import java.nio.FloatBuffer;

/**
 * 编译着色器并进行连接
 * @author 汪荣
 */
public final class GlProgram {

    public static final String TAG = "Wr:GlProgram";

    private int mProgram = 0;
    private int mVertexShader = 0;
    private int mFragmentShader = 0;

    public int program() {
        return mProgram;
    }

    public void use() {
        GLES20.glUseProgram(mProgram);
    }

    public int link(String vertexSource, String fragmentSource) {
        mVertexShader = WrGlUtil.compileShader(WrGlUtil.VERTEX_SHADER, vertexSource);
        mFragmentShader = WrGlUtil.compileShader(WrGlUtil.FRAGMENT_SHADER, fragmentSource);
        mProgram = WrGlUtil.linkProgram(mVertexShader, mFragmentShader);

        if (mProgram <= 0) {
            release();
        }

        return mProgram;
    }

    public int getUniformLoc(String name) {
        return GLES20.glGetUniformLocation(mProgram, name);
    }

    public int getAttribLoc(String name) {
        return GLES20.glGetAttribLocation(mProgram, name);
    }

    public int setAttribute(String name, int size, int stride, FloatBuffer data) {
        return WrGlUtil.setAttribute(mProgram, name, size, stride, data);
    }

    public int setAttribute(String name, int size, int stride, int offset) {
        return WrGlUtil.setAttribute(mProgram, name, size, stride, offset);
    }

    public void unifor1i(int pos, int x) {
        GLES20.glUniform1i(pos, x);
    }

    public void unifor2i(int pos, int x, int y) {
        GLES20.glUniform2i(pos, x, y);
    }

    public void uniform4f(int pos, float[] val) {
        GLES20.glUniform4f(pos, val[0], val[1], val[2], val[3]);
    }

    public void unifor2f(int pos, float x, float y) {
        GLES20.glUniform2f(pos, x, y);
    }

    public void unifor1f(int pos, float x) {
        GLES20.glUniform1f(pos, x);
    }

    public void uniformMatrix4fv(int pos, float[] m, int offset) {
        GLES20.glUniformMatrix4fv(pos, 1, false, m, offset);
    }

    /**
     * 着色器对象
     */
    public void release() {
        if (mVertexShader > 0) {
            GLES20.glDeleteShader(mVertexShader);
            mVertexShader = 0;
        }

        if (mFragmentShader > 0) {
            GLES20.glDeleteShader(mFragmentShader);
            mFragmentShader = 0;
        }

        if (mProgram > 0) {
            GLES20.glDeleteProgram(mProgram);
            mProgram = 0;
        }
    }
}
