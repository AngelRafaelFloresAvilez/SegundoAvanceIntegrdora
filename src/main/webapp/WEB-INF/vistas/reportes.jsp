<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="es_MX"/>
<jsp:include page="/WEB-INF/vistas/fragmentos/cabecera.jsp"/>

<h1>Reportes</h1>
<form class="barra" method="get" action="${ctx}/reportes">
    <span class="subtitulo" style="margin:0">Aquí tienes un resumen de todas las métricas del negocio en general</span>
    <span class="espacio"></span>
    <select name="dias" onchange="this.form.submit()">
        <option value="7" ${dias == 7 ? 'selected' : ''}>Últimos 7 días</option>
        <option value="30" ${dias == 30 ? 'selected' : ''}>Últimos 30 días</option>
        <option value="0" ${dias == 0 ? 'selected' : ''}>Todo el historial</option>
    </select>
</form>

<div class="rejilla">
    <div class="tarjeta">
        <h2>Top pulseras más vendidas</h2>
        <table>
            <thead><tr><th>#</th><th>Pulsera</th><th>Unidades</th><th>Ingresos</th></tr></thead>
            <tbody>
            <c:forEach var="f" items="${top}" varStatus="st">
                <tr>
                    <td>${st.count}</td>
                    <td><c:out value="${f.etiqueta}"/></td>
                    <td>${f.unidades}</td>
                    <td>$<fmt:formatNumber value="${f.monto}" minFractionDigits="2" maxFractionDigits="2"/></td>
                </tr>
            </c:forEach>
            <c:if test="${empty top}"><tr><td colspan="4" class="vacio">Sin ventas en el periodo</td></tr></c:if>
            </tbody>
        </table>
    </div>

    <div class="tarjeta">
        <h2>Ventas por tipo de piedra natural</h2>
        <table>
            <thead><tr><th>Piedra</th><th>Unidades</th><th>Ingresos</th><th>Participación</th></tr></thead>
            <tbody>
            <c:forEach var="f" items="${porPiedra}">
                <tr>
                    <td><c:out value="${f.etiqueta}"/></td>
                    <td>${f.unidades}</td>
                    <td>$<fmt:formatNumber value="${f.monto}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td>
                        <span class="barra-pct"><span style="width:${f.porcentaje}%"></span></span>
                        <fmt:formatNumber value="${f.porcentaje}" maxFractionDigits="1"/>%
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty porPiedra}"><tr><td colspan="4" class="vacio">Sin ventas en el periodo</td></tr></c:if>
            </tbody>
        </table>
    </div>

    <div class="tarjeta">
        <h2>Valuación del inventario actual</h2>
        <table>
            <thead><tr><th>Piedra</th><th>Talla</th><th>Piezas</th><th>Valor</th></tr></thead>
            <tbody>
            <c:forEach var="f" items="${valuacion}">
                <tr>
                    <td><c:out value="${f.etiqueta}"/></td>
                    <td><c:out value="${f.extra}"/></td>
                    <td>${f.unidades}</td>
                    <td>$<fmt:formatNumber value="${f.monto}" minFractionDigits="2" maxFractionDigits="2"/></td>
                </tr>
            </c:forEach>
            <c:if test="${empty valuacion}"><tr><td colspan="4" class="vacio">Sin existencias</td></tr></c:if>
            </tbody>
        </table>
        <div class="pie-tabla">Valor total: <strong>$<fmt:formatNumber value="${valorInventario}" minFractionDigits="2" maxFractionDigits="2"/></strong></div>
    </div>

    <div class="tarjeta">
        <h2>Lista crítica de reabastecimiento</h2>
        <table>
            <thead><tr><th>Orden</th><th>Tipo</th><th>Elemento</th><th>Actual / Mínimo</th></tr></thead>
            <tbody>
            <c:forEach var="a" items="${criticos}" varStatus="st">
                <tr>
                    <td>${st.count}</td>
                    <td>${a.tipo}</td>
                    <td><c:out value="${a.id} - ${a.nombre}"/></td>
                    <td><span class="badge ${a.clase}">${a.actualTexto} / ${a.minimoTexto}</span></td>
                </tr>
            </c:forEach>
            <c:if test="${empty criticos}"><tr><td colspan="4" class="vacio">No hay elementos con stock bajo</td></tr></c:if>
            </tbody>
        </table>
    </div>
</div>

<div class="tarjeta">
    <h2>Bitácora de movimientos de inventario</h2>
    <table>
        <thead><tr><th>Fecha y hora</th><th>Movimiento</th><th>Categoría</th><th>Referencia</th><th>Cantidad</th><th>Detalle</th><th>Usuario</th></tr></thead>
        <tbody>
        <c:forEach var="m" items="${movimientos}">
            <tr>
                <td>${m.fechaTexto}</td>
                <td>${m.tipo}</td>
                <td>${m.categoria}</td>
                <td><c:out value="${m.referencia}"/></td>
                <td>${m.cantidadTexto}</td>
                <td><c:out value="${m.detalle}"/></td>
                <td><c:out value="${m.usuario}"/></td>
            </tr>
        </c:forEach>
        <c:if test="${empty movimientos}"><tr><td colspan="7" class="vacio">Sin movimientos registrados</td></tr></c:if>
        </tbody>
    </table>
    <div class="pie-tabla">Se muestran los 15 movimientos más recientes</div>
</div>

<jsp:include page="/WEB-INF/vistas/fragmentos/pie.jsp"/>
