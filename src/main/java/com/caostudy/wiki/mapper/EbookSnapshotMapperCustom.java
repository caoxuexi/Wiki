package com.caostudy.wiki.mapper;

import com.caostudy.wiki.resp.StatisticResp;

import java.util.List;

public interface EbookSnapshotMapperCustom {

    public void genSnapshot();

    List<StatisticResp> getStatistic();

    List<StatisticResp> get30Statistic();
}