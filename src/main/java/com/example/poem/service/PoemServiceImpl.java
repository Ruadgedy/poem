package com.example.poem.service;

import com.example.poem.dao.PoemDAO;
import com.example.poem.elastic.repository.PoemRepository;
import com.example.poem.entity.Category;
import com.example.poem.entity.Poem;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author :  yuhao
 * @date: 2020/10/19
 * @description:
 */
@Service
@Transactional
@Slf4j
public class PoemServiceImpl implements PoemService {

    @Autowired
    private PoemDAO poemDAO;

    @Autowired
    private PoemRepository poemRepository;

    @Qualifier("client")
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public Page<Poem> findAll(Integer page, Integer size) {
        return (Page<Poem>) poemRepository.findAll();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Poem> findByPage(Integer page, Integer size) {
        int start = (page - 1) * size;
        return poemDAO.findByPage(start, size);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Long findTotalCounts() {
        return poemDAO.findTotalCounts();
    }

    @Override
    public void saveAll() {
        poemRepository.deleteAll();
        // 重新创建
        List<Poem> all = poemDAO.findAll();
         poemRepository.saveAll(all);
    }

    @Override
    public void deleteAll() {
        poemRepository.deleteAll();
    }

    // content查询的内容， type代表诗词类型（唐诗还是宋词）， author代表作者
    @Override
    public List<Poem> findByKeywords(String content, String type, String author) {

        List<Poem> poems = null;
        try {
            SearchRequest searchRequest = new SearchRequest("poems");
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            // 搜索条件为空指定查询条件
            if (StringUtils.isEmpty(content)){
                // 设置查询为所有
                sourceBuilder.query(QueryBuilders.matchAllQuery());
            }else {
                sourceBuilder.query(QueryBuilders.multiMatchQuery(content, "name", "author", "content", "authordes"));
            }

            // 指定过滤条件
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            if (!StringUtils.isEmpty(author)){
                boolQueryBuilder.filter(QueryBuilders.termQuery("author", author));
            }
            if (!StringUtils.isEmpty(type)){
                boolQueryBuilder.filter(QueryBuilders.termQuery("type", type));
            }

            // 指定过滤
            sourceBuilder.postFilter(boolQueryBuilder);

            // 指定高亮
            sourceBuilder.highlighter(new HighlightBuilder().field("*").requireFieldMatch(false).preTags("<span style='color:red'>").postTags("</span>"));

            // 指定显示记录
            sourceBuilder.size(100);

            // 指定搜索类型
            searchRequest.types("poem").source(sourceBuilder);

            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            // 获取返回结果
            if (response.getHits().totalHits > 0) poems = new ArrayList<>();
            SearchHit[] hits = response.getHits().getHits();
            for (SearchHit hit : hits) {
                Poem poem = new Poem();
                // 获取原始字段
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                // 获取高亮字段
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();

                log.debug("sourceAsMap:[{}]", sourceAsMap);
                log.debug("highlightFields:[{}]",highlightFields);

                poem.setId(hit.getId());
                poem.setName(sourceAsMap.get("name").toString());
                if (highlightFields.containsKey("name")){
                    poem.setName(highlightFields.get("name").fragments()[0].toString());
                }
                // 作者
                poem.setAuthor(sourceAsMap.get("author").toString());
                if (highlightFields.containsKey("author")){
                    poem.setAuthor(highlightFields.get("author").fragments()[0].toString());
                }
                // 作者简介
                poem.setAuthordes(sourceAsMap.get("authordes").toString());
                if (highlightFields.containsKey("authordes")){
                    poem.setAuthordes(highlightFields.get("authordes").fragments()[0].toString());
                }
                // 分类
//                poem.getCategory().setName(sourceAsMap.get("category").toString());
                Category poemCategory = poem.getCategory();
                String toString = sourceAsMap.get("category").toString();
                poemCategory.setName(toString);


                // 内容
                poem.setContent(sourceAsMap.get("content").toString());
                if (highlightFields.containsKey("content")){
                    poem.setContent(highlightFields.get("content").fragments()[0].toString());
                }
                // 地址
                poem.setHref(sourceAsMap.get("href").toString());
                // 类型
                poem.setType(sourceAsMap.get("type").toString());

                poems.add(poem);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return poems;
    }
}
