INSERT INTO tb_users (name, email, phone, password) VALUES ('João Silva', 'joao.silva@email.com', '123456789', 'senha123');
INSERT INTO tb_users (name, email, phone, password) VALUES ('Maria Oliveira', 'maria.oliveira@email.com', '987654321', 'senha456');
INSERT INTO tb_users (name, email, phone, password) VALUES ('Carlos Santos', 'carlos.santos@email.com', '111223344', 'senha789');
INSERT INTO tb_users (name, email, phone, password) VALUES ('Ana Pereira', 'ana.pereira@email.com', '555666777', 'senhaABC');
INSERT INTO tb_users (name, email, phone, password) VALUES ('Lucas Souza', 'lucas.souza@email.com', '999000111', 'senhaDEF');
INSERT INTO tb_users (name, email, phone, password) VALUES ('Mariana Lima', 'mariana.lima@email.com', '222333444', 'senhaGHI');
INSERT INTO tb_users (name, email, phone, password) VALUES ('Felipe Rocha', 'felipe.rocha@email.com', '777888999', 'senhaJKL');
INSERT INTO tb_users (name, email, phone, password) VALUES ('Patricia Costa', 'patricia.costa@email.com', '444555666', 'senhaMNO');
INSERT INTO tb_users (name, email, phone, password) VALUES ('Gustavo Silva', 'gustavo.silva@email.com', '111222333', 'senhaPQR');
INSERT INTO tb_users (name, email, phone, password) VALUES ('Juliana Santos', 'juliana.santos@email.com', '888999000', 'senhaSTU');

-- Pedido 1 para o usuário 1 com status "WAITING_PAYMENT"
INSERT INTO tb_orders (moment, order_status, client_id) VALUES (CURRENT_TIMESTAMP, 1, 1);
-- Pedido 2 para o usuário 1 com status "SHIPPED"
INSERT INTO tb_orders (moment, order_status, client_id) VALUES (CURRENT_TIMESTAMP, 3, 1);

-- Pedido 1 para o usuário 2 com status "PAID"
INSERT INTO tb_orders (moment, order_status, client_id) VALUES (CURRENT_TIMESTAMP, 2, 2);
-- Pedido 2 para o usuário 2 com status "DELIVERED"
INSERT INTO tb_orders (moment, order_status, client_id) VALUES (CURRENT_TIMESTAMP, 4, 2);

-- Pedido 1 para o usuário 3 com status "CANCELED"
INSERT INTO tb_orders (moment, order_status, client_id) VALUES (CURRENT_TIMESTAMP, 5, 3);
-- Pedido 2 para o usuário 3 com status "WAITING_PAYMENT"
INSERT INTO tb_orders (moment, order_status, client_id) VALUES (CURRENT_TIMESTAMP, 1, 3);

-- Pedido 1 para o usuário 4 com status "SHIPPED"
INSERT INTO tb_orders (moment, order_status, client_id) VALUES (CURRENT_TIMESTAMP, 3, 4);
-- Pedido 2 para o usuário 4 com status "PAID"
INSERT INTO tb_orders (moment, order_status, client_id) VALUES (CURRENT_TIMESTAMP, 2, 4);

-- Pedido 1 para o usuário 5 com status "DELIVERED"
INSERT INTO tb_orders (moment, order_status, client_id) VALUES (CURRENT_TIMESTAMP, 4, 5);
-- Pedido 2 para o usuário 5 com status "CANCELED"
INSERT INTO tb_orders (moment, order_status, client_id) VALUES (CURRENT_TIMESTAMP, 5, 5);

-- Pedido 1 para o usuário 6 com status "WAITING_PAYMENT"
INSERT INTO tb_orders (moment, order_status, client_id) VALUES (CURRENT_TIMESTAMP, 1, 6);
-- Pedido 2 para o usuário 6 com status "PAID"
INSERT INTO tb_orders (moment, order_status, client_id) VALUES (CURRENT_TIMESTAMP, 2, 6);

-- Pedido 1 para o usuário 7 com status "SHIPPED"
INSERT INTO tb_orders (moment, order_status, client_id) VALUES (CURRENT_TIMESTAMP, 3, 7);
-- Pedido 2 para o usuário 7 com status "DELIVERED"
INSERT INTO tb_orders (moment, order_status, client_id) VALUES (CURRENT_TIMESTAMP, 4, 7);

-- Pedido 1 para o usuário 8 com status "CANCELED"
INSERT INTO tb_orders (moment, order_status, client_id) VALUES (CURRENT_TIMESTAMP, 5, 8);
-- Pedido 2 para o usuário 8 com status "WAITING_PAYMENT"
INSERT INTO tb_orders (moment, order_status, client_id) VALUES (CURRENT_TIMESTAMP, 1, 8);

-- Pedido 1 para o usuário 9 com status "SHIPPED"
INSERT INTO tb_orders (moment, order_status, client_id) VALUES (CURRENT_TIMESTAMP, 3, 9);
-- Pedido 2 para o usuário 9 com status "PAID"
INSERT INTO tb_orders (moment, order_status, client_id) VALUES (CURRENT_TIMESTAMP, 2, 9);

-- Pedido 1 para o usuário 10 com status "DELIVERED"
INSERT INTO tb_orders (moment, order_status, client_id) VALUES (CURRENT_TIMESTAMP, 4, 10);
-- Pedido 2 para o usuário 10 com status "CANCELED"
INSERT INTO tb_orders (moment, order_status, client_id) VALUES (CURRENT_TIMESTAMP, 5, 10);
