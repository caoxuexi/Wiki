package com.caostudy.wiki.service;

import com.caostudy.wiki.bo.EbookQueryReq;
import com.caostudy.wiki.domain.Ebook;
import com.caostudy.wiki.resp.EbookQueryResp;

import java.util.List;

/**
 * @author Cao Study
 * @description EbookService
 * @date 2021/8/24 18:51
 */
public interface EbookService {
    public List<EbookQueryResp> list(EbookQueryReq req);
}
