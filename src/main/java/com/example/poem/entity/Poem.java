package com.example.poem.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author :  yuhao
 * @date: 2020/10/19
 * @description:
 */
@Data
@Document(indexName = "poems", type = "poem")
@NoArgsConstructor
@AllArgsConstructor
public class Poem {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String name;

    @Field(type = FieldType.Keyword)
    private String author;

    @Field(type = FieldType.Keyword)
    private String type;

    @Field(analyzer = "ik_max_word", type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Keyword)
    private String href;

    @Field(analyzer = "ik_max_word", type = FieldType.Text)
    private String authordes;

    @Field(type = FieldType.Keyword)
    private String origin;

    @Field(type = FieldType.Nested)
    private Category category = new Category();
}
