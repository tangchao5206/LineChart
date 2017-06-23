package cn.me.com.linechart.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import cn.me.com.linechart.R;
import cn.me.com.linechart.utils.DentyUtil;
import cn.me.com.linechart.utils.LogUtils;

import static cn.me.com.linechart.utils.DentyUtil.dip2px;

/**
 * 创 建 人: tangchao
 * 创建日期: 2016/7/4 14:27
 * 修改时间：
 * 修改备注：
 */
public class LineGraphicView extends View
{
    /**
     * 公共部分
     */

    private static final String TAG         = " LineGraphicView";

    private static enum Linestyle
    {
        Line, Curve
    }

    //线的颜色
    private int color0;//第一条线的颜色
    private int color1;
    private int color2;
    private int color3;

    private Context        mContext;
    private Paint          mPaint;
    private Paint       mLinePaint;
    private Paint          mPaintdottedline;

    private Resources      res;
    private DisplayMetrics dm;

    /**
     * data
     */
    private Linestyle mStyle = Linestyle.Curve;

    private int canvasHeight; //画布高度
    private int canvasWidth;    //画布宽度
    private int bheight = 0;   //实际画的区域高度

    private static final int CIRCLE_SIZE = 5; //画的点的半径大小
    /**
     * Y轴最大值
     */
    private int maxValue;
    /**
     * Y轴间距值（每格画多高）
     */
    private int averageValue;

    private int marginTop    = 10;
    private int marginBottom = 50;
    private int marginLeft=50;



    /**
     * 曲线上总点数
     */
    private Point[]           mPoints0;
    private Point[]           mPoints1;
    private Point[]           mPoints2;
    private Point[]           mPoints3;
    /**
     * 纵坐标值
     */
    private ArrayList<Double> yRawData0;
    private ArrayList<Double> yRawData1;
    private ArrayList<Double> yRawData2;
    private ArrayList<Double> yRawData3;
    /**
     * 横坐标值
     */
    private ArrayList<String>  xRawDatas0 = new ArrayList<>();
    private ArrayList<Integer> xList0     = new ArrayList<Integer>();// 记录每个x的值
    private ArrayList<String> xRawDatas1;
    private ArrayList<Integer> xList1 = new ArrayList<Integer>();// 记录每个x的值
    private ArrayList<String> xRawDatas2;
    private ArrayList<Integer> xList2 = new ArrayList<Integer>();// 记录每个x的值
    private int yNum; //y轴画多少个


    private Paint  mFontPaint; //字体画笔
    private int bottomY = 0;

    private float textSize;
    private int textColor;
    private int lineColor;

    private int defaultWidth;
    private int defaultHeight;

    public LineGraphicView(Context context) {
        this(context, null);

    }

    public LineGraphicView(Context context, AttributeSet attrs)

    {
        super(context, attrs);
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.LineGraphicView);

        //第一个参数为属性集合里面的属性，R文件名称：R.styleable+属性集合名称+下划线+属性名称
        //第二个参数为，如果没有设置这个属性，则设置的默认的值
        textSize = t.getDimension(R.styleable.LineGraphicView_text_size, 36);
        textColor = t.getColor(R.styleable.LineGraphicView_text_color, 0x000000);
        lineColor = t.getColor(R.styleable.LineGraphicView_line_color, 0x000000);

        //最后记得将TypedArray对象回收
        t.recycle();

        this.mContext = context;
        initView();


    }



    private void initView() {
        this.res = getContext().getResources();
        this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(lineColor);

        this.mPaintdottedline=new Paint();
        mPaintdottedline.setStyle(Paint.Style.STROKE);
        mPaintdottedline.setStrokeWidth(1);
        mPaintdottedline.setAlpha(64);

        mFontPaint = new Paint();
        mFontPaint.setColor(textColor);
        mFontPaint.setAntiAlias(true);
        mFontPaint.setTextSize(textSize);
        mFontPaint.setTextAlign(Paint.Align.CENTER);


        dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMySize(defaultWidth, widthMeasureSpec);
        int height = getMySize(defaultHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);




    }

    private int getMySize(int defaultSize, int measureSpec) {
        int mySize = defaultSize;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        LogUtils.e(TAG,"onMeasure---Size---"+size);
        switch (mode) {
            case MeasureSpec.UNSPECIFIED: {//如果没有指定大小，就设置为默认大小
                mySize = defaultSize;
                break;
            }
            case MeasureSpec.AT_MOST: {//如果测量模式是最大取值为size
                //我们将大小取最大值,你也可以取其他值
                mySize = size;
                break;
            }
            case MeasureSpec.EXACTLY: {//如果是固定的大小，那就不要去改变它
                mySize = size;
                break;
            }
        }
        return mySize;
    }





    @Override
    protected void onDraw(Canvas canvas)

    {


        canvasWidth=getMeasuredWidth();
        canvasHeight=getMeasuredHeight();
        bheight=canvasHeight-marginBottom;

        // 画直线（横向）
        drawAllXLine(canvas);
        // 画直线（纵向）
        drawAllYLine(canvas);
        // 点的操作设置
        if (mPoints0!=null){
            mPoints0 = getPoints(yRawData0);
            mPaint.setColor(res.getColor(color0));
            mPaint.setStrokeWidth(5.5f);
            mPaint.setStyle(Style.STROKE);
            if ( mPoints0.length>1) {
                if (mStyle == Linestyle.Curve) {
//                    LogUtils.showLogI("draw line0");
                    drawScrollLine(canvas, mPoints0);
                    //canvas.drawLine(blwidh, mPoints0[0].y, mPoints0[0].x, mPoints0[0].y, mPaint);
                } else {

                    drawLine(canvas, mPoints0);

                }
            }

            mPaint.setStyle(Style.FILL);
            for (int i = 0; i < mPoints0.length; i++)
            {
                canvas.drawCircle(mPoints0[i].x, mPoints0[i].y, CIRCLE_SIZE, mPaint);

                canvas.drawText(yRawData0.get(i) + "", mPoints0[i].x - dip2px(mContext,2), mPoints0[i].y - dip2px(mContext,2), mFontPaint);

            }
        }
        if (mPoints1!=null){
            mPoints1 = getPoints(yRawData1);
            mPaint.setColor(res.getColor(color1));
            mPaint.setStrokeWidth(5.5f);
            mPaint.setStyle(Style.STROKE);
            if ( mPoints1.length>1) {
                if (mStyle == Linestyle.Curve) {
                    drawScrollLine(canvas, mPoints1);

                } else {

                    drawLine(canvas, mPoints1);

                }
            }
            mPaint.setStyle(Style.FILL);
            for (int i = 0; i < mPoints1.length; i++)
            {
                canvas.drawCircle(mPoints1[i].x, mPoints1[i].y, CIRCLE_SIZE, mPaint);
                canvas.drawText(yRawData1.get(i) + "", mPoints1[i].x - dip2px(mContext,2), mPoints1[i].y - dip2px(mContext,2), mFontPaint);
            }

        }
        if (mPoints2!=null){
            mPoints2 = getPoints(yRawData2);
            mPaint.setColor(res.getColor(color2));
            mPaint.setStrokeWidth(5.5f);
            mPaint.setStyle(Style.STROKE);
            if ( mPoints2.length>1) {
                if (mStyle == Linestyle.Curve) {
                    drawScrollLine(canvas, mPoints2);

                } else {
                    drawLine(canvas, mPoints2);
                }
            }
            mPaint.setStyle(Style.FILL);
            for (int i = 0; i < mPoints2.length; i++)
            {
                canvas.drawCircle(mPoints2[i].x, mPoints2[i].y, CIRCLE_SIZE , mPaint);
                canvas.drawText(yRawData2.get(i) + "", mPoints2[i].x - dip2px(mContext,2), mPoints2[i].y - dip2px(mContext,2), mFontPaint);
            }

        }
        if (mPoints3!=null){
            mPoints3 = getPoints(yRawData3);
            mPaint.setColor(res.getColor(color3));
            mPaint.setStrokeWidth(5.5f);
            mPaint.setStyle(Style.STROKE);
            if ( mPoints3.length>1) {
                if (mStyle == Linestyle.Curve) {
                    drawScrollLine(canvas, mPoints3);
                   // canvas.drawLine(blwidh, mPoints3[0].y, mPoints3[0].x, mPoints3[0].y, mPaint);
                } else {
                    drawLine(canvas, mPoints3);
                }
            }
            mPaint.setStyle(Style.FILL);
            for (int i = 0; i < mPoints3.length; i++)
            {
                canvas.drawCircle(mPoints3[i].x, mPoints3[i].y, CIRCLE_SIZE , mPaint);
                canvas.drawText(yRawData3.get(i) + "", mPoints3[i].x - dip2px(mContext,2), mPoints3[i].y - dip2px(mContext,2), mFontPaint);
            }

        }



    }

    /**
     *  画所有横向表格，包括x轴
     */
    private void drawAllXLine(Canvas canvas)
    {

        for (int i = 0; i < yNum ; i++)
        {
            
                //画矩形
                //canvas.drawRect(new RectF(blwidh, bheight - (int) (bheight * ((y2-bottomY) / maxValue))+marginTop, canvasWidth +bheight, marginTop+bheight - (int) (bheight * ((y1-bottomY) / maxValue))), mPaint1); //


                if (i==0){
                    canvas.drawLine(marginLeft, bheight - (bheight / yNum) * i , canvasWidth ,
                            bheight - (bheight / yNum) * i , mPaint);// Y坐标
                }
                else {
                    canvas.drawLine(marginLeft, bheight - (bheight / yNum) * i , marginLeft+ dip2px(mContext, 3),
                            bheight - (bheight / yNum) * i , mPaint);// Y坐标上一小段距离
                }

               drawText(averageValue*i+"", 0, (bheight - (bheight / yNum) * i),
                    canvas);// y坐标上的坐标值

            }





    }


    /**
     * 画所有纵向表格，包括y轴
     */
    private void drawAllYLine(Canvas canvas) {
        canvas.drawLine(marginLeft, marginTop, marginLeft
                , bheight , mPaint);
        if (xRawDatas0.size() <= 7) {

            for (int i = 0; i < 7; i++) {
                xList0.add(marginLeft + dip2px(mContext,45) * (i + 1));

                    canvas.drawLine(marginLeft + dip2px(mContext,45) * (i + 1),bheight- dip2px(mContext,3),
                            marginLeft + dip2px(mContext,45) * (i + 1), bheight , mPaint);


            }
            for (int i = 0; i < xRawDatas0.size(); i++) {
                drawText(xRawDatas0.get(i), marginLeft + dip2px(mContext,45) * (i + 1) - dip2px(mContext,13), bheight + dip2px(mContext,13),
                        canvas);// X坐标
            }
        } else {
            for (int i = 0; i < xRawDatas0.size(); i++) {
                xList0.add(marginLeft + dip2px(mContext,45) * (i + 1));
                canvas.drawLine(marginLeft + dip2px(mContext, 45) * (i + 1),bheight- dip2px(mContext,3), marginLeft
                        + dip2px(mContext, 45) * (i + 1), bheight , mPaint);
                drawText(xRawDatas0.get(i), marginLeft + dip2px(mContext, 45) * (i + 1) - dip2px(mContext, 13), bheight + dip2px(mContext, 13),
                        canvas);// X坐标
            }
        }

        // 绘制虚线
        int count_line = 0;
        for (int i = 0; i <xRawDatas0.size(); i++) {
            //绘制竖线 连续四条为一组
            if (count_line == 0 || count_line == 1 || count_line == 3 || count_line == 2) {
                Path path = new Path();
                path.moveTo(marginLeft + dip2px(mContext, 45) * (i + 1), 12 * 1);
                path.lineTo(marginLeft + dip2px(mContext, 45) * (i + 1), bheight);
                PathEffect effects = new DashPathEffect(new float[]{12 * 0.3f, 12 * 0.3f, 12 * 0.3f, 12 * 0.3f}, 12 * 0.1f);
                mPaintdottedline.setPathEffect(effects);
                canvas.drawPath(path, mPaintdottedline);
            }
            count_line++;
            if (count_line >= 4) {
                count_line = 0;
            }
        }

    }

    private void drawScrollLine(Canvas canvas, Point[] point)
    {
        Point startp = new Point();
        Point endp = new Point();
        for (int i = 0; i < point.length - 1; i++)
        {

            startp = point[i];
            endp = point[i + 1];
            int wt = (startp.x + endp.x) / 2;
            Point p3 = new Point();
            Point p4 = new Point();
            p3.y = startp.y;
            p3.x = wt;
            p4.y = endp.y;
            p4.x = wt;
            Path path = new Path();
            path.moveTo(startp.x, startp.y);
            path.cubicTo(p3.x, p3.y, p4.x, p4.y, endp.x, endp.y);
            canvas.drawPath(path, mPaint);
        }
    }

    private void drawLine(Canvas canvas, Point[] point)
    {
        Point startp = new Point();
        Point endp = new Point();
        for (int i = 0; i < point.length - 1; i++)
        {
            startp = point[i];
            endp = point[i + 1];
            canvas.drawLine(startp.x, startp.y, endp.x, endp.y, mPaint);
        }
    }

    private void drawText(String text, int x, int y, Canvas canvas)
    {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setTextSize(25);
        p.setColor(getResources().getColor(R.color.black));
        p.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(text, x, y, p);
    }

    private Point[] getPoints(List<Double> date)
    {
        Point[] points = new Point[date.size()];
        for (int i = 0; i < date.size(); i++)
        {
            int ph = bheight - (int) (bheight * ((date.get(i)-bottomY) / maxValue));

            points[i] = new Point(xList0.get(i), ph + marginTop);
        }
        return points;
    }
//设置一组数据
    public void setData0(ArrayList<Double> yRawData, ArrayList<String> xRawData, int maxValue, int averageValue, int color)
    {

        this.maxValue = maxValue;
        this.averageValue = averageValue;
        this.mPoints0 = new Point[yRawData.size()];
        this.xRawDatas0 = xRawData;
        this.yRawData0 = yRawData;
        this.color0=color;
        this.yNum = maxValue / averageValue;
    }
    //设置两组数据
    public void setData1(ArrayList<Double> yRawData0, ArrayList<String> xRawData0, ArrayList<Double> yRawData1, ArrayList<String> xRawData1 , int maxValue, int averageValue, int color0, int color1)
    {
        this.maxValue = maxValue;
        this.averageValue = averageValue;
        this.mPoints0 = new Point[yRawData0.size()];
        this.mPoints1= new Point[yRawData1.size()];
        this.xRawDatas0 = xRawData0;
        this.yRawData0 = yRawData0;
        this.xRawDatas1 = xRawData1;
        this.yRawData1 = yRawData1;
        this.color0=color0;
        this.color1=color1;
        this.yNum = maxValue / averageValue;

        if (xRawData0.size()>=7){
           defaultWidth= DentyUtil.dip2px(mContext,45) * (xRawDatas0.size()+1);
        }else {
            defaultWidth= DentyUtil.dip2px(mContext,45) *7;
        }
    }
    //设置三组数据
    public void setData2(ArrayList<Double> yRawData0, ArrayList<String> xRawData0, ArrayList<Double> yRawData1, ArrayList<String> xRawData1 , ArrayList<Double> yRawData2, ArrayList<String> xRawData2, int maxValue, int averageValue, int color0, int color1, int color2)
    {
        this.maxValue = maxValue;
        this.averageValue = averageValue;
        this.mPoints0 = new Point[yRawData0.size()];
        this.mPoints1= new Point[yRawData1.size()];
        this.mPoints2= new Point[yRawData2.size()];
        this.xRawDatas0 = xRawData0;
        this.yRawData0 = yRawData0;
        this.xRawDatas1 = xRawData1;
        this.yRawData1 = yRawData1;
        this.xRawDatas2 = xRawData2;
        this.yRawData2 = yRawData2;
        this.color1=color1;
        this.color0=color0;
        this.color2=color2;
        this.yNum = maxValue / averageValue;
    }

    //设置四组数据
    public void setData3(ArrayList<Double> yRawData0, ArrayList<String> xRawData0, ArrayList<Double> yRawData1, ArrayList<String> xRawData1 , ArrayList<Double> yRawData2, ArrayList<String> xRawData2, ArrayList<Double> yRawData3, ArrayList<String> xRawData3, int maxValue, int averageValue, int color0, int color1, int color2, int color3)
    {
        this.maxValue = maxValue;
        this.averageValue = averageValue;
        this.mPoints0 = new Point[yRawData0.size()];
        this.mPoints1= new Point[yRawData1.size()];
        this.mPoints2= new Point[yRawData2.size()];
        this.mPoints3= new Point[yRawData3.size()];
        this.xRawDatas0 = xRawData0;
        this.yRawData0 = yRawData0;
        this.xRawDatas1 = xRawData1;
        this.yRawData1 = yRawData1;
        this.xRawDatas2 = xRawData2;
        this.yRawData2 = yRawData2;
        this.yRawData3 = yRawData3;
        this.color1=color1;
        this.color0=color0;
        this.color2=color2;
        this.color3=color3;
        this.yNum = maxValue / averageValue;
    }



}
