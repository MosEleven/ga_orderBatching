package service.factory;

import service.CalFitnessService;
import service.entity.Area;

import java.util.function.Function;

public class CalServiceFactory {

    private Function<Area, Double> calDistance;

    private CalServiceFactory() {}

    public static CalFitnessService buildDefault(){
        return new CalServiceFactory().setCalDistanceMethod(CalDistanceMethod.Best).build();
    }

    public static CalServiceFactory builder(){
        return new CalServiceFactory();
    }

    public enum CalDistanceMethod{
        S,S_Plus,Best
    }
    public CalServiceFactory setCalDistanceMethod(CalDistanceMethod choose){
        switch (choose){
            case S:
                calDistance = CalFitnessService::calDistanceByS;
                break;
            case S_Plus:
                calDistance = CalFitnessService::calDistanceBySPlus;
                break;
            case Best:
                calDistance = CalFitnessService::getBestRoute;
                break;
            default:
                throw new IllegalArgumentException("no such method");
        }
        return this;
    }

    public CalServiceFactory setCalDistanceMethod(Function<Area, Double> custom){
        calDistance = custom;
        return this;
    }

    public CalFitnessService build(){
        return new CalFitnessService() {
            @Override
            public double calDistance(Area area) {
                return calDistance.apply(area);
            }
        };
    }
}
