package com.caostudy.wiki.service.impl;

import com.caostudy.wiki.domain.Content;
import com.caostudy.wiki.domain.Doc;
import com.caostudy.wiki.domain.DocExample;
import com.caostudy.wiki.domain.Ebook;
import com.caostudy.wiki.exception.BusinessException;
import com.caostudy.wiki.exception.BusinessExceptionCodeEnum;
import com.caostudy.wiki.mapper.ContentMapper;
import com.caostudy.wiki.mapper.DocMapper;
import com.caostudy.wiki.mapper.DocMapperCustom;
import com.caostudy.wiki.mapper.EbookMapper;
import com.caostudy.wiki.req.DocQueryReq;
import com.caostudy.wiki.req.DocSaveReq;
import com.caostudy.wiki.resp.DocQueryResp;
import com.caostudy.wiki.resp.PageResp;
import com.caostudy.wiki.service.DocService;
import com.caostudy.wiki.service.WsService;
import com.caostudy.wiki.utils.*;
import com.caostudy.wiki.websocket.WebSocketServer;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private DocMapperCustom docMapperCustom;

    @Autowired
    private EbookMapper ebookMapper;

    @Autowired
    private WsService wsService;

    //雪花算法
    @Autowired
    private SnowFlake snowFlake;
    @Autowired
    private RedisOperator redisOperator;

    /**
     * 查询指定ebook的文档
     * @return
     * @param ebookId
     */
    @Override
    public List<DocQueryResp> all(Long ebookId) {
        DocExample docExample = new DocExample();
        docExample.createCriteria().andEbookIdEqualTo(ebookId);
        docExample.setOrderByClause("sort asc");
        List<Doc> docList = docMapper.selectByExample(docExample);

        // 列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);
        return list;
    }


    /**
     * 分页查询方法
     *
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
    @Transactional
    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        Content content = CopyUtil.copy(req, Content.class);
        //因为doc.id=content.id所以这里还是判断doc的id就可以了
        if (ObjectUtils.isEmpty(req.getId())) {
            // 新增
            doc.setId(snowFlake.nextId());
            doc.setViewCount(0);
            doc.setVoteCount(0);
            docMapper.insert(doc);

            content.setId(doc.getId());
            contentMapper.insert(content);
        } else {
            // 更新
            docMapper.updateByPrimaryKey(doc);
            /**
             * Blob代表富文本字段，如果我们一张表既有大字段又有小字段则
             * updateByPrimaryKey和updateByPrimaryKeyWithBLOBs两个方法都会生成
             * updateByPrimaryKey是没有关于大字段的操作的
             * updateByPrimaryKeyWithBLOBs会带上大字段
             * 同样的还有selectByExample和selectByExampleWithBLOBs
             */
            int count=contentMapper.updateByPrimaryKeyWithBLOBs(content);
            //更新，如果没有则插入。因为有些时候文档的id有，但是content的我们没有去做
            if (count==0){
                contentMapper.insert(content);
            }
        }
        //查询出文档所属的ebook
        Ebook ebook = ebookMapper.selectByPrimaryKey(req.getEbookId());
        String logId = MDC.get("LOG_ID");
        wsService.sendInfo("【"+ebook.getName()+"】"+"的"+req.getName()+"被修改保存，可刷新查看！",logId);
    }

    /**
     * 根据id删除doc
     * @param id
     */
    @Override
    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
    }

    /**
     * 批量删除doc
     * @param ids
     */
    @Override
    public void delete(List<String> ids) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }

    /**
     * 查出对应文档的内容
     * @param id
     * @return
     */
    @Override
    public String findContent(Long id) {
        Content content=contentMapper.selectByPrimaryKey(id);

        if(ObjectUtils.isEmpty(content)){
            return "";
        }else{
            return content.getContent();
        }
    }

    /**
     * 查出对应文档内容的同时，增加文档阅读数
     * @param id
     * @return
     */
    @Override
    public String findContentWithViewIncr(Long id) {
        Content content=contentMapper.selectByPrimaryKey(id);
        docMapperCustom.increaseViewCount(id);

        if(ObjectUtils.isEmpty(content)){
            return "";
        }else{
            return content.getContent();
        }
    }

    @Override
    public void vote(Long id) {
        // docMapperCust.increaseVoteCount(id);
        // 远程IP+doc.id作为key，24小时内不能重复, 同一ip一天只能点赞一次
        String ip = RequestContext.getRemoteAddr();
        if (redisOperator.validateRepeat("DOC_VOTE_" + id + "_" + ip, 3600*24)) {
            docMapperCustom.increaseVoteCount(id);
        } else {
            throw new BusinessException(BusinessExceptionCodeEnum.VOTE_REPEAT);
        }
    }
}


