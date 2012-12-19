SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS `cpanalyze` ;
CREATE SCHEMA IF NOT EXISTS `cpanalyze` DEFAULT CHARACTER SET utf8 ;
USE `cpanalyze` ;

-- -----------------------------------------------------
-- Table `cpanalyze`.`categories`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cpanalyze`.`categories` ;

CREATE  TABLE IF NOT EXISTS `cpanalyze`.`categories` (
  `name` VARCHAR(25) NOT NULL ,
  PRIMARY KEY (`name`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cpanalyze`.`products`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cpanalyze`.`products` ;

CREATE  TABLE IF NOT EXISTS `cpanalyze`.`products` (
  `productId` INT(11) NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(255) NOT NULL ,
  `category` VARCHAR(50) NULL DEFAULT NULL ,
  PRIMARY KEY (`productId`) ,
  INDEX `fk_Product_Category1` (`category` ASC) ,
  CONSTRAINT `fk_Product_Category1`
    FOREIGN KEY (`category` )
    REFERENCES `cpanalyze`.`categories` (`name` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 15
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cpanalyze`.`jars`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cpanalyze`.`jars` ;

CREATE  TABLE IF NOT EXISTS `cpanalyze`.`jars` (
  `jarId` INT(11) NOT NULL AUTO_INCREMENT ,
  `jarname` VARCHAR(100) NOT NULL ,
  `artifact` VARCHAR(255) NOT NULL ,
  `version` VARCHAR(100) NOT NULL ,
  `productId` INT(11) NOT NULL ,
  `fdmmdoc` MEDIUMTEXT NULL ,
  `apidoc` MEDIUMTEXT NULL ,
  PRIMARY KEY (`jarId`) ,
  INDEX `fk_Jars_Product1` (`productId` ASC) ,
  INDEX `jarname` (`jarname` ASC) ,
  CONSTRAINT `fk_Jars_Product1`
    FOREIGN KEY (`productId` )
    REFERENCES `cpanalyze`.`products` (`productId` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 24
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cpanalyze`.`classes`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cpanalyze`.`classes` ;

CREATE  TABLE IF NOT EXISTS `cpanalyze`.`classes` (
  `classId` INT(11) NOT NULL AUTO_INCREMENT ,
  `fqcn` VARCHAR(255) NOT NULL COMMENT 'Full Qualified Class Name' ,
  `accessFlags` INT(11) NOT NULL ,
  `superFqcn` VARCHAR(255) NULL DEFAULT NULL COMMENT 'Full qualified class name of the super class' ,
  `jarId` INT(11) NOT NULL ,
  PRIMARY KEY (`classId`) ,
  INDEX `fk_Classes_Jars` (`jarId` ASC) ,
  INDEX `fqcn` (`fqcn` ASC) ,
  CONSTRAINT `fk_Classes_Jars`
    FOREIGN KEY (`jarId` )
    REFERENCES `cpanalyze`.`jars` (`jarId` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 18629
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cpanalyze`.`dependencies`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cpanalyze`.`dependencies` ;

CREATE  TABLE IF NOT EXISTS `cpanalyze`.`dependencies` (
  `depId` INT(11) NOT NULL AUTO_INCREMENT ,
  `jarId` INT(11) NOT NULL ,
  `dependendJar` INT(11) NOT NULL ,
  PRIMARY KEY (`depId`) ,
  INDEX `fk_Dependencies_Jars2` (`jarId` ASC) ,
  INDEX `fk_Dependencies_Jars3` (`dependendJar` ASC) ,
  CONSTRAINT `fk_Dependencies_Jars2`
    FOREIGN KEY (`jarId` )
    REFERENCES `cpanalyze`.`jars` (`jarId` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_Dependencies_Jars3`
    FOREIGN KEY (`dependendJar` )
    REFERENCES `cpanalyze`.`jars` (`jarId` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cpanalyze`.`methods`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cpanalyze`.`methods` ;

CREATE  TABLE IF NOT EXISTS `cpanalyze`.`methods` (
  `methodId` INT(11) NOT NULL AUTO_INCREMENT ,
  `accessFlags` INT(11) NOT NULL ,
  `constructor` TINYINT(1) NOT NULL ,
  `returnType` VARCHAR(255) NULL DEFAULT NULL ,
  `signature` VARCHAR(1000) NOT NULL ,
  `classId` INT(11) NOT NULL ,
  PRIMARY KEY (`methodId`) ,
  INDEX `fk_Methods_Classes1` (`classId` ASC) ,
  INDEX `signature` (`signature` ASC) ,
  CONSTRAINT `fk_Methods_Classes1`
    FOREIGN KEY (`classId` )
    REFERENCES `cpanalyze`.`classes` (`classId` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 165024
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cpanalyze`.`extcalls`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cpanalyze`.`extcalls` ;

CREATE  TABLE IF NOT EXISTS `cpanalyze`.`extcalls` (
  `callId` INT(11) NOT NULL AUTO_INCREMENT ,
  `extFqcn` VARCHAR(255) NOT NULL ,
  `extSignature` VARCHAR(511) NOT NULL ,
  `classId` INT(11) NULL DEFAULT NULL ,
  `methodId` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`callId`) ,
  INDEX `fk_ExtCalls_Methods1` (`methodId` ASC) ,
  INDEX `fk_ExtCalls_Classes1` (`classId` ASC) ,
  INDEX `ext` (`extFqcn` ASC, `extSignature` ASC) ,
  CONSTRAINT `fk_ExtCalls_Classes1`
    FOREIGN KEY (`classId` )
    REFERENCES `cpanalyze`.`classes` (`classId` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_ExtCalls_Methods1`
    FOREIGN KEY (`methodId` )
    REFERENCES `cpanalyze`.`methods` (`methodId` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 4539
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cpanalyze`.`extimplements`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cpanalyze`.`extimplements` ;

CREATE  TABLE IF NOT EXISTS `cpanalyze`.`extimplements` (
  `implementsId` INT(11) NOT NULL AUTO_INCREMENT ,
  `extFqcn` VARCHAR(255) NOT NULL ,
  `classId` INT(11) NOT NULL ,
  PRIMARY KEY (`implementsId`) ,
  INDEX `fk_ExtImplements_Classes1` (`classId` ASC) ,
  INDEX `extFqcn` (`extFqcn` ASC) ,
  CONSTRAINT `fk_ExtImplements_Classes1`
    FOREIGN KEY (`classId` )
    REFERENCES `cpanalyze`.`classes` (`classId` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 111
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cpanalyze`.`members`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cpanalyze`.`members` ;

CREATE  TABLE IF NOT EXISTS `cpanalyze`.`members` (
  `memberId` INT(11) NOT NULL AUTO_INCREMENT ,
  `signature` VARCHAR(255) NOT NULL ,
  `classId` INT(11) NOT NULL ,
  PRIMARY KEY (`memberId`) ,
  INDEX `fk_Members_Classes1` (`classId` ASC) ,
  INDEX `signature` (`signature` ASC) ,
  CONSTRAINT `fk_Members_Classes1`
    FOREIGN KEY (`classId` )
    REFERENCES `cpanalyze`.`classes` (`classId` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 74684
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cpanalyze`.`extinstanceof`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cpanalyze`.`extinstanceof` ;

CREATE  TABLE IF NOT EXISTS `cpanalyze`.`extinstanceof` (
  `instanceId` INT(11) NOT NULL AUTO_INCREMENT ,
  `extFqcn` VARCHAR(255) NOT NULL ,
  `memberId` INT(11) NOT NULL ,
  PRIMARY KEY (`instanceId`) ,
  INDEX `fk_ExtInstanceOf_Members1` (`memberId` ASC) ,
  INDEX `extFqcn` (`extFqcn` ASC) ,
  CONSTRAINT `fk_ExtInstanceOf_Members1`
    FOREIGN KEY (`memberId` )
    REFERENCES `cpanalyze`.`members` (`memberId` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 260
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cpanalyze`.`extlinks`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cpanalyze`.`extlinks` ;

CREATE  TABLE IF NOT EXISTS `cpanalyze`.`extlinks` (
  `linkId` INT(11) NOT NULL AUTO_INCREMENT ,
  `extFqcn` VARCHAR(255) NOT NULL ,
  `classId` INT(11) NULL DEFAULT NULL ,
  `methodId` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`linkId`) ,
  INDEX `fk_ExtLinks_Methods1` (`methodId` ASC) ,
  INDEX `fk_ExtLinks_Classes1` (`classId` ASC) ,
  CONSTRAINT `fk_ExtLinks_Classes1`
    FOREIGN KEY (`classId` )
    REFERENCES `cpanalyze`.`classes` (`classId` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_ExtLinks_Methods1`
    FOREIGN KEY (`methodId` )
    REFERENCES `cpanalyze`.`methods` (`methodId` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 6038
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cpanalyze`.`interfaces`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cpanalyze`.`interfaces` ;

CREATE  TABLE IF NOT EXISTS `cpanalyze`.`interfaces` (
  `interfaceId` INT(11) NOT NULL AUTO_INCREMENT ,
  `fqin` VARCHAR(255) NOT NULL ,
  `classId` INT(11) NOT NULL ,
  PRIMARY KEY (`interfaceId`) ,
  INDEX `fk_Interfaces_Classes1` (`classId` ASC) ,
  CONSTRAINT `fk_Interfaces_Classes1`
    FOREIGN KEY (`classId` )
    REFERENCES `cpanalyze`.`classes` (`classId` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 8870
DEFAULT CHARACTER SET = utf8;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
