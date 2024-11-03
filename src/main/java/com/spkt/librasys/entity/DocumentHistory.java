package com.spkt.librasys.entity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "document_histories_001")
public class DocumentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id", nullable = false, unique = true)
    private Long historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    @ToString.Exclude
    private Document document;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "warehouseId", column = @Column(name = "history_warehouse_id")),
        @AttributeOverride(name = "rack_id", column = @Column(name = "history_rack_id")),
        @AttributeOverride(name = "quantity", column = @Column(name = "history_quantity")),
        @AttributeOverride(name = "size", column = @Column(name = "history_size"))
    })
    private DocumentLocation location;

    @Column(name = "change_time", nullable = false)
    private LocalDateTime changeTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private Action action; // ADD, REMOVE, MOVE, etc.

    @Column(name = "quantity_change", nullable = false)
    private int quantityChange; // Số lượng thay đổi, có thể dương hoặc âm

    public enum Action {
        ADD,
        UPDATE,
        REMOVE,
        MOVE,
        MAINTENANCE,
        LOST,
        DAMAGED
    }
}
