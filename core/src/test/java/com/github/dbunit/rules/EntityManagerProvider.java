package com.github.dbunit.rules;

/**
 * COPIED from JPA module because of maven cyclic dependencies (even with test scope)
 */

import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityManagerProvider implements TestRule {

    private static Map<String, EntityManagerProvider> providers = new ConcurrentHashMap<>();//one emf per unit

    private EntityManagerFactory emf;

    private EntityManager em;

    private EntityTransaction tx;

    private Connection conn;

    private static Logger log = LoggerFactory.getLogger(EntityManagerProvider.class);

    private EntityManagerProvider() {
    }

    public static synchronized EntityManagerProvider instance(String unitName) {
        EntityManagerProvider instance = providers.get(unitName);
        if (instance == null) {
            instance = new EntityManagerProvider();
            providers.put(unitName,instance);
        }

        try {
            instance.init(unitName);
        } catch (Exception e) {
            log.error("Could not initialize persistence unit " + unitName, e);
        }

        return instance;
    }

    private void init(String unitName) {
        if (emf == null) {
            log.debug("creating emf for unit "+unitName);
            emf = Persistence.createEntityManagerFactory(unitName);
            em = emf.createEntityManager();
            this.tx = this.em.getTransaction();
            if (em.getDelegate() instanceof Session) {
                conn = ((SessionImpl) em.unwrap(Session.class)).connection();
            } else{
                /**
                 * see here:http://wiki.eclipse.org/EclipseLink/Examples/JPA/EMAPI#Getting_a_JDBC_Connection_from_an_EntityManager
                 */
                tx.begin();
                conn = em.unwrap(Connection.class);
                tx.commit();
            }

        }
        em.clear();
        emf.getCache().evictAll();
    }


    public Connection getConnection() {
        return conn;
    }

    public EntityManager em() {
        return em;
    }

    public EntityTransaction tx() {
        return tx;
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                base.evaluate();
                em.clear();
            }

        };
    }

}