package com.simon.safe.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.simon.safe.R;
import com.simon.safe.adapter.ContactListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactListActivity extends BaseActivity {

    private ListView mLvContact;
    protected static final String tag = "ContactListActivity";
    private List<HashMap<String,String>> contactList = new ArrayList<HashMap<String,String>>();
    private ContactListAdapter mAdapter;

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            //8,填充数据适配器
            mAdapter = new ContactListAdapter(getApplicationContext(),contactList);
            mLvContact.setAdapter(mAdapter);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        setTitle("选择联系人");
        initView();
        initData();
    }

    private void initView() {
        mLvContact = (ListView) findViewById(R.id.lv_contact);
        mLvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //1,获取点中条目的索引指向集合中的对象
                if(mAdapter!=null){
                    HashMap<String, String> hashMap = mAdapter.getItem(position);
                    //2,获取当前条目指向集合对应的电话号码
                    String phone = hashMap.get("phone");
                    //3,此电话号码需要给第三个导航界面使用

                    //4,在结束此界面回到前一个导航界面的时候,需要将数据返回过去
                    Intent intent = new Intent();
                    intent.putExtra("phone", phone);
                    setResult(0, intent);

                    finish();
                }
            }
        });
    }

    /**
     * 获取系统联系人数据方法
     */
    private void initData() {
        //因为读取系统联系人,可能是一个耗时操作,放置到子线程中处理
        new Thread(){
            public void run() {
                //1,获取内容解析器对象
                ContentResolver contentResolver = getContentResolver();
                //2,做查询系统联系人数据库表过程(读取联系人权限)
                Cursor cursor = contentResolver.query(
                        Uri.parse("content://com.android.contacts/raw_contacts"),
                        new String[]{"contact_id"},
                        null, null, null);
                contactList.clear();
                //3,循环游标,直到没有数据为止
                while(cursor.moveToNext()){
                    String id = cursor.getString(0);
//					Log.i(tag, "id = "+id);
                    //4,根据用户唯一性id值,查询data表和mimetype表生成的视图,获取data以及mimetype字段
                    Cursor indexCursor = contentResolver.query(
                            Uri.parse("content://com.android.contacts/data"),
                            new String[]{"data1","mimetype"},
                            "raw_contact_id = ?", new String[]{id}, null);
                    //5,循环获取每一个联系人的电话号码以及姓名,数据类型
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    while(indexCursor.moveToNext()){
                        String data = indexCursor.getString(0);
                        String type = indexCursor.getString(1);

                        //6,区分类型去给hashMap填充数据
                        if(type.equals("vnd.android.cursor.item/phone_v2")){
                            //数据非空判断
                            if(!TextUtils.isEmpty(data)){
                                hashMap.put("phone", data);
                            }
                        }else if(type.equals("vnd.android.cursor.item/name")){
                            if(!TextUtils.isEmpty(data)){
                                hashMap.put("name", data);
                            }
                        }
                    }
                    indexCursor.close();
                    if (!hashMap.isEmpty())
                    contactList.add(hashMap);
                }
                cursor.close();
                //7,消息机制,发送一个空的消息,告知主线程可以去使用子线程已经填充好的数据集合
                mHandler.sendEmptyMessage(0);
            };
        }.start();

    }

}
