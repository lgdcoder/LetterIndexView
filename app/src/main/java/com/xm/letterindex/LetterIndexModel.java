package com.xm.letterindex;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class LetterIndexModel implements MultiItemEntity {

    public String name;
    public String pinyin;
    public int itemType;
    public String image;

    @Override
    public int getItemType() {
        return itemType;
    }
}
