package org.lg.internal.Connection;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EntityManagerFactoryUtil {
    private  final EntityManagerFactory FACTORY;
    private static volatile EntityManagerFactoryUtil instance;
    private EntityManagerFactoryUtil() {
        this.FACTORY = Persistence.createEntityManagerFactory("InventoryDB");
    }
    public  EntityManagerFactory getEntityManagerFactory() {
        return FACTORY;
    }

    public EntityManager getEntityManager() {
        return FACTORY.createEntityManager();
    }

    public static void shutdown() {
        if (EntityManagerFactoryUtil.getInstance().getEntityManagerFactory()!= null) {
            EntityManagerFactoryUtil.getInstance().getEntityManagerFactory().close();
            System.out.println("EntityManagerFactory closed.");
        }
    }
    public static EntityManagerFactoryUtil getInstance(){
        if(instance==null){
            synchronized (EntityManagerFactoryUtil.class){
                if (instance==null){
                    instance=new EntityManagerFactoryUtil();
                }
            }
        }
        return instance;
    }

    static {

        Runtime.getRuntime().addShutdownHook(new Thread(EntityManagerFactoryUtil::shutdown));
    }
}
