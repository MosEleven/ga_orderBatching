package common;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;

public class LoggerNameFilter extends AbstractMatcherFilter<ILoggingEvent> {

    String name;

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }

        if (event.getLoggerName().equals(name)) {
            return onMatch;
        } else {
            return onMismatch;
        }
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void start(){
        if (this.name != null) {
            super.start();
        }
    }
}
