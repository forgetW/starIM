package com.qunar.im.ui.view.faceGridView;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.ui.R;
import com.qunar.im.ui.sdk.QIMSdk;
import com.qunar.im.ui.util.FacebookImageUtil;
import com.qunar.im.base.util.Utils;
import com.qunar.im.base.view.faceGridView.EmoticionMap;
import com.qunar.im.base.view.faceGridView.EmoticonEntity;
import com.qunar.im.ui.view.bigimageview.view.MyGlideUrl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by xinbo.wang on 2015/2/5.
 */
public class FaceAdapter extends BaseAdapter {
    private EmoticionMap data;
    Context context;
    int pos;
    int itemCount;

    public FaceAdapter(Context context, EmoticionMap data, int pos, int itemCount) {
        this.data = data;
        this.context = context;
        this.pos = pos;
        this.itemCount = itemCount;
    }

    @Override
    public int getCount() {

        if ((pos + 1) * itemCount > data.count) {
            return data.count - pos * itemCount;
        }
        return itemCount;
    }

    @Override
    public EmoticonEntity getItem(int position) {
        int index = pos * itemCount + position;
        return data.getEntity(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EmoticonEntity emoji = getItem(position);
        GridViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new GridViewHolder();
            convertView = viewHolder.layoutView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GridViewHolder) convertView.getTag();
        }

        if (emoji.fileFiexd.startsWith("emoticons/") || emoji.fileFiexd.startsWith("Big_Emoticons/")) {
            if (data.showAll == 1)
                viewHolder.defaultSize();
            else viewHolder.extSize();


//            viewHolder.faceIv.setImageResource(R.mipmap.ic_controller_easy_photos);
            try {
                AssetManager assetManager = context.getAssets(); //获取assets下内容
                InputStream in = assetManager.open(emoji.fileFiexd); //打开文件
                Bitmap bmp = BitmapFactory.decodeStream(in); //转化
                viewHolder.faceIv.setImageBitmap(bmp); //将图片写入imageview
            } catch (Exception e) {
                // TODO: handle exception
            }
//            Glide.with(context)
//                    .load(new File("assets://" + emoji.fileFiexd))
//                    .centerCrop()
//                    .dontAnimate()
//                    .listener(new RequestListener<File, GlideDrawable>() {
//                        @Override
//                        public boolean onException(Exception e, File model, Target<GlideDrawable> target, boolean isFirstResource) {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(GlideDrawable resource, File model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                            return false;
//                        }
//                    })
//                    .into(viewHolder.faceIv);


//            FacebookImageUtil.loadWithCache("assets:///" + emoji.fileFiexd, viewHolder.faceIv, true);
        } else {
            if (data.showAll == 1) {
                viewHolder.defaultSize();
            } else {
                viewHolder.extSize();
            }
//            FacebookImageUtil.loadLocalImage(new File(emoji.fileFiexd), viewHolder.faceIv, 0, 0, true, FacebookImageUtil.EMPTY_CALLBACK);

            Glide.with(context)
                    .load(new File(emoji.fileFiexd))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .asBitmap()
                    .centerCrop()
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)//缓存全尺寸
                    .dontAnimate()
                    .into(viewHolder.faceIv);
        }

        return convertView;
    }

    class GridViewHolder {
        public LinearLayout layoutView;
        public SimpleDraweeView faceIv;

        public int faceSize = Utils.dipToPixels(context, 32);
        public int faceExtSize = Utils.dipToPixels(context, 64);

        public GridViewHolder() {
            faceIv = new SimpleDraweeView(context);
            layoutView = new LinearLayout(context);
            faceIv.setScaleType(ImageView.ScaleType.FIT_XY);
            layoutView.setOrientation(LinearLayout.VERTICAL);
            layoutView.setGravity(Gravity.CENTER);
            layoutView.setBackgroundColor(Color.TRANSPARENT);
            faceIv.setBackgroundColor(Color.TRANSPARENT);
            layoutView.addView(faceIv);
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                    AbsListView.LayoutParams.MATCH_PARENT);
            layoutView.setLayoutParams(layoutParams);
        }

        public void extSize() {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(faceExtSize, faceExtSize);
            faceIv.setLayoutParams(params);
            faceIv.invalidate();
        }

        public void defaultSize() {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(faceSize, faceSize);
            faceIv.setLayoutParams(params);
            faceIv.invalidate();
        }
    }
}
