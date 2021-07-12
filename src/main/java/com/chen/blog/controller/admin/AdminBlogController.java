/**
 * FileName: AdminBlogController
 * Author:   嘉平十七
 * Date:     2021/3/11 11:06
 * Description: 后台比赛管理
 */
package com.chen.blog.controller.admin;

import com.chen.blog.domain.Blog;
import com.chen.blog.domain.BlogQuery;
import com.chen.blog.domain.User;
import com.chen.blog.service.BlogService;
import com.chen.blog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminBlogController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;
    
    /**
     * 设置分类
     * @param model
     */
    private void setType(Model model){
        model.addAttribute("types",typeService.listType());
    }

    /**
     * 后台获取所有比赛并分页显示
     * @param pageable
     * @param blogQuery
     * @param model
     * @return
     */
    @GetMapping("/blogs")
    public String blogs(@PageableDefault(size = 5,sort = {"createTime"},direction = Sort.Direction.DESC) Pageable pageable, BlogQuery blogQuery, Model model){
        setType(model);
        model.addAttribute("blogs", blogService.listBlog(pageable,blogQuery));
        return "admin/blogs";
    }

    /**
     * 后台比赛页面的搜索功能
     * @param pageable
     * @param blogQuery
     * @param model
     * @return
     */
    @PostMapping("/blogs/search")
    public String search(@PageableDefault(size = 5,sort = {"createTime"},direction = Sort.Direction.DESC) Pageable pageable, BlogQuery blogQuery, Model model){
        model.addAttribute("blogs", blogService.listBlog(pageable,blogQuery));
        return "admin/blogs::blogList";//返回片段
    }

    /**
     * 后台查看比赛
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/blog/{id}")
    public String see(@PathVariable Long id, Model model){
        setType(model);
        model.addAttribute("blog", blogService.getAndConvert(id));
        return "admin/blog";
    }

    /**
     * 后台编辑比赛
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/blog/{id}/input")
    public String editInput(@PathVariable Long id, Model model){
        setType(model);
        Blog blog = blogService.getBlog(id);
        model.addAttribute("blog",blog);    //拿到tagIds
        return "admin/edit";
    }

    /**
     * 后台删除比赛
     * @param id
     * @param attributes
     * @return
     */
    @GetMapping("/blog/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes attributes){
        blogService.deleteBlog(id);
        attributes.addFlashAttribute("message","删除成功");
        return "redirect:/admin/blogs";
    }

    /**
     * 后台跳转到发布比赛
     * @param model
     * @return
     */
    @GetMapping("/blog/input")
    public String input(Model model){
        model.addAttribute("blog",new Blog());
        setType(model);
        return "admin/edit";
    }

    /**
     * 后台发布比赛
     * @param blog
     * @param attributes
     * @param session
     * @return
     */
    @PostMapping("/blog/input")
    public String post(Blog blog, RedirectAttributes attributes, HttpSession session){
        blog.setUser((User) session.getAttribute("adminUser"));
        blog.setType(typeService.getType(blog.getType().getId()));
        Blog b;

        //处理view为0
        if (blog.getId() == null){
            b = blogService.saveBlog(blog);
        }else {
            b = blogService.updateBlog(blog.getId(),blog);
        }

        if (b == null){
            attributes.addFlashAttribute("message","操作失败");
        }else {
            attributes.addFlashAttribute("message","操作成功");
        }
        return "redirect:/admin/blogs";
    }

}