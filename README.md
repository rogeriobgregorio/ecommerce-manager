# E-Commerce Manager
[![Status](https://img.shields.io/badge/Status-Em&nbsp;Desenvolvimento-yellow.svg)](https://github.com/seu-usuario/seu-projeto)

O **E-Commerce Manager** é uma API REST robusta para gestão de E-Commerce. Construída em Java e Ecossistema Spring, 
prioriza código limpo e implementação de princípios SOLID, além de Design Patterns para criar um software flexível, 
escalável e de fácil manutenção. 
Utiliza tecnologias como JPA, Hibernate, JWT, JUnit, Mockito e log4j2 para garantir a qualidade e eficiência do sistema.

## Problema a ser solucionado:

Este software visa resolver desafios relacionados à gestão de e-commerce, fornecendo funcionalidades essenciais para 
gerenciamento de:

**Cadastro de Usuários e Administradores:**
   - Cadastro de administradores
   - Cadastro de usuários
   - Login seguro

**Gestão de Produtos e Inventário:**
   - Cadastro de categorias para os produtos
   - Cadastro dos itens de inventário
   - Cadastro de produtos

**Gestão de Pedidos e Pagamentos:**
   - Itens de pedido
   - Pagamentos
   - Pedidos

**Gestão de Promoções e Descontos:**
   - Desconto em produtos
   - Cupons de desconto

**Gestão de Entrega e Estoque:**
   - Cadastro dos endereços de entrega
   - Movimentações de estoque

**Comunicação e Feedback:**
   - Feedback dos usuários acerca dos produtos
   - Notificações


## Modelo Conceitual

**Diagrama Entidade-Relacionamento**
![der-ecommerce-manager](https://github.com/rogeriobgregorio/ecommerce-manager/raw/main/diagrams/der-ecommerce-manager.png)

## Funcionalidades implementadas até o momento

| Descrição                                   | Método HTTP | Endpoint                               | Autorizações           |
|---------------------------------------------|-------------|----------------------------------------|------------------------|
| Registro de usuário                         | POST        | /api/v1/register                       | Não Requer Autorização |
| Login de autenticação                       | POST        | /api/v1/login                          | ADMIN, MANAGER, CLIENT |
| Listar endereços                            | GET         | /api/v1/addresses                      | ADMIN, MANAGER         |
| Adicionar endereço                          | POST        | /api/v1/addresses                      | ADMIN, MANAGER, CLIENT |
| Atualizar endereço                          | PUT         | /api/v1/addresses                      | ADMIN, MANAGER, CLIENT |
| Excluir endereço                            | DELETE      | /api/v1/addresses/{id}                 | ADMIN, MANAGER, CLIENT |
| Detalhes de um endereço específico          | GET         | /api/v1/addresses/{id}                 | ADMIN, MANAGER, CLIENT |
| Listar categorias                           | GET         | /api/v1/categories                     | ADMIN, MANAGER, CLIENT | 
| Adicionar categoria                         | POST        | /api/v1/categories                     | ADMIN, MANAGER         |
| Atualizar categoria                         | PUT         | /api/v1/categories                     | ADMIN, MANAGER         |
| Excluir categoria                           | DELETE      | /api/v1/categories/{id}                | ADMIN                  |
| Detalhes de uma categoria específica        | GET         | /api/v1/categories/{id}                | ADMIN, MANAGER, CLIENT |
| Pesquisar categoria por nome                | GET         | /api/v1/categories/search?name={name}  | ADMIN, MANAGER, CLIENT |
| Listar itens de inventário                  | GET         | /api/v1/inventory-items                | ADMIN, MANAGER         |
| Adicionar item de inventário                | POST        | /api/v1/inventory-items                | ADMIN, MANAGER         |
| Atualizar item de inventário                | PUT         | /api/v1/inventory-items                | ADMIN, MANAGER         |
| Excluir item de inventário                  | DELETE      | /api/v1/inventory-items/{id}           | ADMIN                  |
| Detalhes de um item de inventário           | GET         | /api/v1/inventory-items/{id}           | ADMIN, MANAGER         |
| Listar pedidos                              | GET         | /api/v1/orders                         | ADMIN, MANAGER         |
| Adicionar pedido                            | POST        | /api/v1/orders                         | ADMIN, MANAGER, CLIENT |
| Atualizar pedido                            | PUT         | /api/v1/orders                         | ADMIN, MANAGER, CLIENT |
| Atualizar apenas status do pedido           | PUT         | /api/v1/orders/status                  | ADMIN, MANAGER         |
| Excluir pedido                              | DELETE      | /api/v1/orders/{id}                    | ADMIN, MANAGER, CLIENT |
| Detalhes de um pedido específico            | GET         | /api/v1/orders/{id}                    | ADMIN, MANAGER         |
| Listar pedidos de um CLIENT                 | GET         | /api/v1/clients/{id}/orders            | ADMIN, MANAGER, CLIENT |
| Listar itens de pedido                      | GET         | /api/v1/order-items                    | ADMIN, MANAGER, CLIENT |
| Adicionar item de pedido                    | POST        | /api/v1/order-items                    | ADMIN, MANAGER, CLIENT |
| Atualizar item de pedido                    | PUT         | /api/v1/order-items                    | ADMIN, MANAGER, CLIENT |
| Excluir item de pedido                      | DELETE      | /api/v1/order-items/{id}               | ADMIN, MANAGER, CLIENT |
| Detalhes de um item de pedido específico    | GET         | /api/v1/order-items/{orderId}/{itemId} | ADMIN, MANAGER, CLIENT |
| Listar pagamentos                           | GET         | /api/v1/payments                       | ADMIN, MANAGER         |
| Adicionar pagamento                         | POST        | /api/v1/payments                       | ADMIN, MANAGER, CLIENT |
| Excluir pagamento                           | DELETE      | /api/v1/payments/{id}                  | ADMIN                  |
| Detalhes de um pagamento específico         | GET         | /api/v1/payments/{id}                  | ADMIN, MANAGER         |
| Listar produtos                             | GET         | /api/v1/products                       | ADMIN, MANAGER, CLIENT |
| Adicionar produto                           | POST        | /api/v1/products                       | ADMIN, MANAGER         |
| Atualizar produto                           | PUT         | /api/v1/products                       | ADMIN, MANAGER         |
| Excluir produto                             | DELETE      | /api/v1/products/{id}                  | ADMIN                  |
| Detalhes de um produto específico           | GET         | /api/v1/products/{id}                  | ADMIN, MANAGER, CLIENT |
| Pesquisar produto por nome                  | GET         | /api/v1/products/search?name={name}    | ADMIN, MANAGER, CLIENT |
| Listar movimentações de estoque             | GET         | /api/v1/stock-movements                | ADMIN, MANAGER         |
| Adicionar movimentação de estoque           | POST        | /api/v1/stock-movements                | ADMIN, MANAGER         |
| Atualizar movimentação de estoque           | PUT         | /api/v1/stock-movements                | ADMIN, MANAGER         |
| Excluir movimentação de estoque             | DELETE      | /api/v1/stock-movements                | ADMIN                  |
| Detalhes de uma movimentação de estoque     | GET         | /api/v1/stock-movements/{id}           | ADMIN, MANAGER         |
| Listar usuários                             | GET         | /api/v1/users                          | ADMIN, MANAGER         |
| Adicionar usuário                           | POST        | /api/v1/users                          | ADMIN, MANAGER, CLIENT |
| Atualizar usuário                           | PUT         | /api/v1/users                          | ADMIN, MANAGER, CLIENT |
| Excluir usuário                             | DELETE      | /api/v1/users/{id}                     | ADMIN, MANAGER, CLIENT |
| Detalhes de um usuário específico           | GET         | /api/v1/users/{id}                     | ADMIN, MANAGER, CLIENT |
| Pesquisar usuário por nome                  | GET         | /api/v1/users/search?name={name}       | ADMIN, MANAGER         |
| Adicionar função de usuário                 | POST        | /api/v1/users/roles                    | ADMIN                  |
| Listar cupons de desconto                   | GET         | /api/v1/discount-coupons               | ADMIN, MANAGER, CLIENT |
| Criar cupom de desconto                     | POST        | /api/v1/discount-coupons               | ADMIN, MANAGER         |
| Detalhes de um cupom de desconto específico | GET         | /api/v1/discount-coupons{id}           | ADMIN, MANAGER, CLIENT |
| Atualizar cupom de desconto                 | PUT         | /api/v1/discount-coupons               | ADMIN, MANAGER         |
| Excluir cupom de desconto                   | DELETE      | /api/v1/discount-coupons{id}           | ADMIN, MANAGER         |
| Listar notificações                         | GET         | /api/v1/notifications                  | ADMIN, MANAGER, CLIENT |
| Adicionar notificação                       | POST        | /api/v1/notifications                  | ADMIN, MANAGER, CLIENT |
| Atualizar notificação                       | PUT         | /api/v1/notifications                  | ADMIN, MANAGER, CLIENT |
| Excluir notificação                         | DELETE      | /api/v1/notifications/{id}             | ADMIN, MANAGER, CLIENT |
| Detalhes de uma notificação específica      | GET         | /api/v1/notifications/{id}             | ADMIN, MANAGER, CLIENT |