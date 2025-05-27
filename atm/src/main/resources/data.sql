-- Insert test users
INSERT INTO atm.users (username, password, email, role, status) VALUES
('testuser', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'test@example.com', 'USER', 'ACTIVE'),
('admin', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'admin@example.com', 'ADMIN', 'ACTIVE');

-- Insert test accounts
INSERT INTO atm.accounts (account_number, account_type, balance, user_id) VALUES
('ACC123456', 'CHECKING', 1000.00, 1),
('ACC789012', 'SAVINGS', 5000.00, 1); 