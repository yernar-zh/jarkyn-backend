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

CREATE OR REPLACE FUNCTION search(search TEXT, pattern TEXT)
    RETURNS BOOLEAN AS
$$
DECLARE
    ts_query TEXT;
BEGIN
    ts_query := regexp_replace(remove_symbols(pattern), '\s+', ':* & ', 'ig') || ':*';
    RETURN to_tsvector('russian', search) @@ to_tsquery(ts_query);
END;
$$ LANGUAGE plpgsql IMMUTABLE;
