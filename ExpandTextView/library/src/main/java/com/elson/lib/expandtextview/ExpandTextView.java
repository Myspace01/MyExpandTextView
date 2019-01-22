package com.elson.lib.expandtextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.View;

public class ExpandTextView extends AppCompatTextView
{
    /** 展开状态 true：展开，false：收起 */
    private boolean expandState = false;
    /** 状态接口 */
    private Callback mCallback;
    /** 源文字内容 */
    private String mText = "";
    /** 最多展示的行数 */
    private int maxLineCount;
    /** 省略文字 */
    private String ellipsizeText;
    /** 展开文案文字 */
    private String expandText;
    /** 展开文案文字颜色 */
    private int expandTextColor;
    /** 收起文案文字 */
    private String collapseText;
    /** 收起文案文字颜色 */
    private int collapseTextColor;
    /** 是否支持收起功能 */
    private boolean collapseEnable = false;
    /** 是否添加下划线 */
    private boolean underlineEnable = true;


    public ExpandTextView(Context context)
    {
        this(context, null);
    }

    public ExpandTextView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public ExpandTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initTextView(context, attrs, defStyle);
    }

    private void initTextView(Context context, AttributeSet attrs, int defStyle)
    {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandTextView, defStyle, 0);
        maxLineCount = a.getInt(R.styleable.ExpandTextView_maxLineCount, 3);
        ellipsizeText = a.getString(R.styleable.ExpandTextView_ellipsizeText);
        if (TextUtils.isEmpty(ellipsizeText))
        {
            ellipsizeText = "...";
        }
        expandText = a.getString(R.styleable.ExpandTextView_expandText);
        if (TextUtils.isEmpty(expandText))
        {
            expandText = "展开";
        }
        expandTextColor = a.getColor(R.styleable.ExpandTextView_expandTextColor, ContextCompat.getColor(context, android.R.color.holo_blue_dark));

        collapseText = a.getString(R.styleable.ExpandTextView_collapseText);
        if (TextUtils.isEmpty(collapseText))
        {
            collapseText = "收起";
        }
        collapseTextColor = a.getColor(R.styleable.ExpandTextView_collapseTextColor, ContextCompat.getColor(context, android.R.color.holo_blue_dark));
        collapseEnable = a.getBoolean(R.styleable.ExpandTextView_collapseEnable, false);
        underlineEnable = a.getBoolean(R.styleable.ExpandTextView_collapseEnable, true);
        setMovementMethod(LinkMovementMethod.getInstance());
        a.recycle();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 文字计算辅助工具
        if (TextUtils.isEmpty(mText))
        {
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
        }
        //StaticLayout对象
        StaticLayout sl = new StaticLayout(mText, getPaint(), getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), Layout.Alignment.ALIGN_CENTER, 1f, 
                0f, true);
        // 总计行数
        int lineCount = sl.getLineCount();
        //总行数大于最大行数
        if (lineCount > maxLineCount)
        {
            if (expandState)
            {
                setText(mText);
                //是否支持收起功能
                if (collapseEnable)
                {
                    // 收起文案和源文字组成的新的文字
                    String newEndLineText = mText + collapseText;
                    //收起文案和源文字组成的新的文字
                    SpannableString spannableString = new SpannableString(newEndLineText);
                    //给收起设成监听
                    spannableString.setSpan(new ClickableSpan()
                    {
                        @Override
                        public void onClick(View widget)
                        {
                            if (null != mCallback)
                            {
                                mCallback.onCollapseClick();
                            }
                        }
                    }, newEndLineText.length() - collapseText.length(), newEndLineText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    if (underlineEnable)
                    {
                        //给收起添加下划线
                        spannableString.setSpan(new UnderlineSpan(), newEndLineText.length() - collapseText.length(), newEndLineText.length(), Spannable
                                .SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    //给收起设成蓝色
                    spannableString.setSpan(new ForegroundColorSpan(collapseTextColor), newEndLineText.length() - collapseText.length(), newEndLineText
                            .length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    setText(spannableString);
                }
                if (null != mCallback)
                {
                    mCallback.onExpand();
                }
            }
            else
            {
                lineCount = maxLineCount;
                // 省略文字和展开文案的宽度
                float dotWidth = getPaint().measureText(ellipsizeText + expandText);
                // 找出显示最后一行的文字
                int start = sl.getLineStart(lineCount - 1)-1;
                int end = sl.getLineEnd(lineCount - 1);
                String lineText = mText.substring(start, end);
                // 将第最后一行最后的文字替换为 ellipsizeText和expandText
//                int endIndex = 0;
//
//                for (int i = lineText.length() - 1; i >= 0; i--)
//                {
//                    String str = lineText.substring(i, lineText.length());
//                    // 找出文字宽度大于 ellipsizeText 的字符
//                    if (getPaint().measureText(str) >= dotWidth)
//                    {
//                        endIndex = i;
//                        break;
//                    }
//                }
                // 新的文字
                String newEndLineText = mText.substring(0, start)+ ellipsizeText + expandText;
                //全部文字
                SpannableString spannableString = new SpannableString(newEndLineText);
                //给查看全部设成监听
                spannableString.setSpan(new ClickableSpan()
                {
                    @Override
                    public void onClick(View widget)
                    {
                        if (null != mCallback)
                        {
                            mCallback.onExpandClick();
                        }
                    }
                }, newEndLineText.length() - expandText.length(), newEndLineText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                if (underlineEnable)
                {
                    spannableString.setSpan(new UnderlineSpan(), newEndLineText.length() - expandText.length(), newEndLineText.length(), Spannable
                            .SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                //给查看全部设成颜色
                spannableString.setSpan(new ForegroundColorSpan(expandTextColor), newEndLineText.length() - expandText.length(), newEndLineText.length(), Spannable
                        .SPAN_EXCLUSIVE_EXCLUSIVE);
                // 最终显示的文字
                setText(spannableString);
                if (null != mCallback)
                {
                    mCallback.onCollapse();
                }
            }
        }
        else
        {
            setText(mText);
            if (null != mCallback)
            {
                mCallback.onLoss();
            }
        }
        // 重新计算高度
        int lineHeight = 0;
        for (int i = 0; i < lineCount; i++)
        {
            Rect lineBound = new Rect();
            sl.getLineBounds(i, lineBound);
            lineHeight += lineBound.height();
        }
        lineHeight = getPaddingTop() + getPaddingBottom() + (int) (lineHeight * getLineSpacingMultiplier());
        setMeasuredDimension(getMeasuredWidth(), lineHeight);
    }

    /**
     * 设置要显示的文字以及状态
     *
     * @param text
     * @param expanded true：展开，false：收起
     * @param callback
     */
    public void setText(String text, boolean expanded, Callback callback)
    {
        this.mText = text;
        this.expandState = expanded;
        this.mCallback = callback;
        // 设置要显示的文字，这一行必须要，否则 onMeasure 宽度测量不正确
        setText(text);
    }

    /**
     * 展开收起状态变化
     *
     * @param expanded
     */
    public void setChanged(boolean expanded)
    {
        expandState = expanded;
        requestLayout();
    }

    public interface Callback
    {
        /**
         * 展开状态
         */
        void onExpand();

        /**
         * 收起状态
         */
        void onCollapse();

        /**
         * 行数小于最小行数，不满足展开或者收起条件
         */
        void onLoss();

        /**
         * 点击全文
         */
        void onExpandClick();

        /**
         * 点击收起
         */
        void onCollapseClick();
    }


    public boolean isExpandState()
    {
        return expandState;
    }

    public void setExpandState(boolean expandState)
    {
        this.expandState = expandState;
    }

    public Callback getmCallback()
    {
        return mCallback;
    }

    public void setmCallback(Callback mCallback)
    {
        this.mCallback = mCallback;
    }

    public String getmText()
    {
        return mText;
    }

    public void setmText(String mText)
    {
        this.mText = mText;
    }

    public int getMaxLineCount()
    {
        return maxLineCount;
    }

    public void setMaxLineCount(int maxLineCount)
    {
        this.maxLineCount = maxLineCount;
    }

    public String getEllipsizeText()
    {
        return ellipsizeText;
    }

    public void setEllipsizeText(String ellipsizeText)
    {
        this.ellipsizeText = ellipsizeText;
    }

    public String getExpandText()
    {
        return expandText;
    }

    public void setExpandText(String expandText)
    {
        this.expandText = expandText;
    }

    public int getExpandTextColor()
    {
        return expandTextColor;
    }

    public void setExpandTextColor(int expandTextColor)
    {
        this.expandTextColor = expandTextColor;
    }

    public String getCollapseText()
    {
        return collapseText;
    }

    public void setCollapseText(String collapseText)
    {
        this.collapseText = collapseText;
    }

    public int getCollapseTextColor()
    {
        return collapseTextColor;
    }

    public void setCollapseTextColor(int collapseTextColor)
    {
        this.collapseTextColor = collapseTextColor;
    }

    public boolean isCollapseEnable()
    {
        return collapseEnable;
    }

    public void setCollapseEnable(boolean collapseEnable)
    {
        this.collapseEnable = collapseEnable;
    }

    public boolean isUnderlineEnable()
    {
        return underlineEnable;
    }

    public void setUnderlineEnable(boolean underlineEnable)
    {
        this.underlineEnable = underlineEnable;
    }
}
