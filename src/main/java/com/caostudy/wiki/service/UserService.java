package com.caostudy.wiki.service;

import com.caostudy.wiki.req.UserLoginReq;
import com.caostudy.wiki.req.UserQueryReq;
import com.caostudy.wiki.req.UserResetPasswordReq;
import com.caostudy.wiki.req.UserSaveReq;
import com.caostudy.wiki.resp.UserLoginResp;
import com.caostudy.wiki.resp.UserQueryResp;
import com.caostudy.wiki.resp.PageResp;

/**
 * @author Cao Study
 * @description UserService
 * @date 2021/8/24 18:51
 */
public interface UserService {
    public PageResp<UserQueryResp> list(UserQueryReq req);

    public void save(UserSaveReq req);

    public void delete(Long id);

    UserLoginResp login(UserLoginReq req);

    void resetPassword(UserResetPasswordReq req);
}
