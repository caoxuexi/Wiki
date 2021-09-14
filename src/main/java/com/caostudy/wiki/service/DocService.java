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
    List<DocQueryResp> all(Long ebookId);

    void save(DocSaveReq req);

    void delete(Long id);

    void delete(List<String> ids);

    @Deprecated
    PageResp<DocQueryResp> list(DocQueryReq req);

    String findContent(Long id);

    String findContentWithViewIncr(Long id);

    void vote(Long id);

    void updateEbookInfo();
}
