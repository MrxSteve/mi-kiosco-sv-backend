-- =============================================================
-- V2__add_super_admin_role.sql
-- Agrega el rol super_admin y ajusta la tabla usuario
-- para soportar usuarios sin comedor (el dueño del SaaS).
-- =============================================================

SET search_path TO kiosk, public;

-- IF NOT EXISTS: evita error si por alguna razon el valor ya existiera
ALTER TYPE kiosk.user_role ADD VALUE IF NOT EXISTS 'super_admin';

-- Super_admin no pertenece a ningun comedor especifico
ALTER TABLE kiosk.usuario
    ALTER COLUMN comedor_id DROP NOT NULL;

-- Regla: super_admin => comedor_id NULL | otros roles => comedor_id NOT NULL
ALTER TABLE kiosk.usuario
    ADD CONSTRAINT ck_usuario_comedor_role CHECK (
        (role::text = 'super_admin' AND comedor_id IS NULL)
        OR
        (role::text <> 'super_admin' AND comedor_id IS NOT NULL)
    );
