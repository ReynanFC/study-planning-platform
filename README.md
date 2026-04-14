# 📌 Task Manager API

API REST para gerenciamento de tarefas, com autenticação e controle de acesso por usuário.

Projeto desenvolvido durante um hackathon com foco em backend, segurança e boas práticas de desenvolvimento.

---

## 🚀 Funcionalidades

* Cadastro e autenticação de usuários
* Autorização com JWT
* Criptografia de senhas com BCrypt
* CRUD completo de tarefas
* Controle de acesso por usuário
* Paginação de resultados
* Persistência com banco de dados relacional

---

## 🛠 Tecnologias

* Java 21
* Spring Boot
* Spring Security
* JWT
* Spring Data JPA
* MySQL
* Docker
* Docker Compose
* Maven

---

## 🐳 Como rodar o projeto

### Pré-requisitos

* Docker instalado (com suporte a Docker Compose)

---

### Executando

```bash
docker compose up --build
```

---

### Acesso

API:

```
http://localhost:8080
```

Swagger (documentação interativa):

```
http://localhost:8080/swagger-ui.html
```

---

## 🔐 Autenticação

A API utiliza JWT para autenticação.

### Fluxo:

1. Criar usuário
2. Fazer login
3. Receber token JWT
4. Enviar token no header das requisições

Exemplo:

```
Authorization: Bearer SEU_TOKEN
```

---

## 📄 Documentação

Toda a documentação da API está disponível via Swagger no link:

```
http://localhost:8080/swagger-ui.html
```

---

## 📌 Observações

Projeto desenvolvido com foco em aprendizado prático de backend, incluindo segurança, arquitetura e containerização.

