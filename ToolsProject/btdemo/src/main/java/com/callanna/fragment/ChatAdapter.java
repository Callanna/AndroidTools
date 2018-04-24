package com.callanna.fragment;

import com.callanna.ChatBean;
import com.callanna.btdemo.R;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by Callanna on 2017/8/22.
 */

public class ChatAdapter  extends BaseMultiItemQuickAdapter<ChatBean, BaseViewHolder> {
    public ChatAdapter(List<ChatBean> data) {
        super(data);
        addItemType(ChatBean.LEFT, R.layout.item_chat_left);
        addItemType(ChatBean.RIGHT, R.layout.item_chat_right);
    }

    @Override
    protected void convert(BaseViewHolder helper, ChatBean item) {
        int itemViewType = helper.getItemViewType();
        switch (itemViewType) {
            case ChatBean.LEFT:
                helper.setText(R.id.btv_left_msg, item.getContent());
                break;
            case ChatBean.RIGHT:
                helper.setText(R.id.btv_right_msg, item.getContent());
                break;
        }
    }
}


