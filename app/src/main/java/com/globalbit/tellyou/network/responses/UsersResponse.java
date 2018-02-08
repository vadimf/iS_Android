package com.globalbit.tellyou.network.responses;

import com.globalbit.tellyou.model.Pagination;
import com.globalbit.tellyou.model.User;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by alex on 29/10/2017.
 */

public class UsersResponse extends BaseResponse {

    @SerializedName("users")
    private ArrayList<User> mUsers;

    @SerializedName("pagination")
    private Pagination mPagination;

    private String mQuery;

    public ArrayList<User> getUsers() {
        return mUsers;
    }

    public void setUsers(ArrayList<User> users) {
        mUsers=users;
    }

    public Pagination getPagination() {
        return mPagination;
    }

    public void setPagination(Pagination pagination) {
        mPagination=pagination;
    }

    public String getQuery() {
        return mQuery;
    }

    public void setQuery(String query) {
        mQuery=query;
    }
}
