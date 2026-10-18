CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    
    CONSTRAINT uc_roleName UNIQUE (name)
);

CREATE TABLE authorities (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    
    CONSTRAINT uc_authorityName UNIQUE (name)
);

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    public_id VARCHAR(30) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    encrypted_password VARCHAR(255) NOT NULL,
    email_verification_token VARCHAR(255),
    email_verification_status BOOLEAN NOT NULL DEFAULT FALSE,
 
    CONSTRAINT uc_publicId UNIQUE (public_id),
    CONSTRAINT uc_email UNIQUE (email)
);

CREATE TABLE users_addresses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    users_id BIGINT NOT NULL,
    address_id VARCHAR(30) NOT NULL,
    city VARCHAR(50) NOT NULL,
    country VARCHAR(50) NOT NULL,
    street_name VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    type VARCHAR(10) NOT NULL,

    CONSTRAINT uk_users_addresses_address_id UNIQUE (address_id),
    CONSTRAINT fk_user_address_user FOREIGN KEY (users_id) REFERENCES users(id)
);

CREATE TABLE roles_authorities (
    role_id BIGINT NOT NULL,
    authority_id BIGINT NOT NULL,
    
    CONSTRAINT pk_roles_authorities PRIMARY KEY (role_id, authority_id),
    CONSTRAINT fk_roles_authorities_role FOREIGN KEY (role_id) REFERENCES roles(id),
    CONSTRAINT fk_roles_authorities_authority FOREIGN KEY (authority_id) REFERENCES authorities(id)
);

CREATE TABLE users_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
   
    CONSTRAINT pk_users_roles PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_users_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_users_roles_role FOREIGN KEY (role_id) REFERENCES roles(id)
);