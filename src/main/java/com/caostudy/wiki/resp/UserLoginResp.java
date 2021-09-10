package com.caostudy.wiki.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Cao Study
 */
@Data
public class UserLoginResp implements Serializable {
    private Long id;

    private String loginName;

    private String name;

    private String token;
}
