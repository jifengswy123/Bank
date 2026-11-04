# SpringBoot3+MyBatis 银行柜员系统框架

## 功能

- JWT用户登录，密码使用BCrypt哈希
- 用户列表分页查询：按ID或账户状态查询
- 统一的响应结构`Result<T>`与全局异常处理
- MyBatis XML 映射



## 技术栈

| 组件 | 版本 | 说明 |
| --- | --- | --- |
| Java | 25 | 同时可适配SpringAI框架，可做后续开发 |
| Spring Boot | 3.5.16 |  |
| MyBatis | 3.0.5 |  |
| Maven | 3.9.16 |  |
| Lombok |  |  |

## 环境安装

| 组件 | 版本 | 额外说明/配置 |
| --- | --- | --- |
| Mysql | 8.0.46 | 
| Tomcat | 11.0.27 | 将端口改为8081避免冲突 |
| Postman | N/A | 用于转账交易 |

---

## Mysql数据库

```sql
CREATE DATABASE finance_demo
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE TABLE t_account (
                           id           BIGINT        PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
                           account_no   VARCHAR(32)   NOT NULL UNIQUE COMMENT '账户号',
                           holder_name  VARCHAR(64)   NOT NULL COMMENT '持有人',
                           balance      DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '余额',
                           status       VARCHAR(16)   NOT NULL DEFAULT 'NORMAL' COMMENT 'NORMAL正常/FROZEN冻结/CLOSED销户',
                           create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户表';

CREATE TABLE t_user (
                        id           BIGINT      PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
                        username     VARCHAR(64) NOT NULL UNIQUE COMMENT '登录名',
                        password     VARCHAR(128) NOT NULL COMMENT 'BCrypt加密后的密码（不允许出现明文密码）',
                        real_name    VARCHAR(64) COMMENT '真实姓名',
                        role         VARCHAR(32) NOT NULL DEFAULT 'TELLER' COMMENT 'ADMIN/TELLER/AUDITOR',
                        status       VARCHAR(16) NOT NULL DEFAULT 'NORMAL' COMMENT 'NORMAL正常/DISABLED停用',
                        create_time  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';


```

## 运行
- 运行FinanceApplication.java启动项目，
- 访问http://localhost:8081/login

## 接口一览

| 方法 | 路径 | JSON | 说明 |
| --- | --- | --- | --- |
| GET | `/api/accounts/transfer` | {"fromId":1,"toId":2,"amount":80000} | 转账 |
| GET | `/api/auth/login` | {"username": "admin","password": "admin123"} | 获取JWT登录令牌 |



