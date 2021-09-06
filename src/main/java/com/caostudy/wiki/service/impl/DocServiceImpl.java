package com.caostudy.wiki.service.impl;

import com.caostudy.wiki.domain.Doc;
import com.caostudy.wiki.domain.DocExample;
import com.caostudy.wiki.mapper.DocMapper;
import com.caostudy.wiki.req.DocQueryReq;
import com.caostudy.wiki.req.DocSaveReq;
import com.caostudy.wiki.resp.DocQueryResp;
import com.caostudy.wiki.resp.PageResp;
import com.caostudy.wiki.service.DocService;
import com.caostudy.wiki.utils.CopyUtil;
import com.caostudy.wiki.utils.SnowFlake;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @author Cao Study
 * @description DocServiceImpl
 * @date 2021/8/24 18:52
 */
@Service
public class DocServiceImpl implements DocService {
    private static final Logger LOG = LoggerFactory.getLogger(DocServiceImpl.class);

    @Autowired
    private DocMapper docMapper;

    //雪花算法
    @Autowired
    private SnowFlake snowFlake;

    /**
     * 查询所有分裂
     * @return
     */
    @Override
    public List<DocQueryResp> all() {
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        List<Doc> docList = docMapper.selectByExample(docExample);

        // 列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);
        return list;
    }


    /**
     * 分页查询方法
     * @param req
     * @return
     */
    @Deprecated
    @Override
    public PageResp<DocQueryResp> list(DocQueryReq req) {
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        DocExample.Criteria criteria = docExample.createCriteria();
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Doc> docList = docMapper.selectByExample(docExample);

        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        // 列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        PageResp<DocQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    /**
     * 新增或保存doc
     * 根据有无id属性来判断是更新还是新增
     * @param req
     */
    @Override
    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            // 新增
            doc.setId(snowFlake.nextId());
            docMapper.insert(doc);
        } else {
            // 更新
            docMapper.updateByPrimaryKey(doc);
        }
    }

    /**
     * 删除doc
     * @param id
     */
    @Override
    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
    }
}


