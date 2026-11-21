package com.proyecto.servicio;

import com.proyecto.estructuras.ColaAlertas;
import com.proyecto.estructuras.ListaCronologica;
import com.proyecto.modelo.Alerta;
import com.proyecto.modelo.DetalleVenta;
import com.proyecto.modelo.FilaReporte;
import com.proyecto.modelo.Insumo;
import com.proyecto.modelo.MateriaPrima;
import com.proyecto.modelo.Movimiento;
import com.proyecto.modelo.Pulsera;
import com.proyecto.modelo.Usuario;
import com.proyecto.modelo.Venta;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class Almacen {

    public static final List<String> TALLAS = List.of("S", "M", "L", "Ajustable");
    public static final List<String> DISENOS = List.of("Clásico", "Trenzado", "Con dije");
    public static final List<String> CATEGORIAS = List.of("Piedra/Cuenta", "Hilo/Elástico", "Dije/Herraje", "Empaque");
    public static final List<String> UNIDADES = List.of("piezas", "gramos", "metros");
    public static final List<String> METODOS_PAGO = List.of("Efectivo", "Tarjeta", "Transferencia");

    private static final Almacen INSTANCIA = new Almacen();

    public static Almacen obtener() {
        return INSTANCIA;
    }

    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Pulsera> pulseras = new ArrayList<>();
    private final List<MateriaPrima> materiales = new ArrayList<>();
    private final ListaCronologica<Venta> ventas = new ListaCronologica<>();
    private final ListaCronologica<Movimiento> bitacora = new ListaCronologica<>();
    private final ColaAlertas alertas = new ColaAlertas();
    private int contadorVentas = 0;

    private Almacen() {
        cargarDatosIniciales();
    }

    public synchronized Usuario autenticar(String correo, String password) {
        for (Usuario u : usuarios) {
            if (u.getCorreo().equalsIgnoreCase(correo) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    public synchronized List<Pulsera> listarPulseras() {
        return new ArrayList<>(pulseras);
    }

    public synchronized List<Pulsera> pulserasDisponibles() {
        List<Pulsera> resultado = new ArrayList<>();
        for (Pulsera p : pulseras) {
            if (p.getStockActual() > 0) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public synchronized List<Pulsera> recientes(int limite) {
        List<Pulsera> resultado = new ArrayList<>();
        for (int i = pulseras.size() - 1; i >= 0 && resultado.size() < limite; i--) {
            resultado.add(pulseras.get(i));
        }
        return resultado;
    }

    public synchronized List<String> piedras() {
        TreeSet<String> conjunto = new TreeSet<>();
        for (Pulsera p : pulseras) {
            conjunto.add(p.getTipoPiedra());
        }
        return new ArrayList<>(conjunto);
    }

    public synchronized List<Pulsera> filtrarPulseras(String texto, String piedra, String talla, String estado) {
        String q = minusculas(texto);
        List<Pulsera> resultado = new ArrayList<>();
        for (Pulsera p : pulseras) {
            boolean coincideTexto = q.isEmpty()
                    || p.getCodigo().toLowerCase().contains(q)
                    || p.getNombre().toLowerCase().contains(q)
                    || p.getTipoPiedra().toLowerCase().contains(q)
                    || p.getDiseno().toLowerCase().contains(q);
            if (!coincideTexto) {
                continue;
            }
            if (!vacio(piedra) && !p.getTipoPiedra().equals(piedra)) {
                continue;
            }
            if (!vacio(talla) && !p.getTalla().equals(talla)) {
                continue;
            }
            if (!vacio(estado) && !p.getEstado().equals(estado)) {
                continue;
            }
            resultado.add(p);
        }
        return resultado;
    }

    public synchronized Pulsera buscarPulsera(String codigo) {
        for (Pulsera p : pulseras) {
            if (p.getCodigo().equals(codigo)) {
                return p;
            }
        }
        return null;
    }

    public synchronized void crearPulsera(Pulsera nueva, Usuario usuario) throws NegocioException {
        validarPulsera(nueva);
        if (nueva.getStockActual() < 0) {
            throw new NegocioException("Las existencias iniciales no pueden ser negativas");
        }
        if (nueva.getCodigo().isEmpty()) {
            nueva.setCodigo(siguienteCodigoPulsera());
        } else if (buscarPulsera(nueva.getCodigo()) != null) {
            throw new NegocioException("Ya existe una pulsera con el código " + nueva.getCodigo());
        }
        pulseras.add(nueva);
        if (nueva.getStockActual() > 0) {
            registrar("Entrada", "Pulsera", nueva.getCodigo(), "Alta con existencias iniciales", nueva.getStockActual(), usuario);
        }
        revisarAlertaPulsera(nueva);
    }

    public synchronized void actualizarPulsera(Pulsera datos, Usuario usuario) throws NegocioException {
        Pulsera p = buscarPulsera(datos.getCodigo());
        if (p == null) {
            throw new NegocioException("La pulsera no existe");
        }
        validarPulsera(datos);
        p.setNombre(datos.getNombre());
        p.setTipoPiedra(datos.getTipoPiedra());
        p.setDiseno(datos.getDiseno());
        p.setTalla(datos.getTalla());
        p.setDescripcion(datos.getDescripcion());
        p.setPrecioVenta(datos.getPrecioVenta());
        p.setStockMinimo(datos.getStockMinimo());
        p.setReceta(datos.getReceta());
        revisarAlertaPulsera(p);
    }

    public synchronized void eliminarPulsera(String codigo, Usuario usuario) throws NegocioException {
        Pulsera p = buscarPulsera(codigo);
        if (p == null) {
            throw new NegocioException("La pulsera no existe");
        }
        pulseras.remove(p);
        alertas.retirar("Pulsera", codigo);
        registrar("Salida", "Pulsera", codigo, "Baja de pulsera del catálogo", p.getStockActual(), usuario);
    }

    public synchronized void producir(String codigo, int cantidad, Usuario usuario) throws NegocioException {
        Pulsera p = buscarPulsera(codigo);
        if (p == null) {
            throw new NegocioException("La pulsera no existe");
        }
        if (cantidad <= 0) {
            throw new NegocioException("La cantidad a producir debe ser mayor a cero");
        }
        for (Insumo i : p.getReceta()) {
            MateriaPrima m = buscarMaterial(i.getIdMaterial());
            if (m == null) {
                throw new NegocioException("El material " + i.getIdMaterial() + " de la receta ya no existe");
            }
            double requerido = i.getCantidadRequerida() * cantidad;
            if (m.getCantidadDisponible() < requerido) {
                throw new NegocioException("Materia prima insuficiente: " + m.getNombre() + " (requiere "
                        + Util.numero(requerido) + " y hay " + m.getCantidadTexto() + " " + m.getUnidadMedida() + ")");
            }
        }
        for (Insumo i : p.getReceta()) {
            MateriaPrima m = buscarMaterial(i.getIdMaterial());
            double requerido = i.getCantidadRequerida() * cantidad;
            m.setCantidadDisponible(Util.redondear(m.getCantidadDisponible() - requerido));
            registrar("Salida", "Materia prima", m.getId(), "Consumo por producción de " + p.getNombre(), requerido, usuario);
            revisarAlertaMaterial(m);
        }
        p.setStockActual(p.getStockActual() + cantidad);
        registrar("Entrada", "Pulsera", p.getCodigo(), "Producción de " + p.getNombre(), cantidad, usuario);
        revisarAlertaPulsera(p);
    }

    public synchronized List<MateriaPrima> listarMateriales() {
        return new ArrayList<>(materiales);
    }

    public synchronized List<MateriaPrima> filtrarMateriales(String texto, String categoria, String unidad) {
        String q = minusculas(texto);
        List<MateriaPrima> resultado = new ArrayList<>();
        for (MateriaPrima m : materiales) {
            boolean coincideTexto = q.isEmpty()
                    || m.getId().toLowerCase().contains(q)
                    || m.getNombre().toLowerCase().contains(q);
            if (!coincideTexto) {
                continue;
            }
            if (!vacio(categoria) && !m.getCategoria().equals(categoria)) {
                continue;
            }
            if (!vacio(unidad) && !m.getUnidadMedida().equals(unidad)) {
                continue;
            }
            resultado.add(m);
        }
        return resultado;
    }

    public synchronized MateriaPrima buscarMaterial(String id) {
        for (MateriaPrima m : materiales) {
            if (m.getId().equals(id)) {
                return m;
            }
        }
        return null;
    }

    public synchronized void crearMaterial(MateriaPrima nuevo, Usuario usuario) throws NegocioException {
        validarMaterial(nuevo);
        if (nuevo.getCantidadDisponible() < 0) {
            throw new NegocioException("La cantidad inicial no puede ser negativa");
        }
        if (nuevo.getId().isEmpty()) {
            nuevo.setId(siguienteIdMaterial());
        } else if (buscarMaterial(nuevo.getId()) != null) {
            throw new NegocioException("Ya existe un material con el ID " + nuevo.getId());
        }
        materiales.add(nuevo);
        if (nuevo.getCantidadDisponible() > 0) {
            registrar("Entrada", "Materia prima", nuevo.getId(), "Alta con existencias iniciales", nuevo.getCantidadDisponible(), usuario);
        }
        revisarAlertaMaterial(nuevo);
    }

    public synchronized void actualizarMaterial(MateriaPrima datos, Usuario usuario) throws NegocioException {
        MateriaPrima m = buscarMaterial(datos.getId());
        if (m == null) {
            throw new NegocioException("El material no existe");
        }
        validarMaterial(datos);
        m.setNombre(datos.getNombre());
        m.setCategoria(datos.getCategoria());
        m.setUnidadMedida(datos.getUnidadMedida());
        m.setStockMinimo(datos.getStockMinimo());
        revisarAlertaMaterial(m);
    }

    public synchronized void eliminarMaterial(String id, Usuario usuario) throws NegocioException {
        MateriaPrima m = buscarMaterial(id);
        if (m == null) {
            throw new NegocioException("El material no existe");
        }
        for (Pulsera p : pulseras) {
            for (Insumo i : p.getReceta()) {
                if (i.getIdMaterial().equals(id)) {
                    throw new NegocioException("No se puede eliminar: lo usa la receta de " + p.getNombre());
                }
            }
        }
        materiales.remove(m);
        alertas.retirar("Materia prima", id);
        registrar("Salida", "Materia prima", id, "Baja de material", m.getCantidadDisponible(), usuario);
    }

    public synchronized void reabastecer(String id, double cantidad, Usuario usuario) throws NegocioException {
        MateriaPrima m = buscarMaterial(id);
        if (m == null) {
            throw new NegocioException("El material no existe");
        }
        if (cantidad <= 0) {
            throw new NegocioException("La cantidad debe ser mayor a cero");
        }
        m.setCantidadDisponible(Util.redondear(m.getCantidadDisponible() + cantidad));
        registrar("Entrada", "Materia prima", id, "Reabastecimiento / compra a proveedor", cantidad, usuario);
        revisarAlertaMaterial(m);
    }

    public synchronized Venta registrarVenta(Map<String, Integer> items, String metodoPago, Usuario usuario) throws NegocioException {
        if (items.isEmpty()) {
            throw new NegocioException("Agrega al menos una pulsera a la venta");
        }
        if (!METODOS_PAGO.contains(metodoPago)) {
            throw new NegocioException("Selecciona un método de pago válido");
        }
        List<DetalleVenta> detalles = new ArrayList<>();
        for (Map.Entry<String, Integer> e : items.entrySet()) {
            Pulsera p = buscarPulsera(e.getKey());
            if (p == null) {
                throw new NegocioException("La pulsera " + e.getKey() + " no existe");
            }
            int cantidad = e.getValue();
            if (cantidad <= 0) {
                throw new NegocioException("La cantidad de " + p.getNombre() + " debe ser mayor a cero");
            }
            if (p.getStockActual() < cantidad) {
                throw new NegocioException("Stock insuficiente de " + p.getNombre() + ": disponible "
                        + p.getStockActual() + ", solicitado " + cantidad);
            }
            detalles.add(new DetalleVenta(p.getCodigo(), p.getNombre(), p.getTipoPiedra(), cantidad, p.getPrecioVenta()));
        }
        Venta venta = new Venta(siguienteIdVenta(), LocalDateTime.now(), detalles, metodoPago, usuario.getId());
        for (DetalleVenta d : detalles) {
            Pulsera p = buscarPulsera(d.getCodigoPulsera());
            p.setStockActual(p.getStockActual() - d.getCantidad());
            registrar("Salida", "Pulsera", p.getCodigo(), "Venta " + venta.getId(), d.getCantidad(), usuario);
            revisarAlertaPulsera(p);
        }
        ventas.agregar(venta);
        return venta;
    }

    public synchronized List<Venta> listarVentas() {
        return new ArrayList<>(ventas.listar());
    }

    public synchronized int totalPiezas() {
        int suma = 0;
        for (Pulsera p : pulseras) {
            suma += p.getStockActual();
        }
        return suma;
    }

    public synchronized double valorInventario() {
        double suma = 0;
        for (Pulsera p : pulseras) {
            suma += p.getPrecioVenta() * p.getStockActual();
        }
        return suma;
    }

    public synchronized double ventasDelDia() {
        LocalDate hoy = LocalDate.now();
        double suma = 0;
        for (Venta v : ventas.listar()) {
            if (v.getFecha().toLocalDate().equals(hoy)) {
                suma += v.getTotal();
            }
        }
        return suma;
    }

    public synchronized double ingresosTotales() {
        double suma = 0;
        for (Venta v : ventas.listar()) {
            suma += v.getTotal();
        }
        return suma;
    }

    public synchronized int numeroVentas() {
        return ventas.tamanio();
    }

    public synchronized double ticketPromedio() {
        return ventas.estaVacia() ? 0 : ingresosTotales() / ventas.tamanio();
    }

    public synchronized int cantidadAlertas() {
        return alertas.tamanio();
    }

    public synchronized List<Alerta> listarAlertas() {
        return alertas.listar();
    }

    public synchronized List<Movimiento> ultimosMovimientos(int limite) {
        return bitacora.primeros(limite);
    }

    public synchronized List<FilaReporte> topPulseras(int dias, int limite) {
        Map<String, FilaReporte> mapa = new LinkedHashMap<>();
        for (Venta v : ventasPeriodo(dias)) {
            for (DetalleVenta d : v.getDetalles()) {
                FilaReporte fila = mapa.computeIfAbsent(d.getCodigoPulsera(),
                        k -> new FilaReporte(d.getNombrePulsera(), d.getCodigoPulsera()));
                fila.acumular(d.getCantidad(), d.getSubtotal());
            }
        }
        List<FilaReporte> lista = new ArrayList<>(mapa.values());
        lista.sort(Comparator.comparingInt(FilaReporte::getUnidades).reversed()
                .thenComparing(Comparator.comparingDouble(FilaReporte::getMonto).reversed()));
        return new ArrayList<>(lista.subList(0, Math.min(limite, lista.size())));
    }

    public synchronized List<FilaReporte> ventasPorPiedra(int dias) {
        Map<String, FilaReporte> mapa = new LinkedHashMap<>();
        double total = 0;
        for (Venta v : ventasPeriodo(dias)) {
            for (DetalleVenta d : v.getDetalles()) {
                FilaReporte fila = mapa.computeIfAbsent(d.getTipoPiedra(), k -> new FilaReporte(k, ""));
                fila.acumular(d.getCantidad(), d.getSubtotal());
                total += d.getSubtotal();
            }
        }
        List<FilaReporte> lista = new ArrayList<>(mapa.values());
        for (FilaReporte f : lista) {
            f.setPorcentaje(total == 0 ? 0 : f.getMonto() * 100 / total);
        }
        lista.sort(Comparator.comparingDouble(FilaReporte::getMonto).reversed());
        return lista;
    }

    public synchronized List<FilaReporte> valuacionInventario() {
        Map<String, FilaReporte> mapa = new TreeMap<>();
        double total = 0;
        for (Pulsera p : pulseras) {
            if (p.getStockActual() <= 0) {
                continue;
            }
            String clave = p.getTipoPiedra() + "|" + p.getTalla();
            FilaReporte fila = mapa.computeIfAbsent(clave, k -> new FilaReporte(p.getTipoPiedra(), p.getTalla()));
            double valor = p.getPrecioVenta() * p.getStockActual();
            fila.acumular(p.getStockActual(), valor);
            total += valor;
        }
        List<FilaReporte> lista = new ArrayList<>(mapa.values());
        for (FilaReporte f : lista) {
            f.setPorcentaje(total == 0 ? 0 : f.getMonto() * 100 / total);
        }
        return lista;
    }

    private List<Venta> ventasPeriodo(int dias) {
        if (dias <= 0) {
            return new ArrayList<>(ventas.listar());
        }
        LocalDateTime limite = LocalDateTime.now().minusDays(dias);
        return ventas.filtrar(v -> v.getFecha().isAfter(limite));
    }

    private void validarPulsera(Pulsera p) throws NegocioException {
        if (vacio(p.getNombre())) {
            throw new NegocioException("El nombre de la pulsera es obligatorio");
        }
        if (vacio(p.getTipoPiedra())) {
            throw new NegocioException("El tipo de piedra es obligatorio");
        }
        if (!TALLAS.contains(p.getTalla())) {
            throw new NegocioException("Selecciona una talla válida");
        }
        if (p.getPrecioVenta() < 0 || p.getStockMinimo() < 0) {
            throw new NegocioException("El precio y el stock mínimo no pueden ser negativos");
        }
        for (Insumo i : p.getReceta()) {
            if (buscarMaterial(i.getIdMaterial()) == null) {
                throw new NegocioException("La receta usa un material que no existe: " + i.getIdMaterial());
            }
        }
    }

    private void validarMaterial(MateriaPrima m) throws NegocioException {
        if (vacio(m.getNombre())) {
            throw new NegocioException("El nombre del insumo es obligatorio");
        }
        if (!CATEGORIAS.contains(m.getCategoria())) {
            throw new NegocioException("Selecciona una categoría válida");
        }
        if (!UNIDADES.contains(m.getUnidadMedida())) {
            throw new NegocioException("Selecciona una unidad de medida válida");
        }
        if (m.getStockMinimo() < 0) {
            throw new NegocioException("El stock mínimo no puede ser negativo");
        }
    }

    private void revisarAlertaPulsera(Pulsera p) {
        if (p.isBajo()) {
            alertas.encolar(new Alerta("Pulsera", p.getCodigo(), p.getNombre(), p.getStockActual(), p.getStockMinimo()));
        } else {
            alertas.retirar("Pulsera", p.getCodigo());
        }
    }

    private void revisarAlertaMaterial(MateriaPrima m) {
        if (m.isBajo()) {
            alertas.encolar(new Alerta("Materia prima", m.getId(), m.getNombre(), m.getCantidadDisponible(), m.getStockMinimo()));
        } else {
            alertas.retirar("Materia prima", m.getId());
        }
    }

    private void registrar(String tipo, String categoria, String referencia, String detalle, double cantidad, Usuario usuario) {
        registrar(LocalDateTime.now(), tipo, categoria, referencia, detalle, cantidad, usuario.getNombre());
    }

    private void registrar(LocalDateTime fecha, String tipo, String categoria, String referencia,
                           String detalle, double cantidad, String usuario) {
        bitacora.agregar(new Movimiento(fecha, tipo, categoria, referencia, detalle, cantidad, usuario));
    }

    private String siguienteCodigoPulsera() {
        int n = pulseras.size() + 1;
        while (buscarPulsera(String.format("%03d", n)) != null) {
            n++;
        }
        return String.format("%03d", n);
    }

    private String siguienteIdMaterial() {
        int n = materiales.size() + 1;
        while (buscarMaterial(String.format("MP-%02d", n)) != null) {
            n++;
        }
        return String.format("MP-%02d", n);
    }

    private String siguienteIdVenta() {
        contadorVentas++;
        return String.format("V-%03d", contadorVentas);
    }

    private boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }

    private String minusculas(String texto) {
        return texto == null ? "" : texto.trim().toLowerCase();
    }

    private void cargarDatosIniciales() {
        Usuario admin = new Usuario("U-01", "Administrador", "admin@stockstone.com", "admin123", "Administrador");
        Usuario vendedor = new Usuario("U-02", "Vendedor", "vendedor@stockstone.com", "vendedor123", "Vendedor");
        usuarios.add(admin);
        usuarios.add(vendedor);

        materiales.add(material("MP-01", "Cuenta Ónix Mate 8mm", "Piedra/Cuenta", 500, "piezas", 100));
        materiales.add(material("MP-02", "Cuenta Obsidiana 8mm", "Piedra/Cuenta", 400, "piezas", 100));
        materiales.add(material("MP-03", "Cuenta Cuarzo Rosa 8mm", "Piedra/Cuenta", 90, "piezas", 100));
        materiales.add(material("MP-04", "Cuenta Amatista 8mm", "Piedra/Cuenta", 300, "piezas", 100));
        materiales.add(material("MP-05", "Cuenta Ágata 8mm", "Piedra/Cuenta", 250, "piezas", 80));
        materiales.add(material("MP-06", "Hilo elástico 0.8mm", "Hilo/Elástico", 60, "metros", 15));
        materiales.add(material("MP-07", "Dije plata", "Dije/Herraje", 18, "piezas", 10));
        materiales.add(material("MP-08", "Bolsa de empaque", "Empaque", 40, "piezas", 20));

        pulseras.add(pulsera("001", "Pulsera Energía Oscura", "Ónix mate", "Clásico", "M",
                "Ónix mate de 8mm con elástico reforzado", 350, 12, 5, "MP-01:21, MP-06:0.5"));
        pulseras.add(pulsera("002", "Pulsera Noche Profunda", "Obsidiana", "Trenzado", "L",
                "Obsidiana con acabado trenzado", 420, 8, 4, "MP-02:23, MP-06:0.5"));
        pulseras.add(pulsera("003", "Pulsera Amor Suave", "Cuarzo rosa", "Clásico", "S",
                "Cuarzo rosa para uso diario", 380, 3, 5, "MP-03:19, MP-06:0.5"));
        pulseras.add(pulsera("004", "Pulsera Calma Violeta", "Amatista", "Con dije", "M",
                "Amatista con dije de plata", 450, 10, 4, "MP-04:21, MP-07:1, MP-06:0.5"));
        pulseras.add(pulsera("005", "Pulsera Tierra Viva", "Ágata", "Clásico", "L",
                "Ágata natural en tonos tierra", 390, 15, 5, "MP-05:23, MP-06:0.5"));
        pulseras.add(pulsera("006", "Pulsera Sombra Mate", "Ónix mate", "Con dije", "L",
                "Ónix mate con dije de plata", 410, 0, 3, "MP-01:23, MP-07:1, MP-06:0.5"));
        pulseras.add(pulsera("007", "Pulsera Aurora", "Cuarzo rosa", "Ajustable", "Ajustable",
                "Cuarzo rosa con cierre ajustable", 360, 9, 4, "MP-03:20, MP-06:0.5"));
        pulseras.add(pulsera("008", "Pulsera Eclipse", "Obsidiana", "Trenzado", "M",
                "Obsidiana trenzada talla mediana", 400, 7, 4, "MP-02:21, MP-06:0.5"));

        for (Pulsera p : pulseras) {
            revisarAlertaPulsera(p);
        }
        for (MateriaPrima m : materiales) {
            revisarAlertaMaterial(m);
        }

        LocalDateTime ahora = LocalDateTime.now();
        semillaVenta(ahora.minusDays(25).withHour(11).withMinute(20), "Efectivo", vendedor, "001:2", "003:1");
        semillaVenta(ahora.minusDays(18).withHour(16).withMinute(5), "Tarjeta", admin, "004:1", "005:2");
        semillaVenta(ahora.minusDays(12).withHour(12).withMinute(40), "Transferencia", vendedor, "002:1", "007:2");
        semillaVenta(ahora.minusDays(9).withHour(10).withMinute(15), "Efectivo", vendedor, "001:3");
        semillaVenta(ahora.minusDays(6).withHour(17).withMinute(30), "Tarjeta", admin, "005:1", "008:1", "006:2");
        semillaVenta(ahora.minusDays(4).withHour(13).withMinute(10), "Efectivo", vendedor, "003:2", "007:1");
        semillaVenta(ahora.minusDays(2).withHour(15).withMinute(45), "Efectivo", vendedor, "001:2", "004:2");
        semillaVenta(ahora.minusDays(1).withHour(11).withMinute(0), "Tarjeta", admin, "002:2", "008:1");
        semillaVenta(ahora.minusMinutes(40), "Efectivo", vendedor, "001:1", "005:1");
        semillaVenta(ahora.minusMinutes(10), "Tarjeta", admin, "004:1", "007:1");
    }

    private MateriaPrima material(String id, String nombre, String categoria, double cantidad, String unidad, double minimo) {
        MateriaPrima m = new MateriaPrima();
        m.setId(id);
        m.setNombre(nombre);
        m.setCategoria(categoria);
        m.setCantidadDisponible(cantidad);
        m.setUnidadMedida(unidad);
        m.setStockMinimo(minimo);
        return m;
    }

    private Pulsera pulsera(String codigo, String nombre, String piedra, String diseno, String talla,
                            String descripcion, double precio, int stock, int minimo, String receta) {
        Pulsera p = new Pulsera();
        p.setCodigo(codigo);
        p.setNombre(nombre);
        p.setTipoPiedra(piedra);
        p.setDiseno(diseno);
        p.setTalla(talla);
        p.setDescripcion(descripcion);
        p.setPrecioVenta(precio);
        p.setStockActual(stock);
        p.setStockMinimo(minimo);
        try {
            p.setReceta(Util.leerReceta(receta));
        } catch (NegocioException e) {
            throw new IllegalStateException(e.getMessage());
        }
        return p;
    }

    private void semillaVenta(LocalDateTime fecha, String metodo, Usuario vendedor, String... items) {
        List<DetalleVenta> detalles = new ArrayList<>();
        for (String par : items) {
            String[] partes = par.split(":");
            Pulsera p = buscarPulsera(partes[0]);
            detalles.add(new DetalleVenta(p.getCodigo(), p.getNombre(), p.getTipoPiedra(),
                    Integer.parseInt(partes[1]), p.getPrecioVenta()));
        }
        Venta venta = new Venta(siguienteIdVenta(), fecha, detalles, metodo, vendedor.getId());
        ventas.agregar(venta);
        for (DetalleVenta d : detalles) {
            registrar(fecha, "Salida", "Pulsera", d.getCodigoPulsera(), "Venta " + venta.getId(), d.getCantidad(), vendedor.getNombre());
        }
    }
}
