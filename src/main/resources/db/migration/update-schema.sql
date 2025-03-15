CREATE TABLE user
(
    id                  BIGINT       NOT NULL,
    created_by          VARCHAR(255) NULL,
    updated_by          VARCHAR(255) NULL,
    created_at          datetime NULL,
    updated_at          datetime NULL,
    first_name          VARCHAR(50)  NOT NULL,
    last_name           VARCHAR(50)  NOT NULL,
    date_of_birth       date NULL,
    gender              BIT(1) NULL,
    address             VARCHAR(255) NULL,
    email               VARCHAR(100) NOT NULL,
    country             VARCHAR(50) NULL,
    phone_number        VARCHAR(15) NULL,
    avatar              VARCHAR(255) NULL,
    bio                 TEXT NULL,
    is_enabled          BIT(1)       NOT NULL,
    registration_status VARCHAR(255) NOT NULL,
    facebook_url        VARCHAR(255) NULL,
    twitter_url         VARCHAR(255) NULL,
    linkedin_url        VARCHAR(255) NULL,
    instagram_url       VARCHAR(255) NULL,
    github_url          VARCHAR(255) NULL,
    expertise           VARCHAR(255) NULL,
    cv_url              VARCHAR(255) NULL,
    year_of_expertise   INT NULL,
    zipcode             VARCHAR(10) NULL,
    password            VARCHAR(255) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE user_roles
(
    user_id  BIGINT NOT NULL,
    roles_id INT    NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, roles_id)
);

ALTER TABLE user
    ADD CONSTRAINT uc_user_email UNIQUE (email);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_role FOREIGN KEY (roles_id) REFERENCES `role` (id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_user FOREIGN KEY (user_id) REFERENCES user (id);
CREATE TABLE `role`
(
    id            INT AUTO_INCREMENT NOT NULL,
    role_name     VARCHAR(200)       NOT NULL,
    `description` VARCHAR(255)       NULL,
    CONSTRAINT pk_role PRIMARY KEY (id)
);

CREATE TABLE role_permissions
(
    role_id        INT NOT NULL,
    permissions_id INT NOT NULL,
    CONSTRAINT pk_role_permissions PRIMARY KEY (role_id, permissions_id)
);

ALTER TABLE `role`
    ADD CONSTRAINT uc_role_role_name UNIQUE (role_name);

ALTER TABLE role_permissions
    ADD CONSTRAINT fk_rolper_on_permission FOREIGN KEY (permissions_id) REFERENCES permission (id);

ALTER TABLE role_permissions
    ADD CONSTRAINT fk_rolper_on_role FOREIGN KEY (role_id) REFERENCES `role` (id);