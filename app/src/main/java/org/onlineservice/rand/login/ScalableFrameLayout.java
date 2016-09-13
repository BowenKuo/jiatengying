package org.onlineservice.rand.login;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Rand on 2016/9/11. Great!!
 */
public class ScalableFrameLayout extends FrameLayout implements Scalable,Runnable{
    //Constructor
    public ScalableFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public ScalableFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    //Variables
    private int parentHeight = 0,parentWidth = 0;
    private float x=0, y=0;    // 原本圖片存在的X,Y軸位置
    private int mX=0, mY=0; // 圖片被拖曳的X ,Y軸距離長度

    public static final boolean MAGNIFIED = false, ORIGIN = true;
    public static final int HIDE = 0, SHOW = 1;
    private boolean scaleStatus = ORIGIN;
    private ImageView imageView;
    private TextView textView;
    public static final int OPTNOTCHECKED = 0, OPTCHECHED = 1;
    public int checkedStatus = OPTNOTCHECKED;
    private View view = this;

    //Private Methods
    private void initialize(Context context, AttributeSet attrs, int defaultStyle){
        //Set listener
        this.setOnTouchListener(getListener());
        //new view
        imageView = new ImageView(context, attrs, defaultStyle);
        textView = new TextView(context, attrs, defaultStyle);
        //Inject view
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        this.addView(imageView,params);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        this.addView(textView,params);
        //Set component
        setClickable(true);
        setImageViewVisibility(SHOW);
        setTextViewVisibility(SHOW);
    }

    private void setLayout(final int l, final int t, final int r, final int b){
        this.layout(l,t,r,b);
    }

    private OnTouchListener getListener(){

        return new OnTouchListener() {
            private long firstClick = 0;
            private final long timeSpan = 1000;


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        x = motionEvent.getX();
                        y = motionEvent.getRawY() - view.getTop();

                        if (firstClick == 0){
                            firstClick = System.currentTimeMillis();
                            break;
                        }
                        else if (System.currentTimeMillis() - firstClick <= timeSpan){
                            //TODO scale
                            scale();
                            firstClick = 0;
                            break;
                        }else{
                            firstClick = 0;
                        }

                    case MotionEvent.ACTION_MOVE:
                        mX = (int) (motionEvent.getRawX() - x);
                        mY = (int) (motionEvent.getRawY() - y);
                        parentHeight = ((View)view.getParent()).getHeight();
                        parentWidth = ((View)view.getParent()).getWidth();
                        if (mX>0 && (mX+view.getWidth())<parentWidth
                                && mY>0 && (mY+view.getHeight())<parentHeight){
                            view.layout(mX,mY,mX+view.getWidth(),mY+view.getHeight());
                        }
                        break;
                }
                return true;
            }
        };
    }

    //Override Methods
    @Override @Deprecated
    public void scale() {
        if(scaleStatus){
            scaleStatus = MAGNIFIED;
            this.getLayoutParams().width = 300;
            this.getLayoutParams().height = 300;
            textView.setTextSize(20);
            this.requestLayout();
        }
        else {
            scaleStatus = ORIGIN;
            this.getLayoutParams().height = 150;
            this.getLayoutParams().width = 150;
            textView.setTextSize(10);
            this.requestLayout();
        }
        setLayout(mX,mY,mX+view.getWidth(),mY+view.getHeight());
    }

    @Override
    public void scale(int height, int width, float textSize) {
        this.getLayoutParams().height = height;
        this.getLayoutParams().width = width;
        textView.setTextSize(textSize);
        this.requestLayout();
        setLayout(mX,mY,mX+view.getWidth(),mY+view.getHeight());
    }

    @Override
    public void scale(@NonNull ViewGroup.LayoutParams params, float textSize) {
        this.setLayoutParams(params);
        textView.setTextSize(textSize);
        setLayout(mX,mY,mX+view.getWidth(),mY+view.getHeight());
    }

    @Override
    public void scale(@NonNull FrameLayout frameLayout, float textSize) {
        ViewGroup.LayoutParams params = frameLayout.getLayoutParams();
        this.setLayoutParams(params);
        textView.setTextSize(textSize);
        setLayout(mX,mY,mX+view.getWidth(),mY+view.getHeight());
    }

    @Override @Deprecated
    public void run() {
        this.scale();
    }

    //Public Methods

    public void setText(@NonNull CharSequence text){
        textView.setText(text);
        layout(mX,mY,mX+view.getWidth(),mY+view.getHeight());
    }

    public void init(int height, int width){
        this.getLayoutParams().width = width;
        this.getLayoutParams().height = height;
        this.requestLayout();
        setLayout(mX,mY,mX+view.getWidth(),mY+view.getHeight());
    }

    public void setText(@StringRes int resId){
        textView.setText(resId);
        setLayout(mX,mY,mX+view.getWidth(),mY+view.getHeight());
    }

    public void setImageResource(@DrawableRes int resId){
        imageView.setImageResource(resId);
    }

    public void setImageResource(@NonNull Drawable drawable){
        imageView.setImageDrawable(drawable);
    }

    public void setTextViewVisibility(int ops){
        switch (ops){
            case HIDE:
                textView.setVisibility(INVISIBLE);
            case SHOW:
                textView.setVisibility(VISIBLE);
            default:
                break;
        }
    }

    public void setImageViewVisibility(int ops){
        switch (ops){
            case HIDE:
                imageView.setVisibility(INVISIBLE);
            case SHOW:
                imageView.setVisibility(VISIBLE);
            default:
                break;
        }
    }

    public void show(){
        setVisibility(VISIBLE);
        setLayout(mX,mY,mX+view.getWidth(),mY+view.getHeight());
    }

    public void hide(){
        setVisibility(GONE);
    }

}
