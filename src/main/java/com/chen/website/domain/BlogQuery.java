package com.chen.website.domain;

/**
 * 博客查询实体类
 * @author ChenetChen
 * @since 2021/6/18 10:26
 */
public class BlogQuery {
    private String title;
    private Long typeId;

    public BlogQuery() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }
}