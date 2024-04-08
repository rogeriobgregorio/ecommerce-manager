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
### User

- Listar todos os usuários paginados.
- Criar um novo usuário.
- Encontrar um usuário pelo ID.
- Atualizar um usuário.
- Excluir um usuário.
- Encontrar usuários pelo nome paginados.
- Salvar o endereço de um usuário.

### Address

- Listar todos os endereços paginados.
- Criar um novo endereço.
- Encontrar um endereço pelo ID.
- Atualizar um endereço.
- Excluir um endereço.

### Category

- Listar todas as categorias paginadas.
- Criar uma nova categoria.
- Encontrar uma categoria pelo ID.
- Encontrar todas as categorias por IDs.
- Atualizar uma categoria.
- Excluir uma categoria.
- Encontrar categorias pelo nome paginadas.

### InventoryItem

- Listar todos os itens de inventário paginados.
- Criar um novo item de inventário.
- Encontrar um item de inventário pelo ID.
- Atualizar um item de inventário.
- Excluir um item de inventário.
- Encontrar um item de inventário pelo produto.
- Verificar se uma lista de itens está disponível.
- Atualizar a quantidade de um item de inventário.
- Verificar se um item está disponível em um pedido.

### OrderItem

- Listar todos os itens de pedido paginados.
- Criar um novo item de pedido.
- Encontrar um item de pedido pelo ID do pedido e ID do produto.
- Atualizar um item de pedido.
- Excluir um item de pedido.

### Order

- Listar todos os pedidos paginados.
- Criar um novo pedido.
- Salvar um pedido pago.
- Encontrar um pedido pelo ID.
- Atualizar um pedido.
- Excluir um pedido.
- Encontrar pedidos pelo ID do cliente paginados.

### Payment

- Listar todos os pagamentos paginados.
- Criar um novo pagamento.
- Encontrar um pagamento pelo ID.
- Excluir um pagamento.

### Product

- Listar todos os produtos paginados.
- Criar um novo produto.
- Encontrar um produto pelo ID.
- Atualizar um produto.
- Excluir um produto.
- Encontrar produtos pelo nome paginados.

### StockMovement

- Listar todos os movimentos de estoque paginados.
- Criar um novo movimento de estoque.
- Encontrar um movimento de estoque pelo ID.
- Atualizar um movimento de estoque.
- Excluir um movimento de estoque.
- Atualizar a saída de um movimento de estoque com base em um pedido.

