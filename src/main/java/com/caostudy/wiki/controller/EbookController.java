package com.caostudy.wiki.controller;

import com.caostudy.wiki.bo.EbookQueryReq;
import com.caostudy.wiki.domain.Ebook;
import com.caostudy.wiki.resp.CommonResp;
import com.caostudy.wiki.resp.EbookQueryResp;
import com.caostudy.wiki.service.EbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Cao Study
 * @description EbookController
 * @date 2021/8/24 18:43
 */
@RestController
@RequestMapping("/ebook")
public class EbookController {
    @Autowired
    private EbookService ebookService;

    @GetMapping("/list")
    public CommonResp list(EbookQueryReq req) {
        CommonResp<List<EbookQueryResp>> resp = new CommonResp<>();
        List<EbookQueryResp> list=ebookService.list(req);
        resp.setContent(list);
        return resp;
    }
}
