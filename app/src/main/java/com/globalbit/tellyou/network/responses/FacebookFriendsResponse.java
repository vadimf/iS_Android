package com.globalbit.tellyou.network.responses;

import com.globalbit.tellyou.model.User;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by alex on 09/11/2017.
 */

public class FacebookFriendsResponse extends BaseResponse {

    @SerializedName("users")
    private ArrayList<User> mUsers;

    @SerializedName("pageToken")
    private String nextPageToken;

    public ArrayList<User> getUsers() {
        return mUsers;
    }

    public void setUsers(ArrayList<User> users) {
        mUsers=users;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken=nextPageToken;
    }
}
