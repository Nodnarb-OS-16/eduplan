const horas = ["08:00", "08:30", "09:00", "09:45", "10:30", "11:15", "12:00", "13:00", "14:00", "15:15", "16:00"];
const dias = ["Lunes", "Martes", "Miércoles", "Jueves", "Viernes"];
const calendario = document.getElementById("calendario");

const API_URL = "http://localhost:8080/api";

let usuarioActual = null;
let evaluaciones = [];
let tareas = [];
let cursoAsignaturaDocenteId = 1; // Temporal: ID fijo para pruebas
let cursoAlumnoId = 1; // Temporal: ID fijo para pruebas

// ============================================
// LOGIN CON LA API
// ============================================

async function iniciarSesion() {
  const correo = document.getElementById("correo").value.trim();
  const clave = document.getElementById("clave").value.trim();

  if (!correo || !clave) {
    alert("Completa todos los campos");
    return;
  }

  try {
    const response = await fetch(`${API_URL}/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        correoElectronico: correo,
        contrasena: clave
      })
    });

    if (!response.ok) {
      alert("Correo o contraseña incorrectos");
      return;
    }

    const data = await response.json();
    usuarioActual = {
      id: data.id,
      nombre: data.nombre,
      correo: data.correoElectronico,
      rol: data.rol
    };

    // Ocultar login y mostrar interfaz
    document.getElementById("login").classList.add("hidden");
    document.getElementById("calendario").classList.remove("hidden");
    document.getElementById("logout").classList.remove("hidden");
    document.getElementById("tituloCalendario").classList.remove("hidden");
    document.getElementById("infoUsuario").classList.remove("hidden");
    document.getElementById("infoUsuario").textContent = `Usuario: ${correo} (${usuarioActual.rol})`;

    // Mostrar paneles según rol
    if (usuarioActual.rol === "Docente") {
      document.getElementById("panelProfesor").classList.remove("hidden");
      document.getElementById("panelEliminar").classList.remove("hidden");
      await cargarEvaluacionesDocente();
    }

    if (usuarioActual.rol === "Alumno") {
      document.getElementById("panelAlumno").classList.remove("hidden");
      document.getElementById("panelEliminarAlumno").classList.remove("hidden");
      await cargarDatosAlumno();
    }

    construirCalendario();

  } catch (error) {
    console.error("Error en login:", error);
    alert("Error al conectar con el servidor. Verifica que el backend esté corriendo.");
  }
}

// ============================================
// CARGAR DATOS DESDE LA API
// ============================================

async function cargarEvaluacionesDocente() {
  try {
    const response = await fetch(`${API_URL}/evaluaciones/docente/${usuarioActual.id}`);
    evaluaciones = await response.json();
    mostrarEvaluacionesDelProfesor();
  } catch (error) {
    console.error("Error cargando evaluaciones:", error);
  }
}

async function cargarDatosAlumno() {
  try {
    const respEval = await fetch(`${API_URL}/evaluaciones/alumno/${usuarioActual.id}`);
    evaluaciones = await respEval.json();

    const respTarea = await fetch(`${API_URL}/tareas/alumno/${usuarioActual.id}`);
    tareas = await respTarea.json();
    
    mostrarTareasDelAlumno();
  } catch (error) {
    console.error("Error cargando datos del alumno:", error);
  }
}

// ============================================
// CONSTRUIR CALENDARIO
// ============================================

function construirCalendario() {
  calendario.innerHTML = "";

  // Headers
  calendario.innerHTML += `<div class="celda header">Hora</div>`;
  dias.forEach(dia => {
    calendario.innerHTML += `<div class="celda header">${dia}</div>`;
  });

  // Obtener el lunes de la semana actual
  const hoy = new Date();
  const lunesActual = getLunesDeLaSemana(hoy);

  // Construir celdas
  horas.forEach(hora => {
    calendario.innerHTML += `<div class="celda header">${hora}</div>`;
    
    dias.forEach((dia, indexDia) => {
      const celda = document.createElement("div");
      celda.className = "celda";
      
      // Calcular fecha real para este día
      const fechaCelda = new Date(lunesActual);
      fechaCelda.setDate(lunesActual.getDate() + indexDia);
      const fechaStr = fechaCelda.toISOString().split('T')[0];

      // Buscar evaluación en esta celda
      const evalEnCelda = evaluaciones.find(e => 
        e.fecha === fechaStr && e.hora.substring(0, 5) === hora
      );

      // Buscar tarea en esta celda
      const tareaEnCelda = tareas.find(t => 
        t.fecha === fechaStr && t.hora && t.hora.substring(0, 5) === hora
      );

      if (evalEnCelda) {
        let texto = "";
        if (evalEnCelda.cursoAsignaturaDocente && evalEnCelda.cursoAsignaturaDocente.asignatura) {
          const asignatura = evalEnCelda.cursoAsignaturaDocente.asignatura.nombre;
          texto = evalEnCelda.descripcion ? `${asignatura}: ${evalEnCelda.descripcion}` : asignatura;
        } else {
          texto = evalEnCelda.descripcion || "Evaluación";
        }
        
        celda.textContent = texto;
        celda.style.backgroundColor = "#f9c74f";
        celda.style.color = "#000";
        celda.dataset.evalId = evalEnCelda.id;
        celda.dataset.tipo = "evaluacion";
        
        // Verificar si es del docente actual
        if (usuarioActual.rol === "Docente" && 
            evalEnCelda.cursoAsignaturaDocente.docente.id === usuarioActual.id) {
          celda.dataset.esMio = "true";
        }
        
      } else if (tareaEnCelda) {
        celda.textContent = `Tarea: ${tareaEnCelda.titulo}`;
        celda.style.backgroundColor = "#aed581";
        celda.style.color = "#000";
        celda.dataset.tareaId = tareaEnCelda.id;
        celda.dataset.tipo = "tarea";
        celda.dataset.esMio = "true"; // Las tareas siempre son del alumno actual
      }

      asignarEventoCelda(celda, fechaStr, hora, dia);
      calendario.appendChild(celda);
    });
  });
}

// ============================================
// EVENTOS DE CELDA
// ============================================

function asignarEventoCelda(celda, fecha, hora, diaNombre) {
  celda.onclick = async () => {
    const tipo = celda.dataset.tipo;
    const esMio = celda.dataset.esMio === "true";

    // Si hay contenido y es mío
    if (tipo === "evaluacion" && usuarioActual.rol === "Docente" && esMio) {
      const evalId = celda.dataset.evalId;
      const confirmar = confirm("¿Quieres eliminar esta evaluación?");
      if (confirmar) {
        await eliminarEvaluacion(evalId);
      }
      return;
    }

    if (tipo === "tarea" && usuarioActual.rol === "Alumno" && esMio) {
      const tareaId = celda.dataset.tareaId;
      const confirmar = confirm("¿Quieres eliminar esta tarea?");
      if (confirmar) {
        await eliminarTarea(tareaId);
      }
      return;
    }

    // Si hay contenido pero no es mío
    if (tipo && !esMio) {
      if (tipo === "evaluacion") {
        alert("Esta evaluación fue creada por otro docente. No puedes modificarla.");
      } else {
        alert("Esta tarea fue creada por otro alumno. No puedes modificarla.");
      }
      return;
    }

    // Crear nuevo contenido
    if (usuarioActual.rol === "Docente" && !tipo) {
      const descripcion = prompt(`Evaluación para ${diaNombre} a las ${hora}:`);
      if (descripcion) {
        await crearEvaluacion(fecha, hora, descripcion);
      }
    }

    if (usuarioActual.rol === "Alumno" && !tipo) {
      const titulo = prompt(`Tarea para ${diaNombre} a las ${hora}:`);
      if (titulo) {
        await crearTarea(fecha, hora, titulo);
      }
    }
  };
}

// ============================================
// CREAR EVALUACIÓN
// ============================================

async function crearEvaluacion(fecha, hora, descripcion) {
  try {
    // Validar máximo 2 evaluaciones por día
    const evalsDelDia = evaluaciones.filter(e => e.fecha === fecha);
    if (evalsDelDia.length >= 2) {
      alert("⚠️ No se puede agregar más de 2 evaluaciones en un mismo día.");
      return;
    }

    const response = await fetch(`${API_URL}/evaluaciones?docenteId=${usuarioActual.id}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        fecha: fecha,
        hora: hora + ":00",
        descripcion: descripcion,
        cursoAsignaturaDocente: { id: cursoAsignaturaDocenteId }
      })
    });

    if (!response.ok) {
      const error = await response.text();
      alert(error);
      return;
    }

    await cargarEvaluacionesDocente();
    construirCalendario();

  } catch (error) {
    console.error("Error creando evaluación:", error);
    alert("Error al crear evaluación");
  }
}

// ============================================
// CREAR TAREA
// ============================================

async function crearTarea(fecha, hora, titulo) {
  try {
    const response = await fetch(`${API_URL}/tareas?alumnoId=${usuarioActual.id}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        titulo: titulo,
        descripcion: "",
        fecha: fecha,
        hora: hora + ":00",
        curso: { id: cursoAlumnoId }
      })
    });

    if (!response.ok) {
      const error = await response.text();
      alert(error);
      return;
    }

    await cargarDatosAlumno();
    construirCalendario();

  } catch (error) {
    console.error("Error creando tarea:", error);
    alert("Error al crear tarea");
  }
}

// ============================================
// ELIMINAR EVALUACIÓN
// ============================================

async function eliminarEvaluacion(evalId) {
  try {
    const response = await fetch(`${API_URL}/evaluaciones/${evalId}?docenteId=${usuarioActual.id}`, {
      method: "DELETE"
    });

    if (!response.ok) {
      alert("No tienes permiso para eliminar esta evaluación");
      return;
    }

    await cargarEvaluacionesDocente();
    construirCalendario();

  } catch (error) {
    console.error("Error eliminando evaluación:", error);
  }
}

// ============================================
// ELIMINAR TAREA
// ============================================

async function eliminarTarea(tareaId) {
  try {
    const response = await fetch(`${API_URL}/tareas/${tareaId}?alumnoId=${usuarioActual.id}`, {
      method: "DELETE"
    });

    if (!response.ok) {
      alert("No tienes permiso para eliminar esta tarea");
      return;
    }

    await cargarDatosAlumno();
    construirCalendario();

  } catch (error) {
    console.error("Error eliminando tarea:", error);
  }
}

// ============================================
// GUARDAR PRUEBA DESDE FORMULARIO
// ============================================

async function guardarPrueba() {
  const asignatura = document.getElementById("asignatura").value.trim();
  const evaluacion = document.getElementById("evaluacion").value.trim();
  const dia = document.getElementById("dia").value;
  const hora = document.getElementById("hora").value;

  if (!asignatura || !evaluacion || !dia || !hora) {
    alert("Completa todos los campos");
    return;
  }

  // Convertir día a fecha
  const hoy = new Date();
  const lunesActual = getLunesDeLaSemana(hoy);
  const indiceDia = dias.indexOf(dia);
  const fechaObj = new Date(lunesActual);
  fechaObj.setDate(lunesActual.getDate() + indiceDia);
  const fecha = fechaObj.toISOString().split('T')[0];

  const descripcion = `${asignatura}: ${evaluacion}`;
  await crearEvaluacion(fecha, hora, descripcion);

  // Limpiar formulario
  document.getElementById("asignatura").value = "";
  document.getElementById("evaluacion").value = "";
  document.getElementById("dia").selectedIndex = 0;
  document.getElementById("hora").selectedIndex = 0;
}

// ============================================
// GUARDAR TAREA DESDE FORMULARIO
// ============================================

async function guardarTarea() {
  const tarea = document.getElementById("tareaAlumno").value.trim();
  const dia = document.getElementById("diaAlumno").value;
  const hora = document.getElementById("horaAlumno").value;

  if (!tarea || !dia || !hora) {
    alert("Completa todos los campos");
    return;
  }

  // Convertir día a fecha
  const hoy = new Date();
  const lunesActual = getLunesDeLaSemana(hoy);
  const indiceDia = dias.indexOf(dia);
  const fechaObj = new Date(lunesActual);
  fechaObj.setDate(lunesActual.getDate() + indiceDia);
  const fecha = fechaObj.toISOString().split('T')[0];

  await crearTarea(fecha, hora, tarea);

  // Limpiar formulario
  document.getElementById("tareaAlumno").value = "";
  document.getElementById("diaAlumno").selectedIndex = 0;
  document.getElementById("horaAlumno").selectedIndex = 0;
}

// ============================================
// MOSTRAR EVALUACIONES DEL DOCENTE
// ============================================

function mostrarEvaluacionesDelProfesor() {
  const select = document.getElementById("evaluacionesProfesor");
  select.innerHTML = `<option disabled selected>Selecciona una evaluación</option>`;

  evaluaciones.forEach(eval => {
    let texto = eval.descripcion || "Evaluación";
    
    if (eval.cursoAsignaturaDocente) {
      if (eval.cursoAsignaturaDocente.asignatura) {
        texto = eval.cursoAsignaturaDocente.asignatura.nombre + ": " + texto;
      }
      if (eval.cursoAsignaturaDocente.curso) {
        texto += ` (${eval.cursoAsignaturaDocente.curso.nombre})`;
      }
    }
    
    texto += ` - ${eval.fecha} ${eval.hora}`;
    
    const option = document.createElement("option");
    option.value = eval.id;
    option.textContent = texto;
    select.appendChild(option);
  });
}

// ============================================
// ELIMINAR EVALUACIÓN SELECCIONADA
// ============================================

async function eliminarEvaluacionSeleccionada() {
  const select = document.getElementById("evaluacionesProfesor");
  const evalId = select.value;

  if (!evalId || evalId === "Selecciona una evaluación") {
    alert("Selecciona una evaluación válida.");
    return;
  }

  const confirmacion = confirm("¿Estás seguro de que quieres eliminar esta evaluación?");
  if (confirmacion) {
    await eliminarEvaluacion(evalId);
  }
}

// ============================================
// MOSTRAR TAREAS DEL ALUMNO
// ============================================

function mostrarTareasDelAlumno() {
  const select = document.getElementById("tareasAlumno");
  select.innerHTML = `<option disabled selected>Selecciona una tarea</option>`;

  tareas.forEach(tarea => {
    const texto = `${tarea.titulo} - ${tarea.fecha} ${tarea.hora || ''}`;
    
    const option = document.createElement("option");
    option.value = tarea.id;
    option.textContent = texto;
    select.appendChild(option);
  });
}

// ============================================
// ELIMINAR TAREA SELECCIONADA
// ============================================

async function eliminarTareaSeleccionada() {
  const select = document.getElementById("tareasAlumno");
  const tareaId = select.value;

  if (!tareaId || tareaId === "Selecciona una tarea") {
    alert("Selecciona una tarea válida.");
    return;
  }

  const confirmacion = confirm("¿Estás seguro de que quieres eliminar esta tarea?");
  if (confirmacion) {
    await eliminarTarea(tareaId);
  }
}

// ============================================
// UTILIDADES
// ============================================

function getLunesDeLaSemana(fecha) {
  const dia = fecha.getDay();
  const diff = fecha.getDate() - dia + (dia === 0 ? -6 : 1);
  return new Date(fecha.setDate(diff));
}

function cerrarSesion() {
  location.reload();
}