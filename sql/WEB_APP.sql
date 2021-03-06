CREATE DATABASE IF NOT EXISTS WEYE DEFAULT CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI;
USE WEYE;
DROP TABLE IF EXISTS WEB_APP;
CREATE TABLE IF NOT EXISTS WEB_APP
(
    ID         INT PRIMARY KEY AUTO_INCREMENT,
    NAME       VARCHAR(255) NOT NULL,
    PORT       INTEGER      NOT NULL,
    SERVER_ID  INTEGER      NOT NULL
) ENGINE = INNODB
  DEFAULT CHARSET = UTF8;