package com.waiting.timelineview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hechao
 */
public class MainActivity extends AppCompatActivity {

    private TimeLineView mTimeLineView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTimeLineView = (TimeLineView) findViewById(R.id.timeLine);

        List<TimeLineView.Item> items = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            items.add(new TimeLineView.Item(i == 3 ? "测试 测试 测试 测试 测试 测试 测试 测试 测试 测试 测试 " + i : "测试 ", "2017-09-10", i == 3));
        }
        mTimeLineView.setItems(items);
        mTimeLineView.setCurrentItem(4);
        mTimeLineView.setErrorItem(1);
    }
}
