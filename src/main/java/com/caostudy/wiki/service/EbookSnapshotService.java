package com.caostudy.wiki.service;

import com.caostudy.wiki.resp.StatisticResp;

import java.util.List;

/**
 * @author Cao Study
 * @description EbookSnapshotService
 * @date 2021/9/15 22:02
 */
public interface EbookSnapshotService {
    void genSnapshot();

    List<StatisticResp> getStatistic();

    List<StatisticResp> get30Statistic();
}
