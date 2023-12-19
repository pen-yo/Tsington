package com.penyo.tsington.v0;

import com.penyo.tsington.config.PerformanceConfig;
import com.penyo.tsington.config.UserConfig;
import com.penyo.tsington.util.DriverProxy;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * <h1>青彤™ 连接池</h1>
 *
 * <p>
 * 连接池是一种对 {@link java.sql.Connection Connection} 池化技术的体现。其生命周期分为<b>初始期、
 * 伺服期和终结期</b>三个阶段。实例化池时，其立刻进入初始期，期间池拒绝响应；当驱动和连接集合就绪后，
 * 进入伺服期，此时池可以向外提供有关 {@link com.penyo.tsington.v0.ConnectionShell ConnectionShell}
 * 的服务；手动调用池的 {@code shutdown()} 方法后，池进入终结期，其内的连接会被全部销毁，即不再可用。
 * </p>
 *
 * <p>
 * 一般情况下，一个项目只需要一个连接池。因此建议您按照<b>单例模式</b>设计一个代理类，用于存放静态化的池实例，如：
 * </p>
 *
 * <pre>
 * public class TsingtonSingleton {
 *   public static final TsingtonDataSource POOL;
 * }
 * </pre>
 *
 * <p>
 * 初始状态下，连接池会有 8 个最低连接数。当池已见底且对连接的请求比较频繁时，就会触发池的扩张机制；
 * 相反地，如果池长时间有大量空闲连接（远远大于最低连接数），就会触发收缩机制以节省硬件资源。
 * 具体的参数可在 {@link com.penyo.tsington.config.PerformanceConfig TsingtonInside}
 * 中被指定，并传入池。
 * </p>
 *
 * @author Penyo
 */
public abstract class TsingtonDataSource implements TsingtonDataSourceSpecification, Closeable {
  /**
   * 用户配置
   */
  private UserConfig uc;
  /**
   * 性能配置
   */
  private PerformanceConfig pc;

  @Override
  public UserConfig getUserConfig() {
    return uc;
  }

  @Override
  public PerformanceConfig getPerformanceConfig() {
    return pc;
  }

  /**
   * 压力监视器
   */
  private PressureMonitor pm;

  protected TsingtonDataSource() {
  }

  /**
   * 连接池生命周期状态
   */
  private boolean isAlive = false;

  /**
   * 激活连接池。
   */
  protected void activate(UserConfig uc, PerformanceConfig pc) {
    if (uc == null || pc == null || isAlive) throw new RuntimeException();

    DriverProxy.register(uc.driver());
    this.uc = uc;
    this.pc = pc;
    isAlive = true;
    expand(pc.getMinConnectionsNum());
    pm = new PressureMonitor(this);
  }

  /**
   * 空闲队列
   */
  private final Queue<ConnectionShell> remainings = new ConcurrentLinkedQueue<>();
  /**
   * 忙碌队列
   */
  private final Queue<ConnectionShell> workings = new ConcurrentLinkedQueue<>();

  @Override
  public int getRemainingCapacity() {
    return remainings.size();
  }

  @Override
  public int getCapacity() {
    return remainings.size() + workings.size();
  }

  /**
   * 扩张连接池。
   */
  protected void expand(int amount) {
    if (!isAlive) throw new RuntimeException();

    if (getRemainingCapacity() + amount <= pc.getMaxConnectionsNum()) for (int i = 0; i < amount; i++)
      try {
        Connection c = DriverProxy.getConnection(uc);
        if (c == null) throw new RuntimeException("Cannot login!");
        remainings.add(new ConnectionShell(c));
      } catch (Exception ignored) {
      }
  }

  /**
   * 收缩连接池。
   */
  protected void contract(int amount) {
    if (!isAlive) throw new RuntimeException();

    if (getRemainingCapacity() - amount >= pc.getMinConnectionsNum()) for (int i = 0; i < amount; i++) {
      try {
        ConnectionShell cs = remainings.poll();
        if (cs != null) cs.getUsufruct().close();
      } catch (SQLException ignored) {
      }
    }
  }

  @Override
  public synchronized ConnectionShell lendShell() {
    if (!isAlive) throw new RuntimeException();

    pm.request();

    ConnectionShell cs = null;

    long requestTime = System.currentTimeMillis();
    while (System.currentTimeMillis() - requestTime < pc.getRequestTimeout()) {
      if (remainings.isEmpty()) try {
        wait(100);
      } catch (InterruptedException ignored) {
      }
      else {
        cs = remainings.poll();
        cs.enable();
        workings.offer(cs);
        break;
      }
    }

    return cs;
  }

  @Override
  public synchronized void returnShell(ConnectionShell cs) {
    if (!isAlive) throw new RuntimeException();

    if (workings.contains(cs)) {
      workings.remove(cs);
      cs.disable();
      remainings.offer(cs);
    }
  }

  @Override
  public void close() {
    if (!isAlive) throw new RuntimeException();

    for (ConnectionShell cs : workings)
      try {
        returnShell(cs);
        cs.getUsufruct().close();
      } catch (Exception ignored) {
      }
    for (ConnectionShell cs : remainings)
      try {
        cs.getUsufruct().close();
      } catch (Exception ignored) {
      }

    pm.close();
    isAlive = false;
  }
}
