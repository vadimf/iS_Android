package com.globalbit.tellyou.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.globalbit.androidutils.CollectionUtils;
import com.globalbit.androidutils.ConversionUtils;
import com.globalbit.androidutils.StringUtils;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.CustomApplication;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.ItemVideoBinding;
import com.globalbit.tellyou.model.Post;
import com.globalbit.tellyou.model.User;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.BaseResponse;
import com.globalbit.tellyou.ui.activities.SearchActivity;
import com.globalbit.tellyou.ui.events.NextVideoEvent;
import com.globalbit.tellyou.ui.interfaces.IGestureEventsListener;
import com.globalbit.tellyou.ui.interfaces.IVideoListener;
import com.globalbit.tellyou.utils.CustomLinearLayoutManager;
import com.globalbit.tellyou.utils.SharedPrefsUtils;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.video.VideoListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import im.ene.toro.CacheManager;
import im.ene.toro.ToroPlayer;
import im.ene.toro.ToroUtil;
import im.ene.toro.exoplayer.ExoPlayerViewHelper;
import im.ene.toro.helper.ToroPlayerHelper;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.widget.Container;

/**
 * Created by alex on 14/06/2016.
 */
public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> implements CacheManager{
    private static final String TAG=VideosAdapter.class.getSimpleName();
    private static long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 1000;
    private static final long HIDE_DETAILS_THRESHOLD=6000;
    private ArrayList<Post> mItems;
    private Context mContext;
    private IVideoListener mListener;
    private User mUser;
    private int mImgWidth, mImgHeight;
    private CustomLinearLayoutManager mLinearLayoutManager;
    private ClickableSpan mClickableSpan;


    public VideosAdapter(final Context context, IVideoListener listener, CustomLinearLayoutManager linearLayoutManager) {
        mContext=context;
        mListener=listener;
        mLinearLayoutManager=linearLayoutManager;
        mUser=SharedPrefsUtils.getUserDetails();
        Resources resources = mContext.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        mImgWidth=metrics.widthPixels;
        mImgHeight=metrics.heightPixels;
        mClickableSpan=new ClickableSpan() {
            @Override
            public void onClick(View view) {
                if(view instanceof TextView) {
                    Spanned s = (Spanned) ((TextView) view).getText();
                    int start = s.getSpanStart(this);
                    int end = s.getSpanEnd(this);
                    CharSequence tag=s.subSequence(start+1, end);
                    Log.d(TAG, "onClick " + tag);
                    Intent intent=new Intent(context, SearchActivity.class);
                    intent.putExtra(Constants.DATA_SEARCH, tag.toString());
                    context.startActivity(intent);
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                int linkColor = ContextCompat.getColor(mContext, R.color.white);
                ds.setColor(linkColor);
                ds.setUnderlineText(false);
            }
        };
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

    public void updateFollowState(User user) {
        if(!CollectionUtils.isEmpty(mItems)) {
            for(int i=0; i<mItems.size(); i++) {
                Post post=mItems.get(i);
                if(post.getUser().getUsername().equals(user.getUsername())) {
                    post.getUser().setFollowing(user.isFollowing());
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateComments(String id) {
        if(!CollectionUtils.isEmpty(mItems)) {
            for(int i=0; i<mItems.size(); i++) {
                Post item=mItems.get(i);
                if(item.getId().equals(id)) {
                    item.setComments(item.getComments()+1);
                }
            }
        }
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video, parent, false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(mItems!=null) {
            final Post item=mItems.get(position);
            PROGRESS_UPDATE_INTERNAL=(item.getVideo().getDuration()*1000)/100;
            Uri uri=Uri.parse(item.getVideo().getUrl());
            if(!StringUtils.isEmpty(item.getVideo().getThumbnail())) {
                holder.mBinding.imgViewPreview.setVisibility(View.VISIBLE);
                Picasso.with(mContext).load(item.getVideo().getThumbnail()).resize(mImgWidth, 0).into(holder.mBinding.imgViewPreview);
            }
            else {
                holder.mBinding.imgViewPreview.setVisibility(View.GONE);
            }
            holder.mMediaUri=uri;
            holder.mPosition=position;
            holder.mPost=item;
            holder.mBinding.gestureView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction()==MotionEvent.ACTION_UP) {
                        mLinearLayoutManager.setScrollEnabled(true);
                    }
                    return false;
                }
            });
            holder.mBinding.gestureView.setGesterEventsListener(new IGestureEventsListener() {
                @Override
                public void onTap() {
                    holder.mBinding.imgViewPreview.setVisibility(View.GONE);
                    if(holder.wantsToPlay()) {
                        if(holder.isPlaying()) {
                            if(holder.mHelper!=null) {
                                holder.mHelper.pause();
                            }
                            holder.stopSeekbarUpdate();
                        } else {
                            if(holder.mHelper!=null) {
                                holder.mHelper.play();
                            }
                            holder.scheduleSeekbarUpdate();
                        }
                    }
                }

                @Override
                public void onHorizontalScroll(MotionEvent event, float delta, int speed) {
                    if(holder.wantsToPlay()) {
                        mLinearLayoutManager.setScrollEnabled(false);
                        if(delta<0) {
                            int newPosition=(int)holder.mBinding.videoViewPlayer.getPlayer().getCurrentPosition()+speed;
                            if(newPosition>holder.mBinding.videoViewPlayer.getPlayer().getDuration()) {
                                newPosition=(int)holder.mBinding.videoViewPlayer.getPlayer().getDuration();
                            }
                            holder.mBinding.videoViewPlayer.getPlayer().seekTo(newPosition);
                            holder.mElapsedTime=newPosition;
                            holder.mBinding.progressBarPortrait.setProgress(newPosition);
                        } else {
                            int newPosition=(int)holder.mBinding.videoViewPlayer.getPlayer().getCurrentPosition()-speed;
                            if(newPosition<0) {
                                newPosition=0;
                            }

                            holder.mBinding.videoViewPlayer.getPlayer().seekTo(newPosition);
                            holder.mElapsedTime=newPosition;
                            holder.mBinding.progressBarPortrait.setProgress(newPosition);
                        }
                    }
                }

                @Override
                public void onVerticalScroll(MotionEvent event, float delta, int speed) {

                }

                @Override
                public void onSwipeRight() {

                }

                @Override
                public void onSwipeLeft() {

                }

                @Override
                public void onSwipeBottom() {

                }

                @Override
                public void onSwipeTop() {

                }
            }, item.getVideo().getDuration());
            holder.mBinding.txtViewViews.setText(String.format(Locale.getDefault(), "%d", item.getViews()));
            if(item.getComments()>0) {
                holder.mBinding.layoutPlayerActions.txtViewComments.setText(String.format(Locale.getDefault(), "%d", item.getComments()));
            }
            else {
                holder.mBinding.layoutPlayerActions.txtViewComments.setText("");
            }
            holder.mBinding.layoutVideoInformation.txtViewUsername.setText(String.format(Locale.getDefault(),"@%s",item.getUser().getUsername()));
            if(item.getUser().getProfile()!=null&&item.getUser().getProfile().getPicture()!=null&&!StringUtils.isEmpty(item.getUser().getProfile().getPicture().getThumbnail())) {
                Picasso.with(mContext).load(item.getUser().getProfile().getPicture().getThumbnail()).into(holder.mBinding.layoutVideoInformation.imgViewPhoto);
            }
            else {
                holder.mBinding.layoutVideoInformation.imgViewPhoto.setImageResource(R.drawable.img_xs_no_photo_user);
            }
            if(item.getCreatedAt()!=null) {
                holder.mBinding.layoutVideoInformation.txtViewDate.setText(DateUtils.getRelativeTimeSpanString(item.getCreatedAt().getTime()));
            }
            SpannableStringBuilder spannableStringBuilder=new SpannableStringBuilder();
            spannableStringBuilder.append(item.getText());
            if(!CollectionUtils.isEmpty(item.getTags())) {
                spannableStringBuilder.append("\n\n");
                for(String s : item.getTags()) {
                    SpannableString spannableString=new SpannableString("#"+s+" ");
                    ClickableSpan clickableSpan=new ClickableSpan() {
                        @Override
                        public void onClick(View view) {
                            Log.i(TAG, "onClick: ");
                            if(view instanceof TextView) {
                                Spanned s = (Spanned) ((TextView) view).getText();
                                int start = s.getSpanStart(this);
                                int end = s.getSpanEnd(this);
                                CharSequence tag=s.subSequence(start+1, end);
                                Log.d(TAG, "onClick " + tag);
                                Intent intent=new Intent(mContext, SearchActivity.class);
                                intent.putExtra(Constants.DATA_SEARCH, tag.toString());
                                mContext.startActivity(intent);
                            }
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            int linkColor = ContextCompat.getColor(mContext, R.color.white);
                            ds.setColor(linkColor);
                            ds.setUnderlineText(false);
                        }
                    };
                    spannableString.setSpan(clickableSpan,0,spannableString.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableStringBuilder.append(spannableString);
                }
            }
            holder.mBinding.layoutVideoInformation.txtViewTitle.setText(spannableStringBuilder);
            holder.mBinding.layoutVideoInformation.txtViewTitle.setMovementMethod(LinkMovementMethod.getInstance());
            if(mUser.getUsername().equals(item.getUser().getUsername())) {
                holder.mBinding.layoutVideoInformation.btnAction.setVisibility(View.GONE);
            }
            else {
                holder.mBinding.layoutVideoInformation.btnAction.setVisibility(View.VISIBLE);
                if(item.getUser().isFollowing()) {
                    holder.mBinding.layoutVideoInformation.btnAction.setBackgroundResource(R.drawable.button_share);
                    holder.mBinding.layoutVideoInformation.btnAction.setTextColor(mContext.getResources().getColor(R.color.red_border));
                    holder.mBinding.layoutVideoInformation.btnAction.setText(R.string.btn_following);
                }
                else {
                    holder.mBinding.layoutVideoInformation.btnAction.setBackgroundResource(R.drawable.background_button);
                    holder.mBinding.layoutVideoInformation.btnAction.setTextColor(mContext.getResources().getColor(R.color.white));
                    holder.mBinding.layoutVideoInformation.btnAction.setText(R.string.btn_follow);
                }
            }
            holder.mBinding.progressBarPortrait.setMax(item.getVideo().getDuration()*1000);
            holder.mBinding.progressBarPortrait.setProgress(0);
            holder.mBinding.layoutVideoMenu.lnrLayoutVideoMenu.setVisibility(View.GONE);
            holder.mBinding.layoutVideoInformation.lnrLayoutVideoInformation.setVisibility(View.VISIBLE);
            holder.mBinding.layoutVideoMenu.switchAutoplay.setChecked(SharedPrefsUtils.isAutoplayNextVideo());
            holder.setClickListener(new ViewHolder.ClickListener() {
                @Override
                public void onClick(View v, int position) {
                    switch(v.getId()) {
                        case R.id.imgViewPhoto:
                            mListener.onProfile(item);
                            break;
                        case R.id.imgViewCancel:
                            mListener.onClose();
                            break;
                        case R.id.frmLayoutMenu:
                            if(holder.mTimer!=null) {
                                holder.mTimer.cancel();
                            }
                            if(holder.mBinding.layoutVideoMenu.lnrLayoutVideoMenu.getVisibility()==View.VISIBLE) {
                                holder.mBinding.layoutVideoMenu.lnrLayoutVideoMenu.setVisibility(View.GONE);
                            }
                            else {
                                holder.mBinding.layoutVideoMenu.lnrLayoutVideoMenu.setVisibility(View.VISIBLE);
                                holder.mBinding.layoutVideoInformation.lnrLayoutVideoInformation.setVisibility(View.GONE);
                            }
                            break;
                        case R.id.frmLayoutInfo:
                            if(holder.mTimer!=null) {
                                holder.mTimer.cancel();
                            }
                            if(holder.mBinding.layoutVideoInformation.lnrLayoutVideoInformation.getVisibility()==View.VISIBLE) {
                                holder.mBinding.layoutVideoInformation.lnrLayoutVideoInformation.setVisibility(View.GONE);
                            }
                            else {
                                holder.mBinding.layoutVideoInformation.lnrLayoutVideoInformation.setVisibility(View.VISIBLE);
                                holder.mBinding.layoutVideoMenu.lnrLayoutVideoMenu.setVisibility(View.GONE);
                            }
                            break;
                        case R.id.frmLayoutComments:
                            mListener.onComments(item);
                            break;
                        case R.id.frmLayoutShare:
                            /*if(holder.isPlaying()) {
                                if(holder.mHelper!=null) {
                                    holder.mHelper.pause();
                                }
                                holder.stopSeekbarUpdate();
                            }*/
                            final MaterialDialog loadingDialog=new MaterialDialog.Builder(mContext)
                                    .title(R.string.dialog_loading_title)
                                    .content(R.string.dialog_loading_content)
                                    .progress(true, 0)
                                    .show();
                            String url=CustomApplication.getSystemPreference().getPages().getPostShare().replace(":post",item.getId()).replace(":username", mUser.getUsername());
                            Task<ShortDynamicLink> shortLinkTask=FirebaseDynamicLinks.getInstance().createDynamicLink()
                                    .setLink(Uri.parse(url))
                                    .setDynamicLinkDomain("a676h.app.goo.gl")
                                    .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                                    .setIosParameters(new DynamicLink.IosParameters.Builder("com.globalbit.tellyou").build())
                                    .buildShortDynamicLink()
                                    .addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                                        @Override
                                        public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                                            loadingDialog.dismiss();
                                            if(task.isSuccessful()) {
                                                String shareLink=task.getResult().getShortLink().toString();
                                                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                                                share.setType("text/plain");
                                                String title;
                                                //title=String.format(Locale.getDefault(), mContext.getString(R.string.label_share_title),SharedPrefsUtils.getUserDetails().getUsername(), item.getText());
                                                share.putExtra(Intent.EXTRA_TEXT, String.format(Locale.getDefault(),"%s", shareLink));
                                                if (share.resolveActivity(mContext.getPackageManager()) != null) {
                                                    mContext.startActivity(Intent.createChooser(share, mContext.getString(R.string.label_share_via)));
                                                }
                                            }
                                        }
                                    });
                            //TODO share outside the application (Deep-linking)
                            break;
                        case R.id.btnAction:
                            if(item.getUser().isFollowing()) {
                                new MaterialDialog.Builder(mContext)
                                        .content(String.format(Locale.getDefault(),"%s %s%s?", mContext.getResources().getString(R.string.dialog_button_unfollow), mContext.getResources().getString(R.string.special), item.getUser().getUsername()))
                                        .positiveText(R.string.dialog_button_unfollow)
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                NetworkManager.getInstance().unfollow(new IBaseNetworkResponseListener<BaseResponse>() {
                                                    @Override
                                                    public void onSuccess(BaseResponse response, Object object) {
                                                        holder.mBinding.layoutVideoInformation.btnAction.setBackgroundResource(R.drawable.background_button);
                                                        holder.mBinding.layoutVideoInformation.btnAction.setTextColor(mContext.getResources().getColor(R.color.white));
                                                        holder.mBinding.layoutVideoInformation.btnAction.setText(mContext.getString(R.string.btn_follow));
                                                        item.getUser().setFollowing(false);
                                                        mListener.onFollow(item);
                                                    }

                                                    @Override
                                                    public void onError(int errorCode, String errorMessage) {

                                                    }
                                                }, item.getUser().getUsername());
                                            }
                                        })
                                        .negativeText(R.string.btn_cancel)
                                        .show();
                            }
                            else {
                                NetworkManager.getInstance().follow(new IBaseNetworkResponseListener<BaseResponse>() {
                                    @Override
                                    public void onSuccess(BaseResponse response, Object object) {
                                        holder.mBinding.layoutVideoInformation.btnAction.setBackgroundResource(R.drawable.button_share);
                                        holder.mBinding.layoutVideoInformation.btnAction.setTextColor(mContext.getResources().getColor(R.color.red_border));
                                        holder.mBinding.layoutVideoInformation.btnAction.setText(mContext.getString(R.string.btn_following));
                                        item.getUser().setFollowing(true);
                                        mListener.onFollow(item);
                                    }

                                    @Override
                                    public void onError(int errorCode, String errorMessage) {

                                    }
                                }, item.getUser().getUsername());

                            }
                            break;
                        case R.id.txtViewReport:
                            mListener.onReport(item.getId());
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

    @Nullable
    @Override
    public Object getKeyForOrder(int order) {
        return mItems.get(order);
    }

    @Nullable
    @Override
    public Integer getOrderForKey(@NonNull Object key) {
        return key instanceof Post ? mItems.indexOf(key) : null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ToroPlayer, VideoListener {
        private ItemVideoBinding mBinding;
        private ClickListener mClickListener;
        private ToroPlayerHelper mHelper;
        private Uri mMediaUri;
        private CountDownTimer mTimer;
        private int mElapsedTime=0;
        private ScheduledFuture<?> mScheduleFuture;
        private Runnable mUpdateProgressTask = new Runnable() {
            @Override
            public void run() {
                updateProgress();
            }
        };
        private ScheduledExecutorService mExecutorService =
                Executors.newSingleThreadScheduledExecutor();
        private final Handler mHandler = new Handler();
        private int mPosition;
        private Post mPost;

        public ViewHolder(View v) {
            super(v);
            mBinding=DataBindingUtil.bind(v);
            mBinding.imgViewCancel.setOnClickListener(this);
            mBinding.layoutPlayerActions.frmLayoutMenu.setOnClickListener(this);
            mBinding.layoutPlayerActions.frmLayoutInfo.setOnClickListener(this);
            mBinding.layoutPlayerActions.frmLayoutComments.setOnClickListener(this);
            mBinding.layoutPlayerActions.frmLayoutShare.setOnClickListener(this);
            mBinding.layoutVideoInformation.btnAction.setOnClickListener(this);
            mBinding.layoutVideoMenu.txtViewReport.setOnClickListener(this);
            mBinding.layoutVideoInformation.imgViewPhoto.setOnClickListener(this);
            mBinding.layoutVideoMenu.switchAutoplay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    SharedPrefsUtils.setAutoplayNextVideo(b);
                }
            });
        }

        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            Log.i(TAG, "onVideoSizeChanged: "+width+","+height);
        }

        @Override
        public void onRenderedFirstFrame() {

        }

        @NonNull
        @Override
        public View getPlayerView() {
            return mBinding.videoViewPlayer;
        }

        @NonNull
        @Override
        public PlaybackInfo getCurrentPlaybackInfo() {
            return mHelper != null ? mHelper.getLatestPlaybackInfo() : new PlaybackInfo();
        }

        @Override
        public void onSettled(Container container) {

        }

        @Override
        public void initialize(@NonNull Container container, @Nullable PlaybackInfo playbackInfo) {
            if (mHelper == null) {
                Log.i(TAG, "initialize: "+mPosition);
                mBinding.imgViewPreview.setVisibility(View.VISIBLE);
                mHelper = new ExoPlayerViewHelper( this, mMediaUri);
                NetworkManager.getInstance().viewPost(new IBaseNetworkResponseListener<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response, Object object) {

                    }

                    @Override
                    public void onError(int errorCode, String errorMessage) {

                    }
                }, mPost.getId());
            }
            mHelper.initialize(container, playbackInfo);
            mElapsedTime=0;
            mBinding.progressBarPortrait.setProgress(mElapsedTime);
            mTimer=new CountDownTimer(HIDE_DETAILS_THRESHOLD, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    mBinding.layoutVideoInformation.lnrLayoutVideoInformation.setVisibility(View.GONE);
                }
            };
            mHelper.addPlayerEventListener(new ToroPlayer.EventListener() {
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
                    stopSeekbarUpdate();
                    mElapsedTime=0;
                    mBinding.progressBarPortrait.setProgress(0);
                    mBinding.videoViewPlayer.getPlayer().seekTo(0);
                    mHelper.pause();
                    if(SharedPrefsUtils.isAutoplayNextVideo()) {
                        EventBus.getDefault().post(new NextVideoEvent(mPosition));
                    }

                }
            });
        }

        @Override
        public void play() {
            if(!SharedPrefsUtils.isShowTutorial()) {
                if(mHelper!=null) mHelper.play();
                scheduleSeekbarUpdate();
                mTimer.start();
                mElapsedTime=(int) mHelper.getLatestPlaybackInfo().getResumePosition();
                mBinding.progressBarPortrait.setProgress(mElapsedTime);
            }
        }

        @Override
        public void pause() {
            if (mHelper != null) mHelper.pause();
            stopSeekbarUpdate();
        }

        @Override
        public boolean isPlaying() {
            return mHelper != null && mHelper.isPlaying();
        }

        @Override
        public void release() {
            if (mHelper != null) {
                mHelper.release();
                mHelper = null;
            }
            stopSeekbarUpdate();
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
        private void updateProgress() {
            //Log.i(TAG, "updateProgress: "+mElapsedTime);
            mBinding.progressBarPortrait.setProgress(mElapsedTime);
            mElapsedTime+=PROGRESS_UPDATE_INTERNAL;
        }

        private void stopSeekbarUpdate() {
            if (mScheduleFuture != null) {
                mScheduleFuture.cancel(false);
            }
        }

        private void scheduleSeekbarUpdate() {
            stopSeekbarUpdate();
            if (!mExecutorService.isShutdown()) {
                mScheduleFuture = mExecutorService.scheduleAtFixedRate(
                        new Runnable() {
                            @Override
                            public void run() {
                                mHandler.post(mUpdateProgressTask);
                            }
                        }, PROGRESS_UPDATE_INITIAL_INTERVAL,
                        PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
            }
        }

    }

}
