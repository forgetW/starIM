package com.qunar.im.ui.view.chatExtFunc;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.qunar.im.ui.util.FacebookImageUtil;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.CommonAdapter;
import com.qunar.im.ui.adapter.CommonViewHolder;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * Created by xinbo.wang on 2016/5/19.
 */
public class GridFuncAdapter extends CommonAdapter<FuncItem> {

    private String TAG = "GridFuncAdapter";

    public GridFuncAdapter(Context cxt, List<FuncItem> datas, int itemLayoutId) {
        super(cxt, datas, itemLayoutId);
    }

    @Override
    public void convert(CommonViewHolder viewHolder, FuncItem item) {
        final TextView desc = viewHolder.getView(R.id.ItemText);
        final SimpleDraweeView btnPic = viewHolder.getView(R.id.ItemImage);
        desc.setText(item.textId);
        Uri uri = Uri.parse("file://" + R.drawable.im_album);

        Log.e(TAG, "convert: 1---------" +  item.icon);
        Log.e(TAG, "convert: 2---------" +  R.drawable.im_album);
        Log.e(TAG, "convert: ---------");

        loadImage(btnPic, uri, Integer.parseInt(item.icon));
//        FacebookImageUtil.loadWithCache(item.icon, btnPic);
    }

    /**
     * 加载图片核心方法
     *
     * @param simpleDraweeView              图片加载控件
     * @param uri                           图片加载地址

     */
    public void loadImage(SimpleDraweeView simpleDraweeView, Uri uri, int resErrorImg) {
        //设置Hierarchy
        setHierarchay(simpleDraweeView.getHierarchy(), resErrorImg);
        //构建并获取ImageRequest
        ImageRequest imageRequest = getImageRequest(uri, simpleDraweeView);
        //构建并获取Controller
        DraweeController draweeController = getController(imageRequest, simpleDraweeView.getController());
        //开始加载
        simpleDraweeView.setController(draweeController);
    }

    //对Hierarchy进行设置，如各种状态下显示的图片
    public void setHierarchay(GenericDraweeHierarchy hierarchy, int resErrorImg) {
        if (hierarchy != null) {
            //重新加载显示的图片
//            hierarchy.setRetryImage(R.drawable.atom_ui_error_img);
            //加载失败显示的图片
            hierarchy.setFailureImage(resErrorImg, ScalingUtils.ScaleType.CENTER_CROP);
            //加载完成前显示的占位图
//            hierarchy.setPlaceholderImage(R.drawable.atom_ui_error_img, ScalingUtils.ScaleType.CENTER_CROP);
            //设置加载成功后图片的缩放模式
//            hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);

            //显示加载进度条，使用自带的new ProgressBarDrawable()
            //默认会显示在图片的底部，可以设置进度条的颜色。
//            hierarchy.setProgressBarImage(new ProgressBarDrawable());
            //设置图片加载为圆形
            hierarchy.setRoundingParams(RoundingParams.asCircle());
            //设置图片加载为圆角，并可设置圆角大小
            hierarchy.setRoundingParams(RoundingParams.fromCornersRadius(10));

            //其他设置请查看具体API。

        }
    }

    /**
     * 构建、获取Controller
     * @param request
     * @param oldController
     * @return
     */
    public DraweeController getController(ImageRequest request, @Nullable DraweeController oldController) {

        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder();
        builder.setImageRequest(request);//设置图片请求
        builder.setTapToRetryEnabled(false);//设置是否允许加载失败时点击再次加载
        builder.setAutoPlayAnimations(true);//设置是否允许动画图自动播放
        builder.setOldController(oldController);
        return builder.build();
    }

    /**
     * 构建、获取ImageRequest
     * @param uri 加载路径
     * @param simpleDraweeView 加载的图片控件
     * @return ImageRequest
     */
    public ImageRequest getImageRequest(Uri uri, SimpleDraweeView simpleDraweeView) {

        int width;
        int height;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            width = simpleDraweeView.getWidth();
            height = simpleDraweeView.getHeight();
        } else {
            width = simpleDraweeView.getMaxWidth();
            height = simpleDraweeView.getMaxHeight();
        }

        //根据请求路径生成ImageRequest的构造者
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(uri);
        //调整解码图片的大小
        if (width > 0 && height > 0) {
            builder.setResizeOptions(new ResizeOptions(width, height));
        }
        //设置是否开启渐进式加载，仅支持JPEG图片
        builder.setProgressiveRenderingEnabled(true);

        //图片变换处理
//        CombinePostProcessors.Builder processorBuilder = new CombinePostProcessors.Builder();
        //加入模糊变换
//        processorBuilder.add(new BlurPostprocessor(context, radius));
        //加入灰白变换
//        processorBuilder.add(new GrayscalePostprocessor());
        //应用加入的变换
//        builder.setPostprocessor(processorBuilder.build());
        //更多图片变换请查看https://github.com/wasabeef/fresco-processors
        return builder.build();
    }
}
