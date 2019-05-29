package com.example.billnote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.Serializable;
import java.util.Date;

public class BillActicity extends Activity {
    private SingleBill modifiedBill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        //得到传入的bill
        Intent intent_got = getIntent();
        modifiedBill = (SingleBill) intent_got.getSerializableExtra("bill");
        //设置按钮
        Button button_returnMain = (Button)findViewById(R.id.bt_returnMain);
        button_returnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("bill", modifiedBill);
                setResult(0, intent);
                finish();
            }
        });
    }

    public void fillWithBill(SingleBill bill) {
        modifiedBill = bill;
        //绘制UI

    }

}
