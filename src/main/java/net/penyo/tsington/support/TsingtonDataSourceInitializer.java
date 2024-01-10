package net.penyo.tsington.support;

import net.penyo.tsington.config.PerformanceConfig;
import net.penyo.tsington.config.UserConfig;
import net.penyo.tsington.v0.TsingtonDataSource;

/**
 * 青彤™ 连接池初始化器
 *
 * @author Penyo
 * @see TsingtonDataSource
 */
public interface TsingtonDataSourceInitializer {
  /**
   * 定义用户配置。
   *
   * @see UserConfig
   */
  UserConfig defineUserConfig();

  /**
   * 定义性能配置。
   *
   * @see PerformanceConfig
   */
  PerformanceConfig definePerformanceConfig();
}
