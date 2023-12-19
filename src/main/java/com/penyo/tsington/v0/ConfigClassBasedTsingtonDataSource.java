package com.penyo.tsington.v0;

import com.penyo.tsington.config.PerformanceConfig;
import com.penyo.tsington.config.UserConfig;
import com.penyo.tsington.support.TsingtonDataSourceInitializer;

/**
 * 基于配置类构造的青彤™ 连接池
 *
 * @author Penyo
 * @see TsingtonDataSource
 * @see TsingtonDataSourceInitializer
 */
public class ConfigClassBasedTsingtonDataSource extends TsingtonDataSource {
  public <ConfigClass extends TsingtonDataSourceInitializer> ConfigClassBasedTsingtonDataSource(Class<ConfigClass> clazz) {
    UserConfig uc = null;
    PerformanceConfig pc = new PerformanceConfig();
    try {
      TsingtonDataSourceInitializer init = clazz.getDeclaredConstructor().newInstance();
      uc = init.defineUserConfig();
      pc = init.definePerformanceConfig();
    } catch (Exception ignored) {
    }
    activate(uc, pc);
  }
}
