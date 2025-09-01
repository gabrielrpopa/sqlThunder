package com.widescope.chat.db;

import com.google.gson.Gson;
import com.widescope.chat.db.persistence.UserPairValue;
import com.widescope.sqlThunder.rest.RestInterface;

import java.util.ArrayList;
import java.util.List;

public class UserPairValueList implements RestInterface {

    private List<UserPairValue> userPairValueList = new ArrayList<>() ;

    public UserPairValueList(List<UserPairValue> userPairValueList) {
        this.userPairValueList = userPairValueList;
    }


    public List<UserPairValue> getUserPairValueList() {
        return userPairValueList;
    }

    public void setUserPairValueList(List<UserPairValue> userPairValueList) {
        this.userPairValueList = userPairValueList;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
