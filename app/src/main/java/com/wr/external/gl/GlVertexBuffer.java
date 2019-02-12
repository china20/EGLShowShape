package com.wr.external.gl;

import android.opengl.GLES20;
import java.nio.Buffer;

/**
 * 用来操作VBO，生成VertexBuffer，存储对应的定点数据
 * @author 汪荣
 */
public final class GlVertexBuffer {

    public static final String TAG = "Wr:GlVertexBuffer";

    // 如果数据分配后不需要改动，那么使用GL_STATIC会有很好的效率
    public static final int GL_STATIC = GLES20.GL_STATIC_DRAW;
    // 数据很少改动时使用
    public static final int GL_DYNAMIC = GLES20.GL_DYNAMIC_DRAW;
    public static final int GL_STREAM = GLES20.GL_STREAM_DRAW;

    // 绑定到FrameBuffer的纹理ID
    private int mBufferId = 0;

    public GlVertexBuffer() {
        initVertexBuffer();
    }

    public int bufferId() {
        return mBufferId;
    }

    public void release() {
        if (mBufferId > 0) {
            WrGlUtil.deleteVertexBuffer(mBufferId);
            mBufferId = 0;
        }
    }

    /**
     * 撤销GL_ARRAY_BUFFER类型的VBO绑定
     */
    public static void unbindArrayBuffer() {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    /**
     * 撤销GL_ELEMENT_ARRAY_BUFFER类型的VBO绑定
     */
    public static void unbindElementArrayBuffer() {
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    /**
     * 调用glGenBuffers生成VBO对象
     */
    private void initVertexBuffer() {
        int[] values = new int[1];
        GLES20.glGenBuffers(1, values, 0);
        mBufferId = values[0];
    }

    /**
     * 绑定GL_ARRAY_BUFFER类型的VBO
     */
    public void bindArrayBuffer() {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mBufferId);
    }

    /**
     * 绑定GL_ELEMENT_ARRAY_BUFFER类型的VBO
     */
    public void bindElementArrayBuffer() {
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mBufferId);
    }

    /**
     * 为GL_ARRAY_BUFFER类型的VBO分配内存，如果data不为空，则把data数据拷贝
     * 分配的内存
     * @param usage GL_STATIC、GL_DYNAMIC、GL_STREAM值之一
     * @param data data数据的偏移
     * @param size 需要拷贝的data数据的长度
     */
    public void allocArrayBuffer(int usage, Buffer data, int size) {
        if (mBufferId > 0) {
            bindArrayBuffer();
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, size, data, usage);
        }
    }

    /**
     * 为GL_ELEMENT_ARRAY_BUFFER类型的VBO分配内存，如果data不为空，则把data数据拷贝
     * 分配的内存
     * @param usage GL_STATIC、GL_DYNAMIC、GL_STREAM值之一
     * @param data data数据的偏移
     * @param size 需要拷贝的data数据的长度
     */
    public void allocELementArrayBuffer(int usage, Buffer data, int size) {
        if (mBufferId > 0) {
            bindElementArrayBuffer();
            GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, size, data, usage);
        }
    }

    /**
     * 为GL_ARRAY_BUFFER类型的VBO设置数据
     * @param data 数据
     * @param offset data数据的偏移
     * @param size 需要拷贝的data数据的长度
     */
    public void assignArrayBuffer(Buffer data, int offset, int size) {
        if (mBufferId > 0) {
            bindArrayBuffer();
            GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, offset, size, data);
        }
    }

    /**
     * 为GL_ELEMENT_ARRAY_BUFFER类型的VBO设置数据
     * @param data 数据
     * @param offset data数据的偏移
     * @param size 需要拷贝的data数据的长度
     */
    public void assignELementArrayBuffer(Buffer data, int offset, int size) {
        if (mBufferId > 0) {
            bindElementArrayBuffer();
            GLES20.glBufferSubData(GLES20.GL_ELEMENT_ARRAY_BUFFER, offset, size, data);
        }
    }
}
