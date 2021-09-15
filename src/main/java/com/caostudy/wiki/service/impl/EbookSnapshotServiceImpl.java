package com.caostudy.wiki.service.impl;

import com.caostudy.wiki.mapper.EbookSnapshotMapperCustom;
import com.caostudy.wiki.resp.StatisticResp;
import com.caostudy.wiki.service.EbookSnapshotService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookSnapshotServiceImpl implements EbookSnapshotService {

    @Resource
    private EbookSnapshotMapperCustom ebookSnapshotMapperCust;

    @Override
    public void genSnapshot() {
        ebookSnapshotMapperCust.genSnapshot();
    }

    /**
     * 获取首页数值数据：总阅读数、总点赞数、今日阅读数、今日点赞数、今日预计阅读数、今日预计阅读增长
     */
    @Override
    public List<StatisticResp> getStatistic() {
        return ebookSnapshotMapperCust.getStatistic();
    }

    /**
     * 30天数值统计
     */
    @Override
    public List<StatisticResp> get30Statistic() {
        return ebookSnapshotMapperCust.get30Statistic();
    }
}