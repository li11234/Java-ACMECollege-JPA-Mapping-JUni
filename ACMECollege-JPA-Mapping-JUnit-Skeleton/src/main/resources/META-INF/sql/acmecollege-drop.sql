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

--DROP SCHEMA IF EXISTS `acmecollege`;
use acmecollege;

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE acmecollege.peer_tutor;

TRUNCATE TABLE acmecollege.student_club;

TRUNCATE TABLE acmecollege.club_membership;

TRUNCATE TABLE acmecollege.peer_tutor_registration;

TRUNCATE TABLE acmecollege.membership_card;

TRUNCATE TABLE acmecollege.student;

TRUNCATE TABLE acmecollege.course;

SET FOREIGN_KEY_CHECKS = 1;