package com.hsg.roundindicator;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by Joe on 2017/1/16.
 */

public class RoundIndicatorView extends View{
    private int radius;//内圆半径
    private int mWidth;//控件的宽度
    private int mHeight;//控件的高度
    private Paint paint_1;//内圆画笔
    private Paint paint_2;
    private Paint paint_3;
    private Paint paint_4;
    private Paint paint_5;
    private Context context;
    private int maxNum;//圆盘最大值
    private int startAngle;//圆盘起始角度
    private int sweepAngle;//圆盘扫过的角度
    private int sweepInWidth;//内圆弧宽度
    private int sweepOutWidth;//外圆宽度
    private String[] text ={"较差","中等","良好","优秀","极好"};
    private int[] indicatorColor = {0xffffffff,0x00ffffff,0x99ffffff,0xffffffff};
    private int currentNum = 0;
    public RoundIndicatorView(Context context) {
        super(context);
    }

    public RoundIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        setBackgroundColor(0xFFFF6347);
        initAttrs(attrs);
        initPaint();
    }

    /**
     * 初始化自定义属性
     * @param attrs
     */
    private void initAttrs(AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.RoundIndicatorView);
        maxNum = typedArray.getInt(R.styleable.RoundIndicatorView_maxNum,500);
        startAngle = typedArray.getInt(R.styleable.RoundIndicatorView_startAngle,160);
        sweepAngle = typedArray.getInt(R.styleable.RoundIndicatorView_sweepAngle,220);
        sweepInWidth = dp2px(8);
        sweepOutWidth = dp2px(3);
        typedArray.recycle();
    }

    /**
     * 初始化所有画笔
     */
    private void initPaint(){
        paint_1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_1.setDither(true);
        paint_1.setStyle(Paint.Style.STROKE);
        paint_1.setColor(0xffffffff);
        paint_2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_4 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_5 = new Paint(Paint.ANTI_ALIAS_FLAG);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);

        if (wMode == MeasureSpec.EXACTLY ){
            mWidth = wSize;
        }else {
            mWidth = dp2px(300);
        }
        if (hMode == MeasureSpec.EXACTLY){
            mHeight = hSize;
        }else {
            mHeight = dp2px(400);
        }
        setMeasuredDimension(mWidth,mHeight);
    }

    /**
     * 注意圆的半径不要在构造方法里就去设置，那时候是得不到view的宽高值的，所以我在onDraw方法里设置半径，默认就view宽度的1/4吧。把原点移到view的中心去：
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        radius = getMeasuredWidth()/4;//不要在构造方法里初始化，那时还没测量宽高
        canvas.save();
        canvas.translate(mWidth/2,mWidth/2);

        drawRound(canvas);//画圆盘
        drawScale(canvas);//画刻度及文字
        drawIndicator(canvas);//画芝麻分指示针
        drawCenterText(canvas);//画中间的文字

        canvas.restore();
    }

    /**
     * 画内外圆
     * @param canvas
     */
    private void drawRound(Canvas canvas){
        canvas.save();
        //内圆
        paint_1.setAlpha(0x40);
        paint_1.setStrokeWidth(sweepInWidth);
        RectF rectF = new RectF(-radius,-radius,radius,radius);
        canvas.drawArc(rectF,startAngle,sweepAngle,false,paint_1);
        //外圆
        paint_1.setStrokeWidth(sweepOutWidth);
        int w = dp2px(10);
        RectF rectF1 = new RectF(-radius-w,-radius-w,radius+w,radius+w);
        canvas.drawArc(rectF1,startAngle,sweepAngle,false,paint_1);
        canvas.restore();
    }

    /**
     * 画刻度及刻度值
     * @param canvas
     */
    private void drawScale(Canvas canvas){
        canvas.save();
        float angle = sweepAngle/30;//刻度间隔
        canvas.rotate(-270 + startAngle);//将起始的刻度点旋转到正上方
        for (int i = 0; i <= 30; i++) {
            if (i%6 == 0){//画粗刻度和刻度值
                paint_1.setStrokeWidth(dp2px(2));
                paint_1.setAlpha(0x70);
                canvas.drawLine(0,-radius-sweepInWidth/2,0,-radius+sweepInWidth/2+dp2px(1),paint_1);
                drawText(canvas,i*maxNum/30+"",paint_1);
            }else {//画西刻度
                paint_1.setStrokeWidth(dp2px(1));
                paint_1.setAlpha(0x55);
                canvas.drawLine(0,-radius-sweepInWidth/2,0,-radius+sweepInWidth/2,paint_1);
            }
            if (i==3 || i==9 || i==15 || i==21 || i==27){
                paint_1.setStrokeWidth(dp2px(2));
                paint_1.setAlpha(0x55);
                drawText(canvas,text[0],paint_1);
            }
            canvas.rotate(angle);//逆时针
        }
        canvas.restore();
    }
    private void drawText(Canvas canvas,String text,Paint paint){
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(sp2px(8));
        float width = paint.measureText(text);//相比getTextBounds来说，这个方法获得的类型是float，更精确些
        // Rect rect = new Rect();
        //paint.getTextBounds(text,0,text.length(),rect);
        canvas.drawText(text,-width/2,-radius+dp2px(15),paint);
        paint.setStyle(Paint.Style.STROKE);
    }

    /**
     * 画芝麻分指示针
     * @param c
     */
    private void drawIndicator(Canvas c){
        c.save();
        paint_2.setStyle(Paint.Style.STROKE);
        int sweep;
        if (currentNum <=maxNum){
//            sweep = currentNum/maxNum*sweepAngle;
            sweep = (int)((float)currentNum/(float)maxNum*sweepAngle);
        }else {
            sweep = sweepAngle;
        }
        paint_2.setStrokeWidth(sweepOutWidth);
        Shader shader = new SweepGradient(0,0,indicatorColor,null);
        paint_2.setShader(shader);
        int w = dp2px(10);
        RectF rectF = new RectF(-radius-w,-radius-w,radius+w,radius+w);
        c.drawArc(rectF,startAngle,sweep,false,paint_2);
        float x = (float) ((radius+dp2px(10))*Math.cos(Math.toRadians(startAngle+sweep)));
        float y = (float) ((radius+dp2px(10))*Math.sin(Math.toRadians(startAngle+sweep)));
        paint_3.setStyle(Paint.Style.FILL);
        paint_3.setColor(0xffffffff);
        paint_3.setMaskFilter(new BlurMaskFilter(dp2px(3), BlurMaskFilter.Blur.SOLID));//需关闭硬件加速
        c.drawCircle(x,y,dp2px(3),paint_3);
        c.restore();
    }

    private void drawCenterText(Canvas canvas){
        canvas.save();
        paint_4.setStyle(Paint.Style.FILL);
        paint_4.setTextSize(radius/2);
        paint_4.setColor(0xffffffff);
        canvas.drawText(currentNum+"",-paint_4.measureText(currentNum+"")/2,0,paint_4);

        paint_4.setTextSize(radius/4);
        String content = "信用";
        if (currentNum<maxNum/5){
            content += text[0];
        }else if (currentNum>=maxNum/5 && currentNum <maxNum*2/5){
            content += text[1];
        }else if (currentNum>=maxNum*2/5 && currentNum < maxNum*3/5){
            content += text[2];
        }else if (currentNum>= maxNum*3/5 && currentNum < maxNum*4/5){
            content += text[3];
        }else if (currentNum>=maxNum*4/5){
            content += text[4];
        }
        Rect rect = new Rect();
        paint_4.getTextBounds(content,0,content.length(),rect);
        canvas.drawText(content,-rect.width()/2,rect.height()+20,paint_4);
        canvas.restore();
    }


    //获取当前值
    public int getCurrentNum() {
        return currentNum;
    }

    //设置当前值
    public void setCurrentNum(int currentNum) {
        this.currentNum = currentNum;
        invalidate();
    }
    //设置改变值后的动画
    public void setCurrentNumAnim(int num) {
        float duration = (float)Math.abs(num-currentNum)/maxNum *1500+500; //根据进度差计算动画时间
        ObjectAnimator anim = ObjectAnimator.ofInt(this,"currentNum",num);
        anim.setDuration((long) Math.min(duration,2000));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                int color = calculateColor(value);
                setBackgroundColor(color);
            }
        });
        anim.start();
    }
    private int calculateColor(int value){
        ArgbEvaluator evealuator = new ArgbEvaluator();
        float fraction = 0;
        int color = 0;
        if(value <= maxNum/2){
            fraction = (float)value/(maxNum/2);
            color = (int) evealuator.evaluate(fraction,0xFFFF6347,0xFFFF8C00); //由红到橙
        }else {
            fraction = ( (float)value-maxNum/2 ) / (maxNum/2);
            color = (int) evealuator.evaluate(fraction,0xFFFF8C00,0xFF00CED1); //由橙到蓝
        }
        return color;
    }

    //该view用到的工具类
    protected int dp2px(int dp){
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }
    protected int sp2px(int sp){
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sp,
                getResources().getDisplayMetrics()
        );
    }
    public static DisplayMetrics getScreenMetrics(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm;
    }
}
