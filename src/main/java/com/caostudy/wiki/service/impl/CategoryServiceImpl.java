package com.caostudy.wiki.service.impl;

import com.caostudy.wiki.domain.Category;
import com.caostudy.wiki.domain.CategoryExample;
import com.caostudy.wiki.mapper.CategoryMapper;
import com.caostudy.wiki.req.CategoryQueryReq;
import com.caostudy.wiki.req.CategorySaveReq;
import com.caostudy.wiki.resp.CategoryQueryResp;
import com.caostudy.wiki.resp.PageResp;
import com.caostudy.wiki.service.CategoryService;
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
 * @description CategoryServiceImpl
 * @date 2021/8/24 18:52
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    private static final Logger LOG = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    //雪花算法
    @Autowired
    private SnowFlake snowFlake;

    /**
     * 查询所有分裂
     * @return
     */
    @Override
    public List<CategoryQueryResp> all() {
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.setOrderByClause("sort asc");
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);

        // 列表复制
        List<CategoryQueryResp> list = CopyUtil.copyList(categoryList, CategoryQueryResp.class);
        return list;
    }


    /**
     * 分页查询方法
     * @param req
     * @return
     */
    @Deprecated
    @Override
    public PageResp<CategoryQueryResp> list(CategoryQueryReq req) {
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.setOrderByClause("sort asc");
        CategoryExample.Criteria criteria = categoryExample.createCriteria();
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);

        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        // 列表复制
        List<CategoryQueryResp> list = CopyUtil.copyList(categoryList, CategoryQueryResp.class);

        PageResp<CategoryQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    /**
     * 新增或保存category
     * 根据有无id属性来判断是更新还是新增
     * @param req
     */
    @Override
    public void save(CategorySaveReq req) {
        Category category = CopyUtil.copy(req, Category.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            // 新增
            category.setId(snowFlake.nextId());
            categoryMapper.insert(category);
        } else {
            // 更新
            categoryMapper.updateByPrimaryKey(category);
        }
    }

    /**
     * 删除category
     * @param id
     */
    @Override
    public void delete(Long id) {
        categoryMapper.deleteByPrimaryKey(id);
    }
}


