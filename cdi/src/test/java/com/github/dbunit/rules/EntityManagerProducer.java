package com.github.dbunit.rules;

import com.github.dbunit.rules.cdi.util.EntityManagerProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;

/**
 * Created by pestano on 09/10/15.
 */
@ApplicationScoped
public class EntityManagerProducer {

    private EntityManager em;


    @Produces
    public EntityManager produce() {
        return EntityManagerProvider.instance("cdipu").em();
    }

}
