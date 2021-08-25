package com.caostudy.wiki.service.impl;

import com.caostudy.wiki.bo.EbookQueryReq;
import com.caostudy.wiki.domain.Ebook;
import com.caostudy.wiki.domain.EbookExample;
import com.caostudy.wiki.mapper.EbookMapper;
import com.caostudy.wiki.resp.EbookQueryResp;
import com.caostudy.wiki.service.EbookService;
import com.caostudy.wiki.utils.CopyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cao Study
 * @description EbookServiceImpl
 * @date 2021/8/24 18:52
 */
@Service
public class EbookServiceImpl implements EbookService {
    @Autowired
    private EbookMapper ebookMapper;

    @Override
    public List<EbookQueryResp> list(EbookQueryReq req) {
        EbookExample ebookExample=new EbookExample();
        EbookExample.Criteria criteria=ebookExample.createCriteria();
        criteria.andNameLike("%"+req.getName()+"%");
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);
//        List<EbookQueryResp> respList=new ArrayList<>();
//        for(Ebook ebook: ebookList){
//            EbookQueryResp ebookQueryResp=new EbookQueryResp();
//            ebookQueryResp.setId(ebook.getId());
//            BeanUtils.copyProperties(ebook,ebookQueryResp);
//            respList.add(ebookQueryResp);
//        }
        List<EbookQueryResp> respList = CopyUtil.copyList(ebookList, EbookQueryResp.class);
        return respList;
    }
}
