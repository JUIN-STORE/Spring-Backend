package store.juin.api.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import store.juin.api.handler.CommandTransactional;

/**
 * @Around("execution([접근제어자] 반환타입 패키지.패키지.패키지.패키지.클래스.메소드(인자))")
 */
@Slf4j
@Aspect
@Component
public class TransactionalAspect {
    private final String packageName = CommandTransactional.class.getPackageName();

    /**
     * 트랜잭션은 store.juin.api.handler 패키지에 있는 XXXTransactional만 사용해야 한다.
     *
     * @Transactional 어노테이션을 통해서 트랜잭션이 처리되는 케이스는 없어야 한다.
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("execution(public * store.juin.api.service.*.*.*(..))")
    public Object checkTransactional(ProceedingJoinPoint joinPoint) throws Throwable {
        final String targetMethod = joinPoint.getSignature().toShortString();
        final String message = targetMethod + "까지 @Transactional이 전파되고 있습니다. 찾아서 삭제해 주세요!!";

        if (isAbnormalTransactional()) {
            log.error(message);
            throw new IllegalTransactionStateException(message);
        } else {
            return joinPoint.proceed(); // 해당 메서드 실행
        }
    }

    private boolean isAbnormalTransactional() {
        final String currentTransactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        if (currentTransactionName == null) {
            return false;
        }

        return TransactionSynchronizationManager.isActualTransactionActive()
                && !currentTransactionName.contains(packageName);
    }
}



