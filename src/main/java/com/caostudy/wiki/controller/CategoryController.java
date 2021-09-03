package com.caostudy.wiki.controller;

import com.caostudy.wiki.req.CategoryQueryReq;
import com.caostudy.wiki.req.CategorySaveReq;
import com.caostudy.wiki.resp.CommonResp;
import com.caostudy.wiki.resp.CategoryQueryResp;
import com.caostudy.wiki.resp.PageResp;
import com.caostudy.wiki.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Cao Study
 * @description CategoryController
 * @date 2021/8/24 18:43
 */
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/all")
    public CommonResp all() {
        CommonResp<List<CategoryQueryResp>> resp = new CommonResp<>();
        List<CategoryQueryResp> list = categoryService.all();
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody CategorySaveReq req) {
        CommonResp resp = new CommonResp<>();
        categoryService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp<>();
        categoryService.delete(id);
        return resp;
    }
}
