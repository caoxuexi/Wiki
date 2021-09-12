package com.caostudy.wiki.controller;

import com.caostudy.wiki.req.DocSaveReq;
import com.caostudy.wiki.resp.DocQueryResp;
import com.caostudy.wiki.resp.CommonResp;
import com.caostudy.wiki.service.DocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

/**
 * @author Cao Study
 * @description DocController
 * @date 2021/8/24 18:43
 */
@RestController
@RequestMapping("/doc")
public class DocController {
    @Autowired
    private DocService docService;

    @GetMapping("/all/{ebookId}")
    public CommonResp all(@PathVariable Long ebookId) {
        CommonResp<List<DocQueryResp>> resp = new CommonResp<>();
        List<DocQueryResp> list = docService.all(ebookId);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody DocSaveReq req) {
        CommonResp resp = new CommonResp<>();
        docService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{idsStr}")
    public CommonResp delete(@PathVariable String idsStr) {
        //这个ids是1,2,3这样的格式字符串
        CommonResp resp = new CommonResp<>();
        List<String> list = Arrays.asList(idsStr.split(","));
        docService.delete(list);
        return resp;
    }

    @GetMapping("/find-content/{id}")
    public CommonResp findContent(@PathVariable Long id) {
        CommonResp<String> resp = new CommonResp<>();
        String content = docService.findContent(id);
        resp.setContent(content);
        return resp;
    }

    @GetMapping("/view/{id}")
    public CommonResp findContentWithViewIncr(@PathVariable Long id) {
        CommonResp<String> resp = new CommonResp<>();
        String content = docService.findContentWithViewIncr(id);
        resp.setContent(content);
        return resp;
    }

    @GetMapping("/vote/{id}")
    public CommonResp vote(@PathVariable Long id) {
        CommonResp commonResp = new CommonResp();
        docService.vote(id);
        return commonResp;
    }
}
