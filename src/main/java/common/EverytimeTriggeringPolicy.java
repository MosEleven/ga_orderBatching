package common;

import ch.qos.logback.core.rolling.TriggeringPolicyBase;

import java.io.File;

/**
 * 日志滚动策略
 * @param <E>
 */
public class EverytimeTriggeringPolicy<E> extends TriggeringPolicyBase<E> {
    private boolean triggeringFlag = true;    //静态全局标识，初始为true

    @Override
    public boolean isTriggeringEvent(File file, E whatever) {
        //如果标识不为true，则不触发文件滚动
        if (!triggeringFlag)
            return false;
        //如果标识为true，则将其改为false并触发文件滚动
        triggeringFlag = false;
        return true;
    }
}
