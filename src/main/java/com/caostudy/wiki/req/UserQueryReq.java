package com.caostudy.wiki.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryReq extends PageReq {

    private String loginName;
}
