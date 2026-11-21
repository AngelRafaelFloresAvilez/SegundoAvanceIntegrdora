<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="es_MX"/>
<jsp:include page="/WEB-INF/vistas/fragmentos/cabecera.jsp"/>

<h1>Materia prima</h1>
<p class="subtitulo">Aquí tienes el inventario de materia prima que hay en el negocio</p>

<form class="barra" method="get" action="${ctx}/materia-prima">
    <input type="search" name="q" placeholder="Buscar material..." value="<c:out value='${param.q}'/>">
    <select name="categoria" onchange="this.form.submit()">
        <option value="">Categoría: Todas</option>
        <c:forEach var="cat" items="${categorias}">
            <option value="<c:out value='${cat}'/>" ${param.categoria == cat ? 'selected' : ''}><c:out value="${cat}"/></option>
        </c:forEach>
    </select>
    <select name="unidad" onchange="this.form.submit()">
        <option value="">Unidad: Todas</option>
        <c:forEach var="u" items="${unidades}">
            <option value="${u}" ${param.unidad == u ? 'selected' : ''}>${u}</option>
        </c:forEach>
    </select>
    <button type="submit" class="btn claro">Buscar</button>
    <span class="espacio"></span>
    <button type="button" class="btn" onclick="nuevoMaterial()">+ Nuevo material</button>
</form>

<div class="tarjeta">
    <table>
        <thead>
        <tr>
            <th>ID</th><th>Nombre del insumo</th><th>Categoría</th><th>Stock disponible</th>
            <th>Unidad de medida</th><th>Estado</th><th>Acciones</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="m" items="${materiales}">
            <tr>
                <td><c:out value="${m.id}"/></td>
                <td><c:out value="${m.nombre}"/></td>
                <td><c:out value="${m.categoria}"/></td>
                <td>${m.cantidadTexto}</td>
                <td><c:out value="${m.unidadMedida}"/></td>
                <td><span class="badge ${m.claseEstado}">${m.estado}</span></td>
                <td>
                    <div class="acciones">
                        <button type="button" class="icono-btn" title="Editar"
                                data-id="<c:out value='${m.id}'/>"
                                data-nombre="<c:out value='${m.nombre}'/>"
                                data-categoria="<c:out value='${m.categoria}'/>"
                                data-unidad="<c:out value='${m.unidadMedida}'/>"
                                data-minimo="${m.minimoTexto}"
                                onclick="editarMaterial(this)">✎</button>
                        <button type="button" class="icono-btn" title="Reabastecer stock"
                                data-id="<c:out value='${m.id}'/>"
                                data-nombre="<c:out value='${m.nombre}'/>"
                                data-unidad="<c:out value='${m.unidadMedida}'/>"
                                onclick="reabastecerMaterial(this)">＋</button>
                        <form method="post" action="${ctx}/materia-prima" onsubmit="return confirm('¿Eliminar este material?')">
                            <input type="hidden" name="accion" value="eliminar">
                            <input type="hidden" name="id" value="<c:out value='${m.id}'/>">
                            <button type="submit" class="icono-btn" title="Eliminar">🗑</button>
                        </form>
                    </div>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty materiales}"><tr><td colspan="7" class="vacio">No se encontraron materiales</td></tr></c:if>
        </tbody>
    </table>
</div>

<div class="modal" id="modalMaterial">
    <div class="modal-caja">
        <h2 id="tituloMaterial">Nuevo material</h2>
        <form id="formMaterial" method="post" action="${ctx}/materia-prima">
            <input type="hidden" name="accion" value="crear">
            <div class="campos">
                <div class="campo">
                    <label>ID</label>
                    <input type="text" name="id" placeholder="Automático si se deja vacío">
                </div>
                <div class="campo">
                    <label>Nombre del insumo</label>
                    <input type="text" name="nombre" required>
                </div>
                <div class="campo">
                    <label>Categoría</label>
                    <select name="categoria" required>
                        <c:forEach var="cat" items="${categorias}"><option value="<c:out value='${cat}'/>"><c:out value="${cat}"/></option></c:forEach>
                    </select>
                </div>
                <div class="campo">
                    <label>Unidad de medida</label>
                    <select name="unidadMedida" required>
                        <c:forEach var="u" items="${unidades}"><option value="${u}">${u}</option></c:forEach>
                    </select>
                </div>
                <div class="campo" id="grupoCantidad">
                    <label>Cantidad inicial</label>
                    <input type="number" name="cantidadDisponible" min="0" step="0.01" value="0">
                </div>
                <div class="campo">
                    <label>Stock mínimo</label>
                    <input type="number" name="stockMinimo" min="0" step="0.01" value="10" required>
                </div>
            </div>
            <div class="modal-pie">
                <button type="button" class="btn claro" onclick="cerrarModal('modalMaterial')">Cancelar</button>
                <button type="submit" class="btn">Guardar</button>
            </div>
        </form>
    </div>
</div>

<div class="modal" id="modalReabastecer">
    <div class="modal-caja">
        <h2>Reabastecer stock</h2>
        <p id="textoReabastecer" class="subtitulo" style="margin-bottom:16px"></p>
        <form id="formReabastecer" method="post" action="${ctx}/materia-prima">
            <input type="hidden" name="accion" value="reabastecer">
            <input type="hidden" name="id">
            <div class="campo">
                <label>Cantidad que ingresa</label>
                <input type="number" name="cantidad" min="0.01" step="0.01" required>
            </div>
            <div class="modal-pie">
                <button type="button" class="btn claro" onclick="cerrarModal('modalReabastecer')">Cancelar</button>
                <button type="submit" class="btn">Reabastecer</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/vistas/fragmentos/pie.jsp"/>
