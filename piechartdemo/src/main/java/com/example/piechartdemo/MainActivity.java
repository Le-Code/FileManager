package com.example.piechartdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class MainActivity extends AppCompatActivity implements PieChartOnValueSelectListener {

    private PieChartView pieView;
    private List<SliceValue> list = new ArrayList<>();
    private int[] color = {Color.RED, Color.YELLOW, Color.GREEN};
    private Float[] date = {0.1f, 0.2f, 0.3f};
    private String[] label = {"音乐","视屏","图片"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pieView = (PieChartView) findViewById(R.id.pieView);
        initPieView();
        pieView.setOnValueTouchListener(this);
    }

    private void initPieView() {
        for (int i = 0;i<3;i++){
            SliceValue value = new SliceValue(date[i],color[i]);
            value.setLabel(label[i]);
            list.add(value);
        }
        PieChartData data = new PieChartData();
        data.setHasLabels(false);//显示表情
        data.setHasCenterCircle(false);//是否环形显示
//        data.setHasLabelsOnlyForSelected(false);//不用点击显示占的百分比
        data.setValues(list);//填充数据
//        data.setCenterCircleColor(Color.WHITE);//设置环形中间的颜色
//        data.setCenterText2("Hello Chart");
//        data.setCenterText1Color(Color.BLACK);
//        data.setCenterText1FontSize(15);
        pieView.setPieChartData(data);
        pieView.setCircleFillRatio(0.2f);
        pieView.setValueSelectionEnabled(true);//设置选中变大
    }

    @Override
    public void onValueSelected(int arcIndex, SliceValue value) {
        Toast.makeText(MainActivity.this, "Selected: " + value.getValue(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onValueDeselected() {

    }
}
