package com.matrix_yue.gesturesview;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.matrix_yue.gesturesview.bean.ImageState;
import com.matrix_yue.gesturesview.bean.StickersHolder;
import com.matrix_yue.gesturesview.view.MyCustomView;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener {

    private FrameLayout frame;
    //变换模式
    private final int NONE = 0; // 无
    private final int DRAG = 1; // 移动
    private final int ZOOM = 2; // 变换
    private final int DOUBLE_ZOOM = 3; //双指缩放

    //选中的是哪个
    private int selectImageCount = -1;
    //用来存储所有的view
    private ArrayList<StickersHolder> list_V;
    //当前view的矩阵
    private Matrix matrix = new Matrix();
    //保存的上一个view的矩阵
    private Matrix savedMatrix = new Matrix();
    //默认设置模式为none 既不移动也不变换
    private int mode = NONE;// 当前操作：移动/变换/无
    private PointF startPoinF = new PointF();// 屏幕刚开始点击的坐标
    private PointF midP = null; // 最开始图片中点
    private float imgLengHalf = 1f;// 最开始图片对角线一半的长度
    private float oldRotation = 0f;// 最开始点击时的旋转角度
    //当前的可缩放的view
    private MyCustomView curruntEditImageView;
    //当前的view的bitmap
    private Bitmap curruntTiezhiBitmap;
    //起始大小
    private float baseValue;
    //图片起始的透明度
    private int curruntEditImageAlpha = 306;
    private Button addView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    private void initData() {
        list_V = new ArrayList<StickersHolder>();
        Bitmap emoji1 = BitmapFactory.decodeResource(getResources(), R.drawable.androidemojiface1);
        addGesturesView(emoji1);
    }

    private void initView() {
        frame = (FrameLayout)findViewById(R.id.frame);
        addView = (Button)findViewById(R.id.addView);
        addView.setOnClickListener(this);
//        Bitmap emoji1 = BitmapFactory.decodeResource(getResources(), R.drawable.androidemojiface0);
    }

    // 添加贴纸图片
    private void addGesturesView(Bitmap b) {
        //将其他贴纸取消选中
        if (b != null) {
            for (int i = (list_V.size() - 1); i >= 0; i--) {
                StickersHolder stickersHolder = list_V.get(i);
                if (stickersHolder.getImgV().isSelect()) {
                    stickersHolder.getImgV().setSelect(false);
                    break;
                }
            }

            curruntEditImageView = new MyCustomView(this, b);
            curruntEditImageView.setLayoutParams(new ViewGroup.LayoutParams(MyApplication.getInstance().getScreenWidth(),
                    MyApplication.getInstance().getScreenHeight()));

            curruntEditImageView.setScaleType(ImageView.ScaleType.MATRIX);
            curruntEditImageView.setImageBitmap(b);
            curruntEditImageView.setSelect(true);
            curruntEditImageView.invalidate();
            frame.addView(curruntEditImageView);

            curruntTiezhiBitmap = b;

            int bW = b.getWidth();
            int bH = b.getHeight();

            Matrix imageMatrix = new Matrix(curruntEditImageView.getImageMatrix());

            //设置图片的宽度 为了适应屏幕
            int newW = MyApplication.getInstance().getScreenWidth() / 3;
            //等比例缩放
            int newH = (int) (((float) newW / (float) bW) * (float) bH);
            float scale = ((float) newW) / bW;
            imageMatrix.postScale(scale, scale, 0, 0);// 缩放图片大小

            int x1 = MyApplication.getInstance().getScreenWidth() / 3;
            int y1 = MyApplication.getInstance().getScreenHeight() / 3;
            // 原图左上角
            curruntEditImageView.leftTop.set(x1, y1);
            // 原图右上角
            curruntEditImageView.rightTop.set(x1 + newW, y1);
            // 原图左下角
            curruntEditImageView.leftBottom.set(x1, y1 + newH);
            // 原图右下角
            curruntEditImageView.rightBottom.set(x1 + newW, y1 + newH);

            imageMatrix.postTranslate(x1, y1);// 图片平移

            //保存位置 上下左右
            ImageState state1 = new ImageState();
            state1.setLeft(x1);
            state1.setTop(y1);
            state1.setRight(x1 + newW);
            state1.setBottom(y1 + newH);

            //设置当前view的矩阵
            curruntEditImageView.setImageMatrix(imageMatrix);

            StickersHolder stickersHolder1 = new StickersHolder();
            stickersHolder1.setImgV(curruntEditImageView);
            stickersHolder1.setState(state1);
            list_V.add(stickersHolder1);

            selectImageCount = list_V.size() - 1;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //这里根据实际情况做了一下计算 亲测 位置准确
        float event_x = event.getRawX();
        // float event_y = event.getY() - 30;
        float event_y = event.getRawY() - getStatusBarHeight();

        int tempInt = getStatusBarHeight() + 10;
        int addint = -20;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                baseValue = 0;
                startPoinF.set(event_x, event_y);// 保存刚开始按下的坐标

                //选中的图片
                selectImG(event_x, event_y);

                if (selectImageCount != -1) {
                    mode = DRAG;
                    matrix.set(list_V.get(selectImageCount).getImgV()
                            .getImageMatrix());
                    savedMatrix.set(matrix);// 把以前的图片大小保存起来

                    MyCustomView edImg = list_V.get(selectImageCount).getImgV();

                    //拿到当前view的坐标 在addGesturesView方法里面设置过了
                    PointF leftTop = edImg.leftTop;// 图片zuo 上角的坐标
                    PointF rightBottom = edImg.rightBottom;// 图片右下角的坐标
                    PointF leftBottom = edImg.leftBottom;
                    PointF rightTop = edImg.rightTop;

                    //生成4个矩形 用来判断点击区域是不是在这个范围内 下面有每一个的注释
                    Rect moveRect = new Rect((int) rightBottom.x - tempInt,
                            (int) rightBottom.y - tempInt, (int) rightBottom.x + tempInt + addint,
                            (int) rightBottom.y + tempInt + addint);
                    Rect deleteRect = new Rect((int) leftTop.x - tempInt,
                            (int) leftTop.y - tempInt, (int) leftTop.x + tempInt + addint,
                            (int) leftTop.y + tempInt + addint);

                    Rect flipRect = new Rect((int) leftBottom.x - tempInt,
                            (int) leftBottom.y - tempInt, (int) leftBottom.x + tempInt + addint,
                            (int) leftBottom.y + tempInt + addint);

                    Rect transparencyRect = new Rect((int) rightTop.x - tempInt,
                            (int) rightTop.y - tempInt, (int) rightTop.x + tempInt + addint,
                            (int) rightTop.y + tempInt + addint);

                    if (moveRect.contains((int) event_x, (int) event_y)) {
                        // 点中了变换
                        midP = midPoint(edImg.leftTop, edImg.rightBottom);
                        imgLengHalf = spacing(midP, edImg.rightBottom);
                        oldRotation = rotation(midP, startPoinF);
                        mode = ZOOM;

                    } else if (deleteRect.contains((int) event_x, (int) event_y)) {
                        // 点中了删除
                        deleteTieZi();
                    } else if (flipRect.contains((int) event_x, (int) event_y)) {

                        // 点中了翻转
                        Matrix m = new Matrix();
                        m.postScale(-1, 1);

                        curruntTiezhiBitmap = list_V.get(selectImageCount).getImgV().getBitmapSelf();
                        curruntTiezhiBitmap = Bitmap.createBitmap(curruntTiezhiBitmap, 0, 0, curruntTiezhiBitmap.getWidth(), curruntTiezhiBitmap.getHeight(), m, true);
                        list_V.get(selectImageCount).getImgV().setImageBitmap(curruntTiezhiBitmap);
                        list_V.get(selectImageCount).getImgV().setBitmapSelf(curruntTiezhiBitmap);
                        list_V.get(selectImageCount).getImgV().invalidate();
                        frame.invalidate();

                    } else if (transparencyRect.contains((int) event_x, (int) event_y)) {
                        // 点中了改变透明度

                        curruntEditImageAlpha = curruntEditImageAlpha - 51;
                        if (selectImageCount != -1) {
                            curruntEditImageView.setAlpha(curruntEditImageAlpha);
                            if (curruntEditImageAlpha == 51) {
                                curruntEditImageAlpha = 306;
                            }
                        }

                    }

                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:

                if (curruntEditImageView != null) {
                    //保存俩个点的中点 双指缩放时候用
                    midP = midPoint(curruntEditImageView.leftTop, curruntEditImageView.rightBottom);
                    //得到俩个点的距离
                    imgLengHalf = spacing(midP, curruntEditImageView.rightBottom);
                    //起始角度
                    oldRotation = rotationforTwo(event);
                }

                break;

            case MotionEvent.ACTION_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:

                if (event.getPointerCount() == 2 && curruntEditImageView != null) {

                    //模式一定要设置 双指缩放 如果不设置模式可能会和单指冲突
                    mode = DOUBLE_ZOOM;

                    //点击的俩个点
                    float x = event.getX(0) - event.getX(1);
                    float y = event.getY(0) - event.getY(1);

                    float value = (float) Math.sqrt(x * x + y * y);// 计算两点的距离

//                    float newRotation = (float) Math.toDegrees(radians) - oldRotation;
                    float newRotation = rotationforTwo(event) - oldRotation;
                    if (baseValue == 0) {
                        baseValue = value;
                    } else {
                        if (value - baseValue >= 10 || value - baseValue <= -10) {
                            float scale = value / baseValue;// 当前两点间的距离除以手指落下时两点间的距离就是需要缩放的比例。
                            matrix.set(savedMatrix);
                            //计算出缩放比例和旋转角度之后使用矩阵来进行变换
                            matrix.postScale(scale, scale, midP.x, midP.y);
                            matrix.postRotate(newRotation, midP.x, midP.y);
                        }

                    }
                } else if (event.getPointerCount() == 1) {

                    //通过selectImageCount判断是否有View选中
                    if (selectImageCount != -1) {
                        if (mode == DRAG) {
                            //判断边缘
                            if (event_x < MyApplication.getInstance().getScreenWidth() - 50 && event_x > 50
                                    && event_y > 100
                                    && event_y < MyApplication.getInstance().getScreenHeight() - 100) {
                                matrix.set(savedMatrix);
                                // 图片移动的距离
                                float translateX = event_x - startPoinF.x;
                                float translateY = event_y - startPoinF.y;
                                //平移
                                matrix.postTranslate(translateX, translateY);
                            }
                        } else if (mode == ZOOM) {
                            //如果点中了变换
                            PointF movePoin = new PointF(event_x, event_y);

                            float moveLeng = spacing(startPoinF, movePoin);
                            float newRotation = rotation(midP, movePoin) - oldRotation;

                            if (moveLeng > 10f) {
                                float moveToMidLeng = spacing(midP, movePoin);
                                float scale = moveToMidLeng / imgLengHalf;

                                matrix.set(savedMatrix);
                                matrix.postScale(scale, scale, midP.x, midP.y);
                                matrix.postRotate(newRotation, midP.x, midP.y);
                            }
                        }
                    }
                }

                //ACTION_MOVE 对图片的最后位置进行确定
                if (selectImageCount != -1) {
                    //拿到当前的view
                    MyCustomView imgView = list_V.get(selectImageCount).getImgV();
                    //拿到当前view的矩阵
                    float[] f = new float[9];
                    matrix.getValues(f);
                    Rect rect = imgView.getDrawable().getBounds();

                    int bWidth = rect.width();
                    int bHeight = rect.height();
                    // 原图左上角
                    float x1 = f[2];
                    float y1 = f[5];
                    imgView.leftTop.set(x1, y1);
                    // 原图右上角
                    float x2 = f[0] * bWidth + f[2];
                    float y2 = f[3] * bWidth + +f[5];
                    imgView.rightTop.set(x2, y2);
                    // 原图左下角
                    float x3 = f[1] * bHeight + f[2];
                    float y3 = f[4] * bHeight + f[5];
                    imgView.leftBottom.set(x3, y3);
                    // 原图右下角
                    float x4 = f[0] * bWidth + f[1] * bHeight + f[2];
                    float y4 = f[3] * bWidth + f[4] * bHeight + f[5];
                    imgView.rightBottom.set(x4, y4);

                    // 最左边x
                    float minX = 0;
                    // 最右边x
                    float maxX = 0;
                    // 最上边y
                    float minY = 0;
                    // 最下边y
                    float maxY = 0;

                    minX = Math.min(x4, Math.min(x3, Math.min(x1, x2))) - 30;
                    maxX = Math.max(x4, Math.max(x3, Math.max(x1, x2))) + 30;
                    minY = Math.min(y4, Math.min(y3, Math.min(y1, y2))) - 30;
                    maxY = Math.max(y4, Math.max(y3, Math.max(y1, y2))) + 30;

                    list_V.get(selectImageCount).getState().setLeft(minX);
                    list_V.get(selectImageCount).getState().setTop(minY);
                    list_V.get(selectImageCount).getState().setRight(maxX);
                    list_V.get(selectImageCount).getState().setBottom(maxY);
                    //最后保存 确定位置
                    list_V.get(selectImageCount).getImgV().setImageMatrix(matrix);
                }


                break;
        }

        return false;
    }

    //获取StatusBarHeight 亲测准确
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private float rotationforTwo(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    // 得到两个点的距离
    private float spacing(PointF p1, PointF p2) {
        float x = p1.x - p2.x;
        float y = p1.y - p2.y;
        return (float) Math.sqrt(x * x + y * y);
    }

    // 得到两个点的中点
    private PointF midPoint(PointF p1, PointF p2) {
        PointF point = new PointF();
        float x = p1.x + p2.x;
        float y = p1.y + p2.y;
        point.set(x / 2, y / 2);
        return point;
    }

    // 旋转
    private float rotation(PointF p1, PointF p2) {
        double delta_x = (p1.x - p2.x);
        double delta_y = (p1.y - p2.y);
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    // 选中了某张图片
    public void selectImG(float x, float y) {
        for (int i = (list_V.size() - 1); i >= 0; i--) {
            StickersHolder stickersHolder = list_V.get(i);
            if (stickersHolder.getImgV().isSelect()) {
                stickersHolder.getImgV().setSelect(false);
                break;
            }
        }

        for (int i = (list_V.size() - 1); i >= 0; i--) {
            StickersHolder stickersHolder = list_V.get(i);
            Rect rect = new Rect((int) stickersHolder.getState().getLeft(),
                    (int) stickersHolder.getState().getTop(), (int) stickersHolder.getState()
                    .getRight(), (int) stickersHolder.getState().getBottom());
            if (rect.contains((int) x, (int) y)) {
                list_V.get(i).getImgV().bringToFront();
                stickersHolder.getImgV().setSelect(true);
                selectImageCount = i;

                curruntEditImageView = list_V.get(selectImageCount).getImgV();
                curruntTiezhiBitmap = curruntEditImageView.getBitmapSelf();
                frame.invalidate();
                break;
            }
            selectImageCount = -1;
        }
    }

    // 删除贴纸
    private void deleteTieZi() {
        if (selectImageCount != -1) {
            frame.removeView(list_V.get(selectImageCount).getImgV());
            list_V.remove(selectImageCount);
            selectImageCount = -1;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addView:
                Bitmap emoji0 = BitmapFactory.decodeResource(getResources(), R.drawable.androidemojiface0);
                addGesturesView(emoji0);
                break;
        }
    }
}
