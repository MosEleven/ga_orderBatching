package service;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ReceiveOrderServiceTest {

    @Test
    void doubleRound(){
        double a = 1.50d;
        System.out.println(Math.round(a));
    }


    public static void main(String[] args) {
        int[] nums = randomSkuNum();
        //print(nums);
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