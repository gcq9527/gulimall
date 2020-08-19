package com.atguigu.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.search.config.GulimallElasticSearchConfig;
import com.atguigu.gulimall.search.constant.EsConstant;
import com.atguigu.gulimall.search.fegin.ProductFeginService;
import com.atguigu.gulimall.search.service.MallSearchService;
import com.atguigu.gulimall.search.vo.AttrResponseVo;
import com.atguigu.gulimall.search.vo.BrandVo;
import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Guo
 * @Create 2020/7/27 8:56
 */
@Service
public class MallSearchServiceImpl implements MallSearchService {
    @Autowired
    private RestHighLevelClient client;
    
    @Autowired
    private ProductFeginService feginService;
    private Object replace;

    //去es中检索
    @Override
    public SearchResult search(SearchParam param) {
        //1.动态构建查询需要的DSL语句
        SearchResult result = null;

        //1.准备检索请求
        SearchRequest searchRequest = buildSearchRequest(param);
        try {
            //2、执行检索请求
            SearchResponse response = client.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);

            //3、分析响应数据封装成我们想要的数据
            result = buildSearchResult(response,param);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * #模糊匹配 过滤 按照属性 分类 品牌 价格区间 库存 排序 分页 高亮 聚合分析 x lg
     * 准备检索请求
     * @param param
     * @return
     */
    public SearchRequest buildSearchRequest(SearchParam param){
        //构建DSL语句
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        /**
         * 模糊匹配 过滤 按照属性 分类 品牌 价格区间 库存
         */
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //1.1、must模糊匹配
        if (!StringUtils.isEmpty(param.getKeyword())) {
            boolQuery.must(QueryBuilders.matchQuery("skuTitle",param.getKeyword()));
            System.out.println(param.getKeyword());
        }
        //1.2 bool-filter - 按照三级分类id查询
        if (param.getCatalog3Id() != null) {
            boolQuery.filter(QueryBuilders.termQuery("catalogId",param.getCatalog3Id()));
        }
        //1.2 bool-filter - 按照品牌id查询
        if (param.getBrandId() != null && param.getBrandId().size() > 0 ) {
            boolQuery.filter(QueryBuilders.termsQuery("brandId",param.getBrandId()));
        }
        //1.2 bool-filter - 按照所有指定的属性查询
        if (param.getAttrs() != null && param.getAttrs().size() > 0) {
            for (String attrStr : param.getAttrs()) {
                //attrs=1.5寸&&attrs=1_16H
                BoolQueryBuilder nestedboolQuery = QueryBuilders.boolQuery();
                String[] s = attrStr.split("_");
                //
                String attrId = s[0];
                //这个属性检索用的值
                String[] attrValues = s[1].split(":");
                nestedboolQuery.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                nestedboolQuery.must(QueryBuilders.termsQuery("attrs.attrValue",attrValues));
                //每一个都得生成一个nested查询
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", nestedboolQuery, ScoreMode.None);
                boolQuery.filter(nestedQueryBuilder);
            }

        }
        //1.2 bool-filter - 按照库存是否进行查询
        if (param.getHasStock()!= null) {
            boolQuery.filter(QueryBuilders.termQuery("hasStock",param.getHasStock()==1));
        }


        //1.2 bool-filter - 价格区间
        if (!StringUtils.isEmpty(param.getSkuPrice())) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
            String s[] = param.getSkuPrice().split("_");
            //有两个价格
            if (s.length == 2) {
                //区间
                rangeQueryBuilder.gte(s[0]).lte(s[1]);
            }else if (s.length == 1) {
                //以_开头
                if (param.getSkuPrice().startsWith("_")) {
                    rangeQueryBuilder.lte(s[0]);
                }
                //
                if (param.getSkuPrice().endsWith("_")) {
                    rangeQueryBuilder.gte(s[0]);
                }
            }
            boolQuery.filter(rangeQueryBuilder);
        }

        //把所有的条件都封装
        sourceBuilder.query(boolQuery);
        /**
         * 排序 分页 高亮
         */
        //2.1、排序
        if (!StringUtils.isEmpty(param.getSort())) {
            String sort = param.getSort();
            //sort=hotScore_asc/desc
            String[] s = sort.split("_");
            //SortOrder.DES 得到字符串desc
            SortOrder order = s[1].equalsIgnoreCase("asc")?SortOrder.ASC:SortOrder.DESC;
            sourceBuilder.sort(s[0],order);
        }

        //2.2、分页 pageSize:5
        sourceBuilder.from(((param.getPageNum()-1)*EsConstant.PRODUCT_PAGESIZE));
        sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);

        //2.3、高亮
        if (!StringUtils.isEmpty(param.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style= 'color:red'>");
            highlightBuilder.postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);
        }

        /**
         * 聚合分析
         */
        //1、品牌聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId").size(50);

        //品牌聚合的子聚合
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName")).size(2);
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg")).size(2);
        //TODO 1、聚合brand
        sourceBuilder.aggregation(brand_agg);

        //2.分类聚合 catalog_agg
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(20);
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));
        //TODO 2、聚合catalog
        sourceBuilder.aggregation(catalog_agg);

        //3、属性聚合 attr_agg
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
        //聚合出当前所有的attrId
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        //聚合分析出当前所有attrId对应的名字
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName"));
        //聚合分析出当前attrid对应的所有可能的属性值 attrvalue
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue"));

        attr_agg.subAggregation(attr_id_agg);
        //TODO 3、聚合attr
        sourceBuilder.aggregation(attr_agg);

        String s = sourceBuilder.toString();
        System.out.println("构建的DSL" + s);
        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
        return searchRequest;
    }

    /**
     * 构建结果数据
     * @param response
     * @return
     */
    public SearchResult buildSearchResult(SearchResponse response,SearchParam param ){

        SearchResult result = new SearchResult();
        //1、返回所有的查询到的商品
        //拿到hits内的信息
        SearchHits hits = response.getHits();
        List<SkuEsModel> esModels = new ArrayList<>();
        if (hits.getHits() != null && hits.getHits().length > 0) {
            //循环遍历取出
            for (SearchHit hit : hits.getHits()) {
                //返回string形式
                String sourceAsString = hit.getSourceAsString();
                SkuEsModel sku = new SkuEsModel();
                //转成json存入集合
                SkuEsModel esModel = JSON.parseObject(sourceAsString,SkuEsModel.class);
                //keyword不为空设置高亮
                if (!StringUtils.isEmpty(param.getKeyword())) {
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String string = skuTitle.getFragments()[0].string();
                    esModel.setSkuTitle(string);
                }
                esModels.add(esModel);
            }
        }

        result.setProducts(esModels);
        //设置分类
        ParsedLongTerms catalog_agg =  response.getAggregations().get("catalog_agg");
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        List<? extends Terms.Bucket> buckets = catalog_agg.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            //得到的分类id
            String keyAsString = bucket.getKeyAsString();
            catalogVo.setCatalogId(Long.parseLong(keyAsString));
            //得到分类名
            ParsedStringTerms catalog_name_agg = bucket.getAggregations().get("catalog_name_agg");
            String catalog_name = catalog_name_agg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalog_name);
            catalogVos.add(catalogVo);
        }
        result.setCatalogs(catalogVos);
        //2、当前所有商品涉及到的所有属性信息
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedNested attr_agg = response.getAggregations().get("attr_agg");
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attr_id_agg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();

            //1、得到属性的id
            long attrId = bucket.getKeyAsNumber().longValue();
            //2、得到属性名字
            String attr_name = ((ParsedStringTerms) bucket.getAggregations().get("attr_name_agg")).getBuckets().get(0).getKeyAsString();
            //3、得到属性的所有值
            List<String> attr_Value = ((ParsedStringTerms) bucket.getAggregations().get("attr_value_agg")).getBuckets().stream().map(item -> {
                String keyAsString = ((Terms.Bucket) item).getKeyAsString();
                return keyAsString;
            }).collect(Collectors.toList());
            attrVo.setAttrId(attrId);
            attrVo.setAttrName(attr_name);
            attrVo.setAttrValue(attr_Value);
            attrVos.add(attrVo);
        }
        //设置所有属性信息
        result.setAttrs(attrVos);
       //3、当前所有商品涉及到的所有品牌信息
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brand_agg = response.getAggregations().get("brand_agg");
        for(Terms.Bucket bucket : brand_agg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            //1、得到品牌id
            long brandId = bucket.getKeyAsNumber().longValue();

            //2、得到品牌的名字
            String brand_Img_agg = ((ParsedStringTerms) bucket.getAggregations().get("brand_img_agg")).getBuckets().get(0).getKeyAsString();
            //3、得到品牌的名字
            String brand_name = ((ParsedStringTerms)bucket.getAggregations().get("brand_name_agg")).getBuckets().get(0).getKeyAsString();
            brandVo.setBrandId(brandId);
            brandVo.setBrandImg(brand_Img_agg);
            brandVo.setBrandName(brand_name);
            brandVos.add(brandVo);
        }

        result.setBrands(brandVos);
//        ============以上从聚合信息获取==============
        //分页信息-页码
        result.setPageNum(param.getPageNum());
//        //5、分页信息总记录数
        Long total = hits.getTotalHits().value;
        result.setTotal(total);
//        //6.分页信息-总页码
        int totalPages = (int) (total%EsConstant.PRODUCT_PAGESIZE == 0 ?total/EsConstant.PRODUCT_PAGESIZE:(total/EsConstant.PRODUCT_PAGESIZE+1));
        result.setTotalPages(totalPages);


        List<Integer> pageNums = new ArrayList<>();
        for (int i =1; i<totalPages;i++) {
            pageNums.add(i);
        }
        result.setPageNavs(pageNums);

        //7、构建面包屑导航功能
        List<SearchResult.NavVo> navVos = new ArrayList<>();
        if (param.getAttrs() != null && param.getAttrs().size() > 0) {
            List<SearchResult.NavVo> collect = param.getAttrs().stream().map(attr -> {
                //1、分析每个attrs传过来的查询参数值
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                //以_拆分
                String[] s = attr.split("_");

                navVo.setNavValue(s[1]);
                R r = feginService.getAttrInfo(Long.parseLong(s[0]));
                result.getAttrIds().add(Long.parseLong(s[0]));
                //响应成功
                if (r.getCode() == 0) {
                    //根据key拿出对应属性
                    AttrResponseVo data = r.getData("attr", new TypeReference<AttrResponseVo>() {
                    });

                    navVo.setNavName(data.getAttrName());
                } else {
                    navVo.setNavValue(s[0]);
                }
                //2、取消了面包屑以后 我们要跳转到那个地方  将请求地址的url里面的当前置空
                //拿到所有查询条件
                String replace = replaceQueryString(param, attr, "attrs");
                navVo.setLink("http://search.gulimall.com/list.html?" + replace);

                return navVo;
            }).collect(Collectors.toList());
            result.setNavs(collect);
        }
        //品牌，分类
        if(param.getBrandId() != null && param.getBrandId().size()>0) {
            List<SearchResult.NavVo> navs = result.getNavs();
            SearchResult.NavVo navVo = new SearchResult.NavVo();

            navVo.setNavName("品牌");
            //TODO 远程查询
            R r = feginService.brandInfo(param.getBrandId());
            if (r.getCode() == 0) {
                List<BrandVo> brand = r.getData("brand", new TypeReference<List<BrandVo>>() {
                });
                StringBuffer buffer = new StringBuffer();
                String replace = "";
                for (BrandVo brandVo : brand) {
                    buffer.append(brandVo.getName() + ";");
                    replace = replaceQueryString(param, brandVo.getBrandId()+"", "brandId");
                }
                navVo.setNavValue(buffer.toString());
                navVo.setLink("http://search.gulimall.com/list.html?" + replace);
            }
            navs.add(navVo);
        }

        //TODO 分类 不需要导航取消

        return result;
    }
    private String replaceQueryString(SearchParam param, String value,String key) {
        String encode = "";
        try {
            encode = URLEncoder.encode(value, "UTF-8");
            //+ 对应浏览器的%20编码
            encode = encode.replace("+","%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
       return  param.get_queryString().replace("&" + key + "=" + encode, "");
    }
}