package com.devplus.mikiosco_sv.infrastructure.persistence.report;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Ejecuta queries analíticas nativas contra el schema kiosk.
 * Todas las operaciones son de solo lectura.
 */
@Service
@Transactional(readOnly = true)
public class ReportQueryService {

    @PersistenceContext
    private EntityManager em;

    // ── Sales summary ────────────────────────────────────────────────────────

    public Object[] getSalesSummary(UUID comedorId, OffsetDateTime from, OffsetDateTime to) {
        var q = em.createNativeQuery("""
                SELECT
                    COALESCE(SUM(p.amount_received), 0),
                    COUNT(p.id)
                FROM kiosk.pago p
                WHERE p.comedor_id = :comedorId
                  AND p.paid_at >= :from
                  AND p.paid_at < :to
                  AND p.status = 'paid'
                """);
        q.setParameter("comedorId", comedorId);
        q.setParameter("from", from);
        q.setParameter("to", to);
        return (Object[]) q.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getSalesByCategory(UUID comedorId, OffsetDateTime from, OffsetDateTime to) {
        var q = em.createNativeQuery("""
                SELECT
                    COALESCE(cat.name, 'Sin categoría'),
                    COALESCE(SUM(od.line_subtotal), 0),
                    COALESCE(SUM(od.quantity), 0)
                FROM kiosk.orden_detalle od
                LEFT JOIN kiosk.producto prod ON prod.id = od.producto_id
                LEFT JOIN kiosk.categoria cat ON cat.id = prod.categoria_id
                JOIN kiosk.pago p ON p.orden_id = od.orden_id
                WHERE od.comedor_id = :comedorId
                  AND p.paid_at >= :from
                  AND p.paid_at < :to
                  AND p.status = 'paid'
                GROUP BY cat.name
                ORDER BY 2 DESC
                """);
        q.setParameter("comedorId", comedorId);
        q.setParameter("from", from);
        q.setParameter("to", to);
        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getPeakHours(UUID comedorId, OffsetDateTime from, OffsetDateTime to) {
        var q = em.createNativeQuery("""
                SELECT
                    EXTRACT(HOUR FROM p.paid_at)::INTEGER,
                    COUNT(p.id),
                    COALESCE(SUM(p.amount_received), 0)
                FROM kiosk.pago p
                WHERE p.comedor_id = :comedorId
                  AND p.paid_at >= :from
                  AND p.paid_at < :to
                  AND p.status = 'paid'
                GROUP BY EXTRACT(HOUR FROM p.paid_at)
                ORDER BY 1
                """);
        q.setParameter("comedorId", comedorId);
        q.setParameter("from", from);
        q.setParameter("to", to);
        return q.getResultList();
    }

    // ── Top products ─────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    public List<Object[]> getTopProducts(UUID comedorId, OffsetDateTime from, OffsetDateTime to, int limit) {
        var q = em.createNativeQuery("""
                SELECT
                    od.product_name_snapshot,
                    COALESCE(cat.name, 'Sin categoría'),
                    SUM(od.quantity),
                    COALESCE(SUM(od.line_subtotal), 0)
                FROM kiosk.orden_detalle od
                LEFT JOIN kiosk.producto prod ON prod.id = od.producto_id
                LEFT JOIN kiosk.categoria cat ON cat.id = prod.categoria_id
                JOIN kiosk.pago p ON p.orden_id = od.orden_id
                WHERE od.comedor_id = :comedorId
                  AND p.paid_at >= :from
                  AND p.paid_at < :to
                  AND p.status = 'paid'
                GROUP BY od.product_name_snapshot, cat.name
                ORDER BY 3 DESC
                LIMIT :limit
                """);
        q.setParameter("comedorId", comedorId);
        q.setParameter("from", from);
        q.setParameter("to", to);
        q.setParameter("limit", limit);
        return q.getResultList();
    }

    // ── Top clients ──────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    public List<Object[]> getTopClients(UUID comedorId, int limit) {
        var q = em.createNativeQuery("""
                SELECT
                    cl.full_name,
                    cl.customer_code,
                    COUNT(DISTINCT o.id),
                    COALESCE(SUM(p.amount_received), 0),
                    MAX(p.paid_at)
                FROM kiosk.cliente cl
                JOIN kiosk.orden o ON o.cliente_id = cl.id
                JOIN kiosk.pago p ON p.orden_id = o.id
                WHERE cl.comedor_id = :comedorId
                  AND p.status = 'paid'
                GROUP BY cl.id, cl.full_name, cl.customer_code
                ORDER BY 4 DESC
                LIMIT :limit
                """);
        q.setParameter("comedorId", comedorId);
        q.setParameter("limit", limit);
        return q.getResultList();
    }

    // ── Order stats ──────────────────────────────────────────────────────────

    public Object[] getOrderStats(UUID comedorId, OffsetDateTime from, OffsetDateTime to) {
        var q = em.createNativeQuery("""
                SELECT
                    COUNT(*),
                    COUNT(*) FILTER (WHERE status = 'pending'),
                    COUNT(*) FILTER (WHERE status = 'in_preparation'),
                    COUNT(*) FILTER (WHERE status = 'completed'),
                    COUNT(*) FILTER (WHERE status = 'delivered'),
                    COUNT(*) FILTER (WHERE status = 'cancelled')
                FROM kiosk.orden
                WHERE comedor_id = :comedorId
                  AND created_at >= :from
                  AND created_at < :to
                """);
        q.setParameter("comedorId", comedorId);
        q.setParameter("from", from);
        q.setParameter("to", to);
        return (Object[]) q.getSingleResult();
    }

    // ── Dashboard ────────────────────────────────────────────────────────────

    public Object[] getDashboardSalesSummary(UUID comedorId, OffsetDateTime from, OffsetDateTime to) {
        var q = em.createNativeQuery("""
                SELECT
                    COALESCE(SUM(p.amount_received), 0),
                    COUNT(p.id)
                FROM kiosk.pago p
                WHERE p.comedor_id = :comedorId
                  AND p.paid_at >= :from
                  AND p.paid_at < :to
                  AND p.status = 'paid'
                """);
        q.setParameter("comedorId", comedorId);
        q.setParameter("from", from);
        q.setParameter("to", to);
        return (Object[]) q.getSingleResult();
    }

    public Long countActiveKitchenOrders(UUID comedorId) {
        var q = em.createNativeQuery("""
                SELECT COUNT(*) FROM kiosk.orden
                WHERE comedor_id = :comedorId
                  AND sent_to_kitchen_at IS NOT NULL
                  AND status IN ('pending', 'in_preparation')
                """);
        q.setParameter("comedorId", comedorId);
        return ((Number) q.getSingleResult()).longValue();
    }

    public Long countTodayOrders(UUID comedorId, OffsetDateTime from, OffsetDateTime to) {
        var q = em.createNativeQuery("""
                SELECT COUNT(*) FROM kiosk.orden
                WHERE comedor_id = :comedorId
                  AND created_at >= :from
                  AND created_at < :to
                """);
        q.setParameter("comedorId", comedorId);
        q.setParameter("from", from);
        q.setParameter("to", to);
        return ((Number) q.getSingleResult()).longValue();
    }

    public BigDecimal getTotalRevenue(UUID comedorId) {
        var q = em.createNativeQuery("""
                SELECT COALESCE(SUM(amount_received), 0)
                FROM kiosk.pago
                WHERE comedor_id = :comedorId AND status = 'paid'
                """);
        q.setParameter("comedorId", comedorId);
        return (BigDecimal) q.getSingleResult();
    }
}
