package com.wr.external.gl;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES;

/**
 * EGL的操作辅助类，编译着色器，连接program等
 * @author 汪荣
 */
public final class WrGlUtil {

    public static final String TAG = "Wr:WrGlUtil";

    public static final int VERTEX_SHADER = GLES20.GL_VERTEX_SHADER;
    public static final int FRAGMENT_SHADER = GLES20.GL_FRAGMENT_SHADER;

    public static int getShaderCompileStatus(int shader) {
        int[] status = { 0 };
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0);
        return status[0];
    }

    public static String getShaderCompileMessage(int shader) {
        return GLES20.glGetShaderInfoLog(shader);
    }

    public static int getProgramLinkStatus(int program) {
        int[] status = { 0 };
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0);
        return status[0];
    }

    public static String getProgramLinkMessage(int program) {
        return GLES20.glGetProgramInfoLog(program);
    }

    public static int compileShader(int type, String source) {
        // 1.创建一个着色器
        int idShader = GLES20.glCreateShader(type);

        Log.i(TAG, "create shader:" + idShader);

        GLES20.glShaderSource(idShader, source);
        GLES20.glCompileShader(idShader);

        if (getShaderCompileStatus(idShader) == 0) {
            Log.e(TAG, "compile shader error:" + getShaderCompileMessage(idShader));
            GLES20.glDeleteShader(idShader);
            idShader = 0;
        }

        return idShader;
    }

    public static int linkProgram(int vertexShader, int fragShader) {
        int idProgram;

        idProgram = GLES20.glCreateProgram();

        Log.i(TAG, "create program:" + idProgram + ";vertexShader:"
                + vertexShader + ";fragShader:" + fragShader);

        GLES20.glAttachShader(idProgram, vertexShader);
        GLES20.glAttachShader(idProgram, fragShader);

        // 连接program
        GLES20.glLinkProgram(idProgram);

        if (getProgramLinkStatus(idProgram) == 0) {
            Log.e(TAG, "link program error:" + getProgramLinkMessage(idProgram));
            GLES20.glDeleteProgram(idProgram);
            idProgram = 0;
        }

        return idProgram;
    }

    public static FloatBuffer arrayToFloatBuffer(final float[] data) {
        FloatBuffer fb = ByteBuffer.allocateDirect(data.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(data);
        fb.position(0);

        return fb;
    }

    public static int setAttribute(int program, String name, int size, int stride, FloatBuffer data) {
        // 1. 在program中生成attribute类型的变量name的属性
        int idAttri = GLES20.glGetAttribLocation(program, name);
        // 2. 把定点数据赋给定点着色器的属性 name
        GLES20.glVertexAttribPointer(idAttri, size, GLES20.GL_FLOAT, false, stride, data);
        // 3. 使顶点属性对于Opengl ES可见（默认情况下为了节省内存，顶点属性是没有打开的）
        // 如果不调用这句那顶点坐标是不可见，所以绘制看不到图形
        enableAttribute(idAttri);
        return idAttri;
    }

    public static int setAttribute(int program, String name, int size, int stride, int offset) {
        int idAttri = GLES20.glGetAttribLocation(program, name);
        GLES20.glVertexAttribPointer(idAttri, size, GLES20.GL_FLOAT, false, stride, offset);
        enableAttribute(idAttri);
        return idAttri;
    }

    public static void enableAttribute(int id) {
        GLES20.glEnableVertexAttribArray(id);
    }

    public static void disableAttribute(int id) {
        GLES20.glDisableVertexAttribArray(id);
    }

    public static int createOESTexture2D() {
        int[] idTexture = { 0 };

        GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
        GLES20.glGenTextures(1, idTexture, 0);

        Log.i(TAG, "createTexture2D:" + idTexture[0]);

        // 绑定到二维纹理（EGL内部自动维护这个绑定状态，后续对二维纹理的操作都是居于绑定的这个）
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, idTexture[0]);

        // 设置绘制纹理时放大缩小等处理方式（在这里使用线性平均值）
        GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        return idTexture[0];
    }

    public static int createTexture2D() {
        int[] values = new int[1];
        int textureId = 0;
        GLES20.glGenTextures(1, values, 0);
        textureId = values[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        // 设置绘制纹理时放大缩小等处理方式（在这里使用线性平均值）
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        return textureId;
    }

    public static void deleteTexture(int id) {
        int[] ids = { id };
        GLES20.glDeleteTextures(1, ids, 0);
    }

    public static void deleteVertexBuffer(int id) {
        int[] ids = { id };
        GLES20.glDeleteBuffers(1, ids, 0);
    }

    public static void deleteFrameBuffer(int id) {
        int[] ids = { id };
        GLES20.glDeleteFramebuffers(1, ids, 0);
    }

    public static void deleteRenderBuffer(int id) {
        int[] ids = { id };
        GLES20.glDeleteRenderbuffers(1, ids, 0);
    }

    /**
     * 把着色器里的属性绑定到创建的扩展纹理对象上
     * @param program program
     * @param texture 纹理对象
     * @return 成功返回1，否则0
     */
    public static int bindTextureOES(int program, int texturePos, String name, int texture) {
        int pos = GLES20.glGetUniformLocation(program, name);
        GLES20.glActiveTexture(texturePos);
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, texture);
        GLES20.glUniform1i(pos, texturePos - GLES20.GL_TEXTURE0);
        return 1;
    }

    /**
     * 把着色器里的属性绑定到创建的纹理对象上
     * @param program program
     * @param texture 纹理对象
     * @return 成功返回1，否则0
     */
    public static int bindTexture(int program, int texturePos, String name, int texture) {
        int pos = GLES20.glGetUniformLocation(program, name);
        GLES20.glActiveTexture(texturePos);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glUniform1i(pos, texturePos - GLES20.GL_TEXTURE0);
        return 1;
    }

    public static void bindTexture() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    public static void bindTextureOES() {
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, 0);
    }

    public static void updateTextureImage(Buffer pixels, int frameWidth, int frameHeight) {
        if (pixels != null) {
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
                    frameWidth, frameHeight, 0, GLES20.GL_RGBA,
                    GLES20.GL_UNSIGNED_BYTE, pixels);
        }
    }
}

