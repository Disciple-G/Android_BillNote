package com.example.billnote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.ArrayList;

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
        Button button_add = (Button)findViewById(R.id.bt_add);
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBillActivity(new SingleBill());
            }
        });
        Button button_lastMonth = (Button)findViewById(R.id.bt_lastMonth);
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
        Button button_nextMonth = (Button)findViewById(R.id.bt_nextMonth);
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

        Button button_showReport = (Button)findViewById(R.id.bt_showReport);
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
        showBillActivity(bills.get(listIndexMap[position]));
        bills.remove(listIndexMap[position]);
    }

    //根据month筛选并显示list
    private void fillListWithData() {
        //填Month字符串
        String monthStr;
        monthStr = selectedMonthDate.toString().substring(24, 28);
        monthStr = monthStr + " " + selectedMonthDate.toString().substring(4, 7);
        selectedMonthText.setText(monthStr);
        //填list
        listData.clear();
        String str;
        int i;
        for (i=bills.size()-1; i>=0; --i)
        {
            String tempDateStr = bills.get(i).getDate().toString();
            if (monthStr.substring(0, 4).equals(tempDateStr.substring(24, 28)) &&
                    monthStr.substring(5, 8).equals(tempDateStr.substring(4, 7)))
            {
                str = bills.get(i).getDate().toString().substring(8, 19);
                str = "    " + str + "                                                    " + String.valueOf(bills.get(i).getMoney())+ " 元";
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
    private void showBillActivity(SingleBill bill) {
        Intent intent = new Intent(MainActivity.this, BillActicity.class);
        intent.putExtra("bill", bill);
        startActivityForResult(intent, 0);
    }

    //获取billActivity返回的bill并刷新界面
    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SingleBill bill = (SingleBill) data.getSerializableExtra("bill");
        int i=0;
        Date date = bill.getDate();
        while (i < bills.size() && date.compareTo(bills.get(i).getDate()) < 0)
        {
            ++i;
        }
        bills.add(i, bill);
        fillListWithData();
    }
}
