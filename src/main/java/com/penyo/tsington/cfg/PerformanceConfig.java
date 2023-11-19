package com.penyo.tsington.cfg;

/**
 * 性能配置
 *
 * @author Penyo
 */
public class PerformanceConfig {
  /**
   * 最小连接数
   */
  private int minConnectionsNum = 8;
  /**
   * 最大连接数
   */
  private int maxConnectionsNum = 24;
  /**
   * 请求超时时长
   */
  private int requestTimeout = 3000;
  /**
   * 扩张压力
   */
  private double pressureToExpand = 4;
  /**
   * 收缩压力
   */
  private double pressureToContract = 0.25;
  /**
   * 尺寸变化量
   */
  private int resizeNum = 4;
  /**
   * 扫描周期
   */
  private long scanCycle = 3000;

  public int getMinConnectionsNum() {
    return minConnectionsNum;
  }

  public void setMinConnectionsNum(int minConnectionsNum) {
    this.minConnectionsNum = minConnectionsNum;
  }

  public int getMaxConnectionsNum() {
    return maxConnectionsNum;
  }

  public void setMaxConnectionsNum(int maxConnectionsNum) {
    this.maxConnectionsNum = maxConnectionsNum;
  }

  public int getRequestTimeout() {
    return requestTimeout;
  }

  public void setRequestTimeout(int requestTimeout) {
    this.requestTimeout = requestTimeout;
  }

  public double getPressureToExpand() {
    return pressureToExpand;
  }

  public void setPressureToExpand(double pressureToExpand) {
    this.pressureToExpand = pressureToExpand;
  }

  public double getPressureToContract() {
    return pressureToContract;
  }

  public void setPressureToContract(double pressureToContract) {
    this.pressureToContract = pressureToContract;
  }

  public int getResizeNum() {
    return resizeNum;
  }

  public void setResizeNum(int resizeNum) {
    this.resizeNum = resizeNum;
  }

  public long getScanCycle() {
    return scanCycle;
  }

  public void setScanCycle(long scanCycle) {
    this.scanCycle = scanCycle;
  }
}
