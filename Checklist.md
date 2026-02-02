# ☑️ Checklist de Evaluación - Proyecto DDSI (2024-25)

Este documento detalla los ítems de verificación cumplidos por el proyecto **GymToni**.

## 💻 Aspectos generales y relacionados con el código

- [x] **1.** La aplicación sigue el patrón MVC. El proyecto sólo tiene una clase con un método `main()` y está en el paquete Aplicación.
- [x] **2.** El código de los métodos de los DAO está comentado adecuadamente para entender su funcionamiento usando la herramienta javadoc.
- [x] **3.** Se ha generado un fichero html con la documentación de los métodos y se ha entregado, junto con el código del proyecto, en el fichero comprimido.
- [x] **4.** Además del Controlador de Conexión y el Controlador Principal, hace uso de otros controladores para gestionar diferentes secciones de la aplicación y, de esta forma, tener un código bien estructurado.
- [x] **5.** Utiliza consultas nombradas, HQL y nativas de SQL.
- [x] **6.** Utiliza Look and Feel para personalizar el aspecto de los componentes.

## 🔐 Acceso a la aplicación

- [x] **7.** La ventana se sitúa en el centro de la pantalla.
- [x] **8.** Se solicita el usuario y contraseña de acceso al servidor MariaDB y funciona con cualquier usuario que tenga credenciales válidas en el servidor.
- [x] **9.** Se muestra un mensaje de error si las credenciales introducidas no son correctas y posibilita una nueva entrada de valores.

## 🖥️ Pantalla principal

- [x] **10.** La ventana se sitúa en el centro de la pantalla.
- [x] **11.** Tiene correctamente integrados todos los menús.
- [x] **12.** Muestra e intercambia correctamente los paneles de "Monitores", "Socios" y "Actividades".
- [x] **13.** Al abrirse por primera vez se muestra un panel principal con un fondo personalizado.
- [x] **14.** Todas las tablas (`JTable`) están correctamente dimensionadas. Se muestran todas las columnas y los datos de las celdas no aparecen "cortados".

## 👥 Gestión de Monitores

- [x] **15.** Al pulsar el botón de "Alta" aparece una ventana modal mostrando el código del siguiente monitor en un campo no editable.
- [x] **16.** Al pulsar el botón de "Actualización" aparece una ventana modal mostrando los datos del monitor seleccionado y con el campo del código no editable. Si no hay ningún monitor seleccionado, muestra un mensaje para indicarlo.
- [x] **17.** Al pulsar el botón de "Baja" se pide confirmación antes de borrar. Si no hay ningún monitor seleccionado, muestra un mensaje para indicarlo.
- [x] **18.** Al insertar, actualizar o borrar un monitor correctamente, la aplicación vuelve a la pantalla principal y se muestra la tabla actualizada.
- [x] **19.** Los campos de tipo fecha se gestionan mediante el componente `JCalendar`.
- [x] **20.** Los mensajes de advertencia o error se muestran por delante de la ventana modal.
- [x] **21.** Captura las siguientes situaciones o excepciones, mostrando un mensaje aclaratorio si no se cumple:
  - [x] a) No permite insertar o actualizar un monitor si algún campo obligatorio en la tabla MONITOR no se ha rellenado.
  - [x] b) El campo "DNI" sólo admite cadenas de 8 dígitos y una letra mayúscula.
  - [x] c) El campo "correo" sólo admite patrones válidos (al menos xxx@xxx).
  - [x] d) El campo "teléfono" sólo admite cadenas de 9 dígitos.
  - [x] e) La fecha de entrada es anterior a la fecha actual.

## 👤 Gestión de Socios

- [x] **22.** Al pulsar el botón de "Alta" aparece una ventana modal mostrando el código del siguiente socio en un campo no editable.
- [x] **23.** Al pulsar el botón de "Actualización" aparece una ventana modal mostrando los datos del socio seleccionado y con el campo del código no editable. Si no hay ningún socio seleccionado, muestra un mensaje para indicarlo.
- [x] **24.** Al pulsar el botón de "Baja" se pide confirmación antes de borrar. Si no hay ningún socio seleccionado, muestra un mensaje para indicarlo.
- [x] **25.** Al insertar, actualizar o borrar un socio correctamente, la aplicación vuelve a la pantalla principal y se muestra la tabla actualizada.
- [x] **26.** La categoría de un socio se muestra con una lista desplegable.
- [x] **27.** Tiene implementado un filtro para buscar socios (se valorará la creatividad y la funcionalidad).
- [x] **28.** Los campos de tipo fecha se gestionan mediante el componente `JCalendar`.
- [x] **29.** Los mensajes de advertencia o error se muestran por delante de la ventana modal.
- [x] **30.** Captura las siguientes situaciones o excepciones, mostrando un mensaje aclaratorio si no se cumple:
  - [x] a) No permite insertar o actualizar un socio si algún campo obligatorio en la tabla SOCIO no se ha rellenado.
  - [x] b) El campo "DNI" sólo admite cadenas de 8 dígitos y una letra mayúscula.
  - [x] c) El campo "correo" sólo admite patrones válidos (al menos xxx@xxx).
  - [x] d) El campo "teléfono" sólo admite cadenas de 9 dígitos.
  - [x] e) La fecha de entrada es anterior a la fecha actual.
  - [x] f) Sólo se admiten socios mayores de 18 años.

## 🏋️ Gestión de Actividades

- [x] **31.** Al pulsar el botón de "Alta" aparece una ventana modal mostrando el código de la siguiente actividad en un campo no editable.
- [x] **32.** Al pulsar el botón de "Actualización" aparece una ventana modal mostrando los datos de la actividad seleccionada y con el campo del código no editable. Si no hay ninguna actividad seleccionada, muestra un mensaje para indicarlo.
- [x] **33.** Al pulsar el botón de "Baja" se pide confirmación antes de borrar. Si no hay ninguna actividad seleccionada, muestra un mensaje para indicarlo.
- [x] **34.** Al insertar, actualizar o borrar una actividad correctamente, la aplicación vuelve a la pantalla principal y se muestra la tabla actualizada.
- [x] **35.** En las ventanas de "Alta" y "Actualización", los días, las horas y los monitores responsables se gestionan mediante listas desplegables (`JComboBox`). En el desplegable de los monitores se muestra el nombre de los monitores.
- [x] **36.** Los mensajes de advertencia o error se muestran por delante de la ventana modal.
- [x] **37.** Captura las siguientes situaciones o excepciones, mostrando un mensaje aclaratorio si no se cumple:
  - [x] a) No permite insertar o actualizar una actividad si algún campo obligatorio en la tabla ACTIVIDAD no se ha rellenado.
  - [x] b) El precio debe ser un valor positivo.
  - [x] c) Un monitor no puede ser responsable de más de una actividad el mismo día y a la misma hora.

## 📝 Gestión de Altas y Bajas (Inscripciones)

- [x] **38.** La selección de los socios y de las actividades se realiza mediante listas desplegables o con cualquier otro componente, pero sin necesidad de tener que escribir, en cuadros de texto, la información de los socios y de las actividades.
- [x] **39.** Muestra mensajes para conocer el resultado de la operación, es decir, si se ha producido correctamente el alta/baja o, por el contrario, la causa por la que no se ha podido llevar a cabo el alta/baja.