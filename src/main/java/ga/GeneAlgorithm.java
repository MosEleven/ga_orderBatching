package ga;


import common.CU;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GeneAlgorithm<T> {

    private Logger log = LoggerFactory.getLogger("Cal");

    //迭代次数
    private final int iterationNum;

    //订单数据
    private List<T> dataList;

    private int genesLength;

    //种群
    private List<Chromosome> population;

    //种群大小（即染色体数量）
    private final int populationSize;

    private final double varyRatio = 0.3d;

    private final double parentRatio = 0.3d;

    //基因排序{1,2...genesLength-1}
    private int[] geneSequence;

    //染色体排序{1,2...populationSize-1}
    private final int[] chromoSequence;

    //选择表{0,1,1,2,2,2...}
    private final int[] selectTable;

    private final GaCalculate<T> gaCalculate;

    private final ExecutorService pool = Executors.newFixedThreadPool(4);

    public GeneAlgorithm(int iterationNum, GaCalculate<T> gaCalculate) {
        this(iterationNum,100,gaCalculate);
    }

    public GeneAlgorithm(int iterationNum, int populationSize, GaCalculate<T> gaCalculate) {
        this.iterationNum = iterationNum;
        this.populationSize = populationSize;
        this.gaCalculate = gaCalculate;

        //初始化染色体顺序
        chromoSequence = new int[populationSize];
        for (int i = 0; i < populationSize; i++) {
            chromoSequence[i] = i;
        }
        //初始化rank轮盘赌选择表
        int tableSize = populationSize*(populationSize+1)/2;
        selectTable = new int[tableSize];
        for (int i = 0, p = 0; i < populationSize; i++) {
            for (int j = 0; j < i + 1; j++) {
                selectTable[p++] = i;
            }
        }

        System.out.printf("遗传算法构建，迭代次数为%s%n",iterationNum);
    }

    public void loadData(List<T> dataList){
        System.out.println("遗传算法加载数据...");
        this.dataList = dataList;
        //订单总量
        this.genesLength = dataList.size();
        //初始化基因顺序
        this.geneSequence = new int[genesLength];
        for (int i = 0; i < genesLength; i++) {
            geneSequence[i] = i;
        }
    }

    //遗传算法入口
    public List<T> ga(){
        System.out.printf("遗传算法开始执行...订单数量：%s%n",dataList.size());
        if (gaCalculate.checkData(dataList)){
            init();
            fitnessAndSort();
            for (int i = 0; i < iterationNum; i++) {
                generating();
            }
            //deBugPrint();
            return population.get(populationSize-1).getListFromGenes(dataList);
        }
        System.out.println("没有进入遗传系统");
        return dataList;
    }

    private void deBugPrint(){
        Chromosome bestC = population.get(populationSize - 1);
        List<T> list = bestC.getListFromGenes(dataList);
        //System.out.printf("GA：best score is %s; ",bestC.getScore());

        log.info("GA: ");
        //System.out.print("GA：");
        gaCalculate.debug(list);
        log.info("FIFO: ");
        //System.out.print("FIFO：");
        gaCalculate.debug(dataList);
    }

    //初始化族群
    private void init(){
        //打乱订单顺序，生成初始族群
        this.population = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; i++) {
            population.add(Chromosome.builder().genes(CU.shuffleIntArray(geneSequence)).build());
        }
    }

    //适应度计算函数
    private void fitness(List<Chromosome> chromosomes){
        List<Future<Double>> scores = new ArrayList<>(chromosomes.size());
        for (Chromosome chromosome : chromosomes) {
            scores.add(pool.submit(() -> gaCalculate.calFitness(chromosome.getListFromGenes(dataList))));
            //chromosome.setScore(gaCalculate.calFitness(chromosome.getListFromGenes(dataList)));
        }
        for (int i = 0; i < scores.size(); i++) {
            try {
                chromosomes.get(i).setScore(scores.get(i).get());
            } catch (InterruptedException | ExecutionException e) {
                System.out.println(" thread pool meet some problem");
            }
        }
    }

    private void fitnessAndSort(){
        fitness(population);
        population.sort(CU::chromoComparator);
    }

    //生成下一代
    private void generating(){

        //得到交叉变异结果
        List<Chromosome> selected = rankRoulette();
        List<Chromosome> crossResult = crossover(selected);
        List<Chromosome> varyResult = variation();
        //计算新的适应度
        fitness(crossResult);
        fitness(varyResult);

        List<Chromosome> offspring = new ArrayList<>(crossResult.size()+varyResult.size());
        offspring.addAll(crossResult);
        offspring.addAll(varyResult);
        offspring.sort(CU::chromoComparator);

        int parentRemain = (int) (populationSize * parentRatio);
        int offspringRemain = populationSize - parentRemain;
        //取分数最高的
        population = population.subList(population.size()-parentRemain,population.size());
        population.addAll(offspring.subList(offspring.size()-offspringRemain,offspring.size()));

        population.sort(CU::chromoComparator);

    }

    //交叉
    private List<Chromosome> crossover(List<Chromosome> selected){
        List<Chromosome> crossResult = new ArrayList<>(populationSize);
        int[] pairs = CU.shuffleIntArray(chromoSequence);
        for (int i = 0; i < populationSize; i += 2) {
            Chromosome c1 = selected.get(pairs[i]);
            Chromosome c2 = selected.get(pairs[i+1]);
            mapCross(crossResult,c1,c2);
        }
        return crossResult;

    }

    //映射交叉
    private void mapCross(List<Chromosome> crossResult, Chromosome c1, Chromosome c2){

        //闭区间
        int[] twoPoints = getTwoPoints(genesLength);

        int[] offspring1 = new int[genesLength];
        int[] offspring2 = new int[genesLength];
        int[] parent1 = c1.getGenes();
        int[] parent2 = c2.getGenes();

        int[] exchangeTable = geneSequence.clone();
        for (int i = twoPoints[0]; i < twoPoints[1]; i++) {
            int a = exchangeTable[parent1[i]];
            int b = exchangeTable[parent2[i]];
            exchangeTable[a] = b;
            exchangeTable[b] = a;
        }

        for (int i = 0; i < twoPoints[0]; i++) {
            offspring1[i] = exchangeTable[parent1[i]];
            offspring2[i] = exchangeTable[parent2[i]];
        }
        for (int i = twoPoints[0]; i <= twoPoints[1]; i++) {
            offspring1[i] = parent2[i];
            offspring2[i] = parent1[i];
        }
        for (int i = twoPoints[1]+1; i < genesLength; i++) {
            offspring1[i] = exchangeTable[parent1[i]];
            offspring2[i] = exchangeTable[parent2[i]];
        }

        crossResult.add(Chromosome.builder().genes(offspring1).build());
        crossResult.add(Chromosome.builder().genes(offspring2).build());
    }

    //获取随机的两个点，且不能是一整段
    private int[] getTwoPoints(int n){
        int[] twoPoints = CU.randomTwoPoints(n);
        while (twoPoints[0]==0 && twoPoints[1]==n){
            twoPoints = CU.randomTwoPoints(n);
        }
        return twoPoints;
    }

    //变异
    private List<Chromosome> variation(){
        int varyNum = (int) (populationSize * varyRatio);
        List<Chromosome> varyResult = new ArrayList<>(varyNum);

        int[] select = CU.randomSelect(chromoSequence, varyNum);
        for (int n : select) {
            Chromosome chromo = population.get(n).cloneGenes();
            int[] genes = chromo.getGenes();
            int[] twoPoints = CU.randomTwoPoints(genesLength);
            CU.swapIntArray(genes,twoPoints[0],twoPoints[1]);
            varyResult.add(chromo);
        }
        return varyResult;
    }

    //根据排名轮盘赌
    private List<Chromosome> rankRoulette(){
        List<Chromosome> selected = new ArrayList<>(populationSize);
        int total = populationSize * (populationSize + 1) / 2;
        for (int i = 0; i < populationSize; i++) {
            int n = selectTable[CU.random.nextInt(total)];
            selected.add(population.get(n));
        }
        return selected;
    }

    public void shutdown(){
        pool.shutdownNow();
    }

}
