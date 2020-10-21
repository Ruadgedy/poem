package com.example.poem.dao;

import com.example.poem.entity.Poem;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.util.Loggers;

import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author :  yuhao
 * @date: 2020/10/19
 * @description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
class PoemDAOTest {
    Logger logger;

    @BeforeEach
    public void getLogger(){
        logger = Logger.getLogger("poemDao");
    }

    @Autowired
    PoemDAO poemDAO;

    @Test
    void findAll() {
        logger.info(String.format("total count:%d",poemDAO.findTotalCounts()));
        List<Poem> all = poemDAO.findAll();
        System.out.println(all);
    }

    @Test
    void findByPage() {
    }

    @Test
    void findTotalCounts() {
        System.out.println(poemDAO.findTotalCounts());
    }
}
