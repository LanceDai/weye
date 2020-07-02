CREATE DATABASE IF NOT EXISTS WEYE DEFAULT CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI;
USE WEYE;
DROP TABLE IF EXISTS WARN_RULE;
CREATE TABLE IF NOT EXISTS WARN_RULE
(
    ID            INT PRIMARY KEY AUTO_INCREMENT,
    NAME          VARCHAR(255) NOT NULL,
    SOURCE_TYPE   VARCHAR(255) NOT NULL,
    SOURCE_ID     INT          NOT NULL,
    COMPARE_ITEM  VARCHAR(255) NOT NULL,
    COMPARE_OP    VARCHAR(255) NOT NULL,
    COMPARE_VALUE VARCHAR(255) NOT NULL,
    WARN_WAY      VARCHAR(255) NOT NULL,
    WARN_URL      VARCHAR(255) NOT NULL,
    WARN_MSG      VARCHAR(255) NOT NULL,
    WEB_APP_ID    INT          NOT NULL
) ENGINE = INNODB
  DEFAULT CHARSET = UTF8;