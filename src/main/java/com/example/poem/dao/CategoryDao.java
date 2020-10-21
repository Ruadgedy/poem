package com.example.poem.dao;

import com.example.poem.entity.Category;
import org.apache.ibatis.annotations.*;
import org.springframework.data.annotation.Id;

/**
 * @author :  yuhao
 * @date: 2020/10/20
 * @description:
 */
@Mapper
public interface CategoryDao {
    @Results({
            @Result(column = "id", property = "id", id = true),
            @Result(column = "name", property = "name")
    })
    @Select("select * from t_category c where c.id=#{feng}")
    public Category findCategoryById(@Param("feng") String feng);
}
