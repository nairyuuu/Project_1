Create table users (
    user_id varchar (255),
    username varchar (30) not null unique,
    password varchar (255) not null
);

CREATE SCHEMA IF NOT EXISTS `caseSchema`
DEFAULT CHARACTER SET latin1
COLLATE latin1_general_cs ;
