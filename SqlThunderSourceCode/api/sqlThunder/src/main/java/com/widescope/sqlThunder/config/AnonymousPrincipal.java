package com.widescope.sqlThunder.config;

import com.google.gson.Gson;

import java.security.Principal;
import java.util.Objects;

public class AnonymousPrincipal implements Principal {


    private String name;
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public boolean equals(Object another) {
        if (!(another instanceof Principal principal))
            return false;
        return Objects.equals(principal.getName(), this.name);
    }
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }


}
