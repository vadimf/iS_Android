package com.globalbit.tellyou.model;

import android.os.Parcel;

/**
 * Created by alex on 29/10/2017.
 */

public class Post extends BasePostComment {

    public Post() {}

    public Post(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}
