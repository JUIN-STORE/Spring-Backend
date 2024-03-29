package store.juin.api.common.handler;

import org.apache.logging.log4j.util.Supplier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class QueryTransactional {

    public void execute(Runnable runnable) {
        runnable.run();
    }

    public <T> T execute(Supplier<T> supplier) {
        return supplier.get();
    }
}