-- -----------------------------------------------------
-- Drop Schema for ACMECollege Application
--
-- In order for the `cst8277`@`localhost` user to be able to create (or drop) a schema,
-- it needs additional privileges.  If you are using MySQL Workbench, log-in to it as root,
-- click on the 'Administration' tab, select 'Users and Privileges' and find and click the cst8277 user.
-- The 'Administrative Roles' tab has an entry 'DBA' - select it, and click all the individual PRIVILEGES
-- and then click 'Apply'.
--
-- If you wish to use a 'raw' .sql-script instead, you still need to log-in as root,
-- the command to GRANT the appropriate PRIVILEGES is:
-- GRANT ALL PRIVILEGES ON *.* TO `cst8277`@`localhost`;
--
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `acmecollege` DEFAULT CHARACTER SET utf8mb4;
USE `acmecollege`;

-- ------------------------------------------------------------------------
-- Table `student`
-- Note:  This is NOT the same Student Entity as in Lab1/Assignment1/Assignment2
-- ------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS `student` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(50) NOT NULL,
  `last_name` VARCHAR(50) NOT NULL,
  `created` DATETIME NULL,
  `updated` DATETIME NULL,
  `version` BIGINT NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`)
);

-- -----------------------------------------------------
-- Table `student_club`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `student_club` (
  `club_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `academic` BIT(1) NOT NULL,
  `created` DATETIME NULL,
  `updated` DATETIME NULL,
  `version` BIGINT NOT NULL DEFAULT 1,
  PRIMARY KEY (`club_id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE
);

-- -----------------------------------------------------
-- Table `club_membership`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `club_membership` (
  `membership_id` INT NOT NULL AUTO_INCREMENT,
  `club_id` INT NOT NULL,
  `start_date` DATETIME NOT NULL,
  `end_date` DATETIME NOT NULL,
  `active` BIT(1) NOT NULL,
  `created` DATETIME NULL,
  `updated` DATETIME NULL,
  `version` BIGINT NOT NULL DEFAULT 1,
  PRIMARY KEY (`membership_id`),
  INDEX `fk_club_membership_student_club1_idx` (`club_id` ASC) VISIBLE,
  CONSTRAINT `fk_club_membership_student_club1`
    FOREIGN KEY (`club_id`) REFERENCES `student_club` (`club_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

-- -----------------------------------------------------
-- Table `membership_card`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `membership_card` (
  `card_id` INT NOT NULL AUTO_INCREMENT,
  `student_id` INT NOT NULL,
  `membership_id` INT NULL,
  `signed` BIT(1) NOT NULL,
  `created` DATETIME NULL,
  `updated` DATETIME NULL,
  `version` BIGINT NOT NULL DEFAULT 1,
  INDEX `fk_membership_card_student1_idx` (`student_id` ASC) VISIBLE,
  INDEX `fk_membership_card_club_membership1_idx` (`membership_id` ASC) VISIBLE,
  UNIQUE INDEX `card_id_UNIQUE` (`card_id` ASC) VISIBLE,
  PRIMARY KEY (`card_id`),
  CONSTRAINT `fk_membership_card_student1`
    FOREIGN KEY (`student_id`)
    REFERENCES `student` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_membership_card_club_membership1`
    FOREIGN KEY (`membership_id`)
    REFERENCES `club_membership` (`membership_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);

-- -----------------------------------------------------
-- Table `peer_tutor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `peer_tutor` (
  `peer_tutor_id` INT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(50) NOT NULL,
  `last_name` VARCHAR(50) NOT NULL,
  `program` VARCHAR(50) NOT NULL,
  `created` DATETIME NULL,
  `updated` DATETIME NULL,
  `version` BIGINT NOT NULL DEFAULT 1,
  PRIMARY KEY (`peer_tutor_id`)
);

-- -----------------------------------------------------
-- Table `course`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `course` (
  `course_id` INT NOT NULL AUTO_INCREMENT,
  `course_code` VARCHAR(7) NOT NULL,
  `course_title` VARCHAR(100) NOT NULL,
  `year` INT NOT NULL,
  `semester` VARCHAR(6) NOT NULL,
  `credit_units` INT NOT NULL,
  `online` BIT(1) NOT NULL,
  `created` DATETIME NULL,
  `updated` DATETIME NULL,
  `version` BIGINT NOT NULL DEFAULT 1,
  PRIMARY KEY (`course_id`)
);

-- -----------------------------------------------------
-- Table `peer_tutor_registration`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `peer_tutor_registration` (
  `student_id` INT NOT NULL,
  `course_id` INT NOT NULL,
  `numeric_grade` INT NULL,
  `letter_grade` VARCHAR(3) NULL,
  `peer_tutor_id` INT NULL,
  `created` DATETIME NULL,
  `updated` DATETIME NULL,
  `version` BIGINT NOT NULL DEFAULT 1,
  PRIMARY KEY (`student_id`, `course_id`),
  INDEX `fk_peer_tutor_registration_course1_idx` (`course_id` ASC) VISIBLE,
  INDEX `fk_peer_tutor_registration_student1_idx` (`student_id` ASC) VISIBLE,
  CONSTRAINT `fk_peer_tutor_registration_peer_tutor1`
    FOREIGN KEY (`peer_tutor_id`)
    REFERENCES `peer_tutor` (`peer_tutor_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_peer_tutor_registration_course1`
    FOREIGN KEY (`course_id`)
    REFERENCES `course` (`course_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_peer_tutor_registration_student1`
    FOREIGN KEY (`student_id`)
    REFERENCES `student` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);
