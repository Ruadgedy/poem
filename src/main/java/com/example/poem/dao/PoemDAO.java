package com.example.poem.dao;

import com.example.poem.entity.Category;
import com.example.poem.entity.Poem;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

/**
 * @author :  yuhao
 * @date: 2020/10/19
 * @description:
 */
@Mapper
public interface PoemDAO {
    @Select("select * from t_poem")
    @Results({
            @Result(column = "id", property = "id", id = true),
            @Result(column = "name", property = "name"),
            @Result(column = "author", property = "author"),
            @Result(column = "type", property = "type"),
            @Result(column = "origin",property = "origin"),
            @Result(column = "content", property = "content"),
            @Result(column = "href", property = "href"),
            @Result(column = "authordes", property = "authordes"),
            @Result(column = "categoryId", property = "category", one = @One(select = "com.example.poem.dao.CategoryDao.findCategoryById", fetchType = FetchType.EAGER))
    })
    List<Poem> findAll();

//    @Select("select c.id, c.name from t_category c where c.id=#{id}")
//    @ResultType(Category.class)
//    Category findCategoryById(@Param("id") String id);

    @Select("select p.id,p.name,p.author,p.type,p.origin,p.content,p.href,p.authordes, " +
            "        c.id cid,c.name cname\n" +
            "        from t_poem p\n" +
            "        left join t_category c\n" +
            "        on p.categoryid = c.id\n" +
            "        limit #{start},#{size}")
    List<Poem> findByPage(@Param("start") Integer start, @Param("size") Integer size);

    @Select("select count(p.id) \n" +
            "from t_poem p \n" +
            "left join t_category c \n" +
            "on p.categoryId = c.id")
    Long findTotalCounts();
}
