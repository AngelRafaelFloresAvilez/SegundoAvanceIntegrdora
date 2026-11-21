<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="es_MX"/>
<jsp:include page="/WEB-INF/vistas/fragmentos/cabecera.jsp"/>

<h1>Inventario de pulseras</h1>
<p class="subtitulo">Aquí tienes el inventario de pulseras que actualmente hay en el negocio</p>

<form class="barra" method="get" action="${ctx}/pulseras">
    <input type="search" name="q" placeholder="Buscar..." value="<c:out value='${param.q}'/>">
    <select name="piedra" onchange="this.form.submit()">
        <option value="">Piedra: Todas</option>
        <c:forEach var="s" items="${piedras}">
            <option value="<c:out value='${s}'/>" ${param.piedra == s ? 'selected' : ''}><c:out value="${s}"/></option>
        </c:forEach>
    </select>
    <select name="talla" onchange="this.form.submit()">
        <option value="">Talla: Todas</option>
        <c:forEach var="t" items="${tallas}">
            <option value="${t}" ${param.talla == t ? 'selected' : ''}>${t}</option>
        </c:forEach>
    </select>
    <select name="estado" onchange="this.form.submit()">
        <option value="">Stock: Todos</option>
        <option value="Disponible" ${param.estado == 'Disponible' ? 'selected' : ''}>Disponible</option>
        <option value="Stock bajo" ${param.estado == 'Stock bajo' ? 'selected' : ''}>Stock bajo</option>
        <option value="Agotado" ${param.estado == 'Agotado' ? 'selected' : ''}>Agotado</option>
    </select>
    <button type="submit" class="btn claro">Buscar</button>
    <span class="espacio"></span>
    <c:if test="${usuario.admin}">
        <button type="button" class="btn" onclick="nuevaPulsera()">+ Nueva pulsera</button>
    </c:if>
</form>

<div class="tarjeta">
    <table>
        <thead>
        <tr>
            <th>Código</th><th>Nombre</th><th>Piedra</th><th>Diseño</th><th>Talla</th><th>Precio</th><th>Stock</th>
            <c:if test="${usuario.admin}"><th>Acciones</th></c:if>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="p" items="${pulseras}">
            <tr>
                <td><c:out value="${p.codigo}"/></td>
                <td><c:out value="${p.nombre}"/></td>
                <td><c:out value="${p.tipoPiedra}"/></td>
                <td><c:out value="${p.diseno}"/></td>
                <td><c:out value="${p.talla}"/></td>
                <td>$<fmt:formatNumber value="${p.precioVenta}" minFractionDigits="2" maxFractionDigits="2"/></td>
                <td><span class="badge ${p.claseEstado}" title="${p.estado}">${p.stockActual}</span></td>
                <c:if test="${usuario.admin}">
                    <td>
                        <div class="acciones">
                            <button type="button" class="icono-btn" title="Editar"
                                    data-codigo="<c:out value='${p.codigo}'/>"
                                    data-nombre="<c:out value='${p.nombre}'/>"
                                    data-piedra="<c:out value='${p.tipoPiedra}'/>"
                                    data-diseno="<c:out value='${p.diseno}'/>"
                                    data-talla="<c:out value='${p.talla}'/>"
                                    data-precio="${p.precioVenta}"
                                    data-minimo="${p.stockMinimo}"
                                    data-descripcion="<c:out value='${p.descripcion}'/>"
                                    data-receta="<c:out value='${p.recetaTexto}'/>"
                                    onclick="editarPulsera(this)">✎</button>
                            <button type="button" class="icono-btn" title="Sumar stock / Producción"
                                    data-codigo="<c:out value='${p.codigo}'/>"
                                    data-nombre="<c:out value='${p.nombre}'/>"
                                    data-receta="<c:out value='${p.recetaTexto}'/>"
                                    onclick="producirPulsera(this)">＋</button>
                            <form method="post" action="${ctx}/pulseras" onsubmit="return confirm('¿Eliminar esta pulsera del inventario?')">
                                <input type="hidden" name="accion" value="eliminar">
                                <input type="hidden" name="codigo" value="<c:out value='${p.codigo}'/>">
                                <button type="submit" class="icono-btn" title="Eliminar">🗑</button>
                            </form>
                        </div>
                    </td>
                </c:if>
            </tr>
        </c:forEach>
        <c:if test="${empty pulseras}"><tr><td colspan="8" class="vacio">No se encontraron pulseras</td></tr></c:if>
        </tbody>
    </table>
</div>

<c:if test="${usuario.admin}">
    <div class="modal" id="modalPulsera">
        <div class="modal-caja">
            <h2 id="tituloPulsera">Nueva pulsera</h2>
            <form id="formPulsera" method="post" action="${ctx}/pulseras">
                <input type="hidden" name="accion" value="crear">
                <div class="campos">
                    <div class="campo">
                        <label>Código</label>
                        <input type="text" name="codigo" placeholder="Automático si se deja vacío">
                    </div>
                    <div class="campo">
                        <label>Nombre</label>
                        <input type="text" name="nombre" required>
                    </div>
                    <div class="campo">
                        <label>Tipo de piedra</label>
                        <input type="text" name="tipoPiedra" list="listaPiedras" required>
                        <datalist id="listaPiedras">
                            <c:forEach var="s" items="${piedras}"><option value="<c:out value='${s}'/>"></option></c:forEach>
                        </datalist>
                    </div>
                    <div class="campo">
                        <label>Diseño</label>
                        <input type="text" name="diseno" list="listaDisenos">
                        <datalist id="listaDisenos">
                            <c:forEach var="d" items="${disenos}"><option value="${d}"></option></c:forEach>
                        </datalist>
                    </div>
                    <div class="campo">
                        <label>Talla</label>
                        <select name="talla" required>
                            <c:forEach var="t" items="${tallas}"><option value="${t}">${t}</option></c:forEach>
                        </select>
                    </div>
                    <div class="campo">
                        <label>Precio de venta</label>
                        <input type="number" name="precioVenta" min="0" step="0.01" required>
                    </div>
                    <div class="campo" id="grupoStock">
                        <label>Existencias iniciales</label>
                        <input type="number" name="stockActual" min="0" step="1" value="0">
                    </div>
                    <div class="campo">
                        <label>Stock mínimo</label>
                        <input type="number" name="stockMinimo" min="0" step="1" value="3" required>
                    </div>
                    <div class="campo completo">
                        <label>Descripción</label>
                        <textarea name="descripcion" rows="2"></textarea>
                    </div>
                    <div class="campo completo">
                        <label>Receta de insumos (opcional)</label>
                        <input type="text" name="receta" placeholder="MP-01:21, MP-06:0.5">
                        <div class="ayuda">Formato: ID del material:cantidad por pulsera, separados por coma</div>
                    </div>
                </div>
                <div class="modal-pie">
                    <button type="button" class="btn claro" onclick="cerrarModal('modalPulsera')">Cancelar</button>
                    <button type="submit" class="btn">Guardar</button>
                </div>
            </form>
        </div>
    </div>

    <div class="modal" id="modalProducir">
        <div class="modal-caja">
            <h2>Sumar stock / Producción</h2>
            <p id="textoProducir" class="subtitulo" style="margin-bottom:4px"></p>
            <p id="recetaProducir" class="ayuda" style="margin-bottom:16px"></p>
            <form id="formProducir" method="post" action="${ctx}/pulseras">
                <input type="hidden" name="accion" value="producir">
                <input type="hidden" name="codigo">
                <div class="campo">
                    <label>Unidades fabricadas</label>
                    <input type="number" name="cantidad" min="1" step="1" required>
                    <div class="ayuda">Si la pulsera tiene receta, se descontará la materia prima automáticamente</div>
                </div>
                <div class="modal-pie">
                    <button type="button" class="btn claro" onclick="cerrarModal('modalProducir')">Cancelar</button>
                    <button type="submit" class="btn">Registrar producción</button>
                </div>
            </form>
        </div>
    </div>
</c:if>

<jsp:include page="/WEB-INF/vistas/fragmentos/pie.jsp"/>
