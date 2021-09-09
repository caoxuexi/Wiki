package com.caostudy.wiki.service;

import com.caostudy.wiki.req.DocQueryReq;
import com.caostudy.wiki.req.DocSaveReq;
import com.caostudy.wiki.resp.DocQueryResp;
import com.caostudy.wiki.resp.PageResp;

import java.util.List;

/**
 * @author Cao Study
 * @description DocService
 * @date 2021/8/24 18:51
 */
public interface DocService {
    public List<DocQueryResp> all(Long ebookId);

    public void save(DocSaveReq req);

    public void delete(Long id);

    public void delete(List<String> ids);

    @Deprecated
    public PageResp<DocQueryResp> list(DocQueryReq req);

    public String findContent(Long id);
}
