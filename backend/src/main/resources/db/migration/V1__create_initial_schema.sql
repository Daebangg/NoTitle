-- 현재 서비스 엔티티의 최초 스키마. 적용된 파일은 수정하지 않고 다음 버전을 추가한다.
-- created_at / updated_at은 JPA Auditing이 기록한다.

CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(255),
    email_verified_at DATETIME(6),
    nickname VARCHAR(50) NOT NULL,
    profile_image_url VARCHAR(500),
    gender ENUM('MALE', 'FEMALE'),
    birth_year INT,
    role ENUM('USER', 'ADMIN') NOT NULL,
    deleted_at DATETIME(6),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_credential (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    password_changed_at DATETIME(6),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_user_credential_user UNIQUE (user_id),
    CONSTRAINT fk_user_credential_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_social_account (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    provider ENUM('KAKAO', 'GOOGLE', 'NAVER') NOT NULL,
    provider_user_id VARCHAR(100) NOT NULL,
    email VARCHAR(255),
    last_login_at DATETIME(6),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_user_social_account_provider_user UNIQUE (provider, provider_user_id),
    CONSTRAINT uk_user_social_account_user_provider UNIQUE (user_id, provider),
    CONSTRAINT fk_user_social_account_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE room (
    id BIGINT NOT NULL AUTO_INCREMENT,
    type ENUM('TRIP', 'MATCH', 'DIRECT') NOT NULL,
    name VARCHAR(100),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE room_member (
    id BIGINT NOT NULL AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role ENUM('OWNER', 'MEMBER') NOT NULL,
    status ENUM('JOINED', 'LEFT', 'KICKED') NOT NULL,
    last_read_message_id BIGINT,
    notification_muted BIT NOT NULL,
    joined_at DATETIME(6) NOT NULL,
    left_at DATETIME(6),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_room_member_room_user UNIQUE (room_id, user_id),
    CONSTRAINT fk_room_member_room FOREIGN KEY (room_id) REFERENCES room (id),
    CONSTRAINT fk_room_member_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE room_invitation (
    id BIGINT NOT NULL AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    inviter_id BIGINT NOT NULL,
    invitee_id BIGINT,
    code VARCHAR(50),
    status ENUM('PENDING', 'ACCEPTED', 'REJECTED'),
    expires_at DATETIME(6) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_room_invitation_code UNIQUE (code),
    CONSTRAINT fk_room_invitation_room FOREIGN KEY (room_id) REFERENCES room (id),
    CONSTRAINT fk_room_invitation_inviter FOREIGN KEY (inviter_id) REFERENCES users (id),
    CONSTRAINT fk_room_invitation_invitee FOREIGN KEY (invitee_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE travel_style_item (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    min_label VARCHAR(30) NOT NULL,
    max_label VARCHAR(30) NOT NULL,
    sort_order INT NOT NULL,
    is_active BIT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_travel_style (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    score INT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_user_travel_style_user_item UNIQUE (user_id, item_id),
    CONSTRAINT fk_user_travel_style_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_travel_style_item FOREIGN KEY (item_id) REFERENCES travel_style_item (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE companion_search (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    region VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    preferred_gender ENUM('MALE', 'FEMALE'),
    preferred_min_age INT,
    preferred_max_age INT,
    introduction TEXT,
    status ENUM('ACTIVE', 'CLOSED') NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_companion_search_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE companion_request (
    id BIGINT NOT NULL AUTO_INCREMENT,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    companion_search_id BIGINT NOT NULL,
    room_id BIGINT,
    message VARCHAR(300),
    status ENUM('PENDING', 'ACCEPTED', 'REJECTED', 'EXPIRED') NOT NULL,
    responded_at DATETIME(6),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_companion_request_sender_receiver_search UNIQUE (sender_id, receiver_id, companion_search_id),
    CONSTRAINT uk_companion_request_room UNIQUE (room_id),
    CONSTRAINT fk_companion_request_sender FOREIGN KEY (sender_id) REFERENCES users (id),
    CONSTRAINT fk_companion_request_receiver FOREIGN KEY (receiver_id) REFERENCES users (id),
    CONSTRAINT fk_companion_request_search FOREIGN KEY (companion_search_id) REFERENCES companion_search (id),
    CONSTRAINT fk_companion_request_room FOREIGN KEY (room_id) REFERENCES room (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE place (
    id BIGINT NOT NULL AUTO_INCREMENT,
    provider ENUM('KAKAO') NOT NULL,
    external_id VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    category_code VARCHAR(10),
    category_name VARCHAR(255),
    road_address VARCHAR(255),
    address VARCHAR(255),
    latitude DECIMAL(10,7) NOT NULL,
    longitude DECIMAL(10,7) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_place_provider_external_id UNIQUE (provider, external_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE trip (
    id BIGINT NOT NULL AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    source_companion_request_id BIGINT,
    region VARCHAR(50),
    start_date DATE,
    end_date DATE,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_trip_room UNIQUE (room_id),
    CONSTRAINT fk_trip_room FOREIGN KEY (room_id) REFERENCES room (id),
    CONSTRAINT fk_trip_companion_request FOREIGN KEY (source_companion_request_id) REFERENCES companion_request (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE trip_place (
    id BIGINT NOT NULL AUTO_INCREMENT,
    trip_id BIGINT NOT NULL,
    place_id BIGINT NOT NULL,
    day_number INT,
    visit_order INT,
    memo VARCHAR(255),
    added_by BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_trip_place_trip FOREIGN KEY (trip_id) REFERENCES trip (id),
    CONSTRAINT fk_trip_place_place FOREIGN KEY (place_id) REFERENCES place (id),
    CONSTRAINT fk_trip_place_added_by FOREIGN KEY (added_by) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE expense (
    id BIGINT NOT NULL AUTO_INCREMENT,
    trip_id BIGINT NOT NULL,
    payer_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    amount BIGINT NOT NULL,
    split_type ENUM('EQUAL', 'CUSTOM') NOT NULL,
    spent_at DATETIME(6) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_expense_trip FOREIGN KEY (trip_id) REFERENCES trip (id),
    CONSTRAINT fk_expense_payer FOREIGN KEY (payer_id) REFERENCES users (id),
    CONSTRAINT fk_expense_created_by FOREIGN KEY (created_by) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE expense_share (
    id BIGINT NOT NULL AUTO_INCREMENT,
    expense_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    amount BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_expense_share_expense_user UNIQUE (expense_id, user_id),
    CONSTRAINT fk_expense_share_expense FOREIGN KEY (expense_id) REFERENCES expense (id),
    CONSTRAINT fk_expense_share_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE chat_message (
    id BIGINT NOT NULL AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    sender_id BIGINT,
    type ENUM('TEXT', 'IMAGE', 'SYSTEM', 'PLACE_SHARE') NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_chat_message_room FOREIGN KEY (room_id) REFERENCES room (id),
    CONSTRAINT fk_chat_message_sender FOREIGN KEY (sender_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE notification (
    id BIGINT NOT NULL AUTO_INCREMENT,
    receiver_id BIGINT NOT NULL,
    type ENUM('COMPANION_REQUEST', 'COMPANION_ACCEPTED', 'TRIP_INVITE', 'EXPENSE_ADDED') NOT NULL,
    target_type ENUM('COMPANION_REQUEST', 'TRIP', 'ROOM_INVITATION'),
    target_id BIGINT,
    content VARCHAR(255) NOT NULL,
    read_at DATETIME(6),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_notification_receiver FOREIGN KEY (receiver_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
