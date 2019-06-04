package com.example.billnote;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BillActicity extends Activity {
    private SingleBill modifiedBill;
    private boolean isEditing;

    private  Button button_billEdit;
    private  Button button_locateHere;
    private Button button_billDate;
    private Button button_billDTime;
    private EditText editText_billMoney;
    private EditText editText_billEvent;
    private EditText editText_billLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        //得到传入的bill
        Intent intent_got = getIntent();
        modifiedBill = (SingleBill) intent_got.getSerializableExtra("bill");
        isEditing = (boolean) intent_got.getBooleanExtra("isEditing", false);
        //设置顶端按钮
        Button button_billReturn = (Button) findViewById(R.id.bt_billReturn);
        button_billReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromUI();
                Intent intent = new Intent();
                intent.putExtra("bill", modifiedBill);
                setResult(0, intent);
                finish();
            }
        });
        button_billEdit = (Button) findViewById(R.id.bt_billEdit);
        button_billEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEditing = !isEditing;
                setUIByEdit();
            }
        });
        Button button_billDelete = (Button) findViewById(R.id.bt_billDelete);
        button_billDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifiedBill = null;
                Intent intent = new Intent();
                intent.putExtra("bill", modifiedBill);
                setResult(0, intent);
                finish();
            }
        });
        //初始化日期按钮
        button_billDate =(Button) findViewById(R.id.bt_billDate);
        button_billDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editBillDate((Button) v);
            }
        });
        button_billDTime = (Button) findViewById(R.id.bt_billTime);
        button_billDTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editBillTime((Button) v);
            }
        });
        //初始化else
        editText_billMoney = (EditText) findViewById(R.id.et_billMoney);
        editText_billEvent = (EditText) findViewById(R.id.et_billEvent);
        editText_billLocation = (EditText) findViewById(R.id.et_billLocation);
        //设置获取位置按钮
        button_locateHere = (Button) findViewById(R.id.bt_locateHere);
        button_locateHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_billLocation.setText(getAutoLocationStr());
            }
        });
        //System.out.println(getAutoLocationStr());
        System.out.println(modifiedBill.getDate().toString());

        fillWithData();
        setUIByEdit();
    }

    private String getAutoLocationStr() {
        LocationManager locationManager;
        String provider = null;
        Location location;
        //获取定位服务
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.GPS_PROVIDER))
        {
            provider = LocationManager.GPS_PROVIDER;
        }
        else if (providerList.contains(LocationManager.NETWORK_PROVIDER))
        {
            provider = LocationManager.NETWORK_PROVIDER;
        }
        else
        {
            Toast.makeText(this, "请检查网络或GPS是否打开",
                    Toast.LENGTH_LONG).show();
        }
        if (provider != null)
        {
            if (ActivityCompat.checkSelfPermission(this.getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this.getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
            {
                return null;
            }
            location = locationManager.getLastKnownLocation(provider);
            return location.toString();
        }
        return "here";
    }

    private void fillWithData()
    {
        //为UI填数据
        String str;
        String dateStr = modifiedBill.getDate().toString();
        str = dateStr.substring(4, 7) + " " + dateStr.substring(8, 10)
                + " " + dateStr.substring(24, 28);
        button_billDate.setText(str);
        str = dateStr.substring(11, 16);
        button_billDTime.setText(str);
        //Money
        NumberFormat format = new DecimalFormat("0.00");
        str = format.format(modifiedBill.getMoney());
        editText_billMoney.setText(str);
        editText_billEvent.setText(modifiedBill.getEvent());
        editText_billLocation.setText(modifiedBill.getLocation());
        //还差图片
    }

    private void setUIByEdit()
    {
        if (isEditing)
        {
            button_billEdit.setText("完成");
            button_locateHere.setVisibility(View.VISIBLE);

        }
        else
        {
            button_billEdit.setText("编辑");
            button_locateHere.setVisibility(View.GONE);
        }
        button_billDate.setEnabled(isEditing);
        button_billDTime.setEnabled(isEditing);
        editText_billMoney.setEnabled(isEditing);
        editText_billEvent.setEnabled(isEditing);
        editText_billLocation.setEnabled(isEditing);
        //还差图片
    }

    private void getDataFromUI()
    {
        modifiedBill.setMoney(Float.parseFloat(editText_billMoney.getText().toString()));
        modifiedBill.setEvent(editText_billEvent.getText().toString());
        modifiedBill.setLocation(editText_billLocation.getText().toString());
    }

    private void editBillDate(final Button button)
    {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(modifiedBill.getDate());
        final DatePickerDialog DPD;
        DPD = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                Date date = calendar.getTime();
                modifiedBill.setDate(date);
                String dateStr = date.toString();
                String str = dateStr.substring(4, 7) + " " + dateStr.substring(8, 10)
                        + " " + dateStr.substring(24, 28);
                button.setText(str);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        DPD.show();
    }

    private void editBillTime(final Button button)
    {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(modifiedBill.getDate());
        final TimePickerDialog TPD;
        TPD = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                Date date = calendar.getTime();
                modifiedBill.setDate(date);
                String dateStr = date.toString();
                String str = dateStr.substring(11, 16);
                button.setText(str);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        TPD.show();
    }

}
