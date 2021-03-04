package common;

import ch.qos.logback.core.rolling.TriggeringPolicyBase;

import java.io.File;

/**
 * 日志滚动策略
 * @param <E>
 */
public class EverytimeTriggeringPolicy<E> extends TriggeringPolicyBase<E> {
    private boolean start = true;    //静态全局标识，初始为true

    @Override
    public boolean isTriggeringEvent(File file, E whatever) {

        if (!start){
            return false;
        }
        start = false;
        return file.length() != 0;
    }
}
