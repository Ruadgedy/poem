package com.example.poem.controller;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * @author :  yuhao
 * @date: 2020/10/19
 * @description:
 */
@RestController
@RequestMapping("dic")
public class DicController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 记载远程词典
    @RequestMapping(value = "remote", produces = "text/plain")
    public String remote(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println(request.getContextPath()); //  /poem
        System.out.println(request.getServletPath()); //  /dic/remote
        System.out.println(request.getServletContext().getRealPath("/"));
        String realPath = request.getServletContext().getRealPath("/");
        File file = new File(realPath, "init.dic");
        String readFileToString = FileUtils.readFileToString(file);
        response.setDateHeader("Last-Modified", System.currentTimeMillis());
        response.setHeader("ETag", DigestUtils.md5DigestAsHex(readFileToString.getBytes()));
        return readFileToString;
    }

    // 获取redis热词排行榜
    @RequestMapping("/findRedisKeywords")
    public Set<ZSetOperations.TypedTuple<String>> findRedisKeywords() {
        Set<ZSetOperations.TypedTuple<String>> keywords = stringRedisTemplate.opsForZSet().reverseRangeWithScores("keywords", 0, 10);
        return keywords;
    }

    // 添加到远程词典
    @RequestMapping("save")
    public Map<String, Object> saveDic(String keyword, HttpServletRequest request) throws IOException {
        Map<String, Object> result = new HashMap<>();

        try {
            String trimAllWhitespace = StringUtils.trimAllWhitespace(keyword);

            // 获取项目地址
            String realPath = request.getServletContext().getRealPath("/");
            File file = new File(realPath, "init.dic");
            String readFileToString = FileUtils.readFileToString(file);
            // 判断热词词典是否包含该热词，不包含则加入
            if (!readFileToString.contains(trimAllWhitespace)) {
                FileUtils.write(file, trimAllWhitespace, "UTF-8", true);
                result.put("success", true);
            } else {
                throw new RuntimeException("该热词已经存在，无需重复添加！");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    // 删除词典中的热词
    @RequestMapping("delete")
    public Map<String, Object> delete(String keyword, HttpServletRequest request) throws IOException {
        Map<String, Object> result = new HashMap<>();

        try {
            String realPath = request.getServletContext().getRealPath("/");
            File file = new File(realPath, "init.dic");

            // 因为字典里头保存的是中文字符，所以选择字符流读取
            FileReader fileReader = new FileReader(file);
            // 因为流式读取效率不高，这里采用缓冲区读取方式
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder sb = new StringBuilder();
            while (true) {
                String s = bufferedReader.readLine();
                if (s == null) break;
                if (s != keyword) {
                    sb.append(s).append("\r\n"); // 回车 + 换行
                }
            }
            // 将StringBuilder中的内容重新写入文件
            FileUtils.write(file, sb.toString(), "UTF-8", false);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    // 获取远程词典列表
    @RequestMapping("findAll")
    public List<String> findAllDics(HttpServletRequest request) throws IOException {
        String realPath = request.getServletContext().getRealPath("/");
        File file = new File(realPath, "init.dic");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        List<String> res = new ArrayList<>();

        while (true){
            String line = bufferedReader.readLine();
            if (line == null) break;
            res.add(line);
        }
        return res;
    }
}
