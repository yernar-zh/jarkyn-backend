package kz.jarkyn.backend.operation.model.message;

import java.time.Instant;
import java.util.UUID;

public class TurnoverFixMessage {
    private UUID warehouseId;
    private UUID goodId;
    private Instant moment;

    public TurnoverFixMessage() {
    }

    public TurnoverFixMessage(UUID warehouseId, UUID goodId, Instant moment) {
        this.warehouseId = warehouseId;
        this.goodId = goodId;
        this.moment = moment;
    }

    public UUID getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(UUID warehouseId) {
        this.warehouseId = warehouseId;
    }

    public UUID getGoodId() {
        return goodId;
    }

    public void setGoodId(UUID goodId) {
        this.goodId = goodId;
    }

    public Instant getMoment() {
        return moment;
    }

    public void setMoment(Instant moment) {
        this.moment = moment;
    }
}
