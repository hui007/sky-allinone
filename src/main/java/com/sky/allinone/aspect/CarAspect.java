package com.sky.allinone.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.DeclareParents;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.dao.DataAccessException;

/**
 * 有几个点需要注意：可以拦截抛出的特定异常、可以校验输入参数、通过注解引入新功能（动态添加新接口功能，针对jar包里的类很有效）、
 * 		以及切面间执行的顺序问题（order注解）
 * @author joshui
 *
 */
@Aspect
public class CarAspect {
	@Pointcut("execution()")
	public void run() {
	}
	
	@Before("com.xyz.myapp.SystemArchitecture.dataAccessOperation()")
    public void doAccessCheck() {
        // ...
    }
	
	@Before("com.xyz.myapp.SystemArchitecture.dataAccessOperation() && args(car,..)")
	public void validateAccount(Car car) {
	    // ...
	}
	
	// 还有很多校验参数的方式，具体参见spring文档
	@Pointcut("com.xyz.myapp.SystemArchitecture.dataAccessOperation() && args(car,..)")
	private void accountDataAccessOperation(Car account) {}
	@Before("accountDataAccessOperation(car)")
	public void validateAccount2(Car car) {
	    // ...
	}
	
	// 动态引入新接口
	@DeclareParents(value="com.xzy.myapp.service.*+", defaultImpl=DefaultUsageTracked.class)
    public static UsageTracked mixin;
    @Before("com.xyz.myapp.SystemArchitecture.businessService() && this(usageTracked)")
    public void recordUsage(UsageTracked usageTracked) {
        usageTracked.incrementUseCount();
    }
	
	@AfterReturning("com.xyz.myapp.SystemArchitecture.dataAccessOperation()")
    public void AfterReturning() {
        // ...
    }
	
	@AfterReturning(
	        pointcut="com.xyz.myapp.SystemArchitecture.dataAccessOperation()",
	        returning="retVal")
    public void doAccessCheck(Object retVal) {
        // ...
    }
	
	@AfterThrowing("com.xyz.myapp.SystemArchitecture.dataAccessOperation()")
    public void doRecoveryActions() {
        // ...
    }
	
	@AfterThrowing(
	        pointcut="com.xyz.myapp.SystemArchitecture.dataAccessOperation()",
	        throwing="ex")
    public void doRecoveryActions(DataAccessException ex) {
        // ...
    }
	
	@After("com.xyz.myapp.SystemArchitecture.dataAccessOperation()")
    public void doReleaseLock() {
        // ...
    }
	
	@Around("com.xyz.myapp.SystemArchitecture.businessService()")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
        // start stopwatch
        Object retVal = pjp.proceed();
        // stop stopwatch
        return retVal;
    }

	/**
	 * A join point is in the web layer if the method is defined in a type in
	 * the com.xyz.someapp.web package or any sub-package under that.
	 */
	@Pointcut("within(com.xyz.someapp.web..*)")
	public void inWebLayer() {
	}

	/**
	 * A join point is in the service layer if the method is defined in a type
	 * in the com.xyz.someapp.service package or any sub-package under that.
	 */
	@Pointcut("within(com.xyz.someapp.service..*)")
	public void inServiceLayer() {
	}

	/**
	 * A join point is in the data access layer if the method is defined in a
	 * type in the com.xyz.someapp.dao package or any sub-package under that.
	 */
	@Pointcut("within(com.xyz.someapp.dao..*)")
	public void inDataAccessLayer() {
	}

	/**
	 * A business service is the execution of any method defined on a service
	 * interface. This definition assumes that interfaces are placed in the
	 * "service" package, and that implementation types are in sub-packages.
	 *
	 * If you group service interfaces by functional area (for example, in
	 * packages com.xyz.someapp.abc.service and com.xyz.someapp.def.service)
	 * then the pointcut expression "execution(*
	 * com.xyz.someapp..service.*.*(..))" could be used instead.
	 *
	 * Alternatively, you can write the expression using the 'bean' PCD, like so
	 * "bean(*Service)". (This assumes that you have named your Spring service
	 * beans in a consistent fashion.)
	 */
	@Pointcut("execution(* com.xyz.someapp..service.*.*(..))")
	public void businessService() {
	}

	/**
	 * A data access operation is the execution of any method defined on a dao
	 * interface. This definition assumes that interfaces are placed in the
	 * "dao" package, and that implementation types are in sub-packages.
	 */
	@Pointcut("execution(* com.xyz.someapp.dao.*.*(..))")
	public void dataAccessOperation() {
	}
	
	/**
	 * execution(public * *(..)) the execution of any public method
	 * execution(* set*(..)) the execution of any method with a name beginning with "set"
	 * execution(* com.xyz.service.AccountService.*(..)) the execution of any method defined by the AccountService interface
	 * execution(* com.xyz.service.*.*(..)) the execution of any method defined in the service package
	 * execution(* com.xyz.service..*.*(..)) the execution of any method defined in the service package or a sub-package
	 * within(com.xyz.service.*) any join point (method execution only in Spring AOP) within the service package
	 * within(com.xyz.service..*) any join point (method execution only in Spring AOP) within the service package or a sub-package
	 * this(com.xyz.service.AccountService) any join point (method execution only in Spring AOP) where the proxy implements the AccountService interface
	 * target(com.xyz.service.AccountService) any join point (method execution only in Spring AOP) where the target object implements the AccountService interface
	 * args(java.io.Serializable) any join point (method execution only in Spring AOP) which takes a single parameter, and where the argument passed at runtime is Serializable
	 * @target(org.springframework.transaction.annotation.Transactional) any join point (method execution only in Spring AOP) where the target object has an @Transactional annotation
	 * @within(org.springframework.transaction.annotation.Transactional) any join point (method execution only in Spring AOP) where the declared type of the target object has an @Transactional annotation
	 * @annotation(org.springframework.transaction.annotation.Transactional) any join point (method execution only in Spring AOP) where the executing method has an @Transactional annotation
	 * @args(com.xyz.security.Classified) any join point (method execution only in Spring AOP) which takes a single parameter, and where the runtime type of the argument passed has the @Classified annotation
	 * bean(tradeService) any join point (method execution only in Spring AOP) on a Spring bean named tradeService:
	 * bean(*Service) any join point (method execution only in Spring AOP) on Spring beans having names that match the wildcard expression *Service
	 */
}
