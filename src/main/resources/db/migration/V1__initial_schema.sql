-- =============================================================
-- V1__initial_schema.sql
-- Kiosco de Comida SaaS - Esquema inicial de base de datos
-- =============================================================

BEGIN;

CREATE SCHEMA IF NOT EXISTS kiosk;
SET search_path TO kiosk, public;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- =============================================================
-- Tipos enumerados
-- =============================================================
DO $$ BEGIN
    CREATE TYPE user_role AS ENUM ('admin', 'service', 'kitchen');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE generic_status AS ENUM ('active', 'inactive');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE subscription_status AS ENUM ('active', 'expired', 'suspended', 'cancelled');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE discount_type AS ENUM ('percentage', 'fixed');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE order_status AS ENUM ('pending', 'in_preparation', 'completed', 'delivered', 'cancelled');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE payment_method AS ENUM ('cash', 'card', 'mixed');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE payment_status AS ENUM ('pending', 'paid', 'refunded', 'cancelled');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE audit_action AS ENUM (
        'insert', 'update', 'delete',
        'login', 'logout',
        'status_change', 'password_reset'
    );
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

-- =============================================================
-- Funcion utilitaria: actualiza updated_at automaticamente
-- =============================================================
CREATE OR REPLACE FUNCTION kiosk.set_updated_at()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$;

-- =============================================================
-- Planes de suscripcion
-- Precio unico por ahora; la tabla soporta multiples planes
-- =============================================================
CREATE TABLE IF NOT EXISTS kiosk.plan_suscripcion (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    code            VARCHAR(50) NOT NULL,
    name            VARCHAR(120) NOT NULL,
    description     TEXT,
    price           NUMERIC(12,2) NOT NULL DEFAULT 0,
    billing_cycle   VARCHAR(20)  NOT NULL DEFAULT 'monthly',
    user_limit      INTEGER      NOT NULL DEFAULT 5,
    status          generic_status NOT NULL DEFAULT 'active',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_plan_code         UNIQUE (code),
    CONSTRAINT ck_plan_price        CHECK (price >= 0),
    CONSTRAINT ck_plan_user_limit   CHECK (user_limit > 0),
    CONSTRAINT ck_plan_billing      CHECK (billing_cycle IN ('monthly', 'quarterly', 'yearly'))
);

-- =============================================================
-- Comedor (tenant principal del SaaS)
-- =============================================================
CREATE TABLE IF NOT EXISTS kiosk.comedor (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(150) NOT NULL,
    legal_name  VARCHAR(200),
    email       VARCHAR(150) NOT NULL,
    phone       VARCHAR(30),
    address     VARCHAR(255),
    logo_url    TEXT,
    timezone    VARCHAR(80)  NOT NULL DEFAULT 'America/El_Salvador',
    status      generic_status NOT NULL DEFAULT 'active',
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_comedor_email UNIQUE (email)
);

-- Configuracion por comedor (moneda, horario, intentos de login, etc.)
CREATE TABLE IF NOT EXISTS kiosk.configuracion_comedor (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    comedor_id          UUID        NOT NULL UNIQUE,
    currency_symbol     VARCHAR(10) NOT NULL DEFAULT '$',
    currency_code       VARCHAR(5)  NOT NULL DEFAULT 'USD',
    date_format         VARCHAR(30) NOT NULL DEFAULT 'DD/MM/YYYY',
    time_format         VARCHAR(10) NOT NULL DEFAULT 'HH:mm',
    opening_time        TIME,
    closing_time        TIME,
    max_login_attempts  INTEGER     NOT NULL DEFAULT 5,
    lockout_minutes     INTEGER     NOT NULL DEFAULT 15,
    ticket_footer       TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_config_comedor        FOREIGN KEY (comedor_id)    REFERENCES kiosk.comedor (id) ON DELETE CASCADE,
    CONSTRAINT ck_max_login_attempts    CHECK (max_login_attempts > 0),
    CONSTRAINT ck_lockout_minutes       CHECK (lockout_minutes >= 0)
);

-- =============================================================
-- Suscripciones
-- =============================================================
CREATE TABLE IF NOT EXISTS kiosk.suscripcion (
    id                  UUID                PRIMARY KEY DEFAULT gen_random_uuid(),
    comedor_id          UUID                NOT NULL,
    plan_id             UUID                NOT NULL,
    status              subscription_status NOT NULL DEFAULT 'active',
    start_date          DATE                NOT NULL DEFAULT CURRENT_DATE,
    end_date            DATE,
    auto_renew          BOOLEAN             NOT NULL DEFAULT TRUE,
    payment_reference   VARCHAR(120),
    notes               TEXT,
    created_at          TIMESTAMPTZ         NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ         NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_suscripcion_comedor   FOREIGN KEY (comedor_id) REFERENCES kiosk.comedor (id)           ON DELETE CASCADE,
    CONSTRAINT fk_suscripcion_plan      FOREIGN KEY (plan_id)    REFERENCES kiosk.plan_suscripcion (id)  ON DELETE RESTRICT
);

-- Solo una suscripcion activa por comedor
CREATE UNIQUE INDEX IF NOT EXISTS ux_suscripcion_activa
    ON kiosk.suscripcion (comedor_id) WHERE status = 'active';

-- =============================================================
-- Usuarios del sistema (por comedor)
-- =============================================================
CREATE TABLE IF NOT EXISTS kiosk.usuario (
    id                  UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    comedor_id          UUID           NOT NULL,
    full_name           VARCHAR(150)   NOT NULL,
    email               VARCHAR(150)   NOT NULL,
    password_hash       TEXT           NOT NULL,
    role                user_role      NOT NULL DEFAULT 'service',
    status              generic_status NOT NULL DEFAULT 'active',
    failed_login_count  INTEGER        NOT NULL DEFAULT 0,
    locked_until        TIMESTAMPTZ,
    last_login_at       TIMESTAMPTZ,
    created_at          TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_usuario_comedor   FOREIGN KEY (comedor_id) REFERENCES kiosk.comedor (id) ON DELETE CASCADE,
    CONSTRAINT uq_usuario_email     UNIQUE (comedor_id, email),
    CONSTRAINT ck_failed_count      CHECK (failed_login_count >= 0)
);

-- Tokens para recuperacion de contrasena (expiran en 24h)
CREATE TABLE IF NOT EXISTS kiosk.password_reset_token (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id  UUID         NOT NULL,
    token       VARCHAR(255) NOT NULL,
    expires_at  TIMESTAMPTZ  NOT NULL,
    used        BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_prt_usuario   FOREIGN KEY (usuario_id) REFERENCES kiosk.usuario (id) ON DELETE CASCADE,
    CONSTRAINT uq_prt_token     UNIQUE (token)
);

-- =============================================================
-- Clientes del comedor
-- =============================================================
CREATE TABLE IF NOT EXISTS kiosk.cliente (
    id              UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    comedor_id      UUID           NOT NULL,
    full_name       VARCHAR(150)   NOT NULL,
    email           VARCHAR(150),
    phone           VARCHAR(30),
    address         VARCHAR(255),
    customer_code   VARCHAR(40)    NOT NULL,
    notes           TEXT,
    status          generic_status NOT NULL DEFAULT 'active',
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_cliente_comedor   FOREIGN KEY (comedor_id) REFERENCES kiosk.comedor (id) ON DELETE CASCADE,
    CONSTRAINT uq_cliente_code      UNIQUE (comedor_id, customer_code),
    CONSTRAINT uq_cliente_email     UNIQUE (comedor_id, email)
);

-- =============================================================
-- Catalogo: Categorias
-- =============================================================
CREATE TABLE IF NOT EXISTS kiosk.categoria (
    id              UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    comedor_id      UUID           NOT NULL,
    name            VARCHAR(120)   NOT NULL,
    description     TEXT,
    display_order   INTEGER        NOT NULL DEFAULT 0,
    status          generic_status NOT NULL DEFAULT 'active',
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_categoria_comedor FOREIGN KEY (comedor_id) REFERENCES kiosk.comedor (id) ON DELETE CASCADE,
    CONSTRAINT uq_categoria_name    UNIQUE (comedor_id, name)
);

-- =============================================================
-- Catalogo: Productos / Platos
-- =============================================================
CREATE TABLE IF NOT EXISTS kiosk.producto (
    id              UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    comedor_id      UUID           NOT NULL,
    categoria_id    UUID           NOT NULL,
    name            VARCHAR(150)   NOT NULL,
    description     TEXT,
    price           NUMERIC(12,2)  NOT NULL DEFAULT 0,
    image_url       TEXT,
    has_extras      BOOLEAN        NOT NULL DEFAULT FALSE,
    has_combos      BOOLEAN        NOT NULL DEFAULT FALSE, -- soporta precios combo (ej: 3 por $1)
    status          generic_status NOT NULL DEFAULT 'active',
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_producto_comedor      FOREIGN KEY (comedor_id)    REFERENCES kiosk.comedor (id)   ON DELETE CASCADE,
    CONSTRAINT fk_producto_categoria    FOREIGN KEY (categoria_id)  REFERENCES kiosk.categoria (id) ON DELETE RESTRICT,
    CONSTRAINT uq_producto_name         UNIQUE (comedor_id, name),
    CONSTRAINT ck_producto_price        CHECK (price >= 0)
);

-- Precios combo / granel por producto
-- Ej: pupusas a $0.35 c/u pero tambien "3 por $1.00"
CREATE TABLE IF NOT EXISTS kiosk.producto_combo (
    id          UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    comedor_id  UUID           NOT NULL,
    producto_id UUID           NOT NULL,
    label       VARCHAR(100)   NOT NULL,       -- texto visible: "3 por $1.00"
    combo_qty   INTEGER        NOT NULL,        -- cantidad de unidades que incluye
    combo_price NUMERIC(12,2)  NOT NULL,        -- precio total del combo
    status      generic_status NOT NULL DEFAULT 'active',
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_combo_comedor     FOREIGN KEY (comedor_id)    REFERENCES kiosk.comedor (id)  ON DELETE CASCADE,
    CONSTRAINT fk_combo_producto    FOREIGN KEY (producto_id)   REFERENCES kiosk.producto (id) ON DELETE CASCADE,
    CONSTRAINT ck_combo_qty         CHECK (combo_qty > 1),
    CONSTRAINT ck_combo_price       CHECK (combo_price > 0)
);

-- =============================================================
-- Catalogo: Grupos de extras y Extras
-- =============================================================

-- Agrupaciones para extras (ej: "Bebidas", "Condimentos", "Salsas")
CREATE TABLE IF NOT EXISTS kiosk.grupo_extra (
    id          UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    comedor_id  UUID           NOT NULL,
    name        VARCHAR(120)   NOT NULL,
    description TEXT,
    status      generic_status NOT NULL DEFAULT 'active',
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_grupo_extra_comedor   FOREIGN KEY (comedor_id) REFERENCES kiosk.comedor (id) ON DELETE CASCADE,
    CONSTRAINT uq_grupo_extra_name      UNIQUE (comedor_id, name)
);

CREATE TABLE IF NOT EXISTS kiosk.extra (
    id          UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    comedor_id  UUID           NOT NULL,
    grupo_id    UUID,                           -- grupo al que pertenece (nullable)
    name        VARCHAR(120)   NOT NULL,
    description TEXT,
    price       NUMERIC(12,2)  NOT NULL DEFAULT 0,
    status      generic_status NOT NULL DEFAULT 'active',
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_extra_comedor FOREIGN KEY (comedor_id) REFERENCES kiosk.comedor (id)    ON DELETE CASCADE,
    CONSTRAINT fk_extra_grupo   FOREIGN KEY (grupo_id)   REFERENCES kiosk.grupo_extra (id) ON DELETE SET NULL,
    CONSTRAINT uq_extra_name    UNIQUE (comedor_id, name),
    CONSTRAINT ck_extra_price   CHECK (price >= 0)
);

-- Relacion N:N entre productos y extras disponibles
CREATE TABLE IF NOT EXISTS kiosk.producto_extra (
    producto_id UUID        NOT NULL,
    extra_id    UUID        NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (producto_id, extra_id),
    CONSTRAINT fk_pe_producto   FOREIGN KEY (producto_id) REFERENCES kiosk.producto (id) ON DELETE CASCADE,
    CONSTRAINT fk_pe_extra      FOREIGN KEY (extra_id)    REFERENCES kiosk.extra (id)    ON DELETE CASCADE
);

-- =============================================================
-- Promociones / Descuentos
-- =============================================================
CREATE TABLE IF NOT EXISTS kiosk.promocion (
    id              UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    comedor_id      UUID           NOT NULL,
    code            VARCHAR(60)    NOT NULL,
    name            VARCHAR(150)   NOT NULL,
    discount_kind   discount_type  NOT NULL,
    discount_value  NUMERIC(12,2)  NOT NULL DEFAULT 0,
    starts_at       TIMESTAMPTZ,
    ends_at         TIMESTAMPTZ,
    max_uses        INTEGER,
    uses_count      INTEGER        NOT NULL DEFAULT 0,
    applies_to_all  BOOLEAN        NOT NULL DEFAULT TRUE,
    status          generic_status NOT NULL DEFAULT 'active',
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_promocion_comedor FOREIGN KEY (comedor_id) REFERENCES kiosk.comedor (id) ON DELETE CASCADE,
    CONSTRAINT uq_promocion_code    UNIQUE (comedor_id, code),
    CONSTRAINT ck_promo_discount    CHECK (discount_value >= 0),
    CONSTRAINT ck_promo_max_uses    CHECK (max_uses IS NULL OR max_uses > 0),
    CONSTRAINT ck_promo_dates       CHECK (ends_at IS NULL OR starts_at IS NULL OR ends_at >= starts_at)
);

-- Productos especificos a los que aplica una promocion (cuando applies_to_all = false)
CREATE TABLE IF NOT EXISTS kiosk.promocion_producto (
    promocion_id    UUID        NOT NULL,
    producto_id     UUID        NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (promocion_id, producto_id),
    CONSTRAINT fk_pp_promocion  FOREIGN KEY (promocion_id) REFERENCES kiosk.promocion (id) ON DELETE CASCADE,
    CONSTRAINT fk_pp_producto   FOREIGN KEY (producto_id)  REFERENCES kiosk.producto (id)  ON DELETE CASCADE
);

-- =============================================================
-- Ordenes
-- Las ordenes pueden ser:
--   - Anonimas: cliente_id=NULL y customer_name=NULL (solo numero de orden)
--   - Con nombre: cliente_id=NULL y customer_name='Juan'
--   - Con cliente registrado: cliente_id=<uuid>
-- =============================================================
CREATE TABLE IF NOT EXISTS kiosk.orden (
    id                  UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    comedor_id          UUID           NOT NULL,
    usuario_id          UUID           NOT NULL,
    cliente_id          UUID,
    promocion_id        UUID,
    order_number        BIGINT         GENERATED ALWAYS AS IDENTITY UNIQUE,
    customer_name       VARCHAR(150),  -- nombre libre para ordenes sin cuenta registrada
    subtotal            NUMERIC(12,2)  NOT NULL DEFAULT 0,
    discount_amount     NUMERIC(12,2)  NOT NULL DEFAULT 0,
    tax_amount          NUMERIC(12,2)  NOT NULL DEFAULT 0,
    total_amount        NUMERIC(12,2)  NOT NULL DEFAULT 0,
    status              order_status   NOT NULL DEFAULT 'pending',
    payment_method      payment_method,
    notes               TEXT,
    sent_to_kitchen_at  TIMESTAMPTZ,
    completed_at        TIMESTAMPTZ,
    delivered_at        TIMESTAMPTZ,
    cancelled_at        TIMESTAMPTZ,
    created_at          TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_orden_comedor     FOREIGN KEY (comedor_id)    REFERENCES kiosk.comedor (id)   ON DELETE CASCADE,
    CONSTRAINT fk_orden_usuario     FOREIGN KEY (usuario_id)    REFERENCES kiosk.usuario (id)   ON DELETE RESTRICT,
    CONSTRAINT fk_orden_cliente     FOREIGN KEY (cliente_id)    REFERENCES kiosk.cliente (id)   ON DELETE SET NULL,
    CONSTRAINT fk_orden_promocion   FOREIGN KEY (promocion_id)  REFERENCES kiosk.promocion (id) ON DELETE SET NULL,
    CONSTRAINT ck_orden_amounts     CHECK (subtotal >= 0 AND discount_amount >= 0 AND tax_amount >= 0 AND total_amount >= 0)
);

-- Detalle de la orden (lineas de pedido)
CREATE TABLE IF NOT EXISTS kiosk.orden_detalle (
    id                      UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    comedor_id              UUID          NOT NULL,
    orden_id                UUID          NOT NULL,
    producto_id             UUID,                         -- nullable si el producto fue eliminado
    combo_id                UUID,                         -- nullable; si se uso precio combo
    product_name_snapshot   VARCHAR(150)  NOT NULL,       -- nombre al momento de la orden
    unit_price_snapshot     NUMERIC(12,2) NOT NULL DEFAULT 0,
    combo_price_snapshot    NUMERIC(12,2),                -- precio total del combo usado (nullable)
    quantity                INTEGER       NOT NULL DEFAULT 1,
    line_subtotal           NUMERIC(12,2) NOT NULL DEFAULT 0,
    notes                   TEXT,
    created_at              TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_od_orden      FOREIGN KEY (orden_id)     REFERENCES kiosk.orden (id)          ON DELETE CASCADE,
    CONSTRAINT fk_od_comedor    FOREIGN KEY (comedor_id)   REFERENCES kiosk.comedor (id)         ON DELETE CASCADE,
    CONSTRAINT fk_od_producto   FOREIGN KEY (producto_id)  REFERENCES kiosk.producto (id)        ON DELETE SET NULL,
    CONSTRAINT fk_od_combo      FOREIGN KEY (combo_id)     REFERENCES kiosk.producto_combo (id)  ON DELETE SET NULL,
    CONSTRAINT ck_od_qty        CHECK (quantity > 0),
    CONSTRAINT ck_od_amounts    CHECK (unit_price_snapshot >= 0 AND line_subtotal >= 0)
);

-- Extras aplicados a cada linea de la orden
CREATE TABLE IF NOT EXISTS kiosk.orden_detalle_extra (
    id                      UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    comedor_id              UUID          NOT NULL,
    orden_detalle_id        UUID          NOT NULL,
    extra_id                UUID,                         -- nullable si el extra fue eliminado
    extra_name_snapshot     VARCHAR(120)  NOT NULL,
    extra_price_snapshot    NUMERIC(12,2) NOT NULL DEFAULT 0,
    created_at              TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_ode_detalle   FOREIGN KEY (orden_detalle_id) REFERENCES kiosk.orden_detalle (id) ON DELETE CASCADE,
    CONSTRAINT fk_ode_comedor   FOREIGN KEY (comedor_id)        REFERENCES kiosk.comedor (id)       ON DELETE CASCADE,
    CONSTRAINT fk_ode_extra     FOREIGN KEY (extra_id)          REFERENCES kiosk.extra (id)         ON DELETE SET NULL,
    CONSTRAINT ck_ode_price     CHECK (extra_price_snapshot >= 0)
);

-- =============================================================
-- Pagos
-- =============================================================
CREATE TABLE IF NOT EXISTS kiosk.pago (
    id                      UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    comedor_id              UUID           NOT NULL,
    orden_id                UUID           NOT NULL UNIQUE,
    amount_received         NUMERIC(12,2)  NOT NULL DEFAULT 0,
    change_amount           NUMERIC(12,2)  NOT NULL DEFAULT 0,
    method                  payment_method NOT NULL DEFAULT 'cash',
    status                  payment_status NOT NULL DEFAULT 'paid',
    transaction_reference   VARCHAR(120),
    paid_at                 TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_at              TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_pago_orden    FOREIGN KEY (orden_id)   REFERENCES kiosk.orden (id)   ON DELETE CASCADE,
    CONSTRAINT fk_pago_comedor  FOREIGN KEY (comedor_id) REFERENCES kiosk.comedor (id) ON DELETE CASCADE,
    CONSTRAINT ck_pago_amounts  CHECK (amount_received >= 0 AND change_amount >= 0)
);

-- =============================================================
-- Historial de estados de una orden
-- =============================================================
CREATE TABLE IF NOT EXISTS kiosk.historial_estado_orden (
    id                  UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    comedor_id          UUID         NOT NULL,
    orden_id            UUID         NOT NULL,
    previous_status     order_status,
    new_status          order_status NOT NULL,
    changed_by_user_id  UUID,
    change_notes        TEXT,
    changed_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_heo_orden     FOREIGN KEY (orden_id)              REFERENCES kiosk.orden (id)    ON DELETE CASCADE,
    CONSTRAINT fk_heo_comedor   FOREIGN KEY (comedor_id)            REFERENCES kiosk.comedor (id)  ON DELETE CASCADE,
    CONSTRAINT fk_heo_usuario   FOREIGN KEY (changed_by_user_id)    REFERENCES kiosk.usuario (id)  ON DELETE SET NULL
);

-- =============================================================
-- Log de auditoria
-- =============================================================
CREATE TABLE IF NOT EXISTS kiosk.auditoria_log (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    comedor_id  UUID,
    user_id     UUID,
    entity_name VARCHAR(120) NOT NULL,
    entity_id   UUID,
    action      audit_action NOT NULL,
    detail      JSONB,
    ip_address  INET,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_audit_comedor FOREIGN KEY (comedor_id) REFERENCES kiosk.comedor (id)  ON DELETE SET NULL,
    CONSTRAINT fk_audit_usuario FOREIGN KEY (user_id)    REFERENCES kiosk.usuario (id)  ON DELETE SET NULL
);

-- =============================================================
-- Triggers para updated_at
-- =============================================================
DROP TRIGGER IF EXISTS trg_plan_suscripcion_upd   ON kiosk.plan_suscripcion;
DROP TRIGGER IF EXISTS trg_comedor_upd             ON kiosk.comedor;
DROP TRIGGER IF EXISTS trg_config_comedor_upd      ON kiosk.configuracion_comedor;
DROP TRIGGER IF EXISTS trg_suscripcion_upd         ON kiosk.suscripcion;
DROP TRIGGER IF EXISTS trg_usuario_upd             ON kiosk.usuario;
DROP TRIGGER IF EXISTS trg_cliente_upd             ON kiosk.cliente;
DROP TRIGGER IF EXISTS trg_categoria_upd           ON kiosk.categoria;
DROP TRIGGER IF EXISTS trg_producto_upd            ON kiosk.producto;
DROP TRIGGER IF EXISTS trg_producto_combo_upd      ON kiosk.producto_combo;
DROP TRIGGER IF EXISTS trg_grupo_extra_upd         ON kiosk.grupo_extra;
DROP TRIGGER IF EXISTS trg_extra_upd               ON kiosk.extra;
DROP TRIGGER IF EXISTS trg_promocion_upd           ON kiosk.promocion;
DROP TRIGGER IF EXISTS trg_orden_upd               ON kiosk.orden;
DROP TRIGGER IF EXISTS trg_orden_detalle_upd       ON kiosk.orden_detalle;
DROP TRIGGER IF EXISTS trg_pago_upd                ON kiosk.pago;

CREATE TRIGGER trg_plan_suscripcion_upd  BEFORE UPDATE ON kiosk.plan_suscripcion     FOR EACH ROW EXECUTE FUNCTION kiosk.set_updated_at();
CREATE TRIGGER trg_comedor_upd           BEFORE UPDATE ON kiosk.comedor              FOR EACH ROW EXECUTE FUNCTION kiosk.set_updated_at();
CREATE TRIGGER trg_config_comedor_upd    BEFORE UPDATE ON kiosk.configuracion_comedor FOR EACH ROW EXECUTE FUNCTION kiosk.set_updated_at();
CREATE TRIGGER trg_suscripcion_upd       BEFORE UPDATE ON kiosk.suscripcion          FOR EACH ROW EXECUTE FUNCTION kiosk.set_updated_at();
CREATE TRIGGER trg_usuario_upd           BEFORE UPDATE ON kiosk.usuario              FOR EACH ROW EXECUTE FUNCTION kiosk.set_updated_at();
CREATE TRIGGER trg_cliente_upd           BEFORE UPDATE ON kiosk.cliente              FOR EACH ROW EXECUTE FUNCTION kiosk.set_updated_at();
CREATE TRIGGER trg_categoria_upd         BEFORE UPDATE ON kiosk.categoria            FOR EACH ROW EXECUTE FUNCTION kiosk.set_updated_at();
CREATE TRIGGER trg_producto_upd          BEFORE UPDATE ON kiosk.producto             FOR EACH ROW EXECUTE FUNCTION kiosk.set_updated_at();
CREATE TRIGGER trg_producto_combo_upd    BEFORE UPDATE ON kiosk.producto_combo       FOR EACH ROW EXECUTE FUNCTION kiosk.set_updated_at();
CREATE TRIGGER trg_grupo_extra_upd       BEFORE UPDATE ON kiosk.grupo_extra          FOR EACH ROW EXECUTE FUNCTION kiosk.set_updated_at();
CREATE TRIGGER trg_extra_upd             BEFORE UPDATE ON kiosk.extra                FOR EACH ROW EXECUTE FUNCTION kiosk.set_updated_at();
CREATE TRIGGER trg_promocion_upd         BEFORE UPDATE ON kiosk.promocion            FOR EACH ROW EXECUTE FUNCTION kiosk.set_updated_at();
CREATE TRIGGER trg_orden_upd             BEFORE UPDATE ON kiosk.orden                FOR EACH ROW EXECUTE FUNCTION kiosk.set_updated_at();
CREATE TRIGGER trg_orden_detalle_upd     BEFORE UPDATE ON kiosk.orden_detalle        FOR EACH ROW EXECUTE FUNCTION kiosk.set_updated_at();
CREATE TRIGGER trg_pago_upd              BEFORE UPDATE ON kiosk.pago                 FOR EACH ROW EXECUTE FUNCTION kiosk.set_updated_at();

-- =============================================================
-- Indices para patrones de acceso comunes
-- =============================================================

-- Usuarios
CREATE INDEX IF NOT EXISTS idx_usuario_comedor_email    ON kiosk.usuario (comedor_id, email);
CREATE INDEX IF NOT EXISTS idx_usuario_comedor_role     ON kiosk.usuario (comedor_id, role);
CREATE INDEX IF NOT EXISTS idx_prt_usuario_token        ON kiosk.password_reset_token (usuario_id, token) WHERE used = FALSE;

-- Clientes
CREATE INDEX IF NOT EXISTS idx_cliente_comedor_code     ON kiosk.cliente (comedor_id, customer_code);
CREATE INDEX IF NOT EXISTS idx_cliente_comedor_name     ON kiosk.cliente (comedor_id, full_name);

-- Catalogo
CREATE INDEX IF NOT EXISTS idx_categoria_comedor_order  ON kiosk.categoria (comedor_id, display_order);
CREATE INDEX IF NOT EXISTS idx_producto_comedor_cat     ON kiosk.producto (comedor_id, categoria_id);
CREATE INDEX IF NOT EXISTS idx_producto_comedor_status  ON kiosk.producto (comedor_id, status);
CREATE INDEX IF NOT EXISTS idx_combo_producto           ON kiosk.producto_combo (producto_id);
CREATE INDEX IF NOT EXISTS idx_extra_comedor_grupo      ON kiosk.extra (comedor_id, grupo_id);
CREATE INDEX IF NOT EXISTS idx_promocion_comedor_code   ON kiosk.promocion (comedor_id, code);
CREATE INDEX IF NOT EXISTS idx_promocion_vigencia       ON kiosk.promocion (comedor_id, status, starts_at, ends_at);

-- Ordenes (acceso mas frecuente)
CREATE INDEX IF NOT EXISTS idx_orden_comedor_status_cre ON kiosk.orden (comedor_id, status, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_orden_comedor_date       ON kiosk.orden (comedor_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_orden_usuario            ON kiosk.orden (usuario_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_orden_cliente            ON kiosk.orden (cliente_id, created_at DESC) WHERE cliente_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_orden_kitchen            ON kiosk.orden (comedor_id, status, sent_to_kitchen_at) WHERE status IN ('pending', 'in_preparation');
CREATE INDEX IF NOT EXISTS idx_od_orden                 ON kiosk.orden_detalle (orden_id);
CREATE INDEX IF NOT EXISTS idx_od_comedor_orden         ON kiosk.orden_detalle (comedor_id, orden_id);
CREATE INDEX IF NOT EXISTS idx_ode_detalle              ON kiosk.orden_detalle_extra (orden_detalle_id);
CREATE INDEX IF NOT EXISTS idx_pago_orden               ON kiosk.pago (orden_id, paid_at DESC);
CREATE INDEX IF NOT EXISTS idx_pago_comedor_date        ON kiosk.pago (comedor_id, paid_at DESC);
CREATE INDEX IF NOT EXISTS idx_heo_orden                ON kiosk.historial_estado_orden (orden_id, changed_at DESC);

-- Auditoria
CREATE INDEX IF NOT EXISTS idx_audit_comedor_date       ON kiosk.auditoria_log (comedor_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_audit_user_date          ON kiosk.auditoria_log (user_id, created_at DESC);

-- =============================================================
-- Datos iniciales: Plan de suscripcion unico (precio configurable)
-- =============================================================
INSERT INTO kiosk.plan_suscripcion (code, name, description, price, billing_cycle, user_limit)
VALUES (
    'standard',
    'Estándar',
    'Plan único para comedores pequeños y medianos. Incluye todas las funcionalidades del sistema.',
    29.99,
    'monthly',
    10
)
ON CONFLICT (code) DO NOTHING;

COMMIT;
