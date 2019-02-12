package com.wr.external.gl;

import android.opengl.GLES20;

/**
 * 继承GlRunner.Render，实现继承的绘制
 * @author 汪荣
 */
public abstract class WrRender implements GlRunner.Render {

    public WrRender() {

    }

    protected void clear(float r, float g, float b, float a) {
        // 清除背景色(使用黑色清除)
        GLES20.glClearColor(r, g, b, a);

        // 按位清除
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
    }
}
