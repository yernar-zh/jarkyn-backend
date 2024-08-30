package kz.jarkyn.backend.model.good;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class GoodTransportEntity {
    @ManyToOne
    @JoinColumn(name = "good_id")
    private GoodEntity good;
    @ManyToOne
    @JoinColumn(name = "good_id")
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
