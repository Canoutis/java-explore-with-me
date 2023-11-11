create table if not exists users (
  id bigint GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  email VARCHAR(254) NOT NULL,
  name VARCHAR(250) NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT uq_user_email UNIQUE (email)
);

create table if not exists category (
  id INT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(50) NOT NULL,
  CONSTRAINT pk_category PRIMARY KEY (id),
  CONSTRAINT uq_cat_name UNIQUE (name)
);

create table if not exists event (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  annotation VARCHAR(2000) NOT NULL,
  category_id INT NOT NULL,
  initiator_id BIGINT NOT NULL,
  description VARCHAR(7000) NOT NULL,
  event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  latitude FLOAT NOT NULL,
  longitude FLOAT NOT NULL,
  paid BOOLEAN NOT NULL,
  participant_limit INT NOT NULL,
  request_moderation BOOLEAN NOT NULL,
  title VARCHAR(120) NOT NULL,
  state VARCHAR(50) NOT NULL,
  published_time TIMESTAMP WITHOUT TIME ZONE,
  created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT pk_event PRIMARY KEY (id),
  CONSTRAINT fk_event_cat FOREIGN KEY (category_id) REFERENCES category(id) ON delete RESTRICT ON update RESTRICT
);

create table if not exists request (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_id BIGINT NOT NULL,
  requester_id BIGINT NOT NULL,
  status VARCHAR(50) NOT NULL,
  CONSTRAINT pk_request PRIMARY KEY (id)
);

create table if not exists compilation (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  pinned BOOLEAN NOT NULL,
  title VARCHAR(50) NOT NULL,
  CONSTRAINT pk_compilation PRIMARY KEY (id)
);

create table if not exists compilation_event (
  compilation_id BIGINT NOT NULL,
  event_id BIGINT NOT NULL,
  PRIMARY KEY (compilation_id, event_id),
  CONSTRAINT fk_comp_ev_comp FOREIGN KEY (compilation_id) REFERENCES compilation(id) ON delete CASCADE ON update CASCADE,
  CONSTRAINT fk_comp_ev_event FOREIGN KEY (event_id) REFERENCES event(id) ON delete RESTRICT ON update RESTRICT
);

create table if not exists comment (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  event_id BIGINT NOT NULL,
  author_id INT NOT NULL,
  text VARCHAR(2500) NOT NULL,
  created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  state VARCHAR(50) NOT NULL,
  moderator_id INT,
  parent_comment_id BIGINT,
  anonymous BOOLEAN NOT NULL,
  updated BOOLEAN NOT NULL,
  CONSTRAINT pk_comment PRIMARY KEY (id)
  CONSTRAINT fk_author_id FOREIGN KEY (author_id) REFERENCES users(id) ON delete CASCADE ON update RESTRICT,
  CONSTRAINT fk_parent_comment_id FOREIGN KEY (parent_comment_id) REFERENCES comment(id) ON delete CASCADE ON update RESTRICT,
  CONSTRAINT fk_event_id FOREIGN KEY (event_id) REFERENCES event(id) ON delete CASCADE ON update RESTRICT,
);

create table if not exists black_list (
  id INT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  person_id INT NOT NULL,
  comment_id BIGINT NOT NULL,
  CONSTRAINT pk_black_list PRIMARY KEY (id)
  CONSTRAINT fk_person_id FOREIGN KEY (person_id) REFERENCES users(id) ON delete CASCADE ON update RESTRICT,
  CONSTRAINT fk_comment_id FOREIGN KEY (comment_id) REFERENCES comment(id) ON delete CASCADE ON update RESTRICT,
);

create table if not exists report (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  initiator_id INT NOT NULL,
  comment_id BIGINT NOT NULL,
  state VARCHAR(50) NOT NULL,
  created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT pk_report PRIMARY KEY (id)
  CONSTRAINT fk_initiator_id FOREIGN KEY (initiator_id) REFERENCES users(id) ON delete CASCADE ON update RESTRICT,
  CONSTRAINT fk_comment_id FOREIGN KEY (comment_id) REFERENCES users(id) ON delete CASCADE ON update RESTRICT,
);