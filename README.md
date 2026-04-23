# 📚 Sistema de Biblioteca — JDBC

> Continuação direta do [Sistema de Biblioteca em Java OOP](https://github.com/juniorr742/ProjetoBiblioteca).  
> Nesta fase, o armazenamento em memória é substituído por persistência real com **MySQL via JDBC**.

---

## 🔗 Contexto

O projeto anterior estabeleceu toda a lógica de negócio em Java puro — empréstimos, devoluções, cálculo de multas e separação em camadas. O código está bem estruturado e funcionando.

Agora o objetivo é dar o próximo passo: **conectar essa lógica a um banco de dados real**, sem depender de nenhum framework. Tudo feito na mão, com JDBC puro, para entender o que o Spring Data JPA vai abstrair no futuro.

---

## 🎯 Objetivos desta fase

- Substituir os contadores de ID em memória por chaves primárias reais no banco
- Implementar a camada DAO com operações de **Create, Read, Update e Delete**
- Externalizar as credenciais do banco em `db.properties` — sem senha no código
- Criar um `ConnectionFactory` responsável exclusivamente por gerenciar a conexão
- Entender na prática como o JDBC funciona antes de usar abstrações como JPA

---

## 🏗️ Arquitetura

```
src/
└── br.com.biblioteca/
    ├── config/           → Constantes do sistema
    ├── controller/       → Coordenação entre camadas
    ├── dao/              → Acesso ao banco de dados (JDBC)
    │   ├── ConnectionFactory.java
    │   ├── IDao.java
    │   ├── LivroDAO.java
    │   └── UsuarioDAO.java
    ├── factory/          → Criação de objetos
    ├── model/            → Entidades do domínio
    ├── service/          → Regras de negócio
    └── view/             → Menus e interação com o usuário

resources/
└── db.properties         → Credenciais do banco (não versionado)
```

---

## ⚙️ Configuração

### Pré-requisitos

- Java 17+
- MySQL 8+
- Driver JDBC do MySQL (`mysql-connector-j`)

### `db.properties`

Crie o arquivo `src/main/resources/db.properties` com suas credenciais:

### Banco de dados

Execute o script SQL para criar as tabelas:

```sql
CREATE DATABASE biblioteca_jdbc;

USE biblioteca_jdbc;

CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    tipo VARCHAR(20) NOT NULL
);

CREATE TABLE livros (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    autor VARCHAR(100) NOT NULL,
    disponivel BOOLEAN DEFAULT TRUE
);

CREATE TABLE emprestimos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_livro INT NOT NULL,
    data_emprestimo DATE NOT NULL,
    data_devolucao DATE,
    finalizado BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    FOREIGN KEY (id_livro) REFERENCES livros(id)
);
```

---

## 🗺️ Roadmap

- [x] Lógica de negócio em Java puro *(repositório anterior)*
- [x] Arquitetura em camadas (MVC parcial)
- [x] Injeção de dependência sem acoplamento forte
- [x] `ConnectionFactory` para gerenciar conexões
- [x] Credenciais externalizadas em `db.properties`
- [x] DAO com CRUD completo para Usuário, Livro e Empréstimo
- [x] Exceções customizadas no lugar de mensagens de console
- [ ] Migração para Spring Boot *(será desenvolvido em repositório separado)*

---

## 👨‍💻 Autor

**Francisco Junior**  
Focado em aprender as melhores práticas de engenharia de software.

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Francisco_Junior-blue?style=flat&logo=linkedin)](https://www.linkedin.com/in/francisco-magalh%C3%A3es-481a20245/)
[![GitHub](https://img.shields.io/badge/GitHub-juniorr742-black?style=flat&logo=github)](https://github.com/juniorr742)
