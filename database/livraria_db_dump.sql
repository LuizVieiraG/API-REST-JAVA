CREATE DATABASE IF NOT EXISTS livraria_db;
USE livraria_db;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS livro_autor;
DROP TABLE IF EXISTS livros;
DROP TABLE IF EXISTS autores;

CREATE TABLE autores (
  id BIGINT NOT NULL AUTO_INCREMENT,
  email VARCHAR(255),
  nome VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE livros (
  id BIGINT NOT NULL AUTO_INCREMENT,
  isbn VARCHAR(255) NOT NULL,
  preco DECIMAL(10, 2) NOT NULL,
  titulo VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_livros_isbn (isbn)
);

CREATE TABLE livro_autor (
  livro_id BIGINT NOT NULL,
  autor_id BIGINT NOT NULL,
  PRIMARY KEY (livro_id, autor_id),
  CONSTRAINT fk_livro_autor_livro
    FOREIGN KEY (livro_id) REFERENCES livros (id),
  CONSTRAINT fk_livro_autor_autor
    FOREIGN KEY (autor_id) REFERENCES autores (id)
);

INSERT INTO autores (id, nome, email) VALUES
  (37, 'Machado de Assis', 'machado@literatura.br'),
  (38, 'Jorge Amado', 'jorge@literatura.br'),
  (39, 'Clarice Lispector', 'clarice@literatura.br'),
  (40, 'Jose Saramago', 'saramago@literatura.pt');

INSERT INTO livros (id, titulo, isbn, preco) VALUES
  (16, 'Dom Casmurro', '978-85-359-0277-5', 39.90),
  (17, 'A Hora da Estrela', '978-85-359-0001-0', 29.90),
  (18, 'Capitaes da Areia', '978-85-359-0002-7', 44.90),
  (19, 'Ensaio Sobre a Cegueira', '978-85-359-0003-4', 49.90),
  (20, 'Antologia da Literatura Lusofona', '978-85-359-0004-1', 59.90);

INSERT INTO livro_autor (livro_id, autor_id) VALUES
  (16, 37),
  (17, 39),
  (18, 38),
  (19, 40),
  (20, 37),
  (20, 39),
  (20, 40);

ALTER TABLE autores AUTO_INCREMENT = 41;
ALTER TABLE livros AUTO_INCREMENT = 21;

SET FOREIGN_KEY_CHECKS = 1;
