package service;

import com.alibaba.fastjson.JSON;
import entity.Area;
import entity.Order;
import entity.OrderDetail;
import entity.Position;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Test
    void getBestRoute(){
        List<OrderDetail> list = new ArrayList<>(4);
        list.add(OrderDetail.builder().position(Position.builder().tunnel(0).shelf(8).build()).build());
        list.add(OrderDetail.builder().position(Position.builder().tunnel(1).shelf(8).build()).build());
        list.add(OrderDetail.builder().position(Position.builder().tunnel(1).shelf(16).build()).build());
        list.add(OrderDetail.builder().position(Position.builder().tunnel(2).shelf(6).build()).build());
        Area area = new Area();
        area.setFarthestShelf(3);
        area.setMaxTunnelNo(2);
        Set<Integer> set = new HashSet<>(3);
        set.add(0);set.add(1);set.add(2);
        area.setPickedTunnelSet(set);
        area.setDetailList(list);
        CalFitnessService cs = new CalFitnessService();
        double s = cs.calDistanceByS(area);
        double sp = cs.calDistanceBySPlus(area);
        System.out.printf("s = %s; sp = %s",s,sp);

    }
}
