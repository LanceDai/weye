CREATE DATABASE IF NOT EXISTS WEYE DEFAULT CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI;
USE WEYE;
DROP TABLE IF EXISTS SERVER_INFO;
CREATE TABLE IF NOT EXISTS SERVER_INFO
(
    ID             INT PRIMARY KEY AUTO_INCREMENT,
    SERVER_ID      INT          NOT NULL UNIQUE ,
    PROCESSOR_INFO VARCHAR(255) NOT NULL,
    SYSTEM_VERSION VARCHAR(255) NOT NULL,
    SYSTEM_FAMILY  VARCHAR(255) NOT NULL,
    BITNESS        INT          NOT NULL,
    TOTAL_MEM      LONG         NOT NULL
) ENGINE = INNODB
  DEFAULT CHARSET = UTF8;