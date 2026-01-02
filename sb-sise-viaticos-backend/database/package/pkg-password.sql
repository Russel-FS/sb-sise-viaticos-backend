-- Paquete para gestionar los tokens
CREATE OR REPLACE PACKAGE PKG_PASSWORD_RESET AS
    -- Crea un nuevo token 
    PROCEDURE PRC_CREAR_TOKEN(
        p_email IN VARCHAR2,
        p_token IN VARCHAR2,
        p_horas_validez IN NUMBER,
        p_id_usuario OUT NUMBER
    );

    -- Valida un token 
    FUNCTION FNC_VALIDAR_TOKEN(
        p_token IN VARCHAR2
    ) RETURN NUMBER;

    -- Marca un token como usado
    PROCEDURE PRC_USAR_TOKEN(
        p_token IN VARCHAR2
    );
END PKG_PASSWORD_RESET;
/

CREATE OR REPLACE PACKAGE BODY PKG_PASSWORD_RESET AS
    PROCEDURE PRC_CREAR_TOKEN(
        p_email IN VARCHAR2,
        p_token IN VARCHAR2,
        p_horas_validez IN NUMBER,
        p_id_usuario OUT NUMBER
    ) AS
    BEGIN
        -- Buscar el usuario por email
        SELECT id_usuario INTO p_id_usuario 
        FROM usuarios 
        WHERE email = p_email 
        AND activo = 1;

        -- Invalidar tokens anteriores del mismo usuario que no hayan sido usados
        UPDATE PASSWORD_RESET_TOKENS 
        SET usado = 1 
        WHERE id_usuario = p_id_usuario AND usado = 0;

        -- Insertar el nuevo token
        INSERT INTO PASSWORD_RESET_TOKENS (token, id_usuario, fecha_expiracion)
        VALUES (p_token, p_id_usuario, CURRENT_TIMESTAMP + p_horas_validez/24);
        
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_id_usuario := NULL;
    END PRC_CREAR_TOKEN;

    FUNCTION FNC_VALIDAR_TOKEN(
        p_token IN VARCHAR2
    ) RETURN NUMBER AS
        v_id_usuario NUMBER;
    BEGIN
        SELECT id_usuario INTO v_id_usuario
        FROM PASSWORD_RESET_TOKENS
        WHERE token = p_token
        AND usado = 0
        AND fecha_expiracion > CURRENT_TIMESTAMP;

        return v_id_usuario;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            return NULL;
    END FNC_VALIDAR_TOKEN;

    PROCEDURE PRC_USAR_TOKEN(
        p_token IN VARCHAR2
    ) AS
    BEGIN
        UPDATE PASSWORD_RESET_TOKENS
        SET usado = 1
        WHERE token = p_token;
    END PRC_USAR_TOKEN;
END PKG_PASSWORD_RESET;
/