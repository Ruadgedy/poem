package com.example.poem.controller;

import com.example.poem.entity.Poem;
import com.example.poem.service.PoemService;
import com.github.houbb.segment.support.segment.result.impl.SegmentResultHandlers;
import com.github.houbb.segment.util.SegmentHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author :  yuhao
 * @date: 2020/10/19
 * @description:
 */
@RestController
@RequestMapping("poem")
@Slf4j
public class PoemController {

    @Resource
    private PoemService poemService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 编辑操作(当用户点击后台按钮，执行相应的操作)
    public Map<String, Object> edit(String oper){
        if (oper.equals("add")){

        }
        if (oper.equals("edit")){

        }
        if (oper.equals("del")){

        }
        return null;
    }

    // 分页查询所有
    @RequestMapping("findByPage")
    public Map<String, Object> findByPage(Integer page, Integer rows){
        log.info("当前页：[{}] 每页展示记录数：[{}]", page, rows);

        Map<String, Object> result = new HashMap<>();

        List<Poem> byPage = poemService.findByPage(page, rows);

        Long totalCounts = poemService.findTotalCounts();

        // 计算总页数
        Long totalPage = totalCounts % rows == 0 ? totalCounts / rows : totalCounts / rows + 1;

        result.put("page", page);
        result.put("rows", byPage);
        result.put("total", totalPage);
        result.put("records", totalCounts);

        return result;
    }

    // 批量录入ES索引库
    @RequestMapping("saveAll")
    public Map<String, Object> saveAll(){
        log.info("正在执行索引的创建....");
        Map<String, Object> map = new HashMap<>();
        try {
            poemService.saveAll();
            map.put("success", true);
            map.put("msg", "索引录入成功");
        }catch (Exception e){
            e.printStackTrace();
            map.put("success", false);
            map.put("msg","索引录入失败" + e.getMessage());
        }
        return map;
    }

    // 清空所有文档
    @RequestMapping("deleteAll")
    public Map<String, Object> deleteAll(){
        log.info("删除所有的索引.....");
        Map<String, Object> map = new HashMap<>();
        try {
            poemService.deleteAll();
            map.put("success", true);
            map.put("msg", "文档已经全部删除");
        }catch (Exception e){
            e.printStackTrace();
            map.put("success", false);
            map.put("mag", "文档删除失败");
        }
        return map;
    }

    // 前台搜索
    // 参数1：搜索的条件 2. 搜索类型   3. 根据作者搜索
    @RequestMapping(value = "findAllKeywords", method = RequestMethod.GET)
    public Map<String, Object> findAll(String content, String type, String author){
        Map<String, Object> map = new HashMap<>();

        // 放入redis
        if (!StringUtils.isEmpty(content)) {
            List<String> segment = SegmentHelper.segment(content, SegmentResultHandlers.word());

            log.info("当前搜索分词的结果是:[{}]", segment);
            segment.forEach(word -> {
                if (word.length() > 1){
                    stringRedisTemplate.opsForZSet().incrementScore("keywords", word, 0.5);
                }
            });
        }
        if (type == null || type.equals("所有")) type = null;
        if (author == null || author.equals("所有")) author = null;

        log.info("type: [{}]   author:[{}]", type, author);
        try {
            List<Poem> poems = poemService.findByKeywords(content, type, author);
            map.put("success", true);
            map.put("msg", "查询成功");
            map.put("poems", poems);
        }catch (Exception e){
            e.printStackTrace();
            map.put("success",false);
            map.put("msg", "查询失败");
        }
        return map;
    }

    // 获取redis热词排行榜
    @RequestMapping("findRedisKeywords")
    public Set<ZSetOperations.TypedTuple<String>> findRedisKeywords(){
        Set<ZSetOperations.TypedTuple<String>> keywords = stringRedisTemplate.opsForZSet().reverseRangeWithScores("keywords", 0, 20);
        return keywords;
    }
}
