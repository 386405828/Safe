package com.simon.safe.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.simon.safe.R;
import com.simon.safe.adapter.BlackNumberAdapter;
import com.simon.safe.db.dao.BlackNumberDao;
import com.simon.safe.db.domain.BlackNumberInfo;
import com.simon.safe.utils.LogUtils;
import com.simon.safe.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class BlackNumberActivity extends BaseActivity {

    private static final String TAG = "BlackNumberActivity";
    /** 添加 */
    private ImageButton mBtAdd;
    private XRecyclerView mRecyclerView;
    private BlackNumberDao mDao;
    private List<BlackNumberInfo> mBlackNumberList = new ArrayList<>();
    private BlackNumberAdapter mAdapter;
    private int mode = 1;
    private boolean mIsLoad = false;
    private int mCount;
    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            //4,告知listView可以去设置数据适配器
//            if (mAdapter == null) {
//                mAdapter = new BlackNumberAdapter(mBlackNumberList);
//                mRecyclerView.setAdapter(mAdapter);
//            } else {
            mAdapter.notifyDataSetChanged();
//            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_number);
        setTitle("黑名单管理");
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        mBtAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        mAdapter.setOnItemClickListener(new BlackNumberAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                LogUtils.i(TAG, "点击了：" + position);
                //1,数据库删除
                mDao.delete(mBlackNumberList.get(position - 1).phone);
                //2,集合中的删除
                mBlackNumberList.remove(position - 1);
                //3,通知数据适配器刷新
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {

            @Override
            public void onRefresh() {
                ToastUtil.show(getApplicationContext(),"刷新成功！");
                mAdapter.notifyDataSetChanged();
                mRecyclerView.refreshComplete();
            }

            @Override
            public void onLoadMore() {
                //如果条目总数大于集合大小的时,才可以去继续加载更多
                if (mCount > mBlackNumberList.size()) {
                    //加载下一页数据
                    new Thread() {

                        public void run() {
                            //1,获取操作黑名单数据库的对象
                            mDao = BlackNumberDao.getInstance(getApplicationContext());
                            //2,查询部分数据
                            List<BlackNumberInfo> moreData = mDao.find(mBlackNumberList.size());
                            //3,添加下一页数据的过程
                            mBlackNumberList.addAll(moreData);
                            //4,通知数据适配器刷新
                            mHandler.sendEmptyMessage(0);

                        }
                    }.start();
                }
                mRecyclerView.loadMoreComplete();
            }
        });
    }

    protected void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final AlertDialog dialog = builder.create();
        View view = View.inflate(getApplicationContext(), R.layout.dialog_add_blacknumber, null);
        dialog.setView(view, 0, 0, 0, 0);

        final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);
        RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);

        Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

        //监听其选中条目的切换过程
        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_sms:
                        //拦截短信
                        mode = 1;
                        break;
                    case R.id.rb_phone:
                        //拦截电话
                        mode = 2;
                        break;
                    case R.id.rb_all:
                        //拦截所有
                        mode = 3;
                        break;
                }
            }
        });

        bt_submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //1,获取输入框中的电话号码
                String phone = et_phone.getText().toString();
                if (!TextUtils.isEmpty(phone)) {
                    //2,数据库插入当前输入的拦截电话号码
                    mDao.insert(phone, mode + "");
                    //3,让数据库和集合保持同步(1.数据库中数据重新读一遍,2.手动向集合中添加一个对象(插入数据构建的对象))
                    BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                    blackNumberInfo.phone = phone;
                    blackNumberInfo.mode = mode + "";
                    //4,将对象插入到集合的最顶部
                    mBlackNumberList.add(0, blackNumberInfo);
                    //5,通知数据适配器刷新(数据适配器中的数据有改变了)
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                    //6,隐藏对话框
                    dialog.dismiss();
                } else {
                    ToastUtil.show(getApplicationContext(), "请输入拦截号码");
                }
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void initData() {
        mAdapter = new BlackNumberAdapter(mBlackNumberList);
        mRecyclerView.setAdapter(mAdapter);
        //1,获取操作黑名单数据库的对象
        mDao = BlackNumberDao.getInstance(getApplicationContext());
        new Thread() {

            @Override
            public void run() {
                //2,查询部分数据
                List<BlackNumberInfo> blackNumberList = mDao.find(0);
                mCount = mDao.getCount();
                mBlackNumberList.clear();
                mBlackNumberList.addAll(blackNumberList);
                //3,通过消息机制告知主线程可以去使用包含数据的集合
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initView() {
        mBtAdd = (ImageButton) findViewById(R.id.bt_add);
        mRecyclerView = (XRecyclerView) findViewById(R.id.lv_blacknumber);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);
    }
}
