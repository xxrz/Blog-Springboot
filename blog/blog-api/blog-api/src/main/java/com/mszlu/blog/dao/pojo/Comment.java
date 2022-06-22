package com.mszlu.blog.dao.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class Comment {

    private Long id;

    private String content;

    private Long createDate;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long articleId;

    private Long authorId;

    private Long parentId;

    private Long toUid;

    private Integer level;
}
