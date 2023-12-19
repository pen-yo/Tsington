package com.penyo.tsington.support;

import com.penyo.tsington.config.PerformanceConfig;
import com.penyo.tsington.config.UserConfig;

/**
 * 青彤™ 连接池初始化器
 *
 * @author Penyo
 * @see com.penyo.tsington.v0.TsingtonDataSource
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
