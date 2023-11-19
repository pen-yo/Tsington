package com.penyo.tsington.util;

import com.penyo.tsington.cfg.UserConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 驱动代理
 *
 * @author Penyo
 */
public class DriverProxy {
  /**
   * 日志发生器
   */
  private static final Logger logger = LogManager.getLogger(DriverProxy.class);

  /**
   * 注册驱动。
   */
  public static void register(SQLDBProduct prod) {
    try {
      Class.forName(prod.getDriverClassName());
    } catch (ClassNotFoundException e) {
      logger.error(e);
    }
  }

  /**
   * 获取连接。
   */
  public static Connection getConnection(UserConfig uc) {
    try {
      return DriverManager.getConnection(uc.getJdbcUrl(), uc.getUsername(), uc.getPassword());
    } catch (SQLException e) {
      logger.error(e);
      return null;
    }
  }
}
