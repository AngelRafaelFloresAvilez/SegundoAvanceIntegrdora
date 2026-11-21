# Stock Stone - Segundo avance

Sistema de control de inventario y ventas para pulseras artesanales.
Proyecto Jakarta EE (Servlets + JSP + JSTL), Maven, Java 21, Tomcat 10.1.

## Cuentas de prueba

| Rol           | Correo                    | Contraseña    |
|---------------|---------------------------|---------------|
| Administrador | admin@stockstone.com      | admin123      |
| Vendedor      | vendedor@stockstone.com   | vendedor123   |

## Cómo importarlo y ejecutarlo en IntelliJ IDEA

1. Descomprime la carpeta `SegundoAvanceIntegrdora` en `Documents\SISTEMA`.
2. En IntelliJ: **File > Open**, selecciona la carpeta `SegundoAvanceIntegrdora` (o su `pom.xml`) y elige **Open as Project**.
   La primera vez Maven descarga las dependencias (necesita internet). Espera a que termine.
3. Verifica el JDK: **File > Project Structure > Project > SDK = 21**.
4. Crea la configuración de ejecución: **Run > Edit Configurations > + > Tomcat Server > Local**.
   - **Application server:** Tomcat 10.1.57 (debe ser 10.x; Tomcat 9 no funciona con `jakarta.*`).
   - Pestaña **Deployment > + > Artifact > `SegundoAvanceIntegrdora:war exploded`**.
   - **Application context:** `/`
   - Pestaña **Server:** URL `http://localhost:8080/`
5. Presiona **Run**. Se abre el navegador en el login.

Si el artifact `war exploded` no aparece: **File > Project Structure > Artifacts > + > Web Application: Exploded > From Modules** y selecciona el módulo del proyecto.

Alternativa sin IntelliJ: `mvn clean package` y copiar `target/SegundoAvanceIntegrdora-1.0-SNAPSHOT.war` a `webapps/` de Tomcat 10.1.

## Datos

Los datos viven en memoria (listas, lista enlazada y cola) y se cargan con datos de ejemplo al arrancar.
Al reiniciar Tomcat vuelven al estado inicial, lo que sirve para repetir la demostración del video.

## Estructura

```
src/main/java/com/proyecto
  modelo/        Pulsera, MateriaPrima, Venta, DetalleVenta, Usuario, Insumo, Movimiento, Alerta, FilaReporte
  estructuras/   ListaCronologica (TAD, LinkedList)  y  ColaAlertas (TAD, Queue)
  servicio/      Almacen (reglas de negocio y datos), Util, NegocioException
  servlet/       Login, Logout, Dashboard, Pulseras, MateriaPrima, Ventas, Reportes
  filtro/        AutenticacionFilter (sesión y permisos por rol)
src/main/webapp
  css/estilos.css, js/app.js, index.jsp
  WEB-INF/vistas/  login, inicio, pulseras, materia-prima, ventas, reportes (+ fragmentos)
```

## Pantallas y permisos

| Pantalla              | URL            | Administrador | Vendedor                       |
|-----------------------|----------------|---------------|--------------------------------|
| Login                 | /login         | si            | si                             |
| Inicio                | /inicio        | todo          | sin "Valor inventario"         |
| Inventario de pulseras| /pulseras      | CRUD + producción | solo consulta              |
| Materia prima         | /materia-prima | CRUD + reabastecer | sin acceso                |
| Ventas                | /ventas        | todo          | registrar y ver historial (sin KPIs) |
| Reportes              | /reportes      | si            | sin acceso                     |

## Reglas de negocio implementadas

- Venta: valida `stockActual >= cantidad` por pulsera; si no alcanza, bloquea la venta con mensaje. Si alcanza, descuenta, guarda la venta y registra la bitácora.
- Producción: si la pulsera tiene receta (`MP-01:21, MP-06:0.5`), verifica y descuenta materia prima; si falta, no hace nada y avisa cuál.
- Alertas: cuando `stock <= stockMinimo` (pulsera o insumo) entra a la cola de alertas; cuando se repone sale de ella. El contador del Dashboard y de la campana sale de esa cola.
- Una materia prima usada en una receta no se puede eliminar.

## Estructuras de datos

| Estructura                    | Dónde                                    | Para qué                                           |
|-------------------------------|------------------------------------------|----------------------------------------------------|
| `ArrayList`                   | `Almacen`                                | Catálogos de pulseras, materia prima y usuarios    |
| `LinkedList` (TAD `ListaCronologica`) | historial de ventas y bitácora   | Lo más reciente se inserta al inicio (`addFirst`)  |
| `Queue` (TAD `ColaAlertas`)   | alertas de stock bajo                    | Orden de llegada: la alerta más antigua es la primera a atender |
