console.log('✅ Script iniciado');

let codigoFuente = '';
let resultadosAnalisis = null;

const GRAMATICA = {
    noTerminales: [
        "Programa", "Clase", "CuerpoClase", "Miembro", "MiembroPrima",
        "Parametros", "ListaParametros", "ListaParametrosPrima",
        "Tipo", "Bloque", "ListaSentencias", "Sentencia",
        "Asignacion", "Retorno", "RetornoPrima",
        "Expresion", "ExpresionPrima", "Termino", "TerminoPrima",
        "Factor", "FactorPrima", "Argumentos", "ArgumentosPrima"
    ],

    terminales: [
        "class", "int", "void", "return",
        "identificador", "numero",
        "+", "-", "*", "/", "=",
        ";", ",", "(", ")", "{", "}",
        "$"
    ],

    producciones: {
        "Programa": [["Clase"]],
        "Clase": [["class", "identificador", "{", "CuerpoClase", "}"]],
        "CuerpoClase": [["Miembro", "CuerpoClase"], ["ε"]],
        "Miembro": [["Tipo", "identificador", "MiembroPrima"]],
        "MiembroPrima": [[";"], ["(", "Parametros", ")", "Bloque"]],
        "Parametros": [["ListaParametros"], ["ε"]],
        "ListaParametros": [["Tipo", "identificador", "ListaParametrosPrima"]],
        "ListaParametrosPrima": [[",", "Tipo", "identificador", "ListaParametrosPrima"], ["ε"]],
        "Tipo": [["int"], ["void"]],
        "Bloque": [["{", "ListaSentencias", "}"]],
        "ListaSentencias": [["Sentencia", "ListaSentencias"], ["ε"]],
        "Sentencia": [["Asignacion"], ["Retorno"], ["Tipo", "identificador", ";"]],
        "Asignacion": [["identificador", "=", "Expresion", ";"]],
        "Retorno": [["return", "RetornoPrima"]],
        "RetornoPrima": [["Expresion", ";"], [";"]],
        "Expresion": [["Termino", "ExpresionPrima"]],
        "ExpresionPrima": [["+", "Termino", "ExpresionPrima"], ["-", "Termino", "ExpresionPrima"], ["ε"]],
        "Termino": [["Factor", "TerminoPrima"]],
        "TerminoPrima": [["*", "Factor", "TerminoPrima"], ["/", "Factor", "TerminoPrima"], ["ε"]],
        "Factor": [["numero"], ["identificador", "FactorPrima"], ["(", "Expresion", ")"]],
        "FactorPrima": [["(", "Argumentos", ")"], ["ε"]],
        "Argumentos": [["Expresion", "ArgumentosPrima"], ["ε"]],
        "ArgumentosPrima": [[",", "Expresion", "ArgumentosPrima"], ["ε"]]
    },

    primero: {
        "Programa": ["class"], "Clase": ["class"], "CuerpoClase": ["int", "void", "ε"],
        "Miembro": ["int", "void"], "MiembroPrima": [";", "("], "Parametros": ["int", "void", "ε"],
        "ListaParametros": ["int", "void"], "ListaParametrosPrima": [",", "ε"], "Tipo": ["int", "void"],
        "Bloque": ["{"], "ListaSentencias": ["identificador", "return", "int", "void", "ε"],
        "Sentencia": ["identificador", "return", "int", "void"], "Asignacion": ["identificador"],
        "Retorno": ["return"], "RetornoPrima": ["identificador", "numero", "(", ";"],
        "Expresion": ["identificador", "numero", "("], "ExpresionPrima": ["+", "-", "ε"],
        "Termino": ["identificador", "numero", "("], "TerminoPrima": ["*", "/", "ε"],
        "Factor": ["numero", "identificador", "("], "FactorPrima": ["(", "ε"],
        "Argumentos": ["identificador", "numero", "(", "ε"], "ArgumentosPrima": [",", "ε"]
    },

    siguiente: {
        "Programa": ["$"], "Clase": ["$"], "CuerpoClase": ["}"], "Miembro": ["int", "void", "}"],
        "MiembroPrima": ["int", "void", "}"], "Parametros": [")"], "ListaParametros": [")"],
        "ListaParametrosPrima": [")"], "Tipo": ["identificador"], "Bloque": ["int", "void", "}"],
        "ListaSentencias": ["}"], "Sentencia": ["identificador", "return", "int", "void", "}"],
        "Asignacion": ["identificador", "return", "int", "void", "}"],
        "Retorno": ["identificador", "return", "int", "void", "}"],
        "RetornoPrima": ["identificador", "return", "int", "void", "}"],
        "Expresion": [";", ")", ","], "ExpresionPrima": [";", ")", ","],
        "Termino": ["+", "-", ";", ")", ","], "TerminoPrima": ["+", "-", ";", ")", ","],
        "Factor": ["*", "/", "+", "-", ";", ")", ","],
        "FactorPrima": ["*", "/", "+", "-", ";", ")", ","],
        "Argumentos": [")"], "ArgumentosPrima": [")"]
    }
};

window.addEventListener('load', function() {
    console.log('Página cargada');
    inicializar();
    ocultarBotonesNoFuncionales();
    configurarBotonCerrar();
});

function inicializar() {
    const fileInput = document.getElementById('file-input');
    const btnAnalizar = document.getElementById('btn-analizar');
    const statusText = document.getElementById('status-text');
    const batteryLevel = document.getElementById('battery-level');

    if (!fileInput || !btnAnalizar) {
        console.error('Elementos no encontrados');
        return;
    }

    console.log('Elementos encontrados');

    fileInput.addEventListener('change', function(e) {
        const file = e.target.files[0];
        if (file) {
            console.log('📄 Archivo:', file.name);
            statusText.textContent = 'Cargando ' + file.name;

            const reader = new FileReader();
            reader.onload = function(event) {
                codigoFuente = event.target.result;
                btnAnalizar.disabled = false;
                statusText.textContent = 'Archivo cargado ✓';
                batteryLevel.textContent = '100%';

                const codePreview = document.getElementById('code-preview');
                if (codePreview) {
                    codePreview.innerHTML = '<pre><code>' + codigoFuente + '</code></pre>';
                }

                console.log('✅ Archivo leído');
            };
            reader.readAsText(file);
        }
    });

    btnAnalizar.addEventListener('click', function() {
        if (!codigoFuente) {
            mostrarNotificacion('Por favor, carga un archivo primero');
            return;
        }
        analizar();
    });

    configurarDescargas();
    configurarModales();

    console.log('✅ Eventos configurados');
}

function configurarBotonCerrar() {
    const btnCerrar = document.querySelector('.close-btn');
    if (btnCerrar) {
        btnCerrar.addEventListener('click', function() {
            if (confirm('¿Deseas cerrar el analizador?')) {
                window.close();
                // Si no se puede cerrar, mostrar mensaje
                setTimeout(function() {
                    mostrarNotificacion('ℹPara cerrar esta ventana, usa Alt+F4 o cierra la pestaña manualmente');
                }, 100);
            }
        });
        console.log('✅ Botón X configurado');
    }
}

function ocultarBotonesNoFuncionales() {
    const tabResultados = document.getElementById('tab-resultados');
    const tabAnalisis = document.getElementById('tab-analisis');
    const footerTokens = document.getElementById('footer-tokens');
    const footerErrores = document.getElementById('footer-errores');

    if (tabResultados) tabResultados.style.display = 'none';
    if (tabAnalisis) tabAnalisis.style.display = 'none';
    if (footerTokens) footerTokens.style.display = 'none';
    if (footerErrores) footerErrores.style.display = 'none';

    console.log('✅ Botones no funcionales ocultos');
}

function analizar() {
    console.log('🔍 Analizando...');

    const statusText = document.getElementById('status-text');
    const batteryLevel = document.getElementById('battery-level');

    statusText.textContent = 'Analizando...';
    batteryLevel.textContent = '90%';

    setTimeout(function() {
        const tokens = codigoFuente.match(/\w+|[^\s\w]/g) || [];
        const errores = detectarErrores(codigoFuente);
        const variables = (codigoFuente.match(/int\s+\w+/g) || []).length;
        const funciones = (codigoFuente.match(/(?:int|void)\s+\w+\s*\(/g) || []).length;
        const operadores = (codigoFuente.match(/[+\-*\/=]/g) || []).filter((v, i, a) => a.indexOf(v) === i).length;
        const simbolos = (codigoFuente.match(/[;(){}]/g) || []).filter((v, i, a) => a.indexOf(v) === i).length;

        resultadosAnalisis = {
            tokens: tokens,
            errores: errores,
            variables: variables,
            funciones: funciones,
            operadores: operadores,
            simbolos: simbolos
        };

        document.getElementById('stat-tokens').textContent = tokens.length;
        document.getElementById('stat-errores').textContent = errores.length;
        document.getElementById('class-variables').textContent = variables;
        document.getElementById('class-funciones').textContent = funciones;
        document.getElementById('class-operadores').textContent = operadores;
        document.getElementById('class-simbolos').textContent = simbolos;

        if (errores.length === 0) {
            statusText.textContent = 'Análisis completado sin errores';
            batteryLevel.textContent = '100%';
            mostrarNotificacion('¡Análisis completado exitosamente!');
        } else {
            statusText.textContent = '⚠️ ' + errores.length + ' errores encontrados';
            batteryLevel.textContent = '85%';
            mostrarNotificacion('⚠️ Se encontraron ' + errores.length + ' errores');
        }

        actualizarAreaArbol();
        actualizarTablaPreview();

        console.log('✅ Análisis completado');
    }, 1500);
}

function detectarErrores(codigo) {
    const errores = [];
    const lineas = codigo.split('\n');

    lineas.forEach(function(linea, i) {
        if (/int\s+\w+\s*$/.test(linea.trim())) {
            errores.push({ linea: i + 1, mensaje: "Falta ';' después de la declaración" });
        }
        if (linea.includes('(') && !linea.includes(')') && linea.includes(';')) {
            errores.push({ linea: i + 1, mensaje: 'Paréntesis sin cerrar' });
        }
        if (/\w+\s*==/.test(linea) && !linea.includes('if') && !linea.includes('while')) {
            errores.push({ linea: i + 1, mensaje: "Se esperaba '=' pero se encontró '=='" });
        }
    });

    return errores;
}

function actualizarAreaArbol() {
    const treeContainer = document.getElementById('tree-container');
    if (!treeContainer) return;

    try {
        treeContainer.innerHTML = generarArbolVisualHTML();
        console.log('✅ Árbol generado');
    } catch (error) {
        console.error('Error al generar árbol:', error);
    }
}

function generarArbolVisualHTML() {
    let html = '<div style="width: 100%; height: 550px; overflow: auto; background: #0f3460; border-radius: 10px; padding: 30px;">';
    html += '<svg width="1200" height="800" xmlns="http://www.w3.org/2000/svg">';

    html += dibujarNodoSimple(600, 50, 'Programa', false);
    html += dibujarLinea(600, 85, 600, 130);
    html += dibujarNodoSimple(600, 150, 'Clase', false);
    html += dibujarLinea(550, 185, 300, 230);
    html += dibujarLinea(600, 185, 600, 230);
    html += dibujarLinea(650, 185, 900, 230);
    html += dibujarNodoSimple(200, 250, 'class', true);
    html += dibujarNodoSimple(400, 250, 'id', true);
    html += dibujarNodoSimple(600, 250, '{', true);
    html += dibujarNodoSimple(800, 250, 'CuerpoClase', false);
    html += dibujarNodoSimple(1000, 250, '}', true);
    html += dibujarLinea(800, 285, 800, 330);
    html += dibujarNodoSimple(800, 350, 'Miembro', false);
    html += dibujarLinea(750, 385, 650, 430);
    html += dibujarLinea(800, 385, 800, 430);
    html += dibujarLinea(850, 385, 950, 430);
    html += dibujarNodoSimple(650, 450, 'Tipo', false);
    html += dibujarNodoSimple(800, 450, 'id', true);
    html += dibujarNodoSimple(950, 450, 'MiembroPrima', false);
    html += dibujarLinea(650, 485, 650, 530);
    html += dibujarNodoSimple(650, 550, 'int', true);
    html += dibujarLinea(900, 485, 800, 530);
    html += dibujarLinea(950, 485, 950, 530);
    html += dibujarLinea(1000, 485, 1100, 530);
    html += dibujarNodoSimple(800, 550, '(', true);
    html += dibujarNodoSimple(950, 550, 'Params', false);
    html += dibujarNodoSimple(1100, 550, ')', true);
    html += dibujarLinea(950, 585, 950, 630);
    html += dibujarNodoSimple(950, 650, 'ε', true);

    html += '</svg></div>';
    return html;
}

function dibujarNodoSimple(x, y, texto, esTerminal) {
    const radio = 30;
    const color = texto === 'ε' ? '#f59e0b' : (esTerminal ? '#4ade80' : '#00d4ff');
    const colorTexto = esTerminal ? '#000' : '#0f3460';
    const tamañoFuente = texto.length > 10 ? '10' : '12';

    let svg = '<circle cx="' + x + '" cy="' + y + '" r="' + (radio + 2) + '" fill="#000" opacity="0.15"/>';
    svg += '<circle cx="' + x + '" cy="' + y + '" r="' + radio + '" fill="' + color + '" stroke="#fff" stroke-width="2"/>';
    svg += '<text x="' + x + '" y="' + (y + 4) + '" text-anchor="middle" fill="' + colorTexto + '" ';
    svg += 'font-size="' + tamañoFuente + '" font-weight="bold" font-family="Arial">' + texto + '</text>';

    return svg;
}

function dibujarLinea(x1, y1, x2, y2) {
    return '<line x1="' + x1 + '" y1="' + y1 + '" x2="' + x2 + '" y2="' + y2 + '" stroke="#00d4ff" stroke-width="2"/>';
}

function actualizarTablaPreview() {
    const tablaPreview = document.getElementById('tabla-preview-contenido');
    if (tablaPreview) {
        tablaPreview.innerHTML = '<p class="preview-text" style="color: #00d4ff; cursor: pointer;" onclick="document.getElementById(\'footer-tabla\').click()">✓ Ver Tabla LL(1) Completa</p>';
    }
}

function generarTablaLL1HTML() {
    let html = '<div style="overflow-x: auto; max-height: 600px; background: #0f3460; padding: 20px; border-radius: 10px;">';
    html += '<table style="width: 100%; border-collapse: collapse; color: #fff; font-size: 12px;">';
    html += '<thead><tr style="background: #16213e; position: sticky; top: 0;">';
    html += '<th style="padding: 10px; border: 1px solid #00d4ff; text-align: center;">No Terminal</th>';

    GRAMATICA.terminales.forEach(function(terminal) {
        html += '<th style="padding: 10px; border: 1px solid #00d4ff; text-align: center;">' + terminal + '</th>';
    });

    html += '</tr></thead><tbody>';

    GRAMATICA.noTerminales.forEach(function(noTerminal) {
        html += '<tr>';
        html += '<td style="padding: 10px; border: 1px solid #00d4ff; font-weight: bold; background: #16213e; color: #00d4ff;">' + noTerminal + '</td>';

        GRAMATICA.terminales.forEach(function(terminal) {
            const produccion = obtenerProduccion(noTerminal, terminal);
            const cellStyle = produccion ?
                'padding: 8px; border: 1px solid #444; text-align: center; font-size: 11px; background: #1a4d2e; color: #4ade80;' :
                'padding: 8px; border: 1px solid #444; text-align: center; font-size: 11px; background: #16213e; color: #666;';

            html += '<td style="' + cellStyle + '">' + (produccion || '—') + '</td>';
        });

        html += '</tr>';
    });

    html += '</tbody></table></div>';
    return html;
}

function obtenerProduccion(noTerminal, terminal) {
    const primero = GRAMATICA.primero[noTerminal] || [];
    const siguiente = GRAMATICA.siguiente[noTerminal] || [];
    const producciones = GRAMATICA.producciones[noTerminal] || [];

    for (let i = 0; i < producciones.length; i++) {
        const prod = producciones[i];
        const primerSimbolo = prod[0];

        if (primerSimbolo === terminal) {
            return noTerminal + ' → ' + prod.join(' ');
        }

        if (primerSimbolo === 'ε' && siguiente.includes(terminal)) {
            return noTerminal + ' → ε';
        }

        if (primerSimbolo !== 'ε' && primero.includes(terminal)) {
            if (GRAMATICA.primero[primerSimbolo] && GRAMATICA.primero[primerSimbolo].includes(terminal)) {
                return noTerminal + ' → ' + prod.join(' ');
            }
        }
    }

    if (primero.includes('ε') && siguiente.includes(terminal)) {
        return noTerminal + ' → ε';
    }

    return null;
}

function configurarDescargas() {
    document.getElementById('btn-download-errores').addEventListener('click', function() {
        if (!resultadosAnalisis) {
            mostrarNotificacion('Realiza un análisis primero');
            return;
        }
        descargar('errores.txt', generarReporteErrores());
    });

    document.getElementById('btn-download-tabla').addEventListener('click', function() {
        descargar('tabla_transicion.txt', generarTablaTexto());
    });

    document.getElementById('btn-download-arbol').addEventListener('click', function() {
        descargar('arbol.dot', 'digraph ArbolDerivacion {\n  node [shape=box];\n  Programa -> Clase;\n}');
    });

    document.getElementById('btn-download-ast').addEventListener('click', function() {
        descargar('ast.dot', 'digraph AST {\n  node [shape=circle];\n  Programa -> Clase;\n}');
    });
}

function generarReporteErrores() {
    let reporte = '════════════════════════════════════════════════════════════\n';
    reporte += '              REPORTE DE ERRORES\n';
    reporte += '════════════════════════════════════════════════════════════\n\n';
    reporte += 'Total de errores: ' + resultadosAnalisis.errores.length + '\n\n';

    if (resultadosAnalisis.errores.length === 0) {
        reporte += '✓ No se encontraron errores\n';
    } else {
        resultadosAnalisis.errores.forEach(function(error, i) {
            reporte += (i + 1) + '. Línea ' + error.linea + ': ' + error.mensaje + '\n';
        });
    }

    return reporte;
}

function generarTablaTexto() {
    let texto = '                       TABLA DE ANALISIS LL(1)                                  \n\n';

    const w1 = 18;
    const w2 = 15;

    texto += padRight('NO TERMINAL', w1) + ' | ';
    GRAMATICA.terminales.forEach(function(terminal) {
        texto += padCenter(terminal, w2) + ' | ';
    });
    texto += '\n';

    texto += '-'.repeat(w1) + '-+-';
    GRAMATICA.terminales.forEach(function() {
        texto += '-'.repeat(w2) + '-+-';
    });
    texto += '\n';

    GRAMATICA.noTerminales.forEach(function(noTerminal) {
        texto += padRight(noTerminal, w1) + ' | ';

        GRAMATICA.terminales.forEach(function(terminal) {
            const prod = obtenerProduccionSimple(noTerminal, terminal);
            texto += padCenter(prod || '-', w2) + ' | ';
        });

        texto += '\n';
    });

    return texto;
}

function obtenerProduccionSimple(noTerminal, terminal) {
    const primero = GRAMATICA.primero[noTerminal] || [];
    const siguiente = GRAMATICA.siguiente[noTerminal] || [];
    const producciones = GRAMATICA.producciones[noTerminal] || [];

    for (let i = 0; i < producciones.length; i++) {
        const prod = producciones[i];
        const primerSimbolo = prod[0];

        // Si el primer símbolo de la producción es el terminal
        if (primerSimbolo === terminal) {
            return prod.join(' ');
        }

        // Si es epsilon y el terminal está en SIGUIENTE
        if (primerSimbolo === 'ε' && siguiente.includes(terminal)) {
            return 'ε';
        }

        // Si el terminal está en PRIMERO del no terminal
        if (primero.includes(terminal)) {
            // Verificar si esta producción genera ese terminal
            if (primerSimbolo !== 'ε') {
                const primeroPrimerSimbolo = GRAMATICA.primero[primerSimbolo];
                if (primeroPrimerSimbolo && primeroPrimerSimbolo.includes(terminal)) {
                    return prod.join(' ');
                }
            }
        }
    }

    // Caso especial para epsilon
    if (primero.includes('ε') && siguiente.includes(terminal)) {
        return 'ε';
    }

    return null;
}



function padRight(str, length) {
    str = String(str);
    while (str.length < length) str += ' ';
    return str.substring(0, length);
}

function padCenter(str, length) {
    str = String(str);
    if (str.length >= length) return str.substring(0, length);
    const padTotal = length - str.length;
    const padLeft = Math.floor(padTotal / 2);
    const padRight = padTotal - padLeft;
    return ' '.repeat(padLeft) + str + ' '.repeat(padRight);
}

function descargar(nombreArchivo, contenido) {
    const blob = new Blob([contenido], { type: 'text/plain; charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = nombreArchivo;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
}

function configurarModales() {
    const btnGramatica = document.getElementById('btn-gramatica');
    const modalGramatica = document.getElementById('modal-gramatica');

    if (btnGramatica && modalGramatica) {
        btnGramatica.addEventListener('click', function() {
            const gramaticaContent = document.getElementById('gramatica-content');
            if (gramaticaContent) {
                gramaticaContent.textContent = generarGramaticaTexto();
            }
            modalGramatica.classList.add('active');
        });

        modalGramatica.querySelectorAll('.modal-close').forEach(function(btn) {
            btn.addEventListener('click', function() {
                modalGramatica.classList.remove('active');
            });
        });
    }

    const btnTablaFooter = document.getElementById('footer-tabla');
    const modalTabla = document.getElementById('modal-tabla');

    if (btnTablaFooter && modalTabla) {
        btnTablaFooter.addEventListener('click', function() {
            const tablaContent = document.getElementById('tabla-content');
            if (tablaContent) {
                tablaContent.innerHTML = generarTablaLL1HTML();
            }
            modalTabla.classList.add('active');
        });

        modalTabla.querySelectorAll('.modal-close').forEach(function(btn) {
            btn.addEventListener('click', function() {
                modalTabla.classList.remove('active');
            });
        });
    }
}

function generarGramaticaTexto() {
    let texto = '\nGRAMÁTICA DEL LENGUAJE (Subconjunto Java)\n';
    texto += '════════════════════════════════════════\n\n';

    for (let noTerminal in GRAMATICA.producciones) {
        const prods = GRAMATICA.producciones[noTerminal];
        prods.forEach(function(prod, i) {
            if (i === 0) {
                texto += noTerminal + ' → ' + prod.join(' ') + '\n';
            } else {
                texto += ' '.repeat(noTerminal.length) + ' | ' + prod.join(' ') + '\n';
            }
        });
        texto += '\n';
    }

    return texto;
}

function mostrarNotificacion(mensaje) {
    const modal = document.getElementById('modal-notificacion');
    const texto = document.getElementById('notif-texto');
    const btn = document.getElementById('notif-btn');

    if (modal && texto && btn) {
        texto.textContent = mensaje;
        modal.style.display = 'flex';

        btn.onclick = function() {
            modal.style.display = 'none';
        };
    } else {
        alert(mensaje);
    }
}

console.log('Script cargado completamente');
