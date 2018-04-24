package com.callanna;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by chenhao on 2017/8/17.
 */

public class ChatBean implements MultiItemEntity {
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    private int itemType;
    private String content;

    public ChatBean(int itemType, String content) {
        this.itemType = itemType;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    @Override
    public String toString() {
        return "ChatBean{" +
                "itemType=" + itemType +
                ", content='" + content + '\'' +
                '}';
    }
}
