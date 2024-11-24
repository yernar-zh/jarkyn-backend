CREATE OR REPLACE FUNCTION remove_symbols(text TEXT)
    RETURNS TEXT AS
$$
BEGIN
    text := regexp_replace(text, '[^a-zA-Z0-9а-яА-Я]+', ' ', 'g');
    text := regexp_replace(text, '([a-zA-Z])([а-яА-Я0-9])', '\1 \2', 'g');
    text := regexp_replace(text, '([а-яА-Я])([a-zA-Z0-9])', '\1 \2', 'g');
    text := regexp_replace(text, '([0-9])([a-zA-Zа-яА-Я])', '\1 \2', 'g');
    text := trim(regexp_replace(text, '\s+', ' ', 'g'));
    RETURN text;
END;
$$ LANGUAGE plpgsql IMMUTABLE;

CREATE OR REPLACE FUNCTION search(pattern TEXT, VARIADIC texts TEXT[])
    RETURNS BOOLEAN AS
$$
DECLARE
    query    TEXT;
    document TEXT;
BEGIN
    document := remove_symbols(array_to_string(texts, ' '));
    query := regexp_replace(remove_symbols(pattern), '\s+', ':* & ', 'ig') || ':*';
    RETURN to_tsvector('russian', document) @@ to_tsquery(query);
END;
$$ LANGUAGE plpgsql IMMUTABLE;

