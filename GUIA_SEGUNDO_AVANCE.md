# Guía del segundo avance - Stock Stone

Esta guía trae el contenido para el **documento** y el guion para el **video** que piden las reglas del segundo avance.

---

# PARTE A. DOCUMENTO

## 1. Portada

Sistema de Control de Inventario para Pulseras Artesanales (Stock Stone)
Materia: Estructura de datos | Maestra: Nelida Baron Perez | Grado: 4° Grupo: E

Equipo:
- Ramirez Pineda Lian Joel
- Linares Noriega David
- Rodriguez Sandoval Leonardo
- Flores Avilez Angel Rafael

(Copiados del primer avance. Verifiquen la escritura de cada nombre antes de entregar: según las reglas, un nombre mal escrito o ausente no recibe calificación.)

## 2. Nombre del proyecto y descripción breve

**Stock Stone** es una aplicación web (Servlets y JSP en Java) para un negocio de joyería artesanal de pulseras de piedras naturales. Permite:

- Registrar y consultar pulseras terminadas y la materia prima (cuentas, elástico, dijes, empaque).
- Registrar ventas, que descuentan automáticamente el inventario y se guardan en un historial.
- Registrar producción de pulseras, que descuenta la materia prima según una receta.
- Avisar cuando una pulsera o un insumo llega a su stock mínimo.
- Mostrar reportes: más vendidas, ventas por piedra, valuación del inventario y lista de reabastecimiento.

Hay dos roles: Administrador (acceso total) y Vendedor (consulta pulseras y registra ventas).

## 3. Selección y justificación de las estructuras de datos

| Estructura | Qué almacena | Funcionalidades donde se usa | Por qué es adecuada |
|---|---|---|---|
| **Lista (`ArrayList`)** | Pulseras, materias primas y usuarios | Inventario de pulseras, materia prima, búsqueda y filtros, login | El catálogo es pequeño, se recorre completo para listar y filtrar, y el acceso por posición y la inserción al final son directos |
| **Lista enlazada (`LinkedList`)**, dentro del TAD `ListaCronologica` | Ventas realizadas y movimientos de inventario (bitácora) | Historial de ventas, bitácora de movimientos, reportes por periodo | Cada venta nueva es más reciente que todas las anteriores; con `addFirst` se inserta al inicio en O(1) sin desplazar elementos, y el historial queda ordenado del más nuevo al más antiguo |
| **Cola (`Queue`)**, dentro del TAD `ColaAlertas` | Alertas de stock bajo (pulseras e insumos) | Contador de "Stock bajo" del Dashboard, campana, lista crítica de reabastecimiento | Las alertas se atienden en el orden en que se generaron (FIFO): la más antigua es la primera de la lista. Salen de la cola cuando el producto se repone |

## 4. Tipos Abstractos de Dato (TAD)

### TAD 1: `ListaCronologica<T>`

- **Datos:** secuencia de elementos de tipo T ordenados del más reciente al más antiguo (implementada con `LinkedList<T>`).
- **Operaciones:**

| Operación | Descripción |
|---|---|
| `agregar(T elemento)` | Inserta el elemento al inicio de la lista (el más reciente) |
| `obtenerUltimo() : T` | Devuelve el más reciente sin quitarlo; `null` si está vacía |
| `listar() : List<T>` | Devuelve la lista completa, solo lectura |
| `filtrar(condición) : List<T>` | Devuelve los elementos que cumplen la condición (por ejemplo, ventas de los últimos 7 días) |
| `primeros(n) : List<T>` | Devuelve los n elementos más recientes |
| `tamanio() : int` | Cantidad de elementos |
| `estaVacia() : boolean` | `true` si no hay elementos |

- **Comportamiento esperado:** el orden de inserción se conserva de forma inversa; nunca se borran ni modifican elementos desde fuera (historial de solo escritura). Se usa para `ListaCronologica<Venta>` (historial) y `ListaCronologica<Movimiento>` (bitácora).

### TAD 2: `ColaAlertas`

- **Datos:** cola FIFO de objetos `Alerta` (tipo, id, nombre, existencia actual, mínimo), sin alertas repetidas para el mismo elemento (implementada con `Queue<Alerta>`).
- **Operaciones:**

| Operación | Descripción |
|---|---|
| `encolar(Alerta a)` | Si el elemento no tiene alerta, la agrega al final; si ya la tiene, solo actualiza sus cantidades (conserva su lugar) |
| `verSiguiente() : Alerta` | Devuelve la alerta del frente sin quitarla |
| `atender() : Alerta` | Quita y devuelve la alerta del frente |
| `retirar(tipo, id)` | Quita la alerta de un elemento ya repuesto |
| `listar() : List<Alerta>` | Copia de las alertas en orden de llegada |
| `tamanio() : int` | Cantidad de alertas pendientes |
| `estaVacia() : boolean` | `true` si no hay alertas |

- **Comportamiento esperado:** la primera alerta generada es la primera en la lista. Cada vez que cambia una existencia, el sistema revisa: si `stock <= stockMinimo` se encola; si no, se retira.

## 5. Funcionalidades en pseudocódigo

### Funcionalidad 1: Registrar venta (ArrayList + LinkedList + Cola)

```
FUNCION registrarVenta(items, metodoPago, usuario)
    SI items esta vacio ENTONCES ERROR "Agrega al menos una pulsera"
    detalles <- lista vacia

    PARA CADA (codigo, cantidad) EN items HACER
        pulsera <- buscarPulsera(codigo)              // recorre el ArrayList de pulseras
        SI pulsera = nulo ENTONCES ERROR "La pulsera no existe"
        SI pulsera.stockActual < cantidad ENTONCES
            ERROR "Stock insuficiente: disponible X, solicitado Y"
        FIN SI
        detalles.agregar(DetalleVenta(pulsera, cantidad, pulsera.precioVenta))
    FIN PARA

    venta <- nueva Venta(nuevoId, fechaActual, detalles, metodoPago, usuario)

    PARA CADA d EN detalles HACER
        pulsera.stockActual <- pulsera.stockActual - d.cantidad
        bitacora.agregar(Movimiento("Salida", pulsera, d.cantidad))   // LinkedList: addFirst
        revisarAlertaPulsera(pulsera)                                 // Cola de alertas
    FIN PARA

    historialVentas.agregar(venta)                                    // LinkedList: addFirst
    RETORNAR venta
FIN FUNCION
```

### Funcionalidad 2: Registrar producción y alertas de stock bajo (ArrayList + LinkedList + Cola)

```
FUNCION producir(codigo, cantidad, usuario)
    pulsera <- buscarPulsera(codigo)
    SI pulsera = nulo O cantidad <= 0 ENTONCES ERROR

    PARA CADA insumo EN pulsera.receta HACER                 // primero solo verifica
        material <- buscarMaterial(insumo.idMaterial)        // recorre el ArrayList de materiales
        SI material.cantidadDisponible < insumo.cantidad * cantidad ENTONCES
            ERROR "Materia prima insuficiente"
        FIN SI
    FIN PARA

    PARA CADA insumo EN pulsera.receta HACER                 // despues descuenta
        material.cantidadDisponible <- material.cantidadDisponible - insumo.cantidad * cantidad
        bitacora.agregar(Movimiento("Salida", material, ...))
        revisarAlertaMaterial(material)
    FIN PARA

    pulsera.stockActual <- pulsera.stockActual + cantidad
    bitacora.agregar(Movimiento("Entrada", pulsera, cantidad))
    revisarAlertaPulsera(pulsera)
FIN FUNCION

FUNCION revisarAlertaPulsera(pulsera)
    SI pulsera.stockActual <= pulsera.stockMinimo ENTONCES
        colaAlertas.encolar(Alerta(pulsera))        // al final de la cola si no estaba
    SINO
        colaAlertas.retirar("Pulsera", pulsera.codigo)
    FIN SI
FIN FUNCION
```

(Si el profesor pide diagrama de flujo, estos mismos pasos se dibujan: inicio > validar > ciclo de verificación > ciclo de descuento > revisar alerta > fin.)

---

# PARTE B. VIDEO (aprox. 5 a 6 minutos)

**Antes de grabar:** reinicia Tomcat para que los datos estén en su estado inicial. Ten abierto en IntelliJ los archivos `ListaCronologica.java`, `ColaAlertas.java` y `Almacen.java`.

## Parte 1. Navegación entre pantallas (mínimo 4)

1. **Login** (`/login`): mostrar la pantalla dividida. Entrar con `admin@stockstone.com` / `admin123`.
2. **Inicio:** mostrar las 4 tarjetas. Señalar "Stock bajo = 3" y "Ventas del día".
3. Menú lateral > **Inventario de pulseras:** mostrar filtros (piedra, talla, stock) y la búsqueda.
4. Menú lateral > **Materia prima:** mostrar la tabla con estados Normal / Stock bajo.
5. Menú lateral > **Ventas:** mostrar KPIs e historial.
6. Menú lateral > **Reportes:** mostrar los 4 paneles y, en "Lista crítica de reabastecimiento", el **orden** de las alertas: 1) 003, 2) 006, 3) MP-03.

Decir al recorrer: "Pasamos de una pantalla a otra desde el menú lateral".

## Parte 2. Operación que usa estructuras de datos (con datos de ejemplo)

**A. Venta bloqueada (validación):**
Ventas > `+ Registrar venta` > pulsera `003 - Pulsera Amor Suave`, cantidad 99 > Registrar. Debe aparecer: *Stock insuficiente... disponible 3, solicitado 99*. El stock no cambia.

**B. Venta correcta (LinkedList + Cola):**
Ventas > `+ Registrar venta` > fila 1: `004 - Pulsera Calma Violeta` x 7; `+ Agregar pulsera`, fila 2: `005 - Pulsera Tierra Viva` x 1; método Efectivo. El total se calcula solo ($3,540.00). Registrar.
- Resultado: la venta nueva aparece **arriba** del historial (inserción al inicio de la lista enlazada).
- Ir a Inventario de pulseras: la 004 bajó de 10 a 3 (ahora con badge ámbar) y la 005 de 15 a 14.
- Ir a Reportes: la cola crítica ahora tiene **4** elementos y la 004 quedó **al final** (entró la última). La bitácora muestra las dos salidas.

**C. Producción (descuento de materia prima + salida de la cola):**
Inventario de pulseras > botón `+` de la `006 - Pulsera Sombra Mate` > 5 unidades.
- Resultado: stock de 006 pasa de 0 a 5 (mayor a su mínimo de 3), se descuentan MP-01 (-115), MP-07 (-5) y MP-06 (-2.5) y la alerta de la 006 **sale** de la cola. Mostrarlo en Materia prima y en Reportes (la cola baja a 3).

(Opcional, 20 segundos: cerrar sesión, entrar como `vendedor@stockstone.com` / `vendedor123` y mostrar que el menú no tiene Materia prima ni Reportes.)

## Parte 3. Explicación de la estructura (mostrar el código)

- **`ListaCronologica` (LinkedList):** "Guarda cada venta y cada movimiento. Usamos `addFirst` porque cada registro nuevo es el más reciente; insertar al inicio de una lista enlazada es O(1) y no hay que mover elementos como en un arreglo. Por eso el historial sale ordenado del más nuevo al más antiguo." Mostrar `agregar()` y la llamada en `Almacen.registrarVenta`.
- **`ColaAlertas` (Queue):** "Guarda las alertas de stock bajo. Es una cola porque se atienden en orden de llegada: la primera que se generó es la primera de la lista. Cuando un producto se repone, la alerta sale de la cola." Mostrar `encolar()`, `retirar()` y `revisarAlertaPulsera` en `Almacen`.
- **`ArrayList`:** "Guarda los catálogos de pulseras y materia prima, que se recorren completos al listar y filtrar."

---

# PARTE C. CUMPLIMIENTO DE LAS REGLAS

| Requisito | Dónde se cumple |
|---|---|
| Portada con nombres completos | Parte A.1 (verificar nombres) |
| Nombre y descripción breve | Parte A.2 |
| Selección y justificación de estructuras (arreglos, listas, pilas o colas) | Parte A.3: lista, lista enlazada y cola |
| Al menos un TAD | Parte A.4: dos TAD |
| Al menos dos funcionalidades en pseudocódigo/diagrama | Parte A.5 |
| Video: navegación entre 4+ pantallas | Video, parte 1: 6 pantallas |
| Video: operación con estructura, ya implementada, con datos de ejemplo | Video, parte 2: venta, producción y alertas |
| Video: explicación de la estructura | Video, parte 3 |
