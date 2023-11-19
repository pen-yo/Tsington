package com.penyo.tsington.v0;

import com.penyo.tsington.cfg.PerformanceConfig;
import com.penyo.tsington.cfg.UserConfig;
import com.penyo.tsington.util.DriverProxy;
import com.penyo.tsington.util.SQLDBProduct;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
 * public class TsingtonProxy {
 *   public static final TsingtonDataSource pool = new TsingtonDataSource(null, null);
 * }
 * </pre>
 *
 * <p>
 * 初始状态下，连接池会有 8 个最低连接数。当池已见底且对连接的请求比较频繁时，就会触发池的扩张机制；
 * 相反地，如果池长时间有大量空闲连接（远远大于最低连接数），就会触发收缩机制以节省硬件资源。
 * 具体的参数可在 {@link com.penyo.tsington.cfg.PerformanceConfig PerformanceConfig}
 * 中被指定，并传入池。
 * </p>
 *
 * @author Penyo
 */
public class TsingtonDataSource {
  /**
   * 日志发生器
   */
  private static final Logger logger = LogManager.getLogger(TsingtonDataSource.class);

  /**
   * 用户配置
   */
  private final UserConfig uc;
  /**
   * 性能配置
   */
  private final PerformanceConfig pc;

  /**
   * 压力监视器
   */
  private final PressureMonitor pm;

  public TsingtonDataSource(SQLDBProduct prod, UserConfig uc) {
    DriverProxy.register(prod);
    this.uc = uc;
    pc = new PerformanceConfig();
    expand(pc.getMinConnectionsNum());
    pm = new PressureMonitor(this);
  }

  public TsingtonDataSource(SQLDBProduct prod, UserConfig uc, PerformanceConfig pc) {
    DriverProxy.register(prod);
    this.uc = uc;
    this.pc = pc;
    expand(pc.getMinConnectionsNum());
    pm = new PressureMonitor(this);
  }

  public UserConfig getUserConfig() {
    return uc;
  }

  public PerformanceConfig getPerformanceConfig() {
    return pc;
  }

  /**
   * 空闲队列
   */
  private final Queue<ConnectionShell> remainings = new ConcurrentLinkedQueue<>();
  /**
   * 忙碌队列
   */
  private final Queue<ConnectionShell> workings = new ConcurrentLinkedQueue<>();

  /**
   * 检查连接池是否繁忙。
   */
  public boolean isBusy() {
    return remainings.isEmpty();
  }

  /**
   * 检查连接池是否悠闲。
   */
  public boolean isLeisurely() {
    return (double) getRemainingCapacity() / getCapacity() >= 0.5;
  }

  /**
   * 获取剩余容量。
   */
  public int getRemainingCapacity() {
    return remainings.size();
  }

  /**
   * 获取总容量。
   */
  public int getCapacity() {
    return remainings.size() + workings.size();
  }

  /**
   * 扩张连接池。
   */
  protected void expand(int amount) {
    if (getRemainingCapacity() + amount <= pc.getMaxConnectionsNum()) for (int i = 0; i < amount; i++)
      try {
        Connection c = DriverProxy.getConnection(uc);
        if (c == null) throw new RuntimeException("Cannot login!");
        remainings.add(new ConnectionShell(c));
      } catch (Exception e) {
        logger.error(e);
      }
  }

  /**
   * 收缩连接池。
   */
  protected void contract(int amount) {
    if (getRemainingCapacity() - amount >= pc.getMinConnectionsNum()) for (int i = 0; i < amount; i++) {
      try {
        ConnectionShell cs = remainings.poll();
        if (cs != null) cs.getUsufruct().close();
      } catch (SQLException e) {
        logger.error(e);
      }
    }
  }

  /**
   * 借用连接壳。
   */
  public synchronized ConnectionShell lendShell() {
    if (!isAlive) throw new RuntimeException();

    pm.request();

    ConnectionShell cs = null;

    long requestTime = System.currentTimeMillis();
    while (System.currentTimeMillis() - requestTime < pc.getRequestTimeout()) {
      if (remainings.isEmpty()) try {
        wait(100);
      } catch (InterruptedException e) {
        logger.error(e);
      }
      else {
        cs = remainings.poll();
        cs.enable();
        workings.offer(cs);
        logger.debug("发生了一次借出，当前可用线程数：" + getRemainingCapacity() + "/" + getCapacity());
        break;
      }
    }

    return cs;
  }

  /**
   * 归还连接壳。
   */
  public synchronized void returnShell(ConnectionShell cs) {
    if (!isAlive) throw new RuntimeException();

    if (workings.contains(cs)) {
      workings.remove(cs);
      cs.disable();
      remainings.offer(cs);
      logger.debug("发生了一次归还，当前可用线程数：" + getRemainingCapacity() + "/" + getCapacity());
    }
  }

  /**
   * 连接池生命周期状态
   */
  private boolean isAlive = true;

  /**
   * 关闭连接池。
   */
  public synchronized void shutdown() {
    if (!isAlive) throw new RuntimeException();

    for (ConnectionShell cs : workings)
      try {
        returnShell(cs);
        cs.getUsufruct().close();
      } catch (Exception e) {
        logger.warn(e);
      }
    for (ConnectionShell cs : remainings)
      try {
        cs.getUsufruct().close();
      } catch (Exception e) {
        logger.warn(e);
      }

    pm.close();
    isAlive = false;
  }
}
