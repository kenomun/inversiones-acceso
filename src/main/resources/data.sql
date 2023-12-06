INSERT INTO roles (id, description, permission) VALUES (1, 'user', 1);
INSERT INTO roles (id, description, permission) VALUES (2, 'admin', 2);

INSERT INTO users (email, name, password, state, role_id) VALUES ('user@example.com', 'Usuario1', '$argon2id$v=19$m=1024,t=1,p=1$zwBNKLcfM8hWRcFiLeiCQg$ok9B84aWvTEou/Kxy0pqEn+yrsMZOJ+cHQwIWL2rol8', 'activo', 1);
INSERT INTO users (email, name, password, state, role_id) VALUES ('admin@example.com', 'Admin1', '$argon2id$v=19$m=1024,t=1,p=1$pqu/1zzxAn7FypwDYwC6qA$wDB+IOtIsY48QEKOKsSoow/kPnNVObPoU4+gvXtr/TE', 'activo', 2);
