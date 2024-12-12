# Disciplina: Programação Orientada a Objetos Ano: 2024

## Trabalho Prático Final - Cinema

**Valor: 15 pontos**

### Objetivo:
Aplicar os conceitos de polimorfismo, herança, sobrecarga, interfaces, modificadores de acesso, exceções, coleções e armazenamento secundário em Java para modelar um cinema simples.

---

### Enunciado:
Crie um programa em Java que simule um cinema. O cinema terá diferentes salas, cada sala com filmes em cartaz.

---

### Requisitos:
#### 1. Classes:
- **Classe Abstrata Cinema**:
  - **Atributos**:
    - `int id` – Identificador único para o cinema.
    - `String nome` – Nome popular do cinema.
    - `String local` – Endereço do cinema.
  - **Métodos Básicos**:
    - `criarSala(...)` – Cria uma sala no cinema.
    - `listarSalas()` – Lista as salas do cinema e suas informações.
    - `listarCinemas()` – Lista todos os cinemas criados.
- **Classes Concretas que Heredem de Cinema**:
  - Crie cinemas que representem cinemas reais.
  - Cada classe representará um único cinema, não deve ser possível ter mais de uma instância da classe concreta.
- **Classe Sala**:
  - **Atributos**:
    - `String nome` – Nome da sala, único no cinema.
    - `int capacidade` – Capacidade da sala.
  - **Métodos Básicos**:
    - `criarSessao(Filme, LocalDateTime)` – Cria uma sessão para exibir um filme específico em determinado horário.
    - `listarSessoes()` – Lista as sessões de uma sala.
- **Classe Filme**:
  - **Atributos**:
    - `int id` – Identificador do filme.
    - `String nome` – Nome do filme.
    - `long duracao_s` – Duração do filme, em segundos.
  - **Métodos**:
    - Me surpreenda!
- **Classe Sessao**:
  - **Atributos**:
    - `int id` – Identificador da sessão.
    - `Sala sala` – Sala onde ocorrerá a sessão.
    - `Filme filme` – Filme em exibição.
    - `LocalDateTime dataHora` – Data e hora da sessão.
  - **Métodos**:
    - Me surpreenda!
- **Classe Venda**:
  - **Métodos**:
    - Me surpreenda!

#### 2. Polimorfismo:
- Utilize `Cinema` quando for geral.
- Crie itens específicos para cada cinema para diferenciá-los dos demais.

#### 3. Sobrecarga:
- Crie métodos sobrecarregados para facilitar a vida do usuário.
  - Exemplos:
    - `criarSessao(Filme)` - cria uma sessão com horário de início 30 minutos depois do término do último filme em sessão na sala.
    - `criarSessao(Filme, LocalDateTime horario)` – cria uma sessão para o filme em todos os dias da semana, sempre no mesmo horário.

#### 4. Modificadores de Acesso:
- Utilize modificadores de acesso adequados para os atributos e métodos das classes.
- Utilize métodos get e set para acessar e modificar atributos privados.

#### 5. Interface:
- Crie uma interface que representa cada entidade DAO e trabalhe sempre com essa interface.

#### 6. Exceções:
- Crie exceções para operações que não possam ser concluídas conforme o esperado.
  - Exceções obrigatórias:
    - `SalaOcupadaException` – Quando uma sessão tiver horário de início inferior ao término de outra sessão.
    - `IdExistenteException` – Não devem existir dois cinemas com o mesmo identificador.
    - `NomeDuplicadoException` – Não devem existir duas salas com o mesmo nome no mesmo cinema.

#### 7. Armazenamento Secundário:
- Ao iniciar o sistema, carregue os dados do arquivo para memória.
- Ao finalizar o sistema, salve os dados da memória em arquivo.

---
