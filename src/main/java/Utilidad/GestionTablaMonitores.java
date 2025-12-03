/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utilidad;

import Modelo.Monitor;
import Vista.VistaMonitor;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * @version 09/02/25
 * @author Antonio Abad Hernandez Galvez
 */
public class GestionTablaMonitores
{
    private static DefaultTableModel modeloTablaMonitores;
    private static int numColumnas;
    
    public GestionTablaMonitores()
    {
        numColumnas=7;
        modeloTablaMonitores=new DefaultTableModel()
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
    }
    
    /**
     * Inicializa la Tabla de Monitores y hace que las celdas de la Tabla NO sean modificables al hacer doble click sobre ellas
     * @param vMonitor Es la Vista en la que queremos mostrar la Tabla ('VistaMonitor')
     */
    public static void inicializarTablaMonitores(VistaMonitor vMonitor)
    {
        vMonitor.jTable_monitores.setModel(modeloTablaMonitores);
    }
    
    /**
     * Escribimos los valores de la Tabla (Cabecera y datos)
     * @param vMonitor Es la Vista en la que queremos mostrar la Tabla ('VistaMonitor')
     */
    public static void dibujarTablaMonitores(VistaMonitor vMonitor)
    {
        //String[] columnas_tabla={"Código", "Nombre", "DNI", "Teléfono", "Correo", "Fecha Incorporación", "Nick"};
        String[] columnas_tabla=new String[numColumnas];
        columnas_tabla[0]="Código";
        columnas_tabla[1]="Nombre";
        columnas_tabla[2]="DNI";
        columnas_tabla[3]="Teléfono";
        columnas_tabla[4]="Correo";
        columnas_tabla[5]="Fecha Incorporación";
        columnas_tabla[6]="Nick";
        
        modeloTablaMonitores.setColumnIdentifiers(columnas_tabla); // Escribimos el valor de las Columnas en la Tabla
        
        vMonitor.jTable_monitores.getTableHeader().setResizingAllowed(false); // Hacemos que la Tabla NO sea redimensionable
        vMonitor.jTable_monitores.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN); // Hacemos que la Tabla se redimensione automaticamente segun el ancho de la ultima columna
        
        // Ajustamos el Ancho de cada Columna de la Tabla
        vMonitor.jTable_monitores.getColumnModel().getColumn(0).setPreferredWidth(40);
        vMonitor.jTable_monitores.getColumnModel().getColumn(1).setPreferredWidth(240);
        vMonitor.jTable_monitores.getColumnModel().getColumn(2).setPreferredWidth(70);
        vMonitor.jTable_monitores.getColumnModel().getColumn(3).setPreferredWidth(70);
        vMonitor.jTable_monitores.getColumnModel().getColumn(4).setPreferredWidth(200);
        vMonitor.jTable_monitores.getColumnModel().getColumn(5).setPreferredWidth(150);
        vMonitor.jTable_monitores.getColumnModel().getColumn(6).setPreferredWidth(60);
    }
    
    /**
     * Escribe en la Tabla de Monitores lo que contenga el vector 'v_monitores' tras realizar una consulta
     * @param v_monitores Es el vector de tipo Monitor que contiene los datos de los Monitores que queremos mostrar en la Tabla
     */
    public static void escribirTablaMonitores(ArrayList<Monitor> v_monitores)
    {
        Object[] filas_tabla=new Object[numColumnas];
        
        for(Monitor m: v_monitores)
        {
            filas_tabla[0]=m.getCodMonitor();
            filas_tabla[1]=m.getNombre();
            filas_tabla[2]=m.getDni();
            filas_tabla[3]=m.getTelefono();
            filas_tabla[4]=m.getCorreo();
            filas_tabla[5]=m.getFechaEntrada();
            filas_tabla[6]=m.getNick();
            modeloTablaMonitores.addRow(filas_tabla); // Añadimos la fila 'i' con los datos del Monitor que hemos obtenido en el vector 'filas_tabla'
        }
    }
    
    /**
     * Elimina todas las filas de la Tabla de Monitores
     * Se utiliza antes de escribir en la Tabla de Monitores, ya que los datos deben haber sido borrados previamente antes de escribir
     */
    public static void borrarTablaMonitores()
    {
        while(modeloTablaMonitores.getRowCount()>0) // Mientras haya filas, las borramos
        {
            modeloTablaMonitores.removeRow(0);
        }
    }
    
    /**
     * 
     * @return Devuelve el campo 'codMonitor' del ultimo Monitor de la BD
     */
    public static String getCodUltimo()
    {
        int numFilas=modeloTablaMonitores.getRowCount();
        int fila_codMonitor=0;
        String cod;
        
        cod=modeloTablaMonitores.getValueAt(numFilas-1, fila_codMonitor).toString();
        
        return cod;
    }
    
};
