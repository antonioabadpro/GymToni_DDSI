/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Config;

import Controlador.ControladorConexion;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * @author Antonio Abad Hernandez Galvez
 * @version 09/02/25
 */
public class HibernateUtil
{
    private static SessionFactory sessionFactory;
    private static StandardServiceRegistry serviceRegistry;

    /**
     * Crea un objeto 'SesionFactory' necesario para Iniciar Sesion en la BD
     * @return Devuelve un objeto de tipo 'SessionFactory' con el que vamos a trabajar todo el tiempo que tengamos la sesion iniciada, es decir, durante la ejecucion del programa o 'null' si los datos introducidos son incorrectos
     */
    public static SessionFactory buildSessionFactory()
    {
        try
        {
            HibernateUtil.serviceRegistry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml")
            .applySetting("hibernate.connection.username", ControladorConexion.user)
            .applySetting("hibernate.connection.password", ControladorConexion.pass)
            .applySetting("hibernate.connection.url", "jdbc:mariadb://172.18.1.241:3306/" + ControladorConexion.user).build();
            
            Metadata metadata = new MetadataSources(HibernateUtil.serviceRegistry).getMetadataBuilder().build();
            return metadata.getSessionFactoryBuilder().build();
        }
        catch (Throwable ex)
        {
            if(HibernateUtil.serviceRegistry!=null)
            {
                StandardServiceRegistryBuilder.destroy(HibernateUtil.serviceRegistry);
            }
            return null;
        }
    }

    /**
     * 
     * @return Devuelve la sesion de tipo 'SessionFactory' que tenemos abierta actualmente, si NO tenemos abierta lanza una excepcion de tipo 'IllegalStateException'
     */
    public static SessionFactory getSessionFactory()
    {
        if(HibernateUtil.sessionFactory==null)
        {
            throw new IllegalStateException("HibernateUtil: La SesionFactory aun NO esta inicializada, ya que debe llamar al metodo 'buildSessionFactoy()' primero");
        }
        return HibernateUtil.sessionFactory;
    }

    /**
     * Cierra la Sesion de tipo 'SessionFactory' con la que hemos trabajado todo el tiempo durante la ejecucion del programa
     */
    public static void close()
    {
        if ((HibernateUtil.sessionFactory != null) && (HibernateUtil.sessionFactory.isClosed() == false))
        {
            HibernateUtil.sessionFactory.close();
        }
    }
};
