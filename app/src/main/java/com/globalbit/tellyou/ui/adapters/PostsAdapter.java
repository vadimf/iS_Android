package com.globalbit.tellyou.ui.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.bumptech.glide.Glide;
import com.globalbit.androidutils.StringUtils;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.ItemPostBinding;
import com.globalbit.tellyou.model.Post;
import com.globalbit.tellyou.model.User;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.BaseResponse;
import com.globalbit.tellyou.ui.interfaces.IPostListener;
import com.globalbit.tellyou.utils.SharedPrefsUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by alex on 14/06/2016.
 */
public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    private ArrayList<Post> mItems;
    private Context mContext;
    private int mFeedType;
    private User mUser;
    private IPostListener mListener;
    private int mImgWidth, mImgHeight;
    private RelativeLayout.LayoutParams mLayoutParams;


    public PostsAdapter(Context context, int feedType, IPostListener listener) {
        mContext=context;
        mFeedType=feedType;
        mListener=listener;
        mUser=SharedPrefsUtils.getUserDetails();
        Resources resources = mContext.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        mImgWidth=metrics.widthPixels/2;
        mImgHeight=mImgWidth;
        mLayoutParams=new RelativeLayout.LayoutParams(mImgWidth, mImgHeight);
    }

    public void setItems(ArrayList<Post> items) {
        mItems=items;
        notifyDataSetChanged();
    }

    public void addItem(Post item) {
        if(mItems==null) {
            mItems=new ArrayList<>();
        }
        mItems.add(item);
        notifyItemChanged(mItems.size()-1);
    }

    public void addItems(ArrayList<Post> items) {
        if(mItems==null) {
            mItems=new ArrayList<>();
        }
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void clear() {
        if(mItems!=null) {
            mItems.clear();
            notifyDataSetChanged();
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        ViewHolder viewHolder=new ViewHolder(v);
        viewHolder.mBinding.imgViewPost.setLayoutParams(mLayoutParams);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(mItems!=null) {
            final Post item=mItems.get(position);
            if(item.getVideo()!=null&&!StringUtils.isEmpty(item.getVideo().getThumbnail())) {
                Glide.with(mContext).load(item.getVideo().getThumbnail()).into(holder.mBinding.imgViewPost);
            }
            else {
                holder.mBinding.imgViewUser.setImageResource(R.drawable.img_xs_no_photo_user);
            }
            switch(mFeedType) {
                case Constants.TYPE_FEED_HOME:
                    holder.mBinding.txtViewUsername.setText(String.format(Locale.getDefault(),"@%s",item.getUser().getUsername()));
                    if(item.getUser().getProfile()!=null&&item.getUser().getProfile().getPicture()!=null&&!StringUtils.isEmpty(item.getUser().getProfile().getPicture().getThumbnail())) {
                        Picasso.with(mContext).load(item.getUser().getProfile().getPicture().getThumbnail()).into(holder.mBinding.imgViewUser);
                    }
                    else {
                        holder.mBinding.imgViewUser.setImageResource(R.drawable.img_xs_no_photo_user);
                    }
                    break;
                case Constants.TYPE_FEED_USER:
                    holder.mBinding.imgViewUser.setVisibility(View.GONE);
                    holder.mBinding.txtViewUsername.setVisibility(View.GONE);
                    break;
            }
            if(item.getUser().getUsername().equals(mUser.getUsername())) {
                holder.mBinding.frmLayoutAction.setVisibility(View.VISIBLE);
            }
            else {
                holder.mBinding.frmLayoutAction.setVisibility(View.GONE);
            }
            holder.mBinding.txtViewDescription.setText(item.getText());
            holder.mBinding.txtViewViews.setText(String.format(Locale.getDefault(),"%d", item.getViews()));
            holder.setClickListener(new ViewHolder.ClickListener() {
                @Override
                public void onClick(View v, final int position) {
                    switch(v.getId()) {
                        case R.id.frmLayoutAction:
                            final MaterialDialog dialog=new MaterialDialog.Builder(mContext)
                                    .customView(R.layout.dialog_my_videos_actions, false)
                                    .show();
                            View viewDeleteQuestion=dialog.findViewById(R.id.lnrLayoutDeleteVideo);
                            viewDeleteQuestion.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    MaterialDialog dialog1=new MaterialDialog.Builder(mContext)
                                            .content(R.string.dialog_delete_video)
                                            .positiveText(R.string.btn_delete)
                                            .negativeText(R.string.btn_cancel)
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    dialog.dismiss();
                                                    final MaterialDialog loadingDialog=new MaterialDialog.Builder(mContext)
                                                            .title(R.string.dialog_loading_title)
                                                            .content(R.string.dialog_loading_content)
                                                            .progress(true, 0)
                                                            .show();
                                                    NetworkManager.getInstance().deletePost(new IBaseNetworkResponseListener<BaseResponse>() {
                                                        @Override
                                                        public void onSuccess(BaseResponse response) {
                                                            loadingDialog.dismiss();
                                                            mItems.remove(position);
                                                            notifyItemRemoved(position);
                                                            if(mItems.size()==0) {
                                                                mListener.onRefreshPosts();
                                                            }
                                                        }

                                                        @Override
                                                        public void onError(int errorCode, String errorMessage) {
                                                            loadingDialog.dismiss();
                                                            new MaterialDialog.Builder(mContext)
                                                                    .title(R.string.error)
                                                                    .content(errorMessage)
                                                                    .positiveText(R.string.btn_ok)
                                                                    .show();
                                                        }
                                                    }, item.getId());
                                                }
                                            })
                                            .show();
                                }
                            });
                            break;
                        case R.id.imgViewUser:
                            showProfile(item);
                            break;
                        case R.id.rltvLayoutPost:
                            mListener.onVideoPayer(item, position);
                            break;
                    }
                }
            });
        }

    }

    private void showProfile(Post item) {
        if(item.getUser()!=null&&!StringUtils.isEmpty(item.getUser().getUsername())) {
            mListener.onUserProfile(item.getUser().getUsername());
        }
    }

    public ArrayList<Post> getItems() {
        return  mItems;
    }

    @Override
    public int getItemCount() {
        return mItems==null ? 0 : mItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ClickListener mClickListener;
        private ItemPostBinding mBinding;

        public ViewHolder(View v) {
            super(v);
            mBinding=DataBindingUtil.bind(v);
            mBinding.imgViewUser.setOnClickListener(this);
            mBinding.frmLayoutAction.setOnClickListener(this);
            mBinding.rltvLayoutPost.setOnClickListener(this);
        }

        public interface ClickListener {

            /**
             * Called when the view is clicked.
             *
             * @param v        view that is clicked
             * @param position of the clicked item
             */
            void onClick(View v, int position);

        }

        public void setClickListener(ClickListener clickListener) {
            this.mClickListener=clickListener;
        }

        @Override
        public void onClick(View v) {

            mClickListener.onClick(v, getPosition());
        }
    }

}
