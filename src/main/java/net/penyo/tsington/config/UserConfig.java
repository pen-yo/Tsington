package net.penyo.tsington.config;

/**
 * 用户配置
 *
 * @param driver   数据库驱动
 * @param url      数据库地址
 * @param username 用户名
 * @param password 密码
 * @author Penyo
 */
public record UserConfig(String driver, String url, String username, String password) {
}
