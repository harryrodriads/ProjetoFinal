# 🏥 Sistema de Gestão Hospitalar e de Serviços de Saúde

O projeto **SGHSS** tem como objetivo fornecer uma solução completa para gestão hospitalar, clínica, laboratorial e serviços de saúde em geral, atendendo às demandas da instituição **VidaPlus**.

Construído utilizando Java e o framework Spring, o sistema oferece funcionalidades de cadastro, agendamento, prontuário, gestão administrativa, videochamadas e segurança de dados, em conformidade com a LGPD.

---

## 🚀 Tecnologias Utilizadas

- **Java**
- **Spring Boot**
- **Spring Data JPA**
- **Spring Security**
- **Hibernate**
- **MySQL**
- **Lombok**
- **Thymeleaf**
- **JUnit 5**

---

## 📑 Principais Funcionalidades

### 👨‍⚕️ Pacientes
- Cadastro de dados pessoais.
- Visualização do seu prontuário.
- Agendamento e cancelamento de consultas.
- Recebimento de notificações automáticas.
- Acesso à teleconsulta (videochamadas).

### 🩺 Profissionais de Saúde
- Gestão completa da agenda de atendimentos.
- Atualização e acompanhamento de prontuários.
- Acompanhamento detalhado do histórico clínico de pacientes.

### 🏢 Administração Hospitalar
- Cadastro, gestão de pacientes e profissionais de saúde.
- Controle de internações.
- Gestão de leitos hospitalares e estoque de suprimentos.
- Geração de relatórios financeiros.

### 💻 Telemedicina
- Atendimento médico por videochamada.
- Registro de prontuários e emissão de prescrições.
- Marcação e gerenciamento de consultas presenciais e exames.

### 🔒 Segurança e Compliance
- Criptografia e proteção de dados sensíveis.
- Controle de acesso por perfis (paciente, profissional, administrador).
- Registros detalhados de auditoria.
- Adequação ao princípios da Lei Geral de Proteção de Dados (LGPD).

---

## ✅ Requisitos Não Funcionais Implementados
- **Escalabilidade**: arquitetura modularizada, suportando múltiplas unidades hospitalares.
- **Desempenho**: rapidez em consultas críticas.
- **Acessibilidade**: interface responsiva e amigável.

---

## 📂 Estrutura do Projeto

```bash
SGHSS
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.example.sghss
│   │   │       ├── config
│   │   │       ├── controller
│   │   │       ├── model
│   │   │       ├── repository
│   │   │       └── service
│   │   └── resources
│   │       ├── static
│   │       ├── templates
│   │       └── application.properties
│   └── test
│       └── java
│           └── com.example.sghss
│               └── service
├── api
├── diagrams
├── pom.xml
└── README.md
```

---

## ⚙️ Como Executar o Projeto Localmente?

### 🛠️ Pré-Requisitos:
- [Java 17+](https://www.oracle.com/java/technologies/downloads/)
- [Maven](https://maven.apache.org/)

### 🔧 Configuração e Execução:

```bash
# Clone o repositório
git clone https://github.com/harryrodriads/ProjetoFinal

# Entre na pasta do projeto
cd ProjetoFinal

# Edite o arquivo application.properties para suas credenciais de banco
spring.datasource.url=jdbc:mysql://localhost:3306/sghssdb
spring.datasource.username=root
spring.datasource.password=sua_senha

# Build do projeto
mvn clean install

# Executar aplicação
mvn spring-boot:run
```

Após executar, o projeto estará disponível em:

```
http://localhost:8080
```

---

## 🤝 Contribuições

Deseja contribuir com o projeto? Será muito bem-vindo(a):

1. Faça o Fork deste repositório.
2. Crie sua branch (`git checkout -b feature/suaFuncionalidade`).
3. Commit suas mudanças (`git commit -m 'Adiciona suaFuncionalidade'`).
4. Faça push (`git push origin feature/suaFuncionalidade`).
5. Abra uma Pull Request detalhando suas alterações.

---

> Feito com ❤️ por [Harry Rodrigues](https://github.com/harryrodriads).
