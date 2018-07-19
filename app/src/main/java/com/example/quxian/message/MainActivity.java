package com.example.quxian.message;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;

public class MainActivity extends AppCompatActivity {

    private Button mSendBtn;//发送
    private Button mAddBtn; //导入
    private TextView mTipText;
    private ListView mListView;
    private List<Person> lists = new ArrayList<>();
    private MyAdapter mAdapter;

    private final static String TAG = "MainActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //绑定
        mSendBtn = findViewById(R.id.main_send);
        mAddBtn = findViewById(R.id.main_add);
        mListView  = findViewById(R.id.main_listview);
        mTipText = findViewById(R.id.main_tips);

        //listView绑定
        mAdapter = new MyAdapter(this,lists);
        mListView.setAdapter(mAdapter);


        //批量发送
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onClick(View view) {
                if(lists.size()==0){
                    Toast.makeText(getApplicationContext(), "发送列表为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (Person item : lists){
                    sendSms(0,item.getPhoneNumber(),item.getContent());
                }
                Toast.makeText(getApplicationContext(), "正在发送,请稍后...", Toast.LENGTH_SHORT).show();
            }
        });

        //添加文件
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,1);

            }
        });

    }

    /**
     *读取excel信息
     * @param filePath 文件路径
     * @param index 0
     * @return 发送信息集合
     */
    private ArrayList<Person> getXlsData(String filePath, int index) {
        ArrayList<Person> persons = new ArrayList<>();
        try {
            InputStream is = new FileInputStream(filePath);
            Workbook workbook = Workbook.getWorkbook(is);
            Sheet sheet = workbook.getSheet(index);
            int sheetRows = sheet.getRows();
            for (int i = 0; i < sheetRows; i++) {
                Person person = new Person();
                person.setName(sheet.getCell(0, i).getContents());
                person.setPhoneNumber(sheet.getCell(1, i).getContents());
                person.setContent(sheet.getCell(2, i).getContents());
                persons.add(person);
            }
            workbook.close();
        } catch (Exception e) {
            Log.e(TAG, "数据读取错误=" + e);
        }
        return persons;
    }



    private void refreshUI(List<Person> persons) {
        lists.clear();
        lists.addAll(persons);
        mAdapter.notifyDataSetChanged();
    }

    // 异步获取Excel数据信息
    private class ExcelDataLoader extends AsyncTask<String, Void, ArrayList<Person>> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected ArrayList<Person> doInBackground(String... params) {
            return getXlsData(params[0], 0);
        }

        @Override
        protected void onPostExecute(ArrayList<Person> persons) {
            if (persons != null && persons.size() > 0) {
                // 列表显示数据
                refreshUI(persons);
            } else {
                // 加载失败
                Toast.makeText(MainActivity.this, "数据加载失败！", Toast.LENGTH_SHORT);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                Uri uri = data.getData();
                String path = uri.getPath().toString();
                mTipText.setText(path);

                // 执行Excel数据导入
                new ExcelDataLoader().execute(path.trim());
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private void sendSms(final int which, String phone, String context) {
        SubscriptionInfo sInfo = null;

        final SubscriptionManager sManager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

        List<SubscriptionInfo> list = sManager.getActiveSubscriptionInfoList();

        Log.e(TAG, "电话卡数： " + String.valueOf(list.size()));
        for(int i = 0 ; i<list.size();i++){
            Log.e(TAG,list.get(i).toString());
        }

        if (list.size() == 2) {
            // 双卡
            Log.e(TAG,"双卡");
            sInfo = list.get(which);
        } else {
            // 单卡
            Log.e(TAG,"单卡");
            sInfo = list.get(0);
        }

        if (sInfo != null) {
            int subId = sInfo.getSubscriptionId();
            SmsManager manager = SmsManager.getSmsManagerForSubscriptionId(subId);

            if (!TextUtils.isEmpty(phone)) {
                ArrayList<String> messageList =manager.divideMessage(context);
                for(String text:messageList){
                    manager.sendTextMessage(phone, null, text, null, null);
                }
                Toast.makeText(this, "信息正在发送，请稍候", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(this, "无法正确的获取SIM卡信息，请稍候重试",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
