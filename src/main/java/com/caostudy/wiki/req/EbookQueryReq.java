package com.caostudy.wiki.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EbookQueryReq extends PageReq {
    private Long id;

    private String name;

    private Long categoryId2;

}
