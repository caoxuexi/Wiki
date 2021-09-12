package com.caostudy.wiki.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * @author Cao Study
 * @description DocMapperCustom
 * @date 2021/9/12 13:28
 */
public interface DocMapperCustom {
    public void increaseViewCount(@Param("id") Long id);

    public void increaseVoteCount(@Param("id") Long id);
}
