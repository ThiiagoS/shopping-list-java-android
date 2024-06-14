
# Shopping List App

Este é um aplicativo de lista de compras desenvolvido em Java utilizando o Android Studio. O aplicativo permite que os usuários criem, visualizem, editem e excluam listas de compras. As listas e os itens são armazenados em um banco de dados SQLite local.

## Funcionalidades

- **Tela Inicial:**
  - Exibe uma lista de listas de compras com nome e data.
  - Botão para adicionar novas listas de compras.

- **Adicionar Nova Lista:**
  - Permite criar uma nova lista de compras com um nome e selecionar itens de uma lista predefinida.
  - Mostra a soma total dos preços dos itens selecionados.
  - Botão para salvar a nova lista no banco de dados.

- **Visualizar Detalhes da Lista:**
  - Exibe os detalhes de uma lista de compras selecionada, mostrando os itens e seus preços.
  - Botão para editar a lista.

- **Editar Lista:**
  - Permite modificar o nome e os itens de uma lista de compras existente.
  - Mostra a soma total dos preços dos itens selecionados.
  - Botão para salvar as alterações no banco de dados.
  - Botão para excluir a lista de compras.

## Estrutura do Projeto

### Atividades

- **MainActivity:** Exibe a lista de listas de compras e fornece a funcionalidade para adicionar novas listas.
- **AddListActivity:** Permite ao usuário criar uma nova lista de compras.
- **ListDetailsActivity:** Exibe os itens de uma lista de compras selecionada.
- **EditListActivity:** Permite ao usuário editar uma lista de compras existente e excluir a lista.

### Banco de Dados

- **DatabaseHelper:** Classe auxiliar para gerenciar a criação e atualização do banco de dados SQLite. Contém as definições das tabelas e colunas.
- **Tabela `lists`:** Armazena as listas de compras com colunas para ID, nome e data.
- **Tabela `items`:** Armazena os itens das listas de compras com colunas para ID, nome, preço, quantidade e ID da lista associada.

### Layouts

- **activity_main.xml:** Layout para a tela inicial.
- **activity_add_list.xml:** Layout para adicionar uma nova lista de compras.
- **activity_list_details.xml:** Layout para exibir os detalhes de uma lista de compras.
- **activity_edit_list.xml:** Layout para editar uma lista de compras.

## Como Usar

1. **Tela Inicial:**
   - Abra o aplicativo para ver a lista de listas de compras.
   - Clique no botão "Adicionar Lista" para criar uma nova lista de compras.

2. **Adicionar Nova Lista:**
   - Insira o nome da nova lista.
   - Selecione os itens desejados.
   - Veja o total dos preços dos itens selecionados.
   - Clique no botão "Salvar" para armazenar a nova lista no banco de dados.

3. **Visualizar Detalhes da Lista:**
   - Clique em uma lista de compras na tela inicial para ver os detalhes.
   - Clique no botão "Editar Lista" para modificar ou excluir a lista.

4. **Editar Lista:**
   - Modifique o nome e os itens da lista.
   - Veja o total dos preços dos itens selecionados.
   - Clique no botão "Salvar" para atualizar a lista no banco de dados.
   - Clique no botão "Excluir" para remover a lista de compras.

## Requisitos

- Android Studio
- Emulador Android ou dispositivo físico para testes

## Configuração

1. Clone o repositório:
   \`\`\`bash
   git clone https://github.com/ThiiagoS/shopping-list-java-android.git
   \`\`\`

2. Abra o projeto no Android Studio.

3. Compile e execute o aplicativo no emulador ou dispositivo físico.
