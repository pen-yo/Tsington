package com.penyo.tsington.cfg;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 用户配置
 *
 * @author Penyo
 */
public class UserConfig {
  /**
   * 日志发生器
   */
  private static final Logger logger = LogManager.getLogger(UserConfig.class);

  /**
   * JDBC 数据库地址
   */
  private final String jdbcUrl;
  /**
   * 用户名
   */
  private final String username;
  /**
   * 密码
   */
  private final String password;

  public UserConfig(String jdbcUrl, String username, String password) {
    this.jdbcUrl = jdbcUrl;
    this.username = username;
    this.password = password;
  }

  public UserConfig(InputStream propertiesFile) {
    Properties props = new Properties();
    try {
      props.load(propertiesFile);
    } catch (IOException e) {
      logger.error(e);
    }
    jdbcUrl = props.getProperty("jdbcUrl");
    username = props.getProperty("username");
    password = props.getProperty("password");
  }

  public String getJdbcUrl() {
    return jdbcUrl;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
}
