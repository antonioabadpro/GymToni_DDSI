/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vista;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * @version 05/02/25
 * @author Antonio Abad Hernandez Galvez
 * Se encarga de Mostrar los elementos por Consola o por Pantalla
 */
public class VistaMensaje
{
    
    /**
     * Se utiliza para mostrar los mensajes por la Consola en vez de hacer un System.out.println()
     * @param s Es la cadena de caracteres que queremos mostrar por Consola
     */
    public void mostrar(String s)
    {
        System.out.print(s);
    }
    
    /**
     * Muestra un mensaje de tipo 'JOptionPane'
     * @param c Es el Componente que queremos que quede por detras del JOptionPane que queremos mostrar (suele ser 'null' o un objeto de alguna clase 'Vista')
     * @param mensaje Es el mensaje que queremos mostrar
     * @param titulo_ventana Es el titulo que queremos que tenga el Frame del 'JOptionPane'
     * @param tipo_mensaje Es el tipo de mensaje que queremos mostrar (INFO o ERROR)
     */
    public void mensaje(Component c, String mensaje, String titulo_ventana, String tipo_mensaje)
    {
        switch(tipo_mensaje)
        {
            case "info":
            case "INFO": // Muestra un mensaje de informacion (icono info)
            {
                JOptionPane.showMessageDialog(c, mensaje, titulo_ventana, JOptionPane.INFORMATION_MESSAGE);
            }; break;
            
            case "error":
            case "ERROR": // Muestra un mensaje de error (icono X)
            {
                JOptionPane.showMessageDialog(c, mensaje, titulo_ventana, JOptionPane.ERROR_MESSAGE);
            }; break;
            
            case "cancelar":
            case "CANCELAR": 
            {
                JOptionPane.showMessageDialog(c, mensaje, titulo_ventana, JOptionPane.CANCEL_OPTION);
            }; break;
            
            case "normal":
            case "NORMAL": // Muestra un mensaje sin icono
            {
                JOptionPane.showMessageDialog(c, mensaje, titulo_ventana, JOptionPane.DEFAULT_OPTION);
            }; break;
            
            case "aviso":
            case "AVISO": // Muestra un mensaje de warning (icono warning)
            {
                JOptionPane.showMessageDialog(c, mensaje, titulo_ventana, JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    /**
     * Muestra un mensaje de confirmacion de tipo 'JOptionPane' que debe mostrarse por encima de la Vista
     * @param c Es el Componente que queremos que quede por detras del JOptionPane que queremos mostrar (objeto de alguna clase 'Vista', NO puede ser 'null')
     * @param mensaje Es el mensaje que queremos mostrar
     * @param titulo_ventana Es el titulo que queremos que tenga el Frame del 'JOptionPane'
     * @return Devuelve 0 al pulsar 'YES', devuelve 1 al pulsar 'NO', devuelve -1 si NO se selecciona ninguna opcion
     */
    public int mensajeConfirmacion(Component c, String mensaje, String titulo_ventana)
    {
        int opcion_seleccionada;
        
        opcion_seleccionada=JOptionPane.showConfirmDialog(c, mensaje, titulo_ventana, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        return opcion_seleccionada;
    }
};
