package com.caostudy.wiki.controller;

import com.caostudy.wiki.req.DocSaveReq;
import com.caostudy.wiki.resp.DocQueryResp;
import com.caostudy.wiki.resp.CommonResp;
import com.caostudy.wiki.service.DocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    @GetMapping("/all")
    public CommonResp all() {
        CommonResp<List<DocQueryResp>> resp = new CommonResp<>();
        List<DocQueryResp> list = docService.all();
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody DocSaveReq req) {
        CommonResp resp = new CommonResp<>();
        docService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp<>();
        docService.delete(id);
        return resp;
    }
}
