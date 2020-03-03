package com.xm.letterindex;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xm.demo.R;

import static com.xm.letterindex.MainActivity.ITEM_TYPE_CONTENT;
import static com.xm.letterindex.MainActivity.ITEM_TYPE_HEADER;

public class LetterIndexAdapter extends BaseMultiItemQuickAdapter<LetterIndexModel, BaseViewHolder> {

    public LetterIndexAdapter() {
        super(null);
        addItemType(ITEM_TYPE_HEADER, R.layout.item_letter_index_header);
        addItemType(ITEM_TYPE_CONTENT, R.layout.item_letter_index);
    }

    @Override
    protected void convert(BaseViewHolder holder, LetterIndexModel model) {
        holder.setText(R.id.tvName, model.name);
        if (model.itemType == ITEM_TYPE_CONTENT) {
            ImageView image = holder.getView(R.id.image);
            Glide.with(mContext).applyDefaultRequestOptions(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                    .load(model.image).into(image);
        }
    }
}
