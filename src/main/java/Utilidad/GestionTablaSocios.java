/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utilidad;

import Modelo.Socio;
import Vista.VistaSocio;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * @version 09/02/25
 * @author Antonio Abad Hernandez Galvez
 */
public class GestionTablaSocios
{
    private static DefaultTableModel modeloTablaSocios;
    private static int numColumnas;
    
    public GestionTablaSocios()
    {
        numColumnas=8;
        modeloTablaSocios=new DefaultTableModel()
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
    }
    
    /**
     * Inicializa la Tabla de Socios y hace que las celdas de la Tabla NO sean modificables al hacer doble click sobre ellas
     * @param vSocio Es la Vista en la que queremos mostrar la Tabla ('VistaSocio')
     */
    public static void inicializarTablaSocios(VistaSocio vSocio)
    {
        vSocio.jTable_socios.setModel(modeloTablaSocios);
    }
    
    /**
     * Escribimos los valores de la Tabla (Cabecera y datos)
     * @param vSocio Es la Vista en la que queremos mostrar la Tabla ('VistaSocio')
     */
    public static void dibujarTablaSocios(VistaSocio vSocio)
    {
        String[] columnas_tabla=new String[numColumnas];
        columnas_tabla[0]="Número Socio";
        columnas_tabla[1]="Nombre";
        columnas_tabla[2]="DNI";
        columnas_tabla[3]="Fecha Nacimiento";
        columnas_tabla[4]="Teléfono";
        columnas_tabla[5]="Correo";
        columnas_tabla[6]="Fecha Incorporación";
        columnas_tabla[7]="Categoría";
        
        modeloTablaSocios.setColumnIdentifiers(columnas_tabla); // Escribimos el valor de las Columnas en la Tabla
        
        vSocio.jTable_socios.getTableHeader().setResizingAllowed(false); // Hacemos que la Tabla NO sea redimensionable
        vSocio.jTable_socios.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN); // Hacemos que la Tabla se redimensione automaticamente segun el ancho de la ultima columna
        
        // Ajustamos el Ancho de cada Columna de la Tabla
        vSocio.jTable_socios.getColumnModel().getColumn(0).setPreferredWidth(40);
        vSocio.jTable_socios.getColumnModel().getColumn(1).setPreferredWidth(240);
        vSocio.jTable_socios.getColumnModel().getColumn(2).setPreferredWidth(70);
        vSocio.jTable_socios.getColumnModel().getColumn(3).setPreferredWidth(70);
        vSocio.jTable_socios.getColumnModel().getColumn(4).setPreferredWidth(100);
        vSocio.jTable_socios.getColumnModel().getColumn(5).setPreferredWidth(150);
        vSocio.jTable_socios.getColumnModel().getColumn(6).setPreferredWidth(60);
        vSocio.jTable_socios.getColumnModel().getColumn(7).setPreferredWidth(30);
    }
    
    /**
     * Escribe en la Tabla de Socios lo que contenga el vector 'v_socios' tras realizar una consulta
     * @param v_socios Es el vector de tipo Socio que contiene los datos de los Socios que queremos mostrar en la Tabla
     */
    public static void escribirTablaSocios(ArrayList<Socio> v_socios)
    {
        Object[] filas_tabla=new Object[numColumnas];
        
        for(Socio s: v_socios)
        {
            filas_tabla[0]=s.getNumeroSocio();
            filas_tabla[1]=s.getNombre();
            filas_tabla[2]=s.getDni();
            filas_tabla[3]=s.getFechaNacimiento();
            filas_tabla[4]=s.getTelefono();
            filas_tabla[5]=s.getCorreo();
            filas_tabla[6]=s.getFechaEntrada();
            filas_tabla[7]=s.getCategoria();
            modeloTablaSocios.addRow(filas_tabla); // Añadimos la fila 'i' con los datos del Socio que hemos obtenido en el vector 'filas_tabla'
        }
    }
    
    /**
     * Elimina todas las filas de la Tabla de Socios
     * Se utiliza antes de escribir en la Tabla de Socios, ya que los datos deben haber sido borrados previamente antes de escribir
     */
    public static void borrarTablaSocios()
    {
        while(modeloTablaSocios.getRowCount()>0) // Mientras haya filas, las borramos
        {
            modeloTablaSocios.removeRow(0);
        }
    }
    
    /**
     * 
     * @return Devuelve el campo 'numeroSocio' del ultimo Socio de la BD
     */
    public static String getCodUltimo()
    {
        int numFilas=modeloTablaSocios.getRowCount();
        int fila_numeroSocio=0;
        String cod;
        
        cod=modeloTablaSocios.getValueAt(numFilas-1, fila_numeroSocio).toString();
        
        return cod;
    }
    
};
