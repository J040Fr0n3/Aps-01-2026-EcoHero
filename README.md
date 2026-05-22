# 🦸‍♂️ EcoHero 🌍

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/Java_Swing-007396?style=for-the-badge)
![MySQL](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)

**EcoHero** é um jogo de plataforma 2D desenvolvido inteiramente em **Java** como um projeto acadêmico de extensão universitária (UNIP). O objetivo do jogo é conscientizar as crianças sobre a importância do descarte correto de resíduos e da preservação ambiental de forma lúdica, desafiadora e interativa.

---

## 🚀 Funcionalidades Clave

* **Mecânica de Coleta Seletiva:** Colete os diferentes tipos de resíduos espalhados pelas fases e faça o descarte correto nas lixeiras correspondentes (Papel, Vidro, Metal, Plástico e Orgânico).
* **Física de Plataforma Avançada:** Movimentação fluida com pulos, colisões precisas, uso de escadas para escalada e trampolins para impulsos verticais.
* **Plataformas Voláteis (Nuvens):** Desafio dinâmico com blocos de nuvens que desaparecem temporariamente após o contato e se regeneram de forma automatizada após 5 segundos.
* **Efeito Paralaxe:** Renderização de camadas de fundo com efeito de paralaxe vertical baseado na movimentação do jogador, adicionando profundidade ao cenário.
* **Subsistema de Áudio Dinâmico:** Gerenciamento assíncrono de efeitos sonoros (SFX) para passos (adaptados ao tipo de terreno), pulos, danos e coletas, além de trilhas sonoras de fundo (BGM) contínuas para cada fase.
* **Persistência em Banco de Dados:** Tela de Score integrada a um banco de dados local para armazenar e listar o Top 10 de melhores tempos e pontuações dos jogadores, identificados por Nome e RA.
* **Modo Administrador:** Painel seguro integrado à interface para que professores ou administradores possam deletar registros do ranking diretamente pelo jogo digitando o RA do aluno.

---

## 🎮 Como Jogar

### 📌 Comandos do Teclado

| Tecla | Ação |
| :--- | :--- |
| **`A` / `D` ou Setas (Esq/Dir)** | Movimenta o EcoHero para os lados |
| **`W` / `S` ou Setas (Cima/Baixo)** | Sobe e desce escadas |
| **`ESPAÇO`** | Pula (pressione no tempo certo em trampolins para pular mais alto) |
| **`ESC`** | Pausa o jogo / Abre o menu de opções / Volta à tela anterior |
| **`ENTER`** | Confirma seleções nos menus / Inicia o jogo |

### 🎯 Objetivo e Regras
1.  **Registre-se:** Ao iniciar, insira seu **Nome** e **RA** na tela de cadastro.
2.  **Limpe o Cenário:** Explore os níveis e recolha os lixos gerados pelos *spawners*.
3.  **Descarte Corretamente:** Leve os resíduos até as lixeiras certas para acumular pontos (**PTS**). Cuidado para não errar a lixeira!
4.  **Sobreviva aos Riscos:** Evite cair na **Água Tóxica** ou no esgoto para não sofrer danos e perder vidas.
5.  **Grave seu Recorde:** Conclua todas as 4 fases no menor tempo possível para carimbar o seu nome no topo do ranking!

---

## 🛠️ Arquitetura do Sistema (Classes Principais)

O motor do jogo foi construído utilizando os conceitos de Programação Orientada a Objetos (POO), dividido nos seguintes módulos especialistas:

* **`UI.java`:** Centraliza a renderização de todas as telas do jogo, menus flutuantes, tabelas de classificação e cursor de seleção gráfica utilizando `Graphics2D`.
* **`TileManager.java` & `Tile.java`:** Carrega as matrizes dos mapas salvos em arquivos `.txt` (convertendo dados hexadecimais), define as propriedades de colisão e renderiza o cenário na tela.
* **`CloudManager.java`:** Controla de forma assíncrona o tempo de vida e o *respawn* das nuvens voláteis através de estruturas de mapeamento `HashMap`.
* **`Sound.java`:** Manipula os fluxos de áudio da API nativa do Java (`javax.sound.sampled`), separando o tratamento de loops de música de fundo de efeitos instantâneos.
* **`MapGeneratorStandalone.java`:** Ferramenta isolada criada para automatizar e agilizar o design dos níveis, gerando caixas geométricas com paredes e chãos baseadas nas dimensões da fase.

---

## 🔧 Como Executar o Projeto

### Pré-requisitos
* **Java JDK 17** ou superior instalado.
* Banco de dados configurado (conforme os parâmetros descritos na classe `DataBase.java`).

### Passo a Passo
1. Clone o repositório em sua máquina local:
   ```bash
   git clone [https://github.com/J040Fr0n3/Aps-01-2026-EcoHero.git] (https://github.com/J040Fr0n3/Aps-01-2026-EcoHero)

👥 Desenvolvedores (Equipe UNIP)
João Frone (R6591B2) — Desenvolvimento & Código

Gabriel Medeiros (R8391J4) — Desenvolvimento & Código

Hiago Müller (R857BH5) — Arquitetura de Documentação Técnica

Marcelo Agante (F364313) — Design Visual & Texturas

Eduardo Leite (R660CC3) - Arquitetura de Documentação Técnica

Henrique Lucca (H77JCB3) - Arquitetura de Documentação Técnica
