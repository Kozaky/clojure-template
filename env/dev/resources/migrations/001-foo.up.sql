CREATE TABLE COMPANY(
    ID SERIAL PRIMARY KEY NOT NULL,
    NAME TEXT NOT NULL,
    AGE INT NOT NULL,
    ADDRESS TEXT,
    SALARY REAL
);

INSERT INTO
    COMPANY (NAME, AGE, ADDRESS, SALARY)
VALUES
    (
        'Julio Gómez Campanario',
        28,
        'Oetwil am See',
        8365
    );

INSERT INTO
    COMPANY (NAME, AGE, ADDRESS, SALARY)
VALUES
    ('Bohdana Andriienko', 25, 'Oetwil am See', 9000);