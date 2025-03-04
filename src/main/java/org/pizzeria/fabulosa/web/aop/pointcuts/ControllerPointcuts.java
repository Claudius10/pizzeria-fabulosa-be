package org.pizzeria.fabulosa.web.aop.pointcuts;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class ControllerPointcuts {

	@Pointcut("within(org.pizzeria.fabulosa.web.controllers.user.UserController)")
	public void isUserController() {
	}

	@Pointcut("within(org.pizzeria.fabulosa.web.controllers.user.UserOrdersController)")
	public void isUserOrdersController() {
	}

	/**
	 * A join point in the anon controller if the method is defined
	 * in the org.pizzeria.api.controllers.open package or any sub-package
	 * under that.
	 */
	@Pointcut("within(org.pizzeria.fabulosa.web.controllers.open.AnonController)")
	public void isAnonController() {
	}
}
