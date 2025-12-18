-- liquibase formatted sql

-- changeset professornik:add-compared-letters-table
-- comment: Create compared_letters table
CREATE TABLE compared_letters (
    id BIGSERIAL PRIMARY KEY,
    text1 TEXT NOT NULL,
    text2 TEXT NOT NULL,
    hu_invariants DOUBLE PRECISION NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- changeset professornik:add-indexes
-- comment: Create indexes for better performance
CREATE INDEX idx_compared_letters_text1 ON compared_letters(text1);
CREATE INDEX idx_compared_letters_text2 ON compared_letters(text2);
CREATE INDEX idx_compared_letters_created_at ON compared_letters(created_at);

-- changeset professornik:add-unique-constraint
-- comment: Add unique constraint to prevent duplicate comparisons
ALTER TABLE compared_letters
ADD CONSTRAINT uk_compared_letters_texts
UNIQUE (text1, text2);