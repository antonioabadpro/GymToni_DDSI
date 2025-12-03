/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Vista.VistaMensaje;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.NoResultException;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 * @version 09/02/25
 * @author Antonio Abad Hernandez Galvez
 * Clase 'Actividad' DAO (Data Access Object)
 */
public class ActividadDAO
{
    // ********************************* ATRIBUTOS PRIVADOS *********************************
    private VistaMensaje vMensaje=null;
    
    // ********************************* METODOS PUBLICOS *********************************
    public ActividadDAO()
    {
        this.vMensaje=new VistaMensaje();
    }
    
    /**
     * Inserta una nueva Actividad dada por parametro en la BD
     * @param sesion Indica la Sesion en la que queremos insertar la nueva Actividad (Operacion CRUD)
     * @param actividad Es la Nueva Actividad que queremos insertar en la BD.
     * @throws Exception Es la excepcion que se lanza si la Actividad NO ha sido insertado correctamente en la BD.
     */
    public void insertaActividad(Session sesion, Actividad actividad) throws Exception
    {
        sesion.saveOrUpdate(actividad);
    }
    
    /**
     * Elimina una Actividad dada por parametro de la BD
     * @param sesion Indica la Sesion en la que queremos eliminar la nueva Actividad (Operacion CRUD)
     * @param actividad Es la Actividad que queremos eliminar de la BD
     * @throws Exception Es la excpecion que se lanza si la Actividad NO ha sido eliminada correctamente de la BD
     */
    public void eliminarActividad(Session sesion, Actividad actividad) throws Exception
    {
        sesion.delete(actividad);
    }
    
    /**
     * Consulta que muestra todas las Actividades que existen en la BD
     * Consulta SQL Nativo
     * @param sesion Indica la Sesion en la que queremos realizar la consulta, NO podemos cerrarla
     * @return Devuelve un array que contiene todos las Actividades de la BD
     * El array es de tipo 'Object' y no de tipo 'Actividad', ya que devolvemos varios atributos que son de diferentes tipos (int, String...)
     */
    public ArrayList<Object[]> listarActividades(Session sesion)
    {
        Query consulta;
        ArrayList<Object[]> v_actividades=new ArrayList<>();
        
        try
        {
            // Realizamos la Consulta para mostrar las Actividades que tiene un Monitor
            consulta=sesion.createNativeQuery("SELECT a.idActividad, a.nombre AS actividad, a.dia, a.hora, a.descripcion, a.precioBaseMes, m.nombre AS monitorResponsable " + 
                                            "FROM ACTIVIDAD a LEFT JOIN MONITOR m ON a.monitorResponsable=m.codMonitor"); // Consulta SQL Nativo
            v_actividades=(ArrayList<Object[]>)consulta.getResultList();
            
            if(v_actividades.isEmpty()) // Si el array de Actividades esta vacio
            {
                this.vMensaje.mensaje(null, "NO hay Actividades en la BD", "Información en 'listarActividades()'", "INFO");
            }
        }
        catch(Exception e)
        {
            this.vMensaje.mensaje(null, "ActividadDAO: Error en 'listarActividades()' -> " + e.getMessage(), "ERROR", "ERROR");
        }
        return v_actividades;
    }
    
    /**
     * Consulta que busca una Actividad dado un 'nombre' en la BD
     * Consulta HQL
     * @param sesion Es la Sesion que tenemos abierta para poder realizar la consulta, NO podemos cerrarla
     * @param nombreActividad Es el 'nombre' de la Actividad que queremos buscar
     * @return Devuelve la Actividad encontrada y 'null' si NO ha encontrado ninguna Actividad
     */
    public Actividad buscarPorNombre(Session sesion, String nombreActividad)
    {
        Actividad a=null;
        Query consulta;
        try
        {
            // Realizamos una Consulta para buscar la Actividad por su 'nombre'
            consulta = sesion.createQuery("FROM Actividad WHERE nombre = :nomActividad");
            consulta.setParameter("nomActividad", nombreActividad);
            
            a=(Actividad)consulta.getSingleResult();
        }
        catch (NoResultException e)
        {
            // Si NO se encuentra ninguna Actividad devolvemos 'null'
            a=null;
        }
        catch (Exception e)
        {
            this.vMensaje.mensaje(null, "ActividadDAO: Error en 'buscarPorNombre()' -> " + e.getMessage(), "ERROR", "ERROR");
            this.vMensaje.mostrar("\n\nActividadDAO: Error en 'buscarPorNombre()' -> " + e.getMessage());
        }
        return a;
    }

    /**
     * Consulta que comprueba si hay una Actividad con el 'nombre' introducido en la BD
     * Se utiliza para insertar una Actividad en la BD (Boton 'Nueva Actividad' en VistaActividad)
     * Consulta HQL
     * @param sesion Indica la Sesion en la que queremos realizar la consulta
     * @param nombre Es el 'nombre' de la Actividad que queremos comprobar si esta en nuestra BD
     * @return Devuelve 'true' si existe una Actividad con el 'nombre' introducido y 'false' si NO existe
     */
    public boolean existeActividad(Session sesion, String nombre)
    {
        boolean encontrado=false;
        Query consulta;
        ArrayList<Actividad> v_actividades=new ArrayList<>();
        
        try
        {
            // Realizamos la Consulta para comprobar que NO exista una Actividad en nuestra BD con el 'nombre'
            consulta=sesion.createQuery("FROM Actividad a WHERE a.nombre=:nomActividad");
            consulta.setParameter("nomActividad", nombre);
            v_actividades=(ArrayList<Actividad>)consulta.getResultList();
            
            if(v_actividades.isEmpty()==false)
            {
                encontrado=true;
            }
        }
        catch(Exception e)
        {
            this.vMensaje.mostrar("\nError en 'existeActividad()' -> " + e.getMessage() + "\n\n");
        }
        // ¡OJO! -> No podemos cerrar la Sesion, ya que sino cuando volvamos a llamar a este metodo la Sesion estara cerrada y
        // el metodo NO vuelve e abrirla y salta por el catch()
        
        return encontrado;
    }
    
    /**
     * Consulta que comprueba si un Monitor Responsable tiene asignada una actividad en un dia y a una hora en concreto
     * Se utiliza para saber si el Monitor Responsable esta ocupado o esta libre para poder asignarle una actividad en un dia y en una hora en concreto
     * Consulta HQL
     * @param sesion Indica la Sesion en la que queremos realizar la consulta
     * @param cod_monitorResponsable Es el 'codMonitor' del Monitor Responsable que queremos comprobar si tiene alguna actividad programada
     * @param dia Es el dia en el que queremos saber si el Monitor Responsable tiene alguna actividad programada
     * @param hora Es la hora a la que queremos saber si el Monitor Responsable tiene alguna actividad programada
     * @return Devuelve 'true' si el Monitor Responsable esta ocupado (tiene una actividad programada para ese dia a esa hora) o 'false' en caso contrario
     */
    public boolean compruebaMonitorResponsable(Session sesion, String cod_monitorResponsable, String dia, int hora)
    {
        boolean monitor_ocupado=false;
        Query consulta;
        
        try
        {
            consulta = sesion.createQuery("FROM Actividad a WHERE a.monitorResponsable.codMonitor = :monitorResponsable AND a.dia = :dia AND a.hora = :hora", Actividad.class); // Consulta HQL
            consulta.setParameter("monitorResponsable", cod_monitorResponsable);
            consulta.setParameter("dia", dia);
            consulta.setParameter("hora", hora);
            List<Actividad> v_actv = consulta.getResultList();
            
            if(v_actv.isEmpty()==false) // Si hay alguna Actividad, el Monitor esta ocupado
            {
                monitor_ocupado=true;
            }
        }
        catch(Exception e)
        {
            this.vMensaje.mostrar("\n\nActividadDAO: Error en 'compruebaMonitorResponsable()' -> " + e.getMessage());
            this.vMensaje.mensaje(null, "ActividadDAO: Error en 'compruebaMonitorResponsable()' -> " + e.getMessage(), "ERROR", "ERROR");
        }
        return monitor_ocupado;
    }
};
