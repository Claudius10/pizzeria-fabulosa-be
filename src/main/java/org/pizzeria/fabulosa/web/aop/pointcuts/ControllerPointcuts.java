package org.pizzeria.fabulosa.web.aop.pointcuts;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class ControllerPointcuts {
	/**
	 * A join point in the user controller if the method is defined
	 * in the org.pizzeria.api.controllers.locked.user package or any sub-package
	 * under that.
	 */
	@Pointcut("within(org.pizzeria.fabulosa.web.controllers.user)")
	public void isUserController() {
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
