package service;

import com.alibaba.fastjson.JSON;
import entity.Order;
import entity.Position;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalFitnessServiceTest {

    @Test
    void initWarehouse(){
        Position[] positions = new Position[240];
        for (int i = 0; i < 4; i++) {
            char area = (char) ('A'+i);
            for (int tunnel = 0; tunnel < 3; tunnel++) {
                for (int shelf = 0; shelf < 20; shelf++) {
                    positions[i*60+tunnel*20+shelf] = Position.builder().area(area).tunnel(tunnel).shelf(shelf+1).build();
                }
            }
        }
        System.out.println(JSON.toJSON(positions));
        assertEquals(240,positions.length);
    }

    @Test
    void doubleTest(){
        double a = 0d;
        double b = a;
        a += 1.0d;
        System.out.println(a);
        System.out.println(b);
    }


    static List<Order> createOrderList(){
        List<Order> list = new ArrayList<>(10);

        Order o1 = new Order();


        return new ArrayList<>();
    }

    @Test
    void doubleAdd(){
        double a = 1.d;
        double b = 2.d;
        a += b + 3d;
        System.out.println(a);
    }

    @Test
    void doubleOverflow(){
        double maxValue = Double.MAX_VALUE;
        double pow = Math.pow(maxValue, 2);
        System.out.println(maxValue);
        System.out.println(pow);
        System.out.println(pow>=maxValue);
        BigDecimal a = BigDecimal.valueOf(maxValue);
        BigDecimal b = BigDecimal.valueOf(2);
        BigDecimal c = a.pow(2);
        System.out.println(c);
    }
}