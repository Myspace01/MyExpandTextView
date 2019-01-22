package com.elson.lib.expandtextview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.elson.lib.expandtextview.ExpandTextView.Callback;

public class MainActivity extends AppCompatActivity
{

    private boolean state = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ExpandTextView tv_content = findViewById(R.id.tv_content);
        tv_content.setMaxLineCount(4);
        //收起文案
        tv_content.setCollapseText("收起");
        //展开文案
        tv_content.setExpandText("点击展开");
        //是否支持收起功能
        tv_content.setCollapseEnable(true);
        //是否给展开收起添加下划线
        tv_content.setUnderlineEnable(true);
        //收起文案颜色
        tv_content.setCollapseTextColor(Color.parseColor("#FF1C7FFD"));
        //展开文案颜色
        tv_content.setExpandTextColor(Color.parseColor("#FF1C7FFD"));
        tv_content.setText("      好像是每个人都拖着旧行李  好像是每个人都住在电话里  有人不常联系有人似友似敌  铃声不响我便记不起  什么才是真理  噢失恋快失忆  他们说爱过就可以伤和别离  说什么不会忘记说什么保持联系  明天遇见他却牵着新的伴侣  要讲什么道理", state, 
                new Callback()
        {
            @Override
            public void onCollapseClick()
            {
                state = !state;
                tv_content.setChanged(state);
            }

            @Override
            public void onExpandClick()
            {
                state = !state;
                tv_content.setChanged(state);
            }

            @Override
            public void onLoss()
            {

            }

            @Override
            public void onCollapse()
            {

            }

            @Override
            public void onExpand()
            {

            }
        });
    }
}
