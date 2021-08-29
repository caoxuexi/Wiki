package com.caostudy.wiki.service;

import com.caostudy.wiki.req.EbookQueryReq;
import com.caostudy.wiki.req.EbookSaveReq;
import com.caostudy.wiki.resp.EbookQueryResp;
import com.caostudy.wiki.resp.PageResp;

import java.util.List;

/**
 * @author Cao Study
 * @description EbookService
 * @date 2021/8/24 18:51
 */
public interface EbookService {
    public PageResp<EbookQueryResp> list(EbookQueryReq req);

    public void save(EbookSaveReq req);

    public void delete(Long id);

}
