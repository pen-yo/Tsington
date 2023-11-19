package com.penyo.tsington.v0;

import java.sql.Connection;
import java.util.Objects;

/**
 * 连接壳
 *
 * <p>
 * 连接壳是对 {@link java.sql.Connection Connection} 的再封装。一般由 {@link
 * com.penyo.tsington.v0.TsingtonDataSource TsingtonDataSource} 给出而不能直接实例化。
 * 使用时建议总是调用 {@code getUsufruct()} 方法来获取 {@link java.sql.Connection Connection}，
 * 且不使用本地变量来接收。使用结束后，应当将连接壳归还到连接池中。
 * </p>
 *
 * @author Penyo
 */
public class ConnectionShell {
  /**
   * 唯一识别码
   */
  private final int id;
  /**
   * 连接实例
   */
  private final Connection c;
  /**
   * 连接占用状态
   */
  private boolean isAvailable = true;

  protected ConnectionShell(Connection c) {
    id = c.hashCode();
    this.c = c;
  }

  /**
   * 设置连接可用。
   */
  protected void enable() {
    this.isAvailable = true;
  }

  /**
   * 设置连接不可用。
   */
  protected void disable() {
    this.isAvailable = false;
  }

  /**
   * 对连接进行借用。
   */
  public Connection getUsufruct() {
    if (isAvailable) return c;
    return null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ConnectionShell that = (ConnectionShell) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
