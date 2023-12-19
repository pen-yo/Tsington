package com.penyo.tsington.v0;

import com.penyo.tsington.config.PerformanceConfig;
import com.penyo.tsington.config.UserConfig;

import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

/**
 * 基于类路径配置构造的青彤™ 连接池
 *
 * @author Penyo
 * @see TsingtonDataSource
 */
public class ClassPathYmlBasedTsingtonDataSource extends TsingtonDataSource {
  public ClassPathYmlBasedTsingtonDataSource(String ymlPath) {
    UserConfig uc = null;
    PerformanceConfig pc = new PerformanceConfig();
    try (InputStream s = ClassPathYmlBasedTsingtonDataSource.class.getClassLoader().getResourceAsStream(ymlPath)) {
      if (s != null) {
        Map<String, Map<String, Object>> data = new Yaml().load(s);
        if (data != null) {
          Map<String, Object> ucd = data.get("user");
          if (ucd != null) {
            String driver = "" + ucd.get("driver");
            String url = "" + ucd.get("url");
            String username = "" + ucd.get("username");
            String password = "" + ucd.get("password");
            uc = new UserConfig(driver, url, username, password);
          }

          Map<String, Object> pcd = data.get("performance");
          if (pcd != null) {
            Object minConnectionsNum = pcd.get("minConnectionsNum");
            if (minConnectionsNum != null) pc.setMinConnectionsNum((Integer) minConnectionsNum);
            Object maxConnectionsNum = pcd.get("maxConnectionsNum");
            if (maxConnectionsNum != null) pc.setMaxConnectionsNum((Integer) maxConnectionsNum);
            Object requestTimeout = pcd.get("requestTimeout");
            if (requestTimeout != null) pc.setRequestTimeout((Long) requestTimeout);
            Object pressureToExpand = pcd.get("pressureToExpand");
            if (pressureToExpand != null) pc.setPressureToExpand((Double) pressureToExpand);
            Object pressureToContract = pcd.get("pressureToContract");
            if (pressureToContract != null) pc.setPressureToContract((Double) pressureToContract);
            Object resizeNum = pcd.get("resizeNum");
            if (resizeNum != null) pc.setResizeNum((Integer) resizeNum);
            Object scanCycle = pcd.get("scanCycle");
            if (scanCycle != null) pc.setScanCycle((Long) scanCycle);
          }
        }
      }
    } catch (Exception ignored) {
    }
    activate(uc, pc);
  }
}
