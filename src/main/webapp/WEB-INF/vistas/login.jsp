<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Stock Stone - Iniciar sesión</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&family=Playfair+Display:wght@400;500&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/estilos.css">
</head>
<body>
<div class="login">
    <section class="login-izq">
        <div class="login-marca">
            <div class="marca">STOCKSTONE</div>
            <p>Control de inventario para pulseras artesanales</p>
        </div>
        <div class="modulos">
            <div><strong>Pulseras</strong><small>Gestiona tus diseños y existencias</small></div>
            <div><strong>Materia prima</strong><small>Controla tus insumos y materiales</small></div>
            <div><strong>Ventas</strong><small>Registra y da seguimiento a tus ventas</small></div>
        </div>
    </section>
    <section class="login-der">
        <form class="login-form" method="post" action="${ctx}/login">
            <h1>Iniciar sesión</h1>
            <p class="subtitulo">Accede a tu cuenta para gestionar el inventario de tu negocio</p>
            <c:if test="${not empty error}"><div class="alerta error"><c:out value="${error}"/></div></c:if>
            <c:if test="${not empty info}"><div class="alerta info"><c:out value="${info}"/></div></c:if>
            <div class="campo">
                <input type="email" name="correo" placeholder="Correo electrónico" value="<c:out value='${correo}'/>" required autofocus>
            </div>
            <div class="campo">
                <input type="password" id="password" name="password" placeholder="Contraseña" required>
                <button type="button" class="ojo" onclick="mostrarPassword()" title="Mostrar contraseña">👁</button>
            </div>
            <label class="recordar"><input type="checkbox" name="recordar"> Recordar sesión</label>
            <button type="submit" class="btn">Iniciar sesión</button>
            <div class="enlaces">¿Olvidaste tu contraseña? <a href="${ctx}/login?recuperar=1">Recuperar contraseña</a></div>
            <div class="demo">
                <strong>Cuentas de demostración</strong><br>
                Administrador: admin@stockstone.com / admin123<br>
                Vendedor: vendedor@stockstone.com / vendedor123
            </div>
        </form>
    </section>
</div>
<script src="${ctx}/js/app.js"></script>
</body>
</html>
