package net.penyo.tsington.util;

import org.apache.logging.log4j.LogManager;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;

import java.util.Arrays;

/**
 * 全局日志发生器
 *
 * @author Penyo
 */
@Aspect
public class Logger {
  /**
   * 公共日志发生器
   */
  private final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(Logger.class);

  @AfterThrowing(value = "execution(* net.penyo.tsington.*.*(..))", throwing = "exception")
  public void printStackTrace(JoinPoint joinPoint, Throwable exception) {
    LOGGER.error("\n\t- 来自 " + joinPoint.getSignature().getName() + "\n\t- 形式 " + exception.getMessage() + "\n\t- 栈追踪 " + Arrays.toString(exception.getStackTrace()));
  }
}
