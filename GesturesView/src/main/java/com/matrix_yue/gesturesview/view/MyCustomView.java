package com.matrix_yue.gesturesview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.matrix_yue.gesturesview.R;

/**
 * Created by yue on 2016/3/11.
 */
public class MyCustomView extends ImageView{
    public PointF leftTop = new PointF();// 图片左上角的坐标
    public PointF rightTop = new PointF();// 右上
    public PointF leftBottom = new PointF();// 左下
    public PointF rightBottom = new PointF();// 图片右下角的坐标
    private boolean isSelect = false;
    public Bitmap bitDelete = null;
    public Bitmap bitMove = null;
    public Bitmap bitFlip = null;
    public Bitmap transparency = null;
    private Bitmap bitmapSelf;

    public MyCustomView(Context context, Bitmap bitmapSelf) {
        super(context);
        this.bitmapSelf = bitmapSelf;
    }

    public MyCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Bitmap getBitmapSelf() {
        return bitmapSelf;
    }

    public void setBitmapSelf(Bitmap bitmapSelf) {
        this.bitmapSelf = bitmapSelf;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isSelect) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);

            paint.setColor(Color.parseColor("#F77DA3"));
            paint.setStrokeWidth(5f);
            canvas.drawLine(leftTop.x, leftTop.y, rightTop.x, rightTop.y,paint);
            canvas.drawLine(leftBottom.x, leftBottom.y, rightBottom.x,rightBottom.y, paint);
            canvas.drawLine(leftTop.x, leftTop.y, leftBottom.x, leftBottom.y,paint);
            canvas.drawLine(rightTop.x, rightTop.y, rightBottom.x,rightBottom.y, paint);

            if (bitDelete == null) {
                bitDelete = BitmapFactory.decodeResource(getResources(),
                        R.drawable.btn_sticker_cancel_n);
            }
            if (bitMove == null) {
                bitMove = BitmapFactory.decodeResource(getResources(),
                        R.drawable.btn_sticker_turn_n);
            }if (bitFlip == null) {
                bitFlip = BitmapFactory.decodeResource(getResources(),
                        R.drawable.btn_sticker_reverse_n);
            }if (transparency == null) {
                transparency = BitmapFactory.decodeResource(getResources(),
                        R.drawable.btn_sticker_transparency_n);
            }

            int xiao = bitDelete.getHeight()/2;
            canvas.drawBitmap(bitDelete, leftTop.x-xiao, leftTop.y-xiao, paint);
            canvas.drawBitmap(transparency, rightTop.x-xiao, rightTop.y-xiao, paint);
            canvas.drawBitmap(bitMove, rightBottom.x-xiao, rightBottom.y-xiao, paint);
            canvas.drawBitmap(bitFlip, leftBottom.x-xiao, leftBottom.y-xiao, paint);
        }
    }


    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
        this.invalidate();
    }


}
