package service;

import ch.qos.logback.classic.filter.ThresholdFilter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ReceiveOrderServiceTest {

    private final DecimalFormat df = new DecimalFormat("####.00");

    @Test
    void doubleRound(){
        double a = 1.50d;
        System.out.println(Math.round(a));
    }

    @Test
    void getTNextOrder(){
        int n = 1000;
        int[] nums = new int[300];
        double receivingRate = 0.025d;
        for (int i = 0; i < n; i++) {
            double t = ((-1 / receivingRate) * Math.log(1-Math.random()));
            if (t < 300){
                nums[(int)Math.floor(t)]++;
            }

            //System.out.printf("No.%s, t = %s%n",i,t);
            //System.out.printf("%s,",df.format(t));
        }
        for (int i = 0; i < nums.length; i++) {
            System.out.printf("%s到%s秒内到达：%s个订单%n",i,i+1,nums[i]);
        }
        //print(nums);
        //System.out.println(Arrays.toString(nums));
    }


    public static void main(String[] args) {
        int[] nums = randomSkuNum();
        print(nums);
    }

    private static int[] randomSkuNum(){
        int[] frequency = new int[10];

        Random random = new Random();

        double sigma = 0.3152d;
        long count = 0;
        for (int i = 0; i < 100000; i++) {
            count++;
            double num = sigma * random.nextGaussian() + 1;
            while (num<1 || num>10.5) {
                count++;
                num = sigma * random.nextGaussian() + 1;
            }
            int n = (int) Math.round(num);
            frequency[n-1]++;
        }
        System.out.println(count);
        System.out.println(Arrays.toString(frequency));
        return frequency;
    }

    private static void print(int[] nums){
        XYSeries series = new XYSeries("xySeries");
        for (int x = 0; x < 10; x++) {
            series.add(x+1, nums[x]);
        }
        print(series);
    }

    private static void print(double[] nums){
        XYSeries series = new XYSeries("xySeries");
        for (int x = 0; x < 10; x++) {
            series.add(x+1, nums[x]);
        }
        print(series);
    }

    private static void print(XYSeries series) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "y = freq(x)", // chart title
                "x", // x axis label
                "freq(x)", // y axis label
                dataset, // data
                PlotOrientation.VERTICAL,
                false, // include legend
                false, // tooltips
                false // urls
        );

        ChartFrame frame = new ChartFrame("my picture", chart);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
