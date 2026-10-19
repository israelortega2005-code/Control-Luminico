package com.example.luminicocontrol

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.switchmaterial.SwitchMaterial
import java.net.HttpURLConnection
import java.net.URL

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {

    private val esp32Ip = "http://192.168.4.1"
    private var ultimoComandoMs: Long = 0
    private var esMinutoActivo = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnBack = findViewById<TextView>(R.id.btnBack)
        val tvSubtituloConexion = findViewById<TextView>(R.id.tvSubtituloConexion)
        val tvNivelAudio = findViewById<TextView>(R.id.tvNivelAudio)
        val tvEstadoPir = findViewById<TextView>(R.id.tvEstadoPir)

        val switchLuces = findViewById<SwitchMaterial>(R.id.btnApagar)
        val switchPir = findViewById<SwitchMaterial>(R.id.switchPir)
        val switchMic = findViewById<SwitchMaterial>(R.id.switchMic)
        val switchSeguridadMaster = findViewById<SwitchMaterial>(R.id.switchSeguridadMaster)
        val switchSeguridadPermanente = findViewById<SwitchMaterial>(R.id.switchSeguridadPermanente)

        val cardHeroDevice = findViewById<MaterialCardView>(R.id.cardHeroDevice)

        // ---DISEÑO DE INTERRUPTORES ---
        val estadosSwitch = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked)
        )
        val coloresTrack = intArrayOf(0xFF00B4D8.toInt(), 0xFFCBD5E1.toInt())
        val coloresThumb = intArrayOf(0xFFFFFFFF.toInt(), 0xFFFFFFFF.toInt())
        val trackList = android.content.res.ColorStateList(estadosSwitch, coloresTrack)
        val thumbList = android.content.res.ColorStateList(estadosSwitch, coloresThumb)

        switchLuces.trackTintList = trackList
        switchLuces.thumbTintList = thumbList
        switchPir.trackTintList = trackList
        switchPir.thumbTintList = thumbList
        switchMic.trackTintList = trackList
        switchMic.thumbTintList = thumbList
        switchSeguridadMaster.trackTintList = trackList
        switchSeguridadMaster.thumbTintList = thumbList
        switchSeguridadPermanente.trackTintList = trackList
        switchSeguridadPermanente.thumbTintList = thumbList

        // --- MODOS ---
        val cardColor = findViewById<MaterialCardView>(R.id.btnModoColor)
        val cardSeguridad = findViewById<MaterialCardView>(R.id.btnModoSeguridad)
        val cardRitmo = findViewById<MaterialCardView>(R.id.btnModoRitmo)
        val cardMulticolor = findViewById<MaterialCardView>(R.id.btnModoGaming)
        val cardNeon = findViewById<MaterialCardView>(R.id.btnModoFreeze)
        val cardOnda = findViewById<MaterialCardView>(R.id.btnModoWave)

        // --- SUB-PANELES ---
        val panelSubModosRitmo = findViewById<android.widget.HorizontalScrollView>(R.id.panelSubModosRitmo)
        val panelSubModosColor = findViewById<android.widget.HorizontalScrollView>(R.id.panelSubModosColor)
        val panelSubModosNeon = findViewById<android.widget.HorizontalScrollView>(R.id.panelSubModosNeon)
        val panelSubModosSeguridad = findViewById<LinearLayout>(R.id.panelSubModosSeguridad)

        // Sub-modos Ritmo
        val cardRitmoAuto = findViewById<MaterialCardView>(R.id.cardRitmoAuto)
        val cardRitmoVu = findViewById<MaterialCardView>(R.id.cardRitmoVu)
        val cardRitmoEspectro = findViewById<MaterialCardView>(R.id.cardRitmoEspectro)
        val cardRitmoBeat = findViewById<MaterialCardView>(R.id.cardRitmoBeat)
        val cardRitmoFuego = findViewById<MaterialCardView>(R.id.cardRitmoFuego)
        val cardRitmoDestellos = findViewById<MaterialCardView>(R.id.cardRitmoDestellos)
        val cardRitmoPulso = findViewById<MaterialCardView>(R.id.cardRitmoPulso)

        // Sub-modos Neón
        val cardNeonClasico = findViewById<MaterialCardView>(R.id.cardNeonClasico)
        val cardNeonArcoiris = findViewById<MaterialCardView>(R.id.cardNeonArcoiris)
        val cardNeonCyberpunk = findViewById<MaterialCardView>(R.id.cardNeonCyberpunk)
        val cardNeonRespiracion = findViewById<MaterialCardView>(R.id.cardNeonRespiracion)
        val cardNeonPlasma = findViewById<MaterialCardView>(R.id.cardNeonPlasma)

        // Sub-modos Color
        val btnColorRojo = findViewById<MaterialCardView>(R.id.btnColorRojo)
        val btnColorNaranja = findViewById<MaterialCardView>(R.id.btnColorNaranja)
        val btnColorVerde = findViewById<MaterialCardView>(R.id.btnColorVerde)
        val btnColorCian = findViewById<MaterialCardView>(R.id.btnColorCian)
        val btnColorAzul = findViewById<MaterialCardView>(R.id.btnColorAzul)
        val btnColorRosa = findViewById<MaterialCardView>(R.id.btnColorRosa)
        val btnColorBlanco = findViewById<MaterialCardView>(R.id.btnColorBlanco)

        // Sub-modos Seguridad
        val cardSegBaliza = findViewById<MaterialCardView>(R.id.cardSegBaliza)
        val cardSegEstrobo = findViewById<MaterialCardView>(R.id.cardSegEstrobo)
        val cardSegAlerta = findViewById<MaterialCardView>(R.id.cardSegAlerta)
        val cardSegTv = findViewById<MaterialCardView>(R.id.cardSegTv)

        // Temporizador Seguridad
        val etSeguridadTiempo = findViewById<EditText>(R.id.etSeguridadTiempo)
        val cardTiempoSeg = findViewById<MaterialCardView>(R.id.cardTiempoSeg)
        val cardTiempoMin = findViewById<MaterialCardView>(R.id.cardTiempoMin)
        val tvTiempoSeg = findViewById<TextView>(R.id.tvTiempoSeg)
        val tvTiempoMin = findViewById<TextView>(R.id.tvTiempoMin)

        // Referencias a Sensibilidad
        val cardSensEstandar = findViewById<MaterialCardView>(R.id.cardSensEstandar)
        val tvSensEstandar = findViewById<TextView>(R.id.tvSensEstandar)
        val cardSensSuave = findViewById<MaterialCardView>(R.id.cardSensSuave)
        val tvSensSuave = findViewById<TextView>(R.id.tvSensSuave)
        val cardSensFiesta = findViewById<MaterialCardView>(R.id.cardSensFiesta)
        val tvSensFiesta = findViewById<TextView>(R.id.tvSensFiesta)

        val todasLasTarjetas = listOf(cardColor, cardRitmo, cardMulticolor, cardNeon, cardOnda, cardSeguridad)
        val todasLasTarjetasRitmo = listOf(cardRitmoAuto, cardRitmoVu, cardRitmoEspectro, cardRitmoBeat, cardRitmoFuego, cardRitmoDestellos, cardRitmoPulso)
        val todasLasTarjetasNeon = listOf(cardNeonClasico, cardNeonArcoiris, cardNeonCyberpunk, cardNeonRespiracion, cardNeonPlasma)
        val todasLasTarjetasSeguridad = listOf(cardSegBaliza, cardSegEstrobo, cardSegAlerta, cardSegTv)
        val todosLosColores = listOf(btnColorRojo, btnColorNaranja, btnColorVerde, btnColorCian, btnColorAzul, btnColorRosa, btnColorBlanco)

        fun resaltarTarjetaActiva(tarjetaSeleccionada: MaterialCardView) {
            val densidad = resources.displayMetrics.density
            for (tarjeta in todasLasTarjetas) {
                if (tarjeta == tarjetaSeleccionada) {
                    tarjeta.strokeColor = 0xFF00B4D8.toInt()
                    tarjeta.strokeWidth = (3 * densidad).toInt()
                } else {
                    tarjeta.strokeColor = 0xFFFFFFFF.toInt()
                    tarjeta.strokeWidth = (1.5f * densidad).toInt()
                }
            }
        }

        fun seleccionarSubModoGeneral(tarjetas: List<MaterialCardView>, cardSeleccionada: MaterialCardView, tieneSubtitulo: Boolean) {
            val densidad = resources.displayMetrics.density
            for (card in tarjetas) {
                val layoutInterno = if (tieneSubtitulo) card.getChildAt(0) as LinearLayout else null
                val tvTitulo = if (tieneSubtitulo) layoutInterno?.getChildAt(0) as TextView else card.getChildAt(0) as TextView
                val tvSubtitulo = if (tieneSubtitulo) layoutInterno?.getChildAt(1) as TextView else null

                if (card == cardSeleccionada) {
                    card.setCardBackgroundColor(0xFF00B4D8.toInt())
                    card.strokeWidth = 0
                    tvTitulo.setTextColor(0xFFFFFFFF.toInt())
                    tvSubtitulo?.setTextColor(0xFFE0F2FE.toInt())
                } else {
                    card.setCardBackgroundColor(0xFFFFFFFF.toInt())
                    card.strokeColor = 0xFFCBD5E1.toInt()
                    card.strokeWidth = (1 * densidad).toInt()
                    tvTitulo.setTextColor(0xFF0F172A.toInt())
                    tvSubtitulo?.setTextColor(0xFF64748B.toInt())
                }
            }
        }

        fun seleccionarSelectorColor(cardSeleccionada: MaterialCardView) {
            val densidad = resources.displayMetrics.density
            for (card in todosLosColores) {
                if (card == cardSeleccionada) {
                    card.strokeWidth = (3 * densidad).toInt()
                    card.strokeColor = 0xFFFFFFFF.toInt()
                } else {
                    card.strokeWidth = (1 * densidad).toInt()
                    card.strokeColor = 0x88FFFFFF.toInt()
                }
            }
            btnColorBlanco.strokeColor = 0xFFE2E8F0.toInt()
            if(cardSeleccionada == btnColorBlanco) btnColorBlanco.strokeWidth = (3 * densidad).toInt()
        }

        fun actualizarSensibilidad(cardActiva: MaterialCardView) {
            val tarjetas = listOf(cardSensEstandar, cardSensSuave, cardSensFiesta)
            val textos = listOf(tvSensEstandar, tvSensSuave, tvSensFiesta)
            val densidad = resources.displayMetrics.density

            for (i in tarjetas.indices) {
                if (tarjetas[i] == cardActiva) {
                    tarjetas[i].setCardBackgroundColor(0xFF00B4D8.toInt())
                    tarjetas[i].strokeWidth = 0
                    textos[i].setTextColor(0xFFFFFFFF.toInt())
                } else {
                    tarjetas[i].setCardBackgroundColor(0xFFF8FAFC.toInt())
                    tarjetas[i].strokeColor = 0xFFE2E8F0.toInt()
                    tarjetas[i].strokeWidth = (1 * densidad).toInt()
                    textos[i].setTextColor(0xFF334155.toInt())
                }
            }
        }

        fun actualizarUnidadTiempo(esMinuto: Boolean) {
            esMinutoActivo = esMinuto
            val densidad = resources.displayMetrics.density
            if (esMinuto) {
                cardTiempoMin.setCardBackgroundColor(0xFF00B4D8.toInt())
                cardTiempoMin.strokeWidth = 0
                tvTiempoMin.setTextColor(0xFFFFFFFF.toInt())
                cardTiempoSeg.setCardBackgroundColor(0xFFFFFFFF.toInt())
                cardTiempoSeg.strokeColor = 0xFFCBD5E1.toInt()
                cardTiempoSeg.strokeWidth = (1 * densidad).toInt()
                tvTiempoSeg.setTextColor(0xFF64748B.toInt())
            } else {
                cardTiempoSeg.setCardBackgroundColor(0xFF00B4D8.toInt())
                cardTiempoSeg.strokeWidth = 0
                tvTiempoSeg.setTextColor(0xFFFFFFFF.toInt())
                cardTiempoMin.setCardBackgroundColor(0xFFFFFFFF.toInt())
                cardTiempoMin.strokeColor = 0xFFCBD5E1.toInt()
                cardTiempoMin.strokeWidth = (1 * densidad).toInt()
                tvTiempoMin.setTextColor(0xFF64748B.toInt())
            }
        }

        fun generarComandoSeguridad(efecto: String): String {
            val fijo = if (switchSeguridadPermanente.isChecked) "1" else "0"
            val tiempo = etSeguridadTiempo.text.toString()
            val unidad = if (esMinutoActivo) "min" else "seg"
            return "/modo?efecto=$efecto&tiempo=$tiempo&unidad=$unidad&fijo=$fijo"
        }

        // Temporizador
        cardTiempoMin.setOnClickListener { actualizarUnidadTiempo(true) }
        cardTiempoSeg.setOnClickListener { actualizarUnidadTiempo(false) }
        switchSeguridadPermanente.setOnCheckedChangeListener { _, isChecked ->
            etSeguridadTiempo.isEnabled = !isChecked
            cardTiempoMin.isEnabled = !isChecked
            cardTiempoSeg.isEnabled = !isChecked
            val alpha = if (isChecked) 0.5f else 1.0f
            etSeguridadTiempo.alpha = alpha
            cardTiempoMin.alpha = alpha
            cardTiempoSeg.alpha = alpha
        }

        cardHeroDevice.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                tvSubtituloConexion.text = "WS2812B • BUSCANDO..."
                enviarComandoEsp32("/", "Verificando conexión al ESP32", tvSubtituloConexion)
            }
        }

        // ---  MODOS PRINCIPALES ---
        cardColor.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                resaltarTarjetaActiva(cardColor)
                panelSubModosRitmo.visibility = View.GONE
                panelSubModosNeon.visibility = View.GONE
                panelSubModosSeguridad.visibility = View.GONE
                panelSubModosColor.visibility = View.VISIBLE
                switchLuces.isChecked = true
                seleccionarSelectorColor(btnColorRojo)
                enviarComandoEsp32("/color?hex=EF4444", "Color Fijo: Rojo", tvSubtituloConexion)
            }
        }

        cardRitmo.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                resaltarTarjetaActiva(cardRitmo)
                panelSubModosColor.visibility = View.GONE
                panelSubModosNeon.visibility = View.GONE
                panelSubModosSeguridad.visibility = View.GONE
                panelSubModosRitmo.visibility = View.VISIBLE
                switchLuces.isChecked = true
                tvNivelAudio.text = "58 dB"
                seleccionarSubModoGeneral(todasLasTarjetasRitmo, cardRitmoAuto, true)
                enviarComandoEsp32("/modo?efecto=ritmo", "Modo Ritmo Audio-Reactivo", tvSubtituloConexion)
            }
        }

        cardNeon.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                resaltarTarjetaActiva(cardNeon)
                panelSubModosRitmo.visibility = View.GONE
                panelSubModosColor.visibility = View.GONE
                panelSubModosSeguridad.visibility = View.GONE
                panelSubModosNeon.visibility = View.VISIBLE
                switchLuces.isChecked = true
                seleccionarSubModoGeneral(todasLasTarjetasNeon, cardNeonClasico, true)
                enviarComandoEsp32("/modo?efecto=neon", "Modo Neón Clásico", tvSubtituloConexion)
            }
        }

        cardMulticolor.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                resaltarTarjetaActiva(cardMulticolor)
                panelSubModosRitmo.visibility = View.GONE
                panelSubModosColor.visibility = View.GONE
                panelSubModosNeon.visibility = View.GONE
                panelSubModosSeguridad.visibility = View.GONE
                switchLuces.isChecked = true
                enviarComandoEsp32("/modo?efecto=multicolor", "Modo Multicolor RGB", tvSubtituloConexion)
            }
        }

        cardOnda.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                resaltarTarjetaActiva(cardOnda)
                panelSubModosRitmo.visibility = View.GONE
                panelSubModosColor.visibility = View.GONE
                panelSubModosNeon.visibility = View.GONE
                panelSubModosSeguridad.visibility = View.GONE
                switchLuces.isChecked = true
                enviarComandoEsp32("/modo?efecto=onda", "Modo Onda Suave", tvSubtituloConexion)
            }
        }

        cardSeguridad.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                resaltarTarjetaActiva(cardSeguridad)
                panelSubModosRitmo.visibility = View.GONE
                panelSubModosColor.visibility = View.GONE
                panelSubModosNeon.visibility = View.GONE
                panelSubModosSeguridad.visibility = View.VISIBLE
                switchLuces.isChecked = true
                seleccionarSubModoGeneral(todasLasTarjetasSeguridad, cardSegBaliza, false)
                enviarComandoEsp32(generarComandoSeguridad("baliza"), "Seguridad: Baliza Activa", tvSubtituloConexion)
            }
        }

        // --- EVENTOS CÍRCULOS DE COLORES ---
        btnColorRojo.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                seleccionarSelectorColor(btnColorRojo)
                switchLuces.isChecked = true
                enviarComandoEsp32("/color?hex=EF4444", "Color: Rojo", tvSubtituloConexion)
            }
        }
        btnColorNaranja.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                seleccionarSelectorColor(btnColorNaranja)
                switchLuces.isChecked = true
                enviarComandoEsp32("/color?hex=F97316", "Color: Naranja", tvSubtituloConexion)
            }
        }
        btnColorVerde.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                seleccionarSelectorColor(btnColorVerde)
                switchLuces.isChecked = true
                enviarComandoEsp32("/color?hex=22C55E", "Color: Verde", tvSubtituloConexion)
            }
        }
        btnColorCian.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                seleccionarSelectorColor(btnColorCian)
                switchLuces.isChecked = true
                enviarComandoEsp32("/color?hex=06B6D4", "Color: Cian", tvSubtituloConexion)
            }
        }
        btnColorAzul.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                seleccionarSelectorColor(btnColorAzul)
                switchLuces.isChecked = true
                enviarComandoEsp32("/color?hex=3B82F6", "Color: Azul", tvSubtituloConexion)
            }
        }
        btnColorRosa.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                seleccionarSelectorColor(btnColorRosa)
                switchLuces.isChecked = true
                enviarComandoEsp32("/color?hex=EC4899", "Color: Rosa", tvSubtituloConexion)
            }
        }
        btnColorBlanco.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                seleccionarSelectorColor(btnColorBlanco)
                switchLuces.isChecked = true
                enviarComandoEsp32("/color?hex=FFFFFF", "Color: Blanco", tvSubtituloConexion)
            }
        }

        // --- EVENTOS SUB-MODOS RITMO ---
        cardRitmoAuto.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                seleccionarSubModoGeneral(todasLasTarjetasRitmo, cardRitmoAuto, true)
                switchLuces.isChecked = true
                enviarComandoEsp32("/modo?efecto=autorotacion", "Ritmo: Auto-Rotación", tvSubtituloConexion)
            }
        }
        cardRitmoVu.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                seleccionarSubModoGeneral(todasLasTarjetasRitmo, cardRitmoVu, true)
                switchLuces.isChecked = true
                enviarComandoEsp32("/modo?efecto=vumeter", "Ritmo: VU Meter", tvSubtituloConexion)
            }
        }
        cardRitmoEspectro.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                seleccionarSubModoGeneral(todasLasTarjetasRitmo, cardRitmoEspectro, true)
                switchLuces.isChecked = true
                enviarComandoEsp32("/modo?efecto=espectro", "Ritmo: Espectro RGB", tvSubtituloConexion)
            }
        }
        cardRitmoBeat.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                seleccionarSubModoGeneral(todasLasTarjetasRitmo, cardRitmoBeat, true)
                switchLuces.isChecked = true
                enviarComandoEsp32("/modo?efecto=beat", "Ritmo: Arcoíris Beat", tvSubtituloConexion)
            }
        }
        cardRitmoFuego.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                seleccionarSubModoGeneral(todasLasTarjetasRitmo, cardRitmoFuego, true)
                switchLuces.isChecked = true
                enviarComandoEsp32("/modo?efecto=fuego", "Ritmo: Fuego Rítmico", tvSubtituloConexion)
            }
        }
        cardRitmoDestellos.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                seleccionarSubModoGeneral(todasLasTarjetasRitmo, cardRitmoDestellos, true)
                switchLuces.isChecked = true
                enviarComandoEsp32("/modo?efecto=destellos", "Ritmo: Destellos", tvSubtituloConexion)
            }
        }
        cardRitmoPulso.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                seleccionarSubModoGeneral(todasLasTarjetasRitmo, cardRitmoPulso, true)
                switchLuces.isChecked = true
                enviarComandoEsp32("/modo?efecto=pulso", "Ritmo: Pulso Bass", tvSubtituloConexion)
            }
        }

        // --- EVENTOS SUB-MODOS NEÓN ---
        cardNeonClasico.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                seleccionarSubModoGeneral(todasLasTarjetasNeon, cardNeonClasico, true)
                switchLuces.isChecked = true
                enviarComandoEsp32("/modo?efecto=neon_clasico", "Neón: Clásico", tvSubtituloConexion)
            }
        }
        cardNeonArcoiris.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                seleccionarSubModoGeneral(todasLasTarjetasNeon, cardNeonArcoiris, true)
                switchLuces.isChecked = true
                enviarComandoEsp32("/modo?efecto=neon_arcoiris", "Neón: Arcoíris", tvSubtituloConexion)
            }
        }
        cardNeonCyberpunk.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                seleccionarSubModoGeneral(todasLasTarjetasNeon, cardNeonCyberpunk, true)
                switchLuces.isChecked = true
                enviarComandoEsp32("/modo?efecto=neon_cyberpunk", "Neón: Cyberpunk", tvSubtituloConexion)
            }
        }
        cardNeonRespiracion.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                seleccionarSubModoGeneral(todasLasTarjetasNeon, cardNeonRespiracion, true)
                switchLuces.isChecked = true
                enviarComandoEsp32("/modo?efecto=neon_respiracion", "Neón: Respiración", tvSubtituloConexion)
            }
        }
        cardNeonPlasma.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                seleccionarSubModoGeneral(todasLasTarjetasNeon, cardNeonPlasma, true)
                switchLuces.isChecked = true
                enviarComandoEsp32("/modo?efecto=neon_plasma", "Neón: Plasma", tvSubtituloConexion)
            }
        }

        // --- MODOS SEGURIDAD ---
        switchSeguridadMaster.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enviarComandoEsp32("/seguridad?estado=1", "Sistema de Seguridad Activado", tvSubtituloConexion)
            } else {
                enviarComandoEsp32("/seguridad?estado=0", "Sistema de Seguridad Desactivado", tvSubtituloConexion)
            }
        }

        cardSegBaliza.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                seleccionarSubModoGeneral(todasLasTarjetasSeguridad, cardSegBaliza, false)
                enviarComandoEsp32(generarComandoSeguridad("baliza"), "Seguridad: Baliza", tvSubtituloConexion)
            }
        }
        cardSegEstrobo.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                seleccionarSubModoGeneral(todasLasTarjetasSeguridad, cardSegEstrobo, false)
                enviarComandoEsp32(generarComandoSeguridad("estrobo"), "Seguridad: Estrobo", tvSubtituloConexion)
            }
        }
        cardSegAlerta.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                seleccionarSubModoGeneral(todasLasTarjetasSeguridad, cardSegAlerta, false)
                enviarComandoEsp32(generarComandoSeguridad("alerta"), "Seguridad: Alerta Silenciosa", tvSubtituloConexion)
            }
        }
        cardSegTv.setOnClickListener {
            if (permitirEnvioSeguridad()) {
                seleccionarSubModoGeneral(todasLasTarjetasSeguridad, cardSegTv, false)
                enviarComandoEsp32(generarComandoSeguridad("tv"), "Seguridad: Simulación TV", tvSubtituloConexion)
            }
        }

        // --- SENSIBILIDAD Y SWITCHES ---
        cardSensEstandar.setOnClickListener {
            actualizarSensibilidad(cardSensEstandar)
            enviarComandoEsp32("/mic?sensibilidad=estandar", "Sensibilidad: Estándar", tvSubtituloConexion)
        }
        cardSensSuave.setOnClickListener {
            actualizarSensibilidad(cardSensSuave)
            enviarComandoEsp32("/mic?sensibilidad=suave", "Sensibilidad: Suave", tvSubtituloConexion)
        }
        cardSensFiesta.setOnClickListener {
            actualizarSensibilidad(cardSensFiesta)
            enviarComandoEsp32("/mic?sensibilidad=fiesta", "Sensibilidad: Fiesta", tvSubtituloConexion)
        }

        switchLuces.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enviarComandoEsp32("/power?estado=1", "Tira LED Encendida", tvSubtituloConexion)
            } else {
                enviarComandoEsp32("/power?estado=0", "Tira LED Apagada", tvSubtituloConexion)
            }
        }

        switchMic.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enviarComandoEsp32("/mic?estado=1", "Micrófono Activado", tvSubtituloConexion)
            } else {
                enviarComandoEsp32("/mic?estado=0", "Micrófono Desactivado", tvSubtituloConexion)
            }
        }

        switchPir.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tvEstadoPir.text = "Sensor HC-SR501: Vigilancia activa (Monitoreando)"
                tvEstadoPir.setTextColor(0xFF0284C7.toInt())
                enviarComandoEsp32("/pir?alarma=1", "Alarma PIR Activada", tvSubtituloConexion)
            } else {
                tvEstadoPir.text = "Sensor HC-SR501: Sin movimiento detectado"
                tvEstadoPir.setTextColor(0xFF64748B.toInt())
                enviarComandoEsp32("/pir?alarma=0", "Alarma PIR Desactivada", tvSubtituloConexion)
            }
        }

        btnBack.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun permitirEnvioSeguridad(): Boolean {
        val ahora = System.currentTimeMillis()
        if (ahora - ultimoComandoMs < 400) return false
        ultimoComandoMs = ahora
        return true
    }

    private fun enviarComandoEsp32(ruta: String, nombreAccion: String, tvEstado: TextView) {
        Thread {
            try {
                val url = URL("$esp32Ip$ruta")
                val conexion = url.openConnection() as HttpURLConnection
                conexion.requestMethod = "GET"
                conexion.connectTimeout = 1500
                val codigoRespuesta = conexion.responseCode
                conexion.disconnect()
                runOnUiThread {
                    if (codigoRespuesta == 200) {
                        tvEstado.text = "WS2812B • CONECTADO"
                        Toast.makeText(this, "ESP32: $nombreAccion", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (_: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "$nombreAccion (Local)", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }
}
