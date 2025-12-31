package com.viatico.proyect.infrastructure.persistence;

import com.viatico.proyect.domain.repositories.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class PasswordResetTokenRepositoryImpl implements PasswordResetTokenRepository {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Long crearToken(String email, String token, int horasValidez) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_PASSWORD_RESET")
                .withProcedureName("PRC_CREAR_TOKEN")
                .declareParameters(
                        new SqlParameter("p_email", Types.VARCHAR),
                        new SqlParameter("p_token", Types.VARCHAR),
                        new SqlParameter("p_horas_validez", Types.NUMERIC),
                        new SqlOutParameter("p_id_usuario", Types.NUMERIC));

        Map<String, Object> out = call.execute(email, token, horasValidez);
        Object id = out.get("p_id_usuario");
        return id != null ? ((Number) id).longValue() : null;
    }

    @Override
    public Long validarToken(String token) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_PASSWORD_RESET")
                .withFunctionName("FNC_VALIDAR_TOKEN")
                .declareParameters(new SqlParameter("p_token", Types.VARCHAR));

        Object result = call.executeFunction(Object.class, token);
        return result != null ? ((Number) result).longValue() : null;
    }

    @Override
    public void usarToken(String token) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_PASSWORD_RESET")
                .withProcedureName("PRC_USAR_TOKEN");
        call.execute(token);
    }

    @Override
    public void actualizarPassword(Long idUsuario, String nuevaPassword) {
        String encoded = passwordEncoder.encode(nuevaPassword);
        jdbcTemplate.update("UPDATE usuarios SET password = ? WHERE id_usuario = ?", encoded, idUsuario);
    }
}
