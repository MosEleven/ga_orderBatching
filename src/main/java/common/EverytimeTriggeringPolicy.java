package common;

import ch.qos.logback.core.rolling.TriggeringPolicyBase;

import java.io.File;

/**
 * 日志滚动策略
 * 每次启动都打印到新日志
 * @param <E>
 */
public class EverytimeTriggeringPolicy<E> extends TriggeringPolicyBase<E> {

    //是否是第一次触发
    private boolean start = true;

    @Override
    public boolean isTriggeringEvent(File file, E whatever) {

        if (!start){
            return false;
        }
        start = false;
        //不滚动空日志
        return file.length() != 0;
    }
}
