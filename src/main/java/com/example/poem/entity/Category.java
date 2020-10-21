package com.example.poem.entity;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author :  yuhao
 * @date: 2020/10/19
 * @description:
 */

@Data
public class Category {

    @Field(type = FieldType.Keyword, index = false)
    private String id;

    @Field(type = FieldType.Keyword)
    private String name;
}
