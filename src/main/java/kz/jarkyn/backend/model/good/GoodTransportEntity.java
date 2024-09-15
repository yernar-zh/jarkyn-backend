package kz.jarkyn.backend.model.good;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kz.jarkyn.backend.model.common.AbstractEntity;

@Entity
@Table(name = "good_transport")
public class GoodTransportEntity extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "good_id")
    private GoodEntity good;
    @ManyToOne
    @JoinColumn(name = "transport_id")
    private TransportEntity transport;

    public GoodEntity getGood() {
        return good;
    }

    public void setGood(GoodEntity good) {
        this.good = good;
    }

    public TransportEntity getTransport() {
        return transport;
    }

    public void setTransport(TransportEntity transport) {
        this.transport = transport;
    }
}
