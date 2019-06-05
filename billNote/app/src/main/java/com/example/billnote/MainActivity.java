package com.example.billnote;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.ArrayList;

/*
 *  图标
 *      报表
 *      左右箭头
 *      加号
 */


public class MainActivity extends Activity implements AdapterView.OnItemClickListener {
    private LinkedList<SingleBill> bills;//全部bill的列表
    private int[] listIndexMap;
    //主页bill列表的UI控件
    private ListView listView;
    private ArrayList<String> listData;
    private ArrayAdapter adapter;
    private Date selectedMonthDate;//显示这个月的账单
    private  TextView selectedMonthText;

    private void initDIY() {
        bills = new LinkedList<SingleBill>();
        listIndexMap = new int[1000];
        selectedMonthDate = new Date();
        selectedMonthText = (TextView)findViewById(R.id.lb_selectedMonth);
        listView = (ListView)findViewById(R.id.lv_billList);
        listView.setOnItemClickListener(this);
        listData = new ArrayList<String>();
        //模拟三个数据
        bills.add(new SingleBill());
        bills.add(new SingleBill());
        bills.add(new SingleBill());
        //填list数据
        fillListWithData();
        //设置各个按钮的点击函数
        ImageButton button_add = (ImageButton)findViewById(R.id.bt_add);
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBillActivity(new SingleBill(), true);
            }
        });
        ImageButton button_lastMonth = (ImageButton)findViewById(R.id.bt_lastMonth);
        button_lastMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(selectedMonthDate);
                calendar.add(Calendar.MONTH, -1);
                selectedMonthDate = calendar.getTime();
                fillListWithData();
            }
        });
        ImageButton button_nextMonth = (ImageButton)findViewById(R.id.bt_nextMonth);
        button_nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(selectedMonthDate);
                calendar.add(Calendar.MONTH, 1);
                selectedMonthDate = calendar.getTime();
                fillListWithData();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDIY();
        //动态请求权限
        if (Build.VERSION.SDK_INT>=23){
            //相册（SD卡）
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},1);
        }

        //
        ImageButton button_showReport = (ImageButton) findViewById(R.id.bt_showReport);
        button_showReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("有请teammate完成接下来的工作");
            }
        });
    }

    //设置点击list一行的事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
        showBillActivity(bills.get(listIndexMap[position]), false);
        bills.remove(listIndexMap[position]);
    }

    //根据month筛选并显示list
    private void fillListWithData() {
        //填Month字符串
        String monthStr;
        System.out.println(selectedMonthDate.toString());
        monthStr = selectedMonthDate.toString().substring(selectedMonthDate.toString().length()-4, selectedMonthDate.toString().length());
        monthStr = monthStr + " " + selectedMonthDate.toString().substring(4, 7);
        selectedMonthText.setText(monthStr);
        //填list
        listData.clear();
        String str;
        int i;
        for (i=bills.size()-1; i>=0; --i)
        {
            String tempDateStr = bills.get(i).getDate().toString();
            if (monthStr.substring(0, 4).equals(tempDateStr.substring(tempDateStr.length()-4, tempDateStr.length())) &&
                    monthStr.substring(5, 8).equals(tempDateStr.substring(4, 7)))
            {
                str = "Day" + bills.get(i).getDate().toString().substring(8, 16);
                str = "    " + str + "                                                 ";
                NumberFormat format = new DecimalFormat("0.00");
                if (bills.get(i).getMoney() >= 1.)
                    str = str + "+";
                str = str + format.format(bills.get(i).getMoney()) + "元";
                listData.add(str);
                listIndexMap[listData.size()-1] = i;
            }
        }
        adapter = new ArrayAdapter<String>(
                MainActivity.this,
                android.R.layout.simple_list_item_1,
                listData);
        listView.setAdapter(adapter);
    }

    //切换到bill的Activity
    private void showBillActivity(SingleBill bill, boolean isEditing) {
        Intent intent = new Intent(MainActivity.this, BillActicity.class);
        intent.putExtra("bill", bill);
        intent.putExtra("isEditing", isEditing);
        startActivityForResult(intent, 0);
    }

    //获取billActivity返回的bill并刷新界面
    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SingleBill bill = (SingleBill) data.getSerializableExtra("bill");
        if (bill != null)
        {
            int i=0;
            Date date = bill.getDate();
            while (i < bills.size() && date.compareTo(bills.get(i).getDate()) < 0)
            {
                ++i;
            }
            bills.add(i, bill);
        }
        fillListWithData();
    }
}
