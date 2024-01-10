package net.penyo.tsington;

import net.penyo.tsington.config.PerformanceConfig;
import net.penyo.tsington.config.UserConfig;
import net.penyo.tsington.support.TsingtonDataSourceInitializer;
import net.penyo.tsington.util.SqlDbProduct;

public class TsingtonConfig implements TsingtonDataSourceInitializer {
  @Override
  public UserConfig defineUserConfig() {
    return new UserConfig(SqlDbProduct.MYSQL.getDriverClassName(), "jdbc:mysql:///?serverTimezone=UTC", "root", "1234");
  }

  @Override
  public PerformanceConfig definePerformanceConfig() {
    return new PerformanceConfig();
  }
}
