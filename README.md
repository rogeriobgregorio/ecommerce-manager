# E-Commerce Manager
[![Status](https://img.shields.io/badge/Status-Em&nbsp;Desenvolvimento-yellow.svg)](https://github.com/seu-usuario/seu-projeto)

O E-Commerce Manager é uma API REST robusta para gestão de E-Commerce. Construída em Java e Ecossistema Spring, prioriza código limpo e implementação de 
princípios SOLID, além de Design Patterns para criar um software flexível, escalável e de fácil manutenção. </br>
Utiliza tecnologias como JPA, Hibernate, JWT, JUnit, Mockito e log4j2 para garantir a qualidade e eficiência do sistema.

## Problema a ser solucionado
Este software visa resolver desafios relacionados à gestão de e-commerce, fornecendo funcionalidades essenciais para:
gerenciamento de usuários, endereços, produtos, categorias, itens de inventário, itens de pedido, pedidos, pagamentos e movimentações de estoque.

## Modelo Conceitual

**Diagrama Entidade-Relacionamento**
![der-ecommerce-manager](https://github.com/rogeriobgregorio/ecommerce-manager/raw/main/diagrams/der-ecommerce-manager.png)

## Funcionalidades implementadas até o momento

| Descrição                                | Método HTTP | Endpoint                               | Autorizações            |
|------------------------------------------|-------------|----------------------------------------|-------------------------|
| Login de autenticação                    | POST        | /v1/api/authentication/login           | NÃO REQUER AUTENTICAÇÃO |
| Registro de usuário                      | POST        | /v1/api/registration/register          | NÃO REQUER AUTENTICAÇÃO |
| Listar endereços                         | GET         | /v1/api/addresses                      | ADM, MANAGER            |
| Adicionar endereço                       | POST        | /v1/api/addresses                      | ADM, MANAGER, CLIENTE   |
| Atualizar endereço                       | PUT         | /v1/api/addresses                      | ADM, MANAGER, CLIENTE   |
| Excluir endereço                         | DELETE      | /v1/api/addresses/{id}                 | ADM, MANAGER, CLIENTE   |
| Detalhes de um endereço específico       | GET         | /v1/api/addresses/{id}                 | ADM, MANAGER, CLIENTE   |
| Listar categorias                        | GET         | /v1/api/categories                     | ADM, MANAGER, CLIENTE   | 
| Adicionar categoria                      | POST        | /v1/api/categories                     | ADM, MANAGER            |
| Atualizar categoria                      | PUT         | /v1/api/categories                     | ADM, MANAGER            |
| Excluir categoria                        | DELETE      | /v1/api/categories/{id}                | ADM                     |
| Detalhes de uma categoria específica     | GET         | /v1/api/categories/{id}                | ADM, MANAGER, CLIENTE   |
| Pesquisar categoria por nome             | GET         | /v1/api/categories/search?name={name}  | ADM, MANAGER, CLIENTE   |
| Listar itens de inventário               | GET         | /v1/api/inventory-items                | ADM, MANAGER            |
| Adicionar item de inventário             | POST        | /v1/api/inventory-items                | ADM, MANAGER            |
| Atualizar item de inventário             | PUT         | /v1/api/inventory-items                | ADM, MANAGER            |
| Excluir item de inventário               | DELETE      | /v1/api/inventory-items/{id}           | ADM                     |
| Detalhes de um item de inventário        | GET         | /v1/api/inventory-items/{id}           | ADM, MANAGER            |
| Listar pedidos                           | GET         | /v1/api/orders                         | ADM, MANAGER            |
| Adicionar pedido                         | POST        | /v1/api/orders                         | ADM, MANAGER, CLIENTE   |
| Atualizar pedido                         | PUT         | /v1/api/orders                         | ADM, MANAGER, CLIENTE   |
| Excluir pedido                           | DELETE      | /v1/api/orders/{id}                    | ADM, MANAGER, CLIENTE   |
| Detalhes de um pedido específico         | GET         | /v1/api/orders/{id}                    | ADM, MANAGER            |
| Listar pedidos de um cliente             | GET         | /v1/api/clients/{id}/orders            | ADM, MANAGER, CLIENTE   |
| Listar itens de pedido                   | GET         | /v1/api/order-items                    | ADM, MANAGER, CLIENTE   |
| Adicionar item de pedido                 | POST        | /v1/api/order-items                    | ADM, MANAGER, CLIENTE   |
| Atualizar item de pedido                 | PUT         | /v1/api/order-items                    | ADM, MANAGER, CLIENTE   |
| Excluir item de pedido                   | DELETE      | /v1/api/order-items/{id}               | ADM, MANAGER, CLIENTE   |
| Detalhes de um item de pedido específico | GET         | /v1/api/order-items/{orderId}/{itemId} | ADM, MANAGER, CLIENTE   |
| Listar pagamentos                        | GET         | /v1/api/payments                       | ADM, MANAGER            |
| Adicionar pagamento                      | POST        | /v1/api/payments                       | ADM, MANAGER, CLIENTE   |
| Excluir pagamento                        | DELETE      | /v1/api/payments/{id}                  | ADM                     |
| Detalhes de um pagamento específico      | GET         | /v1/api/payments/{id}                  | ADM, MANAGER            |
| Listar produtos                          | GET         | /v1/api/products                       | ADM, MANAGER, CLIENTE   |
| Adicionar produto                        | POST        | /v1/api/products                       | ADM, MANAGER            |
| Atualizar produto                        | PUT         | /v1/api/products                       | ADM, MANAGER            |
| Excluir produto                          | DELETE      | /v1/api/products/{id}                  | ADM                     |
| Detalhes de um produto específico        | GET         | /v1/api/products/{id}                  | ADM, MANAGER, CLIENTE   |
| Pesquisar produto por nome               | GET         | /v1/api/products/search?name={name}    | ADM, MANAGER, CLIENTE   |
| Listar movimentações de estoque          | GET         | /v1/api/stock-movements                | ADM, MANAGER            |
| Adicionar movimentação de estoque        | POST        | /v1/api/stock-movements                | ADM, MANAGER            |
| Atualizar movimentação de estoque        | PUT         | /v1/api/stock-movements                | ADM, MANAGER            |
| Excluir movimentação de estoque          | DELETE      | /v1/api/stock-movements                | ADM                     |
| Detalhes de uma movimentação de estoque  | GET         | /v1/api/stock-movements/{id}           | ADM, MANAGER            |
| Listar usuários                          | GET         | /v1/api/users                          | ADM, MANAGER            |
| Adicionar usuário                        | POST        | /v1/api/users                          | ADM, MANAGER, CLIENTE   |
| Atualizar usuário                        | PUT         | /v1/api/users                          | ADM, MANAGER, CLIENTE   |
| Excluir usuário                          | DELETE      | /v1/api/users/{id}                     | ADM, MANAGER, CLIENTE   |
| Detalhes de um usuário específico        | GET         | /v1/api/users/{id}                     | ADM, MANAGER, CLIENTE   |
| Pesquisar usuário por nome               | GET         | /v1/api/users/search?name={name}       | ADM, MANAGER            |
| Adicionar função de usuário              | POST        | /v1/api/users/roles                    | ADM                     |

