package kz.jarkyn.backend;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import java.io.IOException;

@TestConfiguration
public class EmbeddedPostgresConfiguration {
    @Bean
    public DataSource dataSource() throws IOException {
        return EmbeddedPostgres.builder()
                .setImage(DockerImageName.parse("postgres:17.1"))
                .start().getPostgresDatabase();
    }
}