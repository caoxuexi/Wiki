package com.caostudy.wiki.service.impl;

import com.caostudy.wiki.bo.EbookQueryReq;
import com.caostudy.wiki.domain.Ebook;
import com.caostudy.wiki.domain.EbookExample;
import com.caostudy.wiki.mapper.EbookMapper;
import com.caostudy.wiki.resp.EbookQueryResp;
import com.caostudy.wiki.service.EbookService;
import com.caostudy.wiki.utils.CopyUtil;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Cao Study
 * @description EbookServiceImpl
 * @date 2021/8/24 18:52
 */
@Service
public class EbookServiceImpl implements EbookService {
    private static final Logger LOG= LoggerFactory.getLogger(EbookServiceImpl.class);

    @Autowired
    private EbookMapper ebookMapper;

    @Override
    public List<EbookQueryResp> list(EbookQueryReq req) {
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        if (!StringUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }
        PageHelper.startPage(1,3);
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);
        PageInfo<Ebook> pageInfo=new PageInfo<>(ebookList);
        LOG.info("总行数：{}",pageInfo.getTotal());
        LOG.info("总页数：{}",pageInfo.getPages());

        List<EbookQueryResp> respList = CopyUtil.copyList(ebookList, EbookQueryResp.class);
        return respList;
    }
}


