package net.penyo.tsington.util;

import net.penyo.tsington.config.UserConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 驱动代理
 *
 * @author Penyo
 */
public class DriverProxy {
  /**
   * 注册驱动。
   */
  public static void register(String driver) {
    try {
      Class.forName(driver);
    } catch (ClassNotFoundException ignored) {
    }
  }

  /**
   * 获取连接。
   */
  public static Connection getConnection(UserConfig uc) {
    try {
      return DriverManager.getConnection(uc.url(), uc.username(), uc.password());
    } catch (SQLException ignored) {
    }
    return null;
  }
}
