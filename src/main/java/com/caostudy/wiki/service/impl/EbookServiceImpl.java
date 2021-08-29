package com.caostudy.wiki.service.impl;

import com.caostudy.wiki.req.EbookQueryReq;
import com.caostudy.wiki.domain.Ebook;
import com.caostudy.wiki.domain.EbookExample;
import com.caostudy.wiki.mapper.EbookMapper;
import com.caostudy.wiki.req.EbookSaveReq;
import com.caostudy.wiki.resp.EbookQueryResp;
import com.caostudy.wiki.resp.PageResp;
import com.caostudy.wiki.service.EbookService;
import com.caostudy.wiki.utils.CopyUtil;

import com.caostudy.wiki.utils.SnowFlake;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author Cao Study
 * @description EbookServiceImpl
 * @date 2021/8/24 18:52
 */
@Service
public class EbookServiceImpl implements EbookService {
    private static final Logger LOG = LoggerFactory.getLogger(EbookServiceImpl.class);

    @Autowired
    private EbookMapper ebookMapper;

    //雪花算法
    @Autowired
    private SnowFlake snowFlake;

    @Override
    public PageResp<EbookQueryResp> list(EbookQueryReq req) {
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        if (!StringUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);
        //只有用这个查询语句出来的list才能获取到准确的PageInfo，获取准确的total
        PageInfo<Ebook> pageInfo = new PageInfo<>(ebookList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<EbookQueryResp> respList = CopyUtil.copyList(ebookList, EbookQueryResp.class);
        PageResp<EbookQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;
    }

    /**
     * 保存ebook
     * 根据有无id属性来判断是更新还是新增
     *
     * @param req
     */
    @Override
    public void save(EbookSaveReq req) {
        Ebook ebook = CopyUtil.copy(req, Ebook.class);
        if (StringUtils.isEmpty(req.getId())) {
            //新增
            ebook.setId(snowFlake.nextId());
            ebookMapper.insert(ebook);
        } else {
            //更新
            ebookMapper.updateByPrimaryKey(ebook);
        }
    }
}


