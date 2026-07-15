UPDATE users SET role = 'ROLE_' || role WHERE role NOT LIKE 'ROLE_%';
