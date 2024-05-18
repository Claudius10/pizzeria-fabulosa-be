package PizzaApp.api.aop.pointcuts;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class UserPointCuts {

	/**
	 * A userAccountUpdate is the execution of any method defined on
	 * the UserService interface that begins with "update...".
	 */
	@Pointcut("execution(* PizzaApp.api.services.user.UserService.update*(..))")
	public void userAccountUpdate() {
	}

	/**
	 * A userAccountDelete is the execution of any method defined on
	 * the UserService interface that begins with "delete...".
	 */
	@Pointcut("execution(* PizzaApp.api.services.user.UserService.delete*(..))")
	public void userAccountDelete() {
	}

	/**
	 * A requieresUserIdValidation is any join point where the executing
	 * method has an @ValidateUserId annotation.
	 */
	@Pointcut("@annotation(PizzaApp.api.aop.annotations.ValidateUserId)")
	public void requieresUserIdValidation() {
	}
}