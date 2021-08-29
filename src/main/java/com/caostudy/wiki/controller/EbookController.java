package com.caostudy.wiki.controller;

import com.caostudy.wiki.req.EbookQueryReq;
import com.caostudy.wiki.req.EbookSaveReq;
import com.caostudy.wiki.resp.CommonResp;
import com.caostudy.wiki.resp.EbookQueryResp;
import com.caostudy.wiki.resp.PageResp;
import com.caostudy.wiki.service.EbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        CommonResp<PageResp<EbookQueryResp>> resp = new CommonResp<>();
        PageResp<EbookQueryResp> list = ebookService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@RequestBody EbookSaveReq req) {
        CommonResp resp = new CommonResp<>();
        ebookService.save (req);
        return resp;
    }
}
