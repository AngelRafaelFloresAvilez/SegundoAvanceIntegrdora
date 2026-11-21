function abrirModal(id) {
    document.getElementById(id).classList.add('abierto');
}

function cerrarModal(id) {
    document.getElementById(id).classList.remove('abierto');
}

document.addEventListener('click', function (e) {
    if (e.target.classList.contains('modal')) {
        e.target.classList.remove('abierto');
    }
});

function nuevaPulsera() {
    var f = document.getElementById('formPulsera');
    f.reset();
    document.getElementById('tituloPulsera').textContent = 'Nueva pulsera';
    f.accion.value = 'crear';
    f.codigo.readOnly = false;
    document.getElementById('grupoStock').style.display = '';
    f.stockActual.disabled = false;
    f.stockActual.value = '0';
    abrirModal('modalPulsera');
}

function editarPulsera(btn) {
    var d = btn.dataset;
    var f = document.getElementById('formPulsera');
    f.reset();
    document.getElementById('tituloPulsera').textContent = 'Editar pulsera';
    f.accion.value = 'editar';
    f.codigo.value = d.codigo;
    f.codigo.readOnly = true;
    f.nombre.value = d.nombre;
    f.tipoPiedra.value = d.piedra;
    f.diseno.value = d.diseno;
    f.talla.value = d.talla;
    f.precioVenta.value = d.precio;
    f.stockMinimo.value = d.minimo;
    f.descripcion.value = d.descripcion;
    f.receta.value = d.receta;
    document.getElementById('grupoStock').style.display = 'none';
    f.stockActual.disabled = true;
    abrirModal('modalPulsera');
}

function producirPulsera(btn) {
    var d = btn.dataset;
    var f = document.getElementById('formProducir');
    f.codigo.value = d.codigo;
    f.cantidad.value = '1';
    document.getElementById('textoProducir').textContent = d.codigo + ' - ' + d.nombre;
    document.getElementById('recetaProducir').textContent = d.receta ? 'Receta: ' + d.receta : 'Esta pulsera no tiene receta configurada';
    abrirModal('modalProducir');
}

function nuevoMaterial() {
    var f = document.getElementById('formMaterial');
    f.reset();
    document.getElementById('tituloMaterial').textContent = 'Nuevo material';
    f.accion.value = 'crear';
    f.elements['id'].readOnly = false;
    document.getElementById('grupoCantidad').style.display = '';
    f.cantidadDisponible.disabled = false;
    f.cantidadDisponible.value = '0';
    abrirModal('modalMaterial');
}

function editarMaterial(btn) {
    var d = btn.dataset;
    var f = document.getElementById('formMaterial');
    f.reset();
    document.getElementById('tituloMaterial').textContent = 'Editar material';
    f.accion.value = 'editar';
    f.elements['id'].value = d.id;
    f.elements['id'].readOnly = true;
    f.nombre.value = d.nombre;
    f.categoria.value = d.categoria;
    f.unidadMedida.value = d.unidad;
    f.stockMinimo.value = d.minimo;
    document.getElementById('grupoCantidad').style.display = 'none';
    f.cantidadDisponible.disabled = true;
    abrirModal('modalMaterial');
}

function reabastecerMaterial(btn) {
    var d = btn.dataset;
    var f = document.getElementById('formReabastecer');
    f.elements['id'].value = d.id;
    f.cantidad.value = '';
    document.getElementById('textoReabastecer').textContent = d.id + ' - ' + d.nombre + ' (' + d.unidad + ')';
    abrirModal('modalReabastecer');
}

function dinero(valor) {
    return '$' + valor.toLocaleString('es-MX', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function recalcularVenta() {
    var total = 0;
    document.querySelectorAll('#filasVenta .fila-venta').forEach(function (fila) {
        var opcion = fila.querySelector('select').selectedOptions[0];
        var entrada = fila.querySelector('input');
        var cantidad = parseInt(entrada.value, 10) || 0;
        var precio = parseFloat(opcion.dataset.precio) || 0;
        var subtotal = precio * cantidad;
        entrada.max = opcion.dataset.stock || '';
        fila.querySelector('.subtotal').textContent = dinero(subtotal);
        total += subtotal;
    });
    document.getElementById('totalVenta').textContent = dinero(total);
}

function agregarFilaVenta() {
    var plantilla = document.getElementById('plantillaFila');
    var fila = plantilla.content.firstElementChild.cloneNode(true);
    document.getElementById('filasVenta').appendChild(fila);
    recalcularVenta();
}

function quitarFilaVenta(btn) {
    var contenedor = document.getElementById('filasVenta');
    if (contenedor.children.length > 1) {
        btn.closest('.fila-venta').remove();
    }
    recalcularVenta();
}

function abrirVenta() {
    document.getElementById('filasVenta').innerHTML = '';
    agregarFilaVenta();
    abrirModal('modalVenta');
}

document.addEventListener('input', function (e) {
    if (e.target.closest && e.target.closest('#filasVenta')) {
        recalcularVenta();
    }
});

document.addEventListener('change', function (e) {
    if (e.target.closest && e.target.closest('#filasVenta')) {
        recalcularVenta();
    }
});

function mostrarPassword() {
    var campo = document.getElementById('password');
    campo.type = campo.type === 'password' ? 'text' : 'password';
}
