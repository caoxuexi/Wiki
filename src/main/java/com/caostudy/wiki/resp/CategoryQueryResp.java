package com.caostudy.wiki.resp;

import lombok.Data;

@Data
public class CategoryQueryResp {
    private Long id;

    private Long parent;

    private String name;

    private Integer sort;
}
