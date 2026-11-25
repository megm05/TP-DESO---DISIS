/* Capit@l Humano - comportamiento de las pantallas */
document.addEventListener('DOMContentLoaded', function () {

  // Los datos literales se ingresan SIEMPRE en mayúsculas, sin importar <bloqmayús>.
  document.querySelectorAll('input.mayusculas, textarea.mayusculas').forEach(function (campo) {
    campo.addEventListener('input', function () {
      var inicio = campo.selectionStart, fin = campo.selectionEnd;
      var mayus = campo.value.toLocaleUpperCase('es');
      if (mayus !== campo.value) {
        campo.value = mayus;
        try { campo.setSelectionRange(inicio, fin); } catch (e) { /* no aplica */ }
      }
    });
  });

  // Listas que, al cambiar, vuelven a enviar el formulario (por ejemplo: competencia -> factores).
  document.querySelectorAll('[data-autoenviar]').forEach(function (control) {
    control.addEventListener('change', function () {
      var formulario = control.form;
      var accion = control.getAttribute('data-autoenviar');
      var oculto = document.createElement('input');
      oculto.type = 'hidden'; oculto.name = 'accion'; oculto.value = accion;
      formulario.appendChild(oculto);
      formulario.submit();
    });
  });

  // Suma de las ponderaciones de las opciones de respuesta (deben sumar 10).
  var indicador = document.getElementById('suma-ponderaciones');
  if (indicador) {
    var recalcular = function () {
      var suma = 0, valida = true;
      document.querySelectorAll('input.ponderacion').forEach(function (campo) {
        var valor = parseFloat(String(campo.value).replace(',', '.'));
        if (isNaN(valor)) { valida = false; } else { suma += valor; }
      });
      indicador.textContent = (Math.round(suma * 100) / 100).toString().replace('.', ',');
      indicador.className = 'suma ' + (valida && Math.abs(suma - 10) < 0.001 ? 'bien' : 'mal');
    };
    document.querySelectorAll('input.ponderacion').forEach(function (campo) {
      campo.addEventListener('input', recalcular);
    });
    recalcular();
  }

  // "El actor presiona cualquier tecla" para continuar.
  var continuar = document.querySelector('[data-tecla-continuar]');
  if (continuar) {
    var destino = continuar.getAttribute('data-tecla-continuar');
    var ir = function () { window.location.href = destino; };
    setTimeout(function () {
      document.addEventListener('keydown', ir, { once: true });
      document.addEventListener('click', ir, { once: true });
    }, 300);
  }

  // Filas de grilla que seleccionan su casilla o botón de opción al hacer clic.
  document.querySelectorAll('tr.seleccionable').forEach(function (fila) {
    fila.addEventListener('click', function (evento) {
      var etiqueta = evento.target.tagName;
      if (etiqueta === 'INPUT' || etiqueta === 'A' || etiqueta === 'LABEL' || etiqueta === 'BUTTON'
          || evento.target.closest('a, button')) { return; }
      var control = fila.querySelector('input[type=radio], input[type=checkbox]');
      if (control) { control.checked = control.type === 'radio' ? true : !control.checked; control.dispatchEvent(new Event('change')); }
    });
  });

  // Casilla "TODAS" (CU028 paso 6).
  var todas = document.querySelector('input[data-todas]');
  if (todas) {
    var nombre = todas.getAttribute('data-todas');
    var individuales = document.querySelectorAll('input[name="' + nombre + '"]');
    todas.addEventListener('change', function () {
      individuales.forEach(function (c) { c.checked = todas.checked; });
    });
    individuales.forEach(function (c) {
      c.addEventListener('change', function () {
        todas.checked = Array.prototype.every.call(individuales, function (x) { return x.checked; });
      });
    });
  }

  // Botón Imprimir (CU028 paso 8).
  document.querySelectorAll('[data-imprimir]').forEach(function (boton) {
    boton.addEventListener('click', function () { window.print(); });
  });

  // Ventanas emergentes: el foco va a su botón principal; "Cerrar" (o Esc) las quita
  // y devuelve el foco al campo indicado por el servidor.
  var enfocarCampo = function () {
    var foco = document.querySelector('[data-foco]');
    if (foco) { foco.focus(); if (foco.select) { foco.select(); } }
  };
  var cerrarEmergente = function (fondo) {
    fondo.parentNode.removeChild(fondo);
    if (!document.querySelector('[data-emergente]')) { enfocarCampo(); }
  };
  document.querySelectorAll('[data-cerrar]').forEach(function (boton) {
    boton.addEventListener('click', function () {
      var fondo = boton.closest('[data-emergente]');
      if (fondo) { cerrarEmergente(fondo); }
    });
  });
  document.addEventListener('keydown', function (evento) {
    if (evento.key !== 'Escape') { return; }
    var fondos = document.querySelectorAll('[data-emergente]');
    if (!fondos.length) { return; }
    var ultimo = fondos[fondos.length - 1];
    var cerrar = ultimo.querySelector('.emergente-cerrar');
    if (cerrar) { cerrar.click(); }
  });
  var emergente = document.querySelector('[data-emergente]');
  if (emergente) {
    var principal = emergente.querySelector('[autofocus]') || emergente.querySelector('.boton-principal')
      || emergente.querySelector('.emergente-botones button, .emergente-botones a.boton');
    if (principal) { principal.focus(); }
  } else {
    // Foco en el campo indicado por el servidor (por ejemplo: Nombre, en el flujo 2.C del CU021).
    enfocarCampo();
  }
});
