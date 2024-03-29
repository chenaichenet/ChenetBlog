package com.chen.website.controller;

import com.chen.website.service.BlogService;
import com.chen.website.service.NoticeService;
import com.chen.website.service.TypeService;
import com.chen.website.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * 前台首页的所有请求的处理类
 * @author ChenetChen
 * @since 2021/6/18 14:29
 */
@Controller
public class IndexController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private NoticeService noticeService;

    /**
     * 首页定位
     * @param pageable 分页参数
     * @param model 视图
     * @return 首页
     */
    @GetMapping({"/","/index"})
    public String index(@PageableDefault(size = 5,sort = {"createTime"},direction = Sort.Direction.DESC)Pageable pageable, Model model, Locale locale){
        //todo-chen pageable参数混乱，应固定在某一层中生成
        model.addAttribute("types",typeService.listType(pageable));
        model.addAttribute("blogs", blogService.listBlog(pageable));//查询所有比赛并分页
        model.addAttribute("hotTypes",typeService.listTypeTop(5));  //前6个分类
        model.addAttribute("hotBlogs", blogService.listBlogTop(5)); //前6个比赛
        return "index";
    }

    @GetMapping(value = "/locale")
    public String localeHandler(HttpServletRequest request) {
        String lastUrl = request.getHeader("referer");
        return "redirect:" + lastUrl;
    }

    /**
     * 后台首页定位
     * @return 后台首页
     */
    @GetMapping("/admin/index")
    public String index_admin(){
        return "admin/index";
    }

    /**
     * 归档页面
     * @param model 视图
     * @return 归档页面
     */
    @GetMapping("/archives")
    public String archives(Model model){
        model.addAttribute("types",typeService.listType());
        model.addAttribute("archiveMap", blogService.archiveBlog());
        model.addAttribute("blogCount", blogService.countBlog());
        return "archives";
    }

    /**
     * 公告通知页面
     * @param pageable 分页参数
     * @param model 视图
     * @return 公告页面
     */
    @GetMapping("/notices")
    public String notices(@PageableDefault(size = 5,sort = {"createTime"},direction = Sort.Direction.DESC)Pageable pageable,Model model){
        model.addAttribute("notices",noticeService.listNotices(pageable));
        return "notices";
    }

    @GetMapping("/notice/{id}")
    public String notice(@PathVariable Long id, Model model){
        model.addAttribute("notice",noticeService.getAndConvert(id));
        return "notice";
    }

    /**
     * 关于页面
     * @param model
     * @return
     */
    @GetMapping("/about")
    public String about(Model model){
        model.addAttribute("types",typeService.listType());
        return "about";
    }

    /**
     * 关键字查询
     * @param pageable
     * @param query
     * @param model
     * @return
     */
    @PostMapping("/search")
    public String search(@PageableDefault(size = 5,sort = {"createTime"},direction = Sort.Direction.DESC) Pageable pageable, @RequestParam String query, Model model){
        model.addAttribute("types",typeService.listType(pageable));
        model.addAttribute("users",userService.findUserByQuery("%"+query+"%",pageable));
        model.addAttribute("blogs", blogService.listBlogByQuery("%"+query+"%",pageable));
        model.addAttribute("query",query);
        return "search";
    }

    /**
     * 分类查询
     * @param pageable
     * @param typename
     * @param model
     * @return
     */
    @GetMapping("/search/{typename}")
    public String searchByTypename(@PageableDefault(size = 5,sort = {"createTime"},direction = Sort.Direction.DESC) Pageable pageable, @PathVariable String typename, Model model){
        model.addAttribute("types",typeService.listType(pageable));
        model.addAttribute("blogs", blogService.listBlogByTypeName("%"+typename+"%",pageable));
        return "search";
    }

    /**
     * IP异常跳转
     * @return
     */
    @GetMapping("/error/ipError")
    public String ipError(){
        return "/error/ipError";
    }

    /**
     * 隐私政策
     * @param model
     * @return
     */
    @GetMapping("/privacy")
    public String privacyProtocol(Model model){
        model.addAttribute("types",typeService.listType());
        return "protocol/privacy";
    }

    /**
     * 隐私政策
     * @param model
     * @return
     */
    @GetMapping("/disclaimer")
    public String disclaimerProtocol(Model model){
        model.addAttribute("types",typeService.listType());
        return "protocol/disclaimer";
    }
}