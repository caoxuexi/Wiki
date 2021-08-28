package com.caostudy.wiki.req;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Data
public class PageReq {
    @NotNull(message = "【页码】不能为空")
    private int page;

    @NotNull(message = "【每页条数】不能为空")
    @Max(value = 1000, message = "【每页条数】不能超过1000")
    private int size;
}
