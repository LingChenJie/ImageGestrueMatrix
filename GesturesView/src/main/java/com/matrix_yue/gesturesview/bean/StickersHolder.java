package com.matrix_yue.gesturesview.bean;

import com.matrix_yue.gesturesview.view.MyCustomView;

/**
 * 贴纸编辑
 */
public class StickersHolder {

    private MyCustomView imgV;
    private ImageState state;

    public MyCustomView getImgV() {
        return imgV;
    }

    public void setImgV(MyCustomView imgV) {
        this.imgV = imgV;
    }

    public ImageState getState() {
        return state;
    }

    public void setState(ImageState state) {
        this.state = state;
    }


}