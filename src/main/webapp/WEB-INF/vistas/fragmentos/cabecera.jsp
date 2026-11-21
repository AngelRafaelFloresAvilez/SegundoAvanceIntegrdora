<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Stock Stone</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&family=Playfair+Display:wght@400;500&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/estilos.css">
</head>
<body>
<div class="app">
    <aside class="sidebar">
        <div class="marca">STOCKSTONE</div>
        <nav>
            <a href="${ctx}/inicio" class="${activo == 'inicio' ? 'activo' : ''}"><span class="icono">⌂</span>Inicio</a>
            <a href="${ctx}/pulseras" class="${activo == 'pulseras' ? 'activo' : ''}"><span class="icono">◇</span>Inventario de pulseras</a>
            <c:if test="${usuario.admin}">
                <a href="${ctx}/materia-prima" class="${activo == 'materia' ? 'activo' : ''}"><span class="icono">▦</span>Materia prima</a>
            </c:if>
            <a href="${ctx}/ventas" class="${activo == 'ventas' ? 'activo' : ''}"><span class="icono">＄</span>Ventas</a>
            <c:if test="${usuario.admin}">
                <a href="${ctx}/reportes" class="${activo == 'reportes' ? 'activo' : ''}"><span class="icono">☰</span>Reportes</a>
            </c:if>
        </nav>
    </aside>
    <div class="principal">
        <header class="topbar">
            <c:choose>
                <c:when test="${usuario.admin}">
                    <a class="campana" href="${ctx}/reportes" title="Alertas de stock bajo">🔔<c:if test="${totalAlertas > 0}"><span class="contador">${totalAlertas}</span></c:if></a>
                </c:when>
                <c:otherwise>
                    <a class="campana" href="${ctx}/pulseras?estado=Stock+bajo" title="Pulseras con stock bajo">🔔<c:if test="${totalAlertas > 0}"><span class="contador">${totalAlertas}</span></c:if></a>
                </c:otherwise>
            </c:choose>
            <details class="menu-usuario">
                <summary>
                    <span class="avatar">${fn:toUpperCase(fn:substring(usuario.nombre, 0, 1))}</span>
                    <c:out value="${usuario.nombre}"/>
                </summary>
                <div class="menu">
                    <span><c:out value="${usuario.rol}"/></span>
                    <a href="${ctx}/logout">Cerrar sesión</a>
                </div>
            </details>
        </header>
        <main class="contenido">
            <c:if test="${not empty mensajeOk}"><div class="alerta ok"><c:out value="${mensajeOk}"/></div></c:if>
            <c:if test="${not empty mensajeError}"><div class="alerta error"><c:out value="${mensajeError}"/></div></c:if>
