package com.interview.simplemessagingservice.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class CommonUtil {
    private static CommonUtil instance = null;

    private CommonUtil(){

    }

    /**
     * Get singleton instance of CommonUtil class.
     * @return CommonUtil instance.
     */
    public static CommonUtil getInstance(){
        if(instance == null){
            instance = new CommonUtil();
        }
        return instance;
    }

    /**
     * Returns the authenticated user's user name.
     * @return String username
     */
    public String getAuthenticatedUsersUsername(){
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUsername();
    }
}
