package edu.universidad.trading.notification.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

interface NotificationDispatchRepository extends JpaRepository<NotificationDispatchEntity, Long> {
}
