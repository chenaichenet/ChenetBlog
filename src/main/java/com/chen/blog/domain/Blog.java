/**
 * FileName: Blog
 * Author:   嘉平十七
 * Date:     2021/6/18 10:05
 * Description: 博客实体类
 */
package com.chen.blog.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "blog")
public class Blog {
    @Id
    @GeneratedValue
    private Long id;
    //博客标题
    private String title;
    //博客图片
    private String picture;
    //博客内容
    @Lob    //指定持久属性或字段应作为大对象持久保存到数据库支持的大对象类型
    @Basic(fetch = FetchType.LAZY)  //懒加载
    private String content;
    //博客浏览量
    private Integer views;
    //博客描述
    private String description;
    //创建时间
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    //发布人
    @ManyToOne
    private User user;
    //所属分类
    @ManyToOne
    private Type type;
    //博客评论
    @OneToMany(mappedBy = "blog",cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    public Blog() {
    }

    @Override
    public String toString() {
        return "Blog{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", picture='" + picture + '\'' +
                ", content='" + content + '\'' +
                ", views=" + views +
                ", description='" + description + '\'' +
                ", createTime=" + createTime +
                ", user=" + user +
                ", type=" + type +
                ", comments=" + comments +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}