<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="es_MX"/>
<jsp:include page="/WEB-INF/vistas/fragmentos/cabecera.jsp"/>

<h1>Ventas</h1>
<p class="subtitulo">Aquí tienes un resumen de las ventas del negocio</p>

<section class="kpis">
    <c:if test="${usuario.admin}">
        <div class="kpi">
            <span class="kpi-icono claro">＄</span>
            <div><small>Ingresos totales</small><strong>$<fmt:formatNumber value="${ingresos}" minFractionDigits="2" maxFractionDigits="2"/></strong><em>Del historial</em></div>
        </div>
        <div class="kpi">
            <span class="kpi-icono">☰</span>
            <div><small>Ventas realizadas</small><strong>${numeroVentas}</strong><em>Número de ventas</em></div>
        </div>
        <div class="kpi">
            <span class="kpi-icono">◇</span>
            <div><small>Ticket promedio</small><strong>$<fmt:formatNumber value="${ticketPromedio}" minFractionDigits="2" maxFractionDigits="2"/></strong><em>Por venta</em></div>
        </div>
    </c:if>
    <div class="espacio" style="flex:1"></div>
    <div style="align-self:center">
        <button type="button" class="btn" onclick="abrirVenta()">+ Registrar venta</button>
    </div>
</section>

<div class="tarjeta">
    <h2>Historial de ventas</h2>
    <table>
        <thead>
        <tr>
            <th>ID Venta</th><th>Fecha y hora</th><th>Productos vendidos</th>
            <th>Cantidad total</th><th>Método de pago</th><th>Total</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="v" items="${ventas}">
            <tr>
                <td>${v.id}</td>
                <td>${v.fechaTexto}</td>
                <td><c:out value="${v.resumen}"/></td>
                <td>${v.cantidadTotal}</td>
                <td>${v.metodoPago}</td>
                <td>$<fmt:formatNumber value="${v.total}" minFractionDigits="2" maxFractionDigits="2"/></td>
            </tr>
        </c:forEach>
        <c:if test="${empty ventas}"><tr><td colspan="6" class="vacio">Aún no se han registrado ventas</td></tr></c:if>
        </tbody>
    </table>
</div>

<div class="modal" id="modalVenta">
    <div class="modal-caja">
        <h2>Registrar venta</h2>
        <form method="post" action="${ctx}/ventas">
            <div class="fila-venta" style="font-size:12px;color:#6F6A64">
                <span>Pulsera</span><span>Cantidad</span><span class="subtotal">Subtotal</span><span></span>
            </div>
            <div id="filasVenta"></div>
            <button type="button" class="btn claro" onclick="agregarFilaVenta()">+ Agregar pulsera</button>
            <div class="campo" style="margin-top:16px">
                <label>Método de pago</label>
                <select name="metodoPago" required>
                    <c:forEach var="mp" items="${metodosPago}"><option value="${mp}">${mp}</option></c:forEach>
                </select>
            </div>
            <div class="total-venta"><span>Total</span><strong id="totalVenta">$0.00</strong></div>
            <div class="modal-pie">
                <button type="button" class="btn claro" onclick="cerrarModal('modalVenta')">Cancelar</button>
                <button type="submit" class="btn">Registrar venta</button>
            </div>
        </form>
    </div>
</div>

<template id="plantillaFila">
    <div class="fila-venta">
        <select name="codigo" required>
            <option value="" data-precio="0" data-stock="">Selecciona una pulsera</option>
            <c:forEach var="p" items="${disponibles}">
                <option value="<c:out value='${p.codigo}'/>" data-precio="${p.precioVenta}" data-stock="${p.stockActual}"><c:out value="${p.codigo} - ${p.nombre}"/> (stock ${p.stockActual})</option>
            </c:forEach>
        </select>
        <input type="number" name="cantidad" min="1" step="1" value="1" required>
        <span class="subtotal">$0.00</span>
        <button type="button" class="icono-btn" title="Quitar" onclick="quitarFilaVenta(this)">✕</button>
    </div>
</template>

<jsp:include page="/WEB-INF/vistas/fragmentos/pie.jsp"/>
