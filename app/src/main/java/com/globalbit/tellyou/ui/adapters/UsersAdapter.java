package com.globalbit.tellyou.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.ItemUserBinding;
import com.globalbit.tellyou.model.User;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.BaseResponse;
import com.globalbit.tellyou.ui.activities.ProfileActivity;
import com.globalbit.tellyou.ui.interfaces.IUserListener;
import com.globalbit.tellyou.utils.SharedPrefsUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by alex on 14/06/2016.
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private ArrayList<User> mItems;
    private Context mContext;
    private boolean mShowStatus=false;
    private User mUser;
    private boolean mIsFollowing=false;
    private IUserListener mListener;


    public UsersAdapter(Context context, IUserListener listener) {
        mContext=context;
        mListener=listener;
        mUser=SharedPrefsUtils.getUserDetails();
    }

    public UsersAdapter(Context context, IUserListener listener, boolean isFollowing) {
        mContext=context;
        mListener=listener;
        mUser=SharedPrefsUtils.getUserDetails();
        mIsFollowing=isFollowing;
    }

    public void showStatus(boolean showStatus) {
        mShowStatus=showStatus;
    }

    public void setItems(ArrayList<User> items) {
        mItems=items;
        notifyDataSetChanged();
    }

    public void addItems(ArrayList<User> items) {
        if(mItems==null) {
            mItems=new ArrayList<>();
        }
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void addItem(User item) {
        if(mItems==null) {
            mItems=new ArrayList<>();
        }
        mItems.add(item);
        notifyItemChanged(mItems.size()-1);
    }
    public void clear() {
        if(mItems!=null) {
            mItems.clear();
        }
        notifyDataSetChanged();
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(mItems!=null) {
            final User item=mItems.get(position);
            holder.mBinding.txtViewName.setText(item.getUsername());
            if(mShowStatus) {
                holder.mBinding.txtViewStatus.setVisibility(View.VISIBLE);
            }
            else {
                holder.mBinding.txtViewStatus.setVisibility(View.GONE);
            }
            if(mUser.getUsername().equals(item.getUsername())) {
                holder.mBinding.btnFollow.setVisibility(View.GONE);
            }
            else {
                holder.mBinding.btnFollow.setVisibility(View.VISIBLE);
                if(item.isFollowing()) {
                    holder.mBinding.btnFollow.setBackgroundResource(R.drawable.button_share);
                    holder.mBinding.btnFollow.setTextColor(mContext.getResources().getColor(R.color.red_border));
                    holder.mBinding.btnFollow.setText(R.string.btnFollowing);
                } else {
                    holder.mBinding.btnFollow.setBackgroundResource(R.drawable.background_button);
                    holder.mBinding.btnFollow.setTextColor(mContext.getResources().getColor(R.color.white));
                    holder.mBinding.btnFollow.setText(R.string.btn_follow);
                }
            }
            if(item.getProfile()!=null&&item.getProfile().getPicture()!=null) {
                Picasso.with(mContext).load(item.getProfile().getPicture().getThumbnail()).into(holder.mBinding.imgViewPhoto);
            }
            else {
                holder.mBinding.imgViewPhoto.setImageResource(R.drawable.img_xs_no_photo_user);
            }
            holder.setClickListener(new ViewHolder.ClickListener() {
                @Override
                public void onClick(View v, int position) {
                    switch(v.getId()) {
                        case R.id.btnFollow:
                            if(item.isFollowing()) {
                                NetworkManager.getInstance().unfollow(new IBaseNetworkResponseListener<BaseResponse>() {
                                    @Override
                                    public void onSuccess(BaseResponse response, Object object) {

                                    }

                                    @Override
                                    public void onError(int errorCode, String errorMessage) {

                                    }
                                }, item.getUsername());
                                if(mIsFollowing) {
                                    mItems.remove(position);
                                    notifyItemRemoved(position);
                                    mListener.onShowEmpty();
                                }
                            }
                            else {
                                NetworkManager.getInstance().follow(new IBaseNetworkResponseListener<BaseResponse>() {
                                    @Override
                                    public void onSuccess(BaseResponse response, Object object) {

                                    }

                                    @Override
                                    public void onError(int errorCode, String errorMessage) {

                                    }
                                }, item.getUsername());
                            }
                            item.setFollowing(!item.isFollowing());
                            notifyItemChanged(position);
                            break;
                        case R.id.lnrLayoutUser:
                            Intent intent=new Intent(mContext, ProfileActivity.class);
                            intent.putExtra(Constants.DATA_PROFILE, Constants.REQUEST_USER_PROFILE);
                            intent.putExtra(Constants.DATA_USER, item);
                            mContext.startActivity(intent);
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

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemUserBinding mBinding;
        private ClickListener mClickListener;

        public ViewHolder(View v) {
            super(v);
            mBinding=DataBindingUtil.bind(v);
            mBinding.btnFollow.setOnClickListener(this);
            mBinding.lnrLayoutUser.setOnClickListener(this);

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
