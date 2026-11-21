<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="es_MX"/>
<jsp:include page="/WEB-INF/vistas/fragmentos/cabecera.jsp"/>

<h1>Bienvenido, <c:out value="${usuario.nombre}"/></h1>
<p class="subtitulo">Aquí tienes un resumen del estado actual de tu inventario y ventas</p>

<section class="kpis">
    <div class="kpi">
        <span class="kpi-icono">◇</span>
        <div><small>Pulseras en stock</small><strong>${totalPiezas}</strong><em>Total de piezas</em></div>
    </div>
    <div class="kpi">
        <span class="kpi-icono rojo">!</span>
        <div><small>Stock bajo</small><strong>${totalAlertas}</strong><em>Necesitan atención</em></div>
    </div>
    <div class="kpi">
        <span class="kpi-icono claro">＄</span>
        <div><small>Ventas del día</small><strong>$<fmt:formatNumber value="${ventasHoy}" minFractionDigits="2" maxFractionDigits="2"/></strong><em>Total de hoy</em></div>
    </div>
    <c:if test="${usuario.admin}">
        <div class="kpi">
            <span class="kpi-icono">▦</span>
            <div><small>Valor inventario</small><strong>$<fmt:formatNumber value="${valorInventario}" minFractionDigits="2" maxFractionDigits="2"/></strong><em>Total en stock</em></div>
        </div>
    </c:if>
</section>

<div class="tarjeta">
    <h2>Inventario reciente</h2>
    <table>
        <thead>
        <tr><th>Código</th><th>Piedra</th><th>Diseño</th><th>Talla</th><th>Precio</th><th>Stock</th></tr>
        </thead>
        <tbody>
        <c:forEach var="p" items="${recientes}">
            <tr>
                <td><c:out value="${p.codigo}"/></td>
                <td><c:out value="${p.tipoPiedra}"/></td>
                <td><c:out value="${p.diseno}"/></td>
                <td><c:out value="${p.talla}"/></td>
                <td>$<fmt:formatNumber value="${p.precioVenta}" minFractionDigits="2" maxFractionDigits="2"/></td>
                <td><span class="badge ${p.claseEstado}">${p.stockActual}</span></td>
            </tr>
        </c:forEach>
        <c:if test="${empty recientes}"><tr><td colspan="6" class="vacio">Aún no hay pulseras registradas</td></tr></c:if>
        </tbody>
    </table>
</div>

<jsp:include page="/WEB-INF/vistas/fragmentos/pie.jsp"/>
