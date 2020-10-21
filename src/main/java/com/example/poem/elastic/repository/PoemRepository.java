package com.example.poem.elastic.repository;

import com.example.poem.entity.Poem;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author :  yuhao
 * @date: 2020/10/19
 * @description:
 */
public interface PoemRepository extends ElasticsearchRepository<Poem, String> {
}
