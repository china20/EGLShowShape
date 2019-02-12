package com.wr.eglshowshape;

import java.nio.FloatBuffer;
import com.wr.external.gl.WrGlUtil;

public class Triangle {

    public float mVertexCoor[] = {
            -0.5f, 0.5f,
            0.5f, 0.5f,
            0.0f, -0.5f,
    };

    public float[] mTextureColor = {
            1.0f,
            0.0f,
            0.0f,
            1.0f,
    };

    public float[] mVertexOrder = { 0, 1, 2 };

    public FloatBuffer mVertexBuffer;
    public FloatBuffer mOrderBuffer;

    public Triangle() {
        mVertexBuffer = WrGlUtil.arrayToFloatBuffer(mVertexCoor);
        mOrderBuffer = WrGlUtil.arrayToFloatBuffer(mVertexOrder);
    }
}
