package com.wr.eglshowshape;

import java.nio.FloatBuffer;
import com.wr.external.gl.WrGlUtil;

public class Square {

    public float mVertexCoor[] = {
            -0.5f, 0.5f,   // left top
            -0.5f, -0.5f,  // left bottom
            0.5f, 0.5f,    // right top
            0.5f, -0.5f    // right bottom
    };

    public float[] mVertexColor = {
            1.0f,
            0.0f,
            0.0f,
            0.6f,
    };

    public float[] mVertexOrder = { 0, 1, 2, 1, 2, 3 };

    public FloatBuffer mVertexBuffer;
    public FloatBuffer mOrderBuffer;

    public Square() {
        mVertexBuffer = WrGlUtil.arrayToFloatBuffer(mVertexCoor);
        mOrderBuffer = WrGlUtil.arrayToFloatBuffer(mVertexOrder);
    }
}
