package com.example.shang.filemanager.Fragment.imagePager;

import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.shang.filemanager.BaseApplication;
import com.example.shang.filemanager.Fragment.ThirdFragment;
import com.example.shang.filemanager.R;
import com.example.shang.filemanager.entity.PhotoInformation;
import com.example.shang.filemanager.utils.ConstantValue;

import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by yaojian on 2017/10/20.
 */

public class ImageShowDetail extends BasePager implements View.OnClickListener {

    private ImageButton ib_remove;
    private ImageButton ib_share;
    private ImageView iv_image_detail;

    public ImageShowDetail(Fragment fragment) {
        super(fragment);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.layout_image_detail,null);
        ib_remove = (ImageButton) view.findViewById(R.id.ib_remove);
        ib_share = (ImageButton) view.findViewById(R.id.ib_share);
        iv_image_detail = (ImageView) view.findViewById(R.id.iv_image_detail);
        initEvent();
        return view;
    }

    public void loadImage(PhotoInformation info){

        DisplayMetrics dm = new DisplayMetrics();
        fragment.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        if (info.getName().matches(ConstantValue.GIF_KEY)){
            Glide.with(mContext).load(info.getPath()).asGif().override(width,height)
                    .placeholder(R.drawable.shape_progresss).fitCenter().into(iv_image_detail);
        }else{
            Glide.with(mContext).load(info.getPath()).override(width,height)
                    .placeholder(R.drawable.shape_progresss).fitCenter().into(iv_image_detail);
        }
    }

    private void initEvent() {
        ib_remove.setOnClickListener(this);
        ib_share.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_share:
                showShare();
                break;
            case R.id.ib_remove:
                ((ThirdFragment)fragment).backOnce();
                break;
        }
    }

    //开启分享界面
    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字  2.5.9以后的版本不     调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("标题");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(mContext.getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(BaseApplication.getContext());
    }
}
