/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @version 09/02/25
 * @author Antonio Abad Hernandez Galvez
 */
@Entity
@Table(name = "ACTIVIDAD")
@XmlRootElement

// Consultas Nombradas HQL
@NamedQueries({
    @NamedQuery(name = "Actividad.findAll", query = "SELECT a FROM Actividad a"),
    @NamedQuery(name = "Actividad.findByIdActividad", query = "SELECT a FROM Actividad a WHERE a.idActividad = :idActividad"),
    @NamedQuery(name = "Actividad.findByNombre", query = "SELECT a FROM Actividad a WHERE a.nombre = :nombre"),
    @NamedQuery(name = "Actividad.findByDia", query = "SELECT a FROM Actividad a WHERE a.dia = :dia"),
    @NamedQuery(name = "Actividad.findByHora", query = "SELECT a FROM Actividad a WHERE a.hora = :hora"),
    @NamedQuery(name = "Actividad.findByDescripcion", query = "SELECT a FROM Actividad a WHERE a.descripcion = :descripcion"),
    @NamedQuery(name = "Actividad.findByPrecioBaseMes", query = "SELECT a FROM Actividad a WHERE a.precioBaseMes = :precioBaseMes")})

public class Actividad implements Serializable {

    // ********************************* ATRIBUTOS PRIVADOS *********************************
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "idActividad")
    private String idActividad;
    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;
    @Basic(optional = false)
    @Column(name = "dia")
    private String dia;
    @Basic(optional = false)
    @Column(name = "hora")
    private int hora;
    @Column(name = "descripcion")
    private String descripcion;
    @Basic(optional = false)
    @Column(name = "precioBaseMes")
    private int precioBaseMes;
    @JoinTable(name = "REALIZA", joinColumns = {
        @JoinColumn(name = "idActividad", referencedColumnName = "idActividad")},
            inverseJoinColumns = {@JoinColumn(name = "numeroSocio", referencedColumnName = "numeroSocio")})
    @ManyToMany
    private Set<Socio> socios=new HashSet<Socio>();
    
    @JoinColumn(name = "monitorResponsable", referencedColumnName = "codMonitor")
    @ManyToOne
    private Monitor monitorResponsable;

    // ********************************* METODOS PUBLICOS *********************************
    
    /**
     * Constructor sin parametros
     */
    public Actividad()
    {
        
    }

    /**
     * Constructor con la Clave Primaria (PK)
     * @param idActividad Es la PK de la Tabla Actividad
     */
    public Actividad(String idActividad)
    {
        this.idActividad = idActividad;
    }

    /**
     * Constructor con todos los campos de la Tabla Actividad
     * @param idActividad Es el Codigo de la Actividad (Clave Primaria)
     * @param nombre Es el Nombre de la Actividad (Campo Unico y NOT NULL)
     * @param dia Es el Dia de la semana en el que se realiza la Actividad (NOT NULL)
     * @param hora Es la hora del dia en la que se realiza la Actividad (NOT NULL)
     * @param descripcion Es la descripcion adicional de la Actividad
     * @param precioBaseMes Es el Precio Mensual que tiene la Actividad (NOT NULL)
     * @param monitorResponsable Es el Monitor que se encarga de impartir la Actividad
     */
    public Actividad(String idActividad, String nombre, String dia, int hora, String descripcion, int precioBaseMes, Monitor monitorResponsable)
    {
        this.idActividad = idActividad;
        this.nombre = nombre;
        this.dia = dia;
        this.hora = hora;
        this.descripcion=descripcion;
        this.precioBaseMes = precioBaseMes;
        this.monitorResponsable=monitorResponsable;
    }

    /**
     * 
     * @return Devuelve el 'idActividad' de la Actividad
     */
    public String getIdActividad()
    {
        return idActividad;
    }

    /**
     * Establece el 'idActividad' de la Actividad
     * @param idActividad Es el codigo de la Actividad que queremos establecer
     */
    public void setIdActividad(String idActividad)
    {
        this.idActividad = idActividad;
    }

    /**
     * 
     * @return Devuelve el 'nombre' de la Actividad
     */
    public String getNombre()
    {
        return nombre;
    }

    /**
     * Establece el 'nombre' de la Actividad
     * @param nombre Es el Nombre de la Actvidad que queremos establecer
     */
    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    /**
     * 
     * @return Devuelve el 'dia' de la semana en el que se realiza la Actividad
     */
    public String getDia()
    {
        return dia;
    }

    /**
     * Establece el 'dia' de la Actividad
     * @param dia Es el Dia de la Semana de la Actividad que queremos establecer
     */
    public void setDia(String dia)
    {
        this.dia = dia;
    }

    /**
     * 
     * @return Devuelve la 'hora' del dia en la que se realiza la Actividad
     */
    public int getHora()
    {
        return hora;
    }

    /**
     * Establece la 'hora' de la Actividad
     * @param hora Es la Hora del dia de la Actividad que queremos establecer
     */
    public void setHora(int hora)
    {
        this.hora = hora;
    }

    /**
     * 
     * @return Devuelve la 'descripcion' de la Actividad
     */
    public String getDescripcion()
    {
        return descripcion;
    }

    /**
     * Establece la 'descripcion' del la Actividad
     * @param descripcion Es la Descripcion de la Actividad que queremos establecer
     */
    public void setDescripcion(String descripcion)
    {
        this.descripcion = descripcion;
    }

    /**
     * 
     * @return Devuelve el 'precioBaseMes' de la Actividad
     */
    public int getPrecioBaseMes()
    {
        return precioBaseMes;
    }

    /**
     * Establece el 'precioBase Mes' de una Actividad
     * @param precioBaseMes Es el Precio Base Mensual de la Actividad que queremos establecer
     */
    public void setPrecioBaseMes(int precioBaseMes)
    {
        this.precioBaseMes = precioBaseMes;
    }

    /**
     * 
     * @return Devuelve el 'monitorResponsable' de la Actividad
     */
    public Monitor getMonitorResponsable()
    {
        return monitorResponsable;
    }

    /**
     * Establece el 'monitorResponsable' de una Actividad
     * @param monitorResponsable Es el Monitor Responsable de la Actividad que queremos establecer
     */
    public void setMonitorResponsable(Monitor monitorResponsable)
    {
        this.monitorResponsable = monitorResponsable;
    }
    /**
     * 
     * @return Devuelve el Conjunto de Socios que estan inscritos en la Actividad
     */
    @XmlTransient
    public Set<Socio> getSocioSet()
    {
        return socios;
    }

    /**
     * Establece el Conjunto de Socios que estan inscritos en una Actividad
     * ¡OJO! -> NO añade un Socio al Conjunto de Socios de la Actividad, sino que lo establece desde 0
     * @param socios Es el Conjunto de Socios que queremos asignar a una Actividad para que este inscrito en ella
     */
    public void setSocioSet(Set<Socio> socios)
    {
        this.socios = socios;
    }

    /**
     * Calcula el código hash de la Actividad basado en el 'idActividad'
     * @return Devuelve el valor del codigo Hash del Socio
     */
    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (idActividad != null ? idActividad.hashCode() : 0);
        return hash;
    }

    /**
     * Compara si 2 Actividades son iguales en base a su Clave Primaria 'idActividad'
     * @param object Es el objeto que queremos comprobar para ver si es igual que la Actividad
     * @return Devuelve 'true' si ambos Objetos son iguales y 'false' en caso contrario
     */
    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Actividad))
        {
            return false;
        }
        Actividad other = (Actividad) object;
        if ((this.idActividad == null && other.idActividad != null) || (this.idActividad != null && !this.idActividad.equals(other.idActividad)))
        {
            return false;
        }
        return true;
    }

    /**
     * Transforma un Objeto que NO es de tipo 'String' en un 'String' con el formato "Modelo.Actividad[ idActividad=" + idActividad + " ]"
     * @return Devuelve una cadena de caracteres
     */
    @Override
    public String toString()
    {
        return "Modelo.Actividad[ idActividad=" + idActividad + " ]";
    }
    
    // ------------------------------------------------------------ METODOS AUXILIARES ------------------------------------------------------------
    /**
     * Permite insertar un Socio 's' en una nueva Actividad, es decir, dar de alta a un Socio 's' existente en el conjunto de Actividades de la BD
     * @param s Es el nuevo Socio que queremos insertar en el conjunto de Actividades de la Tabla Actividad
     */
    public void altaSocio(Socio s)
    {
        this.socios.add(s);
        s.getActividadSet().add(this);
    }
    
    /**
     * Permite eliminar un Socio 's' de una Actividad, es decir, dar de baja a un Socio 's' existente en el conjunto de Actividades de la BD
     * @param s Es el Socio que queremos eliminar del conjunto de Actividades de la Tabla Actividad
     */
    public void bajaSocio(Socio s)
    {
        this.socios.remove(s);
        s.getActividadSet().remove(this);
    }
    
};
