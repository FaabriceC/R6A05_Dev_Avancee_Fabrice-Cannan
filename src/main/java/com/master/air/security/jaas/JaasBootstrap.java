package com.master.air.security.jaas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Helper: si la propriete JVM -Djava.security.auth.login.config n'est pas definie,
 * on copie jaas.conf depuis le classpath vers un fichier temporaire et on la configure.
 *
 * Le TP demande une config JAAS via option JVM : ce bootstrap facilite l'execution locale et les tests.
 */
public final class JaasBootstrap {

    private static final Logger log = LoggerFactory.getLogger(JaasBootstrap.class);
    private static volatile boolean done = false;

    private JaasBootstrap() {}

    public static void ensureConfigured() {
        if (done) return;
        synchronized (JaasBootstrap.class) {
            if (done) return;

            String prop = System.getProperty("java.security.auth.login.config");
            if (prop != null && !prop.isBlank()) {
                log.info("JAAS config chargee depuis JVM property: {}", prop);
                done = true;
                return;
            }

            // Fallback classpath -> temp file
            try (InputStream in = JaasBootstrap.class.getClassLoader().getResourceAsStream("jaas.conf")) {
                if (in == null) {
                    log.warn("Aucune ressource jaas.conf trouvee dans le classpath (et aucune JVM property).");
                    done = true;
                    return;
                }
                Path tmp = Files.createTempFile("masterannonce-jaas-", ".conf");
                Files.copy(in, tmp, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                tmp.toFile().deleteOnExit();
                System.setProperty("java.security.auth.login.config", tmp.toAbsolutePath().toString());
                log.info("JAAS config chargee depuis le classpath -> {}", tmp.toAbsolutePath());
            } catch (IOException e) {
                log.error("Impossible d'initialiser la config JAAS", e);
            }

            done = true;
        }
    }
}
