package com.penyo.tsington;

import com.penyo.tsington.config.PerformanceConfig;
import com.penyo.tsington.config.UserConfig;
import com.penyo.tsington.support.TsingtonDataSourceInitializer;
import com.penyo.tsington.util.SqlDbProduct;

public class QingtongConfig implements TsingtonDataSourceInitializer {
  @Override
  public UserConfig defineUserConfig() {
    return new UserConfig(SqlDbProduct.MYSQL.getDriverClassName(), "jdbc:mysql:///?serverTimezone=UTC", "root", "1234");
  }

  @Override
  public PerformanceConfig definePerformanceConfig() {
    return new PerformanceConfig();
  }
}
