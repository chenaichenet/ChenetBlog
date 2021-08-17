/**
 * FileName: NoticeService
 * Author:   嘉平十七
 * Date:     2021/8/17 17:57
 * Description: 公告服务接口
 */
package com.chen.blog.service;

import com.chen.blog.domain.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeService {

    //列出所有公告并分页
    Page<Notice> listNotices(Pageable pageable);

    Notice getNoticeById(Long id);

    Notice getAndConvert(Long id);

    //新增公告
    Notice addNotice(Notice notice);

    //修改公告
    Notice editNotice(Long id, Notice notice);

    //删除公告
    void deleteNotice(Long id);
}
