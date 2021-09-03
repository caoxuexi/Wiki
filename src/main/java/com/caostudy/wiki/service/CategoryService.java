package com.caostudy.wiki.service;

import com.caostudy.wiki.req.CategoryQueryReq;
import com.caostudy.wiki.req.CategorySaveReq;
import com.caostudy.wiki.resp.CategoryQueryResp;
import com.caostudy.wiki.resp.PageResp;

import java.util.List;

/**
 * @author Cao Study
 * @description CategoryService
 * @date 2021/8/24 18:51
 */
public interface CategoryService {
    public List<CategoryQueryResp> all();

    public void save(CategorySaveReq req);

    public void delete(Long id);

    @Deprecated
    public PageResp<CategoryQueryResp> list(CategoryQueryReq req);

}
