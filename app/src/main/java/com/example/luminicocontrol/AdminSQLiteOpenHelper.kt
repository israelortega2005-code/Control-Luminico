package com.example.luminicocontrol

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.MessageDigest

class AdminSQLiteOpenHelper(context: Context) : SQLiteOpenHelper(context, "LuminicoDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // Creación de tabla local de usuarios
        db.execSQL("CREATE TABLE usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password_hash TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
    }

    // Cifrado de credenciales mediante algoritmo SHA-256 (Cumplimiento ISO/IEC 27400)
    fun cifrarSHA256(texto: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(texto.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // Registrar un nuevo usuario guardando únicamente el hash de su contraseña
    fun registrarUsuario(username: String, passwordPlana: String): Boolean {
        val db = this.writableDatabase
        val valores = ContentValues().apply {
            put("username", username.trim())
            put("password_hash", cifrarSHA256(passwordPlana))
        }
        val resultado = db.insert("usuarios", null, valores)
        db.close()
        return resultado != -1L
    }

    // Validar inicio de sesión comparando hashes SHA-256
    fun validarUsuario(username: String, passwordPlana: String): Boolean {
        val db = this.readableDatabase
        val hashIngresado = cifrarSHA256(passwordPlana)
        val cursor = db.rawQuery(
            "SELECT * FROM usuarios WHERE username = ? AND password_hash = ?",
            arrayOf(username.trim(), hashIngresado)
        )
        val existe = cursor.count > 0
        cursor.close()
        db.close()
        return existe
    }
}