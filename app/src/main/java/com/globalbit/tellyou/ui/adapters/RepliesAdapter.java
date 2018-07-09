package com.globalbit.tellyou.ui.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.globalbit.androidutils.CollectionUtils;
import com.globalbit.androidutils.StringUtils;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.CustomApplication;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.ItemReplyBinding;
import com.globalbit.tellyou.model.Comment;
import com.globalbit.tellyou.model.User;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.BaseResponse;
import com.globalbit.tellyou.ui.events.NextVideoEvent;
import com.globalbit.tellyou.ui.interfaces.IReplyListener;
import com.globalbit.tellyou.utils.SharedPrefsUtils;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Locale;

import im.ene.toro.ToroPlayer;
import im.ene.toro.ToroUtil;
import im.ene.toro.exoplayer.Config;
import im.ene.toro.exoplayer.ExoPlayerViewHelper;
import im.ene.toro.exoplayer.MediaSourceBuilder;
import im.ene.toro.helper.ToroPlayerHelper;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.widget.Container;

/**
 * Created by alex on 14/06/2016.
 */
public class RepliesAdapter extends RecyclerView.Adapter<RepliesAdapter.ViewHolder> {
    private ArrayList<Comment> mItems;
    private Context mContext;
    private IReplyListener mListener;
    public int mImgWidth;
    private LinearLayout.LayoutParams mParams;
    private static final String TAG=RepliesAdapter.class.getSimpleName();
    private User mUser;


    public RepliesAdapter(Context context, IReplyListener listener) {
        mContext=context;
        mListener=listener;
        Resources resources = mContext.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        mImgWidth=(int)(metrics.widthPixels*0.7);
        mParams=new LinearLayout.LayoutParams(mImgWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        mUser=SharedPrefsUtils.getUserDetails();
    }

    public void setItems(ArrayList<Comment> items) {
        mItems=items;
        notifyDataSetChanged();
    }

    public void addItem(Comment item) {
        if(mItems==null) {
            mItems=new ArrayList<>();
        }
        mItems.add(item);
        notifyItemChanged(mItems.size()-1);
    }

    public void addItems(ArrayList<Comment> items) {
        if(mItems==null) {
            mItems=new ArrayList<>();
        }
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void clearItems() {
        if(mItems!=null) {
            mItems.clear();
        }
    }

    public boolean removeItem(Comment item) {
        if(mItems!=null) {
            int index=-1;
            for(int i=0; i<mItems.size(); i++) {
                Comment comment=mItems.get(i);
                if(comment.getId().equals(item.getId())) {
                    index=i;
                    break;
                }
            }
            if(index!=-1) {
                mItems.remove(index);
                notifyItemRemoved(index);
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    public int getIndex(String id) {
        if(!CollectionUtils.isEmpty(mItems)) {
            for(int i=0; i<mItems.size(); i++) {
                Comment comment=mItems.get(i);
                if(comment.getId().equals(id)) {
                    return i;
                }
            }
        }
        return -1;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reply, parent, false);
        ViewHolder viewHolder=new ViewHolder(v);
        viewHolder.mBinding.cardViewReply.setLayoutParams(mParams);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(mItems!=null) {
            final Comment item=mItems.get(position);
            Uri uri=Uri.parse(item.getVideo().getUrl());
            if(!StringUtils.isEmpty(item.getVideo().getThumbnail())) {
                holder.mBinding.imgViewPreview.setVisibility(View.VISIBLE);
                Picasso.with(mContext).load(item.getVideo().getThumbnail()).into(holder.mBinding.imgViewPreview);
            }
            else {
                holder.mBinding.imgViewPreview.setVisibility(View.GONE);
            }
            holder.mediaUri=uri;
            holder.mPosition=position;
            holder.mComment=item;
            holder.mBinding.txtViewUsername.setText(String.format(Locale.getDefault(),"@%s",item.getUser().getUsername()));
            if(item.getUser().getProfile()!=null&&item.getUser().getProfile().getPicture()!=null&&!StringUtils.isEmpty(item.getUser().getProfile().getPicture().getThumbnail())) {
                Picasso.with(mContext).load(item.getUser().getProfile().getPicture().getThumbnail()).into(holder.mBinding.imgViewPhoto);
            }
            else {
                holder.mBinding.imgViewPhoto.setImageResource(R.drawable.img_xs_no_photo_user);
            }
            if(item.getCreatedAt()!=null) {
                holder.mBinding.txtViewDate.setText(DateUtils.getRelativeTimeSpanString(item.getCreatedAt().getTime()));
            }
            if(item.getUser().getUsername().equals(mUser.getUsername())) {
                holder.mBinding.imgViewDelete.setVisibility(View.VISIBLE);
                holder.mBinding.imgViewReport.setVisibility(View.GONE);
            }
            else {
                holder.mBinding.imgViewDelete.setVisibility(View.GONE);
                holder.mBinding.imgViewReport.setVisibility(View.VISIBLE);
            }
            holder.mBinding.txtViewViews.setText(String.format(Locale.getDefault(),"%d", item.getViews()));
            holder.setClickListener(new ViewHolder.ClickListener() {
                @Override
                public void onClick(View v, int position) {
                    switch(v.getId()) {
                        case R.id.frmLayoutMenu:
                            mListener.onReport(item);
                            break;
                        case R.id.imgViewPhoto:
                            mListener.onProfile(item);
                            break;
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mItems==null ? 0 : mItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ToroPlayer {
        private ItemReplyBinding mBinding;
        private ClickListener mClickListener;
        ToroPlayerHelper helper;
        Uri mediaUri;
        private int mPosition;
        private Comment mComment;
        private User mUser=SharedPrefsUtils.getUserDetails();

        public ViewHolder(View v) {
            super(v);
            mBinding=DataBindingUtil.bind(v);
            mBinding.frmLayoutMenu.setOnClickListener(this);
            mBinding.imgViewPhoto.setOnClickListener(this);
        }

        @NonNull
        @Override
        public View getPlayerView() {
            return mBinding.videoViewPlayer;
        }

        @NonNull
        @Override
        public PlaybackInfo getCurrentPlaybackInfo() {
            return helper != null ? helper.getLatestPlaybackInfo() : new PlaybackInfo();
        }

        @Override
        public void initialize(@NonNull final  Container container, @Nullable final PlaybackInfo playbackInfo) {
            if (helper == null) {
                mBinding.imgViewPreview.setVisibility(View.VISIBLE);
                helper = new ExoPlayerViewHelper(this, mediaUri);
                if(!mComment.getUser().getUsername().equals(mUser.getUsername())) {
                    NetworkManager.getInstance().viewPost(new IBaseNetworkResponseListener<BaseResponse>() {
                        @Override
                        public void onSuccess(BaseResponse response, Object object) {

                        }

                        @Override
                        public void onError(int errorCode, String errorMessage) {

                        }
                    }, mComment.getId());
                }
            }
            CustomApplication.getAnalytics().logEvent(Constants.REPLY_PLAYED, null);
            helper.initialize(container, playbackInfo);
            helper.addPlayerEventListener(new ToroPlayer.EventListener() {
                @Override
                public void onBuffering() {

                }

                @Override
                public void onPlaying() {
                    mBinding.imgViewPreview.setVisibility(View.GONE);
                }

                @Override
                public void onPaused() {
                }

                @Override
                public void onCompleted() {
                    mBinding.videoViewPlayer.getPlayer().seekTo(0);
                    EventBus.getDefault().post(new NextVideoEvent(mPosition));

                }
            });
        }

        @Override
        public void play() {
            if (helper != null) helper.play();
        }

        @Override
        public void pause() {
            if (helper != null) helper.pause();
        }

        @Override
        public boolean isPlaying() {
            return helper != null && helper.isPlaying();
        }

        @Override
        public void release() {
            if (helper != null) {
                helper.release();
                helper = null;
            }
        }

        @Override
        public boolean wantsToPlay() {
            return ToroUtil.visibleAreaOffset(this, itemView.getParent()) >= 0.85;
        }

        @Override
        public int getPlayerOrder() {
            return getAdapterPosition();
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
