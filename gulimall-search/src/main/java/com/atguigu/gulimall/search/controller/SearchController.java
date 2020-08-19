package com.atguigu.gulimall.search.controller;

import com.atguigu.gulimall.search.service.MallSearchService;
import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Guo
 * @Create 2020/7/26 20:18
 */
@Controller
public class SearchController {

    @Autowired
    MallSearchService mallSearchService;

    /**
     *在 自动将
     * @param param
     * @return
     */
    @GetMapping("/list.html")
    public String listPage(SearchParam param, Model model, HttpServletRequest httpServletRequest) {
        param.set_queryString(httpServletRequest.getQueryString());
        //1.根据传递来的页面参数区 es中检索商品
       SearchResult result = mallSearchService.search(param);
        model.addAttribute("result",result);
        return "list";
    }
}