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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @version 09/02/25
 * @author Antonio Abad Hernandez Galvez
 */
@Entity
@Table(name = "MONITOR")
@XmlRootElement

// Consultas Nombradas HQL
@NamedQueries({
    @NamedQuery(name = "Monitor.findAll", query = "SELECT m FROM Monitor m"),
    @NamedQuery(name = "Monitor.findByCodMonitor", query = "SELECT m FROM Monitor m WHERE m.codMonitor = :codMonitor"),
    @NamedQuery(name = "Monitor.findByNombre", query = "SELECT m FROM Monitor m WHERE m.nombre = :nombre"),
    @NamedQuery(name = "Monitor.findByDni", query = "SELECT m FROM Monitor m WHERE m.dni = :dni"),
    @NamedQuery(name = "Monitor.findByTelefono", query = "SELECT m FROM Monitor m WHERE m.telefono = :telefono"),
    @NamedQuery(name = "Monitor.findByCorreo", query = "SELECT m FROM Monitor m WHERE m.correo = :correo"),
    @NamedQuery(name = "Monitor.findByFechaEntrada", query = "SELECT m FROM Monitor m WHERE m.fechaEntrada = :fechaEntrada"),
    @NamedQuery(name = "Monitor.findByNick", query = "SELECT m FROM Monitor m WHERE m.nick = :nick")})

public class Monitor implements Serializable
{

    // ********************************* ATRIBUTOS PRIVADOS *********************************
    private static final long serialVersionUID = 1L;
    @Id // Indica la PK
    @Basic(optional = false) // Como la PK es Obligatoria -> optional=false
    @Column(name = "codMonitor") // Nombre de la PK
    private String codMonitor;
    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;
    @Basic(optional = false)
    @Column(name = "dni")
    private String dni;
    @Column(name = "telefono") // Como el teléfono es opcional, por defecto -> @Basic(optional=true)
    private String telefono;
    @Column(name = "correo")
    private String correo;
    @Basic(optional = false)
    @Column(name = "fechaEntrada")
    private String fechaEntrada;
    @Column(name = "nick")
    private String nick;
    @OneToMany(mappedBy = "monitorResponsable")
    private Set<Actividad> actividadesResponsable=new HashSet<Actividad>();

    // ********************************* METODOS PUBLICOS *********************************
    /**
     * Constructor sin parametros
     */
    public Monitor()
    {
        
    }

    /**
     * Constructor con la Clave Primaria (PK)
     * @param codMonitor Es la PK de la Tabla Monitor
     */
    public Monitor(String codMonitor)
    {
        this.codMonitor = codMonitor;
    }

    /**
     * Constructor con los parametros obligatorios de la Tabla Monitor (NOT NULL)
     * @param codMonitor Es el 'codigo' del Monitor (Clave Primaria)
     * @param nombre Es el Nombre del Monitor
     * @param dni Es el DNI del Monitor (Campo Unico)
     * @param fechaEntrada Es la Fecha de Entrada del Monitor en el Gimnasio
     */
    public Monitor(String codMonitor, String nombre, String dni, String fechaEntrada)
    {
        this.codMonitor = codMonitor;
        this.nombre = nombre;
        this.dni = dni;
        this.fechaEntrada = fechaEntrada;
    }
    
    /**
     * Constructor con todos los parametros de la Tabla Montitor
     * @param codMonitor Es el Codigo del Monitor (Clave Primaria)
     * @param nombre Es el Nombre del Monitor (NOT NULL)
     * @param dni Es el DNI del Monitor (Campo Unico, NOT NULL)
     * @param telefono Es el numero de Telefono del Monitor
     * @param correo Es el correo electronico del Monitor
     * @param fechaEntrada Es la Fecha de Entrada del Monitor en el Gimnasio (NOT NULL)
     * @param nick Es el nick/apodo del Monitor
     */
    public Monitor(String codMonitor, String nombre, String dni, String telefono, String correo, String fechaEntrada, String nick)
    {
        this.codMonitor = codMonitor;
        this.nombre = nombre;
        this.dni = dni;
        this.telefono=telefono;
        this.correo=correo;
        this.fechaEntrada = fechaEntrada;
        this.nick=nick;
    }

    /**
     * 
     * @return Devuelve el 'codMonitor' del Monitor
     */
    public String getCodMonitor()
    {
        return codMonitor;
    }

    /**
     * Establece el 'codMonitor' de un Monitor
     * @param codMonitor Es el codigo del Monitor que queremos establecer
     */
    public void setCodMonitor(String codMonitor)
    {
        this.codMonitor = codMonitor;
    }
    
    /**
     * 
     * @return Devuelve el 'nombre' del Monitor
     */
    public String getNombre()
    {
        return nombre;
    }
    
    /**
     * Establece el 'nombre' del Monitor
     * @param nombre Es el Nombre del Monitor que queremos establecer
     */
    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    /**
     * 
     * @return Devuelve el 'dni' del Monitor
     */
    public String getDni()
    {
        return dni;
    }

    /**
     * Establece el 'dni' del Monitor
     * @param dni Es el DNI del Monitor que queremos establecer
     */
    public void setDni(String dni)
    {
        this.dni = dni;
    }

    /**
     * 
     * @return Devuelve el 'telefono' del Monitor
     */
    public String getTelefono()
    {
        return telefono;
    }

    /**
     * Establece el 'telefono' del Monitor
     * @param telefono Es el Numero de Telefono del Monitor que queremos establecer
     */
    public void setTelefono(String telefono)
    {
        this.telefono = telefono;
    }

    /**
     * 
     * @return Devuelve el 'correo' del Monitor
     */
    public String getCorreo()
    {
        return correo;
    }

    /**
     * Establece el 'correo' del Monitor
     * @param correo Es el Corereo Electronico del Monitor que queremos establecer
     */
    public void setCorreo(String correo)
    {
        this.correo = correo;
    }

    /**
     * 
     * @return Devuelve la 'fechaEntrada' del Monitor
     */
    public String getFechaEntrada()
    {
        return fechaEntrada;
    }

    /**
     * Establece la 'fechaEntrada' del Monitor
     * @param fechaEntrada Es la Fecha de Entrada del Monitor que queremos establecer
     */
    public void setFechaEntrada(String fechaEntrada)
    {
        this.fechaEntrada = fechaEntrada;
    }

    /**
     * 
     * @return Devuelve el 'nick' del Monitor
     */
    public String getNick()
    {
        return nick;
    }

    /**
     * Establece el 'nick' del Monitor
     * @param nick Es el nick/apodo del Monitor que queremos establecer
     */
    public void setNick(String nick)
    {
        this.nick = nick;
    }

    /**
     * 
     * @return Devuelve el Conjunto de Actividades de las que el Monitor es responsable
     */
    @XmlTransient
    public Set<Actividad> getActividadSet()
    {
        return actividadesResponsable;
    }

    /**
     * Establece un Conjunto de Actividadess de las que el Monitor es responsable
     * ¡OJO! -> NO añade una actividad al Conjunto de Actividades del Monitor, sino que lo establece desde 0
     * @param actividadesResponsable Es el Conjunto de Actividades que queremos asignar a un Monitor para que sea responsable de ellas
     */
    public void setActividadSet(Set<Actividad> actividadesResponsable)
    {
        this.actividadesResponsable = actividadesResponsable;
    }

    /**
     * Calcula el código hash del Monitor basado en el 'codMonitor'
     * @return Devuelve el valor del codigo Hash del Monitor
     */
    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (codMonitor != null ? codMonitor.hashCode() : 0);
        return hash;
    }

    /**
     * Compara si 2 Monitores son iguales en base a su Clave Primaria 'codMonitor'
     * @param object Es el objeto que queremos comprobar para ver si es igual que el Monitor
     * @return Devuelve 'true' si ambos Objetos son iguales y 'false' en caso contrario
     */
    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Monitor))
        {
            return false;
        }
        Monitor other = (Monitor) object;
        if ((this.codMonitor == null && other.codMonitor != null) || (this.codMonitor != null && !this.codMonitor.equals(other.codMonitor)))
        {
            return false;
        }
        return true;
    }

    /**
     * Transforma un Objeto que NO es de tipo 'String' en un 'String' con el formato "Modelo.Monitor[ codMonitor=" + codMonitor + " ]"
     * @return Devuelve una cadena de caracteres
     */
    @Override
    public String toString()
    {
        return "Modelo.Monitor[ codMonitor=" + codMonitor + " ]";
    }
    
};
