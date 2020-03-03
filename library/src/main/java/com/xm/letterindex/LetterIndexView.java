package com.xm.letterindex;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 字母选择指示器
 *
 * @Auth lgdcoder
 * @Date 2020年3月3日
 */
public class LetterIndexView extends View {

    private List<String> letterList = new ArrayList<>();

    private Paint paint;

    private float itemHeight; // 字母每一项的高度
    private int fontSize;

    private int currentPosition = -1;

    private float circleRadius;
    private float circleRadiusExtra; // 默认的选中时圆形的边距，如果不设置，圆形无法完成包含字母（无需自定义）
    private int circlePadding; // 选中时圆形的边距，可自定义
    private int circleColor;
    private boolean drawCircleActionUp = false;

    private int itemPadding;

    private int textColor, textSelectedColor;

    private OnStateChangeListener stateChangeListener;
    private int eventAction;

    private PopupWindow pop;
    private TextView popTextView;
    private boolean showLetterPop = true;

    private View activityRootView;

    public LetterIndexView(Context context) {
        this(context, null, 0);
    }

    public LetterIndexView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LetterIndexView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        loadDefaultSetting();
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.LetterIndex);
            int textSize = array.getInteger(R.styleable.LetterIndex_text_size, 0);
            if (textSize != 0)
                fontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, getResources().getDisplayMetrics());
            int textColor = array.getColor(R.styleable.LetterIndex_text_color, 0);
            if (textColor != 0)
                this.textColor = textColor;
            int textSelectedColor = array.getColor(R.styleable.LetterIndex_text_selected_color, 0);
            if (textSelectedColor != 0)
                this.textSelectedColor = textSelectedColor;
            int circlePadding = (int) array.getDimension(R.styleable.LetterIndex_circle_padding, 0);
            if (circlePadding != 0)
                this.circlePadding = circlePadding;
            int circleColor = array.getColor(R.styleable.LetterIndex_circle_color, 0);
            if (circleColor != 0)
                this.circleColor = circleColor;
            int itemSpace = (int) array.getDimension(R.styleable.LetterIndex_item_space, 0);
            if (itemSpace != 0)
                this.itemPadding = itemSpace / 2;
            boolean drawCircleActionUp = array.getBoolean(R.styleable.LetterIndex_draw_circle_action_up, true);
            this.drawCircleActionUp = drawCircleActionUp;
            showLetterPop = array.getBoolean(R.styleable.LetterIndex_show_pop, true);
            array.recycle();
        }
        paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setTextSize(fontSize);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);

        loadDefaultLetters();
        calculateCircleRadius(true);
        initPopupwindow();
        if (getContext() instanceof Activity)
            activityRootView = getActivityRootView((Activity) getContext());
        invalidate();
    }

    /**
     * 默认数据
     */
    private void loadDefaultSetting() {
        fontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics());
        circleRadiusExtra = dp2px(2);
        circlePadding = dp2px(2);
        itemPadding = dp2px(5);
        textColor = Color.BLACK;
        textSelectedColor = Color.WHITE;
        circleColor = Color.RED;
    }

    private int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        float height = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) { // 固定高度
            itemHeight = height / letterList.size();
        } else { // 自适应高度
            itemHeight = (int) paint.measureText(letterList.get(0)) + itemPadding * 2;
            height = itemHeight * letterList.size();
        }
        height += getPaddingTop() + getPaddingBottom();
        int width = (int) (paint.measureText(letterList.get(0)) + getPaddingLeft() + getPaddingRight());
        setMeasuredDimension(width, (int) height);
    }

    private void calculateCircleRadius(boolean force) {
        if (force || circleRadius == 0)
            circleRadius = paint.measureText(letterList.get(0)) / 2 + circleRadiusExtra + circlePadding;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < letterList.size(); i++) {
            float itemCenterY = i * itemHeight + itemHeight / 2 + getPaddingTop();
            Paint.FontMetrics metrics = paint.getFontMetrics();
            int baseLineY = (int) (itemCenterY + (metrics.bottom - metrics.top) / 2 - metrics.bottom);

            boolean showCircle = (eventAction != MotionEvent.ACTION_UP || (eventAction == MotionEvent.ACTION_UP && drawCircleActionUp));
            if (currentPosition == i && showCircle) {
                paint.setColor(circleColor);
                canvas.drawCircle(getWidth() / 2, itemCenterY, circleRadius, paint);
            }

            paint.setColor(i == currentPosition && showCircle ? textSelectedColor : textColor);
            canvas.drawText(letterList.get(i), getWidth() / 2, baseLineY, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventAction = event.getAction();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (showLetterPop && activityRootView != null) {
                    if (pop == null)
                        initPopupwindow();
                    pop.showAtLocation(activityRootView, Gravity.CENTER, 0, 0);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (pop != null && pop.isShowing())
                    pop.dismiss();
                break;
        }
        int position = getCurrentPosition(event.getY());
        if (currentPosition == position && this.eventAction == eventAction)
            return true;
        currentPosition = position;
        this.eventAction = eventAction;
        popTextView.setText(letterList.get(currentPosition));
        invalidate();
        if (stateChangeListener != null)
            stateChangeListener.onStateChange(eventAction, currentPosition, letterList.get(currentPosition), (int) (itemHeight * currentPosition + itemHeight / 2) + getPaddingTop());
        return true;
    }

    private int getCurrentPosition(float y) {
        if (y >= getHeight())
            return letterList.size() - 1;
        if (y <= 0)
            return 0;
        y -= getPaddingTop();
        int pos = (int) (y / itemHeight);
        if (pos > letterList.size() - 1)
            return letterList.size() - 1;
        return pos;
    }

    /**
     * 加载默认的26个字母
     */
    private void loadDefaultLetters() {
        for (int i = 0; i < 26; i++)
            letterList.add(String.valueOf(Character.toUpperCase((char) (65 + i))));
    }

    private void initPopupwindow() {
        popTextView = createPopTextView();
        pop = new PopupWindow(popTextView, dp2px(90), dp2px(90));
        pop.setBackgroundDrawable(getShapeDrawable());
    }

    private View getActivityRootView(Activity activity) {
        View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        if (rootView == null)
            rootView = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        return rootView;
    }

    private TextView createPopTextView() {
        TextView textView = new TextView(getContext());
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(params);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 25, getResources().getDisplayMetrics()));
        return textView;
    }

    /**
     * 创建默认的Shape背景
     */
    private Drawable getShapeDrawable() {
        int raduis = 20;
        float[] innerRadii = {raduis, raduis, raduis, raduis, raduis, raduis, raduis, raduis};
        RoundRectShape roundRectShape = new RoundRectShape(innerRadii, new RectF(), innerRadii);
        ShapeDrawable drawable = new ShapeDrawable(roundRectShape);
        drawable.getPaint().setColor(Color.argb(78, 128, 125, 120));
        drawable.getPaint().setStyle(Paint.Style.FILL);
        return drawable;
    }

    public interface OnStateChangeListener {
        void onStateChange(int eventAction, int position, String letter, int itemCenterY);
    }

    public LetterIndexView setOnStateChangeListener(OnStateChangeListener listener) {
        this.stateChangeListener = listener;
        return this;
    }

    /**
     * 当前字母总数
     */
    public int getLetterCount() {
        return letterList.size();
    }

    /**
     * 可在字母先后添加额外的字母
     */
    public LetterIndexView addLetter(int position, String... letter) {
        if (letter != null && letter.length > 0) {
            for (String l : letter)
                letterList.add(position, l);
            invalidate();
        }
        return this;
    }

    /**
     * 设置字体大小
     *
     * @param size 单位 SP
     */
    public LetterIndexView setFontSize(int size) {
        fontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, getResources().getDisplayMetrics());
        invalidate();
        return this;
    }

    /**
     * 设置字体颜色
     */
    public LetterIndexView setTextColor(@ColorInt int color) {
        textColor = color;
        invalidate();
        return this;
    }

    /**
     * 设置字体选中时的颜色
     */
    public LetterIndexView setTextSelectedColor(@ColorInt int color) {
        textSelectedColor = color;
        invalidate();
        return this;
    }

    /**
     * 设置选中时的圆形背景颜色，如果不需要圆形背景，可设置完全透明颜色即可
     */
    public LetterIndexView setCircleColor(@ColorInt int color) {
        circleColor = color;
        invalidate();
        return this;
    }

    /**
     * 设置选中时的圆形的内边距
     */
    public LetterIndexView setCirclePadding(int padding) {
        circlePadding = dp2px(padding);
        invalidate();
        return this;
    }

    /**
     * 设置两个字母Item之间的距离
     *
     * @param space 单位 DP
     * @return
     */
    public LetterIndexView setItemSpace(int space) {
        itemPadding = dp2px(space) / 2;
        invalidate();
        return this;
    }

    /**
     * 设置 手势离开屏幕后选中字母是否显示圆形背景，默认 false
     *
     * @param draw
     * @return
     */
    public LetterIndexView setDrawCircleActionUp(boolean draw) {
        drawCircleActionUp = draw;
        invalidate();
        return this;
    }

    /**
     * 设置 滑动时，是否显示默认的 提示 泡泡窗口，默认 true，不支持自定义，如需自定义 可通过setOnStateChangeListener监听状态做相应的处理
     */
    public LetterIndexView setShowLetterPop(boolean showLetterPop) {
        this.showLetterPop = showLetterPop;
        invalidate();
        return this;
    }

}
