package com.wr.external.gl;

import android.opengl.GLES20;
import android.util.Log;

/**
 * 用来操作FBO，生成FrameBuffer，绑定到对应的纹理
 * @author 汪荣
 */
public final class GlFrameBuffer {

    public static final String TAG = "Wr:GlFrameBuffer";

    // 绑定到FrameBuffer的纹理ID
    private int mTextureId = 0;
    private int mFrameBufferId = 0;
    private int mRenderBufferId = 0;

    public int textureId() {
        return mTextureId;
    }

    /**
     * 返回FBO对象索引
     * @return FBO对象索引
     */
    public int frameBufferId() {
        return mFrameBufferId;
    }

    public int renderBufferId() {
        return mRenderBufferId;
    }

    /**
     * 释放纹理、FBO对象
     */
    public void release() {
        if (mTextureId > 0) {
            WrGlUtil.deleteTexture(mTextureId);
            mTextureId = 0;
        }

        if (mFrameBufferId > 0) {
            WrGlUtil.deleteFrameBuffer(mFrameBufferId);
            mFrameBufferId = 0;
        }

        if (mRenderBufferId > 0) {
            WrGlUtil.deleteRenderBuffer(mRenderBufferId);
            mRenderBufferId = 0;
        }
    }

    /**
     * 把FBO切换到当前帧缓存
     */
    public void useFrameBuffer() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBufferId);
    }

    /**
     * 帧缓存切换到默认
     */
    public void freeFrameBuffer() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public void setFrameBufferSize(int wid, int hei) {

        useFrameBuffer();

        // 先绑定到这个纹理ID
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);

        // 对此纹理ID创建对应的存储
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, wid, hei, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mTextureId, 0);

        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Framebuffer not complete, status=" + status);
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        // 先不使用FrameBuffer，将其切换掉。到开始绘制的时候，绑定回来
        freeFrameBuffer();
    }

    /**
     * 根据图像的宽和高初始化纹理并分配内存，然后绑定到FBO的颜色空间
     * @param width 图像的宽
     * @param height 图像的高
     * @return FBO对象ID
     */
    public int createFramebuffer(int width, int height) {
        int[] values = new int[1];

        release();

        // 先生成对应的纹理ID
        mTextureId = WrGlUtil.createTexture2D();

        // 创建FrameBuffer Object并且绑定它
        GLES20.glGenFramebuffers(1, values, 0);
        mFrameBufferId = values[0];

        setFrameBufferSize(width, height);

        Log.i(TAG, "createFramebuffer, width: " + width + ";height:" + height);

        return mFrameBufferId;
    }
}
