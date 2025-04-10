package org.lg.UUID;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.lg.internal.Connection.EntityManagerFactoryUtil;
import org.lg.Model.UID;
import org.lg.UUID.UID.*;

import java.util.List;

public class UUIDRepository implements UUIDRepo {
    private EntityManagerFactory managerFactory;
    private GenerateNum generateNum;
    private GenerateAlpha generateAlpha;
    private GenerateSymbol generateSymbol;
    private static volatile UUIDRepository instance;

    private UUIDRepository(){
        this.managerFactory= EntityManagerFactoryUtil.getInstance().getEntityManagerFactory();
        this.generateAlpha=new GenerateAlpha();
        this.generateNum=new GenerateNum();
        this.generateSymbol=new GenerateSymbol();
    }

    @Override
    public String generateUUID() {
        UID id=getUUID();
        if(id==null){
            id=new UID(generateSymbol.getSymbol(""),generateAlpha.getAlpha(""),"0000",false,false,false);
            save(id);

        }
        else{
            if(id.isAlphFull()){
                id.setApha(generateAlpha.getAlpha(id.getApha()));
                id.setAlphFull(false);
            }
            else if(id.isSymbolFull()){
                id.setSymbol(generateSymbol.getSymbol(id.getSymbol()));
                id.setSymbolFull(false);
            }
            else {
                id.setNum(generateNum.getNum(Integer.parseInt(id.getNum())));
            }
            if(id.getNum().equals("9999")){
                id.setSymbolFull(true);
            }
            else if(id.getSymbol().equals("++++")){
                id.setAlphFull(true);
            }
            update(id);

        }
        StringBuilder sb=new StringBuilder(id.getApha());
        sb.append('$');
        sb.append(id.getSymbol());
        sb.append('$');
        sb.append(id.getNum());

        return sb.toString();
    }


    private void save(UID uid) {
        EntityManager manager=managerFactory.createEntityManager();
        EntityTransaction et=manager.getTransaction();
        try {
            et.begin();
            manager.persist(uid);
            et.commit();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        finally {
            manager.close();
        }
    }
    private UID getUUID(){
        EntityManager em = managerFactory.createEntityManager();
        List<UID> uids = em.createQuery("SELECT u FROM UID u", UID.class).getResultList();
        em.close();
        if(uids.size()==0) return null;

        return uids.get(0);
    }
    private void update(UID uid){
        EntityManager manager=managerFactory.createEntityManager();
        EntityTransaction et=manager.getTransaction();
        try {
            et.begin();
            manager.merge(uid);
            et.commit();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        finally {
            manager.close();
        }
    }
    public static UUIDRepository getInstance(){
        if(instance==null){
            synchronized (UUIDRepository.class){
                if (instance==null){
                    instance=new UUIDRepository();
                }
            }
        }
        return instance;
    }

}
