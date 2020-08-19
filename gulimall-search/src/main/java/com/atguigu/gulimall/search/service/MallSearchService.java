package com.atguigu.gulimall.search.service;

import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;

/**
 * Created by GuoChengQian on 2020/7/27 8:56
 */
public interface MallSearchService {
    /**
     * 检索的所有参数
     * @param param
     * @return
     */
    SearchResult search(SearchParam param);
}
