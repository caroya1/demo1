package com.example.demo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 重新设计的JWT工具类 - 简化版本
 */
@Component
@Log4j2
public class JwtUtils {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private Long expiration;

  /**
   * 获取签名密钥
   */
  private SecretKey getSigningKey() {
    // 确保密钥长度足够
    byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * 生成JWT token - 重新设计
   */
  public String generateToken(String username) {
    log.info("开始生成JWT token: username={}", username);

    try {
      Date now = new Date();
      Date expiryDate = new Date(now.getTime() + expiration * 1000);

      Map<String, Object> claims = new HashMap<>();
      claims.put("username", username);
      claims.put("iat", now.getTime() / 1000);
      claims.put("exp", expiryDate.getTime() / 1000);

      String token = Jwts.builder()
          .setClaims(claims)
          .setSubject(username)
          .setIssuedAt(now)
          .setExpiration(expiryDate)
          .signWith(getSigningKey(), SignatureAlgorithm.HS256)
          .compact();

      log.info("JWT token生成成功: username={}, token前缀={}", username,
          token.substring(0, Math.min(token.length(), 20)) + "...");
      return token;
    } catch (Exception e) {
      log.error("生成JWT token失败: username={}, error={}", username, e.getMessage(), e);
      throw new RuntimeException("生成token失败: " + e.getMessage());
    }
  }

  /**
   * 从token中获取用户名 - 重新设计
   */
  public String getUsernameFromToken(String token) {
    try {
      log.debug("开始解析token获取用户名, token前缀: {}", token.substring(0, Math.min(token.length(), 20)) + "...");

      Claims claims = Jwts.parserBuilder()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(token)
          .getBody();

      String username = claims.getSubject();
      log.debug("从token解析出用户名: {}", username);
      return username;
    } catch (Exception e) {
      log.error("从token解析用户名失败: {}", e.getMessage());
      throw new RuntimeException("无效的token: " + e.getMessage());
    }
  }

  /**
   * 验证token是否有效 - 重新设计
   */
  public boolean validateToken(String token) {
    try {
      log.debug("开始验证token有效性, token前缀: {}", token.substring(0, Math.min(token.length(), 20)) + "...");

      Claims claims = Jwts.parserBuilder()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(token)
          .getBody();

      // 检查是否过期
      Date expiration = claims.getExpiration();
      boolean isExpired = expiration.before(new Date());

      if (isExpired) {
        log.warn("token已过期: expiration={}", expiration);
        return false;
      }

      log.debug("token验证成功: username={}, expiration={}", claims.getSubject(), expiration);
      return true;
    } catch (Exception e) {
      log.warn("token验证失败: {}", e.getMessage());
      return false;
    }
  }

  /**
   * 检查token是否过期
   */
  public boolean isTokenExpired(String token) {
    try {
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(token)
          .getBody();

      boolean expired = claims.getExpiration().before(new Date());
      log.debug("token过期检查: expired={}, expiration={}", expired, claims.getExpiration());
      return expired;
    } catch (Exception e) {
      log.error("检查token过期状态失败: {}", e.getMessage());
      return true;
    }
  }

  /**
   * 获取token剩余有效时间（秒）
   */
  public long getTokenRemainingTime(String token) {
    try {
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(token)
          .getBody();

      Date expiration = claims.getExpiration();
      long remaining = (expiration.getTime() - System.currentTimeMillis()) / 1000;
      return Math.max(0, remaining);
    } catch (Exception e) {
      log.error("获取token剩余时间失败: {}", e.getMessage());
      return 0;
    }
  }
}