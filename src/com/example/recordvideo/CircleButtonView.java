package com.example.recordvideo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by yufs on 2017/7/4.
 */

public class CircleButtonView extends View{
    private static final int WHAT_LONG_CLICK = 1;
    private Paint mBigCirclePaint;
    private Paint mSmallCirclePaint;
    private Paint mProgressCirclePaint;
    private int mHeight;//��ǰView�ĸ�
    private int mWidth;//��ǰView�Ŀ�
    private float mInitBitRadius;
    private float mInitSmallRadius;
    private float mBigRadius;
    private float mSmallRadius;
    private long mStartTime;
    private long mEndTime;
    private Context mContext;
    private boolean isRecording;//¼��״̬
    private boolean isMaxTime;//�ﵽ���¼��ʱ��
    private float mCurrentProgress;//��ǰ����

    private long mLongClickTime=500;//�������ʱ��(����)��
    private int mTime=5;//¼�����ʱ��s
    private int mMinTime=3;//¼�����ʱ��
    private int mProgressColor;//��������ɫ
    private float mProgressW=18f;//Բ�����

    private boolean isPressed;//��ǰ��ָ���ڰ�ѹ״̬
    private ValueAnimator mProgressAni;//Բ�����ȱ仯


    public CircleButtonView(Context context ) {
        super(context);
        init(context,null);
    }

    public CircleButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    public CircleButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs) {
        this.mContext=context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleButtonView);
        mMinTime=a.getInt(R.styleable.CircleButtonView_minTime,0);
        mTime=a.getInt(R.styleable.CircleButtonView_maxTime,10);
        mProgressW=a.getDimension(R.styleable.CircleButtonView_progressWidth,12f);
        mProgressColor=a.getColor(R.styleable.CircleButtonView_progressColor,Color.parseColor("#6ABF66"));
        a.recycle();
        //��ʼ���ʿ���ݡ���ɫ
        mBigCirclePaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mBigCirclePaint.setColor(Color.parseColor("#DDDDDD"));

        mSmallCirclePaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mSmallCirclePaint.setColor(Color.parseColor("#FFFFFF"));

        mProgressCirclePaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressCirclePaint.setColor(mProgressColor);

        mProgressAni= ValueAnimator.ofFloat(0, 360f);
        mProgressAni.setDuration(mTime*1000);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight=MeasureSpec.getSize(heightMeasureSpec);
        mInitBitRadius=mBigRadius= mWidth/2*0.75f;
        mInitSmallRadius=mSmallRadius= mBigRadius*0.75f;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        //������Բ
        canvas.drawCircle(mWidth/2,mHeight/2,mBigRadius,mBigCirclePaint);
        //������Բ
        canvas.drawCircle(mWidth/2,mHeight/2,mSmallRadius,mSmallCirclePaint);
        //¼�ƵĹ����л��ƽ�����
        if(isRecording){
            drawProgress(canvas);
        }
    }

    /**
     * ����Բ�ν���
     * @param canvas
     */
    private void drawProgress(Canvas canvas) {
        mProgressCirclePaint.setStrokeWidth(mProgressW);
        mProgressCirclePaint.setStyle(Paint.Style.STROKE);
        //���ڶ����Բ������״�ʹ�С�Ľ���
        RectF oval = new RectF(mWidth/2-(mBigRadius-mProgressW/2), mHeight/2-(mBigRadius-mProgressW/2), mWidth/2+(mBigRadius-mProgressW/2),mHeight/2+(mBigRadius-mProgressW/2));
        //���ݽ��Ȼ�Բ��
        canvas.drawArc(oval, -90, mCurrentProgress, false, mProgressCirclePaint);
    }

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case WHAT_LONG_CLICK:
                    //�����¼�����
                    if(onLongClickListener!=null) {
                        onLongClickListener.onLongClick();
                    }
                    //����Բ��������Բ��С����Բ�Ŵ�
                    startAnimation(mBigRadius,mBigRadius*1.33f,mSmallRadius,mSmallRadius*0.7f);
                    break;
            }
        }
    } ;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                isPressed=true;
                mStartTime=System.currentTimeMillis();
                Message mMessage=Message.obtain();
                mMessage.what=WHAT_LONG_CLICK;
                mHandler.sendMessageDelayed(mMessage,mLongClickTime);
                break;
            case MotionEvent.ACTION_UP:
                isPressed=false;
                isRecording=false;
                mEndTime=System.currentTimeMillis();
                if(mEndTime-mStartTime<mLongClickTime){
                    mHandler.removeMessages(WHAT_LONG_CLICK);
                    if(onClickListener!=null)
                        onClickListener.onClick();
                }else{
                    startAnimation(mBigRadius,mInitBitRadius,mSmallRadius,mInitSmallRadius);//��ָ�뿪ʱ������ԭ
                    if(mProgressAni!=null&&mProgressAni.getCurrentPlayTime()/1000<mMinTime&&!isMaxTime){
                        if(onLongClickListener!=null){
                            onLongClickListener.onNoMinRecord(mMinTime);
                        }
                        mProgressAni.cancel();
                    }else{
                        //¼�����
                        if(onLongClickListener!=null&&!isMaxTime){
                            onLongClickListener.onRecordFinishedListener();
                        }
                    }
                }
                break;
        }
        return true;

    }

    private void startAnimation(float bigStart,float bigEnd, float smallStart,float smallEnd) {
        ValueAnimator bigObjAni=ValueAnimator.ofFloat(bigStart,bigEnd);
        bigObjAni.setDuration(150);
        bigObjAni.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBigRadius= (Float) animation.getAnimatedValue();
                invalidate();
            }
        });

        ValueAnimator smallObjAni=ValueAnimator.ofFloat(smallStart,smallEnd);
        smallObjAni.setDuration(150);
        smallObjAni.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSmallRadius= (Float) animation.getAnimatedValue();
                invalidate();
            }
        });

        bigObjAni.start();
        smallObjAni.start();

        smallObjAni.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isRecording=false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //��ʼ����Բ�ν���
                if(isPressed){
                    isRecording=true;
                    isMaxTime=false;
                    startProgressAnimation();
                }
            }



            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    /**
     * Բ�ν��ȱ仯����
     */
    private void startProgressAnimation() {
        mProgressAni.start();
        mProgressAni.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentProgress= (Float) animation.getAnimatedValue();
                invalidate();
            }
        });

        mProgressAni.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //¼�ƶ�������ʱ����Ϊ¼��ȫ�����
                if(onLongClickListener!=null&&isPressed){
                    isPressed=false;
                    isMaxTime=true;
                    onLongClickListener.onRecordFinishedListener();
                    startAnimation(mBigRadius,mInitBitRadius,mSmallRadius,mInitSmallRadius);
                    //Ӱ�ؽ��Ƚ�����
                    mCurrentProgress=0;
                    invalidate();
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * ����������
     */
    public interface OnLongClickListener{
        void onLongClick();
        //δ�ﵽ��С¼��ʱ��
        void onNoMinRecord(int currentTime);
        //¼�����
        void onRecordFinishedListener();
    }
    public OnLongClickListener onLongClickListener;

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    /**
     * ���������
     */
    public interface OnClickListener{
        void onClick();
    }
    public OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

}