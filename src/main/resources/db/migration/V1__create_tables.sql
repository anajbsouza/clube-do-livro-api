CREATE TABLE book (
    id                BIGINT IDENTITY(1,1) PRIMARY KEY,
    title             NVARCHAR(200)  NOT NULL,
    author            NVARCHAR(150)  NOT NULL,
    publication_year  INT CHECK (publication_year BETWEEN 1000 AND 2100),
    meeting_date      DATE           NOT NULL,
    author_present    BIT            NOT NULL DEFAULT 0,
    created_at        DATETIMEOFFSET NOT NULL DEFAULT SYSDATETIMEOFFSET()
);

-- A collation padrão do SQL Server já ignora maiúsculas: "as meninas" = "As Meninas"
CREATE UNIQUE INDEX uq_book_title_author ON book (title, author);

CREATE TABLE review (
    book_id           BIGINT         PRIMARY KEY REFERENCES book (id) ON DELETE CASCADE,
    reading_status    NVARCHAR(20)   NOT NULL
                      CHECK (reading_status IN ('READ', 'DID_NOT_FINISH', 'NOT_READ')),
    attended_meeting  BIT            NOT NULL DEFAULT 0,
    rating            INT CHECK (rating BETWEEN 1 AND 5),
    comment           NVARCHAR(2000),
    updated_at        DATETIMEOFFSET NOT NULL DEFAULT SYSDATETIMEOFFSET()
);