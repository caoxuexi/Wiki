package com.caostudy.wiki.controller;

import com.caostudy.wiki.exception.BusinessException;
import com.caostudy.wiki.exception.BusinessExceptionCodeEnum;
import com.caostudy.wiki.req.DocSaveReq;
import com.caostudy.wiki.resp.DocQueryResp;
import com.caostudy.wiki.resp.CommonResp;
import com.caostudy.wiki.service.DocService;
import com.caostudy.wiki.utils.LoginUserContext;
import com.caostudy.wiki.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @Autowired
    private RedisOperator redisOperator;

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

    @GetMapping("/checkinDocs/{id}")
    public CommonResp checkinDocs(@PathVariable Integer docsId){
        CommonResp commonResp = new CommonResp();
        lockCheckin(docsId);
        return commonResp;
    }

    public void lockCheckin(Integer docsId){
        String username= LoginUserContext.getUser().getName();

        String userLock = redisOperator.get(docsId.toString());
        if(StringUtils.isBlank(userLock)){
            //15分钟后自动释放锁
            redisOperator.set(docsId.toString(),username,1000*60*15);
        }else if(userLock.equals(username)){
            //如果操作的用户是同一人，则延长锁的时间
            redisOperator.set(docsId.toString(),username,1000*60*15);
        }else{
            //如果操作的不是同一个人，则提示当前的文档操作人，并禁止操作
            throw new BusinessException("当前"+userLock+"正在操作文档");
        }
    }

    @GetMapping("/checkoutDocs/{id}")
    public CommonResp checkoutDocs(@PathVariable Integer docsId){
        CommonResp commonResp = new CommonResp();
        lockCheckout(docsId);
        return commonResp;
    }

    public void lockCheckout(Integer docsId){
        String username=LoginUserContext.getUser().getName();
        String userLock = redisOperator.get(docsId.toString());
        if(username.equals(userLock)){
            //如果上锁的用户和现在释放锁的用户是同一个人那就释放锁
            redisOperator.del(docsId.toString());
        }else if(StringUtils.isBlank(userLock)){
            throw new BusinessException(BusinessExceptionCodeEnum.NOBODY_OPERATING);
        }else{
            throw new BusinessException(BusinessExceptionCodeEnum.HAS_PEOPLE_OPERATING);
        }
    }
}
