package ga;

import com.google.common.collect.Lists;
import service.entity.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

class ChromosomeTest {

    @Test
    void getListFromGenes(){
        Order o1 = new Order();o1.setOrderNo(1);
        Order o2 = new Order();o2.setOrderNo(2);
        Order o3 = new Order();o3.setOrderNo(3);
        Order o4 = new Order();o4.setOrderNo(4);
        Order o5 = new Order();o5.setOrderNo(5);

        int[] genes = {2,1,3,4,0};
        Chromosome chromo = Chromosome.builder().genes(genes).build();
        List<Order> listFromGenes = chromo.getListFromGenes(Lists.newArrayList(o1, o2, o3, o4, o5));
        System.out.println(listFromGenes);
    }

}
