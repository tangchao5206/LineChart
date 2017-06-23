package cn.me.com.linechart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import cn.me.com.linechart.view.LineGraphicView;

public class MainActivity extends AppCompatActivity {
    private LineGraphicView mLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLine= (LineGraphicView) findViewById(R.id.line);

       ArrayList<Double> yList = new ArrayList<Double>();
        yList.add((double) 21.103);
        yList.add(40.05);
        yList.add((double) 21.103);
        yList.add(40.05);
        yList.add((double) 21.103);
        yList.add(40.05);
        yList.add((double) 21.103);
        yList.add(40.05);

        ArrayList<Double> yList1 = new ArrayList<Double>();
        yList1.add((double) 11.103);
        yList1.add(20.05);


        ArrayList<String> xRawDatas = new ArrayList<String>();
        xRawDatas.add("05-19");
        xRawDatas.add("05-20");
        xRawDatas.add("05-19");
        xRawDatas.add("05-20");
        xRawDatas.add("05-19");
        xRawDatas.add("05-20");
        xRawDatas.add("05-19");
        xRawDatas.add("05-20");

        ArrayList<String> xRawDatas1 = new ArrayList<String>();
        xRawDatas1.add("05-19");
        xRawDatas1.add("05-20");mLine.setData1(yList, xRawDatas,yList1,xRawDatas1, 200, 20,  R.color.colorAccent,R.color.black);

    }
}
