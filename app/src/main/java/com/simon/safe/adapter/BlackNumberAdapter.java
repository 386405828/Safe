package com.simon.safe.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.simon.safe.R;
import com.simon.safe.db.domain.BlackNumberInfo;

import java.util.List;

public class BlackNumberAdapter extends RecyclerView.Adapter<BlackNumberAdapter.ViewHolder> {

    public interface OnItemClickListener {

        void onItemClick(View view, int position);
    }


    private OnItemClickListener mOnItemClickListener;//声明接口

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    private List<BlackNumberInfo> mBlackNumberList;

    public BlackNumberAdapter(List<BlackNumberInfo> blackNumberList) {
        this.mBlackNumberList = blackNumberList;
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_blacknumber_item, viewGroup, false);
        return new ViewHolder(view);
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        viewHolder.mTextViewPhone.setText(mBlackNumberList.get(position).getPhone());
        int mode = Integer.parseInt(mBlackNumberList.get(position).getMode());
        switch (mode) {
            case 1:
                viewHolder.mTextViewMode.setText("拦截短信");
                break;
            case 2:
                viewHolder.mTextViewMode.setText("拦截电话");
                break;
            case 3:
                viewHolder.mTextViewMode.setText("拦截所有");
                break;
        }
        if (mOnItemClickListener != null) {
            viewHolder.mDeleteIv.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int position = viewHolder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(viewHolder.mDeleteIv, position);
                }
            });
        }
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return mBlackNumberList.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
   static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextViewPhone;
        public TextView mTextViewMode;
        public ImageView mDeleteIv;

        public ViewHolder(View view) {
            super(view);
            mTextViewPhone = (TextView) view.findViewById(R.id.tv_phone);
            mTextViewMode = (TextView) view.findViewById(R.id.tv_mode);
            mDeleteIv = (ImageView) view.findViewById(R.id.iv_delete);
        }
    }
}
