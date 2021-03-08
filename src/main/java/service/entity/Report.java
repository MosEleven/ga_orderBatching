package service.entity;

import common.CU;

import java.math.BigDecimal;

public class Report {

    private double pSpareTime = 0d;

    private double nSpareTime = 0d;

    private double pSpareAvgTime;

    private double nSpareAvgTime;

    private int pSpareNum = 0;

    private int nSpareNum = 0;

    public void addPSpareTime(BigDecimal time){
        pSpareTime += time.doubleValue();
        pSpareNum++;
    }
    public void addNSpareTime(BigDecimal time){
        nSpareTime = time.doubleValue();
        nSpareNum++;
    }

    private void calSpareTime(){
        pSpareAvgTime = pSpareTime/pSpareNum;
        nSpareAvgTime = nSpareTime/nSpareNum;
    }

    public String getSpareTimeReport(){
        calSpareTime();
        return String.format(
                "**********************************SPARE-TIME-SUMMARIZE**********************************%n" +
                "空闲时间：%n" +
                "正次数：%s； 正总时间：%s； 正平均：%s%n" +
                "负次数：%s； 负总时间：%s； 负平均：%s%n",
                pSpareNum,pSpareTime,pSpareAvgTime,nSpareNum,nSpareTime,nSpareAvgTime);
    }

    /***************************************************************************************************************/

    private int totalRouteCount = 0;

    private int sMore = 0;

    private int spMore = 0;

    public void addSMore(){
        totalRouteCount++;
        sMore++;
    }
    public void addSPMore(){
        totalRouteCount++;
        spMore++;
    }

    public String getRouteReport(){
        return String.format(
                "**********************************ROUTE-SUMMARIZE**********************************%n" +
                        ""
        );
    }

    /***************************************************************************************************************/

    private double tTotalPick = 0d;

    private double tTotalDelay = 0d;

    private double tTotalDiff = 0d;

    public void addTTotalPick(BigDecimal time){
        tTotalPick += time.doubleValue();
    }
    public void addTTotalDelay(BigDecimal time){
        tTotalDelay += time.doubleValue();
    }
    public void addTTotalDiff(double time){
        tTotalDiff += time;
    }

    public String getPickReport(){
        return String.format(
                "**********************************PICK-SUMMARIZE**********************************%n" +
                        "总拣选时间：%s； 总延迟时间：%s； 总差异时间%s%n",
                CU.df.format(tTotalPick),CU.df.format(tTotalDelay),CU.df.format(tTotalDiff)
        );
    }
}
